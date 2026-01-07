package model.repository;

import model.entity.Order;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单数据访问实现类 - 使用内存存储
 */
public class OrderRepository implements Repository<Order> {
    // 使用Map存储订单数据
    private Map<String, Order> orderMap = new HashMap<>();

    @Override
    public boolean add(Order order) {
        if (order == null || order.getOrderId() == null) {
            return false;
        }
        orderMap.put(order.getOrderId(), order);
        return true;
    }

    @Override
    public boolean delete(String id) {
        if (orderMap.containsKey(id)) {
            orderMap.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public Order findById(String id) {
        return orderMap.get(id);
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orderMap.values());
    }

    @Override
    public boolean update(Order order) {
        if (order == null || order.getOrderId() == null) {
            return false;
        }
        if (orderMap.containsKey(order.getOrderId())) {
            orderMap.put(order.getOrderId(), order);
            return true;
        }
        return false;
    }

    @Override
    public int count() {
        return orderMap.size();
    }

    @Override
    public boolean exists(String id) {
        return orderMap.containsKey(id);
    }

    // 特定于订单的查询方法

    /**
     * 根据客户ID查找订单
     */
    public List<Order> findByCustomerId(String customerId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orderMap.values()) {
            if (order.getCustomer() != null &&
                    customerId.equals(order.getCustomer().getId())) {
                result.add(order);
            }
        }
        return result;
    }

    /**
     * 根据状态查找订单
     */
    public List<Order> findByStatus(String status) {
        List<Order> result = new ArrayList<>();
        for (Order order : orderMap.values()) {
            if (status.equals(order.getStatus())) {
                result.add(order);
            }
        }
        return result;
    }

    /**
     * 获取各个状态的订单数量统计
     */
    public Map<String, Integer> getStatusStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        for (Order order : orderMap.values()) {
            String status = order.getStatus();
            stats.put(status, stats.getOrDefault(status, 0) + 1);
        }
        return stats;
    }

    /**
     * 获取销售额统计
     */
    public double getTotalSales() {
        double total = 0;
        for (Order order : orderMap.values()) {
            total += order.getTotalAmount();
        }
        return total;
    }
}