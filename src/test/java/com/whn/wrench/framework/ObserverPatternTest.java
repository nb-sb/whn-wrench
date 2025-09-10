package com.whn.wrench.framework;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ObserverPattern 单元测试
 */
public class ObserverPatternTest {
    
    private ObserverPattern<String> observerPattern;
    private List<String> notifications;
    
    @Before
    public void setUp() {
        observerPattern = new ObserverPattern<>();
        notifications = new ArrayList<>();
    }
    
    @Test
    public void testAddObserver() {
        ObserverPattern.Observer<String> observer = data -> notifications.add(data);
        
        assertEquals(0, observerPattern.getObserverCount());
        observerPattern.addObserver(observer);
        assertEquals(1, observerPattern.getObserverCount());
        
        // 添加同一个观察者不应该增加计数
        observerPattern.addObserver(observer);
        assertEquals(1, observerPattern.getObserverCount());
    }
    
    @Test
    public void testAddNullObserver() {
        observerPattern.addObserver(null);
        assertEquals(0, observerPattern.getObserverCount());
    }
    
    @Test
    public void testRemoveObserver() {
        ObserverPattern.Observer<String> observer = data -> notifications.add(data);
        
        observerPattern.addObserver(observer);
        assertEquals(1, observerPattern.getObserverCount());
        
        boolean removed = observerPattern.removeObserver(observer);
        assertTrue(removed);
        assertEquals(0, observerPattern.getObserverCount());
        
        // 移除不存在的观察者应该返回false
        boolean removedAgain = observerPattern.removeObserver(observer);
        assertFalse(removedAgain);
    }
    
    @Test
    public void testNotifyObservers() {
        ObserverPattern.Observer<String> observer1 = data -> notifications.add("Observer1: " + data);
        ObserverPattern.Observer<String> observer2 = data -> notifications.add("Observer2: " + data);
        
        observerPattern.addObserver(observer1);
        observerPattern.addObserver(observer2);
        
        observerPattern.notifyObservers("test message");
        
        assertEquals(2, notifications.size());
        assertTrue(notifications.contains("Observer1: test message"));
        assertTrue(notifications.contains("Observer2: test message"));
    }
    
    @Test
    public void testNotifyWithException() {
        ObserverPattern.Observer<String> goodObserver = data -> notifications.add("Good: " + data);
        ObserverPattern.Observer<String> badObserver = data -> {
            throw new RuntimeException("Observer failed");
        };
        ObserverPattern.Observer<String> anotherGoodObserver = data -> notifications.add("Another: " + data);
        
        observerPattern.addObserver(goodObserver);
        observerPattern.addObserver(badObserver);
        observerPattern.addObserver(anotherGoodObserver);
        
        // 异常不应该影响其他观察者的通知
        observerPattern.notifyObservers("test");
        
        assertEquals(2, notifications.size());
        assertTrue(notifications.contains("Good: test"));
        assertTrue(notifications.contains("Another: test"));
    }
    
    @Test
    public void testClearObservers() {
        ObserverPattern.Observer<String> observer1 = data -> notifications.add(data);
        ObserverPattern.Observer<String> observer2 = data -> notifications.add(data);
        
        observerPattern.addObserver(observer1);
        observerPattern.addObserver(observer2);
        assertEquals(2, observerPattern.getObserverCount());
        
        observerPattern.clearObservers();
        assertEquals(0, observerPattern.getObserverCount());
        
        observerPattern.notifyObservers("test");
        assertEquals(0, notifications.size());
    }
    
    @Test
    public void testGetObservers() {
        ObserverPattern.Observer<String> observer1 = data -> notifications.add("1: " + data);
        ObserverPattern.Observer<String> observer2 = data -> notifications.add("2: " + data);
        
        observerPattern.addObserver(observer1);
        observerPattern.addObserver(observer2);
        
        List<ObserverPattern.Observer<String>> observers = observerPattern.getObservers();
        assertEquals(2, observers.size());
        assertTrue(observers.contains(observer1));
        assertTrue(observers.contains(observer2));
        
        // 返回的应该是副本，修改不应该影响原始列表
        observers.clear();
        assertEquals(2, observerPattern.getObserverCount());
    }
}