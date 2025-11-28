package ims.controller;

import ims.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateWelcomeMessage();
    }

    // Method to update welcome message from DataController's current user
    public void updateWelcomeMessage() {
        System.out.println("=== DEBUG HeaderController.updateWelcomeMessage() ===");
        
        // Get current user from DataController
        User currentUser = DataController.getInstance().getCurrentUser();
        System.out.println("Current user from DataController: " + currentUser);
        
        if (currentUser != null) {
            System.out.println("User details - Name: " + currentUser.getUserName() + ", Role: " + currentUser.getRole());
        }
        
        System.out.println("welcomeLabel is null: " + (welcomeLabel == null));
        
        if (welcomeLabel != null) {
            if (currentUser != null && currentUser.getUserName() != null && !currentUser.getUserName().isEmpty()) {
                String welcomeText = "Welcome, " + currentUser.getUserName() + "!";
                System.out.println("Setting welcome text to: " + welcomeText);
                welcomeLabel.setText(welcomeText);
            } else {
                System.out.println("Using fallback: Welcome, User");
                welcomeLabel.setText("Welcome, User");
            }
        } else {
            System.out.println("ERROR: welcomeLabel is null!");
        }
        System.out.println("=== END DEBUG ===");
    }

    @FXML
    private void openProfile() {
        System.out.println("Profile clicked");
        // You can access current user here too
        User currentUser = DataController.getInstance().getCurrentUser();
        if (currentUser != null) {
            System.out.println("Opening profile for: " + currentUser.getUserName());
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            // Clear current user on logout
            DataController.getInstance().setCurrentUser(null);
            
            Parent root = FXMLLoader.load(getClass().getResource("/ims/view/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("IMS - Login");
            User currentUser = DataController.getInstance().getCurrentUser();
            DataController.getInstance().logUserActivity(
            currentUser.getUserId(),
            "Logged out"
        );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}