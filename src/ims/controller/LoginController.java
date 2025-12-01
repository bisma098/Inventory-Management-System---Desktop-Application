package ims.controller;

import ims.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("Admin", "Manager", "Staff");
        errorLabel.setText("");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();

        // Basic validation
        if (username.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Please fill in all fields and select a role.");
            return;
        }

        try {
            // Validate credentials first
           //if (validateCredentials(username, password, role)) {
             // Login successful - navigate to appropriate dashboard
                String fxml = null;

                switch (role) {
                    case "Admin":   fxml = "/ims/view/AdminDashboard.fxml"; break;
                    case "Manager": fxml = "/ims/view/ManagerDashboard.fxml"; break;
                    case "Staff":   fxml = "/ims/view/StaffDashboard.fxml"; break;
                }

                // Check if FXML file exists before loading
                URL fxmlUrl = getClass().getResource(fxml);
                if (fxmlUrl == null) {
                    showAlert("Dashboard not available for role: " + role);
                    return;
                }

                Parent root = FXMLLoader.load(fxmlUrl);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 700));
                stage.setTitle("IMS - " + role + " Dashboard");

           // } else {
            //   errorLabel.setText("Invalid credentials or role mismatch!");
            //   showAlert("Invalid username, password, or role selection!");
            //}

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Login failed due to system error!");
        }
    }

    private boolean validateCredentials(String username, String password, String role) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            
            String sql = "SELECT u.userId, u.userName, u.role " +
                        "FROM Users u " +
                        "WHERE u.userName = ? AND u.password = ? AND u.role = ? " +
                        "AND EXISTS (SELECT 1 FROM ";
            
            switch (role) {
                case "Admin":
                    sql += "Admin a WHERE a.adminId = u.userId)";
                    break;
                case "Manager":
                    sql += "Manager m WHERE m.managerId = u.userId)";
                    break;
                case "Staff":
                    sql += "Staff s WHERE s.staffId = u.userId)";
                    break;
                default:
                    return false;
            }

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);

            rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Login Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}