package controller;

import model.service.InventoryService;
import model.service.ProductService;
import view.InventoryPanel;
import model.service.InventoryServiceInterface;
import model.service.ProductServiceInterface;
import model.entity.Inventory;
import exception.ValidationException;

import javax.swing.*;

/**
 * 库存管理控制器
 * 作为MVC模式中的Controller层，负责协调库存相关的视图和模型交互
 */
public class InventoryController {
    // 视图层：库存管理界面
    private InventoryPanel inventoryPanel;

    // 服务层接口：库存服务，使用接口类型提高灵活性和可测试性
    private InventoryServiceInterface inventoryService;

    // 服务层接口：商品服务，用于获取商品信息
    private ProductServiceInterface productService;

    // 商品控制器引用：用于在库存调整后刷新商品界面
    private ProductController productController;

    /**
     * 构造方法：初始化控制器
     *
     * @param inventoryPanel 库存管理界面
     * @param inventoryService 库存服务实例
     * @param productService 商品服务实例
     * @param productController 商品控制器实例，用于跨控制器通信
     */
    public InventoryController(InventoryPanel inventoryPanel,
                               InventoryService inventoryService,
                               ProductService productService,
                               ProductController productController) {
        // 注入依赖：将传入的对象赋值给类的成员变量
        this.inventoryPanel = inventoryPanel;
        this.inventoryService = inventoryService;
        this.productService = productService;
        this.productController = productController;

        // 设置界面的事件监听器
        setupEventListeners();
    }

