package ims.model;

import java.time.LocalDateTime;

public class Restore {
    private String backupFileName;
    private String restoredBy;
    private LocalDateTime restoredAt;

    public Restore(String backupFileName, String restoredBy, LocalDateTime restoredAt) {
        this.backupFileName = backupFileName;
        this.restoredBy = restoredBy;
        this.restoredAt = restoredAt;
    }

    public String getBackupFileName() { return backupFileName; }
    public String getRestoredBy() { return restoredBy; }
    public LocalDateTime getRestoredAt() { return restoredAt; }
}
