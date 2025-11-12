package ims.model;

public class Supplier {
    private int supplierId;
    private String name;
    private String contactInfo;

    public Supplier(int supplierId, String name, String contactInfo) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactInfo = contactInfo;
    }
    
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

}
