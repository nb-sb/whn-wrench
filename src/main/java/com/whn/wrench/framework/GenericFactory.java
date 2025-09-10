package com.whn.wrench.framework;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 通用工厂实现
 * Generic factory implementation supporting various design patterns
 */
public class GenericFactory<T> implements DesignPatternFactory<T> {
    
    private final Map<String, Creator<T>> creators = new ConcurrentHashMap<>();
    
    @Override
    public T create(String type) {
        Creator<T> creator = creators.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
        return creator.create();
    }
    
    @Override
    public void register(String type, Creator<T> creator) {
        if (type == null || creator == null) {
            throw new IllegalArgumentException("Type and creator cannot be null");
        }
        creators.put(type, creator);
    }
    
    @Override
    public String[] getSupportedTypes() {
        return creators.keySet().toArray(new String[0]);
    }
    
    /**
     * 检查是否支持指定类型
     * Check if type is supported
     * 
     * @param type type to check
     * @return true if supported
     */
    public boolean isSupported(String type) {
        return creators.containsKey(type);
    }
    
    /**
     * 移除类型注册
     * Unregister type
     * 
     * @param type type to unregister
     * @return true if type was registered and removed
     */
    public boolean unregister(String type) {
        return creators.remove(type) != null;
    }
    
    /**
     * 清空所有注册
     * Clear all registrations
     */
    public void clear() {
        creators.clear();
    }
    
    /**
     * 获取已注册类型数量
     * Get number of registered types
     * 
     * @return number of registered types
     */
    public int size() {
        return creators.size();
    }
}