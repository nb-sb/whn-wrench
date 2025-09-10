package com.whn.wrench.config;

/**
 * 动态配置中心接口
 * Dynamic configuration center interface
 */
public interface ConfigCenter {
    
    /**
     * 获取字符串配置值
     * Get string configuration value
     * 
     * @param key configuration key
     * @param defaultValue default value if key not found
     * @return configuration value
     */
    String getString(String key, String defaultValue);
    
    /**
     * 获取整数配置值
     * Get integer configuration value
     * 
     * @param key configuration key
     * @param defaultValue default value if key not found
     * @return configuration value
     */
    int getInt(String key, int defaultValue);
    
    /**
     * 获取长整数配置值
     * Get long configuration value
     * 
     * @param key configuration key
     * @param defaultValue default value if key not found
     * @return configuration value
     */
    long getLong(String key, long defaultValue);
    
    /**
     * 获取布尔配置值
     * Get boolean configuration value
     * 
     * @param key configuration key
     * @param defaultValue default value if key not found
     * @return configuration value
     */
    boolean getBoolean(String key, boolean defaultValue);
    
    /**
     * 获取双精度配置值
     * Get double configuration value
     * 
     * @param key configuration key
     * @param defaultValue default value if key not found
     * @return configuration value
     */
    double getDouble(String key, double defaultValue);
    
    /**
     * 设置配置值
     * Set configuration value
     * 
     * @param key configuration key
     * @param value configuration value
     */
    void setConfig(String key, Object value);
    
    /**
     * 移除配置
     * Remove configuration
     * 
     * @param key configuration key
     * @return true if key existed and was removed
     */
    boolean removeConfig(String key);
    
    /**
     * 检查配置键是否存在
     * Check if configuration key exists
     * 
     * @param key configuration key
     * @return true if key exists
     */
    boolean containsKey(String key);
    
    /**
     * 添加配置变更监听器
     * Add configuration change listener
     * 
     * @param listener configuration change listener
     */
    void addListener(ConfigChangeListener listener);
    
    /**
     * 移除配置变更监听器
     * Remove configuration change listener
     * 
     * @param listener configuration change listener
     * @return true if listener was removed
     */
    boolean removeListener(ConfigChangeListener listener);
    
    /**
     * 获取所有配置键
     * Get all configuration keys
     * 
     * @return array of configuration keys
     */
    String[] getKeys();
    
    /**
     * 清空所有配置
     * Clear all configurations
     */
    void clear();
}