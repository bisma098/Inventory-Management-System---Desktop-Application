package ims.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class ManagerDashboardController implements Initializable {
    
    @FXML
    private HeaderController headerController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization code if needed
    }

    // In ManagerDashboardController, AdminDashboardController, StaffDashboardController
    public void setUserName(String userName) {
    System.out.println("=== DEBUG DashboardController.setUserName() ===");
    System.out.println("Dashboard controller received username: " + userName);
    System.out.println("headerController is null: " + (headerController == null));
    
    if (headerController != null) {
        headerController.setUserName(userName);
    } else {
        System.out.println("ERROR: headerController is null in dashboard!");
    }
    System.out.println("=== END DEBUG ===");
}
}