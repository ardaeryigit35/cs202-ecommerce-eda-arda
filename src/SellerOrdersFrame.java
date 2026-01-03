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
        JButton cancelBtn = new JButton("Mark as Cancelled");
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(confirmBtn);
        bottom.add(cancelBtn);
        bottom.add(refreshBtn);
        bottom.add(closeBtn);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadOrders();

        confirmBtn.addActionListener(e -> confirmSelectedOrder());
        refreshBtn.addActionListener(e -> loadOrders());
        cancelBtn.addActionListener(e -> {
            System.out.println("CANCEL BUTTON CLICKED");
            cancelSelectedOrder();
        });
        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }


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


    private void cancelSelectedOrder() {
        System.out.println("ENTER cancelSelectedOrder");
        int rowq = table.getSelectedRow();
        System.out.println("selectedRow=" + rowq);




        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order.");
            return;
        }

        Object stObj = model.getValueAt(row, 2);
        String status = stObj == null ? "" : stObj.toString();
        System.out.println("status=" + status);


        if (!"PENDING".equals(status) && !"PAID".equals(status)&& !"SHIPPED".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only PENDING, PAID or SHIPPED orders can be cancelled.");
            return;
        }

        Object idObj = model.getValueAt(row, 0);
        System.out.println("idObj class=" + idObj.getClass() + " value=" + idObj);

        int orderId = Integer.parseInt(idObj.toString());


        boolean ok = SellerOrderService.cancelOrder(orderId, sellerId);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Order marked as CANCELLED.");
            loadOrders();
        } else {
            JOptionPane.showMessageDialog(this, "Operation failed.");
        }

    }


}
