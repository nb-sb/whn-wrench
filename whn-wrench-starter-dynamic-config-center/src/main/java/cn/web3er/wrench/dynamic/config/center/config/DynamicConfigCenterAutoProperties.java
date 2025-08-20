package cn.web3er.wrench.dynamic.config.center.config;


import cn.web3er.wrench.dynamic.config.center.types.common.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "whn.wrench.config", ignoreInvalidFields = true)
public class DynamicConfigCenterAutoProperties {

    /**
     * 系统名称
     */
    private String system = "whn-wrench-ddc";

    public String getKey(String attributeName) {
        return this.system + Constants.LINE + attributeName;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}

