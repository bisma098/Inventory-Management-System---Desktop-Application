package ims.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class HeaderController {

    @FXML
    private Label welcomeLabel;

    // Method to set the welcome message with user's name
    public void setUserName(String userName) {
    System.out.println("=== DEBUG HeaderController.setUserName() ===");
    System.out.println("userName parameter: " + userName);
    System.out.println("welcomeLabel is null: " + (welcomeLabel == null));
    
    if (welcomeLabel != null && userName != null && !userName.isEmpty()) {
        System.out.println("Setting welcome text to: Welcome, " + userName + "!");
        welcomeLabel.setText("Welcome, " + userName + "!");
    } else {
        System.out.println("Using fallback: Welcome, User");
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, User");
        } else {
            System.out.println("ERROR: welcomeLabel is still null!");
        }
    }
    System.out.println("=== END DEBUG ===");
    }

    @FXML
    private void openProfile() {
        System.out.println("Profile clicked");
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ims/view/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("IMS - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}