import javax.swing.*;
import java.awt.*;

public class HomeFrame extends JFrame {

    public HomeFrame() {
        setTitle("E-Commerce System");
        setSize(420, 280);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6,1,8,8));
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JTextField email = new JTextField();
        JPasswordField pass = new JPasswordField();

        JButton login = new JButton("Login");
        JButton register = new JButton("Sign Up");

        panel.add(new JLabel("Email:"));
        panel.add(email);
        panel.add(new JLabel("Password:"));
        panel.add(pass);
        panel.add(login);
        panel.add(register);

        login.addActionListener(e -> {
            AuthService.SessionData d =
                    AuthService.login(email.getText(), new String(pass.getPassword()));

            if (d == null) {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
                return;
            }

            UserSession.setUser(d.userId, d.email, d.userName, d.role);
            openDashboard(d.role);
            dispose();
        });

        register.addActionListener(e -> {
            new RegisterFrame();
            dispose();
        });

        add(panel);
        setVisible(true);
    }

    private void openDashboard(String role) {
        if (role.equals("CUSTOMER")) new CustomerDashboard();
        else if (role.equals("SELLER")) new SellerDashboard();
        else if (role.equals("ADMIN")) new AdminDashboard();
    }
}
