package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.utils.TimerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuitEvent implements org.bukkit.event.Listener {
    private final TimerManager timer;
    public OnPlayerQuitEvent(TimerManager timer) {
        this.timer = timer;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(timer.hasTimer(player)) {
            timer.stop(player.getUniqueId(), player.getWorld().getUID());
        }
    }
}
