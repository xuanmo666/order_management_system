package model.service;

import model.entity.Inventory;
import model.entity.Product;
import model.repository.InventoryRepository;
import exception.ValidationException;
import exception.BusinessException;

import java.util.List;
import java.util.Map;

/**
 * 库存业务逻辑服务类
 */
public class InventoryService implements InventoryServiceInterface {
    private InventoryRepository inventoryRepository;
    private static InventoryService instance;

    private InventoryService() {
        this.inventoryRepository = new InventoryRepository();
    }

    public static synchronized InventoryService getInstance() {
        if (instance == null) {
            instance = new InventoryService();
        }
        return instance;
    }

    /**
     * 调整库存数量 - 修改此方法以同时更新Product的库存
     */
    @Override
    public void adjustInventory(String productId, int amount, String operation)
            throws ValidationException, BusinessException {
        if (!util.ValidationUtil.isNotBlank(productId)) {
            throw new ValidationException("商品ID不能为空");
        }

        Inventory inventory = inventoryRepository.findById(productId);
        if (inventory == null) {
            throw new ValidationException("库存记录不存在: " + productId);
        }

        // 保存旧的库存数量用于比较
        int oldQuantity = inventory.getQuantity();

        if ("in".equalsIgnoreCase(operation) || "increase".equalsIgnoreCase(operation)) {
            inventory.increase(amount);
        } else if ("out".equalsIgnoreCase(operation) || "decrease".equalsIgnoreCase(operation)) {
            inventory.decrease(amount);
        } else {
            throw new ValidationException("不支持的操作类型: " + operation);
        }

        boolean success = inventoryRepository.update(inventory);
        if (!success) {
            throw new ValidationException("调整库存失败");
        }

        // 关键修改：同步更新商品实体的库存
        syncProductStock(productId, inventory.getQuantity());
    }

    /**
     * 添加库存记录 - 同时更新Product的库存
     */
    @Override
    public void addInventory(Inventory inventory) throws ValidationException {
        validateInventory(inventory);

        if (inventoryRepository.exists(inventory.getProductId())) {
            throw new ValidationException("库存记录已存在: " + inventory.getProductId());
        }

        boolean success = inventoryRepository.add(inventory);
        if (!success) {
            throw new ValidationException("添加库存记录失败");
        }

        // 同步更新商品的库存
        syncProductStock(inventory.getProductId(), inventory.getQuantity());
    }

    /**
     * 删除库存记录 - 新增方法
     */
    public void deleteInventory(String productId) throws ValidationException {
        if (!util.ValidationUtil.isNotBlank(productId)) {
            throw new ValidationException("商品ID不能为空");
        }

        if (!inventoryRepository.exists(productId)) {
            throw new ValidationException("库存记录不存在: " + productId);
        }

        boolean success = inventoryRepository.delete(productId);
        if (!success) {
            throw new ValidationException("删除库存记录失败");
        }

        System.out.println("成功删除库存记录: " + productId);
    }

    /**
     * 删除与商品相关的库存记录 - 新增方法
     */
    public void deleteInventoryByProductId(String productId) {
        if (!util.ValidationUtil.isNotBlank(productId)) {
            return;
        }

        try {
            if (inventoryRepository.exists(productId)) {
                boolean success = inventoryRepository.delete(productId);
                if (success) {
                    System.out.println("成功删除商品对应的库存记录: " + productId);
                } else {
                    System.err.println("删除库存记录失败: " + productId);
                }
            } else {
                System.out.println("库存记录不存在，无需删除: " + productId);
            }
        } catch (Exception e) {
            System.err.println("删除库存记录时发生错误: " + productId + " - " + e.getMessage());
        }
    }

    /**
     * 更新库存 - 同时更新Product的库存
     */
    @Override
    public void updateInventory(Inventory inventory) throws ValidationException {
        validateInventory(inventory);

        if (!inventoryRepository.exists(inventory.getProductId())) {
            throw new ValidationException("库存记录不存在: " + inventory.getProductId());
        }

        boolean success = inventoryRepository.update(inventory);
        if (!success) {
            throw new ValidationException("更新库存失败");
        }

        // 同步更新商品的库存
        syncProductStock(inventory.getProductId(), inventory.getQuantity());
    }

    /**
     * 根据商品ID获取库存
     */
    @Override
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
    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    /**
     * 获取低库存预警列表
     */
    @Override
    public List<Inventory> getLowStockItems() {
        return inventoryRepository.getLowStockItems();
    }

    /**
     * 获取库存统计信息
     */
    @Override
    public Map<String, Object> getInventoryStatistics() {
        return inventoryRepository.getStatistics();
    }

    /**
     * 按库存数量排序
     */
    @Override
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

    /**
     * 同步更新商品的库存数量
     * 关键修改：通过ProductService来更新商品库存，确保使用相同的Repository实例
     */
    private void syncProductStock(String productId, int newQuantity) {
        try {
            // 通过ProductService获取商品服务实例
            ProductService productService = ProductService.getInstance();

            // 获取商品
            Product product = productService.getProductById(productId);
            if (product != null) {
                // 更新商品库存
                product.setStock(newQuantity);
                // 使用ProductService更新商品，确保使用相同的Repository
                productService.updateProduct(product);
                System.out.println("成功同步商品库存: " + productId + " -> " + newQuantity);
            } else {
                System.err.println("同步失败: 商品不存在 - " + productId);
            }

        } catch (ValidationException e) {
            System.err.println("同步商品库存失败(验证错误): " + productId + " - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("同步商品库存失败: " + productId + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean needsWarning(String productId) throws ValidationException {
        Inventory inventory = getInventoryByProductId(productId);
        return inventory.needsWarning();
    }

    @Override
    public int getStockQuantity(String productId) throws ValidationException {
        Inventory inventory = getInventoryByProductId(productId);
        return inventory.getQuantity();
    }

    @Override
    public boolean canStockIn(String productId, int amount) throws ValidationException {
        Inventory inventory = getInventoryByProductId(productId);
        return !inventory.isOverCapacity(amount);
    }

    @Override
    public boolean canStockOut(String productId, int amount) throws ValidationException {
        Inventory inventory = getInventoryByProductId(productId);
        return inventory.getQuantity() >= amount;
    }

    @Override
    public boolean inventoryExists(String productId) {
        return inventoryRepository.exists(productId);
    }

    public InventoryRepository getInventoryRepository() {
        return inventoryRepository;
    }

    public void setInventoryRepository(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
}