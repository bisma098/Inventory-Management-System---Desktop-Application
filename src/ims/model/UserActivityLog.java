package ims.model;

import java.time.LocalDateTime;

public class UserActivityLog {
    private int logId;
    private int userId;
    private String userAction;
    private LocalDateTime timestamp;
    
    // Constructors
    public UserActivityLog() {}
    
    public UserActivityLog(int userId, String userAction) {
        this.userId = userId;
        this.userAction = userAction;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUserAction() { return userAction; }
    public void setUserAction(String userAction) { this.userAction = userAction; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}