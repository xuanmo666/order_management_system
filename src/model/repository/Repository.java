package model.repository;

import java.util.List;

/**
 * 数据访问接口 - 定义通用的CRUD操作
 * 使用接口实现解耦，便于替换不同的数据存储方式
 * @param <T> 实体类型
 */
public interface Repository<T> {
    // 添加数据
    boolean add(T item);

    // 根据ID删除数据
    boolean delete(String id);

    // 根据ID查找数据
    T findById(String id);

    // 查找所有数据
    List<T> findAll();

    // 更新数据
    boolean update(T item);

    // 获取数据数量
    int count();

    // 判断数据是否存在
    boolean exists(String id);
}