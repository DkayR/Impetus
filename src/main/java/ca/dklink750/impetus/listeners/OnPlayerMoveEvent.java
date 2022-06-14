package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.Practice;
import ca.dklink750.impetus.utils.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnPlayerMoveEvent implements org.bukkit.event.Listener {

    private final ConfigManager configManager;
    private final Practice practice;

    public OnPlayerMoveEvent(ConfigManager configManager, Practice practice) {
        this.configManager = configManager;
        this.practice = practice;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(configManager.getTeleportWhenInVoid() && event.getTo().getY() < 0) {
            Player player = event.getPlayer();
            if(practice.hasLocation(player.getUniqueId(), player.getWorld().getUID())) {
                player.teleport(practice.getPractice(player.getUniqueId(), player.getWorld().getUID()));
            }
        }
    }
}
