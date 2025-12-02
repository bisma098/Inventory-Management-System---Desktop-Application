package ims.controller;

import ims.database.DatabaseConnection;
import ims.model.SalesStockItem;
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

public class SalesVsStockController {

    @FXML private TableView<SalesStockItem> tableReport;
    @FXML private TableColumn<SalesStockItem, Integer> colProductId;
    @FXML private TableColumn<SalesStockItem, String> colProductName;
    @FXML private TableColumn<SalesStockItem, String> colCategory;
    @FXML private TableColumn<SalesStockItem, Integer> colTotalStock;
    @FXML private TableColumn<SalesStockItem, Integer> colTotalSold;
    @FXML private TableColumn<SalesStockItem, Integer> colStockRemaining;

    @FXML private Button btnDownload;
    @FXML private Button btnBack;

    public void initialize() {
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colTotalStock.setCellValueFactory(new PropertyValueFactory<>("totalStock"));
        colTotalSold.setCellValueFactory(new PropertyValueFactory<>("totalSold"));
        colStockRemaining.setCellValueFactory(new PropertyValueFactory<>("stockRemaining"));

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
                            "p.product_id AS ProductID, " +
                            "p.product_name AS ProductName, " +
                            "c.category_name AS Category, " +
                            "ISNULL(SUM(psr.quantity), 0) AS TotalStock, " +
                            "ISNULL(SUM(sol.quantity), 0) AS TotalSold, " +
                            "ISNULL(SUM(psr.quantity), 0) - ISNULL(SUM(sol.quantity), 0) AS StockRemaining " +
                    "FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "LEFT JOIN ProductStockRecord psr ON p.product_id = psr.productId " +
                    "LEFT JOIN SalesOrderLine sol ON p.product_id = sol.productId " +
                    "GROUP BY p.product_id, p.product_name, c.category_name " +
                    "ORDER BY p.product_name"
            );

            ArrayList<SalesStockItem> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new SalesStockItem(
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getString("Category"),
                        rs.getInt("TotalStock"),
                        rs.getInt("TotalSold"),
                        rs.getInt("StockRemaining")
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
            String desktopPath = userHome + "/Desktop/SalesVsStockReport.csv";

            FileWriter writer = new FileWriter(desktopPath);
            writer.write("Product ID,Product Name,Category,Total Stock,Total Sold,Stock Remaining\n");

            for (SalesStockItem item : tableReport.getItems()) {
                writer.write(item.getProductId() + "," +
                        item.getName() + "," +
                        item.getCategory() + "," +
                        item.getTotalStock() + "," +
                        item.getTotalSold() + "," +
                        item.getStockRemaining() + "\n");
            }

            writer.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Download Successful");
            alert.setHeaderText(null);
            alert.setContentText("Sales vs Stock report successfully downloaded to Desktop!");
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
