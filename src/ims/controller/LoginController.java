package ims.controller;

import ims.database.DatabaseConnection;
import ims.model.User;
import javafx.event.ActionEvent;
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
private void handleLogin(ActionEvent event) {
    String username = usernameField.getText();
    String password = passwordField.getText();
    String role = roleComboBox.getValue();

    DataController dc = DataController.getInstance();

    User user = dc.findUserByCredentials(username, password, role);

    if (user == null) {
        errorLabel.setText("Invalid username or password!");
        return;
    }

    System.out.println("Login SUCCESS - User: " + user.getUserName());
    dc.setCurrentUser(user);
    loadDashboard(user.getRole());
}
    private void loadDashboard(String role) {
    try {
        String fxml = null;
        switch (role) {
            case "Admin":   fxml = "/ims/view/AdminDashboard.fxml"; break;
            case "Manager": fxml = "/ims/view/ManagerDashboard.fxml"; break;
            case "Staff":   fxml = "/ims/view/StaffDashboard.fxml"; break;
        }

        URL fxmlUrl = getClass().getResource(fxml);
        if (fxmlUrl == null) {
            showAlert("Dashboard not available for role: " + role);
            return;
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root, 1200, 700));
        stage.setTitle("IMS - " + role + " Dashboard");

    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Error loading dashboard!");
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