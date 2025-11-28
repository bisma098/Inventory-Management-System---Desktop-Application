package ims.model;

public class CustomerReturnLine {
    private int returnId;
    private int quantity;
    private Product product;
    private BatchLot batch;
    private int lineId;
    private double unitPrice;
    private double subTotal;


    public CustomerReturnLine()
    {}
    public CustomerReturnLine(int qty,Product prod)
    {
        quantity=qty;
        product=prod;
    }

    public void setReturnId(int id) { this.returnId = id; }
    public void setLineId(int id) { this.lineId = id; }
    public void setQuantity(int q) { this.quantity = q; }
    public void setUnitPrice(double price) { this.unitPrice = price; }
    public void setSubTotal(double st) { this.subTotal = st; }
    public void setProduct(Product p) { this.product = p; }
    public void setBatch(BatchLot b) { this.batch = b; }

    public Product getProduct() { return product;}
    public int getQuantity() { return quantity;}
    public BatchLot getBatch() { return batch; }
    public double getUnitPrice() { return unitPrice;}
    public double getTotalPrice() { return subTotal;}






}
