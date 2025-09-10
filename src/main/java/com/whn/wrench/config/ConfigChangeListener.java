package com.whn.wrench.config;

/**
 * 配置变更监听器
 * Configuration change listener
 */
public interface ConfigChangeListener {
    
    /**
     * 配置变更时调用
     * Called when configuration changes
     * 
     * @param key configuration key
     * @param oldValue old value
     * @param newValue new value
     */
    void onConfigChange(String key, Object oldValue, Object newValue);
}