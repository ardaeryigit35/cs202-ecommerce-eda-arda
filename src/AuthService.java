import java.sql.*;

public class AuthService {

    public static class SessionData {
        public final int userId;
        public final String email;
        public final String userName;
        public final String role;

        public SessionData(int userId, String email, String userName, String role) {
            this.userId = userId;
            this.email = email;
            this.userName = userName;
            this.role = role;
        }
    }

    // LOGIN
    public static SessionData login(String email, String password) {

        String sql = """
            SELECT u.UserID, u.email, u.UserName, ur.role
            FROM `User` u
            JOIN UserRole ur ON ur.UserID = u.UserID
            WHERE u.email = ? AND u.password = ?
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            return new SessionData(
                    rs.getInt("UserID"),
                    rs.getString("email"),
                    rs.getString("UserName"),
                    rs.getString("role")
            );

        } catch (SQLException e) {
            return null;
        }
    }

    // REGISTER (CUSTOMER / SELLER only)
    public static String register(String email, String password, String userName, String role) {

        if (!role.equals("CUSTOMER") && !role.equals("SELLER"))
            return "Invalid role.";

        String insertUser = "INSERT INTO `User` (email, password, UserName) VALUES (?,?,?)";
        String insertRole = "INSERT INTO UserRole (UserID, role) VALUES (?,?)";

        try (Connection c = DB.getConnection()) {
            c.setAutoCommit(false);

            int uid;

            try (PreparedStatement ps =
                         c.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, email);
                ps.setString(2, password);
                ps.setString(3, userName);
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (!keys.next()) {
                    c.rollback();
                    return "Registration failed.";
                }
                uid = keys.getInt(1);
            }

            try (PreparedStatement ps2 = c.prepareStatement(insertRole)) {
                ps2.setInt(1, uid);
                ps2.setString(2, role);
                ps2.executeUpdate();
            }

            c.commit();
            return null;

        } catch (SQLException e) {
            return "Email already exists.";
        }
    }
}
