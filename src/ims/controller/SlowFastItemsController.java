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
        // FIXED: Use correct property names from your model
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));  // was "productName"
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colTotalSold.setCellValueFactory(new PropertyValueFactory<>("totalSold"));
        colStockRemaining.setCellValueFactory(new PropertyValueFactory<>("stockRemaining"));

        loadReportData();

        btnBack.setOnAction(e -> goBack());
        btnDownload.setOnAction(e -> downloadCSV());
    }

    private void loadReportData() {
        String sql = 
            "SELECT " +
            "    p.product_id, " +
            "    p.product_name, " +
            "    c.category_name, " +
            "    ISNULL(SUM(sol.quantity), 0) AS totalSold, " +
            "    ISNULL(SUM(b.availableQty), 0) AS stockRemaining " +
            "FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.category_id " +
            "LEFT JOIN SalesOrderLine sol ON p.product_id = sol.productId " +
            "LEFT JOIN BatchLot b ON p.product_id = b.productId " +
            "GROUP BY p.product_id, p.product_name, c.category_name " +
            "ORDER BY totalSold ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ArrayList<SlowFastItem> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new SlowFastItem(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category_name"),
                        rs.getInt("totalSold"),
                        rs.getInt("stockRemaining")
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
            String desktopPath = System.getProperty("user.home") + "/Desktop/SlowFastItemsReport.csv";

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