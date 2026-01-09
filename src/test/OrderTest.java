package test;

import model.entity.Customer;
import model.entity.Order;
import model.entity.OrderItem;
import model.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    private Order order;
    private Customer customer;

    @BeforeEach
    void setUp() {
        // 每个测试前都会执行
        customer = new Customer("C001", "张三", "13800138000");
        order = new Order("O001", customer);
    }

    @Test
    void testOrderCreation() {
        assertEquals("O001", order.getOrderId());
        assertEquals(customer, order.getCustomer());
        assertEquals(Order.STATUS_PENDING, order.getStatus());
        assertNotNull(order.getCreateTime());
        assertEquals(0, order.getTotalAmount(), 0.001);
    }

    @Test
    void testAddItemAndCalculateTotal() {
        // Arrange
        Product product = new Product("P001", "手机", 2999.0, "电子产品");
        OrderItem item = new OrderItem(product, 2); // 2个手机

        // Act
        order.addItem(item);

        // Assert
        assertEquals(1, order.getItemCount());
        assertEquals(2, order.getTotalQuantity());
        assertEquals(5998.0, order.getTotalAmount(), 0.001); // 2999 * 2
    }

    @Test
    void testStatusChangeFromPendingToPaid() {
        // Act
        boolean result = order.changeStatus(Order.STATUS_PAID);

        // Assert
        assertTrue(result);
        assertEquals(Order.STATUS_PAID, order.getStatus());
    }

    @Test
    void testStatusChangeFromPendingToShippedShouldFail() {
        // Act
        boolean result = order.changeStatus(Order.STATUS_SHIPPED);

        // Assert
        assertFalse(result); // 待付款不能直接变为已发货
        assertEquals(Order.STATUS_PENDING, order.getStatus()); // 状态应不变
    }

    @Test
    void testCancelOrder() {
        // Act
        order.changeStatus(Order.STATUS_CANCELLED);

        // 尝试改变已取消的订单状态
        boolean canChange = order.changeStatus(Order.STATUS_PAID);

        // Assert
        assertFalse(canChange); // 已取消的订单不能再改变状态
        assertEquals(Order.STATUS_CANCELLED, order.getStatus());
    }

}