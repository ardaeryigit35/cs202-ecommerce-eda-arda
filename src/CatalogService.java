import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CatalogService {

    // Seller için catalog varsa döner, yoksa oluşturur
    public static int getOrCreateCatalog(int sellerId) {

        String find = """
            SELECT CatalogID
            FROM Catalog
            WHERE SellerID = ?
        """;

        String create = """
            INSERT INTO Catalog (SellerID, catalog_name)
            VALUES (?, 'Default Catalog')
        """;

        try (Connection c = DB.getConnection()) {

            // 1️⃣ VAR MI?
            try (PreparedStatement ps = c.prepareStatement(find)) {
                ps.setInt(1, sellerId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("CatalogID");
                }
            }

            // 2️⃣ YOK → OLUŞTUR
            try (PreparedStatement ps = c.prepareStatement(
                    create, PreparedStatement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, sellerId);
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
