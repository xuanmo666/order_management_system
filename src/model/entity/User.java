package model.entity;

/**
 * 用户实体类 - 系统用户（管理员/销售员）
 */
public class User extends BaseEntity {
    private String password;    // 密码
    private String role;        // 角色：admin（管理员）、sales（销售员）

    // 角色常量
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_SALES = "sales";

    public User() {
        super();
    }

    public User(String id, String name, String password, String role) {
        super(id, name);
        this.password = password;
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // 检查是否是管理员
    public boolean isAdmin() {
        return ROLE_ADMIN.equals(role);
    }

    // 检查是否是销售员
    public boolean isSales() {
        return ROLE_SALES.equals(role);
    }

    @Override
    public String toString() {
        return String.format("用户ID: %s, 用户名: %s, 角色: %s", id, name, role);
    }
}