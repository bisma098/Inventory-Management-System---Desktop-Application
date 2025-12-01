package ims.service;

import ims.model.Notification;
import ims.controller.DataController;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.stage.StageStyle;

import java.util.List;

public class NotificationService {
    private static NotificationService instance;
    private DataController dataController;
    
    private NotificationService() {
        this.dataController = DataController.getInstance();
    }
    
    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }
    
    public List<Notification> getNotifications() {
        return dataController.getNotifications();
    }
    
    public boolean hasNotifications() {
        return !getNotifications().isEmpty();
    }
    
    public int getNotificationCount() {
        return getNotifications().size();
    }
    
    public void showNotificationDialog() {
        List<Notification> notifications = getNotifications();
        
        if (notifications.isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Notifications");
            alert.setHeaderText(null);
            alert.setContentText("No new notifications.");
            alert.showAndWait();
            return;
        }
        
        StringBuilder message = new StringBuilder();
        message.append("You have ").append(notifications.size()).append(" notification(s):\n\n");
        
        for (Notification notification : notifications) {
            message.append("• ").append(notification.getMessage()).append("\n");
        }
        
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Stock Alerts");
        alert.setHeaderText(null); // Remove the big header
        alert.setContentText(message.toString());
        
        // Set smaller dialog size
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setPrefSize(400, 300);
        
        alert.showAndWait();
    }
}