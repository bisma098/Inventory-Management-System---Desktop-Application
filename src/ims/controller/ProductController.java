package ims.controller;

import ims.model.Product;
import ims.model.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProductController {

    @FXML private TextField searchField;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colSKU;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colQty;
    @FXML private TableColumn<Product, Category> colCategory;

    @FXML private TextField addNameField;
    @FXML private TextField addSKUField;
    @FXML private TextField addPriceField;
    @FXML private TextField addQtyField;
    @FXML private TextField addCategoryField;

    private ObservableList<Product> products = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Map columns
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getProductId()));
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        colSKU.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getSKU()));
        colPrice.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getPrice()));
        colQty.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getQuantity()));
        colCategory.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getCategory()));

        loadDummyProducts();
        productTable.setItems(products);
    }

    private void loadDummyProducts() {
        products.add(new Product(1, "Milk", "SKU101", 150, 20, new Category(1, "Dairy")));
        products.add(new Product(2, "Bread", "SKU201", 80, 15, new Category(2, "Bakery")));
        products.add(new Product(3, "Eggs", "SKU301", 200, 30, new Category(3, "Poultry")));
    }

    @FXML
    private void addProduct() {
        int nextId = products.size() + 1;
        Category cat = new Category(nextId, addCategoryField.getText());
        Product p = new Product(
                nextId,
                addNameField.getText(),
                addSKUField.getText(),
                Double.parseDouble(addPriceField.getText()),
                Integer.parseInt(addQtyField.getText()),
                cat
        );
        products.add(p);
        productTable.refresh();
        clearAddFields();
    }

    private void clearAddFields() {
        addNameField.clear();
        addSKUField.clear();
        addPriceField.clear();
        addQtyField.clear();

        addCategoryField.clear();
    }

    @FXML
    private void deleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            products.remove(selected);
        }
    }

    @FXML
    private void searchProduct() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            productTable.setItems(products);
            return;
        }

        ObservableList<Product> filtered = FXCollections.observableArrayList();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(keyword) ||
                p.getSKU().toLowerCase().contains(keyword) ||
                p.getCategory().getName().toLowerCase().contains(keyword)) {
                filtered.add(p);
            }
        }

        productTable.setItems(filtered);
    }
}
