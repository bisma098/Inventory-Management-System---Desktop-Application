package ims.model;

import java.time.LocalDate;

public class PurchaseOrderLine {
    private int lineId;
    private int quantity;
    private double unitPrice;
    private Product product;       // ✅ association
    private Warehouse warehouse;   // ✅ association
    private BatchLot batchLot;     // ✅ link to batch (optional, after goods received)
    private LocalDate manufactureDate;
    private LocalDate expiryDate;


    // ✅ Constructors
    public PurchaseOrderLine() {}

    public PurchaseOrderLine(int lineId, Product product, Warehouse warehouse,
                             int quantity, double unitPrice) {
        this.lineId = lineId;
        this.product = product;
        this.warehouse = warehouse;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // ✅ Getters & Setters
    public int getLineId() { return lineId; }
    public void setLineId(int lineId) { this.lineId = lineId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Warehouse getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public BatchLot getBatchLot() { return batchLot; }
    public void setBatchLot(BatchLot batchLot) { this.batchLot = batchLot; }

    // ✅ Utility / Functional Methods
    public double calculateLineTotal() {
        return quantity * unitPrice;
    }

    public void updateQuantity(int newQty) {
        if (newQty > 0) {
            this.quantity = newQty;
            System.out.println("Quantity updated for Line " + lineId + ": " + newQty);
        }
    }

    public LocalDate getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(LocalDate manufactureDate) { this.manufactureDate = manufactureDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}
