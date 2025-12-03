package ims.model;

public class Stockitem {
    private int productId;
    private String name;
    private String category;
    private int quantity;

    public Stockitem(int productId, String name, String category, int quantity) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
    }

    // Getters
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
}
