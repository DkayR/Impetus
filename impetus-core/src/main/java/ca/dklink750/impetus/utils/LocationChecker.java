package ca.dklink750.impetus.utils;

import ca.dklink750.impetus.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationChecker {
    final private Database db;

    public LocationChecker(Database db) {
        this.db = db;
    }

    private boolean isLocationUsed(String locationUUID) {
        return getNumLocationsUsed(locationUUID) > 0;
    }

    private boolean isLocationUsedByPlayers(String locationUUID) {
        return getNumLocationUsedInPlayerPracLocations(locationUUID) > 0;
    }

    private int getNumLocationsUsed(String locationUUID) {
        return getNumLocationsUsedInTeleportEffects(locationUUID) + getNumLocationUsedInPlayerPracLocations(locationUUID);
    }

    private int getNumLocationsUsedInTeleportEffects(String locationUUID) {
        int numOfLocations = 0;
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(location_uuid) AS total FROM impetus_teleport_effects WHERE location_uuid = ?;")) {
            stmt.setString(1, locationUUID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                numOfLocations = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numOfLocations;
    }

    private int getNumLocationUsedInPlayerPracLocations(String locationUUID) {
        int numOfLocations = 0;
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(location_id) AS total FROM impetus_player_prac_locations WHERE location_id = ?;")) {
            stmt.setString(1, locationUUID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                numOfLocations = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numOfLocations;
    }

    public void deleteActivatorLocation(String locationUUID) {
        final String query = "DELETE FROM impetus_locations " +
                "WHERE uuid=?;";
        if(!isLocationUsedByPlayers(locationUUID)) {
            try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, locationUUID);
                stmt.execute();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteUnusedLocation(String locationUUID) {
        if (!isLocationUsed(locationUUID)) {
            try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM impetus_locations WHERE uuid=?;")) {
                stmt.setString(1, locationUUID);
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
