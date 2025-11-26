package ims.model;

import java.time.LocalDateTime;

public class Backup {
    private String fileName;      // e.g., "IMS_backup_2025-11-26.sql"
    private LocalDateTime createdAt;
    private String status;        // "SUCCESS" or "FAILED"

    public Backup(String fileName, LocalDateTime createdAt, String status) {
        this.fileName = fileName;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
