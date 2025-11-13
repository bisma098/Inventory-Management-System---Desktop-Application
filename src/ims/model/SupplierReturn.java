package ims.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SupplierReturn {
    private int id;
    private LocalDate returnDate;
    private String reason;
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
}
