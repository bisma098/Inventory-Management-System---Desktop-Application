package ims.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class SalesOrder {
    private int orderId;
    private LocalDate orderDate;
    private Customer customer;
    private double totalPrice;
    private List<SalesOrderLine> salesOrders;

    public SalesOrder() {
        salesOrders = new ArrayList<>();
    }

    public SalesOrder(LocalDate date, Customer cus, double totalPrice) {
        salesOrders = new ArrayList<>();
        orderDate = date;
        customer = cus;
        this.totalPrice = totalPrice;
    }

    public void addSalesOrder(SalesOrderLine orderLine) {
        salesOrders.add(orderLine);
    }

    // Getters & Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public List<SalesOrderLine> getOrderLines() { return salesOrders; }
}
