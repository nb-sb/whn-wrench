package com.whn.wrench.example;

import com.whn.wrench.WhnWrench;
import com.whn.wrench.config.ConfigCenter;
import com.whn.wrench.config.ConfigChangeListener;
import com.whn.wrench.framework.ObserverPattern;
import com.whn.wrench.ratelimit.RateLimiter;

/**
 * WHN Wrench 示例应用程序
 * Example application demonstrating all three core components
 */
public class ExampleApplication {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== WHN Wrench 工具集示例 ===");
        System.out.println(WhnWrench.getDescription());
        System.out.println("版本: " + WhnWrench.getVersion());
        System.out.println();
        
        // 1. 动态配置中心示例
        demonstrateConfigCenter();
        
        // 2. 限流组件示例
        demonstrateRateLimiter();
        
        // 3. 设计框架组件示例
        demonstrateDesignFramework();
        
        System.out.println("=== 示例运行完成 ===");
    }
    
    /**
     * 演示动态配置中心功能
     */
    private static void demonstrateConfigCenter() {
        System.out.println("1. 动态配置中心示例:");
        
        ConfigCenter configCenter = WhnWrench.getConfigCenter();
        
        // 添加配置变更监听器
        configCenter.addListener(new ConfigChangeListener() {
            @Override
            public void onConfigChange(String key, Object oldValue, Object newValue) {
                System.out.println("   配置变更: " + key + " = " + oldValue + " -> " + newValue);
            }
        });
        
        // 设置各种类型的配置
        configCenter.setConfig("app.name", "WHN Wrench Example");
        configCenter.setConfig("app.version", "1.0.0");
        configCenter.setConfig("server.port", 8080);
        configCenter.setConfig("feature.enabled", true);
        configCenter.setConfig("cache.timeout", 30.5);
        
        // 读取配置
        System.out.println("   应用名称: " + configCenter.getString("app.name", "Unknown"));
        System.out.println("   服务器端口: " + configCenter.getInt("server.port", 8080));
        System.out.println("   功能启用: " + configCenter.getBoolean("feature.enabled", false));
        System.out.println("   缓存超时: " + configCenter.getDouble("cache.timeout", 0.0) + "秒");
        
        // 更新配置
        configCenter.setConfig("server.port", 9090);
        
        System.out.println("   配置总数: " + configCenter.getKeys().length);
        System.out.println();
    }
    
    /**
     * 演示限流组件功能
     */
    private static void demonstrateRateLimiter() throws InterruptedException {
        System.out.println("2. 限流组件示例:");
        
        // 创建限流器 - 每秒5个令牌
        RateLimiter rateLimiter = WhnWrench.createRateLimiter(5.0);
        System.out.println("   创建限流器，速率: " + rateLimiter.getRate() + " tokens/sec");
        
        // 模拟请求处理
        System.out.println("   模拟10个连续请求:");
        for (int i = 1; i <= 10; i++) {
            if (rateLimiter.tryAcquire()) {
                System.out.println("   请求 " + i + ": 通过");
            } else {
                System.out.println("   请求 " + i + ": 被限流");
            }
        }
        
        System.out.println("   等待1秒后重试...");
        Thread.sleep(1000);
        
        System.out.println("   重试请求:");
        for (int i = 1; i <= 3; i++) {
            if (rateLimiter.tryAcquire()) {
                System.out.println("   重试请求 " + i + ": 通过");
            } else {
                System.out.println("   重试请求 " + i + ": 被限流");
            }
        }
        System.out.println();
    }
    
    /**
     * 演示设计框架组件功能
     */
    private static void demonstrateDesignFramework() {
        System.out.println("3. 设计框架组件示例:");
        
        // 观察者模式示例
        ObserverPattern<String> messageChannel = new ObserverPattern<>();
        
        // 添加观察者
        messageChannel.addObserver(message -> 
            System.out.println("   [邮件服务] 收到消息: " + message)
        );
        
        messageChannel.addObserver(message -> 
            System.out.println("   [短信服务] 收到消息: " + message)
        );
        
        messageChannel.addObserver(message -> 
            System.out.println("   [推送服务] 收到消息: " + message)
        );
        
        System.out.println("   注册了 " + messageChannel.getObserverCount() + " 个观察者");
        
        // 发送通知
        System.out.println("   发送广播消息...");
        messageChannel.notifyObservers("系统维护通知: 服务将在今晚22:00-24:00进行升级");
        
        // 工厂模式示例
        System.out.println("\n   工厂模式示例:");
        WhnWrench.getRateLimiterFactory().register("custom", () -> 
            WhnWrench.createRateLimiter(100.0)
        );
        
        RateLimiter customLimiter = WhnWrench.createRateLimiter("custom");
        System.out.println("   通过工厂创建自定义限流器，速率: " + customLimiter.getRate());
        
        String[] supportedTypes = WhnWrench.getRateLimiterFactory().getSupportedTypes();
        System.out.println("   支持的限流器类型: " + String.join(", ", supportedTypes));
        System.out.println();
    }
}