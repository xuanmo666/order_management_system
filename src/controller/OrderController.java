package controller;

import model.entity.Order;
import model.service.OrderService;
import view.ConsoleView;

/**
 * 订单管理控制器
 */
public class OrderController {
    private OrderService orderService;
    private ConsoleView consoleView;

    public void createOrder(Order order) {
        // 创建订单
    }

    public void updateOrderStatus(String orderId, String status) {
        // 更新订单状态
    }

    public void cancelOrder(String orderId) {
        // 取消订单
    }

    public void listOrdersByCustomer(String customerId) {
        // 查询客户订单
    }
}