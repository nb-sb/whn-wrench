package cn.web3er.wrench.design.framework.link.model2;


import cn.web3er.wrench.design.framework.link.model2.chain.BusinessLinkedList;
import cn.web3er.wrench.design.framework.link.model2.handler.ILogicHandler;

/**
* @author: Wanghaonan @戏人看戏
* @description: 责任链兵工厂，用于链路装配，和单例链区别就是创建的时候一个是单例模式创建一个是原型模式创建链
 * 多例链使用流程：实现(implements ILogicHandler)自己的单个链路,在Factory中使用兵工厂(LinkArmory)造链,拿到链路(BusinessLinkedList)后调用apply即可
 * LinkArmory 使用 BusinessLinkedList 来管理多个 ILogicHandler 实例
 * 多例链每个链都是互不干扰的，不像单例链中执行逻辑和链关系都耦合在一起了，因为多例链中每次新建的链都有new一个新链 new BusinessLinkedList<>(linkName);
* @create: 2025/5/22 15:30
*/
public class LinkArmory<T, D extends DynamicContext, R> {

    private final BusinessLinkedList<T, D, R> logicLink;

    @SafeVarargs
    public LinkArmory(String linkName, ILogicHandler<T, D, R>... logicHandlers) {
        logicLink = new BusinessLinkedList<>(linkName);
        for (ILogicHandler<T, D, R> logicHandler: logicHandlers){
            logicLink.add(logicHandler);
        }
    }

    public BusinessLinkedList<T, D, R> getLogicLink() {
        return logicLink;
    }
}
