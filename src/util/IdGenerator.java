package util;

import java.util.UUID;

/**
 * ID生成工具
 */
public class IdGenerator {
    public static String generateProductId() {
        return "P-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateOrderId() {
        return "O-" + System.currentTimeMillis();
    }

    public static String generateCustomerId() {
        return "C-" + UUID.randomUUID().toString().substring(0, 6);
    }
}