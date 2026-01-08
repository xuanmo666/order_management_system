package controller;

import view.UserPanel;
import model.service.UserService;
import model.entity.User;
import exception.ValidationException;
import util.IdGenerator;

import javax.swing.*;

/**
 * 用户管理控制器（仅管理员可用）
 */
public class UserController {
    private UserPanel userPanel;
    private UserService userService;

    public UserController(UserPanel userPanel, UserService userService) {
        this.userPanel = userPanel;
        this.userService = userService;
        setupEventListeners();
    }

    /**
     * 设置界面事件监听器
     */
    private void setupEventListeners() {
        userPanel.setActionListener(new UserPanel.UserActionListener() {
            @Override
            public void onAddUser() {
                handleAddUser();
            }

            @Override
            public void onEditUser(int selectedRow) {
                handleEditUser(selectedRow);
            }

            @Override
            public void onDeleteUser(int selectedRow) {
                handleDeleteUser(selectedRow);
            }

            @Override
            public void onChangePassword(int selectedRow) {
                handleChangePassword(selectedRow);
            }

            @Override
            public void onRefresh() {
                loadUsers();
            }
        });
    }

    /**
     * 加载用户数据到界面
     */
    public void loadUsers() {
        try {
            userPanel.clearTable();

            var users = userService.getAllUsers();

            for (User user : users) {
                Object[] rowData = {
                        user.getId(),
                        user.getName(),
                        user.getRole(),
                        "2024-01-01" // 简化处理，实际应该从数据库获取
                };
                userPanel.addRowToTable(rowData);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "加载用户失败: " + e.getMessage());
        }
    }

    /**
     * 处理添加用户请求
     */
    private void handleAddUser() {
        // 使用UserPanel提供的对话框
        Object[] input = userPanel.showAddUserDialog();

        if (input != null) {
            try {
                String username = (String) input[0];
                String password = (String) input[1];
                String role = (String) input[2];

                // 创建用户对象
                User user = new User();
                user.setId(IdGenerator.generateUserId(username));
                user.setName(username);
                user.setPassword(password);
                user.setRole("管理员".equals(role) ? User.ROLE_ADMIN : User.ROLE_SALES);

                // 添加用户
                userService.addUser(user);

                // 刷新界面
                loadUsers();
                JOptionPane.showMessageDialog(null, "添加用户成功");

            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(null, "添加失败: " + e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "添加用户时发生错误: " + e.getMessage());
            }
        }
    }

    /**
     * 处理编辑用户请求
     */
    private void handleEditUser(int selectedRow) {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要编辑的用户");
            return;
        }

        try {
            // 获取当前用户信息
            Object[] rowData = userPanel.getRowData(selectedRow);
            String userId = (String) rowData[0];
            String username = (String) rowData[1];
            String currentRole = (String) rowData[2];

            // 显示编辑对话框
            String newRole = userPanel.showEditUserDialog(username, currentRole);

            if (newRole != null) {
                // 获取用户对象
                User user = userService.getUserById(userId);

                // 更新角色
                user.setRole("管理员".equals(newRole) ? User.ROLE_ADMIN : User.ROLE_SALES);

                // 保存更改
                userService.updateUser(user);

                // 刷新界面
                loadUsers();
                JOptionPane.showMessageDialog(null, "更新用户成功");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "编辑失败: " + e.getMessage());
        }
    }

    /**
     * 处理删除用户请求
     */
    private void handleDeleteUser(int selectedRow) {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要删除的用户");
            return;
        }

        try {
            // 获取用户信息
            Object[] rowData = userPanel.getRowData(selectedRow);
            String userId = (String) rowData[0];
            String username = (String) rowData[1];

            // 确认删除
            boolean confirm = userPanel.showConfirmDeleteDialog(username);

            if (confirm) {
                // 删除用户
                userService.deleteUser(userId);

                // 刷新界面
                loadUsers();
                JOptionPane.showMessageDialog(null, "删除用户成功");
            }

        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(null, "删除失败: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "删除用户时发生错误: " + e.getMessage());
        }
    }

    /**
     * 处理修改密码请求
     */
    private void handleChangePassword(int selectedRow) {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要修改密码的用户");
            return;
        }

        try {
            // 获取用户信息
            Object[] rowData = userPanel.getRowData(selectedRow);
            String userId = (String) rowData[0];
            String username = (String) rowData[1];

            // 显示修改密码对话框
            Object[] input = userPanel.showChangePasswordDialog(username);

            if (input != null) {
                String newPassword = (String) input[0];

                // 获取用户对象
                User user = userService.getUserById(userId);

                // 更新密码
                user.setPassword(newPassword);
                userService.updateUser(user);

                JOptionPane.showMessageDialog(null, "修改密码成功");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "修改密码失败: " + e.getMessage());
        }
    }

    /**
     * 刷新用户界面
     */
    public void refreshView() {
        loadUsers();
    }
}