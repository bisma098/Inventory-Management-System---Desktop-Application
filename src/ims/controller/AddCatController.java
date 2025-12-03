package ims.controller;

import ims.database.DatabaseConnection;
import ims.model.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.sql.*;
import java.util.Optional;

public class AddCatController {

    @FXML
    private TableView<Category> tblCategories;

    @FXML
    private TableColumn<Category, Integer> colId;

    @FXML
    private TableColumn<Category, String> colName;

    private ObservableList<Category> categoryList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Bind columns to Category properties
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        loadCategoriesFromDB();  // Load categories from DB on startup
    }

    // ===========================
    // LOAD CATEGORIES FROM DATABASE
    // ===========================
    private void loadCategoriesFromDB() {
        categoryList.clear();  // Clear existing items

        String query = "SELECT category_id, category_name FROM categories";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                categoryList.add(new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        tblCategories.setItems(categoryList);
    }

    // ===========================
    // ADD NEW CATEGORY BUTTON CLICK
    // ===========================
    @FXML
private void addCategory() {
    // Create a custom dialog
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Add New Category");
    dialog.setHeaderText("Enter Category Details");

    // Apply popup.css (if exists)
    dialog.getDialogPane().getStylesheets().add(getClass().getResource("/ims/view/popup.css").toExternalForm());

    // Create GridPane for inputs
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField nameField = new TextField();
    nameField.setPromptText("Category Name");

    TextField descField = new TextField();
    descField.setPromptText("Description (optional)");

    grid.add(new Label("Name:"), 0, 0);
    grid.add(nameField, 1, 0);
    grid.add(new Label("Description:"), 0, 1);
    grid.add(descField, 1, 1);

    dialog.getDialogPane().setContent(grid);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    Optional<ButtonType> result = dialog.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        String name = nameField.getText().trim();
        String description = descField.getText().trim();

        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Category name cannot be empty!");
            alert.showAndWait();
            return;
        }

        // Find the next ID manually
        int nextId = 1; // default if table is empty
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(category_id) AS max_id FROM categories")) {

            if (rs.next()) {
                nextId = rs.getInt("max_id") + 1;
            }

            // Insert into DB with manual ID
            String insertQuery = "INSERT INTO categories(category_id, category_name, description) VALUES (?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(insertQuery)) {
                pst.setInt(1, nextId);
                pst.setString(2, name);
                pst.setString(3, description);
                pst.executeUpdate();

                   // Add new category to DataController list
                Category newCat = new Category(nextId, name);
                DataController.getInstance().addCategoryToList(newCat);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to add category. Please try again.");
            alert.showAndWait();
            return;
        }

        loadCategoriesFromDB(); // Refresh TableView
    }
}


    // ===========================
    // BACK BUTTON
    // ===========================
   @FXML
private void goBack(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ims/view/AdminDashboard.fxml"));
        Parent dashboard = loader.load();

        BorderPane mainRoot = (BorderPane) ((Node) event.getSource())
                .getScene().getRoot();

        mainRoot.setCenter(dashboard);

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
