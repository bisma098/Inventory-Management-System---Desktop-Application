package ims.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {
    
    @FXML
    private HeaderController headerController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization code if needed
    }

    public void setUserName(String userName) {
        if (headerController != null) {
            headerController.setUserName(userName);
        }
    }
}