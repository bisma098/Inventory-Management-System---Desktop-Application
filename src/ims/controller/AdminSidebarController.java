package ims.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
<<<<<<< Updated upstream
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
=======
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
>>>>>>> Stashed changes

public class AdminSidebarController {

    @FXML
<<<<<<< Updated upstream
    private VBox sidebar;

    @FXML
    private Button btnInventory;

    @FXML
    private Button btnReports;

    @FXML
    private Button btnUserActivity;

    @FXML
    private Button btnAuditLog;

    @FXML
    private Button btnCategories;

    @FXML
    private Button btnProductTracking;

    @FXML
    private Button btnUsers;
=======
    private Button btnInventory,btnReports, btnUserActivity,btnAuditLog,btnCategories,btnProductTracking,btnUsers;
>>>>>>> Stashed changes

    // ================== EVENT HANDLERS ==================

    @FXML
<<<<<<< Updated upstream
    private void showInventory(ActionEvent event) {
        System.out.println("Navigating to Inventory...");
        // TODO: Load Inventory view (AdminDashboardController.loadView("Inventory.fxml"))
=======
    private void showInventory() {
        loadContent("/ims/view/Inventory.fxml");
>>>>>>> Stashed changes
    }

    @FXML
    private void showReports(ActionEvent event) {
        System.out.println("Navigating to Reports...");
        // TODO: Load Reports view
    }

    @FXML
    private void showUserActivityLog(ActionEvent event) {
        System.out.println("Navigating to User Activity Log...");
        // TODO: Load User Activity Log view
    }

    @FXML
    private void showInventoryAuditLog(ActionEvent event) {
        System.out.println("Navigating to Inventory Audit Log...");
        // TODO: Load Inventory Audit Log view
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
<<<<<<< Updated upstream
}
=======
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
>>>>>>> Stashed changes
