package cn.web3er.wrench.framework.biz.rule02.logic;

import cn.web3er.wrench.design.framework.link.model2.handler.ILogicHandler;
import cn.web3er.wrench.framework.biz.rule02.factory.Rule02TradeRuleFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试在 applyBefore 中停止的 Handler
 * 用于测试 applyBefore 返回后调用 applyAfter 的场景
 */
@Slf4j
@Service
public class RuleLogic302 implements ILogicHandler<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> {

    public static final AtomicInteger applyBeforeCount = new AtomicInteger(0);
    public static final AtomicInteger applyAfterCount = new AtomicInteger(0);
    public static final AtomicInteger applyCount = new AtomicInteger(0);

    @Override
    public XxxResponse applyBefore(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext) throws Exception {
        applyBeforeCount.incrementAndGet();
        log.info("RuleLogic302 applyBefore 被调用");
        // 在 applyBefore 中设置 stop，测试是否会调用 applyAfter
        return stop(requestParameter, dynamicContext, new XxxResponse("stopInBefore"));
    }

    @Override
    public void applyAfter(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext, XxxResponse result) throws Exception {
        applyAfterCount.incrementAndGet();
        log.info("RuleLogic302 applyAfter 被调用，result: {}", result);
    }

    @Override
    public XxxResponse apply(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext) throws Exception {
        applyCount.incrementAndGet();
        log.info("RuleLogic302 apply 执行（不应该被执行）");
        return next(requestParameter, dynamicContext);
    }

    public static void resetCounters() {
        applyBeforeCount.set(0);
        applyAfterCount.set(0);
        applyCount.set(0);
    }
}

