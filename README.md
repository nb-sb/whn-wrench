# WHN Wrench 工具集

WHN Wrench 是一个功能丰富的Java工具集，提供了多种可复用的组件，帮助开发者快速构建高质量、高性能的应用程序。当前版本包含限流组件、设计框架组件和动态配置中心等核心功能。

## 目录

- [快速开始](#快速开始)
- [限流组件](#限流组件-rate-limiter)
- [设计框架组件](#设计框架组件)
- [动态配置中心](#动态配置中心)
- [示例代码](#示例代码)
- [常见问题](#常见问题)

## 快速开始

### 添加依赖

在您的项目pom.xml中添加以下依赖：

```xml
<dependency>
    <groupId>cn.web3er.wrench</groupId>
    <artifactId>whn-wrench-bom</artifactId>
    <version>1.6</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>

<!-- 根据需要选择以下组件 -->
<dependency>
    <groupId>cn.web3er.wrench</groupId>
    <artifactId>whn-wrench-starter-rate-limiter</artifactId>
</dependency>

<dependency>
    <groupId>cn.web3er.wrench</groupId>
    <artifactId>whn-wrench-starter-design-framework</artifactId>
</dependency>

<dependency>
    <groupId>cn.web3er.wrench</groupId>
    <artifactId>whn-wrench-starter-dynamic-config-center</artifactId>
</dependency>
```

### 基础配置

在`application.yml`中添加Redis配置（用于限流和动态配置中心）：

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

## 限流组件 (Rate Limiter)

`whn-wrench-starter-rate-limiter`是一个功能强大、易于使用的限流组件，支持多种限流策略，可用于防止接口被频繁调用、保护系统稳定性和安全性。

### 主要特性

- 支持基于注解的轻量级接口限流
- 支持全局限流、IP限流、集群限流等多种限流类型
- 支持自定义限流策略和回调方法
- 支持动态配置限流开关，可在运行时调整
- 支持将恶意请求加入黑名单，提供更强的防护能力
- 基于Redis实现分布式限流，可用于微服务架构
- 支持自定义限流策略和回调方法
- 支持动态配置限流开关，可在运行时调整
- 支持将恶意请求加入黑名单，提供更强的防护能力
- 基于Redis实现分布式限流，可用于微服务架构

### 使用方法

#### 1. 使用@RateLimiter注解（分布式限流）

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
    @RateLimiter(key = "userId", limitType = LimitType.CLUSTER, time = 5, count = 2, 
                 fallbackMethod = "rateLimiterFallback", blacklistCount = 5)
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

#### 2. 使用@RateLimiterAccessInterceptor注解（本地限流）

本地限流基于Guava的RateLimiter实现，适用于单机应用：

```java
import cn.web3er.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor;

@RestController
@RequestMapping("/api")
public class LocalRateLimitController {

    /**
     * 每秒允许5个请求
     */
    @GetMapping("/local-limit")
    @RateLimiterAccessInterceptor(key = "userId", permitsPerSecond = 5.0, 
                                fallbackMethod = "handleOverLimit", blacklistCount = 10)
    public String localLimit(@RequestParam String userId) {
        return "访问成功";
    }

    public String handleOverLimit(String userId) {
        return "访问频率过高，请稍后再试";
    }
}
```

### @RateLimiter注解参数说明

| 参数名 | 类型 | 默认值 | 描述 |
| --- | --- | --- | --- |
| key | String | "" | 限流的键值，可以是方法名、业务标识或参数名 |
| time | int | 60 | 限流的时间窗口，单位为秒 |
| count | int | 100 | 在时间窗口内允许通过的请求数量 |
| limitType | LimitType | LimitType.DEFAULT | 限流类型，包括DEFAULT(全局)、IP、CLUSTER(集群)、CUSTOM(自定义) |
| message | String | "{rate.limiter.message}" | 达到限流阈值时的提示消息，支持国际化 |
| timeout | int | 86400 | 限流策略的存活时间，单位为秒，默认为一天 |
| fallbackMethod | String | "null" | 触发限流时的回调方法名 |

### 动态开关控制

可以通过动态配置中心修改`rateLimiterSwitch`的值来控制限流功能的开关：

- `open`: 开启限流功能
- `close`: 关闭限流功能

```java
import cn.web3er.wrench.dynamic.config.center.types.annotations.DCCValue;

@Component
public class RateLimiterSwitchConfig {
    @DCCValue("rateLimiterSwitch:open")
    private String rateLimiterSwitch;
}
```

## 设计框架组件

`whn-wrench-starter-design-framework`提供了一套灵活的设计模式框架，主要包含策略模式和责任链模式的实现，帮助开发者快速构建可扩展、可维护的业务逻辑。

### 主要特性

- 提供策略模式和责任链模式的标准化实现
- 支持通过注解方式注册策略和处理器
- 支持动态策略选择和责任链构建
- 易于扩展和自定义业务逻辑

### 策略模式使用方法

#### 1. 创建策略工厂

```java
import cn.web3er.wrench.design.framework.tree.StrategyHandler;
import org.springframework.stereotype.Component;

@Component
public class DefaultStrategyFactory {

    // 定义动态上下文（可根据业务需求自定义）
    public static class DynamicContext {
        // 添加所需的上下文参数
    }

    // 创建策略处理器
    public StrategyHandler<String, DynamicContext, String> strategyHandler() {
        return new StrategyHandler<>();
    }
}
```

#### 2. 实现具体策略

```java
import cn.web3er.wrench.design.framework.tree.TreeNode;
import org.springframework.stereotype.Component;

@Component
@TreeNode("whn")
public class WhnStrategy implements IStrategy {

    @Override
    public String execute(DefaultStrategyFactory.DynamicContext context) {
        // 实现具体的业务逻辑
        return "处理结果";
    }
}
```

#### 3. 使用策略处理器

```java
import cn.web3er.wrench.design.framework.tree.StrategyHandler;
import org.springframework.stereotype.Service;

@Service
public class BusinessService {

    @Resource
    private DefaultStrategyFactory defaultStrategyFactory;

    public String processRequest(String strategyKey) {
        // 获取策略处理器
        StrategyHandler<String, DefaultStrategyFactory.DynamicContext, String> strategyHandler = 
            defaultStrategyFactory.strategyHandler();

        // 应用策略处理业务
        return strategyHandler.apply(strategyKey, new DefaultStrategyFactory.DynamicContext());
    }
}
```

### 责任链模式使用方法

#### 1. 定义处理节点接口

```java
public interface Handler {
    boolean handle(String request);
}
```

#### 2. 实现责任链节点

```java
import cn.web3er.wrench.design.framework.link.LinkNode;
import org.springframework.stereotype.Component;

@Component
@LinkNode(nodeId = "nodeA", sort = 1)
public class NodeAHandler implements Handler {

    @Override
    public boolean handle(String request) {
        // 处理逻辑
        // 返回true表示继续责任链，返回false表示中断责任链
        return true;
    }
}

@Component
@LinkNode(nodeId = "nodeB", sort = 2)
public class NodeBHandler implements Handler {

    @Override
    public boolean handle(String request) {
        // 处理逻辑
        return true;
    }
}
```

#### 3. 构建和使用责任链

```java
import cn.web3er.wrench.design.framework.link.Chain;
import cn.web3er.wrench.design.framework.link.ChainFactory;
import org.springframework.stereotype.Service;

@Service
public class ChainService {

    @Resource
    private ChainFactory chainFactory;

    public void process(String request) {
        Chain<Handler> chain = chainFactory.buildChain(Handler.class);
        chain.handle(node -> node.handle(request));
    }
}
```

## 动态配置中心

`whn-wrench-starter-dynamic-config-center`提供了一个基于Redis的动态配置中心，支持在运行时动态修改应用程序的配置，无需重启服务。

### 主要特性

- 基于Redis实现配置的分布式存储和同步
- 支持通过注解方式轻松读取配置
- 支持配置变更实时通知
- 支持多种数据类型的配置值
- 简化系统运维，提高系统灵活性

### 使用方法

#### 1. 配置Redis连接

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

#### 2. 使用@DCCValue注解读取配置

```java
import cn.web3er.wrench.dynamic.config.center.types.annotations.DCCValue;
import org.springframework.stereotype.Component;

@Component
public class ConfigExample {

    // 格式："配置键:默认值"
    @DCCValue("maxConnections:100")
    private int maxConnections;

    @DCCValue("featureEnabled:false")
    private boolean featureEnabled;

    @DCCValue("serverName:defaultServer")
    private String serverName;

    // 使用配置值的方法
    public void doSomething() {
        if (featureEnabled) {
            System.out.println("Feature is enabled with " + maxConnections + 
                               " connections on server: " + serverName);
        }
    }
}
```

#### 3. 配置更新监听

当配置中心的值发生变化时，会自动更新注入的字段值，无需额外编写代码。

#### 4. 手动读取和更新配置值

```java
import cn.web3er.wrench.dynamic.config.center.domain.service.IDynamicConfigCenterService;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    @Resource
    private IDynamicConfigCenterService configCenterService;

    public String getConfig(String key, String defaultValue) {
        return configCenterService.getAttributeValue(key, defaultValue);
    }

    public void updateConfig(String key, String value) {
        configCenterService.updateAttribute(key, value);
    }
}
```

## 示例代码

WHN Wrench 工具集提供了完整的测试示例，可以帮助您更好地理解和使用各个组件。

### 设计框架测试示例

```java
import cn.web3er.wrench.design.framework.tree.StrategyHandler;
import cn.web3er.wrench.framework.biz.tree.factory.DefaultStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest()
public class DesignFrameworkTest {

    @Resource
    private DefaultStrategyFactory defaultStrategyFactory;

    @Test
    public void test() throws Exception {
        StrategyHandler<String, DefaultStrategyFactory.DynamicContext, String> strategyHandler = 
            defaultStrategyFactory.strategyHandler();
        String result = strategyHandler.apply("whn", new DefaultStrategyFactory.DynamicContext());

        log.info("测试结果:{}", result);
    }
}
```

### 责任链测试示例

```java
import cn.web3er.wrench.design.framework.link.Chain;
import cn.web3er.wrench.design.framework.link.ChainFactory;
import cn.web3er.wrench.framework.biz.link.handler.Handler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest()
public class Link01Test {

    @Resource
    private ChainFactory chainFactory;

    @Test
    public void test() {
        Chain<Handler> chain = chainFactory.buildChain(Handler.class);
        chain.handle(node -> node.handle("test request"));
    }
}
```

## 常见问题

### 1. 如何在非Spring环境中使用这些组件？

WHN Wrench 组件主要基于Spring设计，但核心功能可以在非Spring环境中使用。您需要手动创建相关对象并进行初始化。

### 2. 限流组件是如何实现分布式限流的？

分布式限流是基于Redis实现的，使用了Redis的计数器功能结合Lua脚本保证了原子性操作，确保在分布式环境中的一致性限流效果。

### 3. 动态配置中心如何处理配置更新？

动态配置中心使用Redis的发布订阅机制，当配置值变更时，会发布通知到所有订阅的应用实例，实现配置的实时同步更新。

### 4. 责任链和策略模式有什么区别？

- 责任链模式：着重于请求在多个处理器之间的传递流程，每个处理器决定是否处理请求或传递给下一个处理器
- 策略模式：着重于算法的封装和动态选择，根据不同条件选择不同的策略实现

### 5. 如何扩展现有的组件功能？

WHN Wrench的所有组件都遵循开闭原则设计，您可以通过以下方式扩展功能：

- 继承现有的类并重写特定方法
- 实现相应的接口提供自定义实现
- 通过Spring的Bean注册机制替换默认实现

### 6. Redis连接失败会导致什么问题？

对于依赖Redis的功能（如分布式限流和动态配置中心），如果Redis连接失败：

- 限流组件会降级为本地限流模式或直接放行请求（取决于配置）
- 动态配置中心会使用默认配置值
- 应用日志中会记录相关错误信息

### 7. 组件是否支持自定义序列化方式？

是的，特别是在动态配置中心中，您可以自定义配置值的序列化和反序列化方式，支持复杂对象的存储和读取。

---

## 贡献指南

欢迎对WHN Wrench项目进行贡献！您可以通过以下方式参与：

- 提交问题和建议
- 提交代码改进和新功能
- 完善文档和示例

## 许可证

WHN Wrench使用MIT许可证，详情请参阅LICENSE文件。

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
