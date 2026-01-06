package model.service;

import model.entity.User;
import model.repository.UserRepository;

/**
 * 用户认证与权限管理
 */
public class UserService {
    private UserRepository userRepository;

    public User login(String username, String password) {
        // 验证用户名密码
        // 返回用户对象（含角色）
        return null;
    }

    public boolean hasPermission(String role, String action) {
        // 权限验证逻辑
        return true;
    }
}