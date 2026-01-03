import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SellerDiscountService {

    public static boolean createDiscount(int sellerId, String code, int percent, int usageLimit) {

        if (code == null) return false;

        code = code.trim().toUpperCase();

        if (code.isEmpty()) return false;
        if (percent < 1 || percent > 100) return false;
        if (usageLimit <= 0) return false;

        String sql = """
            INSERT INTO DiscountCode
            (SellerID, code, discount_percent, usage_limit, usage_left)
            VALUES (?, ?, ?, ?, ?*2)
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ps.setString(2, code);
            ps.setInt(3, percent);
            ps.setInt(4, usageLimit);
            ps.setInt(5, usageLimit);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {

            System.out.println("createDiscount ERROR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
