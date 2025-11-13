package ims.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class ManagerDashboardController {

    @FXML private TextField searchField;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private VBox contentContainer;

    @FXML
    public void initialize() {
        userNameLabel.setText("Marvin McKinney");
        userRoleLabel.setText("Sales Manager");

        // Show inventory section by default
        showInventorySection();
    }

    @FXML
    private void handleDashboardClick() {
        contentContainer.getChildren().clear();
        Label label = new Label("Dashboard Overview Coming Soon");
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #6b7280;");
        VBox box = new VBox(label);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(100));
        contentContainer.getChildren().add(box);
    }

    @FXML
    private void handleInventoryClick() {
        showInventorySection();
    }

    @FXML
    private void handlePurchaseOrdersClick() {
        showPurchaseOrdersSection();
    }

    @FXML
    private void handleSupplierReturnsClick() {
        showSupplierReturnsSection();
    }

    @FXML
    private void handleAdjustStockClick() {
        showAdjustStockSection();
    }

    @FXML
    private void handleProductTrackingClick() {
        showProductTrackingSection();
    }

    private void showInventorySection() {
        contentContainer.getChildren().clear();

        // Section Title
        Label title = new Label("Inventory");
        title.getStyleClass().add("section-title");

        // Action Cards Container
        HBox cardsContainer = new HBox(20);
        cardsContainer.getStyleClass().add("dashboard-cards");
        cardsContainer.setAlignment(Pos.CENTER_LEFT);

        // Add Product Card
        VBox addCard = createDashboardCard("Add Product", "📦", "Add new products to inventory");

        // Update Product Card
        VBox updateCard = createDashboardCard("Update Product", "✏️", "Modify existing products");

        // Delete Product Card
        VBox deleteCard = createDashboardCard("Delete Product", "🗑️", "Remove products from inventory");

        // Search Product Card
        VBox searchCard = createDashboardCard("Search Products", "🔍", "Find products in inventory");

        cardsContainer.getChildren().addAll(addCard, updateCard, deleteCard, searchCard);

        // Placeholder for product list
        VBox productListPlaceholder = new VBox(20);
        productListPlaceholder.getStyleClass().add("table-container");
        productListPlaceholder.setAlignment(Pos.CENTER);
        productListPlaceholder.setPadding(new Insets(50));
        productListPlaceholder.setPrefHeight(400);

        Label placeholderText = new Label("Product List Will Be Displayed Here");
        placeholderText.setStyle("-fx-font-size: 18px; -fx-text-fill: #9ca3af;");
        Label placeholderSubtext = new Label("Add functionality to load and display products");
        placeholderSubtext.setStyle("-fx-font-size: 14px; -fx-text-fill: #d1d5db;");

        productListPlaceholder.getChildren().addAll(placeholderText, placeholderSubtext);

        contentContainer.getChildren().addAll(title, cardsContainer, productListPlaceholder);
    }

    private void showPurchaseOrdersSection() {
        contentContainer.getChildren().clear();

        Label title = new Label("Purchase Orders");
        title.getStyleClass().add("section-title");

        HBox actionBar = new HBox(15);
        actionBar.getStyleClass().add("action-bar");

        Button createOrderBtn = new Button("Create Purchase Order");
        createOrderBtn.getStyleClass().add("primary-button");

        Button viewOrdersBtn = new Button("View All Orders");
        viewOrdersBtn.getStyleClass().add("secondary-button");

        actionBar.getChildren().addAll(createOrderBtn, viewOrdersBtn);

        VBox placeholder = createPlaceholder("Purchase Orders List", "Purchase order management will be displayed here");

        contentContainer.getChildren().addAll(title, actionBar, placeholder);
    }

    private void showSupplierReturnsSection() {
        contentContainer.getChildren().clear();

        Label title = new Label("Supplier Returns");
        title.getStyleClass().add("section-title");

        HBox actionBar = new HBox(15);
        actionBar.getStyleClass().add("action-bar");

        Button createReturnBtn = new Button("Create Return");
        createReturnBtn.getStyleClass().add("primary-button");

        Button viewReturnsBtn = new Button("View All Returns");
        viewReturnsBtn.getStyleClass().add("secondary-button");

        actionBar.getChildren().addAll(createReturnBtn, viewReturnsBtn);

        VBox placeholder = createPlaceholder("Supplier Returns List", "Return management will be displayed here");

        contentContainer.getChildren().addAll(title, actionBar, placeholder);
    }

    private void showAdjustStockSection() {
        contentContainer.getChildren().clear();

        Label title = new Label("Adjust Stock Levels");
        title.getStyleClass().add("section-title");

        HBox cardsContainer = new HBox(20);
        cardsContainer.getStyleClass().add("dashboard-cards");

        VBox increaseCard = createDashboardCard("Increase Stock", "📈", "Add stock quantity");
        VBox decreaseCard = createDashboardCard("Decrease Stock", "📉", "Remove stock quantity");
        VBox transferCard = createDashboardCard("Transfer Stock", "🔄", "Move between warehouses");

        cardsContainer.getChildren().addAll(increaseCard, decreaseCard, transferCard);

        VBox placeholder = createPlaceholder("Stock Adjustment Records", "Stock level changes will be tracked here");

        contentContainer.getChildren().addAll(title, cardsContainer, placeholder);
    }

    private void showProductTrackingSection() {
        contentContainer.getChildren().clear();

        Label title = new Label("Product Tracking");
        title.getStyleClass().add("section-title");

        HBox cardsContainer = new HBox(20);
        cardsContainer.getStyleClass().add("dashboard-cards");

        VBox batchCard = createDashboardCard("Batch Tracking", "📋", "Track product batches");
        VBox warehouseCard = createDashboardCard("Warehouse Locations", "🏭", "Manage warehouse inventory");
        VBox movementCard = createDashboardCard("Movement History", "📊", "View product movements");

        cardsContainer.getChildren().addAll(batchCard, warehouseCard, movementCard);

        VBox placeholder = createPlaceholder("Tracking Information", "Product tracking details will be displayed here");

        contentContainer.getChildren().addAll(title, cardsContainer, placeholder);
    }

    private VBox createDashboardCard(String title, String icon, String description) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(250);
        card.setPrefHeight(200);

        VBox iconBox = new VBox();
        iconBox.getStyleClass().add("card-icon-box");
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setPrefWidth(80);
        iconBox.setPrefHeight(80);

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("card-icon");

        iconBox.getChildren().add(iconLabel);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #9ca3af;");
        descLabel.setWrapText(true);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setMaxWidth(220);

        card.getChildren().addAll(iconBox, titleLabel, descLabel);

        return card;
    }

    private VBox createPlaceholder(String title, String subtitle) {
        VBox placeholder = new VBox(20);
        placeholder.getStyleClass().add("table-container");
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setPadding(new Insets(80));
        placeholder.setPrefHeight(350);

        Label placeholderText = new Label(title);
        placeholderText.setStyle("-fx-font-size: 18px; -fx-text-fill: #9ca3af;");

        Label placeholderSubtext = new Label(subtitle);
        placeholderSubtext.setStyle("-fx-font-size: 14px; -fx-text-fill: #d1d5db;");

        placeholder.getChildren().addAll(placeholderText, placeholderSubtext);

        return placeholder;
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        System.out.println("Searching for: " + searchText);
    }

    @FXML
    private void handleNotifications() {
        System.out.println("Show notifications");
    }

    @FXML
    private void handleSettings() {
        System.out.println("Show settings");
    }

    @FXML
    private void handleProfile() {
        System.out.println("Show profile");
    }
}
