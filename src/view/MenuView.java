package view;

/**
 * 菜单显示与用户输入处理
 */
public class MenuView {
    public void showLoginScreen() {
        System.out.println("=== 订单管理系统登录 ===");
    }

    public void showMainMenu() {
        System.out.println("1. 商品管理");
        System.out.println("2. 订单管理");
        System.out.println("3. 库存管理");
        System.out.println("4. 退出");
    }

    public String getUserInput() {
        // 获取用户输入
        return null;
    }
}