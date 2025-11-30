package ims.model;

import java.time.LocalDate;

public class Product {

    private int productId;
    private String name;
    private String SKU;
    private double price;
    private int quantity;
    private LocalDate expiryDate;
    private Category category;

    public Product(int productId, String name, String SKU,
                   double price, int quantity, LocalDate expiryDate, Category category) {
        this.productId = productId;
        this.name = name;
        this.SKU = SKU;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.category = category;
    }

    // Getters & Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSKU() { return SKU; }
    public void setSKU(String SKU) { this.SKU = SKU; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    // Optional convenience methods
    public void addQuantity(int qty) { this.quantity += qty; }
    public void removeQuantity(int qty) { this.quantity -= qty; }
}
