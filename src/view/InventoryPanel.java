// view/InventoryPanel.java
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * 库存管理面板 - 只负责显示库存管理界面
 * 1. 显示库存列表表格
 * 2. 显示操作按钮
 * 3. 不处理业务逻辑，只收集用户操作
 */
public class InventoryPanel extends JPanel {
    // 界面组件
    private JTable inventoryTable;    // 库存表格
    private DefaultTableModel tableModel; // 表格数据模型

    // 操作按钮
    private JButton warningButton;    // 低库存预警按钮
    private JButton adjustButton;     // 调整库存按钮
    private JButton refreshButton;    // 刷新按钮

    // 监听器接口 - 等待Controller实现
    public interface InventoryActionListener {
        /**
         * 当点击低库存预警按钮时调用
         */
        void onLowStockWarning();

        /**
         * 当点击调整库存按钮时调用
         * @param selectedRow 选中的行号
         */
        void onAdjustInventory(int selectedRow);

        /**
         * 当点击刷新按钮时调用
         */
        void onRefresh();
    }

    private InventoryActionListener actionListener; // 操作监听器

    /**
     * 构造函数 - 初始化库存管理界面
     */
    public InventoryPanel() {
        initialize();
    }

    private void initialize() {
        // 设置布局
        setLayout(new BorderLayout(5, 5));

        // 创建顶部按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 创建按钮
        warningButton = new JButton("低库存预警");
        adjustButton = new JButton("调整库存");
        refreshButton = new JButton("刷新");

        // 添加到按钮面板
        buttonPanel.add(warningButton);
        buttonPanel.add(adjustButton);
        buttonPanel.add(refreshButton);

        // 设置按钮事件
        warningButton.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onLowStockWarning();
            }
        });

        adjustButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow >= 0 && actionListener != null) {
                actionListener.onAdjustInventory(selectedRow);
            }
        });

        refreshButton.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onRefresh();
            }
        });

        // 创建表格
        String[] columns = {"商品ID", "商品名称", "当前库存", "最小阈值", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        inventoryTable = new JTable(tableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(inventoryTable);

        // 添加到主面板
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 底部状态标签
        JLabel statusLabel = new JLabel("提示: 请选择一行进行调整库存操作");
        add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * 设置操作监听器
     * 注意：这个方法由Controller调用
     */
    public void setActionListener(InventoryActionListener listener) {
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
        return inventoryTable.getSelectedRow();
    }
}