package model.entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 */
public class Order {
    private String orderId;
    private Customer customer;
    private List<OrderItem> orderItems;
    private String status; // 待付款、待发货、已发货、已完成、已取消
    private Double totalAmount;
    private LocalDateTime createTime;

    // 构造器、Getter/Setter
}