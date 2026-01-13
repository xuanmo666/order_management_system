package util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * 数据验证工具类
 * 提供各种业务数据验证方法
 */
public class ValidationUtil {

    // 正则表达式预编译
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^1[3-9]\\d{9}$"); // 中国手机号
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^[a-zA-Z0-9@#$%^&+=]{6,20}$");

    /**
     * 验证字符串不为空且不为空白
     */
    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * 验证字符串长度在范围内
     */
    public static boolean isValidLength(String str, int min, int max) {
        if (str == null) return false;
        int length = str.trim().length();
        return length >= min && length <= max;
    }

    /**
     * 验证数字为正数
     */
    public static boolean isPositiveNumber(Number number) {
        if (number == null) return false;
        if (number instanceof Integer) {
            return (Integer)number > 0;
        } else if (number instanceof Double) {
            return (Double)number > 0;
        } else if (number instanceof BigDecimal) {
            return ((BigDecimal)number).compareTo(BigDecimal.ZERO) > 0;
        }
        return false;
    }

    /**
     * 验证数字为非负数
     */
    public static boolean isNonNegativeNumber(Number number) {
        if (number == null) return false;
        if (number instanceof Integer) {
            return (Integer)number >= 0;
        } else if (number instanceof Double) {
            return (Double)number >= 0;
        }
        return false;
    }

    /**
     * 验证库存数量在阈值范围内
     */
    public static boolean isValidInventory(int current, int min, int max) {
        return current >= 0 && min >= 0 && max > 0 && current <= max;
    }

    /**
     * 验证价格格式（两位小数）
     */
    public static boolean isValidPrice(double price) {
        if (price <= 0) return false;
        // 检查是否最多两位小数
        String priceStr = String.valueOf(price);
        int dotIndex = priceStr.indexOf('.');
        if (dotIndex != -1 && priceStr.substring(dotIndex + 1).length() > 2) {
            return false;
        }
        return true;
    }

    /**
     * 验证订单金额（精确到分）
     */
    public static BigDecimal formatMoney(double amount) {
        return BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        if (!isNotBlank(email)) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证手机号格式
     */
    public static boolean isValidPhone(String phone) {
        if (!isNotBlank(phone)) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证用户名格式
     */
    public static boolean isValidUsername(String username) {
        if (!isNotBlank(username)) return false;
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * 验证密码格式
     */
    public static boolean isValidPassword(String password) {
        if (!isNotBlank(password)) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 验证日期格式
     */
    public static boolean isValidDate(String dateStr, String format) {
        if (!isNotBlank(dateStr)) return false;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * 验证订单状态流转是否合法
     * 状态顺序：待付款 → 待发货 → 已发货 → 已完成
     *               ↘ 已取消
     */
    public static boolean isValidStatusTransition(String fromStatus, String toStatus) {
        if (fromStatus == null || toStatus == null) return false;

        // 状态流转规则
        switch (fromStatus) {
            case "待付款":
                return "待发货".equals(toStatus) || "已取消".equals(toStatus);
            case "待发货":
                return "已发货".equals(toStatus) || "已取消".equals(toStatus);
            case "已发货":
                return "已完成".equals(toStatus);
            case "已完成":
            case "已取消":
                return false; // 终态不可再改变
            default:
                return false;
        }
    }

    /**
     * 验证用户角色是否合法
     */
    public static boolean isValidUserRole(String role) {
        if (!isNotBlank(role)) return false;
        return "admin".equals(role) || "sales".equals(role);
    }

    /**
     * 验证会员等级是否合法
     */
    public static boolean isValidMemberLevel(String level) {
        if (!isNotBlank(level)) return false;
        return "普通".equals(level) || "银卡".equals(level) || "金卡".equals(level) || "铂金".equals(level);
    }
}