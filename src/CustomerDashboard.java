import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JFrame {

    public CustomerDashboard() {

        setTitle("Customer Dashboard");
        setSize(450, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(7, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel welcome = new JLabel(
                "Welcome, " + UserSession.getUserName(),
                SwingConstants.CENTER
        );
        welcome.setFont(new Font("Arial", Font.BOLD, 16));

        JButton browseBtn = new JButton("Browse Products");
        JButton cartBtn   = new JButton("My Cart");
        JButton ordersBtn = new JButton("My Orders");
        JButton statsBtn = new JButton("My Statistics");
        JButton favoritesBtn = new JButton("Favorites");
        JButton logoutBtn = new JButton("Logout");

        browseBtn.addActionListener(e ->
                new CustomerProductListFrame()
        );


        cartBtn.addActionListener(e ->
                new CartFrame()   // aşağıda veriyorum
        );

        ordersBtn.addActionListener(e ->
                new OrderHistoryFrame()
        );
        statsBtn.addActionListener(e -> new CustomerStatsFrame());
        favoritesBtn.addActionListener(e -> new CustomerFavoritesFrame(UserSession.getUserId()));


        logoutBtn.addActionListener(e -> {
            UserSession.clear();
            new HomeFrame();
            dispose();
        });

        panel.add(welcome);
        panel.add(browseBtn);
        panel.add(cartBtn);
        panel.add(ordersBtn);
        panel.add(statsBtn);
        panel.add(favoritesBtn);
        panel.add(logoutBtn);

        add(panel);
        setVisible(true);
    }
}
