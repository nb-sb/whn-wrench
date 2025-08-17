package cn.web3er.wrench.design.framework.link.model1;
/**
* @author: Wanghaonan @戏人看戏
* @description: 责任链装配抽象类
 * 单例链：执行过程和链关系 放在一起了，没有解藕，比如Logic1.appendNext(Logic2),
 * 后面想额外 使用一条链中只有Logic1的时候可能会影响到之前链的构建
* @create: 2025/5/21 10:36
*/
public abstract class AbstractLogicLink<T,D,R> implements ILogicLink<T,D,R> {

    private ILogicLink<T,D,R> next;

    @Override
    public ILogicLink<T,D,R> next() {
        return next;
    }

    @Override
    public ILogicLink<T,D,R> appendNext(ILogicLink next) {
        this.next = next;
        return next;
    }

    protected R next(T requestParameter, D dynamicContext) throws Exception {
        return next.apply(requestParameter, dynamicContext);
    }
}
