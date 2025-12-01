package ims.model;

public class SupplierPerformanceItem {
    private int supplierId;
    private String supplierName;
    private int totalOrders;
    private double totalSupplied;
    private double totalReturned;
    private double netSupplied;

    public SupplierPerformanceItem(int supplierId, String supplierName, int totalOrders,
                                   double totalSupplied, double totalReturned, double netSupplied) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.totalOrders = totalOrders;
        this.totalSupplied = totalSupplied;
        this.totalReturned = totalReturned;
        this.netSupplied = netSupplied;
    }

    // Getters
    public int getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public int getTotalOrders() { return totalOrders; }
    public double getTotalSupplied() { return totalSupplied; }
    public double getTotalReturned() { return totalReturned; }
    public double getNetSupplied() { return netSupplied; }
}
