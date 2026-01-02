import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageUsersFrame extends JFrame {

    private final DefaultTableModel model;
    private final JTable table;

    public ManageUsersFrame() {

        setTitle("Admin - Manage Users");
        setSize(820, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new Object[]{"User ID", "Email", "Username", "Role"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JButton refreshBtn = new JButton("Refresh");
        JButton deleteBtn = new JButton("Delete User");
        JButton addBtn = new JButton("Add User");
        JButton changeRoleBtn = new JButton("Change Role");
        JButton closeBtn = new JButton("Close");

        JComboBox<String> roleBox =
                new JComboBox<>(new String[]{"CUSTOMER", "SELLER"});

        JPanel top = new JPanel();
        top.add(refreshBtn);
        top.add(deleteBtn);
        top.add(new JLabel("New Role:"));
        top.add(roleBox);
        top.add(changeRoleBtn);
        top.add(addBtn);
        top.add(closeBtn);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        loadUsers();

        // ================= ACTIONS =================

        refreshBtn.addActionListener(e -> loadUsers());

        deleteBtn.addActionListener(e -> deleteSelectedUser());

        changeRoleBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a user.");
                return;
            }

            int userId = (int) model.getValueAt(row, 0);
            String currentRole = model.getValueAt(row, 3).toString();
            String newRole = roleBox.getSelectedItem().toString();

            if (currentRole.equals("ADMIN")) {
                JOptionPane.showMessageDialog(
                        this,
                        "ADMIN role cannot be changed."
                );
                return;
            }

            if (currentRole.equals(newRole)) {
                JOptionPane.showMessageDialog(
                        this,
                        "User already has this role."
                );
                return;
            }

            if (AdminUserService.updateUserRole(userId, newRole)) {
                JOptionPane.showMessageDialog(this, "Role updated.");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Role update failed.");
            }
        });

        addBtn.addActionListener(e -> openAddUserDialog());

        closeBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    // ================= LOAD USERS =================
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

    // ================= DELETE USER =================
    private void deleteSelectedUser() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user.");
            return;
        }

        int userId = (int) model.getValueAt(row, 0);
        String role = model.getValueAt(row, 3).toString();

        if (role.equals("ADMIN")) {
            JOptionPane.showMessageDialog(
                    this,
                    "ADMIN user cannot be deleted."
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete selected user?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        if (AdminUserService.deleteUser(userId)) {
            loadUsers();
        } else {
            if (!AdminUserService.deleteUser(userId)) {
                JOptionPane.showMessageDialog(
                        this,
                        "User cannot be deleted.\n\n" +
                                "• Admin users cannot be removed\n" +
                                "• Users with orders cannot be deleted",
                        "Delete Failed",
                        JOptionPane.WARNING_MESSAGE
                );
            } else {
                loadUsers();
            }

        }
    }

    // ================= ADD USER DIALOG =================
    private void openAddUserDialog() {

        JTextField email = new JTextField();
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JComboBox<String> roleBox =
                new JComboBox<>(new String[]{"CUSTOMER", "SELLER" });

        Object[] fields = {
                "Email:", email,
                "Username:", username,
                "Password:", password,
                "Role:", roleBox
        };

        int res = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Add New User",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (res != JOptionPane.OK_OPTION) return;

        if (email.getText().isEmpty()
                || username.getText().isEmpty()
                || password.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "All fields required.");
            return;
        }

        boolean ok = AdminUserService.addUser(
                email.getText(),
                new String(password.getPassword()),
                username.getText(),
                roleBox.getSelectedItem().toString()
        );

        if (ok) {
            JOptionPane.showMessageDialog(this, "User added.");
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "Add user failed.");
        }
    }
}
