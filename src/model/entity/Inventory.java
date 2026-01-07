package model.entity;

import exception.BusinessException;

/**
 * 库存实体类 - 记录商品的库存信息
 */
public class Inventory {
    private String productId;       // 商品ID
    private int quantity;           // 当前数量
    private int minThreshold;       // 最小库存阈值
    private int maxCapacity;        // 最大库存容量

    public Inventory() {
        this.minThreshold = 10;
        this.maxCapacity = 1000;
    }

    public Inventory(String productId) {
        this();
        this.productId = productId;
        this.quantity = 0;
    }

    // 检查是否需要预警
    public boolean needsWarning() {
        return quantity < minThreshold;
    }

    // 检查是否超出容量
    public boolean isOverCapacity(int addAmount) {
        return quantity + addAmount > maxCapacity;
    }

    // 增加库存
    public void increase(int amount) throws exception.ValidationException {
        if (amount <= 0) {
            throw new exception.ValidationException("增加数量必须大于0");
        }
        if (isOverCapacity(amount)) {
            throw new exception.ValidationException("超出最大库存容量");
        }
        this.quantity += amount;
    }

    // 减少库存
    public void decrease(int amount) throws exception.ValidationException, BusinessException {
        if (amount <= 0) {
            throw new exception.ValidationException("减少数量必须大于0");
        }
        if (quantity < amount) {
            throw new BusinessException("库存不足");
        }
        this.quantity -= amount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(int minThreshold) {
        this.minThreshold = minThreshold;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public String toString() {
        return String.format("商品ID: %s, 库存: %d, 阈值: %d, 容量: %d",
                productId, quantity, minThreshold, maxCapacity);
    }
}