package ims.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class AdminSidebarController {

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
    private void showUserActivityLog(ActionEvent event) {
        loadContent("/ims/view/UserActivityLog.fxml");
    }

    @FXML
    private void showInventoryAuditLog(ActionEvent event) {
        loadContent("/ims/view/InventoryAuditLogView.fxml");
    }

    @FXML
    private void showCategories(ActionEvent event) {
        System.out.println("Navigating to Categories...");
        // TODO: Load Categories management view
    }

    @FXML
    private void showProductTracking(ActionEvent event) {
        System.out.println("Navigating to Product Tracking...");
        // TODO: Load Product Tracking view
    }

    @FXML
    private void showUsers(ActionEvent event) {
        System.out.println("Navigating to Users...");
        // TODO: Load Users management view
    }

      @FXML
    private void showWarehouse() {
        loadContent("/ims/view/Warehouse.fxml");

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
