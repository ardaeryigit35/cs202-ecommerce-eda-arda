public class UserSession {

    private static int userId;
    private static String email;
    private static String userName;
    private static String role;

    public static void setUser(int uid, String mail, String uname, String r) {
        userId = uid;
        email = mail;
        userName = uname;
        role = r;
    }

    public static void clear() {
        userId = 0;
        email = null;
        userName = null;
        role = null;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getEmail() {
        return email;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getRole() {
        return role;
    }

    public static boolean isLoggedIn() {
        return role != null;
    }
}
