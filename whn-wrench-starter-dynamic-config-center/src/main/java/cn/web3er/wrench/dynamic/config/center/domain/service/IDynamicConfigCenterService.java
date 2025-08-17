package cn.web3er.wrench.dynamic.config.center.domain.service;


import cn.web3er.wrench.dynamic.config.center.domain.valobj.AttributeVO;

/**
* @author: Wanghaonan @戏人看戏
* @description: 动态配置中心服务接口
* @create: 2025/8/11 11:47
*/
public interface IDynamicConfigCenterService {

    Object proxyObject(Object bean);

    /**
     * 调整属性值
     */
    void adjustAttributeValue(AttributeVO attributeVO);

}
