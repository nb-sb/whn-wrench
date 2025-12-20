package cn.web3er.wrench.framework.biz.tree.factory;

import cn.web3er.wrench.design.framework.tree.StrategyHandler;
import cn.web3er.wrench.framework.biz.tree.node.RootNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 规则树工厂类
 * 构建策略树执行链路：RootNode -> SwitchRoot -> AccountNode -> (MemberLevel1Node | MemberLevel2Node)
 * 
 * 执行流程：
 * 1. RootNode: 根节点，路由到 SwitchRoot
 * 2. SwitchRoot: 开关节点，路由到 AccountNode
 * 3. AccountNode: 账户节点，执行多线程异步查询，根据账户状态和用户级别路由到不同会员节点
 * 4. MemberLevel1Node/MemberLevel2Node: 会员级别节点，执行最终业务逻辑并返回结果
 */
@Service
public class DefaultStrategyFactory {

    private final RootNode rootNode;

    public DefaultStrategyFactory(RootNode rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * 获取策略树处理器
     * @return 返回根节点，从根节点开始执行策略树
     */
    public StrategyHandler<String, DynamicContext, String> strategyHandler() {
        return rootNode;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext {

        private int level;

        private Map<String, Object> dataObjects = new HashMap<>();

        public <T> void setValue(String key, T value) {
            dataObjects.put(key, value);
        }

        @SuppressWarnings("unchecked")
        public <T> T getValue(String key) {
            Object value = dataObjects.get(key);
            return value != null ? (T) value : null;

        }

    }

}
