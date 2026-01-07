import model.entity.*;
import model.service.*;
import model.repository.*;
import util.*;
import exception.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== 简易订单管理系统 Model 层演示 ===");

        try {
            demoProductManagement();
            demoOrderManagement();
            demoUserManagement();
            demoExceptionHandling();

        } catch (Exception e) {
            System.out.println("演示过程中发生错误: " + e.getMessage());
        }
    }

    private static void demoProductManagement() throws Exception {
        System.out.println("\n--- 商品管理演示 ---");

        ProductService productService = new ProductService();

        // 添加商品
        Product p1 = new Product("P001", "iPhone 15", 6999.00, "电子产品");
        Product p2 = new Product("P002", "华为Mate 60", 5999.00, "电子产品");
        Product p3 = new Product("P003", "可口可乐", 3.50, "食品饮料");

        productService.addProduct(p1);
        productService.addProduct(p2);
        productService.addProduct(p3);
        System.out.println("添加了3个商品");

        // 商品入库
        productService.stockIn("P001", 100);
        productService.stockIn("P002", 50);
        productService.stockIn("P003", 500);
        System.out.println("商品入库完成");

        // 获取所有商品
        List<Product> products = productService.getAllProducts();
        System.out.println("\n所有商品列表:");
        for (Product p : products) {
            System.out.println("  " + p);
        }

        // 搜索商品
        System.out.println("\n搜索'电子'类商品:");
        List<Product> electronics = productService.searchProducts(null, "电子产品", null, null);
        for (Product p : electronics) {
            System.out.println("  " + p.getName() + " - ¥" + p.getPrice());
        }

        // 排序演示
        System.out.println("\n按价格升序排列:");
        List<Product> sorted = productService.getProductsSortedByPrice(true);
        for (Product p : sorted) {
            System.out.println("  " + p.getName() + " - ¥" + p.getPrice());
        }

        // 统计演示
        System.out.println("\n商品分类统计:");
        java.util.Map<String, Integer> stats = productService.getCategoryStatistics();
        for (java.util.Map.Entry<String, Integer> entry : stats.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + "个商品");
        }
    }

    private static void demoOrderManagement() throws Exception {
        System.out.println("\n--- 订单管理演示 ---");

        ProductService productService = new ProductService();
        OrderService orderService = new OrderService();

        // 创建客户
        Customer customer = new Customer("C001", "张三", "13800138000");

        // 创建订单
        Order order = new Order("O001", customer);

        // 获取商品并添加到订单
        Product phone = productService.getProductById("P001");
        Product drink = productService.getProductById("P003");

        order.addItem(new OrderItem(phone, 2));
        order.addItem(new OrderItem(drink, 10));

        // 创建订单（会扣减库存）
        System.out.println("创建订单前库存:");
        System.out.println("  " + phone.getName() + " 库存: " + phone.getStock());
        System.out.println("  " + drink.getName() + " 库存: " + drink.getStock());

        orderService.createOrder(order);
        System.out.println("\n订单创建成功:");
        System.out.println("  订单号: " + order.getOrderId());
        System.out.println("  客户: " + order.getCustomer().getName());
        System.out.println("  总金额: ¥" + order.getTotalAmount());
        System.out.println("  状态: " + order.getStatus());

        System.out.println("\n创建订单后库存:");
        phone = productService.getProductById("P001");
        drink = productService.getProductById("P003");
        System.out.println("  " + phone.getName() + " 库存: " + phone.getStock());
        System.out.println("  " + drink.getName() + " 库存: " + drink.getStock());

        // 状态流转演示
        System.out.println("\n订单状态流转演示:");
        orderService.updateOrderStatus("O001", Order.STATUS_PAID);
        System.out.println("  状态变为: " + orderService.getOrderById("O001").getStatus());

        // 订单统计
        System.out.println("\n订单统计:");
        java.util.Map<String, Object> orderStats = orderService.getOrderStatistics();
        System.out.println("  总订单数: " + orderStats.get("totalOrders"));
        System.out.println("  总销售额: ¥" + orderStats.get("totalSales"));
        System.out.println("  平均订单金额: ¥" + orderStats.get("averageOrderAmount"));
    }

    private static void demoUserManagement() throws Exception {
        System.out.println("\n--- 用户管理演示 ---");

        UserService userService = new UserService();

        // 添加用户
        User admin = new User("U001", "admin", "admin123", User.ROLE_ADMIN);
        User sales = new User("U002", "sales1", "sales123", User.ROLE_SALES);

        userService.addUser(admin);
        userService.addUser(sales);
        System.out.println("添加了管理员和销售员");

        // 用户登录
        System.out.println("\n用户登录演示:");
        User loggedInUser = userService.login("admin", "admin123");
        System.out.println("  登录成功: " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")");

        // 权限检查
        System.out.println("\n权限检查演示:");
        boolean canManageProduct = userService.checkPermission(loggedInUser, "product", "manage");
        System.out.println("  管理员可以管理商品吗? " + canManageProduct);

        // 获取用户列表
        System.out.println("\n所有用户列表:");
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            System.out.println("  " + user);
        }
    }

    private static void demoExceptionHandling() {
        System.out.println("\n--- 异常处理演示 ---");

        try {
            ProductService productService = new ProductService();

            // 尝试添加一个无效的商品
            Product invalidProduct = new Product();
            invalidProduct.setId("");  // 空ID
            invalidProduct.setName("测试商品");
            invalidProduct.setPrice(-100);  // 负价格

            productService.addProduct(invalidProduct);

        } catch (ValidationException e) {
            System.out.println("捕获到验证异常: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("捕获到其他异常: " + e.getMessage());
        }

        try {
            OrderService orderService = new OrderService();

            // 尝试获取不存在的订单
            orderService.getOrderById("NON_EXISTENT");

        } catch (ValidationException e) {
            System.out.println("捕获到验证异常: " + e.getMessage());
        }

        System.out.println("异常处理演示完成，所有异常都被正确捕获！");
    }
}