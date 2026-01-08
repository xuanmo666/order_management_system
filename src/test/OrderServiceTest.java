package test;

import model.entity.Order;
import model.entity.Customer;
import model.entity.Product;
import model.entity.OrderItem;
import exception.ValidationException;
import exception.BusinessException;
import model.repository.ProductRepository;
import model.service.OrderService;
import model.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest{
    private OrderService orderService;
    private ProductService productService;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
        productRepository = new ProductRepository();
        productService = new ProductService();
        productService.setProductRepository(productRepository);
        orderService.setProductRepository(productRepository);
    }

    @Test
    void testCreateValidOrder() {
        try {
            // Arrange - 准备商品和库存
            Product product = new Product("ORDER001", "订单测试商品", 100.0, "测试");
            product.setStock(10); // 设置库存
            productService.addProduct(product);

            Customer customer = new Customer("C001", "订单测试客户", "13800138000");

            Order order = new Order("O001", customer);
            order.addItem(new OrderItem(product, 2)); // 购买2个

            // Act
            Order createdOrder = orderService.createOrder(order);

            // Assert
            assertNotNull(createdOrder);
            assertEquals(200.0, createdOrder.getTotalAmount(), 0.001);
            assertEquals(8, productService.getProductById("ORDER001").getStock()); // 库存减少2个

        } catch (Exception e) {
            fail("不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    void testCreateOrderWithInsufficientStock() {
        // Arrange
        Product product = new Product("LOWSTOCK001", "低库存商品", 50.0, "测试");
        product.setStock(1); // 只有1个库存

        try {
            productService.addProduct(product);
        } catch (ValidationException e) {
            fail("商品添加失败: " + e.getMessage());
        }

        Customer customer = new Customer("C002", "客户", "13800138001");
        Order order = new Order("O002", customer);
        order.addItem(new OrderItem(product, 3)); // 尝试购买3个

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> orderService.createOrder(order)
        );

        assertTrue(exception.getMessage().contains("库存不足"));
    }

    @Test
    void testUpdateOrderStatus() {
        try {
            // Arrange - 先创建一个订单
            Product product = new Product("STATUS001", "状态测试商品", 30.0, "测试");
            product.setStock(5);
            productService.addProduct(product);

            Customer customer = new Customer("C003", "状态测试客户", "13800138002");
            Order order = new Order("O003", customer);
            order.addItem(new OrderItem(product, 1));

            orderService.createOrder(order);

            // Act - 更新状态
            boolean result = orderService.updateOrderStatus("O003", Order.STATUS_PAID);

            // Assert
            assertTrue(result);
            Order updated = orderService.getOrderById("O003");
            assertEquals(Order.STATUS_PAID, updated.getStatus());

        } catch (Exception e) {
            fail("不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    void testGetOrderStatistics() {
        try {
            // Arrange - 创建一些订单
            Product product = new Product("STATS001", "统计商品", 25.0, "测试");
            product.setStock(100);
            productService.addProduct(product);

            Customer customer = new Customer("C004", "统计客户", "13800138003");

            // 创建订单1
            Order order1 = new Order("STATS001", customer);
            order1.addItem(new OrderItem(product, 2));
            orderService.createOrder(order1);

            // 创建订单2
            Order order2 = new Order("STATS002", customer);
            order2.addItem(new OrderItem(product, 3));
            orderService.createOrder(order2);

            // Act
            var stats = orderService.getOrderStatistics();

            // Assert
            assertNotNull(stats);
            assertTrue(stats.containsKey("totalOrders"));
            assertTrue((Integer)stats.get("totalOrders") >= 2);

        } catch (Exception e) {
            fail("不应该抛出异常: " + e.getMessage());
        }
    }
}