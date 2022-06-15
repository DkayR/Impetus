package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.ActivatorBlock;
import ca.dklink750.impetus.ActivatorLocation;
import ca.dklink750.impetus.utils.ConfigManager;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class OnBlockPistonExtendEvent implements org.bukkit.event.Listener {

    private final ActivatorBlock activatorBlock;
    private final ConfigManager config;

    public OnBlockPistonExtendEvent(ActivatorBlock activatorBlock, ConfigManager config) {
        this.activatorBlock = activatorBlock;
        this.config = config;
    }
    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        Block piston = event.getBlock();
        Block toPush = piston.getRelative(event.getDirection());
        if(!toPush.isEmpty()) {
            ActivatorLocation activator = new ActivatorLocation(toPush.getWorld(), toPush.getX(), toPush.getY(), toPush.getZ());
            if(activatorBlock.isActivatorBlockFromLoc(activator)) {
                if(config.getDestroyActivatorsOnPistonExtend()) {
                    activatorBlock.deleteActivatorBlockData(activator);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
