package ims.model;

public class CustomerReturnLine {
    private int returnId;
    private int quantity;
    private Product product;

    public CustomerReturnLine()
    {}

    public CustomerReturnLine(int qty,Product prod)
    {
        quantity=qty;
        product=prod;
    }

}
