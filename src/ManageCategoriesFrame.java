import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageCategoriesFrame extends JFrame {

    private final DefaultTableModel model;
    private final JTable table;

    public ManageCategoriesFrame() {

        setTitle("Admin - Manage Categories");
        setSize(600, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new Object[]{"Category ID", "Category Name"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JTextField nameField = new JTextField(20);
        JButton addBtn = new JButton("Add Category");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");

        JPanel top = new JPanel();
        top.add(new JLabel("Category Name:"));
        top.add(nameField);
        top.add(addBtn);

        JPanel bottom = new JPanel();
        bottom.add(deleteBtn);
        bottom.add(refreshBtn);
        bottom.add(closeBtn);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadCategories();

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Category name required.");
                return;
            }

            if (AdminCategoryService.addCategory(name)) {
                nameField.setText("");
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this, "Add failed (maybe duplicate).");
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;

            int categoryId = (int) model.getValueAt(row, 0);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete selected category?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            if (AdminCategoryService.deleteCategory(categoryId)) {
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.");
            }
        });

        refreshBtn.addActionListener(e -> loadCategories());
        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadCategories() {
        model.setRowCount(0);
        for (AdminCategoryService.CategoryItem c :
                AdminCategoryService.getCategories()) {
            model.addRow(new Object[]{c.categoryId, c.name});
        }
    }
}
