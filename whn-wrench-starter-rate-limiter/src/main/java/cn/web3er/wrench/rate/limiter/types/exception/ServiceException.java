package cn.web3er.wrench.rate.limiter.types.exception;

/**
 * 业务异常
 *
 * @author ruoyi
 */
public final class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细，内部调试错误
     */
    private String detailMessage;

    /**
     * 默认构造函数
     */
    public ServiceException() {
    }

    /**
     * 带所有参数的构造函数
     */
    public ServiceException(Integer code, String message, String detailMessage) {
        this.code = code;
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public ServiceException(String message) {
        this.message = message;
    }

    public ServiceException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    /**
     * 获取错误码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置错误码
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取错误提示
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 设置错误提示
     */
    public ServiceException setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 获取错误明细
     */
    public String getDetailMessage() {
        return detailMessage;
    }

    /**
     * 设置错误明细
     */
    public ServiceException setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }

    /**
     * equals方法
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }

        ServiceException that = (ServiceException) obj;

        if (code != null ? !code.equals(that.code) : that.code != null) {
            return false;
        }
        if (message != null ? !message.equals(that.message) : that.message != null) {
            return false;
        }
        return detailMessage != null ? detailMessage.equals(that.detailMessage) : that.detailMessage == null;
    }

    /**
     * hashCode方法
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (detailMessage != null ? detailMessage.hashCode() : 0);
        return result;
    }

    /**
     * toString方法
     */
    @Override
    public String toString() {
        return "ServiceException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", detailMessage='" + detailMessage + '\'' +
                '}';
    }
}
