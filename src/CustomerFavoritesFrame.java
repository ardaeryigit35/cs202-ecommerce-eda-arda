import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerFavoritesFrame extends JFrame {

    private final int customerId;
    private final DefaultTableModel model;
    private final JTable table;

    public CustomerFavoritesFrame(int customerId) {
        this.customerId = customerId;

        setTitle("Customer - Favorites");
        setSize(850, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new Object[]{
                "Product ID", "Seller ID", "Product Name", "Price", "Added At"
        }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JButton refreshBtn = new JButton("Refresh");
        JButton removeBtn = new JButton("Remove from Favorites");
        JButton closeBtn = new JButton("Close");

        JPanel bottom = new JPanel();
        bottom.add(refreshBtn);
        bottom.add(removeBtn);
        bottom.add(closeBtn);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadFavorites());

        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a favorite to remove.");
                return;
            }

            int productId = (int) model.getValueAt(row, 0);
            int sellerId  = (int) model.getValueAt(row, 1);

            boolean ok = FavoriteService.removeFavorite(customerId, productId, sellerId);
            JOptionPane.showMessageDialog(this, ok ? "Removed." : "Remove failed.");
            if (ok) loadFavorites();
        });

        closeBtn.addActionListener(e -> dispose());

        loadFavorites();
        setVisible(true);
    }

    private void loadFavorites() {
        model.setRowCount(0);
        for (FavoriteService.FavoriteItem f : FavoriteService.getFavorites(customerId)) {
            model.addRow(new Object[]{
                    f.productId,
                    f.sellerId,
                    f.productName,
                    f.price,
                    f.createdAt
            });
        }
    }
}
