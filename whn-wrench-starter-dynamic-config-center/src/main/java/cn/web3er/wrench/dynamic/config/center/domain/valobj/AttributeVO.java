package cn.web3er.wrench.dynamic.config.center.domain.valobj;

/**
* @author: Wanghaonan @戏人看戏
* @description: 属性值调整值对象
* @create: 2025/8/11 11:27
*/
public class AttributeVO {

    /** 键 - 属性 fileName */
    private String attribute;

    /** 值 */
    private String value;

    public AttributeVO() {
    }

    public AttributeVO(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
