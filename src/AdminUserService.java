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
}
