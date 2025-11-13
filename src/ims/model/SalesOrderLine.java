package ims.model;

public class SalesOrderLine {
    private int lineId;
    private int quantity;
    private Product product;
    private BatchLot batch;

    public SalesOrderLine(){}
    
    public SalesOrderLine(Product prod,int qty,BatchLot b)
    {
        product=prod;
        quantity=qty;
        batch=b;
    }
    
    public void setBatch(BatchLot batch) {
        this.batch = batch;
    }

}
