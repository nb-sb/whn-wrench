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
        // 边界检查：空链表直接返回 null
        if (this.first == null) {
            return null;
        }

        Node<ILogicHandler<T, D, R>> current = this.first;
        while (current != null) {
            ILogicHandler<T, D, R> item = current.item;
            if (item == null) {
                current = current.next;
                continue;
            }

            try {
                // 1. 前置调用
                R applyBefore = item.applyBefore(requestParameter, dynamicContext);
                //判断是否放行当前节点（为proceed为true的时候放行不拦截）
                if (!dynamicContext.isProceed()) {
                    item.applyAfter(requestParameter, dynamicContext, applyBefore);
                    return applyBefore;
                }

                // 2. 节点跳过
                if (dynamicContext.isJump()) {
                    current = current.next;
                    continue;
                }

                // 3. 执行节点
                R apply = item.apply(requestParameter, dynamicContext);
                // proceed 为 true 时，也要调用 applyAfter，然后继续下一个节点
                item.applyAfter(requestParameter, dynamicContext, apply);
                if (!dynamicContext.isProceed()) {
                    return apply;
                }
                current = current.next;
            } catch (Exception e) {
                item.applyAfterException(requestParameter, dynamicContext, e);
                throw e;
            }
        }

        return null;
    }
}
