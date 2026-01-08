package controller;

import view.ProductPanel;
import model.service.ProductService;
import model.entity.Product;
import exception.ValidationException;
import util.IdGenerator;

import javax.swing.*;

/**
 * 商品管理控制器 - 处理商品相关的业务逻辑和界面交互
 */
public class ProductController {
    private ProductPanel productPanel;
    private ProductService productService;

    public ProductController(ProductPanel productPanel, ProductService productService) {
        this.productPanel = productPanel;
        this.productService = productService;
        setupEventListeners();
    }

    /**
     * 设置界面事件监听器
     */
    private void setupEventListeners() {
        productPanel.setActionListener(new ProductPanel.ProductActionListener() {
            @Override
            public void onAddProduct() {
                handleAddProduct();
            }

            @Override
            public void onEditProduct(int selectedRow) {
                handleEditProduct(selectedRow);
            }

            @Override
            public void onDeleteProduct(int selectedRow) {
                handleDeleteProduct(selectedRow);
            }

            @Override
            public void onRefresh() {
                loadProducts();
            }
        });
    }

    /**
     * 加载商品数据到界面
     */
    public void loadProducts() {
        try {
            productPanel.clearTable();

            // 获取所有商品
            var products = productService.getAllProducts();

            // 添加到表格
            for (Product product : products) {
                Object[] rowData = {
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getCategory(),
                        product.getStock()
                };
                productPanel.addRowToTable(rowData);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "加载商品失败: " + e.getMessage());
        }
    }

    /**
     * 处理添加商品请求
     */
    private void handleAddProduct() {
        // 显示添加对话框
        JPanel panel = new JPanel(new java.awt.GridLayout(4, 2, 5, 5));

        JTextField nameField = new JTextField(10);
        JTextField priceField = new JTextField(10);
        JTextField categoryField = new JTextField(10);

        panel.add(new JLabel("商品名称:"));
        panel.add(nameField);
        panel.add(new JLabel("价格:"));
        panel.add(priceField);
        panel.add(new JLabel("分类:"));
        panel.add(categoryField);
        panel.add(new JLabel("初始库存:"));
        JTextField stockField = new JTextField("0", 10);
        panel.add(stockField);

        int result = JOptionPane.showConfirmDialog(
                null, panel, "添加商品",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // 验证输入
                String name = nameField.getText().trim();
                String priceText = priceField.getText().trim();
                String category = categoryField.getText().trim();
                String stockText = stockField.getText().trim();

                if (name.isEmpty() || priceText.isEmpty() || category.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请填写所有必填字段");
                    return;
                }

                double price = Double.parseDouble(priceText);
                int stock = Integer.parseInt(stockText);

                if (price <= 0) {
                    JOptionPane.showMessageDialog(null, "价格必须大于0");
                    return;
                }

                // 创建商品对象
                Product product = new Product();
                product.setId(IdGenerator.generateProductId());
                product.setName(name);
                product.setPrice(price);
                product.setCategory(category);
                product.setStock(stock);

                // 添加商品
                productService.addProduct(product);

                // 刷新界面
                loadProducts();
                JOptionPane.showMessageDialog(null, "添加商品成功");

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "请输入正确的数字格式");
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(null, "添加失败: " + e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "添加商品时发生错误: " + e.getMessage());
            }
        }
    }

    /**
     * 处理编辑商品请求
     */
    private void handleEditProduct(int selectedRow) {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要编辑的商品");
            return;
        }

        try {
            // 获取当前商品信息
            Object[] rowData = productPanel.getRowData(selectedRow);
            String productId = (String) rowData[0];

            // 获取商品对象
            Product product = productService.getProductById(productId);

            // 显示编辑对话框
            JPanel panel = new JPanel(new java.awt.GridLayout(4, 2, 5, 5));

            JLabel idLabel = new JLabel(productId);
            JTextField nameField = new JTextField(product.getName());
            JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
            JTextField categoryField = new JTextField(product.getCategory());

            panel.add(new JLabel("商品ID:"));
            panel.add(idLabel);
            panel.add(new JLabel("商品名称:"));
            panel.add(nameField);
            panel.add(new JLabel("价格:"));
            panel.add(priceField);
            panel.add(new JLabel("分类:"));
            panel.add(categoryField);

            int result = JOptionPane.showConfirmDialog(
                    null, panel, "编辑商品",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                // 更新商品信息
                product.setName(nameField.getText().trim());
                product.setPrice(Double.parseDouble(priceField.getText().trim()));
                product.setCategory(categoryField.getText().trim());

                productService.updateProduct(product);

                // 刷新界面
                loadProducts();
                JOptionPane.showMessageDialog(null, "更新商品成功");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "编辑失败: " + e.getMessage());
        }
    }

    /**
     * 处理删除商品请求
     */
    private void handleDeleteProduct(int selectedRow) {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要删除的商品");
            return;
        }

        try {
            // 确认删除
            int confirm = JOptionPane.showConfirmDialog(
                    null, "确定要删除这个商品吗？",
                    "确认删除", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // 获取商品ID
                Object[] rowData = productPanel.getRowData(selectedRow);
                String productId = (String) rowData[0];

                // 删除商品
                productService.deleteProduct(productId);

                // 刷新界面
                loadProducts();
                JOptionPane.showMessageDialog(null, "删除商品成功");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "删除失败: " + e.getMessage());
        }
    }

    /**
     * 刷新商品界面
     */
    public void refreshView() {
        loadProducts();
    }
}