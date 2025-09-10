package com.whn.wrench.ratelimit;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * TokenBucketRateLimiter 单元测试
 */
public class TokenBucketRateLimiterTest {
    
    private TokenBucketRateLimiter rateLimiter;
    
    @Before
    public void setUp() {
        rateLimiter = new TokenBucketRateLimiter(10.0, 10); // 10 tokens per second, capacity 10
    }
    
    @Test
    public void testConstructor() {
        assertEquals(10.0, rateLimiter.getRate(), 0.01);
        assertEquals(10, rateLimiter.getCapacity());
        assertEquals(10, rateLimiter.getAvailableTokens()); // 初始时桶是满的
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNegativeRate() {
        new TokenBucketRateLimiter(-1.0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithZeroCapacity() {
        new TokenBucketRateLimiter(10.0, 0);
    }
    
    @Test
    public void testTryAcquireSingleToken() {
        assertTrue("Should acquire token when bucket is full", rateLimiter.tryAcquire());
        assertEquals(9, rateLimiter.getAvailableTokens());
    }
    
    @Test
    public void testTryAcquireMultipleTokens() {
        assertTrue("Should acquire 5 tokens", rateLimiter.tryAcquire(5));
        assertEquals(5, rateLimiter.getAvailableTokens());
        
        assertTrue("Should acquire remaining 5 tokens", rateLimiter.tryAcquire(5));
        assertEquals(0, rateLimiter.getAvailableTokens());
        
        assertFalse("Should not acquire when no tokens available", rateLimiter.tryAcquire());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testTryAcquireWithNegativePermits() {
        rateLimiter.tryAcquire(-1);
    }
    
    @Test
    public void testTryAcquireWithTimeout() {
        // 先用完所有令牌
        rateLimiter.tryAcquire(10);
        assertEquals(0, rateLimiter.getAvailableTokens());
        
        // 尝试在很短时间内获取令牌，应该失败
        long startTime = System.currentTimeMillis();
        boolean acquired = rateLimiter.tryAcquire(10);
        long elapsed = System.currentTimeMillis() - startTime;
        
        assertFalse("Should not acquire token within short timeout", acquired);
        assertTrue("Should not wait much longer than timeout", elapsed <= 50);
    }
    
    @Test
    public void testReset() {
        // 用掉一些令牌
        rateLimiter.tryAcquire(5);
        assertEquals(5, rateLimiter.getAvailableTokens());
        
        // 重置后应该恢复满桶
        rateLimiter.reset();
        assertEquals(10, rateLimiter.getAvailableTokens());
    }
    
    @Test
    public void testTokenRefill() throws InterruptedException {
        // 用完所有令牌
        rateLimiter.tryAcquire(10);
        assertEquals(0, rateLimiter.getAvailableTokens());
        
        // 等待一段时间让令牌补充
        Thread.sleep(200); // 等待200ms，理论上应该补充约2个令牌
        
        long availableTokens = rateLimiter.getAvailableTokens();
        assertTrue("Should have some tokens after waiting", availableTokens > 0);
        assertTrue("Should not exceed capacity", availableTokens <= 10);
    }
    
    @Test
    public void testConvenienceConstructor() {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(5.0);
        assertEquals(5.0, limiter.getRate(), 0.01);
        assertEquals(5, limiter.getCapacity());
    }
}