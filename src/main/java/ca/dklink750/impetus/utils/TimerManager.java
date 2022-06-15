package ca.dklink750.impetus.utils;

import ca.dklink750.impetus.Database;
import ca.dklink750.impetus.Impetus;
import ca.dklink750.impetus.Practice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class TimerManager {
    private final HashMap<Player, Timer> timers;
    private final Database db;
    private final Impetus plugin;
    private final Practice practice;

    public TimerManager(Impetus plugin, Database db) {
        this.plugin = plugin;
        this.practice = new Practice(db, this);
        this.db = db;
        timers = new HashMap<>();
    }

    public void start(Player player) {
        start(player.getUniqueId(), player.getWorld().getUID());
    }

    public void start(UUID playerId, UUID saveWorld) {


        Player player = Bukkit.getPlayer(playerId);
        stop(playerId, saveWorld);
        if(practice.hasLocation(player.getUniqueId(), player.getWorld().getUID())) {
            Timer timer = new Timer(plugin, player, getCurrentAttempts(player.getUniqueId(), player.getWorld().getUID()), getCurrentTimeElapsed(player.getUniqueId(), player.getWorld().getUID()), displayTimer(player));
            timer.start();
            timers.put(player, timer);
        }
    }

    public void stop(UUID playerId, UUID saveWorldId) {
        Player player = Bukkit.getPlayer(playerId);
        if(hasTimer(player)) {
            saveData(playerId, saveWorldId);
            getTimer(player).end();
            timers.remove(player);
        }
    }

    public Timer getTimer(Player player) {
        return timers.get(player);
    }

    public boolean hasTimer(Player player) {
        return getTimer(player) != null;
    }

    private void saveData(UUID playerId, UUID saveWorldId) {
        Player player = Bukkit.getPlayer(playerId);
        if(hasTimer(player)) {
            persistAttempts(playerId, saveWorldId, getTimer(player).getAttempts());
            persistTimeElapsed(playerId, saveWorldId, getTimer(player).getTimeElapsed());
        }
    }

    private void persistAttempts(UUID player, UUID world, int attempts) {
        final String query = "UPDATE impetus_player_prac_locations " +
                "SET attempts = ? " +
                "WHERE player_uuid = ? " +
                "AND location_id = ? " +
                "AND current_location = true;";
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, attempts);
            stmt.setString(2, player.toString());
            stmt.setString(3, practice.getPracticeUUID(player, world).toString());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void persistTimeElapsed(UUID player, UUID world, double timeElapsed) {
        final String query = "UPDATE impetus_player_prac_locations " +
                "SET time_elapsed = ? " +
                "WHERE player_uuid = ? " +
                "AND location_id = ? " +
                "AND current_location = true;";
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, timeElapsed);
            stmt.setString(2, player.toString());
            stmt.setString(3, practice.getPracticeUUID(player, world).toString());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private int getCurrentAttempts(UUID player, UUID world) {
        final String query = "SELECT attempts " +
                "FROM impetus_player_prac_locations " +
                "JOIN impetus_locations ON impetus_locations.uuid = impetus_player_prac_locations.location_id " +
                "WHERE impetus_player_prac_locations.player_uuid = ? " +
                "AND impetus_locations.world = ? " +
                "AND impetus_player_prac_locations.current_location = true;";

        int currentAttempts = 0;
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.toString());
            stmt.setString(2, world.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentAttempts = rs.getInt("attempts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currentAttempts;
    }

    private double getCurrentTimeElapsed(UUID player, UUID world) {
        final String query = "SELECT time_elapsed " +
                "FROM impetus_player_prac_locations " +
                "JOIN impetus_locations " +
                "ON impetus_locations.uuid = impetus_player_prac_locations.location_id " +
                "WHERE impetus_player_prac_locations.player_uuid = ? " +
                "AND impetus_locations.world = ? " +
                "AND impetus_player_prac_locations.current_location = true;";

        double timeElapsed = 0.0F;
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.toString());
            stmt.setString(2, world.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                timeElapsed = rs.getDouble("time_elapsed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timeElapsed;
    }

    public void incrementAttemptsFor(Player player) {
        if(hasTimer(player)) {
            getTimer(player).incrementAttempts();
        }
    }

    private boolean displayTimer(Player player) {
        final String query = "SELECT show_timer " +
                "FROM impetus_player_settings " +
                "WHERE uuid = ?;";
        boolean result = false;
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                result = rs.getBoolean("show_timer");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean toggleDisplayTimer(Player player) {
        boolean display = !displayTimer(player);
        persistDisplaySetting(player, display);
        if(hasTimer(player)) {
            getTimer(player).setDisplay(display);
        }
        return display;
    }

    private void persistDisplaySetting(Player player, boolean display) {
        final String query = "UPDATE impetus_player_settings " +
                "SET show_timer = ? " +
                "WHERE uuid=?;";
        try(Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, display);
            stmt.setString(2, player.getUniqueId().toString());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean resetTimer(Player player) {
        boolean result = false;
        if(hasTimer(player)) {
            result = true;
            getTimer(player).setAttempts(0);
            getTimer(player).setTimeElapsed(0);
        }
        return result;
    }

    public boolean togglePause(Player player) {
        boolean result = false;
        if(hasTimer(player)) {
            result = true;
            getTimer(player).setPause(!getTimer(player).isPaused());
        }
        return result;
    }

}