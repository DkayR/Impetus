package ca.dklink750.impetus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class User {
    final private Database database;

    public User(Database database) {
        this.database = database;
    }

    // Stores unique player ID upon login
    public void storeUUID(UUID playerUUID) {
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT IGNORE INTO impetus_players (uuid) VALUES (?);")) {
            stmt.setString(1, playerUUID.toString());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Checks if a username has an associated entry in the database
    public boolean playerExists(String username) {
        boolean exists = false;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT uuid FROM impetus_players WHERE base_display_name = ?;")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    // Gets player unique ID from their username if exists
    public UUID getPlayerUUIDFromUserName(String username) {
        UUID playerUUID = null;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT uuid FROM impetus_players WHERE base_display_name = ?;")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                playerUUID = UUID.fromString(rs.getString("uuid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerUUID;
    }

    // Sets a players display name and associates it with their UUID
    public void setBaseDisplayName(String username, UUID playerUUID) {
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE impetus_players SET base_display_name=? WHERE uuid = ?;")) {
            stmt.setString(1, username);
            stmt.setString(2, playerUUID.toString());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
