// view/UserPanel.java
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * 用户管理面板 - 只负责显示用户管理界面
 * 注意：这个面板只有管理员角色才能看到
 * 1. 显示用户列表表格
 * 2. 显示操作按钮（添加、编辑、删除、修改密码）
 * 3. 不处理业务逻辑，只收集用户操作
 */
public class UserPanel extends JPanel {
    // 界面组件
    private JTable userTable;          // 用户表格
    private DefaultTableModel tableModel; // 表格数据模型

    // 操作按钮
    private JButton addButton;         // 添加用户按钮
    private JButton editButton;        // 编辑用户按钮
    private JButton deleteButton;      // 删除用户按钮
    private JButton changePwdButton;   // 修改密码按钮
    private JButton refreshButton;     // 刷新按钮

    // 监听器接口 - 等待Controller实现
    public interface UserActionListener {
        /**
         * 当点击添加用户按钮时调用
         */
        void onAddUser();

        /**
         * 当点击编辑用户按钮时调用
         * @param selectedRow 选中的行号
         */
        void onEditUser(int selectedRow);

        /**
         * 当点击删除用户按钮时调用
         * @param selectedRow 选中的行号
         */
        void onDeleteUser(int selectedRow);

        /**
         * 当点击修改密码按钮时调用
         * @param selectedRow 选中的行号
         */
        void onChangePassword(int selectedRow);

        /**
         * 当点击刷新按钮时调用
         */
        void onRefresh();
    }

    private UserActionListener actionListener; // 操作监听器

    /**
     * 构造函数 - 初始化用户管理界面
     */
    public UserPanel() {
        initialize();
    }

    /**
     * 初始化界面组件
     */
    private void initialize() {
        // 设置布局
        setLayout(new BorderLayout(5, 5));

        // 创建顶部按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 创建按钮
        addButton = new JButton("添加用户");
        editButton = new JButton("编辑用户");
        deleteButton = new JButton("删除用户");
        changePwdButton = new JButton("修改密码");
        refreshButton = new JButton("刷新");

        // 添加到按钮面板
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(changePwdButton);
        buttonPanel.add(refreshButton);

        // 设置按钮事件
        // 注意：这里只通知Controller，不处理业务逻辑
        addButton.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onAddUser();
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow >= 0 && actionListener != null) {
                actionListener.onEditUser(selectedRow);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow >= 0 && actionListener != null) {
                actionListener.onDeleteUser(selectedRow);
            }
        });

        changePwdButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow >= 0 && actionListener != null) {
                actionListener.onChangePassword(selectedRow);
            }
        });

        refreshButton.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onRefresh();
            }
        });

        // 创建表格
        // 列名：用户ID、用户名、角色、创建时间
        String[] columns = {"用户ID", "用户名", "角色", "创建时间"};

        // 创建表格模型
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };

        // 创建表格
        userTable = new JTable(tableModel);

        // 设置表格只能单选
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 设置列宽
        userTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID列
        userTable.getColumnModel().getColumn(1).setPreferredWidth(100); // 用户名列
        userTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // 角色列
        userTable.getColumnModel().getColumn(3).setPreferredWidth(150); // 时间列

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(userTable);

        // 添加到主面板
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 底部状态标签
        JLabel statusLabel = new JLabel("提示: 此功能仅管理员可用，请谨慎操作用户数据");
        statusLabel.setForeground(Color.BLUE);
        add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * 设置操作监听器
     * 注意：这个方法由Controller调用，用于接收用户操作事件
     * @param listener 操作监听器
     */
    public void setActionListener(UserActionListener listener) {
        this.actionListener = listener;
    }

    /**
     * 清空表格数据（供Controller使用）
     */
    public void clearTable() {
        tableModel.setRowCount(0);
    }

    /**
     * 向表格添加一行数据（供Controller使用）
     * @param rowData 行数据数组
     */
    public void addRowToTable(Object[] rowData) {
        tableModel.addRow(rowData);
    }

    /**
     * 获取选中的行号（供Controller使用）
     * @return 选中的行号，如果没有选中返回-1
     */
    public int getSelectedRow() {
        return userTable.getSelectedRow();
    }

    /**
     * 获取指定行的数据（供Controller使用）
     * @param row 行号
     * @return 该行的数据数组
     */
    public Object[] getRowData(int row) {
        int columnCount = tableModel.getColumnCount();
        Object[] rowData = new Object[columnCount];

        for (int i = 0; i < columnCount; i++) {
            rowData[i] = tableModel.getValueAt(row, i);
        }

        return rowData;
    }

    /**
     * 显示添加用户对话框（供Controller调用）
     * 注意：这里只显示对话框，业务逻辑由Controller处理
     * @return 用户输入的数组 [用户名, 密码, 角色]，如果取消返回null
     */
    public Object[] showAddUserDialog() {
        // 创建自定义对话框
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));

        // 创建输入组件
        JTextField usernameField = new JTextField(10);
        JPasswordField passwordField = new JPasswordField(10);
        JPasswordField confirmField = new JPasswordField(10);
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"销售员", "管理员"});

        // 添加到面板
        panel.add(new JLabel("用户名:"));
        panel.add(usernameField);
        panel.add(new JLabel("密码:"));
        panel.add(passwordField);
        panel.add(new JLabel("确认密码:"));
        panel.add(confirmField);
        panel.add(new JLabel("角色:"));
        panel.add(roleComboBox);

        // 显示对话框
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "添加新用户",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // 如果用户点击确定
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            // 检查必填字段
            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "所有字段都必须填写！", "错误", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            // 检查密码是否一致
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "两次输入的密码不一致！", "错误", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            // 返回用户输入的数据
            return new Object[]{username, password, role};
        }

        // 用户取消操作
        return null;
    }

    /**
     * 显示编辑用户对话框（供Controller调用）
     * @param currentUsername 当前用户名
     * @param currentRole 当前角色
     * @return 用户修改后的角色，如果取消返回null
     */
    public String showEditUserDialog(String currentUsername, String currentRole) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));

        panel.add(new JLabel("用户名:"));
        panel.add(new JLabel(currentUsername)); // 用户名不可修改
        panel.add(new JLabel("角色:"));

        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"销售员", "管理员"});
        roleComboBox.setSelectedItem(currentRole);
        panel.add(roleComboBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "编辑用户信息",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return (String) roleComboBox.getSelectedItem();
        }

        return null;
    }

    /**
     * 显示修改密码对话框（供Controller调用）
     * @param username 要修改密码的用户名
     * @return 包含新旧密码的数组 [旧密码, 新密码]，如果取消返回null
     */
    public Object[] showChangePasswordDialog(String username) {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        panel.add(new JLabel("用户名:"));
        panel.add(new JLabel(username));
        panel.add(new JLabel("新密码:"));
        JPasswordField newPasswordField = new JPasswordField(10);
        panel.add(newPasswordField);
        panel.add(new JLabel("确认新密码:"));
        JPasswordField confirmField = new JPasswordField(10);
        panel.add(confirmField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "修改密码 - " + username,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String newPassword = new String(newPasswordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "新密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            if (!newPassword.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "两次输入的新密码不一致！", "错误", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return new Object[]{newPassword};
        }

        return null;
    }

    /**
     * 显示确认删除对话框（供Controller调用）
     * @param username 要删除的用户名
     * @return true表示确认删除，false表示取消
     */
    public boolean showConfirmDeleteDialog(String username) {
        int result = JOptionPane.showConfirmDialog(
                this,
                "确定要删除用户 '" + username + "' 吗？\n此操作不可撤销！",
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        return result == JOptionPane.YES_OPTION;
    }
}