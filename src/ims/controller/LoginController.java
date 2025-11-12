package ims.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("Admin", "Manager", "Staff");
    }

    @FXML
    private void handleLogin() {
        try {
            String role = roleComboBox.getValue();
            String fxml = null;

            switch (role) {
                case "Admin":   fxml = "/ims/view/AdminDashboard.fxml"; break;
                case "Manager": fxml = "/ims/view/ManagerDashboard.fxml"; break;
                case "Staff":   fxml = "/ims/view/StaffDashboard.fxml"; break;
                default:
                    showAlert("Please select a user role.");
                    return;
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));

        } catch (Exception e) {
            e.printStackTrace();
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
