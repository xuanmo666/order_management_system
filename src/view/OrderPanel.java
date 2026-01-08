// view/OrderPanel.java
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * 订单管理面板 - 只负责显示订单管理界面
 * 1. 显示订单列表表格
 * 2. 显示操作按钮
 * 3. 不处理业务逻辑，只收集用户操作
 */
public class OrderPanel extends JPanel {
    // 界面组件
    private JTable orderTable;        // 订单表格
    private DefaultTableModel tableModel; // 表格数据模型

    // 操作按钮
    private JButton createButton;     // 创建订单按钮
    private JButton viewButton;       // 查看详情按钮
    private JButton cancelButton;     // 取消订单按钮
    private JButton refreshButton;    // 刷新按钮

    // 监听器接口 - 等待Controller实现
    public interface OrderActionListener {
        /**
         * 当点击创建订单按钮时调用
         */
        void onCreateOrder();

        /**
         * 当点击查看详情按钮时调用
         * @param selectedRow 选中的行号
         */
        void onViewOrder(int selectedRow);

        /**
         * 当点击取消订单按钮时调用
         * @param selectedRow 选中的行号
         */
        void onCancelOrder(int selectedRow);

        /**
         * 当点击刷新按钮时调用
         */
        void onRefresh();
    }

    private OrderActionListener actionListener; // 操作监听器

    /**
     * 构造函数 - 初始化订单管理界面
     */
    public OrderPanel() {
        initialize();
    }

    private void initialize() {
        // 设置布局
        setLayout(new BorderLayout(5, 5));

        // 创建顶部按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 创建按钮
        createButton = new JButton("创建订单");
        viewButton = new JButton("查看详情");
        cancelButton = new JButton("取消订单");
        refreshButton = new JButton("刷新");

        // 添加到按钮面板
        buttonPanel.add(createButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);

        // 设置按钮事件
        createButton.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onCreateOrder();
            }
        });

        viewButton.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow >= 0 && actionListener != null) {
                actionListener.onViewOrder(selectedRow);
            }
        });

        cancelButton.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow >= 0 && actionListener != null) {
                actionListener.onCancelOrder(selectedRow);
            }
        });

        refreshButton.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onRefresh();
            }
        });

        // 创建表格
        String[] columns = {"订单号", "客户姓名", "总金额", "状态", "创建时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(orderTable);

        // 添加到主面板
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 底部状态标签
        JLabel statusLabel = new JLabel("提示: 请选择一行进行操作");
        add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * 设置操作监听器
     * 注意：这个方法由Controller调用
     */
    public void setActionListener(OrderActionListener listener) {
        this.actionListener = listener;
    }

    /**
     * 清空表格数据（供Controller使用）
     */
    public void clearTable() {
        tableModel.setRowCount(0);
    }

    /**
     * 向表格添加一行数据（供Controller使用）
     */
    public void addRowToTable(Object[] rowData) {
        tableModel.addRow(rowData);
    }

    /**
     * 获取选中的行号（供Controller使用）
     */
    public int getSelectedRow() {
        return orderTable.getSelectedRow();
    }
}