package ims.controller;

import ims.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import ims.database.DatabaseConnection;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.sql.Date;  // For SQL Date operations
import java.time.LocalDate;

public class DataController {
    private List<User> users;
    private User currentUser;
    private List<Product> products;
    private List<Category> categories;
    private ProductCatalogue productCatalogue;

    private List<Supplier> suppliers;
    private List<Customer> customers;
    private List<PurchaseOrder> purchaseOrders;
    private List<PurchaseOrderLine> allPurchaseOrderLines;
    private List<SupplierReturn> supplierReturns;
    private List<SupplierReturnLine> allSupplierReturnLines;
    private List<SalesOrder> salesOrders;
    private List<SalesOrderLine> allSalesOrderLines;
    private List<CustomerReturn> customerReturns;
    private List<CustomerReturnLine> allCustomerReturnLines;
    private List<Warehouse> warehouses;
    private List<BatchLot> batchLots;
    private List<InventoryAuditLog> inventoryAuditLogs;
    private List<UserActivityLog> userActivityLogs;
    private List<Notification> notifications;
    
    private static DataController instance;
    
    private DataController() {    // private constructor prevents new DataController()
        this.users = new ArrayList<>();
        this.products = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.productCatalogue = new ProductCatalogue();
        this.suppliers = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.purchaseOrders = new ArrayList<>();
        this.allPurchaseOrderLines = new ArrayList<>();
        this.salesOrders = new ArrayList<>();
        this.allSalesOrderLines = new ArrayList<>();
        this.supplierReturns =new ArrayList<>();
        this.allSupplierReturnLines=new ArrayList<>();
        this.customerReturns=new ArrayList<>();
        this.allCustomerReturnLines=new ArrayList<>();
        this.warehouses = new ArrayList<>();
        this.batchLots= new ArrayList<>();
        this.inventoryAuditLogs=new ArrayList<>();
        this.userActivityLogs=new ArrayList<>();
        this.notifications=new ArrayList<>();

        loadAllData();
    }
    
    public static DataController getInstance() {
        if (instance == null) {
            instance = new DataController();
        }
        return instance;
    }
    
public void loadAllData() {
    loadUsers();
    loadCategories();
    loadProducts();
    loadSuppliers();
    loadCustomers();
    loadWarehouses();
    loadBatchLots(); 
    loadPurchaseOrders();
    loadSupplierReturns();
    loadSalesOrders();
    loadCustomerReturns();
    loadAllUserActivityLogs();
    loadAllInventoryAuditLogs();
    loadNotifications();

}

private void loadUsers() {
    String query = "SELECT * FROM Users";
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        
        users.clear();
        while (rs.next()) {
            User user = new User(
    rs.getInt("userId"),
    rs.getString("userName"),
    rs.getString("password"),  
    rs.getString("role")
);

            users.add(user);
        }
        System.out.println("Loaded " + users.size() + " users");
    } catch (SQLException e) {
        System.err.println("Error loading users: " + e.getMessage());
    }
}   

// Add this helper method to find user by credentials
public User findUserByCredentials(String username, String password, String role) {
    
    for (User user : users) {
        boolean usernameMatch = user.getUserName().equals(username);
        boolean passwordMatch = user.getPassword().equals(password);
        boolean roleMatch = user.getRole().equals(role);
        
        
        if (usernameMatch && passwordMatch && roleMatch) {
            return user;
        }
    }
    return null;
}  
    // Add getter for users
public List<User> getUsers() {
    return new ArrayList<>(users);
}
public void addUser(User user) {
    if (user != null) {
        users.add(user);
    }
}

    
public void setCurrentUser(User user)
{
    this.currentUser=user;
}
private void loadCategories() {
    categories.clear();
        String query = "SELECT * FROM categories";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            categories.clear();
            while (rs.next()) {
                Category category = new Category(
                    rs.getInt("category_id"),
                    rs.getString("category_name")
                );
                categories.add(category);
            }
            System.out.println("Loaded " + categories.size() + " categories");
        } catch (SQLException e) {
            System.err.println("Error loading categories: " + e.getMessage());
        }
    }
    
