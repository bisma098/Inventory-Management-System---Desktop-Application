package ims.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class AdminSidebarController {

    @FXML
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

    // ================== EVENT HANDLERS ==================

    @FXML
    private void showInventory(ActionEvent event) {
        System.out.println("Navigating to Inventory...");
        // TODO: Load Inventory view (AdminDashboardController.loadView("Inventory.fxml"))
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
}
