package cn.web3er.wrench.framework.biz.rule02.logic;

import cn.web3er.wrench.design.framework.link.model2.handler.ILogicHandler;
import cn.web3er.wrench.framework.biz.rule02.factory.Rule02TradeRuleFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RuleLogic202 implements ILogicHandler<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> {

    /**
     * 使用 stop() 方法：设置 proceed = false，停止责任链执行，不再继续下一个节点
     * 当前节点的 apply 方法已执行完成，但后续节点不会执行
     */
    public XxxResponse apply(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext) throws Exception{

        log.info("link model02 RuleLogic202");

        return stop(requestParameter, dynamicContext, new XxxResponse("18"));
    }

}
