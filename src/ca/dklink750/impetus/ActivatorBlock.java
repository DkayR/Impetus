package ca.dklink750.impetus;

import ca.dklink750.impetus.utils.CustomItem;
import ca.dklink750.impetus.utils.LocationChecker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ActivatorBlock {
    final private Database database;
    final private LocationChecker locationChecker;
    final private Practice practice;
    final private CustomItem practiceTool = new CustomItem(Material.SLIME_BALL, ChatColor.GREEN + "Return", Arrays.asList("Right click: Return to current practice location", "Left click: Cycle through practice locations", "Drop: Create new practice location"), "prac", "prac");
    final private Map<UUID, ActivatorLocation> tempActivators = new HashMap<>();

    public ActivatorBlock(Database database, Practice practice) {
        this.database = database;
        this.practice = practice;
        this.locationChecker = new LocationChecker(this.database);
    }

    // Takes an activator location (x,y,z & world) and checks if it is currently an activator block
    public boolean isActivatorBlockFromLoc(ActivatorLocation activatorLocation) {
        boolean foundBlock = false;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM impetus_activator_blocks WHERE world_uuid = ? AND block_x = ? AND block_y = ? AND block_z = ?;")) {
            stmt.setString(1, activatorLocation.world().getUID().toString());
            stmt.setInt(2, activatorLocation.x());
            stmt.setInt(3, activatorLocation.y());
            stmt.setInt(4, activatorLocation.z());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                foundBlock = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundBlock;
    }

    // Checks if there is an activator block record for a given UUID
    public boolean isActivatorBlockFromUUID(String activatorBlockUUID) {
        boolean foundBlock = false;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM impetus_activator_blocks WHERE block_uuid = ?;")) {
            stmt.setString(1, activatorBlockUUID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                foundBlock = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundBlock;
    }

    // Gets UUID from activator location
    public UUID getActivatorBlockUUID(ActivatorLocation activatorLocation) {
        UUID activatorUUID = null;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT block_uuid FROM impetus_activator_blocks WHERE world_uuid = ? AND block_x = ? AND block_y = ? AND block_z = ?;")) {
            stmt.setString(1, activatorLocation.world().getUID().toString());
            stmt.setInt(2, activatorLocation.x());
            stmt.setInt(3, activatorLocation.y());
            stmt.setInt(4, activatorLocation.z());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                activatorUUID = UUID.fromString(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activatorUUID;
    }

    // Adds a activator record given a new location
    public UUID createActivatorBlock(ActivatorLocation activatorLocation) {
        UUID activatorBlockUUID = UUID.randomUUID();
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO impetus_activator_blocks (block_uuid, world_uuid, block_x, block_y, block_z) VALUES (?,?,?,?,?);")) {
            stmt.setString(1, activatorBlockUUID.toString());
            stmt.setString(2, activatorLocation.world().getUID().toString());
            stmt.setInt(3, activatorLocation.x());
            stmt.setInt(4, activatorLocation.y());
            stmt.setInt(5, activatorLocation.z());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activatorBlockUUID;
    }

    // Adds teleport effect to an effect set (list of effects on associated activator blocks)
    public void addTeleportEffectToEffectSet(String effectSetUUID, UUID locationUUID, TeleportType teleportType) {
        UUID effectUUID = addEffect(effectSetUUID, EffectType.TELEPORT);

        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO impetus_teleport_effects (effect_id, location_uuid, teleport_type) VALUES (?,?,?);")) {
            stmt.setString(1, effectUUID.toString());
            stmt.setString(2, locationUUID.toString());
            stmt.setString(3, teleportType.name());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Removes all teleport effects from a given effect set
    public void removeTeleportEffectsFromEffectSet(String effectSetUUID) {
        cleanLocations(effectSetUUID);
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM impetus_effect_set_effects WHERE effect_set_uuid = ? AND effect_type = 'TELEPORT';")) {
            stmt.setString(1, effectSetUUID);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Sorts effects on effect set when an effect is removed (For later use when multiple effects are implemented)
    private void decreaseOrderOfHigherEffects(String effectID) {
        String effectSetUUID = getEffectSetUUIDFromEffectID(effectID);
        Integer orderNumOfRemoved = getEffectOrderFromID(effectID);
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE impetus_effect_set_effects SET effect_order = effect_order - 1 WHERE effect_set_uuid = ? AND effect_order > ?;")) {
            stmt.setString(1, effectSetUUID);
            stmt.setInt(2, orderNumOfRemoved);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Returns the index of the effect in an effect set when given an effect id
    private Integer getEffectOrderFromID(String effectID) {
        Integer orderNumber = null;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT effect_order FROM impetus_effect_set_effects WHERE effect_id = ?;")) {
            stmt.setString(1, effectID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                orderNumber = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderNumber;
    }

    // Finds to effect set that an effect is associated with
    public String getEffectSetUUIDFromEffectID(String effectID) {
        String effectSetUUID = "";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT effect_set_uuid FROM impetus_effect_set_effects WHERE effect_id = ?;")) {
            stmt.setString(1, effectID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                effectSetUUID = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return effectSetUUID;
    }

    // Returns location data UUID that is associated with an effect (teleport)
    public String getLocationUUIDFromEffectID(String effectID) {
        String locationID = "";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT impetus_teleport_effects.location_uuid FROM impetus_teleport_effects WHERE effect_id = ?;")) {
            stmt.setString(1, effectID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                locationID = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locationID;
    }

    // Creates a record for an empty effect set
    private UUID createEffectSet() {
        UUID effectSetUUID = UUID.randomUUID();
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO impetus_effect_sets (effect_set_uuid) VALUES (?);")) {
            stmt.setString(1, effectSetUUID.toString());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return effectSetUUID;
    }

    // Associates effect set with activator block (effect sets can be associated with multiple activator blocks)
    public void addEffectSetToActivator(UUID effectSetUUID, String activatorBlockUUID) {
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE impetus_activator_blocks SET effect_set_uuid = ? WHERE block_uuid = ?")) {
            stmt.setString(1, effectSetUUID.toString());
            stmt.setString(2, activatorBlockUUID);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Creates a new effect set and associates it when an activator block
    public UUID createEffectSetAndAddToActivator(String activatorBlockUUID) {
        UUID effectSetUUID = createEffectSet();
        addEffectSetToActivator(effectSetUUID, activatorBlockUUID);
        return effectSetUUID;
    }

    // Returns UUID of effect set from location of activator block
    public String getEffectSetUUIDFromActivatorLoc(ActivatorLocation activatorLocation) {
        String effectSetUUID = "";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT effect_set_uuid FROM impetus_activator_blocks WHERE world_uuid = ? AND block_x = ? AND block_y = ? AND block_z = ?;")) {
            stmt.setString(1, activatorLocation.world().getUID().toString());
            stmt.setInt(2, activatorLocation.x());
            stmt.setInt(3, activatorLocation.y());
            stmt.setInt(4, activatorLocation.z());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                effectSetUUID = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return effectSetUUID;
    }

    // Returns UUID of effect set from activator block uuid
    public String getEffectSetUUIDFromActivatorUUID(String activatorBlockUUID) {
        String effectSetUUID = "";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT effect_set_uuid FROM impetus_activator_blocks WHERE block_uuid = ?;")) {
            stmt.setString(1, activatorBlockUUID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                effectSetUUID = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return effectSetUUID;
    }

    // Deletes records relating to an activator block location
    public void deleteActivatorBlockData(ActivatorLocation activatorLocation) {
        String effectSetUUID = getEffectSetUUIDFromActivatorLoc(activatorLocation);
        UUID activatorUUID = getActivatorBlockUUID(activatorLocation);
        // Do not have to delete effect set always, this is a temporary setup. Check for number of things using effect set and delete if 1 or less.
        cleanLocations(effectSetUUID);
        deleteActivatorBlock(activatorUUID); // Child
        deleteEffectSetFromUUID(effectSetUUID); // Parent
    }

    // Deletes effect set and the unused locations associated
    public void deleteEffectSetFromUUID(String effectSetUUID) {
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM impetus_effect_sets WHERE effect_set_uuid = ?;")) {
            stmt.setString(1, effectSetUUID);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void cleanLocations(String effectSetId) {
        cleanLocations(effectSetId, false);
    }
    public void cleanLocations(String effectSetId, boolean force) {
        for(String effectId : getAllEffectsOfTypeFromEffectSet(effectSetId, EffectType.TELEPORT)) {
            if(force) {
                locationChecker.deleteUnusedLocation(getLocationUUIDFromEffectID(effectId));
            } else {
                locationChecker.deleteActivatorLocation(getLocationUUIDFromEffectID(effectId));
            }
        }
    }

    // Returns list of all UUIDs that match the selected effect set and type
    private ArrayList<String> getAllEffectsOfTypeFromEffectSet(String effectSetUUID, EffectType effectType) {
        ArrayList<String> effects = new ArrayList<>();
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT effect_id FROM impetus_effect_set_effects WHERE effect_set_uuid = ? AND effect_type = ?;")) {
            stmt.setString(1, effectSetUUID);
            stmt.setString(2, effectType.name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                do {
                    effects.add(rs.getString("effect_id"));
                } while (rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return effects;
    }

    // Deletes in database for activator block UUID
    private void deleteActivatorBlock(UUID activatorUUID) {
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM impetus_activator_blocks WHERE block_uuid = ?;")) {
            stmt.setString(1, activatorUUID.toString());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Checks if a player attempted to make the block an activator block previously
    public boolean isRegistered(UUID playerUUID, ActivatorLocation activatorLocation) {
        boolean isRegistered = false;
        if (tempActivators.containsKey(playerUUID)) {
            ActivatorLocation storedBlock = tempActivators.get(playerUUID);
            if (storedBlock.equals(activatorLocation)) {
                isRegistered = true;
            }
        }
        return isRegistered;
    }

    // Saves the last block the player attempted to make into an activator block
    public void register(UUID playerUUID, ActivatorLocation activatorLocation) {
        tempActivators.put(playerUUID, activatorLocation);
    }

    // Checks if passed in block location matches one in temporary map
    public boolean playerSelectsDifferentBlock(UUID playerUUID, ActivatorLocation activatorLocation) {
        if (tempActivators.containsKey(playerUUID)) {
            ActivatorLocation storedBlock = tempActivators.get(playerUUID);
            return !activatorLocation.equals(storedBlock);
        }
        return true;
    }

    // Removes any value for a player in the activator temp map
    public void clearRegistration(UUID playerUUID) {
        tempActivators.remove(playerUUID);
    }

    // Executes any effects that the activator block has associated with it
    public void executeEffects(Player player, UUID activatorUUID) {
        String effectSetUUID = getEffectSetUUIDFromActivatorUUID(activatorUUID.toString());
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM impetus_effect_set_effects WHERE effect_set_uuid = ? ORDER BY effect_order;")) {
            stmt.setString(1, effectSetUUID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                do {
                    String effectID = rs.getString("effect_id");
                    String effect_type = rs.getString("effect_type");
                    switch (effect_type) {
                        case "TELEPORT":
                            // Teleport Player
                            executeTeleportEffect(player, effectID);
                            break;
                        case "POTION":
                            // Apply potion effect
                            break;
                        case "MESSAGE":
                            // Send player a message
                            break;
                    }
                } while (rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Executes a different type of teleport depending on the effect's teleport type
    private void executeTeleportEffect(Player player, String effectID) {
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM impetus_teleport_effects WHERE effect_id = ?;")) {
            stmt.setString(1, effectID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UUID locationUUID = UUID.fromString(rs.getString("location_uuid"));
                String teleportType = rs.getString("teleport_type");
                switch (teleportType) {
                    case "DEFINED":
                        if(!practice.hasThisLocation(player.getUniqueId(), locationUUID)) {
                            if(practiceTool.giveCustomItemToPlayer(player, ChatColor.RED + "Inventory full, cannot practice!")) {
                                practice.unpractice(player.getUniqueId(), player.getWorld().getUID());
                                practice.persistLocationFor(player.getUniqueId(), locationUUID, PracLocationType.DEFINED);
                                player.sendMessage(ChatColor.YELLOW + "Updated precise coordinates!");
                            }
                        }
                        break;
                    case "INSTANT":
                        // Instantly teleport the player to a set position
                        break;
                    case "UPDATE":
                        // Set prac current location to location when clicking activator
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // Checks if an activator block has an associated effect set
    public boolean hasEffectSet(String activatorBlockUUID) {
        boolean hasEffectSet = false;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM impetus_activator_blocks WHERE block_uuid = ? AND effect_set_uuid IS NOT NULL;")) {
            stmt.setString(1, activatorBlockUUID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                hasEffectSet = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hasEffectSet;
    }

    // TODO
    public boolean hasEffectsFromActivatorUUID(String activatorBlockUUID) {
        boolean hasEffects = false;
        if (hasEffectSet(activatorBlockUUID)) {
            hasEffects = hasEffectsFromEffectSetUUID(getEffectSetUUIDFromActivatorUUID(activatorBlockUUID));
        }
        return hasEffects;
    }

    // Checks if an effect set has any associated effects
    public boolean hasEffectsFromEffectSetUUID(String effectSetUUID) {
        boolean hasEffects = false;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM impetus_effect_set_effects WHERE effect_set_uuid = ?;")) {
            stmt.setString(1, effectSetUUID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                hasEffects = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hasEffects;
    }

    // Adds an effect of given type to an effect set
    public UUID addEffect(String effectSetUUID, EffectType effectType) {
        UUID effectID = UUID.randomUUID();
        int effectOrder = getNumberOfEffects(effectSetUUID) + 1;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO impetus_effect_set_effects (effect_set_uuid, effect_id, effect_type, effect_order) VALUES (?,?,?,?);")) {
            stmt.setString(1, effectSetUUID);
            stmt.setString(2, effectID.toString());
            stmt.setString(3, effectType.name());
            stmt.setInt(4, effectOrder);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return effectID;
    }

    // Counts number of effect in a set
    private Integer getNumberOfEffects(String effectSetUUID) {
        Integer numberOfEffects = null;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(effect_order) AS total FROM impetus_effect_set_effects WHERE effect_set_uuid = ?;")) {
            stmt.setString(1, effectSetUUID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                numberOfEffects = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numberOfEffects;
    }
}
