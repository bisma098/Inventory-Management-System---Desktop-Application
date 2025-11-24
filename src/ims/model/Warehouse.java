package ims.model;

public class Warehouse {
    private int warehouseId;
    private String warehouseName;
    private String address;

    // Constructors
    public Warehouse() {}

    public Warehouse(int warehouseId, String warehouseName, String address) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.address = address;
    }

    // Getters & Setters
    public int getWarehouseId() { return warehouseId; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}