// Main.java - 更新为启动GUI界面
import view.GuiView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // 使用SwingUtilities.invokeLater确保线程安全
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("启动订单管理系统GUI...");
                new GuiView();
            }
        });
    }
}