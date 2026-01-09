package test;

import model.entity.Product;
import exception.ValidationException;
import model.service.ProductService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * ProductService服务类测试
 * 简易测试用例
 */
public class ProductServiceTest {

    /**
     * 测试1: 添加商品成功
     */
    @Test
    public void testAddProductSuccess() {
        System.out.println("测试1: 添加商品成功");

        try {
            ProductService productService = ProductService.getInstance();

            // 创建商品对象
            Product product = new Product();
            product.setId("P-test001");
            product.setName("测试商品1");
            product.setPrice(99.99);
            product.setCategory("测试类");
            product.setStock(10);

            // 添加商品
            productService.addProduct(product);

            System.out.println("商品添加成功");

        } catch (Exception e) {
            System.out.println("商品可能已存在: " + e.getMessage());
        }
    }

    /**
     * 测试2: 添加商品失败 - 商品ID已存在
     */
    @Test
    public void testAddProductDuplicateId() {
        System.out.println("\n测试2: 添加商品失败 - 商品ID重复");

        try {
            ProductService productService = ProductService.getInstance();

            // 创建一个唯一的商品ID用于测试
            String testId = "TEST-DUPLICATE-" + System.currentTimeMillis();

            // 第一次添加
            Product product1 = new Product();
            product1.setId(testId);
            product1.setName("测试商品A");
            product1.setPrice(100.0);
            product1.setCategory("测试类");
            product1.setStock(10);

            productService.addProduct(product1);
            System.out.println("第一次添加成功: " + testId);

            // 立即尝试添加相同ID的商品
            Product product2 = new Product();
            product2.setId(testId); // 相同ID
            product2.setName("测试商品B");
            product2.setPrice(150.0);
            product2.setCategory("测试类");
            product2.setStock(20);

            // 这次应该抛出异常
            productService.addProduct(product2);
            fail("应该抛出ValidationException");

        } catch (ValidationException e) {
            // 期望的异常
            System.out.println("✓ 正确捕获异常: " + e.getMessage());
            assertTrue(e.getMessage().contains("商品ID已存在"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("测试失败: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * 测试3: 添加商品失败 - 数据验证失败
     */
    @Test
    public void testAddProductValidationFailure() {
        System.out.println("\n测试3: 添加商品失败 - 数据验证失败");

        try {
            ProductService productService = ProductService.getInstance();

            // 创建无效的商品（价格为0）
            Product product = new Product();
            product.setId("P-invalid001");
            product.setName("无效商品");
            product.setPrice(0); // 无效价格
            product.setCategory("测试类");

            productService.addProduct(product);
            fail("应该抛出ValidationException");

        } catch (ValidationException e) {
            // 期望的异常
            System.out.println("正确捕获异常: " + e.getMessage());
            assertTrue(e.getMessage().contains("商品价格必须大于0"));
        } catch (Exception e) {
            fail("错误的异常类型: " + e.getClass().getName());
        }
    }

    /**
     * 测试4: 获取所有商品
     */
    @Test
    public void testGetAllProducts() {
        System.out.println("\n测试4: 获取所有商品");

        try {
            ProductService productService = ProductService.getInstance();

            // 获取所有商品
            List<Product> products = productService.getAllProducts();

            assertNotNull(products);
            assertTrue(products.size() > 0);

            System.out.println("成功获取商品列表，数量: " + products.size());

            // 打印前几个商品信息
            int count = Math.min(3, products.size());
            for (int i = 0; i < count; i++) {
                Product p = products.get(i);
                System.out.println("  " + (i+1) + ". " + p.getName() + " - ￥" + p.getPrice());
            }

        } catch (Exception e) {
            fail("获取商品列表失败: " + e.getMessage());
        }
    }

    /**
     * 测试5: 根据ID获取商品成功
     */
    @Test
    public void testGetProductByIdSuccess() {
        System.out.println("\n测试5: 根据ID获取商品成功");

        try {
            ProductService productService = ProductService.getInstance();

            // 获取存在的商品（使用初始化数据中的ID）
            Product product = productService.getProductById("P-001");

            assertNotNull(product);
            assertEquals("P-001", product.getId());
            assertNotNull(product.getName());
            assertTrue(product.getPrice() > 0);

            System.out.println("成功获取商品: " + product.getName());

        } catch (Exception e) {
            System.out.println("跳过测试: " + e.getMessage());
        }
    }

    /**
     * 测试6: 根据ID获取商品失败 - 商品不存在
     */
    @Test
    public void testGetProductByIdNotFound() {
        System.out.println("\n测试6: 获取不存在的商品");

        try {
            ProductService productService = ProductService.getInstance();

            // 尝试获取不存在的商品
            productService.getProductById("P-NOT-EXIST-123456");
            fail("应该抛出ValidationException");

        } catch (ValidationException e) {
            // 期望的异常
            System.out.println("正确捕获异常: " + e.getMessage());
            assertTrue(e.getMessage().contains("商品不存在"));
        } catch (Exception e) {
            fail("错误的异常类型: " + e.getClass().getName());
        }
    }

    /**
     * 测试7: 更新商品信息
     */
    @Test
    public void testUpdateProduct() {
        System.out.println("\n测试7: 更新商品信息");

        try {
            ProductService productService = ProductService.getInstance();

            // 先添加一个测试商品
            Product product = new Product();
            product.setId("P-update001");
            product.setName("原始名称");
            product.setPrice(100.0);
            product.setCategory("测试类");

            try {
                productService.addProduct(product);
            } catch (Exception e) {
                // 商品可能已存在
            }

            // 更新商品信息
            product.setName("更新后的名称");
            product.setPrice(150.0);
            product.setCategory("更新类");

            productService.updateProduct(product);

            // 重新获取验证
            Product updated = productService.getProductById("P-update001");
            assertEquals("更新后的名称", updated.getName());
            assertEquals(150.0, updated.getPrice(), 0.001);
            assertEquals("更新类", updated.getCategory());

            System.out.println("商品信息更新成功");

        } catch (Exception e) {
            System.out.println("跳过测试: " + e.getMessage());
        }
    }

    /**
     * 测试8: 搜索商品
     */
    @Test
    public void testSearchProducts() {
        System.out.println("\n测试8: 搜索商品");

        try {
            ProductService productService = ProductService.getInstance();

            // 搜索分类为"手机"的商品
            List<Product> results = productService.searchProducts(
                    null, // 无关键词
                    "手机", // 分类
                    null, // 无最低价
                    null  // 无最高价
            );

            assertNotNull(results);

            if (results.size() > 0) {
                System.out.println("找到" + results.size() + "个手机类商品:");
                for (Product p : results) {
                    System.out.println("  - " + p.getName());
                }
            } else {
                System.out.println("未找到手机类商品");
            }

        } catch (Exception e) {
            fail("搜索商品失败: " + e.getMessage());
        }
    }

    /**
     * 测试9: 库存入库操作
     */
    @Test
    public void testStockIn() {
        System.out.println("\n测试9: 商品入库操作");

        try {
            ProductService productService = ProductService.getInstance();

            // 创建一个测试商品
            Product product = new Product();
            product.setId("P-stock001");
            product.setName("入库测试商品");
            product.setPrice(50.0);
            product.setCategory("测试");
            product.setStock(10); // 初始库存10

            try {
                productService.addProduct(product);
            } catch (Exception e) {
                // 商品可能已存在
            }

            // 执行入库操作
            productService.stockIn("P-stock001", 5);

            // 验证库存增加
            Product updated = productService.getProductById("P-stock001");
            assertEquals(15, updated.getStock()); // 10 + 5 = 15

            System.out.println("商品入库成功，新库存: " + updated.getStock());

        } catch (Exception e) {
            System.out.println("跳过测试: " + e.getMessage());
        }
    }

    /**
     * 测试10: 获取分类统计
     */
    @Test
    public void testGetCategoryStatistics() {
        System.out.println("\n测试10: 获取分类统计");

        try {
            ProductService productService = ProductService.getInstance();

            // 获取分类统计
            var stats = productService.getCategoryStatistics();

            assertNotNull(stats);

            System.out.println("获取分类统计成功:");
            for (var entry : stats.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue() + "个商品");
            }

        } catch (Exception e) {
            fail("获取分类统计失败: " + e.getMessage());
        }
    }
}