import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminProductFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public AdminProductFrame() {

        setTitle("Admin - All Products");
        setSize(820, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new Object[]{"Product ID", "Product", "Seller", "Category", "Price", "Stock"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(refreshBtn);
        bottom.add(closeBtn);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadProducts());
        closeBtn.addActionListener(e -> dispose());

        loadProducts();
        setVisible(true);
    }


    private void loadProducts() {

        model.setRowCount(0);

        List<ProductService.AdminProductItem> products =
                ProductService.getAllProductsForAdmin();

        for (ProductService.AdminProductItem p : products) {
            model.addRow(new Object[]{
                    p.productId,
                    p.productName,
                    p.sellerName,
                    p.category,
                    p.price,
                    p.stock
            });
        }
    }
}
