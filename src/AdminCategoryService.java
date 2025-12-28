import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminCategoryService {

    // ========================
    // DTO
    // ========================
    public static class CategoryItem {
        public final int categoryId;
        public final String name;

        public CategoryItem(int categoryId, String name) {
            this.categoryId = categoryId;
            this.name = name;
        }
    }

    // ========================
    // GET ALL CATEGORIES
    // ========================
    public static List<CategoryItem> getCategories() {

        String sql = "SELECT CategoryID, category_name FROM Category ORDER BY category_name";

        List<CategoryItem> list = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new CategoryItem(
                        rs.getInt("CategoryID"),
                        rs.getString("category_name")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ========================
    // ADD CATEGORY
    // ========================
    public static boolean addCategory(String name) {

        String sql = "INSERT INTO Category (category_name) VALUES (?)";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name.trim());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    // ========================
    // DELETE CATEGORY
    // ========================
    public static boolean deleteCategory(int categoryId) {

        String sql = "DELETE FROM Category WHERE CategoryID = ?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            return false;
        }
    }
}
