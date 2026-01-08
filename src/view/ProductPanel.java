// view/ProductPanel.java
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * 商品管理面板 - 只负责显示商品管理界面
 * 1. 显示商品列表表格
 * 2. 显示操作按钮
 * 3. 不处理业务逻辑，只收集用户操作
 */
public class ProductPanel extends JPanel {
    // 界面组件
    private JTable productTable;      // 商品表格
    private DefaultTableModel tableModel; // 表格数据模型

    // 操作按钮
    private JButton addButton;        // 添加按钮
    private JButton editButton;       // 编辑按钮
    private JButton deleteButton;     // 删除按钮
    private JButton refreshButton;    // 刷新按钮

    // 监听器接口 - 等待Controller实现
    public interface ProductActionListener {
        /**
         * 当点击添加按钮时调用
         */
        void onAddProduct();

        /**
         * 当点击编辑按钮时调用
         * @param selectedRow 选中的行号
         */
        void onEditProduct(int selectedRow);

        /**
         * 当点击删除按钮时调用
         * @param selectedRow 选中的行号
         */
        void onDeleteProduct(int selectedRow);

        /**
         * 当点击刷新按钮时调用
         */
        void onRefresh();
    }

    private ProductActionListener actionListener; // 操作监听器

    /**
     * 构造函数 - 初始化商品管理界面
     */
    public ProductPanel() {
        initialize();
    }

    /**
     * 初始化界面组件
     */
    private void initialize() {
        // 设置布局
        setLayout(new BorderLayout(5, 5));

        // 创建顶部按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 创建按钮
        addButton = new JButton("添加商品");
        editButton = new JButton("编辑商品");
        deleteButton = new JButton("删除商品");
        refreshButton = new JButton("刷新");

        // 添加到按钮面板
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // 设置按钮事件
        // 注意：这里只通知Controller，不处理业务逻辑
        addButton.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onAddProduct();
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow >= 0 && actionListener != null) {
                actionListener.onEditProduct(selectedRow);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow >= 0 && actionListener != null) {
                actionListener.onDeleteProduct(selectedRow);
            }
        });

        refreshButton.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onRefresh();
            }
        });

        // 创建表格
        // 列名
        String[] columns = {"ID", "商品名称", "价格", "分类", "库存"};

        // 创建表格模型
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };

        // 创建表格
        productTable = new JTable(tableModel);

        // 设置表格只能单选
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(productTable);

        // 添加到主面板
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 底部状态标签
        JLabel statusLabel = new JLabel("提示: 请选择一行进行操作");
        add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * 设置操作监听器
     * 注意：这个方法由Controller调用，用于接收用户操作事件
     * @param listener 操作监听器
     */
    public void setActionListener(ProductActionListener listener) {
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
     * @param rowData 行数据数组
     */
    public void addRowToTable(Object[] rowData) {
        tableModel.addRow(rowData);
    }

    /**
     * 获取选中的行号（供Controller使用）
     * @return 选中的行号，如果没有选中返回-1
     */
    public int getSelectedRow() {
        return productTable.getSelectedRow();
    }

    /**
     * 获取指定行的数据（供Controller使用）
     * @param row 行号
     * @return 该行的数据数组
     */
    public Object[] getRowData(int row) {
        Object[] rowData = new Object[tableModel.getColumnCount()];
        for (int i = 0; i < rowData.length; i++) {
            rowData[i] = tableModel.getValueAt(row, i);
        }
        return rowData;
    }
}