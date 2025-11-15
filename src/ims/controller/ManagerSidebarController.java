package ims.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class ManagerSidebarController {

    @FXML
    private Button btnInventory, btnProducts, btnPurchaseOrders, btnSupplierReturn, btnAdjustStock, btnProductTracking;

    @FXML
    private void showInventory() {
        loadContent("/ims/view/Inventory.fxml");
    }

    @FXML
    private void showProducts() {
        System.out.println("Products clicked");
    }

    @FXML
    private void showPurchaseOrders() {
        System.out.println("Purchase Orders clicked");
    }

    @FXML
    private void showSupplierReturns() {
        System.out.println("Supplier Returns clicked");
    }

    @FXML
    private void showAdjustStock() {
        System.out.println("Adjust Stock clicked");
    }

    @FXML
    private void showProductTracking() {
        System.out.println("Product Tracking clicked");
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logout clicked");
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