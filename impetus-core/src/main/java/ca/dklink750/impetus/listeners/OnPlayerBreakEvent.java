package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.ActivatorBlock;
import ca.dklink750.impetus.ActivatorLocation;
import ca.dklink750.impetus.utils.ConfigManager;
import ca.dklink750.impetus.utils.HeldItemUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class OnPlayerBreakEvent implements org.bukkit.event.Listener {
    private final ConfigManager configManager;
    private final ActivatorBlock activatorBlock;

    public OnPlayerBreakEvent(ConfigManager configManager, ActivatorBlock activatorBlock) {
        this.configManager = configManager;
        this.activatorBlock = activatorBlock;
    }

    // TODO: Implement some sort of clickable/hoverable chat message class (only one use case at the moment)
    private TextComponent createActivatorDestroyWarnMsg() {
        TextComponent activatorDestroyWarn = new TextComponent("You can only destroy activator blocks with the ");
        activatorDestroyWarn.setColor(net.md_5.bungee.api.ChatColor.RED);
        TextComponent parkourToolClickable = new TextComponent("[Parkour Tool]");
        parkourToolClickable.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        parkourToolClickable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pktool"));
        parkourToolClickable.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/pktool").create()));
        activatorDestroyWarn.addExtra(parkourToolClickable);

        return activatorDestroyWarn;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        boolean cancelDeletionOfActivator = false;
        HeldItemUtil holding = new HeldItemUtil();
        Block block = event.getBlock();
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();
        ActivatorLocation blockLocation = new ActivatorLocation(player.getWorld(), block.getX(), block.getY(), block.getZ());
        boolean isActivatorBlock = activatorBlock.isActivatorBlockFromLoc(blockLocation);

        if(holding.isHoldingItem(player)) {
            // Stops destroying blocks while holding tool (depends on config)
            if((!configManager.getDestroyBlocksWhileHoldingPracticeTool() && holding.isPracticeTool(itemInHand))) {
                event.setCancelled(true);
            }

            // Stops deletion of activator block when not holding pktool and config does not override
            if(isActivatorBlock) {
                if(!holding.isPkTool(itemInHand) && !configManager.getDestroyActivatorBlocksWithoutPkTool()) {
                    player.spigot().sendMessage(createActivatorDestroyWarnMsg());
                    cancelDeletionOfActivator = true;
                    event.setCancelled(true);
                }
            }
        }
        // Catches empty hand scenario
        else {
            if(isActivatorBlock && !configManager.getDestroyActivatorBlocksWithoutPkTool()) {
                player.spigot().sendMessage(createActivatorDestroyWarnMsg());
                cancelDeletionOfActivator = true;
                event.setCancelled(true);
            }
        }

        if(isActivatorBlock && !cancelDeletionOfActivator) {
            activatorBlock.deleteActivatorBlockData(blockLocation);
        }
    }
}
