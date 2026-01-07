package model.repository;

import model.entity.Inventory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库存数据访问实现类 - 使用内存存储
 */
public class InventoryRepository implements Repository<Inventory> {
    // 使用Map存储库存数据，key为商品ID
    private Map<String, Inventory> inventoryMap = new HashMap<>();

    @Override
    public boolean add(Inventory inventory) {
        if (inventory == null || inventory.getProductId() == null) {
            return false;
        }
        inventoryMap.put(inventory.getProductId(), inventory);
        return true;
    }

    @Override
    public boolean delete(String productId) {
        if (inventoryMap.containsKey(productId)) {
            inventoryMap.remove(productId);
            return true;
        }
        return false;
    }

    @Override
    public Inventory findById(String productId) {
        return inventoryMap.get(productId);
    }

    @Override
    public List<Inventory> findAll() {
        return new ArrayList<>(inventoryMap.values());
    }

    @Override
    public boolean update(Inventory inventory) {
        if (inventory == null || inventory.getProductId() == null) {
            return false;
        }
        if (inventoryMap.containsKey(inventory.getProductId())) {
            inventoryMap.put(inventory.getProductId(), inventory);
            return true;
        }
        return false;
    }

    @Override
    public int count() {
        return inventoryMap.size();
    }

    @Override
    public boolean exists(String productId) {
        return inventoryMap.containsKey(productId);
    }

    // 特定于库存的查询方法

    /**
     * 获取低库存预警列表
     */
    public List<Inventory> getLowStockItems() {
        List<Inventory> result = new ArrayList<>();
        for (Inventory inventory : inventoryMap.values()) {
            if (inventory.needsWarning()) {
                result.add(inventory);
            }
        }
        return result;
    }

    /**
     * 根据库存数量范围查找
     */
    public List<Inventory> findByQuantityRange(int min, int max) {
        List<Inventory> result = new ArrayList<>();
        for (Inventory inventory : inventoryMap.values()) {
            int quantity = inventory.getQuantity();
            if (quantity >= min && quantity <= max) {
                result.add(inventory);
            }
        }
        return result;
    }

    /**
     * 获取库存统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        int totalItems = 0;
        int totalQuantity = 0;
        int lowStockCount = 0;

        for (Inventory inventory : inventoryMap.values()) {
            totalItems++;
            totalQuantity += inventory.getQuantity();
            if (inventory.needsWarning()) {
                lowStockCount++;
            }
        }

        stats.put("totalItems", totalItems);
        stats.put("totalQuantity", totalQuantity);
        stats.put("lowStockCount", lowStockCount);
        stats.put("averageQuantity", totalItems == 0 ? 0 : totalQuantity / totalItems);

        return stats;
    }
}