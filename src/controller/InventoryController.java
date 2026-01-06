package controller;

import model.service.InventoryService;
import view.ConsoleView;

/**
 * 库存管理控制器
 */
public class InventoryController {
    private InventoryService inventoryService;
    private ConsoleView consoleView;

    public void updateInventory(String productId, int quantity, String operation) {
        // 更新库存
    }

    public void checkLowStock() {
        // 检查库存预警
    }

    public void listAllInventory() {
        // 查询所有库存
    }
}