package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.ActivatorBlock;
import ca.dklink750.impetus.ActivatorLocation;
import ca.dklink750.impetus.utils.ConfigManager;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPhysicsEvent;

public class OnBlockPhysicsEvent implements org.bukkit.event.Listener {
    private final ActivatorBlock activatorBlock;
    private final ConfigManager config;
    public OnBlockPhysicsEvent(ActivatorBlock activatorBlock, ConfigManager config) {
        this.activatorBlock = activatorBlock;
        this.config = config;
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        World world = block.getWorld();
        ActivatorLocation activator = new ActivatorLocation(world, block.getX(), block.getY(), block.getZ());
        if(activatorBlock.isActivatorBlockFromLoc(activator)) {
            // Hacky method, potentially find other solution
            if(config.getDestroyActivatorsOnPhysics() && block.getRelative(BlockFace.DOWN).isEmpty()) {
                activatorBlock.deleteActivatorBlockData(activator);
            } else {
                event.setCancelled(true);
            }
        }
    }
}
