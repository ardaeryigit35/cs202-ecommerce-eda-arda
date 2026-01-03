import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageShipmentsFrame extends JFrame {

    private final DefaultTableModel model;
    private final JTable table;

    public ManageShipmentsFrame() {

        setTitle("Admin - Manage Shipments");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new Object[]{"Shipment ID", "Order ID", "Shipment Status", "Payment Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JComboBox<String> statusBox = new JComboBox<>(
                new String[]{"SHIPPED", "DELIVERED", "CANCELLED"}
        );

        JButton updateBtn = new JButton("Update Status");
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");

        JPanel bottom = new JPanel();
        bottom.add(new JLabel("New Status:"));
        bottom.add(statusBox);
        bottom.add(updateBtn);
        bottom.add(refreshBtn);
        bottom.add(closeBtn);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadShipments();

        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a shipment.");
                return;
            }

            String paymentStatus = model.getValueAt(row, 3).toString();
            if (!"DONE".equals(paymentStatus)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Shipment can be updated only if payment is DONE."
                );
                return;
            }

            int shipmentId = (int) model.getValueAt(row, 0);
            String newStatus = statusBox.getSelectedItem().toString();

            if (AdminShipmentService.updateShipmentStatus(shipmentId, newStatus)) {
                JOptionPane.showMessageDialog(this, "Shipment updated.");
                loadShipments();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }
        });

        refreshBtn.addActionListener(e -> loadShipments());
        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadShipments() {
        model.setRowCount(0);
        for (AdminShipmentService.ShipmentItem s :
                AdminShipmentService.getAllShipments()) {
            model.addRow(new Object[]{
                    s.shipmentId,
                    s.orderId,
                    s.shipmentStatus,
                    s.paymentStatus
            });
        }
    }
}
