package controller;

import exception.BusinessException;
import model.service.OrderService;
import model.service.ProductService;
import view.OrderPanel;
import model.service.OrderServiceInterface;
import model.service.ProductServiceInterface;
import model.entity.Order;
import model.entity.OrderItem;
import model.entity.Product;
import model.entity.Customer;
import exception.ValidationException;
import util.IdGenerator;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单管理控制器
 * 负责处理订单相关的所有业务逻辑，包括订单创建、查看、取消和刷新等操作
 * 遵循MVC设计模式，作为View和Model之间的协调者
 */
public class OrderController {
    // 视图层：订单管理界面
    private OrderPanel orderPanel;

    // 服务层接口：订单服务，处理订单相关业务逻辑
    private OrderServiceInterface orderService;

    // 服务层接口：商品服务，用于获取商品信息和库存检查
    private ProductServiceInterface productService;

    // 商品控制器引用：用于在订单操作后刷新商品界面
    private ProductController productController;

    // 库存控制器引用：用于在订单操作后刷新库存界面
    private InventoryController inventoryController;

    /**
     * 构造方法：初始化订单控制器
     *
     * @param orderPanel 订单管理界面
     * @param orderService 订单服务实例
     * @param productService 商品服务实例
     * @param productController 商品控制器实例
     * @param inventoryController 库存控制器实例
     */
    public OrderController(OrderPanel orderPanel, OrderService orderService,
                           ProductService productService, ProductController productController,
                           InventoryController inventoryController) {
        // 注入依赖：将传入的对象赋值给类的成员变量
        this.orderPanel = orderPanel;
        this.orderService = orderService;
        this.productService = productService;
        this.productController = productController;  // 新增：商品控制器引用
        this.inventoryController = inventoryController; // 新增：库存控制器引用

        // 设置界面的事件监听器
        setupEventListeners();
    }

    /**
     * 设置订单服务（由MainController调用）
     * 提供灵活的依赖注入方式
     *
     * @param orderService 订单服务实例
     */
    public void setOrderService(OrderServiceInterface orderService) {
        this.orderService = orderService;
    }

    /**
     * 设置商品服务（由MainController调用）
     *
     * @param productService 商品服务实例
     */
    public void setProductService(ProductServiceInterface productService) {
        this.productService = productService;
    }

    /**
     * 设置商品控制器（由MainController调用）
     *
     * @param productController 商品控制器实例
     */
    public void setProductController(ProductController productController) {
        this.productController = productController;
    }

    /**
     * 设置库存控制器（由MainController调用）
     *
     * @param inventoryController 库存控制器实例
     */
    public void setInventoryController(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
    }

    /**
     * 设置界面事件监听器
     * 为订单界面的各种操作绑定相应的处理方法
     */
    private void setupEventListeners() {
        // 使用匿名内部类实现OrderPanel中的接口
        orderPanel.setActionListener(new OrderPanel.OrderActionListener() {
            /**
             * 处理创建订单按钮点击事件
             */
            @Override
            public void onCreateOrder() {
                handleCreateOrder();
            }

            /**
             * 处理查看订单详情按钮点击事件
             *
             * @param selectedRow 表格中选中的行索引
             */
            @Override
            public void onViewOrder(int selectedRow) {
                handleViewOrderDetails(selectedRow);
            }

            /**
             * 处理取消订单按钮点击事件
             *
             * @param selectedRow 表格中选中的行索引
             */
            @Override
            public void onCancelOrder(int selectedRow) {
                handleCancelOrder(selectedRow);
            }

            /**
             * 处理刷新按钮点击事件
             */
            @Override
            public void onRefresh() {
                loadOrders();
            }
        });
    }

    /**
     * 加载订单数据到界面
     * 从数据库获取所有订单数据，并显示在表格中
     */
    public void loadOrders() {
        try {
            // 检查服务是否已初始化（依赖注入是否完成）
            if (orderService == null) {
                JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
                return;
            }

            // 清空表格中原有数据
            orderPanel.clearTable();

            // 从订单服务获取所有订单
            var orders = orderService.getAllOrders();

            // 遍历每条订单记录
            for (Order order : orders) {
                // 创建表格行数据数组
                Object[] rowData = {
                        order.getOrderId(),  // 订单ID
                        order.getCustomer() != null ? order.getCustomer().getName() : "未知", // 客户姓名
                        order.getTotalAmount(),  // 订单总金额
                        order.getStatus(),  // 订单状态
                        order.getCreateTime()  // 订单创建时间
                };

                // 将行数据添加到表格中
                orderPanel.addRowToTable(rowData);
            }

        } catch (Exception e) {
            // 捕获异常并显示错误信息
            JOptionPane.showMessageDialog(null, "加载订单失败: " + e.getMessage());
            e.printStackTrace(); // 在控制台打印异常堆栈，便于调试
        }
    }

