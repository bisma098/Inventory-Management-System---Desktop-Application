package ims.model;

public class SalesStockItem {
    private int productId;
    private String name;
    private String category;
    private int totalStock;
    private int totalSold;
    private int stockRemaining;

    public SalesStockItem(int productId, String name, String category, int totalStock, int totalSold, int stockRemaining) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.totalStock = totalStock;
        this.totalSold = totalSold;
        this.stockRemaining = stockRemaining;
    }

    // Getters
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getTotalStock() { return totalStock; }
    public int getTotalSold() { return totalSold; }
    public int getStockRemaining() { return stockRemaining; }
}
