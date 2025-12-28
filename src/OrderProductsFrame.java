import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderProductsFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private final int orderId;

    public OrderProductsFrame(int orderId) {
        this.orderId = orderId;

        setTitle("Order Products - Order #" + orderId);
        setSize(600, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new Object[]{"ProductID", "Product", "SellerID"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JButton reviewBtn = new JButton("Leave Review");
        JButton closeBtn = new JButton("Close");

        JPanel bottom = new JPanel();
        bottom.add(reviewBtn);
        bottom.add(closeBtn);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadProducts();

        reviewBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a product.");
                return;
            }

            int productId = (int) model.getValueAt(row, 0);
            int sellerId  = (int) model.getValueAt(row, 2);

            new ReviewFrame(orderId, productId, sellerId);
        });

        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadProducts() {
        model.setRowCount(0);

        List<OrderService.OrderProduct> products =
                OrderService.getProductsByOrder(orderId);

        for (OrderService.OrderProduct p : products) {
            model.addRow(new Object[]{
                    p.productId,
                    p.productName,
                    p.sellerId
            });
        }
    }
}
