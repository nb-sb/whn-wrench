package cn.web3er.wrench.dynamic.config.center.listener;

import cn.web3er.wrench.dynamic.config.center.domain.service.IDynamicConfigCenterService;
import cn.web3er.wrench.dynamic.config.center.domain.valobj.AttributeVO;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicConfigCenterAdjustListener implements MessageListener<AttributeVO> {

    private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterAdjustListener.class);

    private final IDynamicConfigCenterService dynamicConfigCenterService;

    public DynamicConfigCenterAdjustListener(IDynamicConfigCenterService dynamicConfigCenterService) {
        this.dynamicConfigCenterService = dynamicConfigCenterService;
    }

    @Override
    public void onMessage(CharSequence charSequence, AttributeVO attributeVO) {
        try {
            log.info("whn-wrench dcc config attribute:{} value:{}", attributeVO.getAttribute(), attributeVO.getValue());
            dynamicConfigCenterService.adjustAttributeValue(attributeVO);
        } catch (Exception e) {
            log.error("whn-wrench dcc config attribute:{} value:{}", attributeVO.getAttribute(), attributeVO.getValue(), e);
        }
    }

}
