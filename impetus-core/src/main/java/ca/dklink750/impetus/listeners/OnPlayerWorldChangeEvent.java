package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.Practice;
import ca.dklink750.impetus.utils.ConfigManager;
import ca.dklink750.impetus.utils.CustomItem;
import ca.dklink750.impetus.utils.PlayerInventory;
import ca.dklink750.impetus.utils.TimerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.Arrays;

public class OnPlayerWorldChangeEvent implements org.bukkit.event.Listener {
    private final Practice practice;
    private final TimerManager timer;
    private final ConfigManager configManager;
    private final PlayerInventory inv;
    final private CustomItem practiceTool = new CustomItem(Material.SLIME_BALL, ChatColor.GREEN + "Return", Arrays.asList("Right click: Return to current practice location", "Left click: Cycle through practice locations", "Drop: Create new practice location"), "prac", "prac");

    public OnPlayerWorldChangeEvent(Practice practice, ConfigManager configManager) {
        this.practice = practice;
        this.timer = practice.getTimer();
        this.configManager = configManager;
        this.inv = new PlayerInventory();
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        // Give practice tool if player does not have and has location in new world
        if(!practice.hasLocation(player.getUniqueId(), event.getFrom().getUID()) && practice.hasLocation(player.getUniqueId(), player.getWorld().getUID())) {
            if(!inv.containsPracticeTool(player)) {
                practiceTool.giveCustomItemToPlayer(player, ChatColor.RED + "Inventory full, cannot give practice tool!");
            }
        } else if(practice.hasLocation(player.getUniqueId(), event.getFrom().getUID()) && !practice.hasLocation(player.getUniqueId(), player.getWorld().getUID())) {
            inv.removePracticeTool(player);
        }

        if(configManager.getDisplayTimer()) {
            timer.start(event.getPlayer().getUniqueId(), event.getFrom().getUID());
        }
    }
}
