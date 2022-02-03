package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.PracLocation;
import ca.dklink750.impetus.PracStats;
import ca.dklink750.impetus.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class OnPlayerJoinEvent implements org.bukkit.event.Listener {
    private final User user;
    private final PracLocation pracLocation;
    private final PracStats pracStats;
    private final boolean displayTimer;

    public OnPlayerJoinEvent(User user, PracLocation pracLocation, boolean displayTimer) {
        this.user = user;
        this.pracLocation = pracLocation;
        this.pracStats = pracLocation.getPracStats();
        this.displayTimer = displayTimer;
    }

    // Stores player UUID, display name, and resumes practice timer if they have one
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        user.storeUUID(uuid);
        user.setBaseDisplayName(player.getDisplayName(), uuid);

        if(pracLocation.hasCurrentLocationInWorld(player, player.getWorld()) && displayTimer) {
            pracStats.displayStats(player);
        }
    }
}
