import javax.swing.*;
import java.awt.*;

public class SellerDashboard extends JFrame {

    public SellerDashboard() {

        // 🔥 CRITICAL FIX
        // Seller ilk kez giriyorsa default catalog otomatik oluşturulur
        CatalogService.getOrCreateCatalog(UserSession.getUserId());

        setTitle("Seller Dashboard");
        setSize(450, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel welcome = new JLabel(
                "Seller Panel - " + UserSession.getUserName(),
                SwingConstants.CENTER
        );
        welcome.setFont(new Font("Arial", Font.BOLD, 16));

        JButton manage = new JButton("Manage Catalog & Products");
        JButton orders = new JButton("View Orders");
        JButton stats = new JButton("Seller Statistics");
        JButton reviews = new JButton("View Reviews");
        JButton logout = new JButton("Logout");

        // =====================
        // ACTIONS
        // =====================

        manage.addActionListener(e -> new ManageProductsFrame());

        orders.addActionListener(e -> new SellerOrdersFrame());

        stats.addActionListener(e -> new SellerStatsFrame());

        reviews.addActionListener(e -> new SellerReviewsFrame());

        logout.addActionListener(e -> {
            UserSession.clear();
            new HomeFrame();
            dispose();
        });

        panel.add(welcome);
        panel.add(manage);
        panel.add(orders);
        panel.add(stats);
        panel.add(reviews);
        panel.add(logout);

        add(panel);
        setVisible(true);
    }
}