public void loadProducts() {
        // Get a fresh connection for products
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT p.*, c.category_name FROM products p " +
                          "LEFT JOIN categories c ON p.category_id = c.category_id";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                products.clear();
                productCatalogue = new ProductCatalogue();
                
                while (rs.next()) {
                    // Find category
                    Category category = null;
                    for (Category cat : categories) {
                        if (cat.getId() == rs.getInt("category_id")) {
                            category = cat;
                            break;
                        }
                    }
                    
                    Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("sku"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        category
                    );
                    
                    products.add(product);
                    productCatalogue.addProduct(product);
                }
                System.out.println("Loaded " + products.size() + " products into catalogue");
            }
        } catch (SQLException e) {
            System.err.println("Error loading products: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
private void loadSuppliers() {
    suppliers.clear();
        String sql = "SELECT supplierId, name, contactInfo FROM Supplier";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Supplier supplier = new Supplier(
                    rs.getInt("supplierId"),
                    rs.getString("name"),
                    rs.getString("contactInfo")
                );
                suppliers.add(supplier);
            }
            System.out.println("Loaded " + suppliers.size() + " suppliers");
            
        } catch (SQLException e) {
            System.err.println("Error loading suppliers: " + e.getMessage());
        }
    }

public void loadCustomers() {
    customers.clear();

    String sql = "SELECT id, name, contactInfo FROM Customer";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Customer c = new Customer(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("contactInfo")
            );
            customers.add(c);
        }

        System.out.println("Loaded " + customers.size() + " customers.");

    } catch (Exception e) {
        e.printStackTrace();
    }
}

private void loadWarehouses() {
    warehouses.clear();
    String sql = "SELECT warehouseId, warehouseName, address FROM Warehouse";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        
        while (rs.next()) {
            Warehouse warehouse = new Warehouse(
                rs.getInt("warehouseId"),
                rs.getString("warehouseName"),
                rs.getString("address")
            );
            warehouses.add(warehouse);
        }
        System.out.println("Loaded " + warehouses.size() + " warehouses");
        
    } catch (SQLException e) {
        System.err.println("Error loading warehouses: " + e.getMessage());
    }
}

public void loadBatchLots() {
    batchLots.clear();

    String sql = """
        SELECT batchId, manufactureDate, expiryDate,
               totalQuantity, availableQty,
               productId, warehouseId, purchaseOrderLineId
        FROM BatchLot
    """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            java.sql.Date manufDate = rs.getDate("manufactureDate");
            java.sql.Date expDate = rs.getDate("expiryDate");
            
            LocalDate manufactureDate = (manufDate != null) ? manufDate.toLocalDate() : null;
            LocalDate expiryDate = (expDate != null) ? expDate.toLocalDate() : null;

            BatchLot b = new BatchLot(
                    rs.getInt("batchId"),
                    manufactureDate,  
                    expiryDate,       
                    rs.getInt("totalQuantity"),
                    rs.getInt("availableQty"),
                    findProductById(rs.getInt("productId")),
                    findWarehouseById(rs.getInt("warehouseId"))
            );

            b.setPurchaseOrderLineId(rs.getInt("purchaseOrderLineId"));
            batchLots.add(b);
        }
        System.out.println("Loaded " + batchLots.size() + " batch lots.");
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void loadPurchaseOrders() {
    purchaseOrders.clear();

    String sql = "SELECT orderId, supplierId, orderDate FROM PurchaseOrder";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            PurchaseOrder po = new PurchaseOrder(
                    rs.getInt("orderId"),
                    rs.getDate("orderDate").toLocalDate(),
                    findSupplierById(rs.getInt("supplierId"))
            );

            po.getOrderLines().addAll(loadOrderLines(po.getOrderId()));
            purchaseOrders.add(po);
        }

        System.out.println("Loaded " + purchaseOrders.size() + " purchase orders.");

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void loadSupplierReturns() {
    supplierReturns.clear();
    
    String sql = "SELECT id, returnDate, reason, totalAmount, supplierId FROM SupplierReturn";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        
        while (rs.next()) {
            SupplierReturn sr = new SupplierReturn();
            sr.setId(rs.getInt("id"));
            sr.setReturnDate(rs.getDate("returnDate").toLocalDate());
            sr.setReason(rs.getString("reason"));
            sr.setTotalAmount(rs.getDouble("totalAmount"));
            
            // Load supplier
            Supplier supplier = findSupplierById(rs.getInt("supplierId"));
            sr.setSupplier(supplier);
            
            // Load return lines
            List<SupplierReturnLine> lines = loadSupplierReturnLines(sr.getId());
            for (SupplierReturnLine line : lines) {
                sr.addReturnLine(line);
            }
            
            supplierReturns.add(sr);
             System.out.println("Loaded " + supplierReturns.size() + " purchase orders.");
        }
        
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void loadSalesOrders() {
    salesOrders.clear();

    String sql = "SELECT orderId, customerId, orderDate, totalAmount FROM SalesOrder";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Customer cus = findCustomerById(rs.getInt("customerId"));

            SalesOrder so = new SalesOrder(
                rs.getDate("orderDate").toLocalDate(),
                cus,
                rs.getDouble("totalAmount")
            );
            so.setOrderId(rs.getInt("orderId"));

            so.getOrderLines().addAll(loadSalesOrderLines(so.getOrderId()));
            salesOrders.add(so);
        }

        System.out.println("Loaded " + salesOrders.size() + " sales orders.");

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void loadCustomerReturns() {
    customerReturns.clear();

    String sql = """
        SELECT customerReturnId, customerId, date, reason, totalAmount 
        FROM CustomerReturn
    """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            CustomerReturn cr = new CustomerReturn();
            cr.setReturnId(rs.getInt("customerReturnId"));
            cr.setReturnDate(rs.getDate("date").toLocalDate());
            cr.setReason(rs.getString("reason"));
            cr.setTotalAmount(rs.getDouble("totalAmount"));

            // Load customer
            Customer c = findCustomerById(rs.getInt("customerId"));
            cr.setCustomer(c);

            // Load return lines
            List<CustomerReturnLine> lines = loadCustomerReturnLines(cr.getReturnId());
            for (CustomerReturnLine line : lines) {
                cr.addReturnLine(line);
            }

            customerReturns.add(cr);
        }

        System.out.println("Loaded " + customerReturns.size() + " customer returns.");

    } catch (Exception e) {
        e.printStackTrace();
    }
}


