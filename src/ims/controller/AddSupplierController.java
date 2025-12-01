package ims.controller;

import ims.database.DatabaseConnection;
import ims.model.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddSupplierController {

    @FXML
    private TableView<Supplier> tblSuppliers;

    @FXML
    private TableColumn<Supplier, Integer> colId;

    @FXML
    private TableColumn<Supplier, String> colName;

    @FXML
    private TableColumn<Supplier, String> colContactInfo;

    @FXML
    private Button btnAddSupplier;

    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContactInfo.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));

        // Load suppliers from DB
        loadSuppliers();
    }

    private void loadSuppliers() {
        supplierList.clear();
        try {
            Connection con = DatabaseConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Supplier");
            while (rs.next()) {
                supplierList.add(new Supplier(
                        rs.getInt("supplierId"),
                        rs.getString("name"),
                        rs.getString("contactInfo")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tblSuppliers.setItems(supplierList);
    }

    @FXML
    private void AddSupplier() {
        try {
            // Replace with real input from user (popup or TextField)
            String name = "New Supplier";  
            String contactInfo = "123456789";  

            Connection con = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Supplier (name, contactInfo) VALUES (?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, contactInfo);

            int inserted = pst.executeUpdate();
            if (inserted > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Supplier added successfully!");
                alert.showAndWait();

                loadSuppliers(); // refresh table
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed");
                alert.setHeaderText(null);
                alert.setContentText("Failed to add supplier!");
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        btnAddSupplier.getScene().getWindow().hide();
    }
}
