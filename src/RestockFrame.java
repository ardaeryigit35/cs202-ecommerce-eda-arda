import javax.swing.*;
import java.awt.*;

public class RestockFrame extends JFrame {

    public RestockFrame(int productId) {

        setTitle("Restock Product");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTextField qtyField = new JTextField();
        JTextField noteField = new JTextField();

        JButton restockBtn = new JButton("Restock");

        restockBtn.addActionListener(e -> {
            int qty;

            try {
                qty = Integer.parseInt(qtyField.getText());
                if (qty <= 0) throw new NumberFormatException();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter valid quantity.");
                return;
            }

            boolean ok = RestockService.restockProduct(   // 🔴 FIX BURADA
                    productId,
                    UserSession.getUserId(),
                    qty,
                    noteField.getText()
            );

            if (ok) {
                JOptionPane.showMessageDialog(this, "Stock updated.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Restock failed.");
            }
        });

        JPanel p = new JPanel(new GridLayout(5,1,8,8));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        p.add(new JLabel("Added Quantity:"));
        p.add(qtyField);
        p.add(new JLabel("Note (optional):"));
        p.add(noteField);
        p.add(restockBtn);

        add(p);
        setVisible(true);
    }
}
