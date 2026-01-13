package test;

import model.entity.Customer;
import model.entity.Order;
import model.entity.OrderItem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Order实体类测试
 * 测试用例
 */
public class OrderTest {

    /**
     * 测试1: 成功创建订单
     * 测试了订单的基本创建和属性设置
     */
    @Test
    public void testCreateOrderSuccess() {
        System.out.println("测试1: 创建订单成功");

        // 创建客户
        Customer customer = new Customer("C001", "张三", "13800138000");

        // 创建订单
        Order order = new Order("O-001", customer);

        // 验证订单属性
        assertEquals("O-001", order.getOrderId());
        assertEquals(customer, order.getCustomer());
        assertEquals(Order.STATUS_PENDING, order.getStatus());
        assertTrue(order.getTotalAmount() == 0);
        assertEquals(0, order.getItemCount());

        System.out.println("✓ 订单创建成功");
    }

    /**
     * 测试2: 添加订单项并计算总金额
     * 测试了订单项的添加和金额计算
     */
    @Test
    public void testAddOrderItemAndCalculateTotal() {
        System.out.println("\n测试2: 添加订单项并计算总金额");

        Customer customer = new Customer("C002", "李四", "13900139000");
        Order order = new Order("O-002", customer);

        // 创建订单项1
        OrderItem item1 = new OrderItem();
        item1.setProductId("P-001");
        item1.setProductName("iPhone");
        item1.setPrice(5999.0);
        item1.setQuantity(2);
        item1.calculateSubtotal(); // 5999 * 2 = 11998

        // 创建订单项2
        OrderItem item2 = new OrderItem();
        item2.setProductId("P-002");
        item2.setProductName("耳机");
        item2.setPrice(199.0);
        item2.setQuantity(1);
        item2.calculateSubtotal(); // 199

        // 添加订单项
        order.addItem(item1);
        order.addItem(item2);

        // 验证
        assertEquals(2, order.getItemCount());
        assertEquals(3, order.getTotalQuantity()); // 2 + 1 = 3
        assertEquals(12197.0, order.getTotalAmount(), 0.001); // 11998 + 199 = 12197

        System.out.println("✓ 订单项添加成功，总金额计算正确");
    }

    /**
     * 测试3: 订单状态流转 - 正常流程
     * 测试了状态从待付款 -> 已付款 -> 已发货 -> 已完成的流转
     */
    @Test
    public void testOrderStatusFlowSuccess() {
        System.out.println("\n测试3: 订单状态正常流转");

        Customer customer = new Customer("C003", "王五", "13700137000");
        Order order = new Order("O-003", customer);

        // 初始状态应该是待付款
        assertEquals(Order.STATUS_PENDING, order.getStatus());

        // 待付款 -> 已付款 (应该成功)
        assertTrue(order.changeStatus(Order.STATUS_PAID));
        assertEquals(Order.STATUS_PAID, order.getStatus());

        // 已付款 -> 已发货 (应该成功)
        assertTrue(order.changeStatus(Order.STATUS_SHIPPED));
        assertEquals(Order.STATUS_SHIPPED, order.getStatus());

        // 已发货 -> 已完成 (应该成功)
        assertTrue(order.changeStatus(Order.STATUS_COMPLETED));
        assertEquals(Order.STATUS_COMPLETED, order.getStatus());

        System.out.println("✓ 订单状态流转正常");
    }

    /**
     * 测试4: 订单状态流转 - 失败流程
     * 测试了不允许的状态流转
     */
    @Test
    public void testOrderStatusFlowFailure() {
        System.out.println("\n测试4: 订单状态流转失败情况");

        Customer customer = new Customer("C004", "赵六", "13600136000");
        Order order = new Order("O-004", customer);

        // 待付款 -> 已发货 (应该失败，必须先付款)
        assertFalse(order.changeStatus(Order.STATUS_SHIPPED));
        assertEquals(Order.STATUS_PENDING, order.getStatus());

        // 待付款 -> 已取消 (应该成功)
        assertTrue(order.changeStatus(Order.STATUS_CANCELLED));
        assertEquals(Order.STATUS_CANCELLED, order.getStatus());

        // 已取消的订单不能再改变状态
        assertFalse(order.changeStatus(Order.STATUS_PAID));
        assertFalse(order.changeStatus(Order.STATUS_SHIPPED));
        assertEquals(Order.STATUS_CANCELLED, order.getStatus());

        System.out.println("✓ 非法状态流转被正确阻止");
    }

    /**
     * 测试5: 边界测试 - 空订单和空值处理
     */
    @Test
    public void testBoundaryAndNull() {
        System.out.println("\n测试5: 边界和空值测试");

        // 测试空构造函数
        Order order = new Order();
        assertNotNull(order.getItems()); // 订单项列表应该被初始化
        assertEquals(0, order.getItemCount());
        assertEquals(Order.STATUS_PENDING, order.getStatus());

        // 测试设置空客户（虽然业务逻辑不允许，但测试实体类容错）
        order.setCustomer(null);
        assertNull(order.getCustomer());

        System.out.println("✓ 边界情况处理正常");
    }

    /**
     * 测试6: 移除订单项
     */
    @Test
    public void testRemoveOrderItem() {
        System.out.println("\n测试6: 移除订单项测试");

        Customer customer = new Customer("C005", "测试用户", "13500135000");
        Order order = new Order("O-005", customer);

        // 添加两个订单项
        OrderItem item1 = new OrderItem();
        item1.setProductId("P-001");
        item1.setPrice(100);
        item1.setQuantity(2);

        OrderItem item2 = new OrderItem();
        item2.setProductId("P-002");
        item2.setPrice(50);
        item2.setQuantity(1);

        order.addItem(item1);
        order.addItem(item2);
        assertEquals(2, order.getItemCount());

        // 移除第一个订单项
        order.removeItem(0);
        assertEquals(1, order.getItemCount());
        assertEquals(50.0, order.getTotalAmount(), 0.001);

        // 移除不存在的索引（应该无影响）
        order.removeItem(10);
        assertEquals(1, order.getItemCount());

        System.out.println("✓ 订单项移除功能正常");
    }
}