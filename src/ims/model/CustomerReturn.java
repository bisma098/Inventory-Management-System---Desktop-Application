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

    
}
