package model.service;

import model.entity.Product;
import model.repository.ProductRepository;
import exception.ValidationException;
import util.ValidationUtil;

import java.util.List;

/**
 * 商品业务逻辑服务类
 * 负责商品的业务逻辑处理，包括验证、库存管理等
 */
public class ProductService {
    private ProductRepository productRepository;

    public ProductService() {
        this.productRepository = new ProductRepository();
    }

    /**
     * 添加商品
     */
    public void addProduct(Product product) throws ValidationException {
        // 输入验证
        if (product == null) {
            throw new ValidationException("商品不能为空");
        }

        if (!ValidationUtil.isNotBlank(product.getId())) {
            throw new ValidationException("商品ID不能为空");
        }

        if (!ValidationUtil.isNotBlank(product.getName())) {
            throw new ValidationException("商品名称不能为空");
        }

        if (product.getPrice() <= 0) {
            throw new ValidationException("商品价格必须大于0");
        }

        if (!ValidationUtil.isNotBlank(product.getCategory())) {
            throw new ValidationException("商品分类不能为空");
        }

        // 检查商品是否已存在
        if (productRepository.exists(product.getId())) {
            throw new ValidationException("商品ID已存在: " + product.getId());
        }

        // 保存商品
        boolean success = productRepository.add(product);
        if (!success) {
            throw new ValidationException("添加商品失败");
        }
    }

    /**
     * 更新商品信息
     */
    public void updateProduct(Product product) throws ValidationException {
        if (product == null) {
            throw new ValidationException("商品不能为空");
        }

        // 检查商品是否存在
        if (!productRepository.exists(product.getId())) {
            throw new ValidationException("商品不存在: " + product.getId());
        }

        // 验证数据
        if (!ValidationUtil.isNotBlank(product.getName())) {
            throw new ValidationException("商品名称不能为空");
        }

        if (product.getPrice() <= 0) {
            throw new ValidationException("商品价格必须大于0");
        }

        // 更新商品
        boolean success = productRepository.update(product);
        if (!success) {
            throw new ValidationException("更新商品失败");
        }
    }

    /**
     * 删除商品
     */
    public void deleteProduct(String productId) throws ValidationException {
        if (!ValidationUtil.isNotBlank(productId)) {
            throw new ValidationException("商品ID不能为空");
        }

        // 检查商品是否存在
        if (!productRepository.exists(productId)) {
            throw new ValidationException("商品不存在: " + productId);
        }

        // 删除商品
        boolean success = productRepository.delete(productId);
        if (!success) {
            throw new ValidationException("删除商品失败");
        }
    }

    /**
     * 根据ID获取商品
     */
    public Product getProductById(String productId) throws ValidationException {
        if (!ValidationUtil.isNotBlank(productId)) {
            throw new ValidationException("商品ID不能为空");
        }

        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new ValidationException("商品不存在: " + productId);
        }

        return product;
    }

    /**
     * 获取所有商品
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 搜索商品
     */
    public List<Product> searchProducts(String keyword, String category,
                                        Double minPrice, Double maxPrice) {
        List<Product> result = productRepository.findAll();

        // 使用流式API进行筛选（简单实现）
        java.util.Iterator<Product> iterator = result.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            boolean keep = true;

            // 关键词筛选
            if (keyword != null && !keyword.trim().isEmpty()) {
                if (!product.getName().toLowerCase().contains(keyword.toLowerCase())) {
                    keep = false;
                }
            }

            // 分类筛选
            if (category != null && !category.trim().isEmpty()) {
                if (!category.equals(product.getCategory())) {
                    keep = false;
                }
            }

            // 价格筛选
            if (minPrice != null && product.getPrice() < minPrice) {
                keep = false;
            }
            if (maxPrice != null && product.getPrice() > maxPrice) {
                keep = false;
            }

            if (!keep) {
                iterator.remove();
            }
        }

        return result;
    }

    /**
     * 商品入库（增加库存）
     */
    public void stockIn(String productId, int amount) throws ValidationException {
        Product product = getProductById(productId);

        if (amount <= 0) {
            throw new ValidationException("入库数量必须大于0");
        }

        product.increaseStock(amount);
        productRepository.update(product);
    }

    /**
     * 商品出库（减少库存）
     */
    public boolean stockOut(String productId, int amount) throws ValidationException {
        Product product = getProductById(productId);

        if (amount <= 0) {
            throw new ValidationException("出库数量必须大于0");
        }

        if (product.getStock() < amount) {
            return false; // 库存不足
        }

        product.setStock(product.getStock() - amount);
        productRepository.update(product);
        return true;
    }

    /**
     * 获取分类统计
     */
    public java.util.Map<String, Integer> getCategoryStatistics() {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            String category = product.getCategory();
            stats.put(category, stats.getOrDefault(category, 0) + 1);
        }

        return stats;
    }

    /**
     * 获取低库存商品
     */
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.getLowStockProducts(threshold);
    }

    /**
     * 按价格排序商品
     */
    public List<Product> getProductsSortedByPrice(boolean ascending) {
        List<Product> products = productRepository.findAll();

        // 使用冒泡排序（简单易懂）
        for (int i = 0; i < products.size() - 1; i++) {
            for (int j = 0; j < products.size() - i - 1; j++) {
                Product p1 = products.get(j);
                Product p2 = products.get(j + 1);

                boolean needSwap = ascending ?
                        p1.getPrice() > p2.getPrice() :
                        p1.getPrice() < p2.getPrice();

                if (needSwap) {
                    products.set(j, p2);
                    products.set(j + 1, p1);
                }
            }
        }

        return products;
    }
}