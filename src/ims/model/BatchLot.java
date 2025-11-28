package ims.model;

import java.time.LocalDate;

public class BatchLot {
    private int batchId;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
    private int totalQuantity;
    private int availableQuantity;

    private Warehouse warehouse;
    private Product product;

    private int purchaseOrderLineId;              // 🔥 Added for linking
    private PurchaseOrderLine purchaseOrderLine;  // Final object link

    public BatchLot() {}

    public BatchLot(int batchId, LocalDate manufactureDate, LocalDate expiryDate,
                    int totalQuantity, int availableQuantity,
                    Product product, Warehouse warehouse) {

        this.batchId = batchId;
        this.manufactureDate = manufactureDate;
        this.expiryDate = expiryDate;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
        this.product = product;
        this.warehouse = warehouse;
    }

    // Getters & Setters
    public int getBatchId() { return batchId; }
    public void setBatchId(int batchId) { this.batchId = batchId; }

    public LocalDate getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(LocalDate manufactureDate) { this.manufactureDate = manufactureDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public Warehouse getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    // 🔥 NEW
    public int getPurchaseOrderLineId() { return purchaseOrderLineId; }
    public void setPurchaseOrderLineId(int purchaseOrderLineId) { this.purchaseOrderLineId = purchaseOrderLineId; }

    public PurchaseOrderLine getPurchaseOrderLine() { return purchaseOrderLine; }
    public void setPurchaseOrderLine(PurchaseOrderLine purchaseOrderLine) {
        this.purchaseOrderLine = purchaseOrderLine;
    }

    // Functional Methods
    public boolean validateQuantity(int qty) {
        return qty > 0 && qty <= availableQuantity;
    }

    public void updateBatchQuantity(int usedQty) {
        if (validateQuantity(usedQty)) {
            availableQuantity -= usedQty;
            System.out.println("Batch quantity updated. Remaining: " + availableQuantity);
        } else {
            System.out.println("Invalid quantity update. Available: " + availableQuantity);
        }
    }

    public void addQuantity(int qty)
    {
        availableQuantity+=qty;
    }
}
