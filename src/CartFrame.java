import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CartFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JButton removeBtn;
    private JButton submitBtn;

    private JTextField discountField;
    private JLabel discountInfo;
    private Integer appliedDiscountId = null;
    private int appliedPercent = 0;

    public CartFrame() {

        setTitle("Your Shopping Cart");
        setSize(600, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        model = new DefaultTableModel(
                new Object[]{"ProductID", "Product", "Qty", "Price", "Total"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide ProductID


        JPanel discountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        discountField = new JTextField(10);
        JButton applyBtn = new JButton("Apply Code");
        discountInfo = new JLabel(" ");

        discountPanel.add(new JLabel("Discount Code:"));
        discountPanel.add(discountField);
        discountPanel.add(applyBtn);
        discountPanel.add(discountInfo);

        // =========================
        // BUTTONS
        // =========================
        removeBtn = new JButton("Remove Selected Item");
        submitBtn = new JButton("Submit Order");



        removeBtn.addActionListener(e -> handleRemove());
        submitBtn.addActionListener(e -> handleSubmit());

        JPanel bottom = new JPanel(new GridLayout(1, 3, 10, 10));
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bottom.add(removeBtn);
        bottom.add(submitBtn);

        add(discountPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);


        applyBtn.addActionListener(e -> applyDiscount());

        reloadCart();
        setVisible(true);
    }


    private void applyDiscount() {

        String code = discountField.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a discount code.");
            return;
        }

        CartService.DiscountResult res =
                CartService.validateDiscountCode(
                        UserSession.getUserId(),
                        code
                );

        if (res == null) {
            discountInfo.setText(" Invalid or expired code");
            appliedDiscountId = null;
            appliedPercent = 0;
            recalculateTotals();
            return;
        }

        appliedDiscountId = res.discountId;
        appliedPercent = res.percent;

        discountInfo.setText("Discount applied: %" + appliedPercent);
        recalculateTotals();
    }


    private void recalculateTotals() {

        for (int i = 0; i < model.getRowCount(); i++) {
            int qty = (int) model.getValueAt(i, 2);
            double price = (double) model.getValueAt(i, 3);

            double total = qty * price;

            if (appliedPercent > 0) {
                total = total * (100 - appliedPercent) / 100.0;
            }

            model.setValueAt(total, i, 4);
        }
    }


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
                CartService.removeItem(
                        UserSession.getUserId(),
                        productId
                );
                return null;
            }

            @Override
            protected void done() {
                reloadCart();
            }
        }.execute();
    }


    private void handleSubmit() {

        setButtons(false);

        new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() {
                return CartService.submitOrder(
                        UserSession.getUserId(),
                        appliedDiscountId
                );
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
                    dispose();

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


    private void reloadCart() {

        new SwingWorker<List<CartService.CartItem>, Void>() {

            @Override
            protected List<CartService.CartItem> doInBackground() {
                return CartService.getCartItems(
                        UserSession.getUserId()
                );
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

                    recalculateTotals();

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
