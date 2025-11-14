package ims.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ManagerSidebarController {

    @FXML
    private Label managerName;

    @FXML
    private Button btnInventory, btnProducts, btnPurchaseOrders, btnSupplierReturn, btnAdjustStock, btnProductTracking, btnLogout;

    @FXML
    private void showInventory() {
        System.out.println("Inventory clicked");
        // loadInventoryPage();
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
}
