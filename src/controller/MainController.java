package controller;

import view.MenuView;
import model.service.UserService;

/**
 * 主控制器，负责系统启动、用户登录、主菜单分发
 */
public class MainController {
    private MenuView menuView;
    private UserService userService;

    public MainController() {
        menuView = new MenuView();
        userService = new UserService();
    }

    public void start() {
        menuView.showLoginScreen();
        // 登录验证逻辑
        // 根据角色显示不同菜单
        menuView.showMainMenu();
    }

    public void dispatch(String menuOption) {
        // 根据用户选择调用相应控制器
    }
}