    /**
     * 设置库存服务（由MainController调用）
     * 提供灵活的依赖注入方式
     *
     * @param inventoryService 库存服务实例
     */
    public void setInventoryService(InventoryServiceInterface inventoryService) {
        this.inventoryService = inventoryService;
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
     * 设置界面事件监听器
     * 为库存界面的各种操作绑定相应的处理方法
     */
    private void setupEventListeners() {
        // 使用匿名内部类实现InventoryPanel中的接口
        inventoryPanel.setActionListener(new InventoryPanel.InventoryActionListener() {
            /**
             * 处理低库存预警按钮点击事件
             */
            @Override
            public void onLowStockWarning() {
                handleLowStockWarning();
            }

            /**
             * 处理库存调整按钮点击事件
             *
             * @param selectedRow 表格中选中的行索引
             */
            @Override
            public void onAdjustInventory(int selectedRow) {
                handleAdjustInventory(selectedRow);
            }

            /**
             * 处理刷新按钮点击事件
             */
            @Override
            public void onRefresh() {
                loadInventory();
            }
        });
    }

    /**
     * 加载库存数据到界面
     * 从数据库获取库存数据，过滤已删除的商品，并显示在表格中
     */
    public void loadInventory() {
        try {
            // 检查服务是否已初始化（依赖注入是否完成）
            if (inventoryService == null || productService == null) {
                JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
                return;
            }

            // 清空表格中原有数据
            inventoryPanel.clearTable();

            // 从库存服务获取所有库存记录
            var inventoryList = inventoryService.getAllInventory();

            // 遍历每条库存记录
            for (Inventory inventory : inventoryList) {
                String productId = inventory.getProductId();

                // 检查库存记录对应的商品是否存在
                boolean productExists = false;
                String productName = "未知";
                try {
                    // 通过商品服务查询商品信息
                    var product = productService.getProductById(productId);
                    if (product != null) {
                        productExists = true;
                        productName = product.getName();
                    }
                } catch (Exception e) {
                    // 查询商品时发生异常，说明商品可能不存在
                    productExists = false;
                }

                // 关键修复：如果商品已被删除，跳过显示该库存记录
                // 避免在界面上显示无效数据
                if (!productExists) {
                    System.out.println("跳过已删除商品的库存记录: " + productId);
                    continue;
                }

                // 根据库存数量判断库存状态
                String status = "正常";
                if (inventory.needsWarning()) {
                    status = "低库存预警";
                }

                // 创建表格行数据数组
                Object[] rowData = {
                        inventory.getProductId(),  // 商品ID
                        productName,               // 商品名称
                        inventory.getQuantity(),   // 当前库存数量
                        inventory.getMinThreshold(), // 最低库存阈值
                        status                     // 库存状态
                };

                // 将行数据添加到表格中
                inventoryPanel.addRowToTable(rowData);
            }

        } catch (Exception e) {
            // 捕获异常并显示错误信息
            JOptionPane.showMessageDialog(null, "加载库存失败: " + e.getMessage());
            e.printStackTrace(); // 在控制台打印异常堆栈，便于调试
        }
    }

    /**
     * 处理库存调整请求
     * 弹出对话框让用户选择调整类型和数量，然后更新库存
     * 最后刷新库存和商品界面
     *
     * @param selectedRow 表格中选中的行索引
     */
    private void handleAdjustInventory(int selectedRow) {
        // 验证是否选择了有效的行
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要调整的库存记录");
            return;
        }

        // 检查服务是否已初始化
        if (inventoryService == null) {
            JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
            return;
        }

        try {
            // 从界面表格中获取选中行的数据
            Object[] rowData = inventoryPanel.getRowData(selectedRow);
            String productId = (String) rowData[0];

            // 验证商品是否存在（防止操作已删除的商品）
            if (!productService.productExists(productId)) {
                JOptionPane.showMessageDialog(null, "商品不存在，无法调整库存");
                // 刷新界面以移除已删除商品的记录
                loadInventory();
                return;
            }

            // 创建库存调整对话框
            JPanel panel = new JPanel(new java.awt.GridLayout(3, 2, 5, 5));

            // 对话框组件
            JLabel productIdLabel = new JLabel(productId);
            JComboBox<String> operationCombo = new JComboBox<>(new String[]{"入库", "出库"});
            JTextField quantityField = new JTextField("1", 10);

            // 将组件添加到面板
            panel.add(new JLabel("商品ID:"));
            panel.add(productIdLabel);
            panel.add(new JLabel("操作类型:"));
            panel.add(operationCombo);
            panel.add(new JLabel("数量:"));
            panel.add(quantityField);

            // 显示对话框
            int result = JOptionPane.showConfirmDialog(
                    null, panel, "调整库存",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            // 如果用户点击"确定"
            if (result == JOptionPane.OK_OPTION) {
                // 获取用户输入
                String operation = operationCombo.getSelectedItem().toString();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                String operationCode = "入库".equals(operation) ? "in" : "out";

                // 调用库存服务执行调整操作
                inventoryService.adjustInventory(productId, quantity, operationCode);

                // 刷新库存界面显示最新数据
                loadInventory();

                // 关键修改：刷新商品界面（因为库存变化可能影响商品列表的显示）
                refreshProductPanel();

                // 提示用户操作成功
                JOptionPane.showMessageDialog(null, "库存调整成功");
            }

        } catch (NumberFormatException e) {
            // 处理数量输入格式错误
            JOptionPane.showMessageDialog(null, "请输入正确的数量");
        } catch (ValidationException e) {
            // 处理业务逻辑验证失败
            JOptionPane.showMessageDialog(null, "调整库存失败: " + e.getMessage());
        } catch (Exception e) {
            // 处理其他未知异常
            JOptionPane.showMessageDialog(null, "调整库存时发生错误: " + e.getMessage());
        }
    }

    /**
     * 处理低库存预警请求
     * 查询库存数量低于阈值的商品，并显示警告信息
     */
    private void handleLowStockWarning() {
        // 检查服务是否已初始化
        if (inventoryService == null || productService == null) {
            JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
            return;
        }

        try {
            // 获取低库存商品列表
            var lowStockItems = inventoryService.getLowStockItems();

            // 过滤已删除的商品
            java.util.List<Inventory> validLowStockItems = new java.util.ArrayList<>();
            for (Inventory inventory : lowStockItems) {
                if (productService.productExists(inventory.getProductId())) {
                    validLowStockItems.add(inventory);
                }
            }

            // 如果没有低库存商品，显示提示信息
            if (validLowStockItems.isEmpty()) {
                JOptionPane.showMessageDialog(null, "当前没有低库存商品");
                return;
            }

            // 构建预警信息字符串
            StringBuilder warningMsg = new StringBuilder("低库存预警商品:\n\n");

            for (Inventory inventory : validLowStockItems) {
                String productName = "未知";
                try {
                    // 获取商品名称
                    var product = productService.getProductById(inventory.getProductId());
                    productName = product.getName();
                } catch (Exception e) {
                    // 商品可能已被删除，跳过此项
                    continue;
                }

                // 添加每条预警信息
                warningMsg.append("商品: ").append(productName)
                        .append(" (").append(inventory.getProductId()).append(")\n")
                        .append("当前库存: ").append(inventory.getQuantity())
                        .append(", 阈值: ").append(inventory.getMinThreshold())
                        .append("\n\n");
            }

            // 显示预警对话框
            JOptionPane.showMessageDialog(null, warningMsg.toString(),
                    "低库存预警", JOptionPane.WARNING_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "获取低库存信息失败: " + e.getMessage());
        }
    }

    /**
     * 刷新库存界面
     * 公开方法，供其他控制器调用
     */
    public void refreshView() {
        loadInventory();
    }

    /**
     * 刷新商品界面
     * 私有方法，在库存调整后调用，保持两个界面数据同步
     */
    private void refreshProductPanel() {
        if (productController != null) {
            // 调用商品控制器的刷新方法
            productController.refreshView();
            System.out.println("商品界面已刷新");
        } else {
            System.err.println("商品控制器未设置，无法刷新商品界面");
        }
    }

    /**
     * 获取库存服务（用于测试）
     *
     * @return 库存服务实例
     */
    public InventoryServiceInterface getInventoryService() {
        return inventoryService;
    }
}