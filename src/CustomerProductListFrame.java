import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerProductListFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    private JTextField searchField;
    private JTextField minPriceField;
    private JTextField maxPriceField;
    private JComboBox<String> categoryBox;
    private JComboBox<String> catalogBox;

    public CustomerProductListFrame() {

        setTitle("Browse Products");
        setSize(900, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(12);
        minPriceField = new JTextField(6);
        maxPriceField = new JTextField(6);

        categoryBox = new JComboBox<>(CategoryService.getCategoryNamesWithAll());
        catalogBox = new JComboBox<>(CatalogService.getCatalogNamesWithAll());

        JButton filterBtn = new JButton("Filter");

        top.add(new JLabel("Search:"));
        top.add(searchField);
        top.add(new JLabel("Category:"));
        top.add(categoryBox);
        top.add(new JLabel("Catalog:"));
        top.add(catalogBox);
        top.add(new JLabel("Min:"));
        top.add(minPriceField);
        top.add(new JLabel("Max:"));
        top.add(maxPriceField);
        top.add(filterBtn);

        model = new DefaultTableModel(
                new Object[]{"ID", "Product", "Category", "Catalog", "Price", "Stock", "SellerID"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0)); // ID
        table.removeColumn(table.getColumnModel().getColumn(5)); // SellerID

        JButton addBtn = new JButton("Add to Cart");
        JButton detailsBtn = new JButton("See Reviews / Details");
        JButton favBtn = new JButton("Add to Favorites");



        filterBtn.addActionListener(e -> loadProducts());
        addBtn.addActionListener(e -> addToCart());
        detailsBtn.addActionListener(e -> openDetails());
        favBtn.addActionListener(e -> addToFavorites());



        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(detailsBtn);
        bottom.add(favBtn);
        bottom.add(addBtn);

        add(bottom, BorderLayout.SOUTH);


        loadProducts();
        setVisible(true);
    }

    private void loadProducts() {

        String keyword = searchField.getText();
        String category = categoryBox.getSelectedItem().toString();
        String catalog = catalogBox.getSelectedItem().toString();

        Double min = minPriceField.getText().isEmpty() ? null :
                Double.parseDouble(minPriceField.getText());

        Double max = maxPriceField.getText().isEmpty() ? null :
                Double.parseDouble(maxPriceField.getText());

        model.setRowCount(0);

        List<ProductService.ProductItem> list =
                ProductService.searchProducts(keyword, category, catalog, min, max);

        for (ProductService.ProductItem p : list) {
            model.addRow(new Object[]{
                    p.productId,
                    p.productName,
                    p.category,
                    p.catalogName,
                    p.price,
                    p.stock,
                    p.sellerId
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


        Number stockVal = (Number) model.getValueAt(row, 5);
        int stock = stockVal.intValue();

        if (stock <= 0) {
            JOptionPane.showMessageDialog(this, "Out of stock.");
            return;
        }

        ProductService.ProductItem selected =
                ProductService.searchProducts(
                                null,
                                "ALL",
                                "ALL",
                                null,
                                null
                        ).stream()
                        .filter(p -> p.productId == productId)
                        .findFirst()
                        .orElse(null);

        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Product not found.");
            return;
        }

        boolean ok = CartService.addToCart(
                UserSession.getUserId(),
                selected.sellerId,
                productId,
                1
        );

        if (!ok) {
            JOptionPane.showMessageDialog(
                    this,
                    "You already have a cart with another seller!"
            );
            return;
        }

        JOptionPane.showMessageDialog(this, "Product added to cart.");
    }
    private void openDetails() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product.");
            return;
        }

        int productId = (int) model.getValueAt(row, 0); // ID kolonu
        new ProductDetailFrame(productId);
    }
    private void addToFavorites() {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product.");
            return;
        }

        int productId = (int) model.getValueAt(row, 0);
        int sellerId  = (int) model.getValueAt(row, 6);

        boolean ok = FavoriteService.addFavorite(
                UserSession.getUserId(),
                productId,
                sellerId
        );

        JOptionPane.showMessageDialog(this, ok ? "Added to favorites." : "Add failed.");
    }



}
