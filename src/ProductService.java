import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService {


    public static class ProductItem {
        public final int productId;
        public final String productName;
        public final String category;
        public final String catalogName;
        public final double price;
        public final int stock;
        public final int sellerId;

        public ProductItem(int productId,
                           String productName,
                           String category,
                           String catalogName,
                           double price,
                           int stock,
                           int sellerId) {

            this.productId = productId;
            this.productName = productName;
            this.category = category;
            this.catalogName = catalogName;
            this.price = price;
            this.stock = stock;
            this.sellerId = sellerId;
        }
    }


    public static class AdminProductItem {
        public final int productId;
        public final String productName;
        public final String sellerName;
        public final String category;
        public final double price;
        public final int stock;

        public AdminProductItem(int productId,
                                String productName,
                                String sellerName,
                                String category,
                                double price,
                                int stock) {

            this.productId = productId;
            this.productName = productName;
            this.sellerName = sellerName;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }
    }


    public static boolean addProduct(
            int sellerId,
            String name,
            String categoryName,
            double price,
            int stock
    ) {

        int categoryId = CategoryService.getCategoryIdByName(categoryName);
        if (categoryId == -1) return false;

        String sql = """
            INSERT INTO Product
            (product_name, CategoryID, SellerID, p_price, stock_qty)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, categoryId);
            ps.setInt(3, sellerId);
            ps.setDouble(4, price);
            ps.setInt(5, stock);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static List<ProductItem> getProductsBySeller(int sellerId) {

        List<ProductItem> list = new ArrayList<>();

        String sql = """
            SELECT p.ProductID,
                   p.product_name,
                   cat.category_name,
                   cg.catalog_name,
                   p.p_price,
                   p.stock_qty,
                   p.SellerID
            FROM Product p
            JOIN Category cat ON cat.CategoryID = p.CategoryID
            JOIN Catalog cg ON cg.SellerID = p.SellerID
            WHERE p.SellerID = ?
            ORDER BY p.product_name
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new ProductItem(
                        rs.getInt("ProductID"),
                        rs.getString("product_name"),
                        rs.getString("category_name"),
                        rs.getString("catalog_name"),
                        rs.getDouble("p_price"),
                        rs.getInt("stock_qty"),
                        rs.getInt("SellerID")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    public static List<ProductItem> searchProducts(
            String keyword,
            String category,
            String catalogName,
            Double minPrice,
            Double maxPrice
    ) {

        List<ProductItem> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT p.ProductID,
                   p.product_name,
                   cat.category_name,
                   cg.catalog_name,
                   p.p_price,
                   p.stock_qty,
                   p.SellerID
            FROM Product p
            JOIN Category cat ON cat.CategoryID = p.CategoryID
            JOIN Catalog cg ON cg.SellerID = p.SellerID
            WHERE p.stock_qty > 0
        """);

        if (keyword != null && !keyword.isEmpty())
            sql.append(" AND p.product_name LIKE ?");

        if (category != null && !category.equals("ALL"))
            sql.append(" AND cat.category_name = ?");

        if (catalogName != null && !catalogName.equals("ALL"))
            sql.append(" AND cg.catalog_name = ?");

        if (minPrice != null)
            sql.append(" AND p.p_price >= ?");

        if (maxPrice != null)
            sql.append(" AND p.p_price <= ?");

        sql.append(" ORDER BY p.product_name");

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            int i = 1;

            if (keyword != null && !keyword.isEmpty())
                ps.setString(i++, "%" + keyword + "%");

            if (category != null && !category.equals("ALL"))
                ps.setString(i++, category);

            if (catalogName != null && !catalogName.equals("ALL"))
                ps.setString(i++, catalogName);

            if (minPrice != null)
                ps.setDouble(i++, minPrice);

            if (maxPrice != null)
                ps.setDouble(i++, maxPrice);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new ProductItem(
                        rs.getInt("ProductID"),
                        rs.getString("product_name"),
                        rs.getString("category_name"),
                        rs.getString("catalog_name"),
                        rs.getDouble("p_price"),
                        rs.getInt("stock_qty"),
                        rs.getInt("SellerID")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    public static List<AdminProductItem> getAllProductsForAdmin() {

        List<AdminProductItem> list = new ArrayList<>();

        String sql = """
            SELECT p.ProductID,
                   p.product_name,
                   u.UserName,
                   c.category_name,
                   p.p_price,
                   p.stock_qty
            FROM Product p
            JOIN User u ON u.UserID = p.SellerID
            JOIN Category c ON c.CategoryID = p.CategoryID
            ORDER BY p.product_name
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new AdminProductItem(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getDouble(5),
                        rs.getInt(6)
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
