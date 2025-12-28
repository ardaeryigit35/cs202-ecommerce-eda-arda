import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SellerReviewService {

    // ========================
    // DTO
    // ========================
    public static class ReviewItem {
        public final String productName;
        public final String customerName;
        public final int rating;
        public final String comment;
        public final Timestamp date;

        public ReviewItem(String productName,
                          String customerName,
                          int rating,
                          String comment,
                          Timestamp date) {
            this.productName = productName;
            this.customerName = customerName;
            this.rating = rating;
            this.comment = comment;
            this.date = date;
        }
    }

    // ========================
    // GET REVIEWS FOR SELLER
    // ========================
    public static List<ReviewItem> getReviewsForSeller(int sellerId) {

        String sql = """
            SELECT p.product_name,
                   u.UserName AS customer,
                   r.rating,
                   r.comment,
                   r.created_at
            FROM Review r
            JOIN Product p
              ON p.ProductID = r.ProductID AND p.SellerID = r.SellerID
            JOIN User u ON u.UserID = r.CustomerID
            WHERE r.SellerID = ?
            ORDER BY r.created_at DESC
        """;

        List<ReviewItem> list = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, sellerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ReviewItem(
                            rs.getString("product_name"),
                            rs.getString("customer"),
                            rs.getInt("rating"),
                            rs.getString("comment"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
