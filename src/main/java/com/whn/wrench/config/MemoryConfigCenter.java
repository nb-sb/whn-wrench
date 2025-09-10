package com.whn.wrench.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Map;

/**
 * 内存动态配置中心实现
 * In-memory dynamic configuration center implementation
 */
public class MemoryConfigCenter implements ConfigCenter {
    
    private final Map<String, Object> configs = new ConcurrentHashMap<>();
    private final List<ConfigChangeListener> listeners = new CopyOnWriteArrayList<>();
    
    @Override
    public String getString(String key, String defaultValue) {
        Object value = configs.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    @Override
    public int getInt(String key, int defaultValue) {
        Object value = configs.get(key);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    @Override
    public long getLong(String key, long defaultValue) {
        Object value = configs.get(key);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = configs.get(key);
        if (value == null) {
            return defaultValue;
        }
        
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        
        String stringValue = value.toString().toLowerCase();
        return "true".equals(stringValue) || "1".equals(stringValue) || "yes".equals(stringValue);
    }
    
    @Override
    public double getDouble(String key, double defaultValue) {
        Object value = configs.get(key);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    @Override
    public void setConfig(String key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        Object oldValue = configs.put(key, value);
        
        // 通知监听器
        if (!listeners.isEmpty()) {
            for (ConfigChangeListener listener : listeners) {
                try {
                    listener.onConfigChange(key, oldValue, value);
                } catch (Exception e) {
                    // 记录异常但不影响其他监听器
                    System.err.println("Config change listener failed: " + e.getMessage());
                }
            }
        }
    }
    
    @Override
    public boolean removeConfig(String key) {
        Object oldValue = configs.remove(key);
        boolean removed = oldValue != null;
        
        if (removed && !listeners.isEmpty()) {
            for (ConfigChangeListener listener : listeners) {
                try {
                    listener.onConfigChange(key, oldValue, null);
                } catch (Exception e) {
                    System.err.println("Config change listener failed: " + e.getMessage());
                }
            }
        }
        
        return removed;
    }
    
    @Override
    public boolean containsKey(String key) {
        return configs.containsKey(key);
    }
    
    @Override
    public void addListener(ConfigChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    @Override
    public boolean removeListener(ConfigChangeListener listener) {
        return listeners.remove(listener);
    }
    
    @Override
    public String[] getKeys() {
        return configs.keySet().toArray(new String[0]);
    }
    
    @Override
    public void clear() {
        Map<String, Object> oldConfigs = new ConcurrentHashMap<>(configs);
        configs.clear();
        
        // 通知监听器所有配置被清空
        if (!listeners.isEmpty()) {
            for (Map.Entry<String, Object> entry : oldConfigs.entrySet()) {
                for (ConfigChangeListener listener : listeners) {
                    try {
                        listener.onConfigChange(entry.getKey(), entry.getValue(), null);
                    } catch (Exception e) {
                        System.err.println("Config change listener failed: " + e.getMessage());
                    }
                }
            }
        }
    }
    
    /**
     * 获取配置数量
     * Get number of configurations
     * 
     * @return number of configurations
     */
    public int size() {
        return configs.size();
    }
    
    /**
     * 获取监听器数量
     * Get number of listeners
     * 
     * @return number of listeners
     */
    public int getListenerCount() {
        return listeners.size();
    }
}