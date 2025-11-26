package ims.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupUtils {

    private static final String BACKUP_DIR = "C:SQLBackups/";

    // SQL Server connection details
    private static final String URL = "jdbc:sqlserver://SILVERRR;databaseName=ims2;encrypt=false;";
    private static final String USER = "aleena";
    private static final String PASSWORD = "123123";

    public static boolean createBackup() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Timestamped backup file
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupFile = BACKUP_DIR + "backup_" + timestamp + ".bak";

            // SQL Server BACKUP command
            String sql = "BACKUP DATABASE [ims2] TO DISK = '" + backupFile + "' WITH FORMAT, MEDIANAME = 'DBBackup', NAME = 'Full Backup'";

            stmt.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean restoreBackup(String backupFilePath) {
    try {
        // 1. CONNECT TO MASTER, not ims2
        Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://SILVERRR;databaseName=master;encrypt=false;",
                USER, PASSWORD
        );
        Statement stmt = conn.createStatement();

        // 2. Kick users out & set SINGLE_USER
        stmt.executeUpdate("ALTER DATABASE [ims2] SET SINGLE_USER WITH ROLLBACK IMMEDIATE");

        // 3. Restore from backup
        String sql = "RESTORE DATABASE [ims2] FROM DISK = '" + backupFilePath + "' WITH REPLACE";
        stmt.executeUpdate(sql);

        // 4. Return to MULTI_USER
        stmt.executeUpdate("ALTER DATABASE [ims2] SET MULTI_USER");

        conn.close();
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

}

