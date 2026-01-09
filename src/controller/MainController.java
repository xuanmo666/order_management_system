package controller;

import view.GuiView;
import model.service.*;
import model.entity.User;
import exception.ValidationException;

/**
 * 主控制器 - 负责系统启动、用户登录、界面导航
 * 作为整个系统的总控制中心，协调各个子控制器的初始化和数据流转
 */
public class MainController {
    // 视图：主界面，包含登录界面和各个功能模块的界面
    private GuiView guiView;

    // 用户服务：处理用户相关的业务逻辑（登录验证、用户管理）
    private UserService userService;

    // 各个子控制器：遵循单一职责原则，每个控制器负责特定功能模块
    private ProductController productController;   // 商品管理控制器
    private OrderController orderController;       // 订单管理控制器
    private InventoryController inventoryController; // 库存管理控制器
    private UserController userController;         // 用户管理控制器（仅管理员可见）

    // 当前登录用户：保存登录状态和用户信息，用于权限控制
    private User currentUser;

    // 服务实例：各个业务模块的服务对象
    private ProductService productService;
    private OrderService orderService;
    private InventoryService inventoryService;

    /**
     * 构造方法：初始化主控制器
     * 1. 获取各个服务的单例实例
     * 2. 创建主界面
     * 3. 设置登录事件监听器
     *
     * 注意：这里不立即初始化子控制器，因为此时界面面板可能还未创建
     */
    public MainController() {
        // 使用单例模式获取服务实例 - 确保整个应用程序使用相同的服务对象
        this.userService = UserService.getInstance();
        this.productService = ProductService.getInstance();
        this.orderService = OrderService.getInstance();
        this.inventoryService = InventoryService.getInstance();

        // 创建主界面（包含登录界面和各个功能面板）
        this.guiView = new GuiView();

        // 设置登录事件监听器（使用Lambda表达式）
        setupEventListeners();

        System.out.println("主控制器初始化完成");
    }

    /**
     * 设置事件监听器
     * 将界面的事件（如登录按钮点击）与控制器的方法绑定
     */
    private void setupEventListeners() {
        // 获取登录面板，设置登录监听器
        // 当用户在登录界面输入用户名密码并点击登录时，触发handleLogin方法
        guiView.getLoginPanel().setLoginListener(
                (username, password) -> handleLogin(username, password));
    }

    /**
     * 启动应用程序的主方法
     * 由程序的main方法调用，启动整个系统
     */
    public void start() {
        System.out.println("简易订单管理系统启动...");
        // 应用程序启动，显示登录界面，等待用户登录
        // 界面的实际显示在GuiView的构造方法中已经处理
    }

    /**
     * 处理用户登录
     * 核心业务流程：
     * 1. 验证用户名密码
     * 2. 保存用户信息
     * 3. 根据角色显示主界面
     * 4. 初始化子控制器
     * 5. 加载初始数据
     *
     * @param username 用户名
     * @param password 密码
     */
    private void handleLogin(String username, String password) {
        try {
            // 1. 调用用户服务进行登录验证
            // 如果验证失败会抛出ValidationException
            User user = userService.login(username, password);

            // 2. 保存当前登录用户信息
            this.currentUser = user;

            // 3. 根据角色显示主界面（不同角色可能有不同权限）
            // GuiView会根据用户角色显示/隐藏某些功能
            guiView.showMainScreen(user.getName(), user.getRole());

            // 4. 初始化子控制器（此时界面面板已创建完成）
            initializeControllers();

            // 5. 设置退出按钮监听器（关键修复：必须在登录成功后设置）
            guiView.setLogoutListener(e -> handleLogout());

            // 6. 加载各个模块的初始数据
            loadInitialData();

            System.out.println("用户 " + username + " 登录成功，角色: " + user.getRole());

        } catch (ValidationException e) {
            // 业务逻辑验证失败（如用户名不存在、密码错误）
            guiView.showError("登录失败", e.getMessage());
            // 清空登录输入框，方便用户重新输入
            guiView.getLoginPanel().clearInputs();
        } catch (Exception e) {
            // 其他未知异常
            guiView.showError("系统错误", "登录过程出现异常: " + e.getMessage());
            guiView.getLoginPanel().clearInputs();
        }
    }

