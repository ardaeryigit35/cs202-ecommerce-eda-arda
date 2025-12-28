import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageUsersFrame extends JFrame {

    private final DefaultTableModel model;
    private final JTable table;

    public ManageUsersFrame() {

        setTitle("Admin - Manage Users");
        setSize(750, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new Object[]{"User ID", "Email", "Username", "Role"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JButton refreshBtn = new JButton("Refresh");
        JButton deleteBtn = new JButton("Delete User");
        JButton closeBtn = new JButton("Close");

        JPanel top = new JPanel();
        top.add(refreshBtn);
        top.add(deleteBtn);
        top.add(closeBtn);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        loadUsers();

        refreshBtn.addActionListener(e -> loadUsers());

        deleteBtn.addActionListener(e -> deleteSelectedUser());

        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadUsers() {
        model.setRowCount(0);
        for (AdminUserService.UserItem u : AdminUserService.getAllUsers()) {
            model.addRow(new Object[]{
                    u.userId,
                    u.email,
                    u.userName,
                    u.role
            });
        }
    }

    private void deleteSelectedUser() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user.");
            return;
        }

        int userId = (int) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this user?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        if (AdminUserService.deleteUser(userId)) {
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "Delete failed.");
        }
    }
}
