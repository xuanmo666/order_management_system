package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID生成工具类
 * 为不同实体生成唯一ID
 */
public class IdGenerator {

    // 使用原子计数器保证线程安全
    private static final AtomicInteger productCounter = new AtomicInteger(1000);
    private static final AtomicInteger orderCounter = new AtomicInteger(10000);
    private static final AtomicInteger customerCounter = new AtomicInteger(100);
    private static final AtomicInteger userCounter = new AtomicInteger(10);

    /**
     * 生成商品ID：P-年月日-序列号
     * 示例：P-20231201-1001
     */
    public static String generateProductId() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = productCounter.getAndIncrement();
        return String.format("P-%s-%04d", date, seq);
    }

    /**
     * 生成订单ID：O-年月日时分秒-序列号
     * 示例：O-20231201143015-10001
     */
    public static String generateOrderId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int seq = orderCounter.getAndIncrement();
        return String.format("O-%s-%05d", timestamp, seq);
    }

    /**
     * 生成客户ID：C-短UUID
     * 示例：C-7b3f9a2d
     */
    public static String generateCustomerId() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "C-" + uuid;
    }

    /**
     * 生成用户ID：U-短UUID
     * 示例：U-admin001
     * 注意：管理员用户ID可能使用固定值
     */
    public static String generateUserId(String username) {
        // 对于管理员，使用固定ID
        if ("admin".equalsIgnoreCase(username) || "boss".equalsIgnoreCase(username)) {
            return "U-admin001";
        }
        // 其他用户生成唯一ID
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        return "U-" + uuid;
    }

    /**
     * 生成库存记录ID：INV-商品ID-时间戳
     */
    public static String generateInventoryRecordId(String productId) {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        return "INV-" + productId + "-" + timestamp;
    }

    /**
     * 生成盘点记录ID：CK-年月日-序列
     */
    public static String generateCheckId() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "CK-" + date + "-" + (int)(Math.random() * 1000);
    }
}