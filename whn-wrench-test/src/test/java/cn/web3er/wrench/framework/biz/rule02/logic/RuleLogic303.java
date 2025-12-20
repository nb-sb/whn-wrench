package cn.web3er.wrench.framework.biz.rule02.logic;

import cn.web3er.wrench.design.framework.link.model2.handler.ILogicHandler;
import cn.web3er.wrench.framework.biz.rule02.factory.Rule02TradeRuleFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试异常处理的 Handler
 * 用于测试 applyAfterException 的调用
 */
@Slf4j
@Service
public class RuleLogic303 implements ILogicHandler<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> {

    public static final AtomicInteger applyBeforeCount = new AtomicInteger(0);
    public static final AtomicInteger applyAfterCount = new AtomicInteger(0);
    public static final AtomicInteger applyAfterExceptionCount = new AtomicInteger(0);
    public static final AtomicInteger applyCount = new AtomicInteger(0);

    public XxxResponse applyBefore(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext) throws Exception {
        applyBeforeCount.incrementAndGet();
        log.info("RuleLogic303 applyBefore 被调用");
        return null;
    }

    public void applyAfter(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext, XxxResponse result) throws Exception {
        applyAfterCount.incrementAndGet();
        log.info("RuleLogic303 applyAfter 被调用（不应该被执行，因为抛出了异常）");
    }

    public void applyAfterException(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext, Exception e) throws Exception {
        applyAfterExceptionCount.incrementAndGet();
        log.info("RuleLogic303 applyAfterException 被调用，exception: {}", e.getMessage());
        dynamicContext.setValue("exceptionHandled", true);
    }

    @Override
    public XxxResponse apply(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext) throws Exception {
        applyCount.incrementAndGet();
        log.info("RuleLogic303 apply 执行，准备抛出异常");
        throw new RuntimeException("测试异常：RuleLogic303 故意抛出的异常");
    }

    public static void resetCounters() {
        applyBeforeCount.set(0);
        applyAfterCount.set(0);
        applyAfterExceptionCount.set(0);
        applyCount.set(0);
    }
}

