package controller;

import view.GuiView;
import model.service.*;
import model.entity.User;
import exception.ValidationException;

/**
 * 主控制器 - 负责系统启动、用户登录、界面导航
 */
public class MainController {
    private GuiView guiView;
    private UserService userService;

    // 子控制器
    private ProductController productController;
    private OrderController orderController;
    private InventoryController inventoryController;
    private UserController userController;

    private User currentUser; // 当前登录用户

    public MainController() {
        // 初始化服务
        this.userService = new UserService();

        // 初始化界面
        this.guiView = new GuiView();

        // 初始化其他服务（延迟初始化）
        ProductService productService = new ProductService();
        OrderService orderService = new OrderService();
        InventoryService inventoryService = new InventoryService();

        // 初始化子控制器
        this.productController = new ProductController(
                guiView.getProductPanel(), productService);
        this.orderController = new OrderController(
                guiView.getOrderPanel(), orderService, productService);
        this.inventoryController = new InventoryController(
                guiView.getInventoryPanel(), inventoryService, productService);

        // 用户控制器（如果有用户面板）
        if (guiView.getUserPanel() != null) {
            this.userController = new UserController(
                    guiView.getUserPanel(), userService);
        }

        // 设置事件监听器
        setupEventListeners();
    }

    private void setupEventListeners() {
        // 设置登录事件监听器
        guiView.getLoginPanel().setLoginListener(
                (username, password) -> handleLogin(username, password));
    }

    /**
     * 启动应用程序
     */
    public void start() {
        System.out.println("简易订单管理系统启动...");
    }

    /**
     * 处理用户登录
     */
    private void handleLogin(String username, String password) {
        try {
            // 验证登录
            User user = userService.login(username, password);

            // 保存当前用户
            this.currentUser = user;

            // 根据角色显示主界面
            guiView.showMainScreen(user.getName(), user.getRole());

            // 加载初始数据
            loadInitialData();

        } catch (ValidationException e) {
            guiView.showError("登录失败", e.getMessage());
            guiView.getLoginPanel().clearInputs();
        }
    }

    /**
     * 处理用户登出
     */
    public void handleLogout() {
        this.currentUser = null;
        guiView.showLoginScreen();
    }

    /**
     * 加载初始数据
     */
    private void loadInitialData() {
        productController.loadProducts();
        orderController.loadOrders();
        inventoryController.loadInventory();

        if (userController != null) {
            userController.loadUsers();
        }
    }

    /**
     * 获取当前用户
     */
    public User getCurrentUser() {
        return currentUser;
    }
}