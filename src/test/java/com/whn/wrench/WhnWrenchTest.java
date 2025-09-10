package com.whn.wrench;

import com.whn.wrench.config.ConfigCenter;
import com.whn.wrench.ratelimit.RateLimiter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * WhnWrench 主类单元测试
 */
public class WhnWrenchTest {
    
    @Test
    public void testGetConfigCenter() {
        ConfigCenter configCenter = WhnWrench.getConfigCenter();
        assertNotNull(configCenter);
        
        // 测试配置中心功能
        configCenter.setConfig("test.key", "test.value");
        assertEquals("test.value", configCenter.getString("test.key", "default"));
    }
    
    @Test
    public void testCreateRateLimiterByType() {
        RateLimiter rateLimiter = WhnWrench.createRateLimiter("token-bucket");
        assertNotNull(rateLimiter);
        assertEquals(100.0, rateLimiter.getRate(), 0.01);
    }
    
    @Test
    public void testCreateCustomRateLimiterWithConfiguredSettings() {
        ConfigCenter configCenter = WhnWrench.getConfigCenter();
        configCenter.setConfig("ratelimiter.rate", 50.0);
        configCenter.setConfig("ratelimiter.capacity", 100L);
        
        RateLimiter rateLimiter = WhnWrench.createRateLimiter("token-bucket-custom");
        assertNotNull(rateLimiter);
        assertEquals(50.0, rateLimiter.getRate(), 0.01);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateRateLimiterWithUnsupportedType() {
        WhnWrench.createRateLimiter("unsupported-type");
    }
    
    @Test
    public void testCreateRateLimiterWithRate() {
        RateLimiter rateLimiter = WhnWrench.createRateLimiter(50.0);
        assertNotNull(rateLimiter);
        assertEquals(50.0, rateLimiter.getRate(), 0.01);
    }
    
    @Test
    public void testCreateRateLimiterWithRateAndCapacity() {
        RateLimiter rateLimiter = WhnWrench.createRateLimiter(25.0, 50);
        assertNotNull(rateLimiter);
        assertEquals(25.0, rateLimiter.getRate(), 0.01);
    }
    
    @Test
    public void testGetRateLimiterFactory() {
        assertNotNull(WhnWrench.getRateLimiterFactory());
        assertTrue(WhnWrench.getRateLimiterFactory().isSupported("token-bucket"));
        assertTrue(WhnWrench.getRateLimiterFactory().isSupported("token-bucket-custom"));
    }
    
    @Test
    public void testGetVersion() {
        String version = WhnWrench.getVersion();
        assertNotNull(version);
        assertEquals("1.0.0", version);
    }
    
    @Test
    public void testGetDescription() {
        String description = WhnWrench.getDescription();
        assertNotNull(description);
        assertTrue(description.contains("WHN Wrench"));
        assertTrue(description.contains("Java工具集"));
    }
}