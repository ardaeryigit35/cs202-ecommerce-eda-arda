import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminUserService {

    // ========================
    // DTO
    // ========================
    public static class UserItem {
        public final int userId;
        public final String email;
        public final String userName;
        public final String role;

        public UserItem(int userId, String email, String userName, String role) {
            this.userId = userId;
            this.email = email;
            this.userName = userName;
            this.role = role;
        }
    }

    // ========================
    // GET ALL USERS
    // ========================
    public static List<UserItem> getAllUsers() {

        String sql = """
            SELECT u.UserID, u.email, u.UserName, r.role
            FROM User u
            JOIN UserRole r ON r.UserID = u.UserID
            ORDER BY u.UserID
        """;

        List<UserItem> list = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new UserItem(
                        rs.getInt("UserID"),
                        rs.getString("email"),
                        rs.getString("UserName"),
                        rs.getString("role")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ========================
    // DELETE USER
    // ========================
    public static boolean deleteUser(int userId) {

        String sql = "DELETE FROM User WHERE UserID = ?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            return false;
        }
    }
    // ========================
// ADD USER (ADMIN)
// ========================
    public static boolean addUser(
            String email,
            String password,
            String userName,
            String role
    ) {

        // 🔒 ADMIN EKLENEMEZ
        if (!role.equals("CUSTOMER") && !role.equals("SELLER"))
            return false;

        String insertUser =
                "INSERT INTO User (email, password, UserName) VALUES (?, ?, ?)";
        String insertRole =
                "INSERT INTO UserRole (UserID, role) VALUES (?, ?)";

        try (Connection c = DB.getConnection()) {
            c.setAutoCommit(false);

            int userId;

            try (PreparedStatement ps =
                         c.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, email);
                ps.setString(2, password);
                ps.setString(3, userName);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) {
                    c.rollback();
                    return false;
                }
                userId = rs.getInt(1);
            }

            try (PreparedStatement ps2 = c.prepareStatement(insertRole)) {
                ps2.setInt(1, userId);
                ps2.setString(2, role);
                ps2.executeUpdate();
            }

            c.commit();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }


    // ========================
// UPDATE USER ROLE (ADMIN)
// ========================
    public static boolean updateUserRole(int userId, String newRole) {

        if (!newRole.equals("CUSTOMER") && !newRole.equals("SELLER"))
            return false; // ADMIN role değiştirilemez

        String sql =
                "UPDATE UserRole SET role = ? WHERE UserID = ? AND role <> 'ADMIN'";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newRole);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            return false;
        }
    }

}
