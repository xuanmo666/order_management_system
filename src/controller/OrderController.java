package controller;

import view.OrderPanel;
import model.service.OrderService;
import model.service.ProductService;
import model.entity.Order;
import model.entity.OrderItem;
import model.entity.Product;
import model.entity.Customer;
import exception.ValidationException;
import exception.BusinessException;
import util.IdGenerator;

import javax.swing.*;
import java.util.Date;

/**
 * 订单管理控制器
 */
public class OrderController {
    private OrderPanel orderPanel;
    private OrderService orderService;
    private ProductService productService;

    public OrderController(OrderPanel orderPanel, OrderService orderService,
                           ProductService productService) {
        this.orderPanel = orderPanel;
        this.orderService = orderService;
        this.productService = productService;
        setupEventListeners();
    }

    /**
     * 设置界面事件监听器
     */
    private void setupEventListeners() {
        orderPanel.setActionListener(new OrderPanel.OrderActionListener() {
            @Override
            public void onCreateOrder() {
                handleCreateOrder();
            }

            @Override
            public void onViewOrder(int selectedRow) {
                handleViewOrderDetails(selectedRow);
            }

            @Override
            public void onCancelOrder(int selectedRow) {
                handleCancelOrder(selectedRow);
            }

            @Override
            public void onRefresh() {
                loadOrders();
            }
        });
    }

    /**
     * 加载订单数据到界面
     */
    public void loadOrders() {
        try {
            orderPanel.clearTable();

            var orders = orderService.getAllOrders();

            for (Order order : orders) {
                Object[] rowData = {
                        order.getOrderId(),
                        order.getCustomer() != null ? order.getCustomer().getName() : "未知",
                        order.getTotalAmount(),
                        order.getStatus(),
                        order.getCreateTime()
                };
                orderPanel.addRowToTable(rowData);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "加载订单失败: " + e.getMessage());
        }
    }

    /**
     * 处理创建订单请求
     */
    private void handleCreateOrder() {
        // 简化的创建订单对话框
        JPanel panel = new JPanel(new java.awt.GridLayout(4, 2, 5, 5));

        // 创建临时客户（实际项目中应该从数据库获取）
        Customer customer = new Customer("C001", "张三", "13800138000");

        // 选择商品
        JComboBox<String> productCombo = new JComboBox<>();
        try {
            var products = productService.getAllProducts();
            for (Product product : products) {
                productCombo.addItem(product.getName() + " (库存: " + product.getStock() + ")");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "加载商品失败");
            return;
        }

        JTextField quantityField = new JTextField("1", 10);

        panel.add(new JLabel("客户:"));
        panel.add(new JLabel(customer.getName()));
        panel.add(new JLabel("选择商品:"));
        panel.add(productCombo);
        panel.add(new JLabel("数量:"));
        panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(
                null, panel, "创建订单",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // 创建订单
                Order order = new Order(IdGenerator.generateOrderId(), customer);

                // 获取选中的商品（简化处理）
                var products = productService.getAllProducts();
                int selectedIndex = productCombo.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < products.size()) {
                    Product selectedProduct = products.get(selectedIndex);
                    int quantity = Integer.parseInt(quantityField.getText().trim());

                    // 创建订单项
                    OrderItem item = new OrderItem(selectedProduct, quantity);
                    order.addItem(item);

                    // 保存订单
                    orderService.createOrder(order);

                    // 刷新界面
                    loadOrders();
                    JOptionPane.showMessageDialog(null, "创建订单成功！订单号: " + order.getOrderId());
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "请输入正确的数量");
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(null, "创建订单失败: " + e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "创建订单时发生错误: " + e.getMessage());
            }

        }
    }

    /**
     * 处理查看订单详情请求
     */
    private void handleViewOrderDetails(int selectedRow) {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要查看的订单");
            return;
        }

        try {
            // 获取订单ID
            String orderId = (String) orderPanel.getRowData(selectedRow)[0];

            // 获取订单详情
            Order order = orderService.getOrderById(orderId);

            // 显示订单详情
            StringBuilder details = new StringBuilder();
            details.append("订单号: ").append(order.getOrderId()).append("\n");
            details.append("客户: ").append(order.getCustomer().getName()).append("\n");
            details.append("总金额: ").append(order.getTotalAmount()).append("\n");
            details.append("状态: ").append(order.getStatus()).append("\n");
            details.append("创建时间: ").append(order.getCreateTime()).append("\n\n");
            details.append("订单项:\n");

            for (OrderItem item : order.getItems()) {
                details.append("- ").append(item.getProductName())
                        .append(" x").append(item.getQuantity())
                        .append(" = ").append(item.getSubtotal()).append("\n");
            }

            JOptionPane.showMessageDialog(null, details.toString(), "订单详情",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "查看订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 处理取消订单请求
     */
    private void handleCancelOrder(int selectedRow) {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要取消的订单");
            return;
        }

        try {
            // 确认取消
            int confirm = JOptionPane.showConfirmDialog(
                    null, "确定要取消这个订单吗？",
                    "确认取消", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // 获取订单ID
                String orderId = (String) orderPanel.getRowData(selectedRow)[0];

                // 取消订单
                orderService.cancelOrder(orderId);

                // 刷新界面
                loadOrders();
                JOptionPane.showMessageDialog(null, "取消订单成功");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "取消订单失败: " + e.getMessage());
        }
    }

    /**
     * 刷新订单界面
     */
    public void refreshView() {
        loadOrders();
    }
}