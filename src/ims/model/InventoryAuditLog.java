package ims.model;

import java.time.LocalDateTime;

public class InventoryAuditLog {
    private int logId;
    private int userId;
    private int productId;
    private String description;
    private LocalDateTime timestamp;
    
    // Constructors
    public InventoryAuditLog() {}
    
    public InventoryAuditLog(int userId, int productId, String description) {
        this.userId = userId;
        this.productId = productId;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}