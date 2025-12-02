package ims.model;

public class SalesOrderLine {
    private int lineId;
    private int quantity;
    private double unitPrice;
    private double subTotal;
    private Product product;
    private BatchLot batch;

    public SalesOrderLine() {}

    public SalesOrderLine(Product prod, int qty, double unitPrice, double subTotal, BatchLot b) {
        product = prod;
        quantity = qty;
        this.unitPrice = unitPrice;
        this.subTotal = subTotal;
        batch = b;
    }

    public void setBatch(BatchLot batch) {
        this.batch = batch;
    }

    // Getters & Setters
    public int getLineId() { return lineId; }
    public void setLineId(int lineId) { this.lineId = lineId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public double getSubTotal() { return subTotal; }
    public void setSubTotal(double subTotal) { this.subTotal = subTotal; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public BatchLot getBatch() { return batch; }
    public Warehouse getWarehouse() { return batch.getWarehouse();}
}
