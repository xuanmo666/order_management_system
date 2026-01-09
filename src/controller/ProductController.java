package controller;

import model.service.ProductService;
import view.ProductPanel;
import model.service.ProductServiceInterface;
import model.entity.Product;
import exception.ValidationException;
import util.IdGenerator;

import javax.swing.*;

/**
 * 商品管理控制器 - 处理商品相关的业务逻辑和界面交互
 * 遵循MVC设计模式，协调商品界面和商品模型之间的交互
 */
public class ProductController {
    // 视图层：商品管理界面
    private ProductPanel productPanel;

    // 服务层接口：商品服务，处理商品相关的业务逻辑
    private ProductServiceInterface productService;

    // 库存控制器引用：用于在商品操作后刷新库存界面
    private InventoryController inventoryController;

    /**
     * 构造方法重载1：用于MainController初始化时调用（此时可能还没有inventoryController）
     *
     * @param productPanel 商品管理界面
     * @param productService 商品服务实例
     */
    public ProductController(ProductPanel productPanel, ProductService productService) {
        this(productPanel, productService, null);
    }

    /**
     * 构造方法重载2：完整的构造方法（包含inventoryController）
     *
     * @param productPanel 商品管理界面
     * @param productService 商品服务实例
     * @param inventoryController 库存控制器实例
     */
    public ProductController(ProductPanel productPanel, ProductService productService,
                             InventoryController inventoryController) {
        // 注入依赖：将传入的对象赋值给类的成员变量
        this.productPanel = productPanel;
        this.productService = productService;
        this.inventoryController = inventoryController;

        // 设置界面的事件监听器
        setupEventListeners();
    }

    /**
     * 设置商品服务（由MainController调用）
     * 提供灵活的依赖注入方式
     *
     * @param productService 商品服务实例
     */
    public void setProductService(ProductServiceInterface productService) {
        this.productService = productService;
    }

    /**
     * 设置库存控制器（由MainController调用）
     * 建立与库存控制器的双向通信，保持数据同步
     *
     * @param inventoryController 库存控制器实例
     */
    public void setInventoryController(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
    }

    /**
     * 设置界面事件监听器
     * 为商品界面的各种操作绑定相应的处理方法
     */
    private void setupEventListeners() {
        // 使用匿名内部类实现ProductPanel中的接口
        productPanel.setActionListener(new ProductPanel.ProductActionListener() {
            /**
             * 处理添加商品按钮点击事件
             */
            @Override
            public void onAddProduct() {
                handleAddProduct();
            }

            /**
             * 处理编辑商品按钮点击事件
             *
             * @param selectedRow 表格中选中的行索引
             */
            @Override
            public void onEditProduct(int selectedRow) {
                handleEditProduct(selectedRow);
            }

            /**
             * 处理删除商品按钮点击事件
             *
             * @param selectedRow 表格中选中的行索引
             */
            @Override
            public void onDeleteProduct(int selectedRow) {
                handleDeleteProduct(selectedRow);
            }

            /**
             * 处理刷新按钮点击事件
             */
            @Override
            public void onRefresh() {
                loadProducts();
            }
        });
    }

    /**
     * 加载商品数据到界面
     * 从数据库获取所有商品数据，并显示在表格中
     */
    public void loadProducts() {
        try {
            // 检查服务是否已初始化（依赖注入是否完成）
            if (productService == null) {
                JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
                return;
            }

            // 清空表格中原有数据
            productPanel.clearTable();

            // 从商品服务获取所有商品
            var products = productService.getAllProducts();

            // 遍历每条商品记录
            for (Product product : products) {
                // 创建表格行数据数组
                Object[] rowData = {
                        product.getId(),      // 商品ID
                        product.getName(),    // 商品名称
                        product.getPrice(),   // 商品价格
                        product.getCategory(), // 商品分类
                        product.getStock()    // 商品库存
                };

                // 将行数据添加到表格中
                productPanel.addRowToTable(rowData);
            }

        } catch (Exception e) {
            // 捕获异常并显示错误信息
            JOptionPane.showMessageDialog(null, "加载商品失败: " + e.getMessage());
            e.printStackTrace(); // 在控制台打印异常堆栈，便于调试
        }
    }

