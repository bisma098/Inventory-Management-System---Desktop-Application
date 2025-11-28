package ims.controller;

import ims.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ims.database.DatabaseConnection;
import java.sql.*;
import java.util.*;
import java.sql.Date;  // For SQL Date operations
import java.time.LocalDate;

public class DataController {
    private List<User> users;
    private User currentUser;
    private List<Product> products;
    private List<Category> categories;
    private ProductCatalogue productCatalogue;

    private List<Supplier> suppliers;
    private List<PurchaseOrder> purchaseOrders;
    private List<PurchaseOrderLine> allPurchaseOrderLines;
    private List<InventoryAuditLog> inventoryAuditLogs;
    private List<UserActivityLog> userActivityLogs;
    private List<SupplierReturn> supplierReturns;
    private List<SupplierReturnLine> allSupplierReturnLines;
    private List<Warehouse> warehouses;
    private List<BatchLot> batchLots;
    
    private static DataController instance;
    
    private DataController() {    // private constructor prevents new DataController()
        this.users = new ArrayList<>();
        this.products = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.productCatalogue = new ProductCatalogue();
        this.suppliers = new ArrayList<>();
        this.purchaseOrders = new ArrayList<>();
        this.allPurchaseOrderLines = new ArrayList<>();
        this.supplierReturns =new ArrayList<>();
        this.allSupplierReturnLines=new ArrayList<>();
        this.warehouses = new ArrayList<>();
        this.userActivityLogs = new ArrayList<>();
        this.inventoryAuditLogs = new ArrayList<>();

        this.batchLots= new ArrayList<>();

        loadAllData();
    }
    
