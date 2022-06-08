package ca.dklink750.impetus;

import ca.dklink750.impetus.utils.LocationChecker;
import ca.dklink750.impetus.utils.TimerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class Practice {
    final private Database db;
    final private LocationChecker locationChecker;
    final private TimerManager timer;

    public Practice(Impetus plugin, Database db, TimerManager timer) {
        this.db = db;
        this.locationChecker = new LocationChecker(this.db);
        this.timer = timer;
    }

    public boolean unpractice(UUID player, UUID world) {
        boolean result = false;
        if(hasLocation(player, world)) {
            timer.stop(player, world);
            deselectLocations(player, world);
            ArrayList<String> locations = new ArrayList<>();
            locations.addAll(getPracticeByTypeUUID(player, world, PracLocationType.ADHOC));
            locations.addAll(getPracticeByTypeUUID(player, world, PracLocationType.DEFINED));
            for (String location : locations) {
                result = true;
                deleteLocationFor(UUID.fromString(location), player);
            }
        }
        return result;
    }

    public void deleteLocationFor(UUID location, UUID player) {
        final String query = "DELETE FROM impetus_player_prac_locations " +
                "WHERE location_id = ? " +
                "AND player_uuid = ?;";
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, location.toString());
            stmt.setString(2, player.toString());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        locationChecker.deleteUnusedLocation(location.toString());
    }

    public ArrayList<String> getPracticeByTypeUUID(UUID player, UUID world, PracLocationType type) {
        final String query = "SELECT impetus_player_prac_locations.location_id " +
                "FROM impetus_player_prac_locations " +
                "LEFT JOIN impetus_locations " +
                "ON impetus_player_prac_locations.location_id = impetus_locations.uuid " +
                "WHERE impetus_player_prac_locations.player_uuid = ? " +
                "AND impetus_locations.world = ? " +
                "AND impetus_player_prac_locations.prac_location_type = ?;";
        ArrayList<String> result = new ArrayList<>();
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.toString());
            stmt.setString(2, world.toString());
            stmt.setString(3, type.name());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                do {
                    result.add(rs.getString("location_id"));
                } while(rs.next());
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void deselectLocations(UUID player, UUID world) {
        final String query = "UPDATE impetus_player_prac_locations " +
                "LEFT JOIN impetus_locations " +
                "ON impetus_player_prac_locations.location_id = impetus_locations.uuid " +
                "SET impetus_player_prac_locations.current_location = false " +
                "WHERE impetus_player_prac_locations.player_uuid = ? " +
                "AND impetus_locations.world = ?;";
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.toString());
            stmt.setString(2, world.toString());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasLocation(UUID player, UUID world) {
        final String query = "SELECT * " +
                "FROM impetus_player_prac_locations " +
                "JOIN impetus_locations " +
                "ON impetus_locations.uuid = impetus_player_prac_locations.location_id " +
                "WHERE impetus_player_prac_locations.player_uuid = ? " +
                "AND impetus_locations.world = ? " +
                "AND impetus_player_prac_locations.current_location = true;";
        boolean result = false;
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.toString());
            stmt.setString(2, world.toString());

            if(stmt.executeQuery().next()) {
                result = true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean hasThisLocation(UUID player, UUID location) {
        final String query = "SELECT * " +
                "FROM impetus_player_prac_locations " +
                "WHERE location_id = ? " +
                "AND player_uuid = ?";
        boolean result = false;
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, location.toString());
            stmt.setString(2, player.toString());
            if(stmt.executeQuery().next()) {
                result = true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public UUID practice(Location location, UUID player, UUID world, PracLocationType type) {
        unpractice(player, world);
        UUID locationId = UUID.randomUUID();
        persistLocation(location, locationId);
        persistLocationFor(player, locationId, type);
        return locationId;
    }
    public UUID practice(Location location, UUID player, UUID world) {
        return practice(location, player, world, PracLocationType.ADHOC);
    }

    public void persistLocationFor(UUID player, UUID location, PracLocationType type) {
        final String query = "INSERT INTO impetus_player_prac_locations (player_uuid, location_id, creation_date, prac_location_type) " +
                "VALUES (?,?,?,?);";
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.toString());
            stmt.setString(2, location.toString());
            stmt.setLong(3, System.currentTimeMillis());
            stmt.setString(4, type.name());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        timer.start(Bukkit.getPlayer(player));
    }

    private void persistLocation(Location location, UUID uuid) {
        final String query = "INSERT INTO impetus_locations (uuid, world, x, y, z, yaw, pitch) " +
                "VALUES (?,?,?,?,?,?,?);";
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, location.getWorld().getUID().toString());
            stmt.setDouble(3, location.getX());
            stmt.setDouble(4, location.getY());
            stmt.setDouble(5, location.getZ());
            stmt.setDouble(6, location.getYaw());
            stmt.setDouble(7, location.getPitch());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public UUID persistLocation(Location location) {
        UUID locationId = UUID.randomUUID();
        persistLocation(location, locationId);
        return locationId;
    }

    public UUID getPracticeUUID(UUID player, UUID world) {
        final String query = "SELECT location_id " +
                "FROM impetus_player_prac_locations " +
                "JOIN impetus_locations " +
                "ON impetus_player_prac_locations.location_id = impetus_locations.uuid " +
                "WHERE impetus_player_prac_locations.player_uuid = ? " +
                "AND impetus_locations.world = ? " +
                "AND impetus_player_prac_locations.current_location = true;";
        UUID practiceUUID = null;
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.toString());
            stmt.setString(2, world.toString());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                practiceUUID = UUID.fromString(rs.getString("location_id"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return practiceUUID;
    }

    public Location getPractice(UUID player, UUID world) {
        final String query = "SELECT * " +
                "FROM impetus_locations " +
                "JOIN impetus_player_prac_locations " +
                "ON impetus_locations.uuid = impetus_player_prac_locations.location_id " +
                "WHERE impetus_player_prac_locations.player_uuid = ? " +
                "AND impetus_locations.world = ? " +
                "AND impetus_player_prac_locations.current_location = true;";
        Location location = null;
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.toString());
            stmt.setString(2, world.toString());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                location = createLocationObj(rs, world);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return location;
    }

    private Location createLocationObj(ResultSet rs, UUID world) {
        Location location = null;
        try {
            location = new Location(Bukkit.getWorld(world), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch"));
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return location;
    }

    public TimerManager getTimer() {
        return timer;
    }
}
