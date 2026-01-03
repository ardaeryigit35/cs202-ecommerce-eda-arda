import java.sql.*;

import java.sql.*;

public class StatisticsService {


    //kullanıcı bilgileri

    public static double getMonthlyTotal(int customerId) {
        String sql = """
        SELECT IFNULL(SUM(oi.unit_price * oi.quantity * (1 - (oi.discount_percent / 100.0))), 0)
        FROM OrderTable o
        JOIN Payment pay ON pay.OrderID = o.OrderID
        JOIN OrderItems oi ON oi.OrderID = o.OrderID
        WHERE o.CustomerID = ?
          AND pay.payment_status = 'DONE'
          AND o.order_status NOT IN ('CART','CANCELLED')
          AND MONTH(o.order_date) = MONTH(CURRENT_DATE())
          AND YEAR(o.order_date) = YEAR(CURRENT_DATE())
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static String getMostPurchasedCategory(int customerId) {
        String sql = """
        SELECT c.category_name
        FROM OrderTable o
        JOIN Payment pay ON pay.OrderID = o.OrderID
        JOIN OrderItems oi ON oi.OrderID = o.OrderID
        JOIN Product p ON p.ProductID = oi.ProductID AND p.SellerID = oi.SellerID
        JOIN Category c ON c.CategoryID = p.CategoryID
        WHERE o.CustomerID = ?
          AND pay.payment_status = 'DONE'
          AND o.order_status NOT IN ('CANCELLED','CART')
        GROUP BY c.category_name
        ORDER BY SUM(oi.quantity) DESC
        LIMIT 1
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : "N/A";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "N/A";
        }
    }


    //satıcı bilgileri

    public static double getSellerMonthlyRevenue(int sellerId) {
        String sql = """
        SELECT IFNULL(SUM(oi.unit_price * oi.quantity * (1 - (oi.discount_percent / 100.0))), 0)
        FROM OrderTable o
        JOIN Payment pay ON pay.OrderID = o.OrderID
        JOIN OrderItems oi ON oi.OrderID = o.OrderID
        WHERE o.SellerID = ?
          AND pay.payment_status = 'DONE'
          AND o.order_status NOT IN ('CART','CANCELLED')
          AND MONTH(o.order_date) = MONTH(CURRENT_DATE())
          AND YEAR(o.order_date) = YEAR(CURRENT_DATE())
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static String getBestSellingProduct(int sellerId) {
        String sql = """
        SELECT p.product_name
        FROM OrderItems oi
        JOIN OrderTable o ON o.OrderID = oi.OrderID
        JOIN Payment pay ON pay.OrderID = o.OrderID
        JOIN Product p ON p.ProductID = oi.ProductID AND p.SellerID = oi.SellerID
        WHERE o.SellerID = ?
          AND pay.payment_status = 'DONE'
          AND o.order_status NOT IN ('CART','CANCELLED')
        GROUP BY p.product_name
        ORDER BY SUM(oi.quantity) DESC
        LIMIT 1
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : "N/A";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "N/A";
        }
    }


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
            e.printStackTrace();
            return "N/A";
        }
    }

    public static double getAverageOrderValue(int sellerId) {
        String sql = """
        SELECT IFNULL(AVG(order_total), 0)
        FROM (
            SELECT o.OrderID,
                   SUM(oi.unit_price * oi.quantity * (1 - (oi.discount_percent / 100.0))) AS order_total
            FROM OrderTable o
            JOIN Payment pay ON pay.OrderID = o.OrderID
            JOIN OrderItems oi ON oi.OrderID = o.OrderID
            WHERE o.SellerID = ?
              AND pay.payment_status = 'DONE'
              AND o.order_status NOT IN ('CART','CANCELLED')
            GROUP BY o.OrderID
        ) t
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }



//admin bilgileri

    public static double getTotalSales() {
        String sql = """
        SELECT IFNULL(SUM(oi.unit_price * oi.quantity * (1 - (oi.discount_percent / 100.0))), 0)
        FROM OrderTable o
        JOIN Payment pay ON pay.OrderID = o.OrderID
        JOIN OrderItems oi ON oi.OrderID = o.OrderID
        WHERE pay.payment_status = 'DONE'
          AND o.order_status NOT IN ('CART','CANCELLED')
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static String getTopSeller() {
        String sql = """
        SELECT u.UserName
        FROM OrderTable o
        JOIN Payment pay ON pay.OrderID = o.OrderID
        JOIN User u ON u.UserID = o.SellerID
        JOIN OrderItems oi ON oi.OrderID = o.OrderID
        WHERE pay.payment_status = 'DONE'
          AND o.order_status NOT IN ('CART','CANCELLED')
        GROUP BY u.UserName
        ORDER BY SUM(oi.unit_price * oi.quantity * (1 - (oi.discount_percent / 100.0))) DESC
        LIMIT 1
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : "N/A";
        } catch (SQLException e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    public static String getMostPopularItem() {
        String sql = """
        SELECT p.product_name
        FROM OrderItems oi
        JOIN OrderTable o ON o.OrderID = oi.OrderID
        JOIN Payment pay ON pay.OrderID = o.OrderID
        JOIN Product p ON p.ProductID = oi.ProductID AND p.SellerID = oi.SellerID
        WHERE pay.payment_status = 'DONE'
          AND o.order_status NOT IN ('CART','CANCELLED')
        GROUP BY p.product_name
        ORDER BY SUM(oi.quantity) DESC
        LIMIT 1
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : "N/A";
        } catch (SQLException e) {
            e.printStackTrace();
            return "N/A";
        }
    }


    public static String getTopSellingCategory() {
        String sql = """
        SELECT c.category_name
        FROM OrderItems oi
        JOIN OrderTable o ON o.OrderID = oi.OrderID
        JOIN Payment pay ON pay.OrderID = o.OrderID
        JOIN Product p ON p.ProductID = oi.ProductID AND p.SellerID = oi.SellerID
        JOIN Category c ON c.CategoryID = p.CategoryID
        WHERE pay.payment_status = 'DONE'
          AND o.order_status NOT IN ('CART','CANCELLED')
        GROUP BY c.category_name
        ORDER BY SUM(oi.quantity) DESC
        LIMIT 1
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : "N/A";
        } catch (SQLException e) {
            e.printStackTrace();
            return "N/A";
        }
    }


    public static double getAverageMonthlyPurchase(int customerId) {
        String sql = """
        SELECT IFNULL(AVG(month_total), 0)
        FROM (
            SELECT YEAR(o.order_date) AS y,
                   MONTH(o.order_date) AS m,
                   SUM(oi.unit_price * oi.quantity * (1 - (oi.discount_percent / 100.0))) AS month_total
            FROM OrderTable o
            JOIN Payment pay ON pay.OrderID = o.OrderID
            JOIN OrderItems oi ON oi.OrderID = o.OrderID
            WHERE o.CustomerID = ?
              AND pay.payment_status = 'DONE'
              AND o.order_status NOT IN ('CART','CANCELLED')
            GROUP BY YEAR(o.order_date), MONTH(o.order_date)
        ) t
    """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

}


