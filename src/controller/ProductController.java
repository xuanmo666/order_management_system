package controller;

import model.entity.Product;
import model.service.ProductService;
import view.ConsoleView;

/**
 * 商品管理控制器
 */
public class ProductController {
    private ProductService productService;
    private ConsoleView consoleView;

    public ProductController() {
        productService = new ProductService();
        consoleView = new ConsoleView();
    }

    public void addProduct(Product product) {
        // 调用服务层添加商品
    }

    public void updateProduct(String productId, Product product) {
        // 更新商品信息
    }

    public void deleteProduct(String productId) {
        // 删除商品
    }

    public void searchProduct(String keyword, String category, Double minPrice, Double maxPrice) {
        // 搜索商品
    }
}