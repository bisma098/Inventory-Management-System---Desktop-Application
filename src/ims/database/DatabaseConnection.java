package ims.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlserver://DESKTOP-3C2QQNA;databaseName=ims;encrypt=false;";
    private static final String USER = "bisma";
    private static final String PASSWORD = "bix098";

    private static Connection connection;

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("SQL Server JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load SQL Server JDBC Driver.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to SQL Server Successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Database Connection Failed!");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}