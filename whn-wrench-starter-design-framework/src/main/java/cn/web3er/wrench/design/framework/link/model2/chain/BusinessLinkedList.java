package cn.web3er.wrench.design.framework.link.model2.chain;

import cn.web3er.wrench.design.framework.link.model2.DynamicContext;
import cn.web3er.wrench.design.framework.link.model2.handler.ILogicHandler;

/**
* @author: Wanghaonan @戏人看戏
* @description: 策略的执行链路（用于执行这条链）
* @create: 2025/5/22 15:28
*/
public class BusinessLinkedList<T, D extends DynamicContext, R> extends LinkedList<ILogicHandler<T, D, R>> implements ILogicHandler<T, D, R>{


    public BusinessLinkedList(String name) {
        super(name);
    }

    @Override
    public R apply(T requestParameter, D dynamicContext) throws Exception {
        Node<ILogicHandler<T, D, R>> current = this.first;
        do {
            ILogicHandler<T, D, R> item = current.item;
            R apply = item.apply(requestParameter, dynamicContext);
            //返回null则是继续下一个链的执行，如果不为空则直接返回内容
            if (null != apply) {
                return apply;
            }

            current = current.next;
        } while (null != current);

        return null;
    }
}
