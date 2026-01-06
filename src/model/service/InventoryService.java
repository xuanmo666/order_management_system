package model.service;

import model.entity.Inventory;
import model.repository.InventoryRepository;

/**
 * 库存业务逻辑
 */
public class InventoryService {
    private InventoryRepository inventoryRepository;

    public void adjustInventory(String productId, int quantity, String operation) {
        // 入库/出库逻辑
        // 更新库存
        // 触发预警检查
    }

    public List<Inventory> getLowStockItems() {
        // 返回库存低于阈值的商品
        return null;
    }
}