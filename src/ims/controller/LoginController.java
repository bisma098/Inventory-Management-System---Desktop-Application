package ims.controller;
import ims.model.User;
import ims.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.List;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label errorLabel;

    private DataController dataController;

    @FXML
    private void initialize() {
        dataController = DataController.getInstance();
        roleComboBox.getItems().addAll("Admin", "Manager", "Staff");
        errorLabel.setText("");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();

        System.out.println("Login attempt - Username: " + username + ", Role: " + role);

        // Basic validation
        if (username.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Please fill in all fields and select a role.");
            return;
        }

        try {
            // Validate credentials using DataController
            User user = dataController.findUserByCredentials(username, password, role);
            
            if (user != null) {
                System.out.println("Login SUCCESSFUL for: " + username + " (ID: " + user.getUserId() + ")");
                // Login successful - navigate to appropriate dashboard
                navigateToDashboard(role);
            } else {
                System.out.println("Login FAILED for: " + username);
                errorLabel.setText("Invalid credentials or role mismatch!");
                showAlert("Invalid username, password, or role selection!");
                
                // Debug: Print all available users
                debugAvailableUsers(username);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Login failed due to system error: " + e.getMessage());
        }
    }

    private void debugAvailableUsers(String targetUsername) {
        System.out.println("=== DEBUG: All users in DataController ===");
        List<User> allUsers = dataController.getUsers();
        
        boolean foundUser = false;
        for (User user : allUsers) {
            System.out.println("User - ID: " + user.getUserId() + 
                             ", Username: " + user.getUserName() + 
                             ", Role: " + user.getRole() + 
                             ", Password: " + user.getPassword());
            
            if (user.getUserName().equals(targetUsername)) {
                foundUser = true;
                System.out.println(">>> FOUND TARGET USER but role/password might not match <<<");
            }
        }
        
        if (!foundUser) {
            System.out.println(">>> TARGET USER '" + targetUsername + "' NOT FOUND <<<");
        }
        System.out.println("=== END DEBUG ===");
    }

    // The navigateToDashboard and showAlert methods remain the same
    private void navigateToDashboard(String role) {
        try {
            String fxml = null;

            switch (role) {
                case "Admin":   fxml = "/ims/view/AdminDashboard.fxml"; break;
                case "Manager": fxml = "/ims/view/ManagerDashboard.fxml"; break;
                case "Staff":   fxml = "/ims/view/StaffDashboard.fxml"; break;
            }

            System.out.println("Loading dashboard: " + fxml);
            
            URL fxmlUrl = getClass().getResource(fxml);
            if (fxmlUrl == null) {
                System.out.println("FXML file not found: " + fxml);
                showAlert("Dashboard not available for role: " + role);
                return;
            }

            System.out.println("FXML file found, loading...");
            Parent root = FXMLLoader.load(fxmlUrl);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("IMS - " + role + " Dashboard");
            System.out.println("Dashboard loaded successfully");

        } catch (Exception e) {
            System.out.println("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error loading dashboard: " + e.getMessage());
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