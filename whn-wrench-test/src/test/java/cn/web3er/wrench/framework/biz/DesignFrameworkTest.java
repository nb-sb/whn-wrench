package cn.web3er.wrench.framework.biz;

import cn.web3er.wrench.design.framework.tree.StrategyHandler;
import cn.web3er.wrench.framework.biz.tree.factory.DefaultStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * 规则树测试用例
 * 测试策略树模式的执行流程
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest()
public class DesignFrameworkTest {

    @Resource
    private DefaultStrategyFactory defaultStrategyFactory;

    /**
     * 测试规则树执行流程：
     * RootNode -> SwitchRoot -> AccountNode -> (MemberLevel1Node | MemberLevel2Node)
     * 
     * AccountNode 会根据以下条件路由到不同节点：
     * 1. 账户冻结 -> MemberLevel1Node
     * 2. 授信拦截 -> MemberLevel1Node
     * 3. 用户级别 level=1 -> MemberLevel1Node
     * 4. 其他情况 -> MemberLevel2Node
     */
    @Test
    public void test() throws Exception {
        StrategyHandler<String, DefaultStrategyFactory.DynamicContext, String> strategyHandler = defaultStrategyFactory.strategyHandler();
        String result = strategyHandler.apply("whn", new DefaultStrategyFactory.DynamicContext());

        log.info("测试结果:{}", result);
    }

}
