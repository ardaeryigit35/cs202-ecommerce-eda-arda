import java.sql.*;

public class StatisticsService {

    // ========================
    // CUSTOMER STATISTICS
    // ========================

    public static double getMonthlyTotal(int customerId) {
        String sql = """
            SELECT IFNULL(SUM(oi.quantity * oi.unit_price),0)
            FROM OrderTable o
            JOIN OrderItems oi ON oi.OrderID = o.OrderID
            WHERE o.CustomerID = ?
              AND o.order_status <> 'CART'
              AND MONTH(o.order_date) = MONTH(CURRENT_DATE())
              AND YEAR(o.order_date) = YEAR(CURRENT_DATE())
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getDouble(1);

        } catch (SQLException e) {
            return 0;
        }
    }

    public static String getMostPurchasedCategory(int customerId) {
        String sql = """
            SELECT c.category_name
            FROM OrderTable o
            JOIN OrderItems oi ON oi.OrderID = o.OrderID
            JOIN Product p ON p.ProductID = oi.ProductID
            JOIN Category c ON c.CategoryID = p.CategoryID
            WHERE o.CustomerID = ?
              AND o.order_status <> 'CART'
            GROUP BY c.category_name
            ORDER BY SUM(oi.quantity) DESC
            LIMIT 1
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString(1) : "N/A";

        } catch (SQLException e) {
            return "N/A";
        }
    }

    // ========================
    // SELLER STATISTICS
    // ========================

    public static double getSellerMonthlyRevenue(int sellerId) {
        String sql = """
            SELECT IFNULL(SUM(oi.quantity * oi.unit_price),0)
            FROM OrderTable o
            JOIN OrderItems oi ON oi.OrderID = o.OrderID
            WHERE o.SellerID = ?
              AND o.order_status <> 'CART'
              AND MONTH(o.order_date) = MONTH(CURRENT_DATE())
              AND YEAR(o.order_date) = YEAR(CURRENT_DATE())
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getDouble(1);

        } catch (SQLException e) {
            return 0;
        }
    }

    public static String getBestSellingProduct(int sellerId) {
        String sql = """
            SELECT p.product_name
            FROM OrderItems oi
            JOIN OrderTable o ON o.OrderID = oi.OrderID
            JOIN Product p ON p.ProductID = oi.ProductID
            WHERE o.SellerID = ?
            GROUP BY p.product_name
            ORDER BY SUM(oi.quantity) DESC
            LIMIT 1
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString(1) : "N/A";

        } catch (SQLException e) {
            return "N/A";
        }
    }

    // 🔥 NEW — MOST RATED PRODUCT
    public static String getMostRatedProduct(int sellerId) {

        String sql = """
        SELECT p.product_name
        FROM Review r
        JOIN Product p
          ON p.ProductID = r.ProductID AND p.SellerID = r.SellerID
        WHERE p.SellerID = ?
        GROUP BY p.product_name
        ORDER BY COUNT(*) DESC, AVG(r.rating) DESC
        LIMIT 1
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString(1) : "N/A";

        } catch (SQLException e) {
            return "N/A";
        }
    }


    // 🔥 NEW — AVERAGE ORDER VALUE
    public static double getAverageOrderValue(int sellerId) {

        String sql = """
        SELECT IFNULL(AVG(order_total),0)
        FROM (
            SELECT SUM(oi.quantity * oi.unit_price) AS order_total
            FROM OrderTable o
            JOIN OrderItems oi ON oi.OrderID = o.OrderID
            WHERE o.SellerID = ?
              AND o.order_status <> 'CART'
            GROUP BY o.OrderID
        ) t
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getDouble(1);

        } catch (SQLException e) {
            return 0;
        }
    }


    // ========================
    // ADMIN STATISTICS
    // ========================

    public static double getTotalSales() {
        String sql = """
            SELECT IFNULL(SUM(oi.quantity * oi.unit_price),0)
            FROM OrderItems oi
            JOIN OrderTable o ON o.OrderID = oi.OrderID
            WHERE o.order_status <> 'CART'
        """;

        try (Connection c = DB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            rs.next();
            return rs.getDouble(1);

        } catch (SQLException e) {
            return 0;
        }
    }

    public static String getTopSeller() {
        String sql = """
            SELECT u.UserName
            FROM OrderTable o
            JOIN User u ON u.UserID = o.SellerID
            JOIN OrderItems oi ON oi.OrderID = o.OrderID
            GROUP BY u.UserName
            ORDER BY SUM(oi.quantity * oi.unit_price) DESC
            LIMIT 1
        """;

        try (Connection c = DB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            return rs.next() ? rs.getString(1) : "N/A";

        } catch (SQLException e) {
            return "N/A";
        }
    }
    public static String getMostPopularItem() {

        String sql = """
        SELECT p.product_name
        FROM OrderItems oi
        JOIN Product p ON p.ProductID = oi.ProductID
        JOIN OrderTable o ON o.OrderID = oi.OrderID
        WHERE o.order_status <> 'CART'
        GROUP BY p.product_name
        ORDER BY SUM(oi.quantity) DESC
        LIMIT 1
    """;

        try (Connection c = DB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            return rs.next() ? rs.getString(1) : "N/A";

        } catch (SQLException e) {
            return "N/A";
        }
    }

    public static String getTopSellingCategory() {

        String sql = """
        SELECT c.category_name
        FROM OrderItems oi
        JOIN Product p ON p.ProductID = oi.ProductID
        JOIN Category c ON c.CategoryID = p.CategoryID
        JOIN OrderTable o ON o.OrderID = oi.OrderID
        WHERE o.order_status <> 'CART'
        GROUP BY c.category_name
        ORDER BY SUM(oi.quantity) DESC
        LIMIT 1
    """;

        try (Connection c = DB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            return rs.next() ? rs.getString(1) : "N/A";

        } catch (SQLException e) {
            return "N/A";
        }
    }

    public static double getAverageMonthlyPurchase(int customerId) {

        String sql = """
        SELECT IFNULL(AVG(month_total), 0)
        FROM (
            SELECT SUM(oi.quantity * oi.unit_price) AS month_total
            FROM OrderTable o
            JOIN OrderItems oi ON oi.OrderID = o.OrderID
            WHERE o.CustomerID = ?
              AND o.order_status <> 'CART'
            GROUP BY YEAR(o.order_date), MONTH(o.order_date)
        ) t
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getDouble(1);

        } catch (SQLException e) {
            return 0;
        }
    }






}
