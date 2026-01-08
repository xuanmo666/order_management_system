// view/LoginPanel.java
package view;

import javax.swing.*;
import java.awt.*;

/**
 * 登录面板 - 只负责显示登录界面
 * 1. 显示用户名和密码输入框
 * 2. 显示登录按钮
 * 3. 不验证数据，只收集用户输入
 */
public class LoginPanel extends JPanel {
    // 界面组件
    private JTextField usernameField;  // 用户名输入框
    private JPasswordField passwordField; // 密码输入框
    private JButton loginButton;       // 登录按钮

    // 监听器接口 - 等待Controller实现
    public interface LoginListener {
        /**
         * 当用户点击登录按钮时调用
         * @param username 输入的用户名
         * @param password 输入的密码
         */
        void onLogin(String username, String password);
    }

    private LoginListener loginListener; // 登录监听器

    /**
     * 构造函数 - 初始化登录界面
     */
    public LoginPanel() {
        initialize();
    }

    /**
     * 初始化界面组件
     */
    private void initialize() {
        // 设置布局
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 设置组件间距

        // 标题
        JLabel titleLabel = new JLabel("订单管理系统登录");
        titleLabel.setFont(new Font("宋体", Font.BOLD, 18));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // 用户名标签
        JLabel userLabel = new JLabel("用户名:");
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        add(userLabel, gbc);

        // 用户名输入框
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        add(usernameField, gbc);

        // 密码标签
        JLabel passLabel = new JLabel("密码:");
        gbc.gridy = 2;
        gbc.gridx = 0;
        add(passLabel, gbc);

        // 密码输入框
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // 登录按钮
        loginButton = new JButton("登录");
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(loginButton, gbc);

        // 设置登录按钮事件
        // 注意：这里只通知Controller，不处理业务逻辑
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // 如果有监听器，就通知它
            if (loginListener != null) {
                loginListener.onLogin(username, password);
            }
        });

        // 提示信息
        JLabel tipLabel = new JLabel("提示: 请输入用户名和密码，然后点击登录");
        tipLabel.setFont(new Font("宋体", Font.PLAIN, 12));
        tipLabel.setForeground(Color.GRAY);

        gbc.gridy = 4;
        add(tipLabel, gbc);
    }

    /**
     * 设置登录监听器
     * 注意：这个方法由Controller调用，用于接收登录事件
     * @param listener 登录监听器
     */
    public void setLoginListener(LoginListener listener) {
        this.loginListener = listener;
    }

    /**
     * 获取用户名输入框内容（供Controller使用）
     */
    public String getUsername() {
        return usernameField.getText();
    }

    /**
     * 清空输入框（供Controller使用）
     */
    public void clearInputs() {
        usernameField.setText("");
        passwordField.setText("");
    }
}