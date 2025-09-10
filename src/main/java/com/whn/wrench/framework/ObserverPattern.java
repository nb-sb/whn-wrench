package com.whn.wrench.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 观察者模式实现
 * Observer pattern implementation
 */
public class ObserverPattern<T> {
    
    private final List<Observer<T>> observers = new CopyOnWriteArrayList<>();
    
    /**
     * 观察者接口
     */
    public interface Observer<T> {
        /**
         * 处理通知
         * Handle notification
         * 
         * @param data notification data
         */
        void onNotify(T data);
    }
    
    /**
     * 添加观察者
     * Add observer
     * 
     * @param observer observer to add
     */
    public void addObserver(Observer<T> observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * 移除观察者
     * Remove observer
     * 
     * @param observer observer to remove
     * @return true if observer was removed
     */
    public boolean removeObserver(Observer<T> observer) {
        return observers.remove(observer);
    }
    
    /**
     * 通知所有观察者
     * Notify all observers
     * 
     * @param data data to send to observers
     */
    public void notifyObservers(T data) {
        for (Observer<T> observer : observers) {
            try {
                observer.onNotify(data);
            } catch (Exception e) {
                // 记录异常但不影响其他观察者
                System.err.println("Observer notification failed: " + e.getMessage());
            }
        }
    }
    
    /**
     * 获取观察者数量
     * Get number of observers
     * 
     * @return number of observers
     */
    public int getObserverCount() {
        return observers.size();
    }
    
    /**
     * 清空所有观察者
     * Clear all observers
     */
    public void clearObservers() {
        observers.clear();
    }
    
    /**
     * 获取所有观察者的副本
     * Get copy of all observers
     * 
     * @return list of observers
     */
    public List<Observer<T>> getObservers() {
        return new ArrayList<>(observers);
    }
}