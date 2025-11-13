package ims.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;


public class SalesOrder {
    private int orderId;
    private LocalDate orderDate;
    private Customer customer;
    private List<SalesOrderLine> salesOrders;

    public SalesOrder()
    {
        salesOrders= new ArrayList<>();
    }

    public SalesOrder(LocalDate date,Customer cus)
    {
        salesOrders= new ArrayList<>();
        orderDate=date;
        customer=cus;
    }

    public void addSalesOrder(SalesOrderLine orderLine)
    {
        salesOrders.add(orderLine);
    }
    

}
