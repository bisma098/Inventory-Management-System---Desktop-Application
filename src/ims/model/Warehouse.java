package ims.model;

public class Warehouse {
    private int warehouseId;
    private String warehouseName;
    private Address address; // composition

    // ✅ Constructors
    public Warehouse() {
        this.address = new Address();
    }

    public Warehouse(int warehouseId, String warehouseName, Address address) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.address = address != null ? address : new Address();
    }

    // ✅ Getters & Setters
    public int getWarehouseId() { return warehouseId; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    // ✅ Functional Methods
    public void addWarehouse(int id, String name, Address addr) {
        this.warehouseId = id;
        this.warehouseName = name;
        this.address = addr;
        System.out.println("Warehouse added: " + this);
    }

    public void updateWarehouse(String name, Address addr) {
        if (name != null && !name.isEmpty()) this.warehouseName = name;
        if (addr != null) this.address = addr;
        System.out.println("Warehouse updated: " + this);
    }

    public void removeWarehouse() {
        System.out.println("Warehouse removed: " + warehouseName);
        this.warehouseName = null;
        this.address = null;
    }

}
