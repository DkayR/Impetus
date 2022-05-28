package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.PracLocation;
import ca.dklink750.impetus.utils.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnPlayerMoveEvent implements org.bukkit.event.Listener {

    private final ConfigManager configManager;
    private final PracLocation pracLocation;

    public OnPlayerMoveEvent(ConfigManager configManager, PracLocation pracLocation) {
        this.configManager = configManager;
        this.pracLocation = pracLocation;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(configManager.getTeleportWhenInVoid() && event.getTo().getY() < 0) {
            Player player = event.getPlayer();
            if(pracLocation.hasCurrentLocationInWorld(player, player.getWorld())) {
                player.teleport(pracLocation.getCurrentPracticeLocation(player.getUniqueId(), player.getWorld()));
            }
        }
    }
}
