import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogService {

    // Catalog var mı?
    public static boolean hasCatalog(int sellerId) {

        String sql = "SELECT 1 FROM Catalog WHERE SellerID = ?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Yoksa default catalog oluştur
    public static void createCatalogIfNotExists(int sellerId) {

        if (hasCatalog(sellerId)) return;

        String sql = """
            INSERT INTO Catalog (SellerID, catalog_name)
            VALUES (?, 'My Catalog')
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Catalog adını güncelle
    public static boolean updateCatalogName(int sellerId, String newName) {

        String sql = """
            UPDATE Catalog
            SET catalog_name = ?
            WHERE SellerID = ?
        """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newName);
            ps.setInt(2, sellerId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Seller catalog adı
    public static String getCatalogName(int sellerId) {

        String sql = "SELECT catalog_name FROM Catalog WHERE SellerID = ?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return rs.getString("catalog_name");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "My Catalog";
    }


    public static String[] getCatalogNamesWithAll() {

        List<String> list = new ArrayList<>();
        list.add("ALL");

        String sql = "SELECT catalog_name FROM Catalog ORDER BY catalog_name";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list.toArray(new String[0]);
    }
}
