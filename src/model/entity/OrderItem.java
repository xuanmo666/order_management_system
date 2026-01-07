package model.entity;

/**
 * 订单项实体类 - 订单中的单个商品项
 */
public class OrderItem {
    private String productId;   // 商品ID
    private String productName; // 商品名称
    private double price;       // 单价
    private int quantity;       // 数量
    private double subtotal;    // 小计金额

    public OrderItem() {
    }

    public OrderItem(Product product, int quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.price = product.getPrice();
        this.quantity = quantity;
        calculateSubtotal();
    }

    // 计算小计金额
    public void calculateSubtotal() {
        this.subtotal = price * quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        calculateSubtotal();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    @Override
    public String toString() {
        return String.format("商品: %s, 数量: %d, 单价: %.2f, 小计: %.2f",
                productName, quantity, price, subtotal);
    }
}