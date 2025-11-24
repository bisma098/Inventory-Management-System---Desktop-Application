package ims.model;

public class SupplierReturnLine {

    private int lineId;
    private int supplierReturnId; // FK
    private int quantity;
    private Product product;
    private BatchLot batch;
    private double unitPrice;
    private double totalPrice;

    public SupplierReturnLine() {}

    public SupplierReturnLine(Product product, int qty, BatchLot batch) {
        this.product = product;
        this.quantity = qty;
        this.batch = batch;
        this.unitPrice = product.getPrice();
        this.totalPrice = qty * unitPrice;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public int getSupplierReturnId() {
        return supplierReturnId;
    }

    public void setSupplierReturnId(int supplierReturnId) {
        this.supplierReturnId = supplierReturnId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = this.unitPrice * quantity; // auto-update
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.unitPrice = product.getPrice();
            this.totalPrice = this.unitPrice * this.quantity;
        }
    }

    public BatchLot getBatch() {
        return batch;
    }

    public void setBatch(BatchLot batch) {
        this.batch = batch;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice * this.quantity; // auto-update
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
