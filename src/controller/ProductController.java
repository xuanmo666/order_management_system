package controller;

import view.ProductPanel;
import model.service.ProductService;
import model.entity.Product;

/**
 * 商品管理控制器 - 处理商品相关的业务逻辑和界面交互
 * 作为view.ProductPanel和model.service.ProductService之间的桥梁
 */
public class ProductController {
    private ProductPanel productPanel;      // 商品管理界面
    private ProductService productService;  // 商品业务服务

    /**
     * 构造函数
     * @param productPanel 商品管理界面
     * @param productService 商品服务
     */
    public ProductController(ProductPanel productPanel, ProductService productService) {
        this.productPanel = productPanel;
        this.productService = productService;
        setupEventListeners();
    }

    /**
     * 设置界面事件监听器
     */
    private void setupEventListeners() {
        // 设置商品界面上所有按钮的事件监听器
        // 这些监听器会调用本控制器的相应方法
    }

    /**
     * 加载商品数据到界面
     */
    public void loadProducts() {
        // 调用productService获取所有商品
        // 将数据设置到productPanel的表格中
    }

    /**
     * 处理商品搜索请求
     * @param keyword 搜索关键词
     * @param category 商品分类
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     */
    public void handleSearchProducts(String keyword, String category,
                                     Double minPrice, Double maxPrice) {
        // 调用productService搜索商品
        // 更新界面显示结果
    }

    /**
     * 处理添加商品请求
     * @param product 要添加的商品对象
     */
    public void handleAddProduct(Product product) {
        // 调用productService添加商品
        // 处理成功或失败，更新界面
    }

    /**
     * 处理编辑商品请求
     * @param productId 商品ID
     * @param updatedProduct 更新后的商品对象
     */
    public void handleUpdateProduct(String productId, Product updatedProduct) {
        // 调用productService更新商品
        // 处理结果，更新界面
    }

    /**
     * 处理删除商品请求
     * @param productId 商品ID
     */
    public void handleDeleteProduct(String productId) {
        // 调用productService删除商品
        // 确认删除并更新界面
    }

    /**
     * 处理商品入库请求
     * @param productId 商品ID
     * @param quantity 入库数量
     */
    public void handleStockIn(String productId, int quantity) {
        // 调用productService.stockIn方法
        // 更新库存显示
    }

    /**
     * 处理商品出库请求
     * @param productId 商品ID
     * @param quantity 出库数量
     */
    public void handleStockOut(String productId, int quantity) {
        // 调用productService.stockOut方法
        // 检查库存是否足够并处理结果
    }

    /**
     * 获取商品统计信息
     */
    public void handleGetProductStatistics() {
        // 调用productService.getCategoryStatistics等方法
        // 在界面显示统计结果
    }

    /**
     * 刷新商品界面
     */
    public void refreshView() {
        // 重新加载商品数据并更新界面
    }
}