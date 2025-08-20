package cn.web3er.wrench.rate.limiter.types;

import org.springframework.context.NoSuchMessageException;

import java.util.Arrays;

/**
 * 获取i18n资源文件
 *
 * @author Lion Li
 */
public class MessageUtils {


    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String message(String code, Object... args) {
        try {
            return code + Arrays.toString(args);
        } catch (NoSuchMessageException e) {
            return code;
        }
    }
}
