package ims.controller;

import ims.database.DatabaseConnection;
import ims.model.Stockitem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

public class StockReportController {

    @FXML private TableView<Stockitem> stockTable;
    @FXML private TableColumn<Stockitem, Integer> colProductId;
    @FXML private TableColumn<Stockitem, String> colProductName;
    @FXML private TableColumn<Stockitem, String> colCategory;
    @FXML private TableColumn<Stockitem, Integer> colQuantity;

    @FXML private Button btnDownload;
    @FXML private Button btnBack;

    public void initialize() {
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        loadStockData();

        btnBack.setOnAction(e -> goBack(e));
        btnDownload.setOnAction(e -> downloadCSV());
    }

    private void loadStockData() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT " +
                    "    p.product_id AS ProductID, " +
        "    p.product_name AS ProductName, " +
        "    c.category_name AS Category, " +
        "    ISNULL(SUM(pol.quantity),0) " +           // Total purchased
        "    - ISNULL(SUM(sol.quantity),0) " +         // Total sold
        "    + ISNULL(SUM(crl.quantity),0) " +         // Customer returns
        "    - ISNULL(SUM(srl.quantity),0) " +         // Supplier returns
        "    AS StockQuantity " +
        "FROM products p " +
        "LEFT JOIN categories c ON p.category_id = c.category_id " +
        "LEFT JOIN PurchaseOrderLine pol ON p.product_id = pol.productId " +
        "LEFT JOIN SalesOrderLine sol ON p.product_id = sol.productId " +
        "LEFT JOIN CustomerReturnLine crl ON p.product_id = crl.productId " +
        "LEFT JOIN SupplierReturnLine srl ON p.product_id = srl.productId " +
        "GROUP BY p.product_id, p.product_name, c.category_name " +
        "ORDER BY p.product_name"
            );

            ArrayList<Stockitem> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Stockitem(
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getString("Category"),
                        rs.getInt("StockQuantity")
                ));
            }

            stockTable.getItems().setAll(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            Parent previous = FXMLLoader.load(getClass().getResource("/ims/view/AdminDashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(previous));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadCSV() {
    try {
        // Save to Desktop
        String userHome = System.getProperty("user.home");
        String desktopPath = userHome + "/Desktop/StockReport.csv";

        FileWriter writer = new FileWriter(desktopPath);
        writer.write("Product ID,Product Name,Category,Quantity\n");

        for (Stockitem item : stockTable.getItems()) {
            writer.write(item.getProductId() + "," +
                    item.getName() + "," +
                    item.getCategory() + "," +
                    item.getQuantity() + "\n");
        }

        writer.close();

        // Show success message
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Download Successful");
        alert.setHeaderText(null);
        alert.setContentText("Stock report successfully downloaded to Desktop!");
        alert.showAndWait();

    } catch (Exception e) {
        e.printStackTrace();

        // Show error message
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Download Failed");
        alert.setHeaderText(null);
        alert.setContentText("An error occurred while downloading the stock report.");
        alert.showAndWait();
    }
}

}
