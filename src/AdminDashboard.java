import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {

        setTitle("Admin Dashboard");
        setSize(500, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== TOP =====
        JLabel title = new JLabel(
                "Welcome Admin: " + UserSession.getUserName(),
                SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // ===== CENTER BUTTONS =====
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        JButton usersBtn = new JButton("Manage Users");
        JButton categoriesBtn = new JButton("Manage Categories");
        JButton shipmentsBtn = new JButton("Manage Shipments");
        JButton logoutBtn = new JButton("Logout");

        centerPanel.add(usersBtn);
        centerPanel.add(categoriesBtn);
        centerPanel.add(shipmentsBtn);
        centerPanel.add(logoutBtn);

        add(centerPanel, BorderLayout.CENTER);

        // ===== ACTIONS =====
        usersBtn.addActionListener(e -> new ManageUsersFrame());

        categoriesBtn.addActionListener(e -> new ManageCategoriesFrame());

        shipmentsBtn.addActionListener(e -> new ManageShipmentsFrame());

        logoutBtn.addActionListener(e -> {
            UserSession.clear();
            new HomeFrame();
            dispose();
        });

        setVisible(true);
    }
}
