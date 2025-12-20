package cn.web3er.wrench.framework.biz.rule02.logic;

import cn.web3er.wrench.design.framework.link.model2.handler.ILogicHandler;
import cn.web3er.wrench.framework.biz.rule02.factory.Rule02TradeRuleFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试钩子方法的 Handler
 * 用于测试 applyBefore、applyAfter、applyAfterException
 */
@Slf4j
@Service
public class RuleLogic301 implements ILogicHandler<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> {

    // 用于记录钩子方法的调用次数
    public static final AtomicInteger applyBeforeCount = new AtomicInteger(0);
    public static final AtomicInteger applyAfterCount = new AtomicInteger(0);
    public static final AtomicInteger applyAfterExceptionCount = new AtomicInteger(0);

    @Override
    public XxxResponse applyBefore(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext) throws Exception {
        applyBeforeCount.incrementAndGet();
        log.info("RuleLogic301 applyBefore 被调用，requestParameter: {}", requestParameter);
        // 在上下文中记录前置钩子被调用
        dynamicContext.setValue("applyBeforeCalled", true);
        return null;
    }

    @Override
    public void applyAfter(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext, XxxResponse result) throws Exception {
        applyAfterCount.incrementAndGet();
        log.info("RuleLogic301 applyAfter 被调用，result: {}", result);
        // 在上下文中记录后置钩子被调用
        dynamicContext.setValue("applyAfterCalled", true);
        if (result != null) {
            dynamicContext.setValue("applyAfterResult", result.getAge());
        }
    }

    @Override
    public void applyAfterException(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext, Exception e) throws Exception {
        applyAfterExceptionCount.incrementAndGet();
        log.info("RuleLogic301 applyAfterException 被调用，exception: {}", e.getMessage());
        // 在上下文中记录异常钩子被调用
        dynamicContext.setValue("applyAfterExceptionCalled", true);
        dynamicContext.setValue("exceptionMessage", e.getMessage());
    }

    @Override
    public XxxResponse apply(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext) throws Exception {
        log.info("RuleLogic301 apply 执行，requestParameter: {}", requestParameter);
        dynamicContext.setValue("applyCalled", true);
        return next(requestParameter, dynamicContext);
    }

    /**
     * 重置计数器（用于测试）
     */
    public static void resetCounters() {
        applyBeforeCount.set(0);
        applyAfterCount.set(0);
        applyAfterExceptionCount.set(0);
    }
}

