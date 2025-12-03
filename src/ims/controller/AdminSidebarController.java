package ims.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;

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
        loadContent("/ims/view/UserActivityLog.fxml");
    }

    @FXML
    private void showInventoryAuditLog(ActionEvent event) {
        loadContent("/ims/view/InventoryAuditLogView.fxml");
    }

    @FXML
    private void showCategories(ActionEvent event) {
        loadContent("/ims/view/AddCategories.fxml");
    }

    @FXML
    private void WarehouseTracking(ActionEvent event) {
        loadContent("/ims/view/WarehouseTracking.fxml");
        
    }
    
    @FXML
    public void openBatchTracking(ActionEvent event)  {
    loadContent("/ims/view/BatchTracking.fxml"); 
    }

    @FXML
    public void openAddUser(ActionEvent event) {
        loadContent("/ims/view/AddUser.fxml");
}

      @FXML
    private void showWarehouse() {
        loadContent("/ims/view/Warehouse.fxml");

    }
    
    @FXML
    private void showDatabase() {
       loadContent("/ims/view/Backup.fxml");
    }
    
    @FXML
    private void showSupplier() {
       loadContent("/ims/view/AddSupplier.fxml");
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
