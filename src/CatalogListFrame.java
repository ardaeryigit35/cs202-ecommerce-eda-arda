import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CatalogListFrame extends JFrame {

    public CatalogListFrame() {

        setTitle("Catalog List");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);


        model.addElement("All catalogs");

        JButton openBtn = new JButton("Open Products");

        openBtn.addActionListener(e -> {

            new CustomerProductListFrame();
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
