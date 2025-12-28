import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SellerOrdersFrame extends JFrame {

    private final DefaultTableModel model;
    private final JTable table;

    public SellerOrdersFrame() {
        setTitle("Orders for My Catalog");
        setSize(750, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new Object[]{"Order ID", "Date", "Status", "Total", "Confirmed"}, 0
        ) {

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JButton shipBtn = new JButton("Mark as Shipped");
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(shipBtn);
        bottom.add(refreshBtn);
        bottom.add(closeBtn);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadOrders();

        shipBtn.addActionListener(e -> shipSelectedOrder());
        refreshBtn.addActionListener(e -> loadOrders());
        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadOrders() {
        model.setRowCount(0);

        List<SellerOrderService.SellerOrder> orders =
                SellerOrderService.getOrdersForSeller(UserSession.getUserId());

        for (SellerOrderService.SellerOrder o : orders) {
            model.addRow(new Object[]{
                    o.orderId,
                    o.orderDate,
                    o.status,
                    o.totalAmount,
                    o.confirmed
            });
        }
    }

    private void shipSelectedOrder() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order.");
            return;
        }

        String status = model.getValueAt(row, 2).toString();
        if (!"PENDING".equals(status)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Only PENDING orders can be shipped."
            );
            return;
        }

        int orderId = (int) model.getValueAt(row, 0);

        boolean ok = SellerOrderService.shipOrder(
                orderId,
                UserSession.getUserId()
        );

        if (ok) {
            JOptionPane.showMessageDialog(this, "Order marked as SHIPPED.");
            loadOrders();
        } else {
            JOptionPane.showMessageDialog(this, "Operation failed.");
        }
    }
}
