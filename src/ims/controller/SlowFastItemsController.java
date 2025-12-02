package ims.controller;

import ims.database.DatabaseConnection;
import ims.model.SlowFastItem;
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

public class SlowFastItemsController {

    @FXML private TableView<SlowFastItem> tableReport;
    @FXML private TableColumn<SlowFastItem, Integer> colProductId;
    @FXML private TableColumn<SlowFastItem, String> colProductName;
    @FXML private TableColumn<SlowFastItem, String> colCategory;
    @FXML private TableColumn<SlowFastItem, Integer> colTotalSold;
    @FXML private TableColumn<SlowFastItem, Integer> colStockRemaining;

    @FXML private Button btnDownload;
    @FXML private Button btnBack;

    public void initialize() {
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
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
                            "ISNULL(SUM(sol.quantity), 0) AS TotalSold, " +
                            "p.quantity AS StockRemaining " +
                    "FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "LEFT JOIN SalesOrderLine sol ON p.product_id = sol.productId " +
                    "GROUP BY p.product_id, p.product_name, c.category_name, p.quantity " +
                    "ORDER BY TotalSold ASC" // Slow moving items first
            );

            ArrayList<SlowFastItem> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new SlowFastItem(
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getString("Category"),
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
            String desktopPath = userHome + "/Desktop/SlowFastItemsReport.csv";

            FileWriter writer = new FileWriter(desktopPath);
            writer.write("Product ID,Product Name,Category,Total Sold,Stock Remaining\n");

            for (SlowFastItem item : tableReport.getItems()) {
                writer.write(item.getProductId() + "," +
                        item.getName() + "," +
                        item.getCategory() + "," +
                        item.getTotalSold() + "," +
                        item.getStockRemaining() + "\n");
            }

            writer.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Download Successful");
            alert.setHeaderText(null);
            alert.setContentText("Slow/Fast Items report successfully downloaded to Desktop!");
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
