package cn.web3er.wrench.rate.limiter.enums;

/**
* @author: Wanghaonan @戏人看戏
* @description: 限流类型
* @create: 2025/8/20 13:02
*/

public enum LimitType {
    /**
     * 默认策略全局限流
     */
    DEFAULT,

    /**
     * 根据请求者IP进行限流
     */
    IP,

    /**
     * 实例限流(集群多后端实例)
     */
    CLUSTER,
    /**
     * 自定义限流方式
     */
    CUSTOM
}
