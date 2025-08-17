package cn.web3er.wrench.design.framework.link.model2.handler;

import cn.web3er.wrench.design.framework.link.model2.DynamicContext;

/**
* @author: Wanghaonan @戏人看戏
* @description: 逻辑处理器
* @create: 2025/5/22 15:25
*/
public interface ILogicHandler<T, D extends DynamicContext, R>  {

    /**
     * 谁用了next谁重现此方法（override），重写了此方法然后执行的时候则执行完此方法就结束了
     * 重写后 执行了 BusinessLinkedList#apply 方法 if (null != apply) return apply; 这里执行完就return结束了
     * @param requestParameter
     * @param dynamicContext
     * @return
     */
    default R next(T requestParameter, D dynamicContext) {
        dynamicContext.setProceed(true);
        return null;
    }

    default R stop(T requestParameter, D dynamicContext, R result){
        dynamicContext.setProceed(false);
        return result;
    }

    R apply(T requestParameter, D dynamicContext) throws Exception;

}
