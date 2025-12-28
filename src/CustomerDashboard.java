import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JFrame {

    public CustomerDashboard() {
        setTitle("Customer Dashboard");
        setSize(450, 340);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel welcome = new JLabel(
                "Welcome, " + UserSession.getUserName(),
                SwingConstants.CENTER
        );
        welcome.setFont(new Font("Arial", Font.BOLD, 16));

        JButton viewCatalogs = new JButton("View Catalogs");
        JButton viewCart = new JButton("View Ongoing Order");
        JButton history = new JButton("View Order History");
        JButton stats = new JButton("Customer Statistics");
        JButton logout = new JButton("Logout");

        // ===== ACTIONS =====
        viewCatalogs.addActionListener(e -> new CatalogListFrame());

        viewCart.addActionListener(e -> new CartFrame());

        history.addActionListener(e -> new OrderHistoryFrame());

        stats.addActionListener(e -> new CustomerStatsFrame());

        logout.addActionListener(e -> {
            UserSession.clear();
            new HomeFrame();
            dispose();
        });

        panel.add(welcome);
        panel.add(viewCatalogs);
        panel.add(viewCart);
        panel.add(history);
        panel.add(stats);
        panel.add(logout);

        add(panel);
        setVisible(true);
    }
}
