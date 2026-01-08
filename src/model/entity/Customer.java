package model.entity;

/**
 * 客户实体类 - 继承BaseEntity
 */
public class Customer extends BaseEntity {
    private String phone;      // 电话号码
    private String address;    // 地址
    private double totalSpent; // 总消费金额

    public Customer() {
        super();
        this.totalSpent = 0;
    }

    public Customer(String id, String name, String phone) {
        super(id, name);
        this.phone = phone;
        this.totalSpent = 0;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    // 增加消费金额
    public void addSpent(double amount) {
        this.totalSpent += amount;
    }

    @Override
    public String toString() {
        return String.format("客户ID: %s, 姓名: %s, 电话: %s, 总消费: %.2f",
                id, name, phone, totalSpent);
    }
}