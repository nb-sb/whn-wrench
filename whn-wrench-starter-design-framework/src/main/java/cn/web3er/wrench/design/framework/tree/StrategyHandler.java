package cn.web3er.wrench.design.framework.tree;

/**
 * 策略处理器
 * @param <T> 入参类型
 * @param <D> 上下文参数
 * @param <R> 返参类型
 */
public interface StrategyHandler<T, D, R> {

    /**
     * 默认策略处理器
     * @param requestParameter 入参
     * @param dynamicContext 上下文
     * @return 返参
     * @throws Exception 异常
     * 也可以用lambda表达式来实现// StrategyHandler DEFAULT = (T, D) -> null;
     */
    StrategyHandler DEFAULT = new StrategyHandler() {
        @Override
        public Object apply(Object requestParameter, Object dynamicContext) throws Exception {
            return null;
        }
    };


    //默认策略处理器
    R apply(T requestParameter, D dynamicContext) throws Exception;

}
