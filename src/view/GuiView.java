// view/GuiView.java
package view;

import javax.swing.*;
import java.awt.*;

/**
 * 主窗口类 - 只负责显示主界面框架
 * 1. 显示登录界面
 * 2. 显示主界面（包含标签页）
 * 3. 不处理任何业务逻辑
 */
public class GuiView {
    private JFrame mainFrame;          // 主窗口
    private JTabbedPane tabbedPane;    // 标签页容器

    // 各个功能面板
    private LoginPanel loginPanel;     // 登录面板
    private ProductPanel productPanel; // 商品管理面板
    private OrderPanel orderPanel;     // 订单管理面板
    private InventoryPanel inventoryPanel; // 库存管理面板
    private UserPanel userPanel; // 用户管理面板
    /**
     * 构造函数 - 初始化界面
     */
    public GuiView() {
        initialize();
    }

    /**
     * 初始化主窗口
     */
    private void initialize() {
        // 创建主窗口
        mainFrame = new JFrame("简易订单管理系统");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 500);  // 设置窗口大小
        mainFrame.setLocationRelativeTo(null); // 居中显示

        // 初始显示登录界面
        showLoginScreen();

        mainFrame.setVisible(true);
    }

    /**
     * 显示登录界面
     * 注意：这里只是显示界面，登录验证由Controller处理
     */
    public void showLoginScreen() {
        loginPanel = new LoginPanel();

        // 清空窗口内容
        mainFrame.getContentPane().removeAll();

        // 添加登录面板到窗口
        mainFrame.add(loginPanel);

        // 刷新显示
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    /**
     * 显示主界面（登录成功后调用）
     * @param username 用户名
     * @param role 用户角色（admin或sales）
     * 注意：这个方法应该由Controller在登录成功后调用
     */
    public void showMainScreen(String username, String role) {
        // 创建标签页容器
        tabbedPane = new JTabbedPane();

        // 创建各个功能面板
        productPanel = new ProductPanel();
        orderPanel = new OrderPanel();
        inventoryPanel = new InventoryPanel();

        // 添加面板到标签页
        tabbedPane.addTab("商品管理", productPanel);
        tabbedPane.addTab("订单管理", orderPanel);
        tabbedPane.addTab("库存管理", inventoryPanel);
        // 如果是管理员，添加用户管理面板
        if ("admin".equals(role)) {
            userPanel = new UserPanel();
            tabbedPane.addTab("用户管理", userPanel);
        }
        // 创建顶部状态栏
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        // 显示当前用户信息
        JLabel userLabel = new JLabel("当前用户: " + username + " (" + role + ")");
        topPanel.add(userLabel, BorderLayout.WEST);

        // 退出按钮
        JButton logoutBtn = new JButton("退出登录");
        topPanel.add(logoutBtn, BorderLayout.EAST);

        // 设置主界面布局
        mainFrame.getContentPane().removeAll();
        mainFrame.setLayout(new BorderLayout());

        mainFrame.add(topPanel, BorderLayout.NORTH);
        mainFrame.add(tabbedPane, BorderLayout.CENTER);

        // 刷新显示
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    /**
     * 显示消息对话框
     * @param title 对话框标题
     * @param message 消息内容
     * 注意：这个方法供Controller调用显示提示信息
     */
    public void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(mainFrame, message, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示错误对话框
     * @param title 对话框标题
     * @param error 错误信息
     * 注意：这个方法供Controller调用显示错误信息
     */
    public void showError(String title, String error) {
        JOptionPane.showMessageDialog(mainFrame, error, title,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 获取登录面板（供Controller设置监听器）
     */
    public LoginPanel getLoginPanel() {
        return loginPanel;
    }

    /**
     * 获取商品管理面板（供Controller设置监听器）
     */
    public ProductPanel getProductPanel() {
        return productPanel;
    }

    /**
     * 获取订单管理面板（供Controller设置监听器）
     */
    public OrderPanel getOrderPanel() {
        return orderPanel;
    }

    /**
     * 获取库存管理面板（供Controller设置监听器）
     */
    public InventoryPanel getInventoryPanel() {
        return inventoryPanel;
    }

    /**
     * 获取主窗口对象（供Controller使用）
     */
    public JFrame getMainFrame() {
        return mainFrame;
    }
    /**
     *  获取UserPanel的方法
     */
    public UserPanel getUserPanel() {
        return userPanel;
    }
}