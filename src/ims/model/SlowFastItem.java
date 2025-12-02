package ims.model;

public class SlowFastItem {
    private int productId;
    private String name;
    private String category;
    private int totalSold;
    private int stockRemaining;

    public SlowFastItem(int productId, String name, String category, int totalSold, int stockRemaining) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.totalSold = totalSold;
        this.stockRemaining = stockRemaining;
    }

    // Getters
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getTotalSold() { return totalSold; }
    public int getStockRemaining() { return stockRemaining; }
}
