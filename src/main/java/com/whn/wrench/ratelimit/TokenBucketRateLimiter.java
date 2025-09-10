package com.whn.wrench.ratelimit;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 基于令牌桶算法的限流器实现
 * Token bucket based rate limiter implementation
 */
public class TokenBucketRateLimiter implements RateLimiter {
    
    private final double rate;              // 令牌生成速率 (tokens per second)
    private final long capacity;            // 桶容量
    private final AtomicLong tokens;        // 当前令牌数量
    private volatile long lastRefillTime;   // 上次填充时间
    
    /**
     * 构造函数
     * 
     * @param rate tokens per second
     * @param capacity bucket capacity
     */
    public TokenBucketRateLimiter(double rate, long capacity) {
        if (rate <= 0) {
            throw new IllegalArgumentException("Rate must be positive");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        
        this.rate = rate;
        this.capacity = capacity;
        this.tokens = new AtomicLong(capacity);
        this.lastRefillTime = System.nanoTime();
    }
    
    /**
     * 便捷构造函数，容量等于速率
     * 
     * @param rate tokens per second and bucket capacity
     */
    public TokenBucketRateLimiter(double rate) {
        this(rate, (long) rate);
    }
    
    @Override
    public boolean tryAcquire() {
        return tryAcquire(1);
    }
    
    @Override
    public boolean tryAcquire(int permits) {
        if (permits <= 0) {
            throw new IllegalArgumentException("Permits must be positive");
        }
        
        refillTokens();
        
        // 尝试获取令牌
        long currentTokens = tokens.get();
        if (currentTokens >= permits) {
            return tokens.compareAndSet(currentTokens, currentTokens - permits);
        }
        
        return false;
    }
    
    @Override
    public boolean tryAcquire(long timeoutMs) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeoutMs;
        
        while (System.currentTimeMillis() < endTime) {
            if (tryAcquire()) {
                return true;
            }
            
            try {
                Thread.sleep(1); // 短暂休眠避免忙等待
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        return false;
    }
    
    @Override
    public double getRate() {
        return rate;
    }
    
    @Override
    public void reset() {
        tokens.set(capacity);
        lastRefillTime = System.nanoTime();
    }
    
    /**
     * 根据时间间隔补充令牌
     */
    private void refillTokens() {
        long now = System.nanoTime();
        long elapsed = now - lastRefillTime;
        
        if (elapsed > 0) {
            // 计算应该添加的令牌数量
            long tokensToAdd = (long) (elapsed * rate / 1_000_000_000.0);
            
            if (tokensToAdd > 0) {
                long currentTokens = tokens.get();
                long newTokens = Math.min(capacity, currentTokens + tokensToAdd);
                
                if (tokens.compareAndSet(currentTokens, newTokens)) {
                    lastRefillTime = now;
                }
            }
        }
    }
    
    /**
     * 获取当前可用令牌数量
     * 
     * @return current available tokens
     */
    public long getAvailableTokens() {
        refillTokens();
        return tokens.get();
    }
    
    /**
     * 获取桶容量
     * 
     * @return bucket capacity
     */
    public long getCapacity() {
        return capacity;
    }
}