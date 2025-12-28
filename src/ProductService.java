import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService {

    // ============================
    // DTO
    // ============================
    public static class ProductItem {
        public final int productId;
        public final String productName;
        public final String category;
        public final double price;
        public final int stock;

        public ProductItem(int productId, String productName,
                           String category, double price, int stock) {
            this.productId = productId;
            this.productName = productName;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }
    }

    // ============================
    // GET PRODUCTS BY SELLER
    // ============================
    public static List<ProductItem> getProductsBySeller(int sellerId) {

        List<ProductItem> list = new ArrayList<>();

        String sql = """
            SELECT p.ProductID,
                   p.product_name,
                   c.category_name,
                   p.p_price,
                   p.stock_qty
            FROM Product p
            JOIN Category c ON c.CategoryID = p.CategoryID
            WHERE p.SellerID = ? AND p.is_active = TRUE
            ORDER BY p.product_name
        """;

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sellerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ProductItem(
                            rs.getInt("ProductID"),
                            rs.getString("product_name"),
                            rs.getString("category_name"),
                            rs.getDouble("p_price"),
                            rs.getInt("stock_qty")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
