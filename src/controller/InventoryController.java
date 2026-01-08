package controller;

import view.InventoryPanel;
import model.service.InventoryService;
import model.service.ProductService;
import model.entity.Inventory;
import exception.ValidationException;
import exception.BusinessException;

import javax.swing.*;

/**
 * 库存管理控制器
 */
public class InventoryController {
    private InventoryPanel inventoryPanel;
    private InventoryService inventoryService;
    private ProductService productService;

    public InventoryController(InventoryPanel inventoryPanel,
                               InventoryService inventoryService,
                               ProductService productService) {
        this.inventoryPanel = inventoryPanel;
        this.inventoryService = inventoryService;
        this.productService = productService;
        setupEventListeners();
    }

    /**
     * 设置界面事件监听器
     */
    private void setupEventListeners() {
        inventoryPanel.setActionListener(new InventoryPanel.InventoryActionListener() {
            @Override
            public void onLowStockWarning() {
                handleLowStockWarning();
            }

            @Override
            public void onAdjustInventory(int selectedRow) {
                handleAdjustInventory(selectedRow);
            }

            @Override
            public void onRefresh() {
                loadInventory();
            }
        });
    }

    /**
     * 加载库存数据到界面
     */
    public void loadInventory() {
        try {
            inventoryPanel.clearTable();

            var inventoryList = inventoryService.getAllInventory();

            for (Inventory inventory : inventoryList) {
                // 获取商品名称
                String productName = "未知";
                try {
                    var product = productService.getProductById(inventory.getProductId());
                    productName = product.getName();
                } catch (Exception e) {
                    // 商品可能已被删除
                }

                // 判断库存状态
                String status = "正常";
                if (inventory.needsWarning()) {
                    status = "低库存预警";
                }

                Object[] rowData = {
                        inventory.getProductId(),
                        productName,
                        inventory.getQuantity(),
                        inventory.getMinThreshold(),
                        status
                };
                inventoryPanel.addRowToTable(rowData);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "加载库存失败: " + e.getMessage());
        }
    }

    /**
     * 处理库存调整请求
     */
    private void handleAdjustInventory(int selectedRow) {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要调整的库存记录");
            return;
        }

        try {
            // 获取库存信息
            Object[] rowData = inventoryPanel.getRowData(selectedRow);
            String productId = (String) rowData[0];

            // 调整库存对话框
            JPanel panel = new JPanel(new java.awt.GridLayout(3, 2, 5, 5));

            JLabel productIdLabel = new JLabel(productId);
            JComboBox<String> operationCombo = new JComboBox<>(new String[]{"入库", "出库"});
            JTextField quantityField = new JTextField("1", 10);

            panel.add(new JLabel("商品ID:"));
            panel.add(productIdLabel);
            panel.add(new JLabel("操作类型:"));
            panel.add(operationCombo);
            panel.add(new JLabel("数量:"));
            panel.add(quantityField);

            int result = JOptionPane.showConfirmDialog(
                    null, panel, "调整库存",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String operation = operationCombo.getSelectedItem().toString();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                String operationCode = "入库".equals(operation) ? "in" : "out";

                // 执行库存调整
                inventoryService.adjustInventory(productId, quantity, operationCode);

                // 刷新界面
                loadInventory();
                JOptionPane.showMessageDialog(null, "库存调整成功");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "请输入正确的数量");
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(null, "调整库存失败: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "调整库存时发生错误: " + e.getMessage());
        }

    }

    /**
     * 处理低库存预警请求
     */
    private void handleLowStockWarning() {
        try {
            var lowStockItems = inventoryService.getLowStockItems();

            if (lowStockItems.isEmpty()) {
                JOptionPane.showMessageDialog(null, "当前没有低库存商品");
                return;
            }

            StringBuilder warningMsg = new StringBuilder("低库存预警商品:\n\n");

            for (Inventory inventory : lowStockItems) {
                String productName = "未知";
                try {
                    var product = productService.getProductById(inventory.getProductId());
                    productName = product.getName();
                } catch (Exception e) {
                    // 商品可能已被删除
                }

                warningMsg.append("商品: ").append(productName)
                        .append(" (").append(inventory.getProductId()).append(")\n")
                        .append("当前库存: ").append(inventory.getQuantity())
                        .append(", 阈值: ").append(inventory.getMinThreshold())
                        .append("\n\n");
            }

            JOptionPane.showMessageDialog(null, warningMsg.toString(),
                    "低库存预警", JOptionPane.WARNING_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "获取低库存信息失败: " + e.getMessage());
        }
    }

    /**
     * 刷新库存界面
     */
    public void refreshView() {
        loadInventory();
    }
}