package ims.model;

import java.time.LocalDateTime;

public class Notification {

    private int id;
    private Product product;
    private String message;
    private String type; // LOW_STOCK, OUT_OF_STOCK
    private LocalDateTime timestamp;

    public Notification() {}

    public Notification(Product product, String type, String message) {
        this.product = product;
        this.type = type;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
