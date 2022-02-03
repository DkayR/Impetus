package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.PracLocation;
import ca.dklink750.impetus.PracStats;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class OnPlayerWorldChangeEvent implements org.bukkit.event.Listener {
    private final PracLocation pracLocation;
    private final PracStats pracStats;
    private final boolean displayTimer;

    public OnPlayerWorldChangeEvent(PracLocation pracLocation, boolean displayTimer) {
        this.pracLocation = pracLocation;
        this.pracStats = pracLocation.getPracStats();
        this.displayTimer = displayTimer;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World worldFrom = event.getFrom();
        World worldTo = player.getWorld();

        if(!pracLocation.hasCurrentLocationInWorld(player, worldFrom) && pracLocation.hasCurrentLocationInWorld(player, worldTo) && displayTimer) {
            pracStats.displayStats(player);
        }

    }
}
