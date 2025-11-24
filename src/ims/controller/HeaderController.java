package ims.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HeaderController {

    // Add these FXML annotations for the profile menu elements
    @FXML
    private VBox profileMenu;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private Label initialsLabel;

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

    // Profile menu toggle method
    @FXML
    private void handleProfileMenu() {
        // Toggle profile menu visibility
        if (profileMenu != null) {
            boolean isVisible = profileMenu.isVisible();
            profileMenu.setVisible(!isVisible);
            profileMenu.setManaged(!isVisible);
        }
    }

    // Profile settings method
    @FXML
    private void handleProfileSettings() {
        // Hide menu first
        if (profileMenu != null) {
            profileMenu.setVisible(false);
            profileMenu.setManaged(false);
        }
        
        // Load profile settings view
        // Implement your profile settings navigation here
        System.out.println("Opening Profile Settings...");
    }

    // Change password method
    @FXML
    private void handleChangePassword() {
        // Hide menu first
        if (profileMenu != null) {
            profileMenu.setVisible(false);
            profileMenu.setManaged(false);
        }
        
        // Load change password view
        // Implement your change password navigation here
        System.out.println("Opening Change Password...");
    }

    // Method to set user profile information
    public void setUserProfile(String userName, String userRole) {
        // Set the displayed name
        if (nameLabel != null) {
            nameLabel.setText(userName);
        }
        
        // Generate initials for avatar
        String initials = generateInitials(userName);
        if (initialsLabel != null) {
            initialsLabel.setText(initials);
        }
        
        // You can also set role-specific styling if needed
    }

    // Helper method to generate initials from name
    private String generateInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "U"; // Default for unknown user
        }
        
        String[] names = fullName.trim().split("\\s+");
        if (names.length == 1) {
            return names[0].substring(0, 1).toUpperCase();
        } else {
            return (names[0].substring(0, 1) + names[names.length - 1].substring(0, 1)).toUpperCase();
        }
    }
}