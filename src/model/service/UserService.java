package model.service;

import model.entity.User;
import model.repository.UserRepository;
import exception.ValidationException;
import util.ValidationUtil;

import java.util.List;

/**
 * 用户业务逻辑服务类
 * 负责用户认证、权限管理等
 */
public class UserService {
    private UserRepository userRepository;

    // 权限检查器接口 - 通过接口实现解耦
    public interface PermissionChecker {
        boolean hasPermission(User user, String resource, String action);
    }

    // 默认权限检查器实现
    private PermissionChecker permissionChecker = new DefaultPermissionChecker();

    public UserService() {
        this.userRepository = new UserRepository();
    }

    // 可以设置自定义的权限检查器
    public void setPermissionChecker(PermissionChecker checker) {
        this.permissionChecker = checker;
    }

    /**
     * 用户登录
     */
    public User login(String username, String password) throws ValidationException {
        if (!ValidationUtil.isNotBlank(username)) {
            throw new ValidationException("用户名不能为空");
        }

        if (!ValidationUtil.isNotBlank(password)) {
            throw new ValidationException("密码不能为空");
        }

        User user = userRepository.validateLogin(username, password);
        if (user == null) {
            throw new ValidationException("用户名或密码错误");
        }

        return user;
    }

    /**
     * 添加用户
     */
    public void addUser(User user) throws ValidationException {
        validateUser(user);

        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getName()) != null) {
            throw new ValidationException("用户名已存在: " + user.getName());
        }

        boolean success = userRepository.add(user);
        if (!success) {
            throw new ValidationException("添加用户失败");
        }
    }

    /**
     * 更新用户
     */
    public void updateUser(User user) throws ValidationException {
        validateUser(user);

        // 检查用户是否存在
        if (!userRepository.exists(user.getId())) {
            throw new ValidationException("用户不存在: " + user.getId());
        }

        boolean success = userRepository.update(user);
        if (!success) {
            throw new ValidationException("更新用户失败");
        }
    }

    /**
     * 删除用户
     */
    public void deleteUser(String userId) throws ValidationException {
        if (!ValidationUtil.isNotBlank(userId)) {
            throw new ValidationException("用户ID不能为空");
        }

        // 检查用户是否存在
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new ValidationException("用户不存在: " + userId);
        }

        // 不能删除管理员账号
        if (user.isAdmin()) {
            throw new ValidationException("不能删除管理员账号");
        }

        boolean success = userRepository.delete(userId);
        if (!success) {
            throw new ValidationException("删除用户失败");
        }
    }

    /**
     * 根据ID获取用户
     */
    public User getUserById(String userId) throws ValidationException {
        if (!ValidationUtil.isNotBlank(userId)) {
            throw new ValidationException("用户ID不能为空");
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new ValidationException("用户不存在: " + userId);
        }

        return user;
    }

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 根据角色获取用户
     */
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    /**
     * 检查用户权限
     */
    public boolean checkPermission(User user, String resource, String action) {
        if (user == null) {
            return false;
        }
        return permissionChecker.hasPermission(user, resource, action);
    }

    /**
     * 修改密码
     */
    public void changePassword(String userId, String oldPassword, String newPassword)
            throws ValidationException {
        User user = getUserById(userId);

        // 验证旧密码
        if (!oldPassword.equals(user.getPassword())) {
            throw new ValidationException("旧密码错误");
        }

        // 设置新密码
        user.setPassword(newPassword);
        updateUser(user);
    }

    // 私有方法：验证用户数据
    private void validateUser(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("用户不能为空");
        }

        if (!ValidationUtil.isNotBlank(user.getId())) {
            throw new ValidationException("用户ID不能为空");
        }

        if (!ValidationUtil.isNotBlank(user.getName())) {
            throw new ValidationException("用户名不能为空");
        }

        if (!ValidationUtil.isNotBlank(user.getPassword())) {
            throw new ValidationException("密码不能为空");
        }

        if (!ValidationUtil.isNotBlank(user.getRole())) {
            throw new ValidationException("用户角色不能为空");
        }

        // 验证角色
        if (!User.ROLE_ADMIN.equals(user.getRole()) &&
                !User.ROLE_SALES.equals(user.getRole())) {
            throw new ValidationException("用户角色必须是admin或sales");
        }
    }

    // 默认权限检查器实现类
    private static class DefaultPermissionChecker implements PermissionChecker {
        @Override
        public boolean hasPermission(User user, String resource, String action) {
            if (user == null) {
                return false;
            }

            // 管理员拥有所有权限
            if (user.isAdmin()) {
                return true;
            }

            // 销售员权限
            if (user.isSales()) {
                // 销售员可以查看和管理商品、订单
                if ("product".equals(resource) && ("view".equals(action) || "manage".equals(action))) {
                    return true;
                }
                if ("order".equals(resource) && ("view".equals(action) || "manage".equals(action))) {
                    return true;
                }
            }

            return false;
        }
    }
}