import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CartFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JButton removeBtn;
    private JButton submitBtn;

    public CartFrame() {
        setTitle("Your Shopping Cart");
        setSize(600, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new Object[]{"ProductID", "Product", "Qty", "Price", "Total"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0));

        removeBtn = new JButton("Remove Selected Item");
        submitBtn = new JButton("Submit Order");

        removeBtn.addActionListener(e -> handleRemove());
        submitBtn.addActionListener(e -> handleSubmit());

        JPanel bottom = new JPanel(new GridLayout(1, 2, 10, 10));
        bottom.add(removeBtn);
        bottom.add(submitBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        reloadCart();
        setVisible(true);
    }

    // ============================
    // REMOVE ITEM
    // ============================
    private void handleRemove() {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item.");
            return;
        }

        int productId = (int) model.getValueAt(row, 0);
        setButtons(false);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                CartService.removeItem(UserSession.getUserId(), productId);
                return null;
            }

            @Override
            protected void done() {
                reloadCart();
            }
        }.execute();
    }

    // ============================
    // SUBMIT ORDER (FIXED)
    // ============================
    private void handleSubmit() {

        setButtons(false); // 🔴 EN KRİTİK FIX

        new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() {
                return CartService.submitOrder(UserSession.getUserId());
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();

                    if (!ok) {
                        JOptionPane.showMessageDialog(
                                CartFrame.this,
                                "Order could not be submitted."
                        );
                        setButtons(true);
                        return;
                    }

                    JOptionPane.showMessageDialog(
                            CartFrame.this,
                            "Order submitted successfully!"
                    );
                    dispose(); // CART kapanır → lifecycle temiz

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            CartFrame.this,
                            "Unexpected error during submit."
                    );
                    e.printStackTrace();
                    setButtons(true);
                }
            }
        }.execute();
    }

    // ============================
    // LOAD CART
    // ============================
    private void reloadCart() {

        new SwingWorker<List<CartService.CartItem>, Void>() {

            @Override
            protected List<CartService.CartItem> doInBackground() {
                return CartService.getCartItems(UserSession.getUserId());
            }

            @Override
            protected void done() {
                try {
                    model.setRowCount(0);
                    List<CartService.CartItem> items = get();

                    if (items.isEmpty()) {
                        setButtons(false);
                        return;
                    }

                    for (CartService.CartItem i : items) {
                        model.addRow(new Object[]{
                                i.productId,
                                i.productName,
                                i.quantity,
                                i.unitPrice,
                                i.total
                        });
                    }

                    setButtons(true);

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            CartFrame.this,
                            "Failed to load cart."
                    );
                    e.printStackTrace();
                    setButtons(false);
                }
            }
        }.execute();
    }

    private void setButtons(boolean enabled) {
        removeBtn.setEnabled(enabled);
        submitBtn.setEnabled(enabled);
    }
}
