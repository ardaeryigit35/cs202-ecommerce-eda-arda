import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    public RegisterFrame() {
        setTitle("Sign Up");
        setSize(460, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JTextField emailField = new JTextField(22);
        JPasswordField passField = new JPasswordField(22);
        JTextField nameField = new JTextField(22);

        JComboBox<String> roleBox = new JComboBox<>(new String[]{"CUSTOMER", "SELLER"});

        JButton createBtn = new JButton("Create");
        JButton backBtn = new JButton("Back");

        int row = 0;

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        panel.add(title, c);

        row++;
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Email:"), c);
        c.gridx = 1; c.gridy = row; panel.add(emailField, c);

        row++;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Password:"), c);
        c.gridx = 1; c.gridy = row; panel.add(passField, c);

        row++;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Username:"), c);
        c.gridx = 1; c.gridy = row; panel.add(nameField, c);

        row++;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Role:"), c);
        c.gridx = 1; c.gridy = row; panel.add(roleBox, c);

        row++;
        c.gridx = 0; c.gridy = row; panel.add(createBtn, c);
        c.gridx = 1; c.gridy = row; panel.add(backBtn, c);

        createBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword());
            String uname = nameField.getText().trim();
            String role = (String) roleBox.getSelectedItem();

            if (email.isEmpty() || pass.isEmpty() || uname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields.");
                return;
            }

            String err = AuthService.register(email, pass, uname, role);
            if (err != null) {
                JOptionPane.showMessageDialog(this, err);
                return;
            }

            JOptionPane.showMessageDialog(this, "Account created! Please login.");
            new HomeFrame();
            dispose();
        });

        backBtn.addActionListener(e -> {
            new HomeFrame();
            dispose();
        });

        add(panel);
        setVisible(true);
    }
}
