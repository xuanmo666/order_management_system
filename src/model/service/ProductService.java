package model.service;

import model.entity.Product;
import model.repository.ProductRepository;

import java.util.List;

/**
 * 商品业务逻辑
 */
public class ProductService {
    private ProductRepository productRepository;

    public boolean addProduct(Product product) {
        // 验证商品信息
        // 调用Repository保存
        return true;
    }

    public List<Product> searchProducts(String keyword, String category, Double minPrice, Double maxPrice) {
        // 调用Repository查询
        return null;
    }
}