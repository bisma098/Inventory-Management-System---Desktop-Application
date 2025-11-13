package ims.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrder {
    private int orderId;
    private LocalDate orderDate;
    private Supplier supplier;   // ✅ association
    //private Staff staff;         // ✅ created by
    private List<PurchaseOrderLine> orderLines;

    // ✅ Constructors
    public PurchaseOrder() {
        this.orderLines = new ArrayList<>();
    }

    public PurchaseOrder(int orderId, LocalDate orderDate, Supplier supplier) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.supplier = supplier;
       // this.staff = staff;
        this.orderLines = new ArrayList<>();
    }

    // ✅ Getters & Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public List<PurchaseOrderLine> getOrderLines() { return orderLines; }

}
