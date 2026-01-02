import java.sql.Connection;
import java.sql.PreparedStatement;

public class DiscountService {

    public static boolean createDiscount(
            int sellerId,
            String code,
            int percent,
            int usage
    ) {

        String sql = """
            INSERT INTO DiscountCode
            (SellerID, code, discount_percent, usage_limit, usage_left)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ps.setString(2, code);
            ps.setInt(3, percent);
            ps.setInt(4, usage);
            ps.setInt(5, usage);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
