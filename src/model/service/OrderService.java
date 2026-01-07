package model.service;

import model.entity.Order;
import model.entity.OrderItem;
import model.entity.Product;
import model.entity.Customer;
import model.repository.OrderRepository;
import model.repository.ProductRepository;
import exception.ValidationException;
import exception.BusinessException;
import util.ValidationUtil;

import java.util.List;

/**
 * 订单业务逻辑服务类
 * 负责订单的创建、状态管理、库存扣减等复杂业务逻辑
 */
public class OrderService {
    private OrderRepository orderRepository;
    private ProductRepository productRepository;

    public OrderService() {
        this.orderRepository = new OrderRepository();
        this.productRepository = new ProductRepository();
    }

    /**
     * 创建新订单 - 复杂对象交互的示例
     * 涉及商品、库存、订单、客户等多个对象的协作
     */
    public Order createOrder(Order order) throws ValidationException, BusinessException {
        // 验证订单数据
        validateOrder(order);

        // 验证并扣减商品库存
        for (OrderItem item : order.getItems()) {
            String productId = item.getProductId();
            int quantity = item.getQuantity();

            // 获取商品信息
            Product product = productRepository.findById(productId);
            if (product == null) {
                throw new ValidationException("商品不存在: " + productId);
            }

            // 检查库存是否充足
            if (product.getStock() < quantity) {
                throw new BusinessException("商品库存不足: " + product.getName() +
                        "，需要" + quantity + "，库存" + product.getStock());
            }

            // 扣减库存
            product.setStock(product.getStock() - quantity);
            productRepository.update(product);

            // 设置订单项的商品名称和价格
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
        }

        // 计算订单总金额
        order.calculateTotalAmount();

        // 更新客户消费总额
        if (order.getCustomer() != null) {
            order.getCustomer().addSpent(order.getTotalAmount());
        }

        // 保存订单
        boolean success = orderRepository.add(order);
        if (!success) {
            throw new BusinessException("创建订单失败");
        }

        return order;
    }

    /**
     * 更新订单状态
     */
    public boolean updateOrderStatus(String orderId, String newStatus)
            throws ValidationException, BusinessException {
        if (!ValidationUtil.isNotBlank(orderId)) {
            throw new ValidationException("订单ID不能为空");
        }

        if (!ValidationUtil.isNotBlank(newStatus)) {
            throw new ValidationException("订单状态不能为空");
        }

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new ValidationException("订单不存在: " + orderId);
        }

        // 使用订单实体的状态流转方法
        boolean success = order.changeStatus(newStatus);
        if (!success) {
            throw new BusinessException("订单状态流转失败: 从" + order.getStatus() + "到" + newStatus);
        }

        // 更新订单
        orderRepository.update(order);
        return true;
    }

    /**
     * 取消订单 - 需要恢复库存
     */
    public boolean cancelOrder(String orderId) throws ValidationException, BusinessException {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new ValidationException("订单不存在: " + orderId);
        }

        // 检查订单是否可以取消
        if (!order.getStatus().equals(Order.STATUS_PENDING)) {
            throw new BusinessException("只有待付款订单可以取消");
        }

        // 恢复商品库存
        for (OrderItem item : order.getItems()) {
            String productId = item.getProductId();
            int quantity = item.getQuantity();

            Product product = productRepository.findById(productId);
            if (product != null) {
                product.setStock(product.getStock() + quantity);
                productRepository.update(product);
            }
        }

        // 更新订单状态为已取消
        order.setStatus(Order.STATUS_CANCELLED);
        orderRepository.update(order);

        return true;
    }

    /**
     * 根据ID获取订单
     */
    public Order getOrderById(String orderId) throws ValidationException {
        if (!ValidationUtil.isNotBlank(orderId)) {
            throw new ValidationException("订单ID不能为空");
        }

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new ValidationException("订单不存在: " + orderId);
        }

        return order;
    }

    /**
     * 获取所有订单
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * 根据客户ID获取订单
     */
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    /**
     * 根据状态获取订单
     */
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * 搜索订单
     */
    public List<Order> searchOrders(String customerId, String status) {
        List<Order> allOrders = orderRepository.findAll();
        List<Order> result = new java.util.ArrayList<>();

        for (Order order : allOrders) {
            boolean match = true;

            if (customerId != null && !customerId.trim().isEmpty()) {
                if (order.getCustomer() == null ||
                        !customerId.equals(order.getCustomer().getId())) {
                    match = false;
                }
            }

            if (status != null && !status.trim().isEmpty()) {
                if (!status.equals(order.getStatus())) {
                    match = false;
                }
            }

            if (match) {
                result.add(order);
            }
        }

        return result;
    }

    /**
     * 获取订单统计信息
     */
    public java.util.Map<String, Object> getOrderStatistics() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        // 订单数量统计
        stats.put("totalOrders", orderRepository.count());
        stats.put("totalSales", orderRepository.getTotalSales());

        // 状态统计
        java.util.Map<String, Integer> statusStats = orderRepository.getStatusStatistics();
        stats.putAll(statusStats);

        // 计算平均订单金额
        int orderCount = orderRepository.count();
        double avgOrderAmount = orderCount == 0 ? 0 :
                orderRepository.getTotalSales() / orderCount;
        stats.put("averageOrderAmount", avgOrderAmount);

        return stats;
    }

    /**
     * 获取热销商品（按销售数量排序）
     */
    public java.util.List<model.entity.Product> getHotProducts(int limit) {
        // 统计每个商品的销售数量
        java.util.Map<String, Integer> salesCount = new java.util.HashMap<>();
        for (Order order : orderRepository.findAll()) {
            for (OrderItem item : order.getItems()) {
                String productId = item.getProductId();
                int quantity = item.getQuantity();
                salesCount.put(productId,
                        salesCount.getOrDefault(productId, 0) + quantity);
            }
        }

        // 转换为商品列表并排序
        java.util.List<model.entity.Product> hotProducts = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, Integer> entry : salesCount.entrySet()) {
            Product product = productRepository.findById(entry.getKey());
            if (product != null) {
                hotProducts.add(product);
            }
        }

        // 按销售数量排序（冒泡排序）
        for (int i = 0; i < hotProducts.size() - 1; i++) {
            for (int j = 0; j < hotProducts.size() - i - 1; j++) {
                Product p1 = hotProducts.get(j);
                Product p2 = hotProducts.get(j + 1);
                int count1 = salesCount.get(p1.getId());
                int count2 = salesCount.get(p2.getId());

                if (count1 < count2) {
                    hotProducts.set(j, p2);
                    hotProducts.set(j + 1, p1);
                }
            }
        }

        // 限制返回数量
        if (hotProducts.size() > limit) {
            return hotProducts.subList(0, limit);
        }

        return hotProducts;
    }

    // 私有方法：验证订单数据
    private void validateOrder(Order order) throws ValidationException {
        if (order == null) {
            throw new ValidationException("订单不能为空");
        }

        if (!ValidationUtil.isNotBlank(order.getOrderId())) {
            throw new ValidationException("订单ID不能为空");
        }

        if (order.getCustomer() == null) {
            throw new ValidationException("订单必须关联客户");
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new ValidationException("订单必须包含商品");
        }

        // 检查订单是否已存在
        if (orderRepository.exists(order.getOrderId())) {
            throw new ValidationException("订单ID已存在: " + order.getOrderId());
        }
    }
}
