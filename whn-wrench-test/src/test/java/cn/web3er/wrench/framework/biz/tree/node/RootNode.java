package cn.web3er.wrench.framework.biz.tree.node;

import cn.web3er.wrench.design.framework.tree.StrategyHandler;
import cn.web3er.wrench.framework.biz.tree.AbstractXxxSupport;
import cn.web3er.wrench.framework.biz.tree.factory.DefaultStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RootNode extends AbstractXxxSupport {

    @Autowired
    private SwitchRoot switchRoot;

    @Override
    protected String doApply(String requestParameter, DefaultStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("【开关节点】规则决策树 userId:{}", requestParameter);
        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<String, DefaultStrategyFactory.DynamicContext, String> get(String requestParameter, DefaultStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return switchRoot;
    }

}
