package ims.controller;

import ims.model.*;
import ims.database.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class DataController {
    private List<Product> products;
    private List<Category> categories;
    private ProductCatalogue productCatalogue;
    
    private static DataController instance;
    
    private DataController() {
        this.products = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.productCatalogue = new ProductCatalogue();
        loadAllData();
    }
    
    public static DataController getInstance() {
        if (instance == null) {
            instance = new DataController();
        }
        return instance;
    }
    
    public void loadAllData() {
        loadCategories();
        loadProducts();
    }
    
    private void loadCategories() {
        String query = "SELECT * FROM categories";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            categories.clear();
            while (rs.next()) {
                Category category = new Category(
                    rs.getInt("category_id"),
                    rs.getString("category_name")
                );
                categories.add(category);
            }
            System.out.println("Loaded " + categories.size() + " categories");
        } catch (SQLException e) {
            System.err.println("Error loading categories: " + e.getMessage());
        }
    }
    
    private void loadProducts() {
        // Get a fresh connection for products
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT p.*, c.category_name FROM products p " +
                          "LEFT JOIN categories c ON p.category_id = c.category_id";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                products.clear();
                productCatalogue = new ProductCatalogue();
                
                while (rs.next()) {
                    // Find category
                    Category category = null;
                    for (Category cat : categories) {
                        if (cat.getId() == rs.getInt("category_id")) {
                            category = cat;
                            break;
                        }
                    }
                    
                    // Handle null expiry date
                    java.sql.Date sqlDate = rs.getDate("expiry_date");
                    java.time.LocalDate expiryDate = null;
                    if (sqlDate != null) {
                        expiryDate = sqlDate.toLocalDate();
                    }
                    
                    Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("sku"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        expiryDate,
                        category
                    );
                    
                    products.add(product);
                    productCatalogue.addProduct(product);
                }
                System.out.println("Loaded " + products.size() + " products into catalogue");
            }
        } catch (SQLException e) {
            System.err.println("Error loading products: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Getters
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }
    
    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }
    
    public ProductCatalogue getProductCatalogue() {
        return productCatalogue;
    }
}