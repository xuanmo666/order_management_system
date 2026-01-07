package model.entity;



/**
 * 商品实体类 - 继承BaseEntity
 */
public class Product extends BaseEntity {
    private double price;          // 价格
    private String category;       // 分类
    private int stock;             // 库存数量

    // 构造方法
    public Product() {
        super();
    }

    public Product(String id, String name, double price, String category) {
        super(id, name);
        this.price = price;
        this.category = category;
        this.stock = 0;
    }

    // Getter和Setter方法
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // 增加库存
    public void increaseStock(int amount) {
        this.stock += amount;
    }

    // 减少库存
    public boolean decreaseStock(int amount) {
        if (stock >= amount) {
            stock -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("商品ID: %s, 名称: %s, 价格: %.2f, 分类: %s, 库存: %d",
                id, name, price, category, stock);
    }
}