public List<PurchaseOrderLine> loadOrderLines(int orderId) {
    String sql = """
        SELECT orderLineId, productId, warehouseId, quantity, unitPrice
        FROM PurchaseOrderLine
        WHERE orderId = ?
    """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, orderId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int lineId = rs.getInt("orderLineId");

            PurchaseOrderLine line = new PurchaseOrderLine(
                    lineId,
                    findProductById(rs.getInt("productId")),
                    findWarehouseById(rs.getInt("warehouseId")),
                    rs.getInt("quantity"),
                    rs.getDouble("unitPrice")
            );

            BatchLot batch = findBatchByPurchaseOrderLineId(lineId);
            if (batch != null) {
                line.setBatchLot(batch);
                batch.setPurchaseOrderLine(line); 
            }

            allPurchaseOrderLines.add(line);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return allPurchaseOrderLines;
}

public List<SupplierReturnLine> loadSupplierReturnLines(int supplierReturnId) {
    allSupplierReturnLines.clear();
    String sql = """
        SELECT srl.lineId, srl.productId, srl.quantity, srl.unitPrice, srl.totalPrice, srl.batchId
        FROM SupplierReturnLine srl
        WHERE srl.supplierReturnId = ?
    """;
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setInt(1, supplierReturnId);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            SupplierReturnLine line = new SupplierReturnLine();
            line.setLineId(rs.getInt("lineId"));
            line.setSupplierReturnId(supplierReturnId);
            line.setQuantity(rs.getInt("quantity"));
            line.setUnitPrice(rs.getDouble("unitPrice"));
            line.setTotalPrice(rs.getDouble("totalPrice"));
            
            // Load product
            Product product = findProductById(rs.getInt("productId"));
            line.setProduct(product);
            
            // Load batch if exists
            int batchId = rs.getInt("batchId");
            if (!rs.wasNull()) {
                BatchLot batch = findBatchById(batchId);
                line.setBatch(batch);
            }
            
            allSupplierReturnLines.add(line);
        }
        
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    return allSupplierReturnLines;
}

