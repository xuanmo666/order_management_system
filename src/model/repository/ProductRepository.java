package model.repository;

import model.entity.Product;
import java.util.List;

/**
 * 商品数据访问接口
 */
public class ProductRepository {
    public boolean save(Product product) {
        // 保存到文件或数据库
        return true;
    }

    public List<Product> findAll() {
        // 查询所有商品
        return null;
    }

    public Product findById(String productId) {
        // 根据ID查询
        return null;
    }
}