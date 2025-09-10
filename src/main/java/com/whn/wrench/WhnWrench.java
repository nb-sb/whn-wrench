package com.whn.wrench;

import com.whn.wrench.config.ConfigCenter;
import com.whn.wrench.config.MemoryConfigCenter;
import com.whn.wrench.framework.GenericFactory;
import com.whn.wrench.ratelimit.RateLimiter;
import com.whn.wrench.ratelimit.TokenBucketRateLimiter;

/**
 * WHN Wrench 工具集主入口类
 * Main entry point for WHN Wrench toolkit
 */
public class WhnWrench {
    
    private static final ConfigCenter DEFAULT_CONFIG_CENTER = new MemoryConfigCenter();
    private static final GenericFactory<RateLimiter> RATE_LIMITER_FACTORY = new GenericFactory<>();
    
    static {
        // 初始化默认的限流器工厂
        RATE_LIMITER_FACTORY.register("token-bucket", () -> new TokenBucketRateLimiter(100.0));
        RATE_LIMITER_FACTORY.register("token-bucket-custom", () -> {
            double rate = DEFAULT_CONFIG_CENTER.getDouble("ratelimiter.rate", 100.0);
            long capacity = DEFAULT_CONFIG_CENTER.getLong("ratelimiter.capacity", (long) rate);
            return new TokenBucketRateLimiter(rate, capacity);
        });
    }
    
    /**
     * 获取默认配置中心
     * Get default configuration center
     * 
     * @return default configuration center
     */
    public static ConfigCenter getConfigCenter() {
        return DEFAULT_CONFIG_CENTER;
    }
    
    /**
     * 创建限流器
     * Create rate limiter
     * 
     * @param type limiter type
     * @return rate limiter instance
     */
    public static RateLimiter createRateLimiter(String type) {
        return RATE_LIMITER_FACTORY.create(type);
    }
    
    /**
     * 创建自定义限流器
     * Create custom rate limiter
     * 
     * @param rate tokens per second
     * @return rate limiter instance
     */
    public static RateLimiter createRateLimiter(double rate) {
        return new TokenBucketRateLimiter(rate);
    }
    
    /**
     * 创建自定义限流器
     * Create custom rate limiter
     * 
     * @param rate tokens per second
     * @param capacity bucket capacity
     * @return rate limiter instance
     */
    public static RateLimiter createRateLimiter(double rate, long capacity) {
        return new TokenBucketRateLimiter(rate, capacity);
    }
    
    /**
     * 获取限流器工厂
     * Get rate limiter factory
     * 
     * @return rate limiter factory
     */
    public static GenericFactory<RateLimiter> getRateLimiterFactory() {
        return RATE_LIMITER_FACTORY;
    }
    
    /**
     * 获取工具集版本信息
     * Get toolkit version information
     * 
     * @return version string
     */
    public static String getVersion() {
        return "1.0.0";
    }
    
    /**
     * 获取工具集描述信息
     * Get toolkit description
     * 
     * @return description string
     */
    public static String getDescription() {
        return "WHN Wrench - 一个功能丰富的Java工具集，提供了多种可复用的组件，帮助开发者快速构建高质量、高性能的应用程序";
    }
}