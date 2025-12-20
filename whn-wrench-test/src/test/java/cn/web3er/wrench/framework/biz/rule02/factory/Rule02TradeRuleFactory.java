package cn.web3er.wrench.framework.biz.rule02.factory;

import cn.web3er.wrench.design.framework.link.model2.LinkArmory;
import cn.web3er.wrench.design.framework.link.model2.chain.BusinessLinkedList;
import cn.web3er.wrench.framework.biz.rule02.logic.RuleLogic201;
import cn.web3er.wrench.framework.biz.rule02.logic.RuleLogic202;
import cn.web3er.wrench.framework.biz.rule02.logic.RuleLogic301;
import cn.web3er.wrench.framework.biz.rule02.logic.RuleLogic302;
import cn.web3er.wrench.framework.biz.rule02.logic.RuleLogic303;
import cn.web3er.wrench.framework.biz.rule02.logic.XxxResponse;
import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


@Service
public class Rule02TradeRuleFactory {

    /**
     * 构建责任链：RuleLogic201 -> RuleLogic202
     * 执行流程：
     * 1. RuleLogic201.apply() 执行，调用 next() 设置 proceed=true，继续下一个节点
     * 2. RuleLogic202.apply() 执行，调用 stop() 设置 proceed=false，停止责任链并返回结果
     */
    @Bean("demo01")
    public BusinessLinkedList<String, DynamicContext, XxxResponse> demo01(RuleLogic201 ruleLogic201, RuleLogic202 ruleLogic202) {

        LinkArmory<String, DynamicContext, XxxResponse> linkArmory = new LinkArmory<>("demo01", ruleLogic201, ruleLogic202);

        return linkArmory.getLogicLink();
    }

    /**
     * 构建责任链：RuleLogic202
     * 执行流程：
     * 1. RuleLogic202.apply() 执行，调用 stop() 设置 proceed=false，停止责任链并返回结果
     */
    @Bean("demo02")
    public BusinessLinkedList<String, DynamicContext, XxxResponse> demo02(RuleLogic202 ruleLogic202) {

        LinkArmory<String, DynamicContext, XxxResponse> linkArmory = new LinkArmory<>("demo02", ruleLogic202);

        return linkArmory.getLogicLink();
    }

    /**
     * 测试钩子方法的责任链：RuleLogic301
     * 用于测试 applyBefore、applyAfter 的正常调用
     */
    @Bean("demo03")
    public BusinessLinkedList<String, DynamicContext, XxxResponse> demo03(RuleLogic301 ruleLogic301) {
        LinkArmory<String, DynamicContext, XxxResponse> linkArmory = new LinkArmory<>("demo03", ruleLogic301);
        return linkArmory.getLogicLink();
    }

    /**
     * 测试在 applyBefore 中停止的责任链：RuleLogic302
     * 用于测试 applyBefore 返回后调用 applyAfter 的场景
     */
    @Bean("demo04")
    public BusinessLinkedList<String, DynamicContext, XxxResponse> demo04(RuleLogic302 ruleLogic302) {
        LinkArmory<String, DynamicContext, XxxResponse> linkArmory = new LinkArmory<>("demo04", ruleLogic302);
        return linkArmory.getLogicLink();
    }

    /**
     * 测试异常处理的责任链：RuleLogic303
     * 用于测试 applyAfterException 的调用
     */
    @Bean("demo05")
    public BusinessLinkedList<String, DynamicContext, XxxResponse> demo05(RuleLogic303 ruleLogic303) {
        LinkArmory<String, DynamicContext, XxxResponse> linkArmory = new LinkArmory<>("demo05", ruleLogic303);
        return linkArmory.getLogicLink();
    }

    /**
     * 测试多个节点的钩子调用：RuleLogic301 -> RuleLogic202
     * 用于测试多个节点的 applyBefore、applyAfter 调用顺序
     */
    @Bean("demo06")
    public BusinessLinkedList<String, DynamicContext, XxxResponse> demo06(RuleLogic301 ruleLogic301, RuleLogic202 ruleLogic202) {
        LinkArmory<String, DynamicContext, XxxResponse> linkArmory = new LinkArmory<>("demo06", ruleLogic301, ruleLogic202);
        return linkArmory.getLogicLink();
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext extends cn.web3er.wrench.design.framework.link.model2.DynamicContext {
        private String age;
    }

}
