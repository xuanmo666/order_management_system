package model.service;

import model.entity.Inventory;
import model.repository.InventoryRepository;
import exception.ValidationException;
import exception.BusinessException;

import java.util.List;
import java.util.Map;

/**
 * 库存业务逻辑服务类
 */
public class InventoryService {
    private InventoryRepository inventoryRepository;

    public InventoryService() {
        this.inventoryRepository = new InventoryRepository();
    }

    /**
     * 添加库存记录
     */
    public void addInventory(Inventory inventory) throws ValidationException {
        validateInventory(inventory);

        if (inventoryRepository.exists(inventory.getProductId())) {
            throw new ValidationException("库存记录已存在: " + inventory.getProductId());
        }

        boolean success = inventoryRepository.add(inventory);
        if (!success) {
            throw new ValidationException("添加库存记录失败");
        }
    }

    /**
     * 更新库存
     */
    public void updateInventory(Inventory inventory) throws ValidationException {
        validateInventory(inventory);

        if (!inventoryRepository.exists(inventory.getProductId())) {
            throw new ValidationException("库存记录不存在: " + inventory.getProductId());
        }

        boolean success = inventoryRepository.update(inventory);
        if (!success) {
            throw new ValidationException("更新库存失败");
        }
    }

    /**
     * 调整库存数量
     */
    public void adjustInventory(String productId, int amount, String operation)
            throws ValidationException, BusinessException {
        if (!util.ValidationUtil.isNotBlank(productId)) {
            throw new ValidationException("商品ID不能为空");
        }

        Inventory inventory = inventoryRepository.findById(productId);
        if (inventory == null) {
            throw new ValidationException("库存记录不存在: " + productId);
        }

        if ("in".equalsIgnoreCase(operation) || "increase".equalsIgnoreCase(operation)) {
            inventory.increase(amount);
        } else if ("out".equalsIgnoreCase(operation) || "decrease".equalsIgnoreCase(operation)) {
            inventory.decrease(amount);
        } else {
            throw new ValidationException("不支持的操作类型: " + operation);
        }

        inventoryRepository.update(inventory);
    }

    /**
     * 根据商品ID获取库存
     */
    public Inventory getInventoryByProductId(String productId) throws ValidationException {
        if (!util.ValidationUtil.isNotBlank(productId)) {
            throw new ValidationException("商品ID不能为空");
        }

        Inventory inventory = inventoryRepository.findById(productId);
        if (inventory == null) {
            throw new ValidationException("库存记录不存在: " + productId);
        }

        return inventory;
    }

    /**
     * 获取所有库存记录
     */
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    /**
     * 获取低库存预警列表
     */
    public List<Inventory> getLowStockItems() {
        return inventoryRepository.getLowStockItems();
    }

    /**
     * 获取库存统计信息
     */
    public Map<String, Object> getInventoryStatistics() {
        return inventoryRepository.getStatistics();
    }

    /**
     * 按库存数量排序
     */
    public List<Inventory> getInventorySortedByQuantity(boolean ascending) {
        List<Inventory> inventoryList = inventoryRepository.findAll();

        // 使用冒泡排序
        for (int i = 0; i < inventoryList.size() - 1; i++) {
            for (int j = 0; j < inventoryList.size() - i - 1; j++) {
                Inventory inv1 = inventoryList.get(j);
                Inventory inv2 = inventoryList.get(j + 1);

                boolean needSwap = ascending ?
                        inv1.getQuantity() > inv2.getQuantity() :
                        inv1.getQuantity() < inv2.getQuantity();

                if (needSwap) {
                    inventoryList.set(j, inv2);
                    inventoryList.set(j + 1, inv1);
                }
            }
        }

        return inventoryList;
    }

    // 私有方法：验证库存数据
    private void validateInventory(Inventory inventory) throws ValidationException {
        if (inventory == null) {
            throw new ValidationException("库存不能为空");
        }

        if (!util.ValidationUtil.isNotBlank(inventory.getProductId())) {
            throw new ValidationException("商品ID不能为空");
        }

        if (inventory.getQuantity() < 0) {
            throw new ValidationException("库存数量不能为负数");
        }

        if (inventory.getMinThreshold() < 0) {
            throw new ValidationException("最小库存阈值不能为负数");
        }

        if (inventory.getMaxCapacity() <= inventory.getMinThreshold()) {
            throw new ValidationException("最大库存容量必须大于最小阈值");
        }

        if (inventory.getQuantity() > inventory.getMaxCapacity()) {
            throw new ValidationException("库存数量不能超过最大容量");
        }
    }
}