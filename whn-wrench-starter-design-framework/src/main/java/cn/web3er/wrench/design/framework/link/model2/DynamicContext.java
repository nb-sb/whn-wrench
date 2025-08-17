package cn.web3er.wrench.design.framework.link.model2;

import java.util.HashMap;
import java.util.Map;

/**
* @author: Wanghaonan @戏人看戏
* @description: 链路动态上下文
* @create: 2025/8/17 15:17
*/
public class DynamicContext {

    private boolean proceed;

    public DynamicContext() {
        this.proceed = true;
    }

    private Map<String, Object> dataObjects = new HashMap<>();

    public <T> void setValue(String key, T value) {
        dataObjects.put(key, value);
    }

    public <T> T getValue(String key) {
        return (T) dataObjects.get(key);
    }

    public boolean isProceed() {
        return proceed;
    }

    public void setProceed(boolean proceed) {
        this.proceed = proceed;
    }
}
