package model.service;

import model.entity.User;
import exception.ValidationException;
import java.util.List;

/**
 * 用户服务接口 - 供Controller层调用的契约
 */
public interface UserServiceInterface {
    // 用户认证
    User login(String username, String password) throws ValidationException;
    boolean logout(String userId);

    // 用户管理
    void addUser(User user) throws ValidationException;
    void updateUser(User user) throws ValidationException;
    void deleteUser(String userId) throws ValidationException;
    User getUserById(String userId) throws ValidationException;
    User getUserByUsername(String username);
    List<User> getAllUsers();

    // 角色和权限
    List<User> getUsersByRole(String role);
    boolean checkPermission(User user, String resource, String action);
    boolean isAdmin(String userId) throws ValidationException;
    boolean isSales(String userId) throws ValidationException;

    // 密码管理
    void changePassword(String userId, String oldPassword, String newPassword)
            throws ValidationException;
    void resetPassword(String userId, String newPassword) throws ValidationException;

    // 验证方法
    boolean userExists(String userId);
    int getUserCount();
}