    public static DataController getInstance() {
        if (instance == null) {
            instance = new DataController();
        }
        return instance;
    }
    public User getCurrentUser() {
        return currentUser;
    }
    public void loadAllData() {
        loadUsers();
        loadCategories();
        loadProducts();
        loadSuppliers();
        loadWarehouses();
        loadBatchLots(); 
        logUserActivity(0, null);
        loadAllUserActivityLogs();
        loadAllInventoryAuditLogs();

        //loadPurchaseOrders();
        //loadSupplierReturns();
    }
    // Add this method to load users
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
        return users.stream()
                .filter(user -> user.getUserName().equals(username) 
                             && user.getPassword().equals(password)
                             && user.getRole().equals(role))
                .findFirst()
                .orElse(null);
    }
    
    // Add getter for users
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }
    

    private void loadCategories() {
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
    
    private void loadProducts() {
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
                    
                    // Handle null expiry date
                    java.sql.Date sqlDate = rs.getDate("expiry_date");
                    java.time.LocalDate expiryDate = null;
                    if (sqlDate != null) {
                        expiryDate = sqlDate.toLocalDate();
                    }
                    
                    Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("sku"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        expiryDate,
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
    
     // Load suppliers directly in DataController
    private void loadSuppliers() {
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

    // Load warehouses directly in DataController
private void loadWarehouses() {
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

    // Load purchase orders directly in DataController
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

public List<PurchaseOrderLine> loadOrderLines(int orderId) {
    List<PurchaseOrderLine> lines = new ArrayList<>();

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

            lines.add(line);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return lines;
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

// Load all supplier returns
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

// Load return lines for a specific supplier return
public List<SupplierReturnLine> loadSupplierReturnLines(int supplierReturnId) {
    
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



// Helper method to find product by ID
public Product findProductById(int productId) {
    return products.stream()
            .filter(p -> p.getProductId() == productId)
            .findFirst()
            .orElse(null);
}

// Helper method to find supplier by ID
public Supplier findSupplierById(int supplierId) {
    return suppliers.stream()
            .filter(s -> s.getSupplierId() == supplierId)
            .findFirst()
            .orElse(null);
}

// Helper method to find warehouse by ID
public Warehouse findWarehouseById(int warehouseId) {
    return warehouses.stream()
            .filter(w -> w.getWarehouseId() == warehouseId)
            .findFirst()
            .orElse(null);
}

// Helper method to find purchase order by ID
public PurchaseOrder findPurchaseOrderById(int orderId) {
    return purchaseOrders.stream()
            .filter(po -> po.getOrderId() == orderId)
            .findFirst()
            .orElse(null);
}

// Helper method to find purchase order line by ID
public PurchaseOrderLine findPurchaseOrderLineById(int lineId) {
    return allPurchaseOrderLines.stream()
            .filter(pol -> pol.getLineId() == lineId)
            .findFirst()
            .orElse(null);
}

// Add this method to your DataController class
private BatchLot findBatchById(int batchId) {
    for (BatchLot batch : batchLots) {
        if (batch.getBatchId() == batchId) {
            return batch;
        }
    }
    return null;
}

    // Getters
public List<Product> getAllProducts() {
    return new ArrayList<>(products);
}
    
public List<Category> getAllCategories() {
   return new ArrayList<>(categories);
}
public List<UserActivityLog> getUserActivityLogs() {
   return new ArrayList<>(userActivityLogs);
}

public List<InventoryAuditLog> getInventoryAuditLogs() {
   return new ArrayList<>(inventoryAuditLogs);
}
// Getter for suppliers
public List<Supplier> getSuppliers() {
    return new ArrayList<>(suppliers);
}

public List<BatchLot> getBatchLots(){
    return new ArrayList<>(batchLots);
}

// Getter for products  
public List<Product> getProducts() {
    return new ArrayList<>(products);
}

// Getter for warehouses
public List<Warehouse> getWarehouses() {
    return new ArrayList<>(warehouses);
}

// Getter for purchase orders
public List<PurchaseOrder> getPurchaseOrders() {
    return new ArrayList<>(purchaseOrders);
}

// Getter for all purchase order lines
public List<PurchaseOrderLine> getAllPurchaseOrderLines() {
    return new ArrayList<>(allPurchaseOrderLines);
}


// Getter for categories
public List<Category> getCategories() {
    return new ArrayList<>(categories);
}

public void addPurchaseOrder(PurchaseOrder po)
{
    purchaseOrders.add(po);
}
public void addBatch(BatchLot batch)
{
    batchLots.add(batch);
}

public void setCurrentUser(User user)
{
    this.currentUser=user;
}

public ObservableList<SupplierReturn> getSupplierReturns() {
    return FXCollections.observableArrayList(supplierReturns);
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

public void updateProductQuantity(Product prod, int qty) {
    // Use the same table name as in your loadProducts method
    String query = "UPDATE products SET quantity = quantity + ? WHERE product_id = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, qty);
        stmt.setInt(2, prod.getProductId());
        stmt.executeUpdate();
        
        // Also update the product object in memory
        prod.setQuantity(prod.getQuantity() + qty);
        
    } catch (SQLException e) {
        System.err.println("Error updating product quantity: " + e.getMessage());
        e.printStackTrace();
    }
}
// Add this method to DataController class
public void logInventoryChange(int userId, int productId, String type, int qty, String reason) {
    String query = "INSERT INTO InventoryAuditLog (UserID, ProductID, Type, Quantity, Reason, Timestamp) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, userId);
        stmt.setInt(2, productId);
        stmt.setString(3, type);
        stmt.setInt(4, qty);
        stmt.setString(5, reason);
        stmt.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Error logging inventory change: " + e.getMessage());
        e.printStackTrace();
    }
}
// Add these methods to your DataController class:

public void addSupplierReturn(SupplierReturn supplierReturn) {
    if (!supplierReturns.contains(supplierReturn)) {
        supplierReturns.add(supplierReturn);
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
        userActivityLogs.add(log);

        System.out.println("User Activity Logged: " + action);

    } catch (SQLException e) {
        System.err.println("Error logging user activity: " + e.getMessage());
    }
}

public List<UserActivityLog> loadAllUserActivityLogs() {
    List<UserActivityLog> logs = new ArrayList<>();
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
            logs.add(log);
        }

    } catch (SQLException e) {
        System.err.println("Error loading user activity logs: " + e.getMessage());
    }

    return logs;
}

public List<InventoryAuditLog> loadAllInventoryAuditLogs() {
    List<InventoryAuditLog> logs = new ArrayList<>();
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
            logs.add(log);
        }

    } catch (SQLException e) {
        System.err.println("Error loading inventory audit logs: " + e.getMessage());
    }

    return logs;
}


public List<BatchLot> getBatchesByProductId(int productId) {
    List<BatchLot> result = new ArrayList<>();
    for (BatchLot batch : batchLots) {      // <-- your internal list
        if (batch.getProduct() != null && batch.getProduct().getProductId() == productId) {
            result.add(batch);
        }
    }
    return result;
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
        inventoryAuditLogs.add(log);

        System.out.println("Inventory Audit Logged: " + description);

    } catch (SQLException e) {
        System.err.println("Error logging inventory change: " + e.getMessage());
    }
    }
}