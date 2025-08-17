package cn.web3er.wrench.design.framework.tree;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 策略模版类（组合/规则树模式模型）
 * 策略模式：把变化的部分抽出来，放在不同的策略类里，由外部来决定使用哪一种。(不同的类implements相同的接口，可以直接使用不同的类
 * 模板方法模式：定义一个固定流程，把可变的部分留给子类去实现。（比如类中定义 abstract void add();
 * @param <T> 入参类型
 * @param <D> 上下文参数
 * @param <R> 返参类型
 */
public abstract class AbstractMultiThreadStrategyRouter<T, D, R> implements StrategyHandler<T, D, R>, StrategyMapper<T, D, R> {
    /**
     * 默认策略处理器
     */
    protected StrategyHandler<T, D, R> defaultStrategyHandler = DEFAULT;

    /**
     * 策略路由
     * 可以路由到下一个节点 并执行下一个节点的apply方法
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
    /**
     * 进行策略处理，这里使用通用的apply方法，其他方法只需要实现doApply和multiThread即可
     */
    @Override
    public R apply(T requestParameter, D dynamicContext) throws Exception {
        // 异步加载数据
        multiThread(requestParameter, dynamicContext);
        // 业务流程受理
        return doApply(requestParameter, dynamicContext);
    }

    /**
     * 异步加载数据
     */
    protected abstract void multiThread(T requestParameter, D dynamicContext) throws ExecutionException, InterruptedException, TimeoutException;

    /**
     * 业务流程受理
     */
    protected abstract R doApply(T requestParameter, D dynamicContext) throws Exception;

}
