package ims.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class StaffSidebarController {

    @FXML
    private Button btnInventory,btnSalesOrder,btnCustomerReturns;

    // ================== EVENT HANDLERS ==================

    @FXML
      private void showInventory() {
        loadContent("/ims/view/Inventory.fxml");
    }

    @FXML
    private void showSalesOrder(ActionEvent event) {
        loadContent("/ims/view/SalesOrder.fxml");
    }

    @FXML
    private void showCustomerReturns(ActionEvent event) {
        loadContent("/ims/view/CustomerReturn.fxml");
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
