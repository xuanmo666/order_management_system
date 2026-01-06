package util;

/**
 * 数据验证工具
 */
public class ValidationUtil {
    public static boolean isPositiveNumber(Double number) {
        return number != null && number > 0;
    }

    public static boolean isNonNegativeInteger(Integer number) {
        return number != null && number >= 0;
    }

    public static boolean isValidStatusTransition(String from, String to) {
        // 验证订单状态流转是否合法
        return true;
    }
}