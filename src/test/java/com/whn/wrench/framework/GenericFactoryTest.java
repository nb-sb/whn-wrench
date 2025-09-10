package com.whn.wrench.framework;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * GenericFactory 单元测试
 */
public class GenericFactoryTest {
    
    private GenericFactory<String> factory;
    
    @Before
    public void setUp() {
        factory = new GenericFactory<>();
    }
    
    @Test
    public void testRegisterAndCreate() {
        factory.register("test", () -> "Hello World");
        
        String result = factory.create("test");
        assertEquals("Hello World", result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUnsupportedType() {
        factory.create("nonexistent");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterWithNullType() {
        factory.register(null, () -> "test");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterWithNullCreator() {
        factory.register("test", null);
    }
    
    @Test
    public void testGetSupportedTypes() {
        assertTrue("Should have no types initially", factory.getSupportedTypes().length == 0);
        
        factory.register("type1", () -> "value1");
        factory.register("type2", () -> "value2");
        
        String[] types = factory.getSupportedTypes();
        assertEquals(2, types.length);
        assertTrue(factory.isSupported("type1"));
        assertTrue(factory.isSupported("type2"));
    }
    
    @Test
    public void testIsSupported() {
        assertFalse(factory.isSupported("test"));
        
        factory.register("test", () -> "value");
        assertTrue(factory.isSupported("test"));
    }
    
    @Test
    public void testUnregister() {
        factory.register("test", () -> "value");
        assertTrue(factory.isSupported("test"));
        
        boolean removed = factory.unregister("test");
        assertTrue(removed);
        assertFalse(factory.isSupported("test"));
        
        boolean removedAgain = factory.unregister("test");
        assertFalse(removedAgain);
    }
    
    @Test
    public void testClear() {
        factory.register("type1", () -> "value1");
        factory.register("type2", () -> "value2");
        assertEquals(2, factory.size());
        
        factory.clear();
        assertEquals(0, factory.size());
        assertFalse(factory.isSupported("type1"));
        assertFalse(factory.isSupported("type2"));
    }
    
    @Test
    public void testSize() {
        assertEquals(0, factory.size());
        
        factory.register("type1", () -> "value1");
        assertEquals(1, factory.size());
        
        factory.register("type2", () -> "value2");
        assertEquals(2, factory.size());
        
        factory.unregister("type1");
        assertEquals(1, factory.size());
    }
    
    @Test
    public void testMultipleCreations() {
        factory.register("counter", new DesignPatternFactory.Creator<String>() {
            private int count = 0;
            
            @Override
            public String create() {
                return "Instance " + (++count);
            }
        });
        
        assertEquals("Instance 1", factory.create("counter"));
        assertEquals("Instance 2", factory.create("counter"));
        assertEquals("Instance 3", factory.create("counter"));
    }
}