public List<SalesOrderLine> loadSalesOrderLines(int orderId) {
    allSalesOrderLines.clear();

    String sql = """
        SELECT lineId, productId, quantity, unitPrice, subTotal, batchId
        FROM SalesOrderLine
        WHERE salesOrderId = ?
    """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, orderId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Product product = findProductById(rs.getInt("productId"));
            BatchLot batch = findBatchById(rs.getInt("batchId"));

            SalesOrderLine line = new SalesOrderLine(
                product,
                rs.getInt("quantity"),
                rs.getDouble("unitPrice"),
                rs.getDouble("subTotal"),
                batch
            );
            line.setLineId(rs.getInt("lineId"));

            allSalesOrderLines.add(line);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return allSalesOrderLines;
}

public List<CustomerReturnLine> loadCustomerReturnLines(int returnId) {
    allCustomerReturnLines.clear();
    List<CustomerReturnLine> lines = new ArrayList<>();

    String sql = """
        SELECT lineId, productId, batchId, quantity, unitPrice, subTotal
        FROM CustomerReturnLine
        WHERE customerReturnId = ?
    """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, returnId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            CustomerReturnLine line = new CustomerReturnLine();
            line.setReturnId(returnId);
            line.setLineId(rs.getInt("lineId"));
            line.setQuantity(rs.getInt("quantity"));
            line.setUnitPrice(rs.getDouble("unitPrice"));
            line.setSubTotal(rs.getDouble("subTotal"));

            // Load product
            Product p = findProductById(rs.getInt("productId"));
            line.setProduct(p);

            // Load batch (optional)
            int batchId = rs.getInt("batchId");
            if (!rs.wasNull()) {
                BatchLot batch = findBatchById(batchId);
                line.setBatch(batch);
            }

            lines.add(line);
            allCustomerReturnLines.add(line);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return lines;
}

private void loadNotifications() {
    String query = "SELECT n.id, n.productId, n.type, n.message, n.timestamp, " +
                   "p.product_name, p.price " +
                   "FROM Notifications n " +
                   "JOIN Products p ON n.productId = p.product_id";

    notifications.clear();

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        while (rs.next()) {
            
            // Build Product (for reference inside Notification)
            Product product = findProductById(rs.getInt("productId"));

            Notification n = new Notification();
            n.setId(rs.getInt("id"));
            n.setProduct(product);
            n.setType(rs.getString("type"));
            n.setMessage(rs.getString("message"));
            n.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());

            notifications.add(n);
        }

        System.out.println("Loaded " + notifications.size() + " notifications");

    } catch (Exception e) {
        System.err.println("Error loading notifications: " + e.getMessage());
    }
}

public void loadAllUserActivityLogs() {
    userActivityLogs.clear();
    String query = "SELECT logId, userId, userAction, timestamp FROM UserActivityLog ORDER BY logId DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            UserActivityLog log = new UserActivityLog();
            log.setLogId(rs.getInt("logId"));
            log.setUserId(rs.getInt("userId"));
            log.setUserAction(rs.getString("userAction"));
            log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
            userActivityLogs.add(log);
        }

    } catch (SQLException e) {
        System.err.println("Error loading user activity logs: " + e.getMessage());
    }

}

public void loadAllInventoryAuditLogs() {
    inventoryAuditLogs.clear();
    String query = "SELECT logId, userId, productId, description, timestamp FROM InventoryAuditLogs ORDER BY logId DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            InventoryAuditLog log = new InventoryAuditLog();
            log.setLogId(rs.getInt("logId"));
            log.setUserId(rs.getInt("userId"));
            log.setProductId(rs.getInt("productId"));
            log.setDescription(rs.getString("description"));
            log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
            inventoryAuditLogs.add(log);
        }

    } catch (SQLException e) {
        System.err.println("Error loading inventory audit logs: " + e.getMessage());
    }

}


private BatchLot findBatchByPurchaseOrderLineId(int purchaseOrderLineId) {
    for (BatchLot batch : batchLots) {
        if (batch.getPurchaseOrderLineId() == purchaseOrderLineId) {
            return batch;
        }
    }
    return null;
}

