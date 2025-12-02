package ims.controller;

import ims.database.DatabaseConnection;
import ims.model.SupplierPerformanceItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SupplierPerformanceController {

    @FXML private TableView<SupplierPerformanceItem> tableReport;
    @FXML private TableColumn<SupplierPerformanceItem, Integer> colSupplierId;
    @FXML private TableColumn<SupplierPerformanceItem, String> colSupplierName;
    @FXML private TableColumn<SupplierPerformanceItem, Integer> colTotalOrders;
    @FXML private TableColumn<SupplierPerformanceItem, Double> colTotalSupplied;
    @FXML private TableColumn<SupplierPerformanceItem, Double> colTotalReturned;
    @FXML private TableColumn<SupplierPerformanceItem, Double> colNetSupplied;

    @FXML private Button btnDownload;
    @FXML private Button btnBack;

    public void initialize() {
        colSupplierId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colSupplierName.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colTotalOrders.setCellValueFactory(new PropertyValueFactory<>("totalOrders"));
        colTotalSupplied.setCellValueFactory(new PropertyValueFactory<>("totalSupplied"));
        colTotalReturned.setCellValueFactory(new PropertyValueFactory<>("totalReturned"));
        colNetSupplied.setCellValueFactory(new PropertyValueFactory<>("netSupplied"));

        loadReportData();

        btnBack.setOnAction(e -> goBack());
        btnDownload.setOnAction(e -> downloadCSV());
    }

    private void loadReportData() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT " +
                "s.supplierId, " +
                "s.name AS SupplierName, " +
                "COUNT(DISTINCT po.orderId) AS TotalOrders, " +
                "ISNULL(SUM(po.totalAmount), 0) AS TotalSuppliedAmount, " +
                "ISNULL(SUM(sr.totalAmount), 0) AS TotalReturnedAmount, " +
                "ISNULL(SUM(po.totalAmount), 0) - ISNULL(SUM(sr.totalAmount), 0) AS NetSuppliedAmount " +
                "FROM Supplier s " +
                "LEFT JOIN PurchaseOrder po ON s.supplierId = po.supplierId " +
                "LEFT JOIN SupplierReturn sr ON s.supplierId = sr.supplierId " +
                "GROUP BY s.supplierId, s.name " +
                "ORDER BY supplierId DESC"
            );

            ArrayList<SupplierPerformanceItem> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new SupplierPerformanceItem(
                        rs.getInt("supplierId"),
                        rs.getString("SupplierName"),
                        rs.getInt("TotalOrders"),
                        rs.getDouble("TotalSuppliedAmount"),
                        rs.getDouble("TotalReturnedAmount"),
                        rs.getDouble("NetSuppliedAmount")
                ));
            }

            tableReport.getItems().setAll(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ims/view/AdminDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadCSV() {
        try {
            String userHome = System.getProperty("user.home");
            String desktopPath = userHome + "/Desktop/SupplierPerformanceReport.csv";

            FileWriter writer = new FileWriter(desktopPath);
            writer.write("Supplier ID,Supplier Name,Total Orders,Total Supplied,Total Returned,Net Supplied\n");

            for (SupplierPerformanceItem item : tableReport.getItems()) {
                writer.write(item.getSupplierId() + "," +
                        item.getSupplierName() + "," +
                        item.getTotalOrders() + "," +
                        item.getTotalSupplied() + "," +
                        item.getTotalReturned() + "," +
                        item.getNetSupplied() + "\n");
            }

            writer.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Download Successful");
            alert.setHeaderText(null);
            alert.setContentText("Supplier Performance report successfully downloaded to Desktop!");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Download Failed");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while downloading the report.");
            alert.showAndWait();
        }
    }
}
