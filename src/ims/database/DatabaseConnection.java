package ims.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:sqlserver://DESKTOP-90E7HB0\\SQLEXPRESS;databaseName=ims;encrypt=false;";
    private static final String USER = "test1";
    private static final String PASSWORD = "123123";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("SQL Server JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load SQL Server JDBC Driver.");
            e.printStackTrace();
        }
    }

    // ⭐ Always return a NEW connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
