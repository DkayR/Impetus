package ca.dklink750.impetus;

import ca.dklink750.impetus.utils.ConfigManager;
import org.mariadb.jdbc.MariaDbPoolDataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.bukkit.Bukkit.getLogger;

public class Database {
    private String host;
    private String port;
    private String databaseName;
    private String username;
    private String password;
    private MariaDbPoolDataSource dataSource;
    private boolean databaseInitialized = true;

    public Database(ConfigManager configManager) {
        if (initializeConnectionInfo(configManager)) {
            try {
                initializeDataSource();
            } catch (Exception e) {
                this.databaseInitialized = false;
                getLogger().severe("[Impetus] Could not connect to database, make sure config.yml is correctly configured");
                e.printStackTrace();
            }

            try {
                testDataSource(dataSource);
            } catch (Exception e) {
                this.databaseInitialized = false;
                e.printStackTrace();
            }

            try {
                runSQLScript(configManager);
            } catch (Exception e) {
                this.databaseInitialized = false;
                e.printStackTrace();
            }

            if (isDatabaseInitialized()) {
                getLogger().info("[Impetus] Successfully connected and setup database!");
            }
        } else {
            this.databaseInitialized = false;
            getLogger().warning("[Impetus] Setup database info in config.yml for plugin to work!");
        }
    }

    private boolean initializeConnectionInfo(ConfigManager configManager) {
        boolean result = true;
        this.host = configManager.getHost();
        this.port = configManager.getPort();
        this.databaseName = configManager.getDatabaseName();
        this.username = configManager.getUsername();
        this.password = configManager.getPassword();
        if (host == null || port == null || databaseName == null || username == null || password == null) {
            result = false;
        }
        return result;
    }

    private void initializeDataSource() throws SQLException {
        this.dataSource = new MariaDbPoolDataSource("jdbc:mariadb://" + host + ":" + port + "/" + databaseName + "?user=" + username + "&password=" + password);
    }

    private void testDataSource(MariaDbPoolDataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(2000)) {
                throw new SQLException("[Impetus] Could not connect to database");
            }
        }
    }

    public Boolean isDatabaseInitialized() {
        return databaseInitialized;
    }

    private void runSQLScript(ConfigManager configManager) throws SQLException {
        String pathToScript = "plugins/Impetus/" + configManager.getScript();
        String[] contents = getDelimitedStatements(pathToScript);
        for (String content : contents) {
            if (!content.isEmpty()) {
                try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(content)) {
                    stmt.execute();
                }
            }
        }
    }

    private String[] getDelimitedStatements(String fileName) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            this.databaseInitialized = false;
            getLogger().severe("[Impetus] Could not find database script file!");
            e.printStackTrace();
        }
        return content.split(";");
    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = this.dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
