package ims.controller;

import ims.database.DatabaseConnection;
import ims.model.Warehouse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.Optional;

public class WarehouseController {

    @FXML private TableView<Warehouse> warehouseTable;
    @FXML private TableColumn<Warehouse, Integer> colId;
    @FXML private TableColumn<Warehouse, String> colName;
    @FXML private TableColumn<Warehouse, String> colAddress;

    @FXML private TextField txtName;
    @FXML private TextField txtAddress;

    private ObservableList<Warehouse> warehouseList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getWarehouseId()).asObject());
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getWarehouseName()));
        colAddress.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAddress()));

        loadWarehouses();

        warehouseTable.setOnMouseClicked(e -> {
            Warehouse w = warehouseTable.getSelectionModel().getSelectedItem();
            if (w != null) {
                txtName.setText(w.getWarehouseName());
                txtAddress.setText(w.getAddress());
            }
        });
    }

    // LOAD DATA
    private void loadWarehouses() {
        warehouseList.clear();

        String query = "SELECT warehouseId, warehouseName, address FROM Warehouse ORDER BY warehouseId";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                warehouseList.add(new Warehouse(
                        rs.getInt("warehouseId"),
                        rs.getString("warehouseName"),
                        rs.getString("address")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                     "Failed to load warehouses: " + e.getMessage());
        }

        warehouseTable.setItems(warehouseList);
    }

    // ADD WAREHOUSE
    @FXML
    private void addWarehouse() {
        String name = txtName.getText().trim();
        String address = txtAddress.getText().trim();

        // Validation
        if (name.isEmpty() || address.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", 
                     "Please fill in both Warehouse Name and Address.");
            return;
        }

        // Additional validation - check for minimum length
        if (name.length() < 3) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", 
                     "Warehouse name must be at least 3 characters long.");
            return;
        }

        if (address.length() < 5) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", 
                     "Address must be at least 5 characters long.");
            return;
        }

        // Check if warehouse name already exists
        if (warehouseNameExists(name)) {
            showAlert(Alert.AlertType.WARNING, "Duplicate Entry", 
                     "A warehouse with this name already exists!");
            return;
        }

        String sql = "INSERT INTO Warehouse (warehouseName, address) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, address);
            
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                         "Warehouse '" + name + "' added successfully!");
                loadWarehouses();
                clearFields();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                     "Failed to add warehouse: " + e.getMessage());
        }
    }

    // UPDATE WAREHOUSE
    @FXML
    private void updateWarehouse() {
        Warehouse selected = warehouseTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", 
                     "Please select a warehouse from the table to update.");
            return;
        }

        String name = txtName.getText().trim();
        String address = txtAddress.getText().trim();

        // Validation
        if (name.isEmpty() || address.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", 
                     "Please fill in both Warehouse Name and Address.");
            return;
        }

        // Confirmation dialog
        Optional<ButtonType> result = showConfirmation("Confirm Update", 
            "Are you sure you want to update warehouse '" + selected.getWarehouseName() + "'?");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "UPDATE Warehouse SET warehouseName=?, address=? WHERE warehouseId=?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, address);
                ps.setInt(3, selected.getWarehouseId());
                
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                             "Warehouse updated successfully!");
                    loadWarehouses();
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Update Failed", 
                             "No warehouse was updated. It may have been deleted.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", 
                         "Failed to update warehouse: " + e.getMessage());
            }
        }
    }

    // DELETE WAREHOUSE
    @FXML
    private void deleteWarehouse() {
        Warehouse selected = warehouseTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", 
                     "Please select a warehouse from the table to delete.");
            return;
        }

        // Confirmation dialog with more details
        Optional<ButtonType> result = showConfirmation("Confirm Delete", 
            "Are you sure you want to delete the following warehouse?\n\n" +
            "ID: " + selected.getWarehouseId() + "\n" +
            "Name: " + selected.getWarehouseName() + "\n" +
            "Address: " + selected.getAddress() + "\n\n" +
            "This action cannot be undone!");
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM Warehouse WHERE warehouseId=?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, selected.getWarehouseId());
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                             "Warehouse '" + selected.getWarehouseName() + "' deleted successfully!");
                    loadWarehouses();
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Delete Failed", 
                             "No warehouse was deleted. It may have already been removed.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                
                // Check for foreign key constraint violation
                if (e.getMessage().contains("REFERENCE constraint") || 
                    e.getMessage().contains("foreign key constraint")) {
                    showAlert(Alert.AlertType.ERROR, "Cannot Delete", 
                             "This warehouse cannot be deleted because it has associated records.\n\n" +
                             "Please remove all related batch lots and purchase order lines first.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Database Error", 
                             "Failed to delete warehouse: " + e.getMessage());
                }
            }
        }
    }

    // CLEAR FIELDS
    @FXML
    private void clearFields() {
        txtName.clear();
        txtAddress.clear();
        warehouseTable.getSelectionModel().clearSelection();
    }

    // CHECK IF WAREHOUSE NAME EXISTS
    private boolean warehouseNameExists(String name) {
        String query = "SELECT COUNT(*) FROM Warehouse WHERE warehouseName = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    // ALERT HELPER METHOD
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // CONFIRMATION DIALOG HELPER METHOD
    private Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}