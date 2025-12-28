import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CatalogListFrame extends JFrame {

    public CatalogListFrame() {
        setTitle("Seller Catalogs");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        DefaultListModel<CatalogItem> model = new DefaultListModel<>();
        JList<CatalogItem> list = new JList<>(model);

        String sql = """
            SELECT c.CatalogID, c.catalog_name, u.UserID, u.UserName
            FROM Catalog c
            JOIN `User` u ON u.UserID = c.SellerID
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addElement(new CatalogItem(
                        rs.getInt("CatalogID"),
                        rs.getInt("UserID"),
                        rs.getString("catalog_name"),
                        rs.getString("UserName")
                ));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading catalogs.");
        }

        JButton openBtn = new JButton("Open Catalog");
        openBtn.addActionListener(e -> {
            CatalogItem item = list.getSelectedValue();
            if (item == null) {
                JOptionPane.showMessageDialog(this, "Select a catalog.");
                return;
            }
            new ProductListFrame(item.sellerId, item.catalogName);
        });

        add(new JScrollPane(list), BorderLayout.CENTER);
        add(openBtn, BorderLayout.SOUTH);
        setVisible(true);
    }

    // 🔹 INNER DTO – field isimleri NET
    static class CatalogItem {
        int catalogId;
        int sellerId;
        String catalogName;
        String sellerName;

        CatalogItem(int catalogId, int sellerId, String catalogName, String sellerName) {
            this.catalogId = catalogId;
            this.sellerId = sellerId;
            this.catalogName = catalogName;
            this.sellerName = sellerName;
        }

        @Override
        public String toString() {
            return catalogName + " (Seller: " + sellerName + ")";
        }
    }
}
