package ims.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.Node;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AdminSidebarController {

    @FXML
    private Button btnInventory,btnReports, btnUserActivity,btnAuditLog,btnCategories,btnProductTracking,btnUsers,btnSuppliers;

    // ================== EVENT HANDLERS ==================

    @FXML
    private void showInventory() {
        loadContent("/ims/view/Inventory.fxml");
    }


  
@FXML
private void showReports(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ims/view/ReportsDialog.fxml"));
        Parent root = loader.load();

        ReportsDialogController controller = loader.getController();
        controller.setMainStage((Stage)((Node)event.getSource()).getScene().getWindow());

        Scene scene = new Scene(root);
        //scene.getStylesheets().add(getClass().getResource("/ims/css/reports-dialog.css").toExternalForm());
         // ADD YOUR POPUP CSS
        scene.getStylesheets().add(getClass().getResource("/ims/view/popup.css").toExternalForm());
        Stage popup = new Stage();
        controller.setPopupStage(popup);   // << set popup stage

        popup.setScene(scene);
        popup.setTitle("Select Report");
        popup.setResizable(false);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.centerOnScreen();
        popup.show();

    } catch (Exception e) {
        e.printStackTrace();
    }
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
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ims/view/AddCategories.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        Scene scene = new Scene(root, 1200, 700); // 🌟 FIXED SIZE SCENE
        stage.setScene(scene);
        stage.setResizable(false); // 🔒 PREVENT RESIZING
        
        stage.show();

        System.out.println("Navigated to Add Categories page.");

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Failed to load AddCategories.fxml");
    }
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
private void showSuppliers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ims/view/AddSupplier.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root, 1200, 700); // Fixed size scene
            stage.setScene(scene);
            stage.setResizable(false); // Prevent resizing

            stage.show();

            System.out.println("Navigated to Add Supplier page.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load AddSupplier.fxml");
        }

    }
    @FXML
    public void initialize() {
        // Use Platform.runLater to delay the initial load until the scene is fully set up
        javafx.application.Platform.runLater(() -> {
            showInventory();
        });
    }
}
