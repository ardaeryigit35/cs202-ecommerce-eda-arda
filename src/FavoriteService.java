import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoriteService {

    public static class FavoriteItem {
        public final int productId;
        public final int sellerId;
        public final String productName;
        public final double price;
        public final Timestamp createdAt;

        public FavoriteItem(int productId, int sellerId, String productName, double price, Timestamp createdAt) {
            this.productId = productId;
            this.sellerId = sellerId;
            this.productName = productName;
            this.price = price;
            this.createdAt = createdAt;
        }
    }

    public static boolean addFavorite(int userId, int productId, int sellerId) {
        String sql = """
            INSERT INTO Favorite (UserID, ProductID, SellerID)
            VALUES (?, ?, ?)
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, sellerId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            // Duplicate key ise zaten favoride (sessizce true dönmek UX için iyi)
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                return true;
            }
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeFavorite(int userId, int productId, int sellerId) {
        String sql = """
            DELETE FROM Favorite
            WHERE UserID = ? AND ProductID = ? AND SellerID = ?
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, sellerId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static List<FavoriteItem> getFavorites(int userId) {
        String sql = """
            SELECT f.ProductID, f.SellerID, p.product_name, p.p_price, f.created_at
            FROM Favorite f
            JOIN Product p
              ON p.ProductID = f.ProductID AND p.SellerID = f.SellerID
            WHERE f.UserID = ?
            ORDER BY f.created_at DESC
        """;

        List<FavoriteItem> list = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new FavoriteItem(
                            rs.getInt("ProductID"),
                            rs.getInt("SellerID"),
                            rs.getString("product_name"),
                            rs.getDouble("p_price"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    public static boolean isFavorite(int userId, int productId, int sellerId) {
        String sql = """
            SELECT 1
            FROM Favorite
            WHERE UserID=? AND ProductID=? AND SellerID=?
            LIMIT 1
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, sellerId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
