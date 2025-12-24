import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/cs202fall2025project";
    private static final String USER = "root";       // kendi MySQL user
    private static final String PASSWORD = "mertarda12"; // kendi MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
