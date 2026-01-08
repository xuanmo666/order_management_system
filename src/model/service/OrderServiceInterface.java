package model.service;

import model.entity.Order;
import exception.ValidationException;
import exception.BusinessException;
import java.util.List;
import java.util.Map;

/**
 * 订单服务接口 - 供Controller层调用的契约
 */
public interface OrderServiceInterface {
    // 订单管理
    Order createOrder(Order order) throws ValidationException, BusinessException;
    boolean updateOrderStatus(String orderId, String newStatus)
            throws ValidationException, BusinessException;
    boolean cancelOrder(String orderId) throws ValidationException, BusinessException;
    Order getOrderById(String orderId) throws ValidationException;
    List<Order> getAllOrders();

    // 查询和搜索
    List<Order> getOrdersByCustomer(String customerId);
    List<Order> getOrdersByStatus(String status);
    List<Order> searchOrders(String customerId, String status);

    // 统计和分析
    Map<String, Object> getOrderStatistics();
    List<model.entity.Product> getHotProducts(int limit);

    // 验证方法
    boolean orderExists(String orderId);
    int getOrderCount();

    // 订单项管理
    double calculateOrderTotal(Order order);
    int getOrderItemCount(String orderId) throws ValidationException;
}