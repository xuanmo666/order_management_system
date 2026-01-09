package controller;

import view.UserPanel;
import model.service.UserServiceInterface;
import model.entity.User;
import exception.ValidationException;
import util.IdGenerator;

import javax.swing.*;

/**
 * 用户管理控制器（仅管理员可用）
 * 负责处理用户相关的业务逻辑和界面交互
 * 注意：此控制器只对管理员角色可见，普通销售人员无法访问用户管理功能
 */
public class UserController {
    // 视图层：用户管理界面
    private UserPanel userPanel;

    // 服务层接口：用户服务，处理用户相关的业务逻辑
    private UserServiceInterface userService;

    /**
     * 构造方法：初始化用户控制器
     *
     * @param userPanel 用户管理界面
     * 注意：这里没有在构造函数中注入userService，因为MainController会在初始化后调用setUserService方法
     */
    public UserController(UserPanel userPanel) {
        this.userPanel = userPanel;
        // 设置界面的事件监听器
        setupEventListeners();
    }

    /**
     * 设置用户服务（由MainController调用）
     * 提供灵活的依赖注入方式
     *
     * @param userService 用户服务实例
     */
    public void setUserService(UserServiceInterface userService) {
        this.userService = userService;
    }

    /**
     * 设置界面事件监听器
     * 为用户界面的各种操作绑定相应的处理方法
     */
    private void setupEventListeners() {
        // 使用匿名内部类实现UserPanel中的接口
        userPanel.setActionListener(new UserPanel.UserActionListener() {
            /**
             * 处理添加用户按钮点击事件
             */
            @Override
            public void onAddUser() {
                handleAddUser();
            }

            /**
             * 处理编辑用户按钮点击事件
             *
             * @param selectedRow 表格中选中的行索引
             */
            @Override
            public void onEditUser(int selectedRow) {
                handleEditUser(selectedRow);
            }

            /**
             * 处理删除用户按钮点击事件
             *
             * @param selectedRow 表格中选中的行索引
             */
            @Override
            public void onDeleteUser(int selectedRow) {
                handleDeleteUser(selectedRow);
            }

            /**
             * 处理修改密码按钮点击事件
             *
             * @param selectedRow 表格中选中的行索引
             */
            @Override
            public void onChangePassword(int selectedRow) {
                handleChangePassword(selectedRow);
            }

            /**
             * 处理刷新按钮点击事件
             */
            @Override
            public void onRefresh() {
                loadUsers();
            }
        });
    }

    /**
     * 加载用户数据到界面
     * 从数据库获取所有用户数据，并显示在表格中
     */
    public void loadUsers() {
        try {
            // 检查服务是否已初始化（依赖注入是否完成）
            if (userService == null) {
                JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
                return;
            }

            // 清空表格中原有数据
            userPanel.clearTable();

            // 从用户服务获取所有用户
            var users = userService.getAllUsers();

            // 遍历每个用户记录
            for (User user : users) {
                // 创建表格行数据数组
                Object[] rowData = {
                        user.getId(),         // 用户ID
                        user.getName(),       // 用户名
                        user.getRole(),       // 用户角色（管理员/销售员）
                        "2024-01-01"         // 创建日期（简化处理，实际应该从数据库获取）
                };

                // 将行数据添加到表格中
                userPanel.addRowToTable(rowData);
            }

        } catch (Exception e) {
            // 捕获异常并显示错误信息
            JOptionPane.showMessageDialog(null, "加载用户失败: " + e.getMessage());
            e.printStackTrace(); // 在控制台打印异常堆栈，便于调试
        }
    }

    /**
     * 处理添加用户请求
     * 弹出对话框让管理员输入新用户信息，然后创建新用户
     */
    private void handleAddUser() {
        // 检查服务是否已初始化
        if (userService == null) {
            JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
            return;
        }

        // 使用UserPanel提供的对话框获取用户输入
        // 对话框会返回一个Object数组，包含用户名、密码和角色
        Object[] input = userPanel.showAddUserDialog();

        // 如果用户点击了"确定"按钮（input不为null）
        if (input != null) {
            try {
                // 从输入数组中获取数据
                String username = (String) input[0];
                String password = (String) input[1];
                String role = (String) input[2];

                // 创建用户对象
                User user = new User();
                user.setId(IdGenerator.generateUserId(username)); // 根据用户名生成唯一用户ID
                user.setName(username);
                user.setPassword(password);
                // 根据对话框选择的角色设置用户角色
                user.setRole("管理员".equals(role) ? User.ROLE_ADMIN : User.ROLE_SALES);

                // 调用用户服务添加用户
                userService.addUser(user);

                // 刷新用户界面显示最新数据
                loadUsers();

                // 提示管理员操作成功
                JOptionPane.showMessageDialog(null, "添加用户成功");

            } catch (ValidationException e) {
                // 处理业务逻辑验证失败（如用户名已存在）
                JOptionPane.showMessageDialog(null, "添加失败: " + e.getMessage());
            } catch (Exception e) {
                // 处理其他未知异常
                JOptionPane.showMessageDialog(null, "添加用户时发生错误: " + e.getMessage());
            }
        }
    }

