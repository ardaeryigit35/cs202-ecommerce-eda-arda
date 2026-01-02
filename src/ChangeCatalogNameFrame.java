import javax.swing.*;

public class ChangeCatalogNameFrame extends JFrame {

    public ChangeCatalogNameFrame(int sellerId) {

        setTitle("Change Catalog Name");
        setSize(300, 150);
        setLocationRelativeTo(null);

        String currentName = CatalogService.getCatalogName(sellerId);

        JTextField nameField = new JTextField(currentName);

        Object[] fields = {
                "Catalog Name:", nameField
        };

        int res = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Edit Catalog Name",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (res != JOptionPane.OK_OPTION) return;

        if (nameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.");
            return;
        }

        boolean ok = CatalogService.updateCatalogName(
                sellerId,
                nameField.getText()
        );

        if (ok) {
            JOptionPane.showMessageDialog(this, "Catalog name updated.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed.");
        }
    }
}
