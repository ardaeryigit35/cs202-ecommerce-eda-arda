import javax.swing.*;
import java.awt.*;

public class AddProductFrame extends JFrame {

    private final int sellerId;

    public AddProductFrame(int sellerId) {

        this.sellerId = sellerId;

        setTitle("Add New Product");
        setSize(360, 330);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTextField name = new JTextField();
        JTextField price = new JTextField();
        JTextField stock = new JTextField();

        JComboBox<String> categoryBox =
                new JComboBox<>(CategoryService.getCategoryNames());

        Object[] fields = {
                "Product Name:", name,
                "Category:", categoryBox,
                "Price:", price,
                "Initial Stock:", stock
        };

        int res = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Add Product",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (res != JOptionPane.OK_OPTION) return;

        if (name.getText().isEmpty()
                || price.getText().isEmpty()
                || stock.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        boolean ok;
        try {
            ok = ProductService.addProduct(
                    sellerId,
                    name.getText(),
                    categoryBox.getSelectedItem().toString(),
                    Double.parseDouble(price.getText()),
                    Integer.parseInt(stock.getText())
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and stock must be numbers.");
            return;
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, "Product added.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add product.");
        }
    }
}
