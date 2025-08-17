package cn.web3er.wrench.design.framework.tree;

/**
 * 策略路由抽象类
 */
public abstract class AbstractStrategyRouter<T, D, R> implements StrategyHandler<T, D, R>,StrategyMapper<T, D, R>{
    /**
     * 默认策略处理器
     */
    protected StrategyHandler<T, D, R> defaultStrategyHandler = DEFAULT;

    /**
     * 策略路由
     * 可以路由到下一个节点进行执行下一个节点的apply方法
     * @param requestParameter 入参
     * @param dynamicContext   上下文
     * @return 返参
     * @throws Exception 异常
     */
    public R router(T requestParameter, D dynamicContext) throws Exception {
        StrategyHandler<T, D, R> strategyHandler = get(requestParameter, dynamicContext);
        if (null != strategyHandler) {
            return strategyHandler.apply(requestParameter, dynamicContext);
        }
        return defaultStrategyHandler.apply(requestParameter, dynamicContext);
    }
}
