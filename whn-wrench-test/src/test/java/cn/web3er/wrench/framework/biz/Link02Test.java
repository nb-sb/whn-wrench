package cn.web3er.wrench.framework.biz;

import cn.web3er.wrench.design.framework.link.model2.chain.BusinessLinkedList;
import cn.web3er.wrench.framework.biz.rule02.factory.Rule02TradeRuleFactory;
import cn.web3er.wrench.framework.biz.rule02.logic.XxxResponse;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class Link02Test {

    @Resource(name = "demo01")
    private BusinessLinkedList<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> businessLinkedList01;

    @Resource(name = "demo02")
    private BusinessLinkedList<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> businessLinkedList02;

    /**
     * 测试责任链：RuleLogic201 -> RuleLogic202
     * RuleLogic201 使用 next() 放行，继续执行 RuleLogic202
     * RuleLogic202 使用 stop() 停止，返回结果
     */
    @Test
    public void test_model02_01() throws Exception {
        XxxResponse apply = businessLinkedList01.apply("123", new Rule02TradeRuleFactory.DynamicContext());
        log.info("测试结果:{}", JSON.toJSONString(apply));
    }

    /**
     * 测试责任链：RuleLogic202
     * RuleLogic202 使用 stop() 停止，直接返回结果
     */
    @Test
    public void test_model02_02() throws Exception {
        XxxResponse apply = businessLinkedList02.apply("123", new Rule02TradeRuleFactory.DynamicContext());
        log.info("测试结果:{}", JSON.toJSONString(apply));
    }

}
