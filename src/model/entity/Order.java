package model.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单实体类 - 包含订单状态流转功能
 */
public class Order {
    private String orderId;             // 订单ID
    private Customer customer;          // 客户
    private List<OrderItem> items;      // 订单项列表
    private double totalAmount;         // 总金额
    private String status;              // 订单状态
    private Date createTime;            // 创建时间

    // 订单状态常量
    public static final String STATUS_PENDING = "待付款";
    public static final String STATUS_PAID = "已付款";
    public static final String STATUS_SHIPPED = "已发货";
    public static final String STATUS_COMPLETED = "已完成";
    public static final String STATUS_CANCELLED = "已取消";

    public Order() {
        this.items = new ArrayList<>();
        this.status = STATUS_PENDING;
        this.createTime = new Date();
    }

    public Order(String orderId, Customer customer) {
        this();
        this.orderId = orderId;
        this.customer = customer;
    }

    // 添加订单项
    public void addItem(OrderItem item) {
        items.add(item);
        calculateTotalAmount();
    }

    // 移除订单项
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            calculateTotalAmount();
        }
    }

    // 计算订单总金额
    public void calculateTotalAmount() {
        totalAmount = 0;
        for (OrderItem item : items) {
            totalAmount += item.getSubtotal();
        }
    }

    // 获取订单项数量
    public int getItemCount() {
        return items.size();
    }

    // 获取商品总数量
    public int getTotalQuantity() {
        int total = 0;
        for (OrderItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    // 订单状态流转方法
    public boolean changeStatus(String newStatus) {
        // 简单的状态流转规则
        if (STATUS_CANCELLED.equals(status)) {
            return false; // 已取消的订单不能再改变状态
        }

        // 允许的状态流转
        switch (status) {
            case STATUS_PENDING:
                if (STATUS_PAID.equals(newStatus) || STATUS_CANCELLED.equals(newStatus)) {
                    status = newStatus;
                    return true;
                }
                break;
            case STATUS_PAID:
                if (STATUS_SHIPPED.equals(newStatus) || STATUS_CANCELLED.equals(newStatus)) {
                    status = newStatus;
                    return true;
                }
                break;
            case STATUS_SHIPPED:
                if (STATUS_COMPLETED.equals(newStatus)) {
                    status = newStatus;
                    return true;
                }
                break;
        }
        return false;
    }

    // Getter和Setter方法
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        calculateTotalAmount();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public String toString() {
        return String.format("订单号: %s, 客户: %s, 总金额: %.2f, 状态: %s, 创建时间: %s",
                orderId, customer.getName(), totalAmount, status, createTime);
    }
}
