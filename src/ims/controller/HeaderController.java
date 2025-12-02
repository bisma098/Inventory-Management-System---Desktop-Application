package ims.controller;

import ims.model.User;
import ims.service.NotificationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Label notificationCount;
    
    @FXML
    private Button notificationBtn;

    private static HeaderController instance;

    private NotificationService notificationService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        notificationService = NotificationService.getInstance();
        instance = this;
        updateWelcomeMessage();
        updateNotificationBadge();
    }

    public void updateWelcomeMessage() {
        User currentUser = DataController.getInstance().getCurrentUser();  
        if (currentUser != null && currentUser.getUserName() != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUserName() + "!");
        } else {
            welcomeLabel.setText("Welcome, User");
        }
    }

    public void updateNotificationBadge() {
        int count = notificationService.getNotificationCount();
        if (count > 0) {
            notificationCount.setText(String.valueOf(count));
            notificationCount.setVisible(true);
        } else {
            notificationCount.setVisible(false);
        }
    }

    public static void refreshNotifications() {
        if (instance != null) {
            instance.updateNotificationBadge();
        }
    }

    @FXML
    private void toggleNotifications() {
        notificationService.showNotificationDialog();
        updateNotificationBadge();
    }

    @FXML
    private void openProfile() {
        User currentUser = DataController.getInstance().getCurrentUser();
        if (currentUser != null) {
            Alert profile = new Alert(Alert.AlertType.INFORMATION);
            profile.setTitle("Profile");
            profile.setHeaderText("User Profile");
            profile.setContentText("Username: " + currentUser.getUserName() + "\nRole: " + currentUser.getRole()+ "\nContact: " + currentUser.getContactInfo()+ "\nAddress: " + currentUser.getAddress());
            profile.showAndWait();
            DialogPane dialogPane = profile.getDialogPane();
            dialogPane.setPrefSize(400, 300);
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            DataController.getInstance().setCurrentUser(null);
            Parent root = FXMLLoader.load(getClass().getResource("/ims/view/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("IMS - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}