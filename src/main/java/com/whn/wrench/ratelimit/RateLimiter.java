package com.whn.wrench.ratelimit;

/**
 * 限流器接口
 * Rate limiter interface for controlling access rates
 */
public interface RateLimiter {
    
    /**
     * 尝试获取许可
     * Try to acquire a permit
     * 
     * @return true if permit acquired, false otherwise
     */
    boolean tryAcquire();
    
    /**
     * 尝试获取指定数量的许可
     * Try to acquire specified number of permits
     * 
     * @param permits number of permits to acquire
     * @return true if permits acquired, false otherwise
     */
    boolean tryAcquire(int permits);
    
    /**
     * 尝试在指定时间内获取许可
     * Try to acquire permit within specified timeout
     * 
     * @param timeoutMs timeout in milliseconds
     * @return true if permit acquired within timeout, false otherwise
     */
    boolean tryAcquire(long timeoutMs);
    
    /**
     * 获取当前限流器的限制速率
     * Get current rate limit
     * 
     * @return rate limit per second
     */
    double getRate();
    
    /**
     * 重置限流器状态
     * Reset rate limiter state
     */
    void reset();
}