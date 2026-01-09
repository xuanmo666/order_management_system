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
public class OrderService implements OrderServiceInterface {
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private static OrderService instance;

    // 新增：引用InventoryService用于库存同步
    private InventoryService inventoryService;

    private OrderService() {
        this.orderRepository = new OrderRepository();
        // 关键修复：使用ProductService的Repository实例
        this.productRepository = ProductService.getInstance().getProductRepository();
        // 新增：获取InventoryService实例
        this.inventoryService = InventoryService.getInstance();
    }

    public static synchronized OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    /**
     * 创建新订单 - 修复关键：使用同一个Repository实例，并同步库存
     */
    @Override
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

            // 扣减商品库存
            product.setStock(product.getStock() - quantity);
            productRepository.update(product);

            // 关键修复：同步扣减库存记录
            syncInventoryDecrease(productId, quantity);

            // 设置订单项的商品名称和价格（如果未设置）
            if (item.getProductName() == null) {
                item.setProductName(product.getName());
            }
            if (item.getPrice() == 0) {
                item.setPrice(product.getPrice());
            }
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
    @Override
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
    @Override
    public boolean cancelOrder(String orderId) throws ValidationException, BusinessException {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new ValidationException("订单不存在: " + orderId);
        }

        // 检查订单是否可以取消
        if (!order.getStatus().equals(Order.STATUS_PENDING)) {
            throw new BusinessException("只有待付款订单可以取消");
        }

        // 恢复商品库存和库存记录
        for (OrderItem item : order.getItems()) {
            String productId = item.getProductId();
            int quantity = item.getQuantity();

            Product product = productRepository.findById(productId);
            if (product != null) {
                // 恢复商品库存
                product.setStock(product.getStock() + quantity);
                productRepository.update(product);

                // 关键修复：同步恢复库存记录
                syncInventoryIncrease(productId, quantity);
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
    @Override
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
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * 根据客户ID获取订单
     */
    @Override
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    /**
     * 根据状态获取订单
     */
    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * 搜索订单
     */
    @Override
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
    @Override
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
    @Override
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

    /**
     * 同步库存减少 - 新增方法
     */
    private void syncInventoryDecrease(String productId, int amount) {
        try {
            // 获取库存记录
            model.entity.Inventory inventory = inventoryService.getInventoryByProductId(productId);
            if (inventory != null) {
                // 扣减库存数量
                int newQuantity = inventory.getQuantity() - amount;
                if (newQuantity < 0) {
                    newQuantity = 0; // 防止负数
                }
                inventory.setQuantity(newQuantity);
                inventoryService.updateInventory(inventory);
                System.out.println("同步扣减库存: " + productId + " 减少 " + amount + ", 新库存: " + newQuantity);
            }
        } catch (Exception e) {
            System.err.println("同步扣减库存失败: " + productId + " - " + e.getMessage());
        }
    }

    /**
     * 同步库存增加 - 新增方法
     */
    private void syncInventoryIncrease(String productId, int amount) {
        try {
            // 获取库存记录
            model.entity.Inventory inventory = inventoryService.getInventoryByProductId(productId);
            if (inventory != null) {
                // 增加库存数量
                int newQuantity = inventory.getQuantity() + amount;
                inventory.setQuantity(newQuantity);
                inventoryService.updateInventory(inventory);
                System.out.println("同步恢复库存: " + productId + " 增加 " + amount + ", 新库存: " + newQuantity);
            }
        } catch (Exception e) {
            System.err.println("同步恢复库存失败: " + productId + " - " + e.getMessage());
        }
    }

    @Override
    public boolean orderExists(String orderId) {
        return orderRepository.exists(orderId);
    }

    @Override
    public int getOrderCount() {
        return orderRepository.count();
    }

    @Override
    public double calculateOrderTotal(Order order) {
        if (order == null) {
            return 0;
        }
        order.calculateTotalAmount();
        return order.getTotalAmount();
    }

    @Override
    public int getOrderItemCount(String orderId) throws ValidationException {
        Order order = getOrderById(orderId);
        if (order == null) {
            return 0;
        }
        return order.getItems().size();
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public ProductRepository getProductRepository() {
        return productRepository;
    }

    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
}