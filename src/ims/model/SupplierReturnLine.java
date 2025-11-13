package ims.model;

public class SupplierReturnLine {
    private int lineId;
    private int quantity;
    private Product product;
    private BatchLot batch;

    public SupplierReturnLine(){}

    public SupplierReturnLine(Product prod,int qty,BatchLot btch)
    {
        product=prod;
        quantity=qty;
        batch=btch;
    }
}