    /**
     * 处理编辑用户请求
     * 管理员可以修改用户的角色（通常不允许修改用户名）
     *
     * @param selectedRow 表格中选中的行索引
     */
    private void handleEditUser(int selectedRow) {
        // 验证是否选择了有效的行
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要编辑的用户");
            return;
        }

        // 检查服务是否已初始化
        if (userService == null) {
            JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
            return;
        }

        try {
            // 从界面表格中获取选中行的数据
            Object[] rowData = userPanel.getRowData(selectedRow);
            String userId = (String) rowData[0];     // 用户ID
            String username = (String) rowData[1];   // 用户名
            String currentRole = (String) rowData[2]; // 当前角色

            // 显示编辑对话框，让管理员选择新的角色
            String newRole = userPanel.showEditUserDialog(username, currentRole);

            // 如果管理员点击了"确定"按钮（newRole不为null）
            if (newRole != null) {
                // 通过用户服务获取用户详细信息
                User user = userService.getUserById(userId);

                // 更新用户角色
                user.setRole("管理员".equals(newRole) ? User.ROLE_ADMIN : User.ROLE_SALES);

                // 调用用户服务保存更改
                userService.updateUser(user);

                // 刷新用户界面显示最新数据
                loadUsers();

                // 提示管理员操作成功
                JOptionPane.showMessageDialog(null, "更新用户成功");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "编辑失败: " + e.getMessage());
        }
    }

    /**
     * 处理删除用户请求
     * 管理员可以删除用户（通常需要确认，防止误操作）
     *
     * @param selectedRow 表格中选中的行索引
     */
    private void handleDeleteUser(int selectedRow) {
        // 验证是否选择了有效的行
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要删除的用户");
            return;
        }

        // 检查服务是否已初始化
        if (userService == null) {
            JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
            return;
        }

        try {
            // 从界面表格中获取选中行的数据
            Object[] rowData = userPanel.getRowData(selectedRow);
            String userId = (String) rowData[0];     // 用户ID
            String username = (String) rowData[1];   // 用户名

            // 显示确认删除对话框（防止误操作）
            boolean confirm = userPanel.showConfirmDeleteDialog(username);

            // 如果管理员确认删除
            if (confirm) {
                // 调用用户服务删除用户
                userService.deleteUser(userId);

                // 刷新用户界面显示最新数据
                loadUsers();

                // 提示管理员操作成功
                JOptionPane.showMessageDialog(null, "删除用户成功");
            }

        } catch (ValidationException e) {
            // 处理业务逻辑验证失败（如不能删除最后一个管理员）
            JOptionPane.showMessageDialog(null, "删除失败: " + e.getMessage());
        } catch (Exception e) {
            // 处理其他未知异常
            JOptionPane.showMessageDialog(null, "删除用户时发生错误: " + e.getMessage());
        }
    }

    /**
     * 处理修改密码请求
     * 管理员可以修改任意用户的密码
     *
     * @param selectedRow 表格中选中的行索引
     */
    private void handleChangePassword(int selectedRow) {
        // 验证是否选择了有效的行
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "请选择要修改密码的用户");
            return;
        }

        // 检查服务是否已初始化
        if (userService == null) {
            JOptionPane.showMessageDialog(null, "系统未初始化，请重新登录");
            return;
        }

        try {
            // 从界面表格中获取选中行的数据
            Object[] rowData = userPanel.getRowData(selectedRow);
            String userId = (String) rowData[0];     // 用户ID
            String username = (String) rowData[1];   // 用户名

            // 显示修改密码对话框
            Object[] input = userPanel.showChangePasswordDialog(username);

            // 如果管理员点击了"确定"按钮（input不为null）
            if (input != null) {
                String newPassword = (String) input[0]; // 新密码

                // 通过用户服务获取用户详细信息
                User user = userService.getUserById(userId);

                // 更新用户密码
                user.setPassword(newPassword);

                // 调用用户服务保存更改
                userService.updateUser(user);

                // 提示管理员操作成功
                JOptionPane.showMessageDialog(null, "修改密码成功");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "修改密码失败: " + e.getMessage());
        }
    }

    /**
     * 刷新用户界面
     * 公开方法，供其他控制器调用
     */
    public void refreshView() {
        loadUsers();
    }

    /**
     * 获取用户服务（用于测试）
     *
     * @return 用户服务实例
     */
    public UserServiceInterface getUserService() {
        return userService;
    }
}