package ims.model;

import java.time.LocalDate;

public class Product {
    private int productId;
    private String name;
    private String SKU;
    private double price;
    private LocalDate expiryDate;
    private int supplierId;

    public Product(int productId, String name, String SKU, double price, LocalDate expiryDate, int supplierId) {
        this.productId = productId;
        this.name = name;
        this.SKU = SKU;
        this.price = price;
        this.expiryDate = expiryDate;
        this.supplierId = supplierId;
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSKU() { return SKU; }
    public void setSKU(String SKU) { this.SKU = SKU; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    // Methods
    public void addProduct() {
        System.out.println("Product added: " + name);
    }

    public void updateProduct() {
        System.out.println("Product updated: " + name);
    }

    public void deleteProduct() {
        System.out.println("Product deleted: " + name);
    }

    public void searchProduct(String query) {
        System.out.println("Searching product: " + query);
    }

    public double getUnitPrice() {
        return price;
    }
}
