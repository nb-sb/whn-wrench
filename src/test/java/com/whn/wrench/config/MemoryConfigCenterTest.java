package com.whn.wrench.config;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * MemoryConfigCenter 单元测试
 */
public class MemoryConfigCenterTest {
    
    private MemoryConfigCenter configCenter;
    private List<String> changeNotifications;
    
    @Before
    public void setUp() {
        configCenter = new MemoryConfigCenter();
        changeNotifications = new ArrayList<>();
    }
    
    @Test
    public void testStringConfig() {
        configCenter.setConfig("test.string", "hello");
        assertEquals("hello", configCenter.getString("test.string", "default"));
        assertEquals("default", configCenter.getString("nonexistent", "default"));
    }
    
    @Test
    public void testIntConfig() {
        configCenter.setConfig("test.int", 42);
        assertEquals(42, configCenter.getInt("test.int", 0));
        
        configCenter.setConfig("test.int.string", "123");
        assertEquals(123, configCenter.getInt("test.int.string", 0));
        
        configCenter.setConfig("test.int.invalid", "not-a-number");
        assertEquals(999, configCenter.getInt("test.int.invalid", 999));
        
        assertEquals(0, configCenter.getInt("nonexistent", 0));
    }
    
    @Test
    public void testLongConfig() {
        configCenter.setConfig("test.long", 1234567890L);
        assertEquals(1234567890L, configCenter.getLong("test.long", 0L));
        
        configCenter.setConfig("test.long.string", "9876543210");
        assertEquals(9876543210L, configCenter.getLong("test.long.string", 0L));
        
        assertEquals(0L, configCenter.getLong("nonexistent", 0L));
    }
    
    @Test
    public void testBooleanConfig() {
        configCenter.setConfig("test.bool.true", true);
        assertTrue(configCenter.getBoolean("test.bool.true", false));
        
        configCenter.setConfig("test.bool.string.true", "true");
        assertTrue(configCenter.getBoolean("test.bool.string.true", false));
        
        configCenter.setConfig("test.bool.string.1", "1");
        assertTrue(configCenter.getBoolean("test.bool.string.1", false));
        
        configCenter.setConfig("test.bool.string.yes", "yes");
        assertTrue(configCenter.getBoolean("test.bool.string.yes", false));
        
        configCenter.setConfig("test.bool.false", "false");
        assertFalse(configCenter.getBoolean("test.bool.false", true));
        
        assertFalse(configCenter.getBoolean("nonexistent", false));
    }
    
    @Test
    public void testDoubleConfig() {
        configCenter.setConfig("test.double", 3.14);
        assertEquals(3.14, configCenter.getDouble("test.double", 0.0), 0.001);
        
        configCenter.setConfig("test.double.string", "2.718");
        assertEquals(2.718, configCenter.getDouble("test.double.string", 0.0), 0.001);
        
        assertEquals(0.0, configCenter.getDouble("nonexistent", 0.0), 0.001);
    }
    
    @Test
    public void testContainsKey() {
        assertFalse(configCenter.containsKey("test.key"));
        
        configCenter.setConfig("test.key", "value");
        assertTrue(configCenter.containsKey("test.key"));
    }
    
    @Test
    public void testRemoveConfig() {
        configCenter.setConfig("test.remove", "value");
        assertTrue(configCenter.containsKey("test.remove"));
        
        boolean removed = configCenter.removeConfig("test.remove");
        assertTrue(removed);
        assertFalse(configCenter.containsKey("test.remove"));
        
        boolean removedAgain = configCenter.removeConfig("test.remove");
        assertFalse(removedAgain);
    }
    
    @Test
    public void testGetKeys() {
        assertEquals(0, configCenter.getKeys().length);
        
        configCenter.setConfig("key1", "value1");
        configCenter.setConfig("key2", "value2");
        
        String[] keys = configCenter.getKeys();
        assertEquals(2, keys.length);
        
        List<String> keyList = java.util.Arrays.asList(keys);
        assertTrue(keyList.contains("key1"));
        assertTrue(keyList.contains("key2"));
    }
    
    @Test
    public void testClear() {
        configCenter.setConfig("key1", "value1");
        configCenter.setConfig("key2", "value2");
        assertEquals(2, configCenter.size());
        
        configCenter.clear();
        assertEquals(0, configCenter.size());
        assertFalse(configCenter.containsKey("key1"));
        assertFalse(configCenter.containsKey("key2"));
    }
    
    @Test
    public void testConfigChangeListener() {
        ConfigChangeListener listener = (key, oldValue, newValue) -> {
            changeNotifications.add(key + ":" + oldValue + "->" + newValue);
        };
        
        configCenter.addListener(listener);
        assertEquals(1, configCenter.getListenerCount());
        
        configCenter.setConfig("test.key", "value1");
        assertEquals(1, changeNotifications.size());
        assertEquals("test.key:null->value1", changeNotifications.get(0));
        
        configCenter.setConfig("test.key", "value2");
        assertEquals(2, changeNotifications.size());
        assertEquals("test.key:value1->value2", changeNotifications.get(1));
        
        configCenter.removeConfig("test.key");
        assertEquals(3, changeNotifications.size());
        assertEquals("test.key:value2->null", changeNotifications.get(2));
    }
    
    @Test
    public void testRemoveListener() {
        ConfigChangeListener listener = (key, oldValue, newValue) -> {
            changeNotifications.add("change");
        };
        
        configCenter.addListener(listener);
        assertEquals(1, configCenter.getListenerCount());
        
        boolean removed = configCenter.removeListener(listener);
        assertTrue(removed);
        assertEquals(0, configCenter.getListenerCount());
        
        configCenter.setConfig("test", "value");
        assertEquals(0, changeNotifications.size());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetConfigWithNullKey() {
        configCenter.setConfig(null, "value");
    }
}