package ims.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SupplierReturn {
    private int id;
    private LocalDate returnDate;
    private String reason;
    private double totalAmount;
    private Supplier supplier;
    private List<SupplierReturnLine> returnLines;

    public SupplierReturn()
    {
        this.returnLines = new ArrayList<>();
    }

    public SupplierReturn(LocalDate date,String r,Supplier sup)
    {
        returnDate = date;
        supplier = sup;
        this.returnLines = new ArrayList<>();
    }
    public void addReturnLine(SupplierReturnLine returnLine)
    {
        returnLines.add(returnLine);
    }
    public void setId(int id)
    {
        this.id=id;
    }
    public void setReturnDate(LocalDate date)
    {
        this.returnDate=date;
    }
    public void setReason(String reason)
    {
        this.reason=reason;
    }
    public void setTotalAmount(double amount)
    {
        this.totalAmount=amount;
    }
    public void setSupplier(Supplier sup)
    {
        this.supplier=sup;
    }
    public int getId()
    {
        return id;
    }

    public LocalDate getReturnDate() { return returnDate; }
    public String getReason() { return reason; }
    public double getTotalAmount() { return totalAmount; }
    public Supplier getSupplier() { return supplier; }
    public List<SupplierReturnLine> getReturnLines() { return returnLines; }

}
