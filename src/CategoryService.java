import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    // =========================
    // GET CATEGORY NAMES
    // (SELLER / ADMIN / ADD PRODUCT)
    // =========================
    public static String[] getCategoryNames() {

        List<String> list = new ArrayList<>();

        String sql = "SELECT category_name FROM Category ORDER BY category_name";

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

    // =========================
    // GET CATEGORY NAMES + ALL
    // (CUSTOMER FILTER)
    // =========================
    public static String[] getCategoryNamesWithAll() {

        List<String> list = new ArrayList<>();
        list.add("ALL");

        String sql = "SELECT category_name FROM Category ORDER BY category_name";

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

    // =========================
    // GET CATALOG NAMES + ALL
    // (CUSTOMER FILTER)
    // =========================
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

    // =========================
    // GET CATEGORY ID BY NAME
    // =========================
    public static int getCategoryIdByName(String name) {

        String sql = "SELECT CategoryID FROM Category WHERE category_name = ?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
