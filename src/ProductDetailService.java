import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailService {


    public static class ProductDetail {
        public final String productName;
        public final String category;
        public final String description;
        public final double price;
        public final int stock;

        public ProductDetail(String productName, String category,
                             String description, double price, int stock) {
            this.productName = productName;
            this.category = category;
            this.description = description;
            this.price = price;
            this.stock = stock;
        }
    }

    public static class ReviewItem {
        public final String customerName;
        public final int rating;
        public final String comment;
        public final Timestamp date;

        public ReviewItem(String customerName, int rating,
                          String comment, Timestamp date) {
            this.customerName = customerName;
            this.rating = rating;
            this.comment = comment;
            this.date = date;
        }
    }


    public static ProductDetail getProductDetail(int productId) {

        String sql = """
            SELECT p.product_name,
                   c.category_name,
                   p.p_description,
                   p.p_price,
                   p.stock_qty
            FROM Product p
            JOIN Category c ON c.CategoryID = p.CategoryID
            WHERE p.ProductID = ?
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ProductDetail(
                        rs.getString("product_name"),
                        rs.getString("category_name"),
                        rs.getString("p_description"),
                        rs.getDouble("p_price"),
                        rs.getInt("stock_qty")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static List<ReviewItem> getReviewsForProduct(int productId) {

        String sql = """
            SELECT u.UserName,
                   r.rating,
                   r.comment,
                   r.created_at
            FROM Review r
            JOIN User u ON u.UserID = r.CustomerID
            WHERE r.ProductID = ?
            ORDER BY r.created_at DESC
        """;

        List<ReviewItem> list = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new ReviewItem(
                        rs.getString("UserName"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getTimestamp("created_at")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    public static double getAverageRating(int productId) {

        String sql = """
            SELECT IFNULL(AVG(rating),0)
            FROM Review
            WHERE ProductID = ?
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getDouble(1);

        } catch (SQLException e) {
            return 0;
        }
    }
}
