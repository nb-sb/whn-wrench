package cn.web3er.wrench.dynamic.config.center.config;

import cn.web3er.wrench.dynamic.config.center.domain.service.IDynamicConfigCenterService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;



@Configuration
public class DynamicConfigCenterAutoConfig implements BeanPostProcessor {


    private final IDynamicConfigCenterService dynamicConfigCenterService;

    public DynamicConfigCenterAutoConfig(IDynamicConfigCenterService dynamicConfigCenterService) {
        this.dynamicConfigCenterService = dynamicConfigCenterService;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return dynamicConfigCenterService.proxyObject(bean);
    }
}