public boolean savePurchaseOrder(PurchaseOrder order) {
    String orderSql = "INSERT INTO PurchaseOrder (supplierId, orderDate) VALUES (?, ?)";
    String lineSql = "INSERT INTO PurchaseOrderLine (orderId, productId, warehouseId, quantity, unitPrice, linePrice) VALUES (?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
         PreparedStatement lineStmt = conn.prepareStatement(lineSql, Statement.RETURN_GENERATED_KEYS)) {
        
        conn.setAutoCommit(false);
        
        // 1. Save main order
        orderStmt.setInt(1, order.getSupplier().getSupplierId());
        orderStmt.setDate(2, java.sql.Date.valueOf(order.getOrderDate()));
        orderStmt.executeUpdate();
        
        // Get generated order ID
        ResultSet orderRs = orderStmt.getGeneratedKeys();
        if (orderRs.next()) {
            order.setOrderId(orderRs.getInt(1));
        }
        
        // 2. Save order lines
        for (PurchaseOrderLine line : order.getOrderLines()) {
            lineStmt.setInt(1, order.getOrderId());
            lineStmt.setInt(2, line.getProduct().getProductId());
            lineStmt.setInt(3, line.getWarehouse().getWarehouseId());
            lineStmt.setInt(4, line.getQuantity());
            lineStmt.setDouble(5, line.getUnitPrice());
            lineStmt.setDouble(6, line.calculateLineTotal());
            lineStmt.executeUpdate();
            
            // Get generated line ID
            ResultSet lineRs = lineStmt.getGeneratedKeys();
            if (lineRs.next()) {
                line.setLineId(lineRs.getInt(1)); // ✅ This sets the actual database ID
            }
        }
        
        conn.commit();
        return true;
        
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public boolean updatePurchaseOrderLineWithBatchId(int orderLineId, int batchId) {
    String sql = "UPDATE PurchaseOrderLine SET batchId = ? WHERE orderLineId = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, batchId);
        stmt.setInt(2, orderLineId);
        
        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


public Product findProductById(int productId) {
    return products.stream()
            .filter(p -> p.getProductId() == productId)
            .findFirst()
            .orElse(null);
}

public Supplier findSupplierById(int supplierId) {
    return suppliers.stream()
            .filter(s -> s.getSupplierId() == supplierId)
            .findFirst()
            .orElse(null);
}

public Customer findCustomerById(int customerId) {
    for (Customer c : customers) {
        if (c.getId() == customerId) {
            return c;
        }
    }
    return null; // or throw exception if preferred
}


public Warehouse findWarehouseById(int warehouseId) {
    return warehouses.stream()
            .filter(w -> w.getWarehouseId() == warehouseId)
            .findFirst()
            .orElse(null);
}


public PurchaseOrder findPurchaseOrderById(int orderId) {
    return purchaseOrders.stream()
            .filter(po -> po.getOrderId() == orderId)
            .findFirst()
            .orElse(null);
}

public PurchaseOrderLine findPurchaseOrderLineById(int lineId) {
    return allPurchaseOrderLines.stream()
            .filter(pol -> pol.getLineId() == lineId)
            .findFirst()
            .orElse(null);
}


private BatchLot findBatchById(int batchId) 
{
    for (BatchLot batch : batchLots) {
        if (batch.getBatchId() == batchId) {
            return batch;
        }
    }
    return null;
}

public List<BatchLot> getBatchesByProductId(int productId) {
    List<BatchLot> result = new ArrayList<>();
    for (BatchLot batch : batchLots) {      // <-- your internal list
        if (batch.getProduct() != null && batch.getProduct().getProductId() == productId && batch.getAvailableQuantity()>0) {
            result.add(batch);
        }
    }
    return result;
}

public List<BatchLot> getBatchListByWarehouse(int warehouseId) {
    return batchLots.stream()
        .filter(batch -> batch.getWarehouse().getWarehouseId() == warehouseId)
        .collect(Collectors.toList());
}

public List<SalesOrder> getSalesOrdersByCustomer(int customerId) {
    List<SalesOrder> result = new ArrayList<>();
    for (SalesOrder order : salesOrders) {
        if (order.getCustomer().getId() == customerId) {
            result.add(order);
        }
    }
    return result;
}

// Getters
public List<Category> getAllCategories() {
   return new ArrayList<>(categories);
}
    
public List<Supplier> getSuppliers() {
    return new ArrayList<>(suppliers);
}

public User getCurrentUser() {
        return currentUser;
    }

public List<Customer> getCustomers() {
    return new ArrayList<>(customers);
}

public List<BatchLot> getBatchLots(){
    return new ArrayList<>(batchLots);
}

public List<Product> getProducts() {
    return new ArrayList<>(products);
}

public List<Warehouse> getWarehouses() {
    return new ArrayList<>(warehouses);
}

public List<PurchaseOrder> getPurchaseOrders() {
    return new ArrayList<>(purchaseOrders);
}

public List<SalesOrder> getSalesOrders() {
    return new ArrayList<>(salesOrders);
}

public List<PurchaseOrderLine> getAllPurchaseOrderLines() {
    return new ArrayList<>(allPurchaseOrderLines);
}

public ObservableList<SupplierReturn> getSupplierReturns() {
    return FXCollections.observableArrayList(supplierReturns);
}

public List<Category> getCategories() {
    return new ArrayList<>(categories);
}

public List<CustomerReturn> getCustomerReturns()
{
    return customerReturns;
}

public List<Notification> getNotifications() {
    return notifications;
}

public List<UserActivityLog> getUserActivityLogs() {
   return userActivityLogs;
}

public List<InventoryAuditLog> getInventoryAuditLogs() {
   return inventoryAuditLogs;
}

public List<Product> getAllProducts(){
    return products;
}


public void addPurchaseOrder(PurchaseOrder po)
{
    purchaseOrders.add(po);
}
public void addBatch(BatchLot batch)
{
    batchLots.add(batch);
}

public void addSalesOrder(SalesOrder order) {
        salesOrders.add(order);
}

public void addCustomer(Customer customer) {
    customers.add(customer);
}
public void addSupplierToList(Supplier supplier) {
    suppliers.add(supplier);
}

public void addCategoryToList(Category category) {
    categories.add(category);
}


public void addSupplierReturn(SupplierReturn supplierReturn) {
    if (!supplierReturns.contains(supplierReturn)) {
        supplierReturns.add(supplierReturn);
    }
}

public void addCustomerReturn(CustomerReturn Return) {
    if (!customerReturns.contains(Return)) {
        customerReturns.add(Return);
    }
}

public boolean saveBatch(BatchLot batch) {
    String sql = "INSERT INTO BatchLot (manufactureDate, expiryDate, totalQuantity, availableQty, " +
                 "purchaseOrderLineId, productId, warehouseId) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)";

    Connection conn = null;

    try {
        conn = DatabaseConnection.getConnection();

        // IMPORTANT: Request generated keys
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // manufactureDate
        if (batch.getManufactureDate() != null)
            stmt.setDate(1, java.sql.Date.valueOf(batch.getManufactureDate()));
        else
            stmt.setNull(1, java.sql.Types.DATE);

        // expiryDate
        if (batch.getExpiryDate() != null)
            stmt.setDate(2, java.sql.Date.valueOf(batch.getExpiryDate()));
        else
            stmt.setNull(2, java.sql.Types.DATE);

        stmt.setInt(3, batch.getTotalQuantity());
        stmt.setInt(4, batch.getAvailableQuantity());

        if (batch.getPurchaseOrderLineId() > 0)
            stmt.setInt(5, batch.getPurchaseOrderLineId());
        else
            stmt.setNull(5, java.sql.Types.INTEGER);

        stmt.setInt(6, batch.getProduct().getProductId());
        stmt.setInt(7, batch.getWarehouse().getWarehouseId());

        stmt.executeUpdate();

        // 🔥 Retrieve generated batchId
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            batch.setBatchId(rs.getInt(1));
        }

        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;

    } finally {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public boolean saveSalesOrder(SalesOrder order) {

    String orderSql = "INSERT INTO SalesOrder (customerId, orderDate, totalAmount) VALUES (?, ?, ?)";
    String lineSql = "INSERT INTO SalesOrderLine (salesOrderId, productId, batchId, quantity, unitPrice, subTotal) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection()) {

        conn.setAutoCommit(false);

        // Insert SalesOrder
        try (PreparedStatement ps = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getCustomer().getId());
            ps.setDate(2, java.sql.Date.valueOf(order.getOrderDate()));
            ps.setDouble(3, order.getTotalPrice());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) order.setOrderId(rs.getInt(1));
        }

        // Insert SalesOrderLine items
        try (PreparedStatement ps = conn.prepareStatement(lineSql, Statement.RETURN_GENERATED_KEYS)) {

            for (SalesOrderLine line : order.getOrderLines()) {

                ps.setInt(1, order.getOrderId());
                ps.setInt(2, line.getProduct().getProductId());
                ps.setInt(3, line.getBatch().getBatchId());
                ps.setInt(4, line.getQuantity());
                ps.setDouble(5, line.getUnitPrice());
                ps.setDouble(6, line.getSubTotal());

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) line.setLineId(rs.getInt(1));
            }
        }

        conn.commit();
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


public void updateProductQuantity(Product prod,int qty)
{
     String sql = "UPDATE products SET quantity = quantity + ? WHERE product_id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, qty);
        stmt.setInt(2, prod.getProductId());

        stmt.executeUpdate();
        evaluateStockNotification(prod);//****/
        } 
    catch (Exception e) {
        e.printStackTrace();
    }
}

