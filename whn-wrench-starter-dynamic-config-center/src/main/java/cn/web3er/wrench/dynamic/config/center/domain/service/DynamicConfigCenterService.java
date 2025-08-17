package cn.web3er.wrench.dynamic.config.center.domain.service;

import cn.web3er.wrench.dynamic.config.center.config.DynamicConfigCenterAutoConfig;
import cn.web3er.wrench.dynamic.config.center.config.DynamicConfigCenterAutoProperties;
import cn.web3er.wrench.dynamic.config.center.domain.valobj.AttributeVO;
import cn.web3er.wrench.dynamic.config.center.types.annotations.DCCValue;
import cn.web3er.wrench.dynamic.config.center.types.common.Constants;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicConfigCenterService implements IDynamicConfigCenterService {


    private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterAutoConfig.class);

    private final DynamicConfigCenterAutoProperties properties;

    private final RedissonClient redissonClient;

    private final Map<String, Object> dccBeanGroup = new ConcurrentHashMap<>();


    public DynamicConfigCenterService(DynamicConfigCenterAutoProperties properties, RedissonClient redissonClient) {
        this.properties = properties;
        this.redissonClient = redissonClient;
    }

    @Override
    public Object proxyObject(Object bean) {
        // 注意；增加 AOP 代理后，获得类的方式要通过 AopProxyUtils.getTargetClass(bean); 不能直接 bean.class 因为代理后类的结构发生变化，这样不能获得到自己的自定义注解了。
        Class<?> targetBeanClass = bean.getClass();
        Object targetBeanObject = bean;
        if (AopUtils.isAopProxy(bean)) {
            targetBeanClass = AopUtils.getTargetClass(bean);
            targetBeanObject = AopProxyUtils.getSingletonTarget(bean);
        }

        Field[] fields = targetBeanClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(DCCValue.class)) {
                continue;
            }

            DCCValue dccValue = field.getAnnotation(DCCValue.class);

            String value = dccValue.value();
            if (StringUtils.isBlank(value)) {
                throw new RuntimeException(field.getName() + " @DCCValue is not config value config case 「isSwitch/isSwitch:1」");
            }

            String[] splits = value.split(Constants.SYMBOL_COLON);
            String key = properties.getKey(splits[0].trim());

            String defaultValue = splits.length == 2 ? splits[1] : null;

            // 设置值
            String setValue = defaultValue;

            try {
                // 如果为空则抛出异常
                if (StringUtils.isBlank(defaultValue)) {
                    throw new RuntimeException("dcc config error " + key + " is not null - 请配置默认值！");
                }

                // Redis 操作，判断配置Key是否存在，不存在则创建，存在则获取最新值
                RBucket<String> bucket = redissonClient.getBucket(key);
                boolean exists = bucket.isExists();
                if (!exists) {
                    bucket.set(defaultValue);
                } else {
                    setValue = bucket.get();
                }

                field.setAccessible(true);
                field.set(targetBeanObject, setValue);
                field.setAccessible(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            dccBeanGroup.put(key, targetBeanObject);
        }

        return bean;
    }

    @Override
    public void adjustAttributeValue(AttributeVO attributeVO) {
        // 属性信息
        String key = properties.getKey(attributeVO.getAttribute());
        String value = attributeVO.getValue();

        // 设置值
        RBucket<String> bucket = redissonClient.getBucket(key);
        boolean exists = bucket.isExists();
        if (!exists) {
            return;
        }
        bucket.set(attributeVO.getValue());

        Object objBean = dccBeanGroup.get(key);
        if (null == objBean) {
            return;
        }

        Class<?> objBeanClass = objBean.getClass();
        // 检查 objBean 是否是代理对象
        if (AopUtils.isAopProxy(objBean)) {
            // 获取代理对象的目标对象
            objBeanClass = AopUtils.getTargetClass(objBean);
        }

        try {
            // 1. getDeclaredField 方法用于获取指定类中声明的所有字段，包括私有字段、受保护字段和公共字段。
            // 2. getField 方法用于获取指定类中的公共字段，即只能获取到公共访问修饰符（public）的字段。
            Field field = objBeanClass.getDeclaredField(attributeVO.getAttribute());
            field.setAccessible(true);
            field.set(objBean, value);
            field.setAccessible(false);

            log.info("DCC 节点监听，动态设置值 {} {}", key, value);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
