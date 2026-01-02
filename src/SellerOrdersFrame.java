import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SellerOrdersFrame extends JFrame {

    private final int sellerId;
    private final DefaultTableModel model;
    private final JTable table;

    public SellerOrdersFrame(int sellerId) {

        this.sellerId = sellerId;

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

        JButton confirmBtn = new JButton("Confirm (Mark as Paid)");
        JButton shipBtn = new JButton("Mark as Shipped");
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(confirmBtn);
        bottom.add(shipBtn);
        bottom.add(refreshBtn);
        bottom.add(closeBtn);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadOrders();

        confirmBtn.addActionListener(e -> confirmSelectedOrder());
        shipBtn.addActionListener(e -> shipSelectedOrder());
        refreshBtn.addActionListener(e -> loadOrders());
        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    // =========================
    // LOAD ORDERS
    // =========================
    private void loadOrders() {

        model.setRowCount(0);

        List<SellerOrderService.SellerOrder> orders =
                SellerOrderService.getOrdersForSeller(sellerId);

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

    // =========================
    // CONFIRM ORDER
    // =========================
    private void confirmSelectedOrder() {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order.");
            return;
        }

        String status = model.getValueAt(row, 2).toString();
        if (!"PENDING".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only PENDING orders can be confirmed.");
            return;
        }

        int orderId = (int) model.getValueAt(row, 0);

        boolean ok = SellerOrderService.confirmOrder(orderId, sellerId);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Order confirmed. Status is now PAID.");
            loadOrders();
        } else {
            JOptionPane.showMessageDialog(this, "Operation failed.");
        }
    }

    // =========================
    // SHIP ORDER
    // =========================
    private void shipSelectedOrder() {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order.");
            return;
        }

        String status = model.getValueAt(row, 2).toString();
        if (!"PAID".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only PAID orders can be shipped.");
            return;
        }

        int orderId = (int) model.getValueAt(row, 0);

        boolean ok = SellerOrderService.shipOrder(orderId, sellerId);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Order marked as SHIPPED.");
            loadOrders();
        } else {
            JOptionPane.showMessageDialog(this, "Operation failed.");
        }
    }
}
