package cn.web3er.wrench.design.framework.link.model1;
/**
* @author: Wanghaonan @戏人看戏
* @description: 责任链装配（只连接有关的类）
* @create: 2025/5/21 10:30
*/
public interface ILogicChainArmory<T,D,R> {

    ILogicLink<T,D,R> next();

    ILogicLink<T,D,R> appendNext(ILogicLink next);


}
