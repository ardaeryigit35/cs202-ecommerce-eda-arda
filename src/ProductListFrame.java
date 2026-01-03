import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductListFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    private JTextField searchField;
    private JTextField minPriceField;
    private JTextField maxPriceField;
    private JComboBox<String> categoryBox;
    private JComboBox<String> catalogBox;

    public ProductListFrame() {

        setTitle("Browse Products");
        setSize(900, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ================= FILTER PANEL =================
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(12);
        minPriceField = new JTextField(6);
        maxPriceField = new JTextField(6);

        categoryBox = new JComboBox<>(CategoryService.getCategoryNamesWithAll());
        catalogBox = new JComboBox<>(CategoryService.getCatalogNamesWithAll());

        JButton filterBtn = new JButton("Filter");

        top.add(new JLabel("Search:"));
        top.add(searchField);
        top.add(new JLabel("Category:"));
        top.add(categoryBox);
        top.add(new JLabel("Catalog:"));
        top.add(catalogBox);
        top.add(new JLabel("Min Price:"));
        top.add(minPriceField);
        top.add(new JLabel("Max Price:"));
        top.add(maxPriceField);
        top.add(filterBtn);


        model = new DefaultTableModel(
                new Object[]{"ID", "Product", "Category", "Catalog", "Price", "Stock"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide ID

        JButton addBtn = new JButton("Add to Cart");

        filterBtn.addActionListener(e -> loadProducts());
        addBtn.addActionListener(e -> addToCart());

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(addBtn, BorderLayout.SOUTH);

        loadProducts();
        setVisible(true);
    }

    private void loadProducts() {

        String keyword = searchField.getText();
        String category = categoryBox.getSelectedItem().toString();
        String catalog = catalogBox.getSelectedItem().toString();

        Double minPrice = minPriceField.getText().isEmpty() ? null :
                Double.parseDouble(minPriceField.getText());

        Double maxPrice = maxPriceField.getText().isEmpty() ? null :
                Double.parseDouble(maxPriceField.getText());

        model.setRowCount(0);

        List<ProductService.ProductItem> products =
                ProductService.searchProducts(keyword, category, catalog, minPrice, maxPrice);

        for (ProductService.ProductItem p : products) {
            model.addRow(new Object[]{
                    p.productId,
                    p.productName,
                    p.category,
                    p.catalogName,
                    p.price,
                    p.stock
            });
        }
    }

    private void addToCart() {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product.");
            return;
        }

        int productId = (int) model.getValueAt(row, 0);
        int stock = (int) model.getValueAt(row, 5);

        if (stock <= 0) {
            JOptionPane.showMessageDialog(this, "Out of stock.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Product added to cart.");
    }
}
