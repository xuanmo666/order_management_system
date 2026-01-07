package controller;

import view.InventoryPanel;
import model.service.InventoryService;
import model.service.ProductService;

/**
 * 库存管理控制器 - 处理库存相关的业务逻辑和界面交互
 */
public class InventoryController {
    private InventoryPanel inventoryPanel;      // 库存管理界面
    private InventoryService inventoryService;  // 库存业务服务
    private ProductService productService;      // 商品业务服务

    /**
     * 构造函数
     */
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
        // 设置库存界面上所有按钮的事件监听器
    }

    /**
     * 加载库存数据到界面
     */
    public void loadInventory() {
        // 调用inventoryService获取所有库存记录
        // 将数据设置到inventoryPanel的表格中
        // 标记低库存商品
    }

    /**
     * 处理库存调整请求
     * @param productId 商品ID
     * @param quantity 调整数量
     * @param operation 操作类型（in/out/set）
     * @param reason 调整原因
     */
    public void handleAdjustInventory(String productId, int quantity,
                                      String operation, String reason) {
        // 调用inventoryService.adjustInventory方法
        // 处理库存不足等异常情况
        // 更新界面显示
    }

    /**
     * 处理低库存预警请求
     */
    public void handleLowStockWarning() {
        // 调用inventoryService.getLowStockItems方法
        // 在界面显示预警信息
    }

    /**
     * 处理库存盘点请求
     */
    public void handleInventoryCheck() {
        // 调用inventoryService.performInventoryCheck方法
        // 显示盘点报告
    }

    /**
     * 处理库存统计请求
     */
    public void handleGetInventoryStatistics() {
        // 调用inventoryService.getInventoryStatistics方法
        // 在界面显示统计信息
    }

    /**
     * 处理库存调拨请求
     * @param fromProductId 源商品ID
     * @param toProductId 目标商品ID
     * @param quantity 调拨数量
     * @param reason 调拨原因
     */
    public void handleTransferInventory(String fromProductId, String toProductId,
                                        int quantity, String reason) {
        // 调用inventoryService.transferInventory方法
        // 处理库存不足等异常
        // 更新界面显示
    }

    /**
     * 处理库存阈值设置请求
     * @param productId 商品ID
     * @param minThreshold 最小库存阈值
     * @param maxCapacity 最大库存容量
     */
    public void handleSetInventoryThreshold(String productId, int minThreshold,
                                            int maxCapacity) {
        // 调用inventoryService.setInventoryThreshold方法
        // 验证阈值设置是否合理
        // 更新界面显示
    }

    /**
     * 刷新库存界面
     */
    public void refreshView() {
        // 重新加载库存数据并更新界面
    }

    /**
     * 获取商品库存信息
     * @param productId 商品ID
     */
    public void getProductInventoryInfo(String productId) {
        // 调用inventoryService.getInventoryByProductId方法
        // 在界面显示库存详情
    }
}