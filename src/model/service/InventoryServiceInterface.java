package model.service;

import model.entity.Inventory;
import exception.ValidationException;
import exception.BusinessException;
import java.util.List;
import java.util.Map;

/**
 * 库存服务接口 - 供Controller层调用的契约
 */
public interface InventoryServiceInterface {
    // 库存管理
    void addInventory(Inventory inventory) throws ValidationException;
    void updateInventory(Inventory inventory) throws ValidationException;
    void adjustInventory(String productId, int amount, String operation)
            throws ValidationException, BusinessException;
    Inventory getInventoryByProductId(String productId) throws ValidationException;
    List<Inventory> getAllInventory();

    // 库存监控
    List<Inventory> getLowStockItems();
    boolean needsWarning(String productId) throws ValidationException;
    int getStockQuantity(String productId) throws ValidationException;

    // 统计和分析
    Map<String, Object> getInventoryStatistics();
    List<Inventory> getInventorySortedByQuantity(boolean ascending);

    // 库存验证
    boolean canStockIn(String productId, int amount) throws ValidationException;
    boolean canStockOut(String productId, int amount) throws ValidationException;
    boolean inventoryExists(String productId);
}