package util;

import model.entity.User;
import model.service.UserService;
import model.service.ProductService;
import model.service.InventoryService;
import model.service.OrderService;
import model.entity.Product;
import model.entity.Inventory;
import exception.ValidationException;

/**
 * 数据初始化工具类
 * 系统启动时自动创建默认数据
 */
public class DataInitializer {

    /**
     * 初始化所有默认数据 - 使用单例模式确保数据一致性
     */
    public static void initializeAll() {
        System.out.println("=== 系统数据初始化开始 ===");

        // 确保使用单例实例
        UserService userService = UserService.getInstance();
        ProductService productService = ProductService.getInstance();
        InventoryService inventoryService = InventoryService.getInstance();
        OrderService orderService = OrderService.getInstance();

        System.out.println("=== 获取服务实例完成 ===");
        System.out.println("UserService实例: " + userService);
        System.out.println("ProductService实例: " + productService);
        System.out.println("InventoryService实例: " + inventoryService);
        System.out.println("OrderService实例: " + orderService);

        // 初始化用户
        if (userService.getAllUsers().isEmpty()) {
            System.out.println("初始化用户数据...");
            try {
                // 创建管理员用户
                User admin = new User();
                admin.setId("U-admin001");
                admin.setName("admin");
                admin.setPassword("admin123");
                admin.setRole(User.ROLE_ADMIN);
                userService.addUser(admin);
                System.out.println("创建管理员账号: admin / admin123");

                // 创建销售员用户
                User sales = new User();
                sales.setId("U-sales001");
                sales.setName("sales");
                sales.setPassword("sales123");
                sales.setRole(User.ROLE_SALES);
                userService.addUser(sales);
                System.out.println("创建销售员账号: sales / sales123");
            } catch (Exception e) {
                System.err.println("创建用户失败: " + e.getMessage());
            }
        } else {
            System.out.println("用户数据已存在，跳过初始化");
        }

        // 初始化商品和库存
        if (productService.getAllProducts().isEmpty()) {
            System.out.println("初始化商品数据...");
            try {
                String[][] products = {
                        {"P-001", "iPhone 15", "7999.00", "手机", "Apple最新款智能手机"},
                        {"P-002", "华为MateBook", "6999.00", "电脑", "华为轻薄笔记本电脑"},
                        {"P-003", "联想拯救者", "8999.00", "电脑", "游戏笔记本电脑"},
                        {"P-004", "小米手环8", "299.00", "穿戴设备", "智能运动手环"},
                        {"P-005", "索尼耳机", "1299.00", "音频设备", "降噪蓝牙耳机"},
                        {"P-006", "三星显示器", "1999.00", "显示器", "27寸4K显示器"},
                        {"P-007", "戴尔键盘", "299.00", "外设", "机械键盘"},
                        {"P-008", "罗技鼠标", "199.00", "外设", "无线游戏鼠标"}
                };

                for (int i = 0; i < products.length; i++) {
                    String[] productData = products[i];

                    // 创建商品
                    Product product = new Product();
                    product.setId(productData[0]);
                    product.setName(productData[1]);
                    product.setPrice(Double.parseDouble(productData[2]));
                    product.setCategory(productData[3]);
                    product.setStock(50 + i * 10);

                    try {
                        productService.addProduct(product);
                        System.out.println("创建商品: " + product.getName() + " (库存: " + product.getStock() + ")");
                    } catch (ValidationException e) {
                        // 商品可能已存在，尝试更新
                        System.out.println("商品已存在，更新: " + product.getName());
                        try {
                            productService.updateProduct(product);
                        } catch (ValidationException ex) {
                            System.err.println("更新商品失败: " + ex.getMessage());
                        }
                    }

                    // 创建对应的库存记录
                    Inventory inventory = new Inventory(product.getId());
                    inventory.setQuantity(50 + i * 10);
                    inventory.setMinThreshold(10);
                    inventory.setMaxCapacity(200);

                    try {
                        inventoryService.addInventory(inventory);
                    } catch (ValidationException e) {
                        // 库存记录可能已存在，尝试更新
                        try {
                            inventoryService.updateInventory(inventory);
                        } catch (ValidationException ex) {
                            System.err.println("更新库存失败: " + ex.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("创建商品失败: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("商品数据已存在，跳过初始化");
        }

        // 验证数据
        System.out.println("=== 初始化完成验证 ===");
        System.out.println("用户数量: " + userService.getAllUsers().size());
        System.out.println("商品数量: " + productService.getAllProducts().size());
        System.out.println("库存记录: " + inventoryService.getAllInventory().size());

        // 打印商品详情
        System.out.println("商品列表:");
        for (Product product : productService.getAllProducts()) {
            System.out.println("  " + product.getId() + " - " + product.getName() +
                    " 价格: " + product.getPrice() + " 库存: " + product.getStock());
        }

        System.out.println("=== 系统数据初始化完成 ===");
    }
}