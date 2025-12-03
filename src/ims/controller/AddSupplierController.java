package ims.controller;

import ims.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import ims.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddSupplierController {

    @FXML
    private TextField txtSupplierName;

    @FXML
    private TextField txtSupplierContactInfo; // updated id

    @FXML
    public void addSupplier() {
        String name = txtSupplierName.getText().trim();
        String contactInfo = txtSupplierContactInfo.getText().trim();

        if (name.isEmpty() || contactInfo.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("All fields are required!");
            alert.showAndWait();
            return;
        }

        // Correct INSERT query for your table
        String query = "INSERT INTO Supplier(name, contactInfo) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, name);
            pst.setString(2, contactInfo);
            int rows = pst.executeUpdate();
            

            if (rows > 0) {
                // Get generated ID
                PreparedStatement pst2 = conn.prepareStatement("SELECT SCOPE_IDENTITY()");
                var rs = pst2.executeQuery();
                int newId = 0;
                if (rs.next()) newId = rs.getInt(1);

                // Add to DataController list
                Supplier supplier = new Supplier(newId, name, contactInfo);
                DataController.getInstance().addSupplierToList(supplier);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Supplier added successfully!");
            alert.showAndWait();

            // Clear
            txtSupplierName.clear();
            txtSupplierContactInfo.clear();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to add supplier. Please try again.");
            alert.showAndWait();
        }
    }
}