    /**
     * 处理添加商品请求
     * 弹出对话框让用户输入商品信息，然后创建新商品
     * 成功后会刷新商品界面和库存界面
     */
    private void handleAddProduct() {
        // 检查服务是否已初始化
        if (productService == null) {
            JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
            return;
        }

        // 创建商品添加对话框的面板
        JPanel panel = new JPanel(new java.awt.GridLayout(4, 2, 5, 5));

        // 对话框组件
        JTextField nameField = new JTextField(10);      // 商品名称输入框
        JTextField priceField = new JTextField(10);     // 价格输入框
        JTextField categoryField = new JTextField(10);  // 分类输入框

        // 将组件添加到面板
        panel.add(new JLabel("商品名称:"));
        panel.add(nameField);
        panel.add(new JLabel("价格:"));
        panel.add(priceField);
        panel.add(new JLabel("分类:"));
        panel.add(categoryField);
        panel.add(new JLabel("初始库存:"));
        JTextField stockField = new JTextField("0", 10); // 库存输入框，默认值为0
        panel.add(stockField);

        // 显示对话框
        int result = JOptionPane.showConfirmDialog(
                null, panel, "添加商品",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // 如果用户点击"确定"
        if (result == JOptionPane.OK_OPTION) {
            try {
                // 获取并验证用户输入
                String name = nameField.getText().trim();
                String priceText = priceField.getText().trim();
                String category = categoryField.getText().trim();
                String stockText = stockField.getText().trim();

                // 检查必填字段是否为空
                if (name.isEmpty() || priceText.isEmpty() || category.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请填写所有必填字段");
                    return;
                }

                // 解析数字输入
                double price = Double.parseDouble(priceText);
                int stock = Integer.parseInt(stockText);

                // 验证价格合理性
                if (price <= 0) {
                    JOptionPane.showMessageDialog(null, "价格必须大于0");
                    return;
                }

                // 创建商品对象
                Product product = new Product();
                product.setId(IdGenerator.generateProductId()); // 生成唯一商品ID
                product.setName(name);
                product.setPrice(price);
                product.setCategory(category);
                product.setStock(stock);

                // 调用商品服务添加商品
                productService.addProduct(product);

                // 刷新商品界面显示最新数据
                loadProducts();

                // 关键修改：刷新库存界面（因为新增商品会创建对应的库存记录）
                if (inventoryController != null) {
                    inventoryController.refreshView();
                }

                // 提示用户操作成功
                JOptionPane.showMessageDialog(null, "添加商品成功");

            } catch (NumberFormatException e) {
                // 处理数字格式错误
                JOptionPane.showMessageDialog(null, "请输入正确的数字格式");
            } catch (ValidationException e) {
                // 处理业务逻辑验证失败
                JOptionPane.showMessageDialog(null, "添加失败: " + e.getMessage());
            } catch (Exception e) {
                // 处理其他未知异常
                JOptionPane.showMessageDialog(null, "添加商品时发生错误: " + e.getMessage());
            }
        }
    }

    /**
     * 处理编辑商品请求
     * 弹出对话框让用户修改选中的商品信息
     *
     * @param selectedRow 表格中选中的行索引
     */
    private void handleEditProduct(int selectedRow) {
        // 验证是否选择了有效的行
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要编辑的商品");
            return;
        }

        // 检查服务是否已初始化
        if (productService == null) {
            JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
            return;
        }

        try {
            // 从界面表格中获取选中行的数据
            Object[] rowData = productPanel.getRowData(selectedRow);
            String productId = (String) rowData[0];

            // 通过商品服务获取商品详细信息
            Product product = productService.getProductById(productId);

            // 创建商品编辑对话框的面板
            JPanel panel = new JPanel(new java.awt.GridLayout(4, 2, 5, 5));

            // 对话框组件
            JLabel idLabel = new JLabel(productId); // 商品ID不可编辑，只显示
            JTextField nameField = new JTextField(product.getName());
            JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
            JTextField categoryField = new JTextField(product.getCategory());

            // 将组件添加到面板
            panel.add(new JLabel("商品ID:"));
            panel.add(idLabel);
            panel.add(new JLabel("商品名称:"));
            panel.add(nameField);
            panel.add(new JLabel("价格:"));
            panel.add(priceField);
            panel.add(new JLabel("分类:"));
            panel.add(categoryField);

            // 显示对话框
            int result = JOptionPane.showConfirmDialog(
                    null, panel, "编辑商品",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            // 如果用户点击"确定"
            if (result == JOptionPane.OK_OPTION) {
                // 更新商品信息
                product.setName(nameField.getText().trim());
                product.setPrice(Double.parseDouble(priceField.getText().trim()));
                product.setCategory(categoryField.getText().trim());

                // 调用商品服务更新商品
                productService.updateProduct(product);

                // 刷新商品界面显示最新数据
                loadProducts();

                // 关键修改：刷新库存界面（因为商品信息变化可能影响库存显示）
                if (inventoryController != null) {
                    inventoryController.refreshView();
                }

                // 提示用户操作成功
                JOptionPane.showMessageDialog(null, "更新商品成功");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "编辑失败: " + e.getMessage());
        }
    }

    /**
     * 处理删除商品请求
     * 删除选中的商品，并更新相关界面
     *
     * @param selectedRow 表格中选中的行索引
     */
    private void handleDeleteProduct(int selectedRow) {
        // 验证是否选择了有效的行
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要删除的商品");
            return;
        }

        // 检查服务是否已初始化
        if (productService == null) {
            JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
            return;
        }

        try {
            // 确认删除操作（防止误操作）
            int confirm = JOptionPane.showConfirmDialog(
                    null, "确定要删除这个商品吗？",
                    "确认删除", JOptionPane.YES_NO_OPTION);

            // 如果用户确认删除
            if (confirm == JOptionPane.YES_OPTION) {
                // 从界面表格中获取选中行的数据
                Object[] rowData = productPanel.getRowData(selectedRow);
                String productId = (String) rowData[0];

                // 调用商品服务删除商品
                productService.deleteProduct(productId);

                // 刷新商品界面显示最新数据
                loadProducts();

                // 关键修改：刷新库存界面，移除已删除商品的库存记录
                if (inventoryController != null) {
                    inventoryController.refreshView();
                }

                // 提示用户操作成功
                JOptionPane.showMessageDialog(null, "删除商品成功");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "删除失败: " + e.getMessage());
        }
    }

    /**
     * 刷新商品界面
     * 公开方法，供其他控制器调用
     */
    public void refreshView() {
        loadProducts();
    }

    /**
     * 获取商品服务（用于测试）
     *
     * @return 商品服务实例
     */
    public ProductServiceInterface getProductService() {
        return productService;
    }
}