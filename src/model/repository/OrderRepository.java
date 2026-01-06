package model.repository;

import model.entity.Order;
import java.util.List;

/**
 * 订单数据访问接口
 */
public class OrderRepository {
    public boolean save(Order order) {
        // 保存订单
        return true;
    }

    public List<Order> findByCustomerId(String customerId) {
        // 查询客户订单
        return null;
    }
}