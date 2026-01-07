package model.repository;

import model.entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户数据访问实现类 - 使用内存存储
 */
public class UserRepository implements Repository<User> {
    // 使用Map存储用户数据
    private Map<String, User> userMap = new HashMap<>();

    @Override
    public boolean add(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        userMap.put(user.getId(), user);
        return true;
    }

    @Override
    public boolean delete(String id) {
        if (userMap.containsKey(id)) {
            userMap.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public User findById(String id) {
        return userMap.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public boolean update(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        if (userMap.containsKey(user.getId())) {
            userMap.put(user.getId(), user);
            return true;
        }
        return false;
    }

    @Override
    public int count() {
        return userMap.size();
    }

    @Override
    public boolean exists(String id) {
        return userMap.containsKey(id);
    }

    // 特定于用户的查询方法

    /**
     * 根据用户名查找用户
     */
    public User findByUsername(String username) {
        for (User user : userMap.values()) {
            if (username.equals(user.getName())) {
                return user;
            }
        }
        return null;
    }

    /**
     * 根据角色查找用户
     */
    public List<User> findByRole(String role) {
        List<User> result = new ArrayList<>();
        for (User user : userMap.values()) {
            if (role.equals(user.getRole())) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * 验证用户登录
     */
    public User validateLogin(String username, String password) {
        User user = findByUsername(username);
        if (user != null && password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }
}