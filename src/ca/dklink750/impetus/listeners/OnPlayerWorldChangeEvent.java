package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.PracLocation;
import ca.dklink750.impetus.PracStats;
import ca.dklink750.impetus.utils.ConfigManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class OnPlayerWorldChangeEvent implements org.bukkit.event.Listener {
    private final PracLocation pracLocation;
    private final PracStats pracStats;
    private final ConfigManager configManager;

    public OnPlayerWorldChangeEvent(PracLocation pracLocation, ConfigManager configManager) {
        this.pracLocation = pracLocation;
        this.pracStats = pracLocation.getPracStats();
        this.configManager = configManager;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World worldFrom = event.getFrom();
        World worldTo = player.getWorld();

        if(!pracLocation.hasCurrentLocationInWorld(player, worldFrom) && pracLocation.hasCurrentLocationInWorld(player, worldTo) && configManager.getDisplayTimer()) {
            pracStats.displayStats(player);
        }

    }
}
