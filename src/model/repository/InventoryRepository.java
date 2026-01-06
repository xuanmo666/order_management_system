package model.repository;

import model.entity.Inventory;

/**
 * 库存数据访问接口
 */
public class InventoryRepository {
    public Inventory findByProductId(String productId) {
        // 查询商品库存
        return null;
    }

    public boolean update(Inventory inventory) {
        // 更新库存
        return true;
    }
}