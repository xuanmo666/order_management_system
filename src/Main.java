import controller.MainController;
import util.DataInitializer;

/**
 * 简易订单管理系统 - 主程序入口
 * 负责启动应用程序
 */
public class Main {
    public static void main(String[] args) {
        // 使用Swing事件调度线程启动GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("======================================");
                System.out.println("      简易订单管理系统启动中...");
                System.out.println("======================================");

                // 1. 初始化默认数据（必须在Swing线程中运行）
                System.out.println("正在初始化系统数据...");
                DataInitializer.initializeAll();
                System.out.println("系统数据初始化完成！");

                // 2. 初始化主控制器
                System.out.println("正在初始化主控制器...");
                MainController mainController = new MainController();
                System.out.println("主控制器初始化完成！");

                // 3. 启动应用程序
                System.out.println("启动应用程序界面...");
                mainController.start();

                // 4. 显示测试账号信息
                System.out.println("可用测试账号：");
                System.out.println("  管理员账号: admin / admin123");
                System.out.println("  销售员账号: sales / sales123");

            } catch (Exception e) {
                System.err.println("\n应用程序启动失败！");
                System.err.println("错误信息: " + e.getMessage());
                e.printStackTrace();

                // 显示错误对话框
                javax.swing.JOptionPane.showMessageDialog(
                        null,
                        "应用程序启动失败！\n\n" +
                                "错误信息: " + e.getMessage() + "\n\n" +
                                "请检查控制台输出获取详细错误信息。",
                        "系统启动错误",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                );

                // 退出程序
                System.exit(1);
            }
        });
    }
}