import java.sql.*;

public class SellerInventoryService {


    public static int getOrCreateCatalog(int sellerId) {

        String checkSql = """
            SELECT CatalogID
            FROM Catalog
            WHERE SellerID = ?
        """;

        String insertSql = """
            INSERT INTO Catalog (SellerID, catalog_name)
            VALUES (?, ?)
        """;

        try (Connection c = DB.getConnection()) {

            try (PreparedStatement ps = c.prepareStatement(checkSql)) {
                ps.setInt(1, sellerId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("CatalogID");
                }
            }


            try (PreparedStatement ps =
                         c.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, sellerId);
                ps.setString(2, "Catalog of Seller #" + sellerId);
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static boolean restockProduct(
            int productId,
            int sellerId,
            int qty,
            String note
    ) {

        String updateStock = """
            UPDATE Product
            SET stock_qty = stock_qty + ?
            WHERE ProductID = ? AND SellerID = ?
        """;

        String insertHistory = """
            INSERT INTO StockHistory (ProductID, SellerID, change_qty, note)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection c = DB.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps1 = c.prepareStatement(updateStock)) {
                ps1.setInt(1, qty);
                ps1.setInt(2, productId);
                ps1.setInt(3, sellerId);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = c.prepareStatement(insertHistory)) {
                ps2.setInt(1, productId);
                ps2.setInt(2, sellerId);
                ps2.setInt(3, qty);
                ps2.setString(4, note);
                ps2.executeUpdate();
            }

            c.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
