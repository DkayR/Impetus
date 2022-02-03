package ca.dklink750.impetus;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PracStats {
    final private Database database;
    final private PracLocation pracLocation;
    final private Impetus plugin;

    public PracStats(Database database, Impetus plugin, PracLocation pracLocation) {
        this.database = database;
        this.pracLocation = pracLocation;
        this.plugin = plugin;
    }

    // Displays timer and attempts to player continuously until player has no locations
    public void displayStats(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!pracLocation.hasCurrentLocationInWorld(player, player.getWorld()) || !player.isOnline()) {
                    cancel();
                } else {
                    int currentAttempts = getCurrentAttempts(player);
                    double timeElapsed = getCurrentTimeElapsed(player);
                    double timeInSeconds = timeElapsed / 20;
                    double seconds = timeInSeconds % 60;
                    double minutes = (timeInSeconds - seconds) % (60 * 60) / 60;
                    double hours = (timeInSeconds - seconds - (minutes * 60)) % (60 * 60 * 60) / (60 * 60);

                    String timerDisplay = String.format("%02.0f:%02.0f:%02.0f", hours, minutes, seconds);
                    PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(ChatColor.GRAY + "Time Elapsed: " + ChatColor.AQUA + timerDisplay + ChatColor.GRAY + " - " + "Attempts: " + ChatColor.AQUA + currentAttempts), (byte) 2);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                    pracLocation.incrementTimeElapsed(player);
                }
            }
        }.runTaskTimer(plugin, 0, 0);
    }

    // Gets current attempts on current practice location
    private int getCurrentAttempts(Player player) {
        int currentAttempts = 0;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT attempts FROM impetus_player_prac_locations JOIN impetus_locations ON impetus_locations.uuid = impetus_player_prac_locations.location_id WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_locations.world = ? AND impetus_player_prac_locations.current_location = true;")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getWorld().getUID().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentAttempts = rs.getInt("attempts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currentAttempts;
    }

    // Gets time elapsed on current practice location
    private double getCurrentTimeElapsed(Player player) {
        double timeElapsed = 0.0F;
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT time_elapsed FROM impetus_player_prac_locations JOIN impetus_locations ON impetus_locations.uuid = impetus_player_prac_locations.location_id WHERE impetus_player_prac_locations.player_uuid = ? AND impetus_locations.world = ? AND impetus_player_prac_locations.current_location = true;")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getWorld().getUID().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                timeElapsed = rs.getDouble("time_elapsed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timeElapsed;
    }
}
