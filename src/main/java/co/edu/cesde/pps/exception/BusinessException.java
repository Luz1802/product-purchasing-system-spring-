package co.edu.cesde.pps.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class BusinessException extends RuntimeException {
    private static final Logger log = LoggerFactory.getLogger(BusinessException.class);
    public BusinessException(String message) {
        super(message);
        log.error("BusinessException thrown: {}", message);
    }
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        log.error("BusinessException thrown: {} - Cause: {}", message, cause.getMessage(), cause);
    }
    public BusinessException(Throwable cause) {
        super(cause);
        log.error("BusinessException thrown with cause: {}", cause.getMessage(), cause);
    }
}