    /**
     * 处理创建订单请求
     * 弹出对话框让用户选择商品和数量，然后创建订单
     * 会检查库存是否充足，并更新相关界面
     */
    private void handleCreateOrder() {
        // 检查服务是否已初始化
        if (productService == null || orderService == null) {
            JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
            return;
        }

        try {
            // 获取所有商品
            java.util.List<Product> products;
            try {
                products = productService.getAllProducts();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "加载商品失败: " + e.getMessage());
                return;
            }

            // 验证系统中是否有商品
            if (products.isEmpty()) {
                JOptionPane.showMessageDialog(null, "系统中没有商品，请先添加商品");
                return;
            }

            // 创建商品映射：以商品ID为键，商品对象为值，便于快速查找
            Map<String, Product> productMap = new HashMap<>();
            for (Product product : products) {
                productMap.put(product.getId(), product);
            }

            // 创建订单创建对话框的面板
            JPanel panel = new JPanel(new java.awt.GridLayout(5, 2, 5, 5));

            // 客户选择下拉框（简化版，实际应用中应从数据库获取客户列表）
            JComboBox<String> customerCombo = new JComboBox<>();
            customerCombo.addItem("张三");
            customerCombo.addItem("李四");
            customerCombo.addItem("王五");
            customerCombo.addItem("赵六");

            // 商品选择下拉框（显示商品ID和名称）
            JComboBox<String> productCombo = new JComboBox<>();
            for (Product product : products) {
                productCombo.addItem(product.getId() + " - " + product.getName());
            }

            // 购买数量输入框
            JTextField quantityField = new JTextField("1", 10);

            // 显示选中商品的库存数量
            JLabel stockLabel = new JLabel();

            // 初始设置：显示第一个商品的库存
            if (!products.isEmpty()) {
                Product firstProduct = products.get(0);
                stockLabel.setText(String.valueOf(firstProduct.getStock()));
            }

            // 将组件添加到面板
            panel.add(new JLabel("客户:"));
            panel.add(customerCombo);
            panel.add(new JLabel("选择商品:"));
            panel.add(productCombo);
            panel.add(new JLabel("商品库存:"));
            panel.add(stockLabel);
            panel.add(new JLabel("数量:"));
            panel.add(quantityField);

            // 商品选择变化时更新库存显示
            productCombo.addActionListener(e -> {
                String selected = (String) productCombo.getSelectedItem();
                if (selected != null) {
                    // 从下拉框文本中提取商品ID
                    String productId = selected.split(" - ")[0];
                    // 从映射中获取商品对象
                    Product product = productMap.get(productId);
                    if (product != null) {
                        // 更新库存显示
                        stockLabel.setText(String.valueOf(product.getStock()));
                    }
                }
            });

            // 显示对话框
            int result = JOptionPane.showConfirmDialog(
                    null, panel, "创建订单",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            // 如果用户点击"确定"
            if (result == JOptionPane.OK_OPTION) {
                // 获取客户信息（简化处理）
                String customerName = (String) customerCombo.getSelectedItem();
                String customerId = "C-" + customerName.hashCode(); // 简单生成客户ID
                Customer customer = new Customer(customerId, customerName, "13800138000");

                // 创建订单对象
                Order order = new Order(IdGenerator.generateOrderId(), customer);

                // 获取选中的商品信息
                String selected = (String) productCombo.getSelectedItem();
                if (selected != null) {
                    String productId = selected.split(" - ")[0];
                    Product selectedProduct = productMap.get(productId);

                    int quantity;
                    try {
                        // 验证并解析用户输入的数量
                        quantity = Integer.parseInt(quantityField.getText().trim());
                        if (quantity <= 0) {
                            JOptionPane.showMessageDialog(null, "数量必须大于0");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "请输入正确的数量");
                        return;
                    }

                    if (selectedProduct != null) {
                        // 检查库存是否充足
                        if (selectedProduct.getStock() < quantity) {
                            JOptionPane.showMessageDialog(null,
                                    "商品库存不足: " + selectedProduct.getName() +
                                            "，库存: " + selectedProduct.getStock() +
                                            "，需要: " + quantity);
                            return;
                        }

                        // 创建订单项
                        OrderItem item = new OrderItem();
                        item.setProductId(productId);
                        item.setProductName(selectedProduct.getName());
                        item.setPrice(selectedProduct.getPrice());
                        item.setQuantity(quantity);
                        item.calculateSubtotal(); // 计算小计金额

                        // 将订单项添加到订单中
                        order.addItem(item);

                        // 保存订单
                        try {
                            orderService.createOrder(order);

                            // 关键修改：刷新商品界面和库存界面
                            refreshRelatedPanels();

                            // 刷新订单界面显示最新数据
                            loadOrders();

                            // 提示用户操作成功
                            JOptionPane.showMessageDialog(null,
                                    "创建订单成功！\n订单号: " + order.getOrderId() +
                                            "\n客户: " + customerName +
                                            "\n商品: " + selectedProduct.getName() +
                                            "\n数量: " + quantity +
                                            "\n总金额: " + order.getTotalAmount());
                        } catch (ValidationException e) {
                            // 处理业务逻辑验证失败
                            JOptionPane.showMessageDialog(null, "创建订单失败: " + e.getMessage());
                        } catch (BusinessException e) {
                            // 处理业务逻辑异常
                            JOptionPane.showMessageDialog(null, "创建订单失败: " + e.getMessage());
                        }
                    }
                }
            }

        } catch (Exception e) {
            // 处理其他未知异常
            JOptionPane.showMessageDialog(null, "创建订单时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 处理查看订单详情请求
     * 显示选中订单的详细信息，包括客户信息和订单项
     *
     * @param selectedRow 表格中选中的行索引
     */
    private void handleViewOrderDetails(int selectedRow) {
        // 验证是否选择了有效的行
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要查看的订单");
            return;
        }

        // 检查服务是否已初始化
        if (orderService == null) {
            JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
            return;
        }

        try {
            // 从界面表格中获取选中行的订单ID
            String orderId = (String) orderPanel.getRowData(selectedRow)[0];

            // 通过订单服务获取订单详细信息
            Order order = orderService.getOrderById(orderId);

            // 构建订单详情信息字符串
            StringBuilder details = new StringBuilder();
            details.append("订单号: ").append(order.getOrderId()).append("\n");
            details.append("客户: ").append(order.getCustomer().getName()).append("\n");
            details.append("电话: ").append(order.getCustomer().getPhone()).append("\n");
            details.append("总金额: ").append(order.getTotalAmount()).append("\n");
            details.append("状态: ").append(order.getStatus()).append("\n");
            details.append("创建时间: ").append(order.getCreateTime()).append("\n\n");
            details.append("订单项:\n");

            // 遍历订单项，添加到详情信息中
            for (OrderItem item : order.getItems()) {
                details.append("- ").append(item.getProductName())
                        .append(" (ID: ").append(item.getProductId()).append(")")
                        .append(" x").append(item.getQuantity())
                        .append(" 单价: ").append(item.getPrice())
                        .append(" 小计: ").append(item.getSubtotal()).append("\n");
            }

            // 显示订单详情对话框
            JOptionPane.showMessageDialog(null, details.toString(), "订单详情",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "查看订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 处理取消订单请求
     * 取消选中的订单，并更新相关界面
     *
     * @param selectedRow 表格中选中的行索引
     */
    private void handleCancelOrder(int selectedRow) {
        // 验证是否选择了有效的行
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要取消的订单");
            return;
        }

        // 检查服务是否已初始化
        if (orderService == null) {
            JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
            return;
        }

        try {
            // 确认取消操作
            int confirm = JOptionPane.showConfirmDialog(
                    null, "确定要取消这个订单吗？",
                    "确认取消", JOptionPane.YES_NO_OPTION);

            // 如果用户确认取消
            if (confirm == JOptionPane.YES_OPTION) {
                // 从界面表格中获取选中行的订单ID
                String orderId = (String) orderPanel.getRowData(selectedRow)[0];

                // 调用订单服务取消订单
                orderService.cancelOrder(orderId);

                // 关键修改：刷新相关界面
                refreshRelatedPanels();

                // 刷新订单界面显示最新数据
                loadOrders();

                // 提示用户操作成功
                JOptionPane.showMessageDialog(null, "取消订单成功");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "取消订单失败: " + e.getMessage());
        }
    }

    /**
     * 刷新相关界面 - 新增方法
     * 在订单创建或取消后调用，刷新商品和库存界面以保持数据同步
     */
    private void refreshRelatedPanels() {
        // 刷新商品界面
        if (productController != null) {
            productController.refreshView();
            System.out.println("商品界面已刷新");
        } else {
            System.err.println("商品控制器未设置，无法刷新商品界面");
        }

        // 刷新库存界面
        if (inventoryController != null) {
            inventoryController.refreshView();
            System.out.println("库存界面已刷新");
        } else {
            System.err.println("库存控制器未设置，无法刷新库存界面");
        }
    }

    /**
     * 刷新订单界面
     * 公开方法，供其他控制器调用
     */
    public void refreshView() {
        loadOrders();
    }

    /**
     * 获取订单服务（用于测试）
     *
     * @return 订单服务实例
     */
    public OrderServiceInterface getOrderService() {
        return orderService;
    }
}