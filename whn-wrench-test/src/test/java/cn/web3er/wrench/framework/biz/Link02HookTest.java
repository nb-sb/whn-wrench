package cn.web3er.wrench.framework.biz;

import cn.web3er.wrench.design.framework.link.model2.chain.BusinessLinkedList;
import cn.web3er.wrench.framework.biz.rule02.factory.Rule02TradeRuleFactory;
import cn.web3er.wrench.framework.biz.rule02.logic.RuleLogic301;
import cn.web3er.wrench.framework.biz.rule02.logic.RuleLogic302;
import cn.web3er.wrench.framework.biz.rule02.logic.RuleLogic303;
import cn.web3er.wrench.framework.biz.rule02.logic.XxxResponse;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * 测试责任链的钩子方法（applyBefore、applyAfter、applyAfterException）
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class Link02HookTest {

    @Resource(name = "demo03")
    private BusinessLinkedList<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> businessLinkedList03;

    @Resource(name = "demo04")
    private BusinessLinkedList<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> businessLinkedList04;

    @Resource(name = "demo05")
    private BusinessLinkedList<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> businessLinkedList05;

    @Resource(name = "demo06")
    private BusinessLinkedList<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> businessLinkedList06;

    @Before
    public void setUp() {
        // 每个测试前重置计数器
        RuleLogic301.resetCounters();
        RuleLogic302.resetCounters();
        RuleLogic303.resetCounters();
    }

    /**
     * 测试正常执行流程：applyBefore -> apply -> applyAfter
     */
    @Test
    public void test_normal_flow_applyBefore_apply_applyAfter() throws Exception {
        Rule02TradeRuleFactory.DynamicContext context = new Rule02TradeRuleFactory.DynamicContext();
        XxxResponse result = businessLinkedList03.apply("test301", context);

        log.info("测试结果:{}", JSON.toJSONString(result));
        log.info("钩子调用统计 - applyBefore: {}, applyAfter: {}, applyAfterException: {}",
                RuleLogic301.applyBeforeCount.get(),
                RuleLogic301.applyAfterCount.get(),
                RuleLogic301.applyAfterExceptionCount.get());

        // 验证钩子方法被调用
        assertEquals("applyBefore 应该被调用1次", 1, RuleLogic301.applyBeforeCount.get());
        assertEquals("applyAfter 应该被调用1次", 1, RuleLogic301.applyAfterCount.get());
        assertEquals("applyAfterException 不应该被调用", 0, RuleLogic301.applyAfterExceptionCount.get());

        // 验证上下文中的标记
        assertTrue("applyBefore 应该在上下文中设置标记", context.getValue("applyBeforeCalled"));
        assertTrue("applyAfter 应该在上下文中设置标记", context.getValue("applyAfterCalled"));
        assertTrue("apply 应该在上下文中设置标记", context.getValue("applyCalled"));
    }

    /**
     * 测试在 applyBefore 中停止的场景：applyBefore -> applyAfter（不执行 apply）
     */
    @Test
    public void test_stop_in_applyBefore() throws Exception {
        Rule02TradeRuleFactory.DynamicContext context = new Rule02TradeRuleFactory.DynamicContext();
        XxxResponse result = businessLinkedList04.apply("test302", context);

        log.info("测试结果:{}", JSON.toJSONString(result));
        log.info("钩子调用统计 - applyBefore: {}, applyAfter: {}, apply: {}",
                RuleLogic302.applyBeforeCount.get(),
                RuleLogic302.applyAfterCount.get(),
                RuleLogic302.applyCount.get());

        // 验证钩子方法被调用
        assertEquals("applyBefore 应该被调用1次", 1, RuleLogic302.applyBeforeCount.get());
        assertEquals("applyAfter 应该被调用1次（在 applyBefore 返回后）", 1, RuleLogic302.applyAfterCount.get());
        assertEquals("apply 不应该被调用（因为在 applyBefore 中停止了）", 0, RuleLogic302.applyCount.get());

        // 验证返回结果
        assertNotNull("应该返回结果", result);
        assertEquals("返回结果应该是 stopInBefore", "stopInBefore", result.getAge());
    }

    /**
     * 测试异常处理：applyBefore -> apply（抛出异常）-> applyAfterException
     */
    @Test
    public void test_exception_flow_applyAfterException() throws Exception {
        Rule02TradeRuleFactory.DynamicContext context = new Rule02TradeRuleFactory.DynamicContext();

        try {
            businessLinkedList05.apply("test303", context);
            fail("应该抛出异常");
        } catch (RuntimeException e) {
            log.info("捕获到预期的异常: {}", e.getMessage());
        }

        log.info("钩子调用统计 - applyBefore: {}, apply: {}, applyAfter: {}, applyAfterException: {}",
                RuleLogic303.applyBeforeCount.get(),
                RuleLogic303.applyCount.get(),
                RuleLogic303.applyAfterCount.get(),
                RuleLogic303.applyAfterExceptionCount.get());

        // 验证钩子方法被调用
        assertEquals("applyBefore 应该被调用1次", 1, RuleLogic303.applyBeforeCount.get());
        assertEquals("apply 应该被调用1次（抛出异常前）", 1, RuleLogic303.applyCount.get());
        assertEquals("applyAfter 不应该被调用（因为抛出了异常）", 0, RuleLogic303.applyAfterCount.get());
        assertEquals("applyAfterException 应该被调用1次", 1, RuleLogic303.applyAfterExceptionCount.get());

        // 验证上下文中的标记
        assertTrue("异常处理应该在上下文中设置标记", context.getValue("exceptionHandled"));
    }

    /**
     * 测试多个节点的钩子调用顺序
     * RuleLogic301 -> RuleLogic202
     * 验证每个节点的 applyBefore 和 applyAfter 都被正确调用
     */
    @Test
    public void test_multiple_nodes_hooks() throws Exception {
        Rule02TradeRuleFactory.DynamicContext context = new Rule02TradeRuleFactory.DynamicContext();
        XxxResponse result = businessLinkedList06.apply("test306", context);

        log.info("测试结果:{}", JSON.toJSONString(result));
        log.info("RuleLogic301 钩子调用统计 - applyBefore: {}, applyAfter: {}",
                RuleLogic301.applyBeforeCount.get(),
                RuleLogic301.applyAfterCount.get());

        // 验证 RuleLogic301 的钩子被调用
        assertEquals("RuleLogic301 applyBefore 应该被调用1次", 1, RuleLogic301.applyBeforeCount.get());
        assertEquals("RuleLogic301 applyAfter 应该被调用1次", 1, RuleLogic301.applyAfterCount.get());

        // 验证返回结果（来自 RuleLogic202）
        assertNotNull("应该返回结果", result);
        assertEquals("返回结果应该是 18", "18", result.getAge());
    }

    /**
     * 测试空链表场景
     */
    @Test
    public void test_empty_chain() throws Exception {
        BusinessLinkedList<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> emptyChain =
                new BusinessLinkedList<>("empty");
        Rule02TradeRuleFactory.DynamicContext context = new Rule02TradeRuleFactory.DynamicContext();
        XxxResponse result = emptyChain.apply("test", context);

        assertNull("空链表应该返回 null", result);
    }
}

