package ims.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminSidebarController {
    @FXML
    private AnchorPane mainContentPane; // connected to fx:id="mainContentPane" in AdminDashboard.fxml

    @FXML
    private Button btnInventory,btnReports, btnUserActivity,btnAuditLog,btnCategories,btnProductTracking,btnUsers;

    // ================== EVENT HANDLERS ==================

    @FXML
    private void showInventory() {
        loadContent("/ims/view/Inventory.fxml");
    }

    @FXML
    private void showReports(ActionEvent event) {
        System.out.println("Navigating to Reports...");
        // TODO: Load Reports view
    }
    @FXML
    private void showDatabase() {
       loadContent("/ims/view/Backup.fxml");
    }
    @FXML
    private void showUserActivityLog(ActionEvent event) {
        loadContent("/ims/view/UserActivityLogView.fxml");
        // TODO: Load User Activity Log view
    }

    @FXML
    private void showInventoryAuditLog(ActionEvent event) {
        loadContent("/ims/view/InventoryAuditLogView.fxml");
        // TODO: Load Inventory Audit Log view
    }

    @FXML
    private void showCategories(ActionEvent event) {
        System.out.println("Navigating to Categories...");
        // TODO: Load Categories management view
    }
    
    @FXML
    private void WarehouseTracking(ActionEvent event) {
        loadContent("/ims/view/WarehouseTracking.fxml");
        
    }

    @FXML
    private void showProductTracking(ActionEvent event) {
        System.out.println("Navigating to Product Tracking...");
        // TODO: Load Product Tracking view
    }
    public void openAddUser(ActionEvent event) {
        loadContent("/ims/view/AddUser.fxml");
}

    @FXML
    private void showUsers(ActionEvent event) {
        System.out.println("Navigating to Users...");
        // TODO: Load Users management view
    }
    
    private void loadContent(String fxmlPath) {
        try {
            BorderPane parentBorderPane = getParentBorderPane();
            if (parentBorderPane != null) {
                parentBorderPane.setCenter(FXMLLoader.load(getClass().getResource(fxmlPath)));
                System.out.println("Successfully loaded: " + fxmlPath);
            } else {
                System.err.println("Parent BorderPane not found!");
            }
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private BorderPane getParentBorderPane() {
        if (btnInventory != null) {
            var scene = btnInventory.getScene();
            if (scene != null) {
                var root = scene.getRoot();
                if (root instanceof BorderPane) {
                    return (BorderPane) root;
                }
            }
        }
        return null;
    }
    
    @FXML
    public void initialize() {
        // Use Platform.runLater to delay the initial load until the scene is fully set up
        javafx.application.Platform.runLater(() -> {
            showInventory();
        });
    }

}
