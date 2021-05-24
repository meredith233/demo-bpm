package top.mrexgo.demobpm.common.exception;

import lombok.Getter;
import org.slf4j.helpers.MessageFormatter;

/**
 * @author liangjuhong
 * @since 2021/5/24
 **/
public class ServiceException extends RuntimeException {

    @Getter
    private final Integer resultCode = 10000;

    private String exceptionClass;

    private String exceptionMessage;

    public ServiceException() {
    }

    public ServiceException(String format, Object... arguments) {
        this("cn.resico.common.exception.ServiceException", MessageFormatter.arrayFormat(format, arguments).getMessage());
    }

    public ServiceException(String message) {
        this("cn.resico.common.exception.ServiceException", message);
    }

    public ServiceException(String exceptionClass, String exceptionMessage) {
        super(exceptionMessage);
        this.exceptionClass = exceptionClass;
        this.exceptionMessage = exceptionMessage;
    }

    public ServiceException(Throwable cause) {
        super(cause.toString());
        this.exceptionClass = cause.getClass().getName();
        this.exceptionMessage = cause.getMessage();
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
