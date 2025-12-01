package ims.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReportsDialogController {

    private Stage mainStage;
    private Stage popupStage;

    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    public void setPopupStage(Stage popup) {
        this.popupStage = popup;
    }

    // -------------------
    // OPEN REPORT SCREENS
    // -------------------

    public void openStock() {
        openReport("/ims/Reports/StockReport.fxml");
    }

    public void openSales() {
        openReport("/ims/Reports/SalesVsStock.fxml");
    }

    public void openSlowFast() {
        openReport("/ims/Reports/SlowFastItems.fxml");
    }

    public void openSupplier() {
        openReport("/ims/Reports/SupplierPerformance.fxml");
    }

    // Generic function to open ANY report
    private void openReport(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 700);
            mainStage.setScene(scene);
            mainStage.show();

            popupStage.close(); // close popup after selecting

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