    /**
     * 初始化所有子控制器（延迟初始化模式）
     * 只有在用户登录成功后，界面完全创建后才初始化控制器
     * 这样做的好处：
     * 1. 避免界面未创建时的空指针异常
     * 2. 减少不必要的资源消耗
     */
    private void initializeControllers() {
        System.out.println("初始化子控制器...");

        // 1. 初始化商品控制器
        // 商品控制器需要商品面板和商品服务
        productController = new ProductController(
                guiView.getProductPanel(), productService);

        // 2. 初始化库存控制器
        // 关键修改：传递productController引用，实现控制器间的通信
        inventoryController = new InventoryController(
                guiView.getInventoryPanel(), inventoryService, productService, productController);

        // 3. 建立双向引用：为商品控制器设置库存控制器引用
        // 这样当商品数据变化时，可以刷新库存界面
        productController.setInventoryController(inventoryController);

        // 4. 初始化订单控制器
        // 订单控制器需要访问商品和库存信息，因此传递相关控制器引用
        orderController = new OrderController(
                guiView.getOrderPanel(), orderService, productService, productController, inventoryController);

        // 5. 如果是管理员，初始化用户控制器
        // 用户管理功能只有管理员可见
        if (currentUser != null && currentUser.isAdmin() && guiView.getUserPanel() != null) {
            userController = new UserController(
                    guiView.getUserPanel());
            // 设置用户服务
            userController.setUserService(userService);
            System.out.println("用户控制器已初始化（管理员权限）");
        }

        System.out.println("子控制器初始化完成");
    }

    /**
     * 处理用户登出
     * 清理流程：
     * 1. 清理当前用户信息
     * 2. 清空各个面板的数据
     * 3. 重置控制器引用
     * 4. 显示登录界面
     */
    public void handleLogout() {
        System.out.println("用户 " + (currentUser != null ? currentUser.getName() : "未知") + " 退出登录");

        // 1. 重置当前用户信息
        this.currentUser = null;

        // 2. 清空所有面板的数据（防止下一个用户看到上一个用户的数据）
        // 调用各个控制器的refreshView方法，清空表格数据
        if (productController != null) {
            productController.refreshView(); // 清空商品表格
        }
        if (orderController != null) {
            orderController.refreshView();   // 清空订单表格
        }
        if (inventoryController != null) {
            inventoryController.refreshView(); // 清空库存表格
        }
        if (userController != null) {
            userController.refreshView(); // 清空用户表格
        }

        // 3. 重置控制器引用（重要！避免内存泄漏和旧数据问题）
        // 下一个用户登录时会重新创建控制器
        productController = null;
        orderController = null;
        inventoryController = null;
        userController = null;

        // 4. 显示登录界面
        guiView.showLoginScreen();

        // 5. 重新设置登录监听器（因为界面被重置了）
        setupEventListeners();

        System.out.println("已返回登录界面");
    }

    /**
     * 加载初始数据
     * 在用户登录成功后，加载各个模块的数据到界面中
     */
    private void loadInitialData() {
        System.out.println("开始加载初始数据...");

        // 打印当前数据状态（用于调试）
        System.out.println("验证数据状态:");
        System.out.println("商品数量: " + productService.getAllProducts().size());
        System.out.println("订单数量: " + orderService.getAllOrders().size());
        System.out.println("库存记录: " + inventoryService.getAllInventory().size());
        System.out.println("用户数量: " + userService.getAllUsers().size());

        // 1. 加载商品数据
        try {
            if (productController != null) {
                productController.loadProducts();
                System.out.println("商品数据加载完成");
            } else {
                System.err.println("商品控制器未初始化");
            }
        } catch (Exception e) {
            System.err.println("加载商品数据失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 2. 加载订单数据
        try {
            if (orderController != null) {
                orderController.loadOrders();
                System.out.println("订单数据加载完成");
            } else {
                System.err.println("订单控制器未初始化");
            }
        } catch (Exception e) {
            System.err.println("加载订单数据失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. 加载库存数据
        try {
            if (inventoryController != null) {
                inventoryController.loadInventory();
                System.out.println("库存数据加载完成");
            } else {
                System.err.println("库存控制器未初始化");
            }
        } catch (Exception e) {
            System.err.println("加载库存数据失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 4. 加载用户数据（仅管理员可见）
        if (userController != null && currentUser != null && currentUser.isAdmin()) {
            try {
                userController.loadUsers();
                System.out.println("用户数据加载完成");
            } catch (Exception e) {
                System.err.println("加载用户数据失败: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("初始数据加载完成");
    }

    /**
     * 获取当前登录用户
     * 用于权限控制和用户信息展示
     *
     * @return 当前用户对象，如果未登录则返回null
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 获取各个子控制器（用于测试或其他需要访问子控制器的情况）
     */
    public ProductController getProductController() {
        return productController;
    }

    public OrderController getOrderController() {
        return orderController;
    }

    public InventoryController getInventoryController() {
        return inventoryController;
    }
}