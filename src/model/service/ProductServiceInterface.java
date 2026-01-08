package model.service;

import model.entity.Product;
import exception.ValidationException;
import exception.BusinessException;
import java.util.List;
import java.util.Map;

/**
 * 商品服务接口 - 供Controller层调用的契约
 * 组员C实现Controller时需要依赖此接口
 */
public interface ProductServiceInterface {
    // 商品管理
    void addProduct(Product product) throws ValidationException;
    void updateProduct(Product product) throws ValidationException;
    void deleteProduct(String productId) throws ValidationException;
    Product getProductById(String productId) throws ValidationException;
    List<Product> getAllProducts();

    // 搜索和筛选
    List<Product> searchProducts(String keyword, String category,
                                 Double minPrice, Double maxPrice);

    // 库存管理
    void stockIn(String productId, int amount) throws ValidationException;
    boolean stockOut(String productId, int amount) throws ValidationException;

    // 统计和报表
    Map<String, Integer> getCategoryStatistics();
    List<Product> getLowStockProducts(int threshold);
    List<Product> getProductsSortedByPrice(boolean ascending);

    // 其他业务操作
    boolean productExists(String productId);
    int getProductCount();
}