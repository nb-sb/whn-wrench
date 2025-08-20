# WHN Wrench 工具集

## 限流组件 (Rate Limiter)

`whn-wrench-starter-rate-limiter`是一个功能强大、易于使用的限流组件，支持多种限流策略，可用于防止接口被频繁调用、保护系统稳定性和安全性。

### 主要特性

- 支持基于注解的轻量级接口限流
- 支持全局限流、IP限流、集群限流等多种限流类型
- 支持自定义限流策略和回调方法
- 支持动态配置限流开关，可在运行时调整
- 支持将恶意请求加入黑名单，提供更强的防护能力
- 基于Redis实现分布式限流，可用于微服务架构

### 快速开始

#### 1. 添加依赖

```xml
<dependency>
    <groupId>cn.web3er.wrench</groupId>
    <artifactId>whn-wrench-starter-rate-limiter</artifactId>
    <version>1.6</version>
</dependency>
```

#### 2. 配置Redis连接

在`application.yml`中配置Redis连接信息：

```yaml
whn:
  wrench:
    config:
      register:
        host: 127.0.0.1
        port: 6379
        password: your_password
        poolSize: 64
        minIdleSize: 10
        idleTimeout: 10000
        connectTimeout: 10000
        retryAttempts: 3
        retryInterval: 1000
        pingInterval: 0
        keepAlive: true
```

#### 3. 使用注解进行限流

在需要限流的方法上添加`@RateLimiter`注解：

```java
import cn.web3er.wrench.rate.limiter.types.annotations.RateLimiter;
import cn.web3er.wrench.rate.limiter.enums.LimitType;

@RestController
@RequestMapping("/api")
public class DemoController {

    /**
     * 默认限流策略：60秒内最多允许100次访问
     */
    @GetMapping("/test1")
    @RateLimiter(key = "test1")
    public String test1() {
        return "success";
    }

    /**
     * 自定义限流策略：30秒内最多允许5次访问
     */
    @GetMapping("/test2")
    @RateLimiter(key = "test2", time = 30, count = 5, message = "访问过于频繁，请稍后再试")
    public String test2() {
        return "success";
    }

    /**
     * 基于IP的限流策略
     */
    @GetMapping("/test3")
    @RateLimiter(key = "test3", limitType = LimitType.IP, time = 60, count = 10)
    public String test3() {
        return "success";
    }

    /**
     * 集群限流策略，带有自定义回调方法
     */
    @GetMapping("/test4/{userId}")
    @RateLimiter(key = "userId", limitType = LimitType.CLUSTER, time = 5, count = 2, fallbackMethod = "rateLimiterFallback", blacklistCount = 5)
    public String test4(@PathVariable("userId") String userId) {
        return "success: " + userId;
    }

    /**
     * 限流后的回调方法，方法签名必须与原方法一致
     */
    public String rateLimiterFallback(String userId) {
        return "访问频率超限，请稍后再试";
    }
}
```

### 注解参数说明

`@RateLimiter`注解支持以下参数：

| 参数名 | 类型 | 默认值 | 描述 |
| --- | --- | --- | --- |
| key | String | "" | 限流的键值，可以是方法名、业务标识或参数名 |
| time | int | 60 | 限流的时间窗口，单位为秒 |
| count | int | 100 | 在时间窗口内允许通过的请求数量 |
| limitType | LimitType | LimitType.DEFAULT | 限流类型，包括DEFAULT(全局)、IP、CLUSTER(集群)、CUSTOM(自定义) |
| message | String | "{rate.limiter.message}" | 达到限流阈值时的提示消息，支持国际化 |
| timeout | int | 86400 | 限流策略的存活时间，单位为秒，默认为一天 |
| fallbackMethod | String | "" | 触发限流时的回调方法名 |
| blacklistCount | int | 0 | 加入黑名单的阈值，超过该值将被加入黑名单24小时 |

### 高级用法

#### 动态开关控制

可以通过动态配置中心修改`rateLimiterSwitch`的值来控制限流功能的开关：

- `open`: 开启限流功能
- `close`: 关闭限流功能

```java
@Component
public class RateLimiterSwitchUpdater {

    @DCCValue("rateLimiterSwitch:open")
    private String rateLimiterSwitch;

    public void updateSwitch(String value) {
        // 动态修改rateLimiterSwitch的值
    }
}
```

#### 基于Redis的分布式限流

对于分布式系统，可以直接使用`RedisUtils`类提供的方法进行限流：

```java
import cn.web3er.wrench.rate.limiter.types.RedisUtils;
import cn.web3er.wrench.rate.limiter.types.RateType;

// 限流key, 限流类型, 速率, 速率间隔(秒), 超时时间(秒)
long remainingCount = RedisUtils.rateLimiter("api:test", RateType.OVERALL, 100, 60, 86400);
if (remainingCount == -1) {
    // 达到限流阈值，拒绝请求
    return "请求过于频繁，请稍后再试";
}
```

#### 黑名单机制

当用户频繁触发限流时，可以将其加入黑名单，在一段时间内拒绝所有请求：

1. 设置`blacklistCount`参数，当触发限流次数超过该值时，用户将被加入黑名单24小时
2. 使用`fallbackMethod`参数指定回调方法，在用户被加入黑名单时返回友好提示

### 最佳实践

1. **合理设置限流阈值**：根据系统性能和业务需求，设置合适的`time`和`count`值
2. **区分业务接口**：对不同的业务接口设置不同的限流策略，核心接口可以更严格
3. **使用回调方法**：为限流触发提供友好的提示信息，提升用户体验
4. **启用黑名单机制**：对于恶意访问，设置黑名单机制进行拦截
5. **监控限流情况**：关注日志中的限流记录，及时调整限流策略

### 常见问题

1. **Q: 如何在测试环境关闭限流功能？**
   A: 在配置中设置`rateLimiterSwitch=close`即可关闭限流功能

2. **Q: 限流是基于什么实现的？**
   A: 本地限流基于Guava的RateLimiter实现，分布式限流基于Redis实现

3. **Q: 如何自定义限流策略？**
   A: 可以通过自定义实现`RateLimiterAOP`类来实现自定义限流策略

4. **Q: 黑名单数据会持久化吗？**
   A: 本地黑名单默认存储在内存中，有效期为24小时；分布式场景下建议将黑名单数据存储在Redis中

5. **Q: 如何获取当前限流统计信息？**
   A: 可以通过日志查看限流情况，也可以扩展实现统计接口

### 性能考量

- 本地限流对性能影响较小，适用于单体应用
- 分布式限流依赖Redis，会有少量网络开销，但通常不会成为性能瓶颈
- 建议在网关层实现限流，减轻后端服务压力

### 依赖要求

- JDK 8+
- Spring Boot 2.7+
- Redis 5.0+（用于分布式限流）
