package cn.web3er.wrench.dynamic.config.center.types.annotations;

import java.lang.annotation.*;

/**
* @author: Wanghaonan @戏人看戏
* @description: 注解，动态配置中心标记，用来放到需要从动态配置中心获取的字段上
* @create: 2025/8/11 10:34
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface DCCValue {

    String value() default "";

}
