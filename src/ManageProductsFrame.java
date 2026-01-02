import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageProductsFrame extends JFrame {

    private final int sellerId;
    private final DefaultTableModel model;

    public ManageProductsFrame(int sellerId) {

        this.sellerId = sellerId;

        SellerInventoryService.getOrCreateCatalog(sellerId);

        setTitle("Manage My Products");
        setSize(820, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new Object[]{"ProductID", "Name", "Category", "Price", "Stock"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JButton addBtn = new JButton("Add Product");
        JButton restockBtn = new JButton("Restock");
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");

        JPanel bottom = new JPanel();
        bottom.add(addBtn);
        bottom.add(restockBtn);
        bottom.add(refreshBtn);
        bottom.add(closeBtn);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadProducts();

        addBtn.addActionListener(e ->
                new AddProductFrame(sellerId)
        );

        restockBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a product.");
                return;
            }
            int productId = (int) model.getValueAt(row, 0);
            new RestockFrame(productId, false);
        });

        refreshBtn.addActionListener(e -> loadProducts());
        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadProducts() {
        model.setRowCount(0);

        List<ProductService.ProductItem> products =
                ProductService.getProductsBySeller(sellerId);

        for (ProductService.ProductItem p : products) {
            model.addRow(new Object[]{
                    p.productId,
                    p.productName,
                    p.category,
                    p.price,
                    p.stock
            });
        }
    }
}
