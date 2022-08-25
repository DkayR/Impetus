package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.Practice;
import ca.dklink750.impetus.User;
import ca.dklink750.impetus.utils.ConfigManager;
import ca.dklink750.impetus.utils.TimerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoinEvent implements org.bukkit.event.Listener {
    private final User user;
    private final Practice practice;
    private final TimerManager timer;
    private final ConfigManager configManager;

    public OnPlayerJoinEvent(User user, Practice practice, ConfigManager configManager) {
        this.user = user;
        this.practice = practice;
        this.timer = practice.getTimer();
        this.configManager = configManager;
    }

    // Stores player UUID, display name, and resumes practice timer if they have one
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        user.storeData(player);

        if(practice.hasLocation(player.getUniqueId(), player.getWorld().getUID())) {
            timer.start(player);
        }
    }
}
