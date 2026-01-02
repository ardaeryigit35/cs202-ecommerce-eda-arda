import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderHistoryFrame extends JFrame {

    private final DefaultTableModel model;
    private final JTable table;

    public OrderHistoryFrame() {
        setTitle("My Orders");
        setSize(650, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new Object[]{"Order ID", "Date", "Status", "Total Amount"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JButton reviewBtn = new JButton("Leave Review");
        JButton closeBtn = new JButton("Close");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(reviewBtn);
        bottom.add(closeBtn);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadOrders();

        // ✅ REVIEW FLOW (ORDER → PRODUCTS → REVIEW)
        reviewBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an order.");
                return;
            }

            String status = model.getValueAt(row, 2).toString();
            if (!("SHIPPED".equals(status) || "DELIVERED".equals(status))) {
                JOptionPane.showMessageDialog(this,
                        "You can leave a review only for SHIPPED or DELIVERED orders.");
                return;
            }


            int orderId = (int) model.getValueAt(row, 0);

            // 🔴 KRİTİK DEĞİŞİKLİK
            new OrderProductsFrame(orderId);
        });

        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadOrders() {
        model.setRowCount(0);

        List<OrderService.OrderItem> orders =
                OrderService.getOrdersByCustomer(UserSession.getUserId());

        for (OrderService.OrderItem o : orders) {
            model.addRow(new Object[]{
                    o.orderId,
                    o.orderDate,
                    o.status,
                    o.totalAmount
            });
        }
    }
}
