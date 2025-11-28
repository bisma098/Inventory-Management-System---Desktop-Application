package ims.model;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class CustomerReturn {
    private int returnId;
    private LocalDate returnDate;
    private String reason;
    private Customer customer;
    private List<CustomerReturnLine> returnLines;
    private double totalAmount;

    public CustomerReturn()
    {
        returnLines=new ArrayList<>();
    }
    public CustomerReturn(LocalDate date,String r,Customer cus)
    {
        returnLines=new ArrayList<>();
        returnDate=date;
        reason=r;
        customer=cus;
    }
    public void addReturnLine(CustomerReturnLine returnLine)
    {
        returnLines.add(returnLine);
    }
    public void setReturnId(int id) { this.returnId = id; }
public int getReturnId() { return returnId; }

public void setReturnDate(LocalDate d) { this.returnDate = d; }
public LocalDate getReturnDate() { return returnDate; }

public void setReason(String r) { this.reason = r; }
public String getReason() { return reason; }

public void setCustomer(Customer c) { this.customer = c; }
public Customer getCustomer() { return customer; }

public void setTotalAmount(double total) { this.totalAmount = total; }
public double getTotalAmount() { return totalAmount; }

public List<CustomerReturnLine> getReturnLines() { return returnLines; }


    
}
