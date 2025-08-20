package cn.web3er.wrench.rate.limiter.aop;


import cn.hutool.extra.spring.SpringUtil;
import cn.web3er.wrench.rate.limiter.enums.LimitType;
import cn.web3er.wrench.rate.limiter.types.GlobalConstants;
import cn.web3er.wrench.rate.limiter.types.MessageUtils;
import cn.web3er.wrench.rate.limiter.types.RedisUtils;
import cn.web3er.wrench.rate.limiter.types.ServletUtils;
import cn.web3er.wrench.rate.limiter.types.annotations.RateLimiter;
import cn.web3er.wrench.rate.limiter.types.exception.ServiceException;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 限流处理
 *
 * @author Lion Li
 */
@Aspect
public class RateLimiterAspect {

    private final Logger log = LoggerFactory.getLogger(RateLimiterAspect.class);

    /**
     * 定义spel表达式解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();
    /**
     * 定义spel解析模版
     */
    private final ParserContext parserContext = new TemplateParserContext();
    /**
     * 方法参数解析器
     */
    private final ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();

    @Value("${whn.wrench.config.cdn:false}")
    private boolean useCdn;

    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        int time = rateLimiter.time();
        int count = rateLimiter.count();
        int timeout = rateLimiter.timeout();
        try {
            String combineKey = getCombineKey(rateLimiter, point);
            RateType rateType = RateType.OVERALL;
            if (rateLimiter.limitType() == LimitType.CLUSTER) {
                rateType = RateType.PER_CLIENT;
            }
            long number = RedisUtils.rateLimiter(combineKey, rateType, count, time, timeout);
            if (number == -1) {
                String message = rateLimiter.message();
                if (StringUtils.startsWith(message, "{") && StringUtils.endsWith(message, "}")) {
                    message = MessageUtils.message(StringUtils.substring(message, 1, message.length() - 1));
                }
                fallbackMethodResult(point, rateLimiter.fallbackMethod(), message);
            }
            log.info("限制令牌 => {}, 剩余令牌 => {}, 缓存key => '{}'", count, number, combineKey);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw e;
            } else {
                throw new RuntimeException("服务器限流异常，请稍候再试", e);
            }
        }
    }

    private String getCombineKey(RateLimiter rateLimiter, JoinPoint point) {
        String key = rateLimiter.key();
        // 判断 key 不为空 和 不是表达式
        if (StringUtils.isNotBlank(key) && StringUtils.containsAny(key, "#")) {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method targetMethod = signature.getMethod();
            Object[] args = point.getArgs();
            MethodBasedEvaluationContext context =
                new MethodBasedEvaluationContext(null, targetMethod, args, pnd);
            context.setBeanResolver(new BeanFactoryResolver(SpringUtil.getBeanFactory()));
            Expression expression;
            if (StringUtils.startsWith(key, parserContext.getExpressionPrefix())
                && StringUtils.endsWith(key, parserContext.getExpressionSuffix())) {
                expression = parser.parseExpression(key, parserContext);
            } else {
                expression = parser.parseExpression(key);
            }
            key = expression.getValue(context, String.class);
        }
        StringBuilder stringBuffer = new StringBuilder(GlobalConstants.RATE_LIMIT_KEY);
        stringBuffer.append(ServletUtils.getRequest().getRequestURI()).append(":");
        if (rateLimiter.limitType() == LimitType.IP) {
            // 获取请求ip
            String clientIP = ServletUtils.getClientIP(useCdn);
            log.info("Client IP: {}  useCdn: {}", clientIP, useCdn);
            stringBuffer.append(clientIP).append(":");
        } else if (rateLimiter.limitType() == LimitType.CLUSTER) {
            // 获取客户端实例id
            stringBuffer.append(RedisUtils.getClient().getId()).append(":");
        }
        return stringBuffer.append(key).toString();
    }


    /**
     * 调用用户配置的回调方法，当拦截后，返回回调结果。
     */
    private Object fallbackMethodResult(JoinPoint jp, String fallbackMethod,String message) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if ("null".equals(fallbackMethod)) {
            throw new ServiceException(message);
        }
        System.out.println("调用用户配置的回调方法" + fallbackMethod + message);
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        Method method = jp.getTarget().getClass().getMethod(fallbackMethod, methodSignature.getParameterTypes());
        return method.invoke(jp.getThis(), jp.getArgs());
    }

}
