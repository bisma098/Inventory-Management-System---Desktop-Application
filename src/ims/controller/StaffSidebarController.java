package ims.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class StaffSidebarController {

    @FXML
    private VBox sidebar;

    @FXML
    private Button btnInventory;

    @FXML
    private Button btnSalesOrder;

    @FXML
    private Button btnCustomerReturns;

    @FXML
    private Button btnLogout;

    // ================== EVENT HANDLERS ==================

    @FXML
    private void showInventory(ActionEvent event) {
        System.out.println("Navigating to Inventory...");
        // TODO: Load Inventory view (e.g., StaffDashboardController.loadView("Inventory.fxml"))
    }

    @FXML
    private void showSalesOrder(ActionEvent event) {
        System.out.println("Navigating to Sales Order...");
        // TODO: Load Sales Order view
    }

    @FXML
    private void showCustomerReturns(ActionEvent event) {
        System.out.println("Navigating to Customer Returns...");
        // TODO: Load Customer Returns view
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logging out...");
        // TODO: Implement logout logic (close session, redirect to login screen)
    }
}
