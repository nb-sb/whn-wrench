# WHN Wrench

WHN Wrench 是一个功能丰富的Java工具集，提供了多种可复用的组件，帮助开发者快速构建高质量、高性能的应用程序。当前版本包含限流组件、设计框架组件和动态配置中心等核心功能。

## 🚀 特性

- **限流组件** - 基于令牌桶算法的高性能限流器
- **设计框架组件** - 提供工厂模式、观察者模式等常用设计模式实现
- **动态配置中心** - 支持配置热更新和变更监听的配置管理系统

## 📦 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>com.whn</groupId>
    <artifactId>whn-wrench</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 基本使用

```java
import com.whn.wrench.WhnWrench;
import com.whn.wrench.config.ConfigCenter;
import com.whn.wrench.ratelimit.RateLimiter;

// 获取配置中心
ConfigCenter configCenter = WhnWrench.getConfigCenter();
configCenter.setConfig("app.name", "MyApp");
String appName = configCenter.getString("app.name", "DefaultApp");

// 创建限流器
RateLimiter rateLimiter = WhnWrench.createRateLimiter(100.0); // 100 tokens/sec
if (rateLimiter.tryAcquire()) {
    // 处理请求
}
```

## 📚 核心组件

### 1. 限流组件 (Rate Limiting)

提供基于令牌桶算法的限流实现，支持突发流量控制。

#### 主要接口

- `RateLimiter` - 限流器接口
- `TokenBucketRateLimiter` - 令牌桶限流器实现

#### 使用示例

```java
// 创建限流器：每秒允许10个请求，桶容量20
RateLimiter rateLimiter = new TokenBucketRateLimiter(10.0, 20);

// 尝试获取许可
if (rateLimiter.tryAcquire()) {
    System.out.println("请求通过");
} else {
    System.out.println("请求被限流");
}

// 批量获取许可
if (rateLimiter.tryAcquire(5)) {
    System.out.println("5个请求全部通过");
}

// 带超时的获取
if (rateLimiter.tryAcquire(1000)) { // 1秒超时
    System.out.println("在超时时间内获得许可");
}
```

### 2. 设计框架组件 (Design Framework)

提供常用设计模式的实现，包括工厂模式和观察者模式。

#### 工厂模式

```java
GenericFactory<String> factory = new GenericFactory<>();

// 注册创建器
factory.register("type1", () -> "Hello World");
factory.register("type2", () -> "Goodbye World");

// 创建实例
String instance = factory.create("type1"); // "Hello World"

// 获取支持的类型
String[] types = factory.getSupportedTypes();
```

#### 观察者模式

```java
ObserverPattern<String> eventBus = new ObserverPattern<>();

// 添加观察者
eventBus.addObserver(message -> System.out.println("Observer 1: " + message));
eventBus.addObserver(message -> System.out.println("Observer 2: " + message));

// 发布事件
eventBus.notifyObservers("Event occurred!");
```

### 3. 动态配置中心 (Configuration Center)

提供动态配置管理功能，支持配置热更新和变更监听。

#### 主要接口

- `ConfigCenter` - 配置中心接口
- `MemoryConfigCenter` - 内存配置中心实现
- `ConfigChangeListener` - 配置变更监听器

#### 使用示例

```java
ConfigCenter configCenter = new MemoryConfigCenter();

// 添加配置变更监听器
configCenter.addListener((key, oldValue, newValue) -> {
    System.out.println("配置变更: " + key + " = " + oldValue + " -> " + newValue);
});

// 设置配置
configCenter.setConfig("database.url", "jdbc:mysql://localhost:3306/mydb");
configCenter.setConfig("server.port", 8080);
configCenter.setConfig("feature.enabled", true);

// 读取配置
String dbUrl = configCenter.getString("database.url", "default-url");
int port = configCenter.getInt("server.port", 8080);
boolean enabled = configCenter.getBoolean("feature.enabled", false);

// 检查配置是否存在
if (configCenter.containsKey("database.url")) {
    // 配置存在
}
```

## 🔧 构建和测试

```bash
# 编译项目
mvn compile

# 运行测试
mvn test

# 运行示例程序
mvn compile exec:java -Dexec.mainClass="com.whn.wrench.example.ExampleApplication"

# 打包
mvn package
```

## 📊 性能特点

- **高并发支持** - 所有组件都采用线程安全设计
- **低延迟** - 限流组件基于高效的令牌桶算法
- **内存高效** - 合理的数据结构设计，内存占用小
- **可扩展** - 模块化设计，易于扩展和定制

## 🤝 版本信息

- **当前版本**: 1.0.0
- **JDK 要求**: Java 8 或更高版本
- **依赖**: 最小依赖，仅依赖 SLF4J 用于日志

## 📄 许可证

本项目采用 MIT 许可证。

## 🙋‍♂️ 贡献

欢迎提交 Issue 和 Pull Request 来帮助改进项目。
