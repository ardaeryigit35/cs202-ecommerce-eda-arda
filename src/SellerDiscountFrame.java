import javax.swing.*;
import java.awt.*;

public class SellerDiscountFrame extends JFrame {

    private JTextField codeField;
    private JTextField percentField;
    private JTextField usageField;

    public SellerDiscountFrame() {

        setTitle("Create Discount Code");
        setSize(350, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        codeField = new JTextField();
        percentField = new JTextField();
        usageField = new JTextField();

        JButton createBtn = new JButton("Create");

        panel.add(new JLabel("Code:"));
        panel.add(codeField);

        panel.add(new JLabel("Discount %:"));
        panel.add(percentField);

        panel.add(new JLabel("Usage Count:"));
        panel.add(usageField);

        panel.add(new JLabel());
        panel.add(createBtn);

        add(panel);

        createBtn.addActionListener(e -> createDiscount());

        setVisible(true);
    }

    private void createDiscount() {

        String code = codeField.getText().trim().toUpperCase();
        int percent;
        int usage;

        try {
            percent = Integer.parseInt(percentField.getText().trim());
            usage = Integer.parseInt(usageField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid number input");
            return;
        }

        boolean ok = DiscountService.createDiscount(
                UserSession.getUserId(),
                code,
                percent,
                usage
        );

        if (ok) {
            JOptionPane.showMessageDialog(this, "Discount code created!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create discount");
        }
    }
}
