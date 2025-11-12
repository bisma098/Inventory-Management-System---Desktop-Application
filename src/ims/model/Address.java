package ims.model;

public class Address {
    private String address;
    private String area;
    private String city;

    // ✅ Constructors
    public Address() {}

    public Address(String address, String area, String city) {
        this.address = address;
        this.area = area;
        this.city = city;
    }

    // ✅ Getters & Setters
    public String getAddressLine() { return address; }
    public void setAddressLine(String address) { this.address = address; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
