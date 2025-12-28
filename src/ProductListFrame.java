import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductListFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JButton addBtn;
    private JButton detailBtn;

    private final int sellerId;

    public ProductListFrame(int sellerId, String catalogName) {
        this.sellerId = sellerId;

        setTitle("Products - " + catalogName);
        setSize(650, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new Object[]{"ProductID", "Product", "Category", "Price", "Stock"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide ProductID

        // ============================
        // BUTTONS
        // ============================
        addBtn = new JButton("Add to Cart");
        detailBtn = new JButton("View Details");

        addBtn.addActionListener(e -> handleAdd());
        detailBtn.addActionListener(e -> handleDetail());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(detailBtn);
        bottom.add(addBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadProducts();
        setVisible(true);
    }

    // ============================
    // VIEW DETAILS
    // ============================
    private void handleDetail() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product.");
            return;
        }

        int productId = (int) model.getValueAt(row, 0);
        new ProductDetailFrame(productId);
    }

    // ============================
    // ADD TO CART (SAFE)
    // ============================
    private void handleAdd() {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product.");
            return;
        }

        int stock = (int) model.getValueAt(row, 4);
        if (stock <= 0) {
            JOptionPane.showMessageDialog(this, "Out of stock.");
            return;
        }

        int productId = (int) model.getValueAt(row, 0);
        addBtn.setEnabled(false);

        new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() {
                return CartService.addToCart(
                        UserSession.getUserId(),
                        sellerId,
                        productId,
                        1
                );
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();

                    if (!ok) {
                        JOptionPane.showMessageDialog(
                                ProductListFrame.this,
                                "You already have an active cart with another seller."
                        );
                        return;
                    }

                    JOptionPane.showMessageDialog(
                            ProductListFrame.this,
                            "Product added to cart.\nStock will be updated after order submission."
                    );

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            ProductListFrame.this,
                            "Failed to add product."
                    );
                    e.printStackTrace();
                } finally {
                    addBtn.setEnabled(true);
                }
            }
        }.execute();
    }

    // ============================
    // LOAD PRODUCTS
    // ============================
    private void loadProducts() {

        new SwingWorker<List<ProductService.ProductItem>, Void>() {

            @Override
            protected List<ProductService.ProductItem> doInBackground() {
                return ProductService.getProductsBySeller(sellerId);
            }

            @Override
            protected void done() {
                try {
                    model.setRowCount(0);

                    for (ProductService.ProductItem p : get()) {
                        model.addRow(new Object[]{
                                p.productId,
                                p.productName,
                                p.category,
                                p.price,
                                p.stock
                        });
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            ProductListFrame.this,
                            "Failed to load products."
                    );
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
