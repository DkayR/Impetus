package ca.dklink750.impetus.listeners;

import ca.dklink750.impetus.*;
import ca.dklink750.impetus.utils.ConfigManager;
import ca.dklink750.impetus.utils.CustomItem;
import ca.dklink750.impetus.utils.HeldItemUtil;
import ca.dklink750.impetus.utils.TimerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class OnPlayerInteractEvent implements org.bukkit.event.Listener {
    private final ActivatorBlock activatorBlock;
    private final Practice practice;
    private final ConfigManager configManager;
    private final TimerManager timer;
    private Long lastSystemTime = 0L;


    public OnPlayerInteractEvent(ActivatorBlock activatorBlock, Practice practice, ConfigManager configManager, TimerManager timer) {
        this.activatorBlock = activatorBlock;
        this.practice = practice;
        this.configManager = configManager;
        this.timer = timer;
    }

    // Checks if a block is interactable
    private boolean isInteractable(Material block) {
        return configManager.getIneractablesToCancel().contains(block);
    }

    // Checks if a block is an accepted activator block
    private boolean isActivatorBlockType(Material block) {
        return configManager.getActivatorBlockTypes().contains(block);
    }

    // Hacky method: Checks if player double triggered interact event (Stops double interactions from things like cobblestone walls, crafting tables, ect)
    private boolean doubleFire() {
        boolean doubleFire = System.currentTimeMillis() - lastSystemTime < 5;
        lastSystemTime = System.currentTimeMillis();
        return doubleFire;
    }

    // Triggers effect set upon a player triggering an activator block
    private void executeActivatorBlock(Block block, World world, Player player) {
        ActivatorLocation activatorLocation = new ActivatorLocation(world, block.getX(), block.getY(), block.getZ());
        if(activatorBlock.isActivatorBlockFromLoc(activatorLocation)) {
            activatorBlock.executeEffects(player, activatorBlock.getActivatorBlockUUID(activatorLocation));
        }
    }

    // Gives coord setter to player for the given activator UUID
    private void givePreciseCoordSetter(UUID activatorBlockUUID, Player player) {
        CustomItem preciseCoordsEmerald = new CustomItem(Material.EMERALD, ChatColor.GREEN + "Set Precise Coordinates", activatorBlockUUID.toString(), "coordsetter", "coordsetter");
        preciseCoordsEmerald.giveCustomItemToPlayer(player, ChatColor.RED + "Cannot give precise coords emerald, inventory full!", ChatColor.RED + "You already have a precise coords emerald!");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!doubleFire()) {
            Player player = event.getPlayer();
            World world = player.getWorld();
            Block block = event.getClickedBlock();
            Action action = event.getAction();
            boolean leftClickedBlock = action.equals(Action.LEFT_CLICK_BLOCK);
            boolean rightClickedBlock = action.equals(Action.RIGHT_CLICK_BLOCK);
            boolean leftClickedAir = action.equals(Action.LEFT_CLICK_AIR);
            boolean rightClickedAir = action.equals(Action.RIGHT_CLICK_AIR);
            boolean steppedOnPressurePlate = action.equals(Action.PHYSICAL);
            HeldItemUtil holding = new HeldItemUtil();

            // Stepped on pressure plate
            if(steppedOnPressurePlate) {
                executeActivatorBlock(block, world, player);
            }

            // Checks if player does not have an item hand
            if(holding.isHoldingItem(player)) {
                ItemStack itemInHand = player.getItemInHand();

                if(holding.isPracticeTool(itemInHand)) {
                    // Cancel interaction with config blocks if player is holding practice tool
                    if(rightClickedBlock && isInteractable(block.getType())) {
                        player.closeInventory();
                        event.setCancelled(true);
                    }
                    // Teleport player to current practice location if exists on right clicking while holding practice tool
                    if((rightClickedAir || rightClickedBlock) && practice.hasLocation(player.getUniqueId(), world.getUID())) {
                        timer.incrementAttemptsFor(player);
                        player.teleport(practice.getPractice(player.getUniqueId(), world.getUID()));
                    }
                }
                // Associates defined location with activator block on right click with the coord setter
                else if((rightClickedBlock || rightClickedAir) && holding.isCoordSetter(itemInHand)) {
                    String activatorBlockUUID = ChatColor.stripColor(player.getItemInHand().getItemMeta().getLore().get(0));

                    // Creates blank "list of effects" associated with activator block if it does not exist and adds teleport to the set location as an effect
                    if(activatorBlock.isActivatorBlockFromUUID(activatorBlockUUID)) {
                        UUID currentLocUUID = practice.persistLocation(player.getLocation());
                        String effectSetUUID = activatorBlock.hasEffectSet(activatorBlockUUID) ? activatorBlock.getEffectSetUUIDFromActivatorUUID(activatorBlockUUID) : activatorBlock.createEffectSetAndAddToActivator(activatorBlockUUID).toString();

                        // Temporary deletion to only allow a single effect (only teleport for now) on an activator block
                        activatorBlock.removeTeleportEffectsFromEffectSet(effectSetUUID);
                        activatorBlock.addTeleportEffectToEffectSet(effectSetUUID, currentLocUUID, TeleportType.DEFINED);
                        holding.removeItemInHand(player);
                        player.sendMessage(ChatColor.GREEN + "Successfully setup precise coordinates!");
                    }
                    else {
                        holding.removeItemInHand(player);
                        player.sendMessage(ChatColor.RED + "Activator block no longer exists!");
                    }
                }
                // Handles setting up activator blocks on right clicking blocks with the parkour tool (TODO: flesh out parkour to open gui ect)
                else if(rightClickedBlock && holding.isPkTool(itemInHand)) {
                    if(isActivatorBlockType(block.getType())) {
                        ActivatorLocation selectedBlock = new ActivatorLocation(world, block.getX(), block.getY(), block.getZ());

                        // Give emerald to replace coords for existing activator
                        if(activatorBlock.isActivatorBlockFromLoc(selectedBlock)) {
                            player.sendMessage(ChatColor.BLUE + "This is already an activator block, giving precise coords setter!");
                            givePreciseCoordSetter(activatorBlock.getActivatorBlockUUID(selectedBlock), player);
                        }
                        else {
                            // A block must be right clicked twice with the parkour tool to create an activator block (i.e. reasoning for registration and clearing)
                            if(activatorBlock.playerSelectsDifferentBlock(player.getUniqueId(), selectedBlock)) {
                                activatorBlock.clearRegistration(player.getUniqueId());
                            }
                            // Checks if selected block with parkour tool has already been right clicked once by that player
                            if(activatorBlock.isRegistered(player.getUniqueId(), selectedBlock)) {
                                player.sendMessage(ChatColor.GREEN + "Giving precise coords setter!");
                                givePreciseCoordSetter(activatorBlock.createActivatorBlock(selectedBlock), player);
                            }
                            // Register first click of activator block in memory
                            else {
                                player.sendMessage(ChatColor.GREEN + "Click the block again to confirm!");
                                activatorBlock.register(player.getUniqueId(), selectedBlock);
                            }
                        }
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "Sorry, this block type is not currently supported.");
                    }
                }
            }
        }
    }

}
