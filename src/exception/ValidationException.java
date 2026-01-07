package exception;

/**
 * 数据验证异常
 */
public class ValidationException extends BusinessException {
    public ValidationException(String message) {
        super(message);
    }
}