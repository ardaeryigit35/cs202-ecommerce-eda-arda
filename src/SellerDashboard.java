import javax.swing.*;
import java.awt.*;

public class SellerDashboard extends JFrame {

    public SellerDashboard() {

        int sellerId = UserSession.getUserId();

        CatalogService.createCatalogIfNotExists(sellerId);

        setTitle("Seller Dashboard");
        setSize(450, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(8, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel welcome = new JLabel(
                "Seller Panel - " + UserSession.getUserName(),
                SwingConstants.CENTER
        );
        welcome.setFont(new Font("Arial", Font.BOLD, 16));

        JButton manageProductsBtn = new JButton("Manage Products");
        JButton changeCatalogBtn = new JButton("Change Catalog Name");
        JButton ordersBtn = new JButton("View Orders");
        JButton reviewsBtn = new JButton("My Product Reviews");
        JButton discountBtn = new JButton("Create Discount Code");


        JButton statsBtn = new JButton("Seller Statistics");

        JButton logoutBtn = new JButton("Logout");

        // ▶ Manage Products
        manageProductsBtn.addActionListener(e ->
                new ManageProductsFrame(sellerId)
        );

        changeCatalogBtn.addActionListener(e ->
                new ChangeCatalogNameFrame(sellerId)
        );
        discountBtn.addActionListener(e -> new SellerDiscountFrame());


        ordersBtn.addActionListener(e ->
                new SellerOrdersFrame(sellerId)
        );
        reviewsBtn.addActionListener(e -> new SellerReviewsFrame());

        statsBtn.addActionListener(e ->
                new SellerStatsFrame()
        );

        logoutBtn.addActionListener(e -> {
            UserSession.clear();
            new HomeFrame();
            dispose();
        });

        panel.add(welcome);
        panel.add(manageProductsBtn);
        panel.add(changeCatalogBtn);
        panel.add(ordersBtn);
        panel.add(reviewsBtn);
        panel.add(statsBtn);
        panel.add(discountBtn);
        panel.add(logoutBtn);


        add(panel);
        setVisible(true);
    }
}
