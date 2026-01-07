package model.repository;

import model.entity.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品数据访问实现类 - 使用内存存储
 * 实现Repository接口，提供具体的商品数据操作
 */
public class ProductRepository implements Repository<Product> {
    // 使用Map存储商品数据，key为商品ID
    private Map<String, Product> productMap = new HashMap<>();

    @Override
    public boolean add(Product product) {
        if (product == null || product.getId() == null) {
            return false;
        }
        productMap.put(product.getId(), product);
        return true;
    }

    @Override
    public boolean delete(String id) {
        if (productMap.containsKey(id)) {
            productMap.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public Product findById(String id) {
        return productMap.get(id);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(productMap.values());
    }

    @Override
    public boolean update(Product product) {
        if (product == null || product.getId() == null) {
            return false;
        }
        if (productMap.containsKey(product.getId())) {
            productMap.put(product.getId(), product);
            return true;
        }
        return false;
    }

    @Override
    public int count() {
        return productMap.size();
    }

    @Override
    public boolean exists(String id) {
        return productMap.containsKey(id);
    }

    // 特定于商品的查询方法

    /**
     * 根据分类查找商品
     */
    public List<Product> findByCategory(String category) {
        List<Product> result = new ArrayList<>();
        for (Product product : productMap.values()) {
            if (category.equals(product.getCategory())) {
                result.add(product);
            }
        }
        return result;
    }

    /**
     * 根据价格范围查找商品
     */
    public List<Product> findByPriceRange(double minPrice, double maxPrice) {
        List<Product> result = new ArrayList<>();
        for (Product product : productMap.values()) {
            double price = product.getPrice();
            if (price >= minPrice && price <= maxPrice) {
                result.add(product);
            }
        }
        return result;
    }

    /**
     * 根据名称关键词搜索商品
     */
    public List<Product> searchByName(String keyword) {
        List<Product> result = new ArrayList<>();
        for (Product product : productMap.values()) {
            if (product.getName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(product);
            }
        }
        return result;
    }

    /**
     * 获取所有商品的分类列表
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        for (Product product : productMap.values()) {
            String category = product.getCategory();
            if (!categories.contains(category)) {
                categories.add(category);
            }
        }
        return categories;
    }

    /**
     * 获取低库存商品（库存小于阈值）
     */
    public List<Product> getLowStockProducts(int threshold) {
        List<Product> result = new ArrayList<>();
        for (Product product : productMap.values()) {
            if (product.getStock() < threshold) {
                result.add(product);
            }
        }
        return result;
    }
}