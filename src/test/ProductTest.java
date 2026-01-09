package test;

import model.entity.Product;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    @Test
    void testProductCreation() {
        // Arrange（准备数据）
        Product product = new Product("P001", "测试商品", 99.99, "电子产品");

        // Act & Assert（操作和断言）
        assertEquals("P001", product.getId());
        assertEquals("测试商品", product.getName());
        assertEquals(99.99, product.getPrice(), 0.001); // 第三个参数是精度
        assertEquals("电子产品", product.getCategory());
        assertEquals(0, product.getStock()); // 初始库存应为0
    }

    @Test
    void testIncreaseStock() {
        // Arrange
        Product product = new Product("P002", "测试商品2", 50.0, "食品");

        // Act
        product.increaseStock(10);

        // Assert
        assertEquals(10, product.getStock());
    }

    @Test
    void testDecreaseStockSuccess() {
        // Arrange
        Product product = new Product("P003", "测试商品3", 30.0, "文具");
        product.increaseStock(5);

        // Act
        boolean result = product.decreaseStock(3);

        // Assert
        assertTrue(result);
        assertEquals(2, product.getStock());
    }

    @Test
    void testDecreaseStockFail() {
        // Arrange
        Product product = new Product("P004", "测试商品4", 40.0, "图书");
        product.increaseStock(2);

        // Act
        boolean result = product.decreaseStock(5); // 尝试减少超过库存

        // Assert
        assertFalse(result);
        assertEquals(2, product.getStock()); // 库存应不变
    }

    @Test
    void testToString() {
        // Arrange
        Product product = new Product("P005", "测试商品5", 150.0, "服装");

        // Act
        String result = product.toString();

        // Assert
        assertTrue(result.contains("P005"));
        assertTrue(result.contains("测试商品5"));
        assertTrue(result.contains("150.0"));
    }
}