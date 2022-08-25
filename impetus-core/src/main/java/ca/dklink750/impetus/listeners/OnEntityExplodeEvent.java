package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.ActivatorBlock;
import ca.dklink750.impetus.ActivatorLocation;
import ca.dklink750.impetus.utils.ConfigManager;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import java.util.ArrayList;

public class OnEntityExplodeEvent implements org.bukkit.event.Listener {
    private final ActivatorBlock activatorBlock;
    private final ConfigManager config;
    public OnEntityExplodeEvent(ActivatorBlock activatorBlock, ConfigManager config) {
        this.activatorBlock = activatorBlock;
        this.config = config;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        ArrayList<Block> blocksToKeep = new ArrayList<>();
        for(Block block : event.blockList()) {
            ActivatorLocation activator = new ActivatorLocation(block.getWorld(), block.getX(), block.getY(), block.getZ());
            if(activatorBlock.isActivatorBlockFromLoc(activator)) {
                if(config.getDestroyActivatorsOnExplode()) {
                    activatorBlock.deleteActivatorBlockData(activator);
                } else {
                    blocksToKeep.add(block);
                }
            }
        }
        event.blockList().removeAll(blocksToKeep);
    }
}
