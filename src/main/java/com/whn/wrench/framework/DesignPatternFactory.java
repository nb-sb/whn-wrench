package com.whn.wrench.framework;

/**
 * 设计模式工厂接口
 * Design pattern factory interface
 */
public interface DesignPatternFactory<T> {
    
    /**
     * 创建实例
     * Create instance
     * 
     * @param type instance type
     * @return created instance
     */
    T create(String type);
    
    /**
     * 注册实例创建器
     * Register instance creator
     * 
     * @param type instance type
     * @param creator instance creator
     */
    void register(String type, Creator<T> creator);
    
    /**
     * 获取支持的类型列表
     * Get supported types
     * 
     * @return array of supported types
     */
    String[] getSupportedTypes();
    
    /**
     * 实例创建器接口
     */
    interface Creator<T> {
        T create();
    }
}