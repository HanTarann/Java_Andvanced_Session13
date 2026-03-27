package util;

public class DBConnection {
    public static java.sql.Connection getConnection() throws java.sql.SQLException {
        String url = "jdbc:mysql://localhost:3306/SS13";
        String username = "root";
        String password = "trankhanh8506";
        return java.sql.DriverManager.getConnection(url, username, password);
    }
}
