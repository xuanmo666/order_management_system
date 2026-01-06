package view;

import model.entity.Product;
import model.entity.Order;
import java.util.List;

/**
 * 控制台界面输出
 */
public class ConsoleView {
    public void printProductList(List<Product> products) {
        // 打印商品列表
    }

    public void printOrderDetail(Order order) {
        // 打印订单详情
    }

    public void printMessage(String message) {
        System.out.println(message);
    }
}