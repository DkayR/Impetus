package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.utils.HeldItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class OnPlayerDropEvent implements org.bukkit.event.Listener {
    private final boolean practiceOnDrop;
    private final boolean makePluginItemsDroppable;

    public OnPlayerDropEvent(boolean practiceOnDrop, boolean makePluginItemsDroppable) {
        this.practiceOnDrop = practiceOnDrop;
        this.makePluginItemsDroppable = makePluginItemsDroppable;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        // Stops plugin related items from being dropped if not false
        if(!makePluginItemsDroppable) {
            Player player = event.getPlayer();
            ItemStack item = event.getItemDrop().getItemStack();
            HeldItemUtil holding = new HeldItemUtil();

            if(item != null) {
                if(holding.isPracticeTool(item) || holding.isPkTool(item) || holding.isCoordSetter(item)) {
                    event.setCancelled(true);
                    if(practiceOnDrop && holding.isPracticeTool(item)) {
                        // Practices without giving you a practice tool (if you can drop the tool you already have one)
                        player.performCommand("prac noitem");
                    }
                }
            }
        }
    }
}
