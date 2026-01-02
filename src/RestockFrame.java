import javax.swing.*;
import java.awt.*;

public class RestockFrame extends JFrame {

    public RestockFrame(int productId, boolean isDestock) {

        setTitle(isDestock ? "Destock Product" : "Restock Product");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTextField qtyField = new JTextField();
        JTextField noteField = new JTextField();

        JButton restockBtn = new JButton(isDestock ? "Destock" : "Restock");

        restockBtn.addActionListener(e -> {
            int qty;

            try {
                qty = Integer.parseInt(qtyField.getText());
                if (qty <= 0) throw new NumberFormatException();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter valid quantity.");
                return;
            }

            boolean ok = isDestock
                    ? RestockService.destockProduct(productId, UserSession.getUserId(), qty, noteField.getText())
                    : RestockService.restockProduct(productId, UserSession.getUserId(), qty, noteField.getText());


            if (ok) {
                JOptionPane.showMessageDialog(this, "Stock updated.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Stock update failed.");
            }
        });

        JPanel p = new JPanel(new GridLayout(5,1,8,8));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        String qtyLabelText = isDestock
                ? "Decreased Quantity:"
                : "Added Quantity:";

        p.add(new JLabel(qtyLabelText));

        p.add(qtyField);
        p.add(new JLabel("Note (optional):"));
        p.add(noteField);
        p.add(restockBtn);

        add(p);
        setVisible(true);
    }
}
