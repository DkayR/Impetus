package ca.dklink750.impetus;

import ca.dklink750.impetus.utils.LocationChecker;
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

public class PracLocation {
    final private Database database;
    final private LocationChecker locationChecker;
    final private PracStats pracStats;

    public PracLocation(Database database, Impetus plugin) {
        this.database = database;
        this.pracStats = new PracStats(this.database, plugin, this);
        this.locationChecker = new LocationChecker(this.database);
    }

    public PracStats getPracStats() {
        return pracStats;
    }

    // Takes player's UUID and current world and returns their currently selected practice location for the given world.
    public Location getCurrentPracticeLocation(UUID currentPlayer, World currentWorld) {
        Location selectedPracLocation = null;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT impetus_locations.x, impetus_locations.y, impetus_locations.z, impetus_locations.yaw, impetus_locations.pitch, impetus_player_prac_locations.creation_date, impetus_player_prac_locations.current_location FROM impetus_locations JOIN impetus_player_prac_locations ON impetus_locations.uuid = impetus_player_prac_locations.location_id WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_locations.world = ? AND impetus_player_prac_locations.current_location = true ORDER BY impetus_player_prac_locations.creation_date;")) {
            stmt.setString(1, currentPlayer.toString());
            stmt.setString(2, currentWorld.getUID().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                selectedPracLocation = createLocationFromRow(rs, currentWorld);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return selectedPracLocation;
    }

    // Returns array of all Location objects associated with a player in a world
    public Location[] getAllPracticeLocationsLocation(UUID currentPlayer, World currentWorld) {
        ArrayList<Location> currentWorldPracticeLocations = new ArrayList<>();
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT impetus_locations.x, impetus_locations.y, impetus_locations.z, impetus_locations.yaw, impetus_locations.pitch, impetus_player_prac_locations.creation_date, impetus_player_prac_locations.current_location FROM impetus_locations JOIN impetus_player_prac_locations ON impetus_locations.uuid = impetus_player_prac_locations.location_id WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_locations.world = ? ORDER BY impetus_player_prac_locations.creation_date;")) {
            stmt.setString(1, currentPlayer.toString());
            stmt.setString(2, currentWorld.getUID().toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                currentWorldPracticeLocations.add(createLocationFromRow(rs, currentWorld));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currentWorldPracticeLocations.toArray(new Location[0]);
    }

    // Gets a list of practice location UUID (as strings) for a given player in a given world
    public ArrayList<String> getWorldPracticeLocationsUUID(UUID currentPlayer, UUID currentWorld) {
        ArrayList<String> currentWorldPracticeLocationUUIDs = new ArrayList<>();
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT location_id FROM impetus_player_prac_locations JOIN impetus_locations ON impetus_player_prac_locations.location_id = impetus_locations.uuid WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_locations.world = ? ORDER BY impetus_player_prac_locations.creation_date;")) {
            stmt.setString(1, currentPlayer.toString());
            stmt.setString(2, currentWorld.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                currentWorldPracticeLocationUUIDs.add(rs.getString("location_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currentWorldPracticeLocationUUIDs;
    }

    // Gets the current player's practice location UUID for their current world
    public String getCurrentWorldPracLocationUUID(UUID playerUUID, World world) {
        String currentWorldPracLocationUUID = "";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT location_id FROM impetus_player_prac_locations JOIN impetus_locations ON impetus_player_prac_locations.location_id = impetus_locations.uuid WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_locations.world = ? AND impetus_player_prac_locations.current_location = true;")) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, world.getUID().toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                currentWorldPracLocationUUID = rs.getString("location_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currentWorldPracLocationUUID;
    }

    // Returns a location object from the data in the current Result Set row
    private Location createLocationFromRow(ResultSet rs, World currentWorld) {
        Location resultLocation = null;
        try {
            resultLocation = new Location(currentWorld, rs.getDouble(1), rs.getDouble(2), rs.getDouble(3), rs.getFloat(4), rs.getFloat(5));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultLocation;
    }

    // Deselects a player's current practice location in current world
    public void deselectLastPracLocation(UUID currentPlayer, UUID currentWorld) {
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE impetus_player_prac_locations LEFT JOIN impetus_locations ON impetus_player_prac_locations.location_id = impetus_locations.uuid SET impetus_player_prac_locations.current_location = false WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_locations.world = ?;")) {
            stmt.setString(1, currentPlayer.toString());
            stmt.setString(2, currentWorld.toString());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Switches the players current practice location to the next available one in the world
    public Location cyclePlayerPracticeLocation(Player currentPlayer, World currentWorld) {
        Location nextPracLocation = null;
        int pracLocationTotal;
        int currentPracLocation = 0;
        boolean foundCurrent = false;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT impetus_locations.x, impetus_locations.y, impetus_locations.z, impetus_locations.yaw, impetus_locations.pitch, impetus_player_prac_locations.creation_date, impetus_player_prac_locations.current_location, impetus_player_prac_locations.location_id FROM impetus_locations JOIN impetus_player_prac_locations ON impetus_locations.uuid = impetus_player_prac_locations.location_id WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_locations.world = ? ORDER BY impetus_player_prac_locations.creation_date;")) {
            stmt.setString(1, currentPlayer.getUniqueId().toString());
            stmt.setString(2, currentWorld.getUID().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rs.last();
                pracLocationTotal = rs.getRow();
                rs.first();
                do {
                    if (rs.getBoolean(7)) {
                        foundCurrent = true;
                        deselectLastPracLocation(currentPlayer.getUniqueId(), currentWorld.getUID());
                        if (!rs.next()) {
                            rs.first();
                        }
                        try (Connection conn2 = database.getConnection(); PreparedStatement stmt2 = conn2.prepareStatement("UPDATE impetus_player_prac_locations SET current_location=true WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_player_prac_locations.location_id = ?;")) {
                            stmt2.setString(1, currentPlayer.getUniqueId().toString());
                            stmt2.setString(2, rs.getString("location_id"));
                            stmt2.execute();
                        }
                        currentPracLocation = rs.getRow();
                        nextPracLocation = createLocationFromRow(rs, currentWorld);
                    }
                } while (!foundCurrent && rs.next());
                currentPlayer.sendMessage(ChatColor.BLUE + "Switched to practice location: " + currentPracLocation + "/" + pracLocationTotal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextPracLocation;
    }

    // Deletes all of a player's practice locations in a given world
    public void removeAllPracticeLocations(UUID currentPlayer, UUID currentWorld) {
        ArrayList<String> worldPracticeLocationsUUIDs = getWorldPracticeLocationsUUID(currentPlayer, currentWorld);
        for (String worldPracticeLocationsUUID : worldPracticeLocationsUUIDs) {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM impetus_player_prac_locations WHERE location_id = ? AND player_uuid = ?;")) {
                stmt.setString(1, worldPracticeLocationsUUID);
                stmt.setString(2, currentPlayer.toString());
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            locationChecker.deleteUnusedLocation(worldPracticeLocationsUUID);
        }
    }

    // Takes a location UUID and removes related records from the database
    public void removeCurrentPracticeLocation(UUID currentPlayer, UUID currentWorld) {
        String locationToRemove = "";
        boolean foundCurrent = false;
        int numberOfRows;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT impetus_locations.uuid, impetus_player_prac_locations.current_location FROM impetus_locations JOIN impetus_player_prac_locations ON impetus_locations.uuid = impetus_player_prac_locations.location_id WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_locations.world = ?")) {
            stmt.setString(1, currentPlayer.toString());
            stmt.setString(2, currentWorld.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rs.last();
                numberOfRows = rs.getRow();
                rs.first();
                do {
                    if (rs.getBoolean("current_location")) {
                        foundCurrent = true;
                        locationToRemove = rs.getString(1);
                        if (numberOfRows > 1) {
                            if (!rs.next()) {
                                rs.first();
                            }
                            try (Connection conn2 = database.getConnection(); PreparedStatement stmt2 = conn2.prepareStatement("UPDATE impetus_player_prac_locations SET current_location=true WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_player_prac_locations.location_id = ?;")) {
                                stmt2.setString(1, currentPlayer.toString());
                                stmt2.setString(2, rs.getString("uuid"));
                                stmt2.execute();
                            }

                        }
                    }
                } while (!foundCurrent && rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!locationToRemove.isEmpty()) {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM impetus_player_prac_locations WHERE location_id = ? AND player_uuid = ?;")) {
                stmt.setString(1, locationToRemove);
                stmt.setString(2, currentPlayer.toString());
                stmt.execute();
                locationChecker.deleteUnusedLocation(locationToRemove);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Adds the players current location to the stored location table
    public UUID addCurrentLocation(Location currentPlayerLocation) {
        UUID locationUUID = UUID.randomUUID();
        addLocationToDatabase(currentPlayerLocation, locationUUID);
        return locationUUID;
    }

    // Adds a Location and sets to a player's current location
    public UUID addNewLocationAndAssociate(Player currentPlayer, Location currentPlayerLocation, boolean hasCurrentLocation, PracLocationType pracLocationType) {
        UUID locationUUID = UUID.randomUUID();
        addLocationToDatabase(currentPlayerLocation, locationUUID);
        addLocationIDAndPlayerIDBridgeToDatabase(currentPlayer, locationUUID.toString(), pracLocationType, hasCurrentLocation);
        return locationUUID;
    }

    // Deselects current location and then associates given location to a player
    public boolean associateLocation(Player player, String locationUUID, PracLocationType pracLocationType) {
        boolean associatedLocation = false;
        if (!playerHasThisLocation(locationUUID, player)) {
            associatedLocation = true;
            boolean hasLocationInWorld = hasCurrentLocationInWorld(player, player.getWorld());
            deselectLastPracLocation(player.getUniqueId(), player.getWorld().getUID());
            addLocationIDAndPlayerIDBridgeToDatabase(player, locationUUID, pracLocationType, hasLocationInWorld);
        }
        else if (!playerHasThisCurrentLocation(locationUUID, player)) {
            deselectLastPracLocation(player.getUniqueId(), player.getWorld().getUID());
            selectLocation(locationUUID, player);
        }
        return associatedLocation;
    }

    // Deselects current location and then associates given location to a player (determined 'has a' location)
    public void associateLocation(Player player, String locationUUID, PracLocationType pracLocationType, boolean hasACurrentLocation) {
        if (!playerHasThisLocation(locationUUID, player)) {
            deselectLastPracLocation(player.getUniqueId(), player.getWorld().getUID());
            addLocationIDAndPlayerIDBridgeToDatabase(player, locationUUID, pracLocationType, hasACurrentLocation);
        }
    }

    // Adds a location with type to database and associates (Used to tell if a player has a location before deselection)
    public UUID registerNewLocation(Player player, Location currentLocation, PracLocationType pracLocationType) {
        World world = player.getWorld();
        boolean hasLocation = hasCurrentLocationInWorld(player, world);
        deselectLastPracLocation(player.getUniqueId(), world.getUID());
        return addNewLocationAndAssociate(player, currentLocation, hasLocation, pracLocationType);
    }

    // Adds the location record to the database
    private void addLocationToDatabase(Location currentPlayerLocation, UUID locationUUID) {
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO impetus_locations (uuid, world, x, y, z, yaw, pitch) VALUES (?,?,?,?,?,?,?);")) {
            stmt.setString(1, locationUUID.toString());
            stmt.setString(2, currentPlayerLocation.getWorld().getUID().toString());
            stmt.setDouble(3, currentPlayerLocation.getX());
            stmt.setDouble(4, currentPlayerLocation.getY());
            stmt.setDouble(5, currentPlayerLocation.getZ());
            stmt.setDouble(6, currentPlayerLocation.getYaw());
            stmt.setDouble(7, currentPlayerLocation.getPitch());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Adds the bridge record linking a player and location to the database
    public void addLocationIDAndPlayerIDBridgeToDatabase(Player currentPlayer, String locationUUID, PracLocationType pracLocationType, boolean hasCurrentLocation) {
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO impetus_player_prac_locations (player_uuid, location_id, creation_date, prac_location_type) VALUES (?,?,?,?);")) {
            stmt.setString(1, currentPlayer.getUniqueId().toString());
            stmt.setString(2, locationUUID);
            stmt.setLong(3, System.currentTimeMillis());
            stmt.setString(4, pracLocationType.name());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!hasCurrentLocation) {
            displayPracStats(currentPlayer);
        }
    }

    // Returns true if player has at least one location stored in the database
    public boolean containsPlayer(UUID currentPlayer, UUID currentWorld) {
        boolean recordExists = false;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM impetus_locations JOIN impetus_player_prac_locations ON impetus_locations.uuid = impetus_player_prac_locations.location_id WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_locations.world = ?;")) {
            stmt.setString(1, currentPlayer.toString());
            stmt.setString(2, currentWorld.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                recordExists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recordExists;
    }

    // Selects a specific location to be a player's current (does not deselect old one)
    public void selectLocation(String locationUUID, Player player) {
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE impetus_player_prac_locations SET current_location=true WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_player_prac_locations.location_id = ?;")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, locationUUID);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Removes all of the associated defined locations (from activator blocks) in a player's table
    public void removeDefinedLocations(Player player) {
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE impetus_player_prac_locations FROM impetus_player_prac_locations JOIN impetus_locations ON impetus_locations.uuid = impetus_player_prac_locations.location_id WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_locations.world = ? AND impetus_player_prac_locations.prac_location_type = 'DEFINED';")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getWorld().getUID().toString());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Checks if a player already has a defined location
    public boolean playerHasOtherDefinedLocations(Player player, String locationUUID) {
        int numOfDefined;
        boolean hasOtherDefined = false;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(impetus_player_prac_locations.player_uuid) AS total FROM impetus_player_prac_locations JOIN impetus_locations ON impetus_locations.uuid = impetus_player_prac_locations.location_id WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_locations.world = ? AND prac_location_type = 'DEFINED' AND impetus_player_prac_locations.location_id <> ?;")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getWorld().getUID().toString());
            stmt.setString(3, locationUUID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                numOfDefined = rs.getInt("total");
                if (numOfDefined > 0) {
                    hasOtherDefined = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hasOtherDefined;
    }

    // Checks if a given UUID matches any of the player's locations (non-selected)
    public boolean playerHasThisLocation(String locationUUID, Player player) {
        boolean hasLocation = false;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM impetus_player_prac_locations WHERE location_id = ? AND player_uuid = ?;")) {
            stmt.setString(1, locationUUID);
            stmt.setString(2, player.getUniqueId().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                hasLocation = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hasLocation;
    }

    // Checks if a given UUID matches the player's currently selected location
    public boolean playerHasThisCurrentLocation(String locationUUID, Player player) {
        boolean current = false;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM impetus_player_prac_locations WHERE location_id = ? AND player_uuid = ? AND current_location = true;")) {
            stmt.setString(1, locationUUID);
            stmt.setString(2, player.getUniqueId().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                current = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return current;
    }

    // Checks if a player has an active location in the current world
    public boolean hasCurrentLocationInWorld(Player player, World world) {
        boolean hasCurrentInWorld = false;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT current_location FROM impetus_player_prac_locations JOIN impetus_locations ON impetus_locations.uuid = impetus_player_prac_locations.location_id WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_locations.world = ? AND impetus_player_prac_locations.current_location = true;")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, world.getUID().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                hasCurrentInWorld = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hasCurrentInWorld;
    }

    // Checks if the player has locations other than the currently selected one
    public boolean hasOtherLocationInWorld(Player player, World world) {
        boolean hasOtherLocationInWorld = false;

        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT current_location FROM impetus_player_prac_locations JOIN impetus_locations ON impetus_locations.uuid = impetus_player_prac_locations.location_id WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_locations.world = ? AND impetus_player_prac_locations.current_location = false;")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, world.getUID().toString());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                hasOtherLocationInWorld = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hasOtherLocationInWorld;
    }

    // Increment practice locations attempt field for a player
    public void incrementAttempts(Player player) {
        String locationToIncreaseAttempts = getCurrentWorldPracLocationUUID(player.getUniqueId(), player.getWorld());
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE impetus_player_prac_locations SET attempts = attempts + 1 WHERE player_uuid = ? AND location_id = ?;")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, locationToIncreaseAttempts);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Increment time elapsed field for a player
    public void incrementTimeElapsed(Player player) {
        String locationToIncreaseTime = getCurrentWorldPracLocationUUID(player.getUniqueId(), player.getWorld());
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE impetus_player_prac_locations SET time_elapsed = time_elapsed + 1 WHERE player_uuid = ? AND location_id = ?;")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, locationToIncreaseTime);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Displays timer for a given player
    public void displayPracStats(Player player) {
        pracStats.displayStats(player);
    }
}
