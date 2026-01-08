package test;

import model.entity.Product;
import exception.ValidationException;
import model.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


class ProductServiceTest {private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService();
    }

    @Test
    void testAddValidProduct() {
        try {
            // Arrange
            Product product = new Product("TEST001", "测试商品", 100.0, "测试分类");

            // Act
            productService.addProduct(product);

            // Assert
            Product retrieved = productService.getProductById("TEST001");
            assertNotNull(retrieved);
            assertEquals("测试商品", retrieved.getName());

        } catch (ValidationException e) {
            fail("不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    void testAddProductWithEmptyIdShouldThrowException() {
        // Arrange
        Product product = new Product("", "测试商品", 100.0, "测试分类");

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> productService.addProduct(product)
        );

        assertTrue(exception.getMessage().contains("商品ID不能为空"));
    }

    @Test
    void testAddProductWithZeroPriceShouldThrowException() {
        // Arrange
        Product product = new Product("TEST002", "测试商品", 0, "测试分类");

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> productService.addProduct(product)
        );

        assertTrue(exception.getMessage().contains("商品价格必须大于0"));
    }

    @Test
    void testGetNonExistentProductShouldThrowException() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> productService.getProductById("NON_EXISTENT")
        );

        assertTrue(exception.getMessage().contains("商品不存在"));
    }

    @Test
    void testStockInOperation() {
        try {
            // Arrange
            Product product = new Product("STOCK001", "库存商品", 50.0, "库存测试");
            productService.addProduct(product);

            // Act
            productService.stockIn("STOCK001", 10);

            // Assert
            Product updated = productService.getProductById("STOCK001");
            assertEquals(10, updated.getStock());

        } catch (ValidationException e) {
            fail("不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    void testGetAllProducts() {
        // Arrange
        Product product1 = new Product("ALL001", "商品1", 10.0, "分类1");
        Product product2 = new Product("ALL002", "商品2", 20.0, "分类2");

        try {
            productService.addProduct(product1);
            productService.addProduct(product2);

            // Act
            var allProducts = productService.getAllProducts();

            // Assert
            assertTrue(allProducts.size() >= 2);

        } catch (ValidationException e) {
            fail("不应该抛出异常: " + e.getMessage());
        }
    }

}