import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        // 1️⃣ Database bilgileri
        String url = "jdbc:mysql://localhost:3306/cs202fall2025project";
        String user = "root";
        String password = "mertarda12";

        // 2️⃣ Bağlantı denemesi
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("DB CONNECTED");

            conn.close();
        } catch (SQLException e) {
            System.out.println("DB CONNECTION FAILED");
            e.printStackTrace();
        }
    }
}
