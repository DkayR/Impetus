package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.ActivatorBlock;
import ca.dklink750.impetus.ActivatorLocation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPhysicsEvent;

public class OnBlockPhysicsEvent implements org.bukkit.event.Listener {
    private final ActivatorBlock activatorBlock;

    public OnBlockPhysicsEvent(ActivatorBlock activatorBlock) {
        this.activatorBlock = activatorBlock;
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        World world = block.getWorld();
        ActivatorLocation currentLocation = new ActivatorLocation(world, block.getX(), block.getY(), block.getZ());
        if(activatorBlock.isActivatorBlockFromLoc(currentLocation)) {
            event.setCancelled(true);
        }
    }
}
