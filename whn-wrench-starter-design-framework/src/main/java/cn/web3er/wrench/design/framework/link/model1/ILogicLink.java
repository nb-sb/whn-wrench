package cn.web3er.wrench.design.framework.link.model1;
/**
* @author: Wanghaonan @戏人看戏
* @description: 责任链接口（只定义有关执行方法，让子类实现）
* @create: 2025/5/21 10:26
*/
public interface ILogicLink<T,D,R> extends ILogicChainArmory<T,D,R>{

    R apply(T param,D dynamicContext) throws Exception;
}