public boolean saveCustomer(Customer customer) {
    String sql = "INSERT INTO Customer (name, contactInfo) VALUES (?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setString(1, customer.getName());
        ps.setString(2, customer.getContactInfo());

        int rows = ps.executeUpdate();
        if (rows == 0) return false;

        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) {
            customer.setId(keys.getInt(1));
        }

        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public boolean saveSupplierReturn(SupplierReturn returnObj) {
    String returnSql = "INSERT INTO SupplierReturn (returnDate, reason, totalAmount, supplierId) VALUES (?, ?, ?, ?)";
    String lineSql = "INSERT INTO SupplierReturnLine (supplierReturnId, productId, quantity, unitPrice, totalPrice, batchId) VALUES (?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement returnStmt = conn.prepareStatement(returnSql, Statement.RETURN_GENERATED_KEYS);
         PreparedStatement lineStmt = conn.prepareStatement(lineSql)) {
        
        conn.setAutoCommit(false);
        
        // 1. Save main return
        returnStmt.setDate(1, java.sql.Date.valueOf(returnObj.getReturnDate()));
        returnStmt.setString(2, returnObj.getReason());
        returnStmt.setDouble(3, returnObj.getTotalAmount());
        returnStmt.setInt(4, returnObj.getSupplier().getSupplierId());
        returnStmt.executeUpdate();
        
        // Get generated return ID
        ResultSet returnRs = returnStmt.getGeneratedKeys();
        if (returnRs.next()) {
            returnObj.setId(returnRs.getInt(1));
        }
        
        // 2. Save return lines
        for (SupplierReturnLine line : returnObj.getReturnLines()) {
            lineStmt.setInt(1, returnObj.getId());
            lineStmt.setInt(2, line.getProduct().getProductId());
            lineStmt.setInt(3, line.getQuantity());
            lineStmt.setDouble(4, line.getUnitPrice());
            lineStmt.setDouble(5, line.getTotalPrice());
            
            if (line.getBatch() != null) {
                lineStmt.setInt(6, line.getBatch().getBatchId());
            } else {
                lineStmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            lineStmt.executeUpdate();
        }
        
        conn.commit();
        return true;
        
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public boolean saveCustomerReturn(CustomerReturn returnObj) {
    String returnSql = "INSERT INTO CustomerReturn (date, reason, totalAmount, customerId) VALUES (?, ?, ?, ?)";
    String lineSql = "INSERT INTO CustomerReturnLine (customerReturnId, productId, quantity, unitPrice, subTotal, batchId) VALUES (?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement returnStmt = conn.prepareStatement(returnSql, Statement.RETURN_GENERATED_KEYS);
         PreparedStatement lineStmt = conn.prepareStatement(lineSql)) {
        
        conn.setAutoCommit(false);
        
        // 1. Save main return
        returnStmt.setDate(1, java.sql.Date.valueOf(returnObj.getReturnDate()));
        returnStmt.setString(2, returnObj.getReason());
        returnStmt.setDouble(3, returnObj.getTotalAmount());
        returnStmt.setInt(4, returnObj.getCustomer().getId());
        returnStmt.executeUpdate();
        
        // Get generated return ID
        ResultSet returnRs = returnStmt.getGeneratedKeys();
        if (returnRs.next()) {
            returnObj.setReturnId(returnRs.getInt(1));
        }
        
        // 2. Save return lines
        for (CustomerReturnLine line : returnObj.getReturnLines()) {
            lineStmt.setInt(1, returnObj.getReturnId());
            lineStmt.setInt(2, line.getProduct().getProductId());
            lineStmt.setInt(3, line.getQuantity());
            lineStmt.setDouble(4, line.getUnitPrice());
            lineStmt.setDouble(5, line.getTotalPrice());
            
            if (line.getBatch() != null) {
                lineStmt.setInt(6, line.getBatch().getBatchId());
            } else {
                lineStmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            lineStmt.executeUpdate();
        }
        
        conn.commit();
        return true;
        
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public boolean updateBatchQuantity(BatchLot batch, int quantityChange) {
    String sql = "UPDATE BatchLot SET availableQty = availableQty + ? WHERE batchId = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, quantityChange);
        stmt.setInt(2, batch.getBatchId());
        
        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public void logUserActivity(int userId, String action) {
    String query = "INSERT INTO UserActivityLog (userId, userAction) VALUES (?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setInt(1, userId);
        stmt.setString(2, action);
        stmt.executeUpdate();

        // Get generated logId
        ResultSet rs = stmt.getGeneratedKeys();
        int logId = 0;
        if (rs.next()) logId = rs.getInt(1);

        // Create object and store in list
        UserActivityLog log = new UserActivityLog(userId, action);
        log.setLogId(logId);
        userActivityLogs.addFirst(log);

        System.out.println("User Activity Logged: " + action);

    } catch (SQLException e) {
        System.err.println("Error logging user activity: " + e.getMessage());
    }
}

public void logInventoryChange(int userId, int productId, String description) {
    String query = "INSERT INTO InventoryAuditLogs (userId, productId, description) VALUES (?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setInt(1, userId);
        stmt.setInt(2, productId);
        stmt.setString(3, description);
        stmt.executeUpdate();

        // Get generated logId
        ResultSet rs = stmt.getGeneratedKeys();
        int logId = 0;
        if (rs.next()) logId = rs.getInt(1);

        // Make object & add to list
        InventoryAuditLog log = new InventoryAuditLog(userId, productId, description);
        log.setLogId(logId);
        inventoryAuditLogs.addFirst(log);

        System.out.println("Inventory Audit Logged: " + description);

    } catch (SQLException e) {
        System.err.println("Error logging inventory change: " + e.getMessage());
    }
}

public void addNotification(Notification notification) {
    String query = "INSERT INTO Notifications (productId, type, message) VALUES (?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setInt(1, notification.getProduct().getProductId());
        stmt.setString(2, notification.getType());
        stmt.setString(3, notification.getMessage());

        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) notification.setId(keys.getInt(1));

        this.notifications.add(notification);
        HeaderController.refreshNotifications();

        System.out.println("NOTIFICATION ADDED: " + notification.getMessage());

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public void removeNotificationForProduct(int productId) {
    String query = "DELETE FROM Notifications WHERE productId = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, productId);
        stmt.executeUpdate();

        // Remove from in-memory list
        notifications.removeIf(n -> n.getProduct().getProductId() == productId);
        HeaderController.refreshNotifications();
        System.out.println("NOTIFICATION REMOVED for productId = " + productId);

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public void evaluateStockNotification(Product product) {
    int qty = product.getQuantity();

    // Remove existing notifications if stock is fine now
    if (qty >= 10) {
        removeNotificationForProduct(product.getProductId());
        return;
    }

    // Out of stock
    if (qty == 0) {
        Notification n = new Notification(
                product,
                "OUT_OF_STOCK",
                "Product '" + product.getName() + "' is OUT OF STOCK!"
        );
        addNotification(n);
        return;
    }

    // Low stock
    if (qty < 10) {
        Notification n = new Notification(
                product,
                "LOW_STOCK",
                "Product '" + product.getName() + "' is LOW on stock (" + qty + " left)"
        );
        addNotification(n);
    }
}


}