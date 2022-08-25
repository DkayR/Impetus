package ca.dklink750.impetus.commands;

import ca.dklink750.impetus.*;
import ca.dklink750.impetus.utils.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.UUID;

public class Prac implements CommandExecutor {
    final private Practice practice;
    final private Database database;
    final private CustomItem practiceTool = new CustomItem(Material.SLIME_BALL, ChatColor.GREEN + "Return", Arrays.asList("Right click: Return to current practice location", "Left click: Cycle through practice locations", "Drop: Create new practice location"), "prac", "prac");

    public Prac(Practice pracLocations, Database database) {
        this.practice = pracLocations;
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("Impetus.prac")) {
                World currentWorld = player.getWorld();

                // Hack to not give item twice when calling via drop event
                if (args.length > 0 && args[0].equals("noitem")) {
                    pracAtCurrentLocation(player);
                } else {
                    if (practiceTool.giveCustomItemToPlayer(player, ChatColor.RED + "Inventory full, cannot practice!")) {
                        if (args.length > 0) {
                            User userToCheck = new User(database);
                            if (userToCheck.playerExists(args[0])) {
                                UUID otherPlayer = userToCheck.getPlayerUUIDFromUserName(args[0]);
                                if (practice.hasLocation(otherPlayer, currentWorld.getUID())) {
                                    copyCurrentPracLocationFromOtherPlayer(player, otherPlayer, args[0]);
                                } else {
                                    player.sendMessage(ChatColor.RED + args[0] + " does not have a practice location in this world!");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + args[0] + " has never joined the server!");
                            }
                        } else {
                            pracAtCurrentLocation(player);
                        }
                    }
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
        }
        return true;
    }

    private void pracAtCurrentLocation(Player player) {
        practice.practice(player.getLocation(), player.getUniqueId(), player.getWorld().getUID());
        player.sendMessage(ChatColor.GREEN + "You are now in practice mode!");
    }

    private void copyCurrentPracLocationFromOtherPlayer(Player player, UUID otherPlayer, String otherPlayerDisplayName) {
        if(practice.hasLocation(otherPlayer, player.getWorld().getUID())) {
            if(practice.hasThisLocation(player.getUniqueId(), practice.getPracticeUUID(otherPlayer, player.getWorld().getUID()))) {
                player.sendMessage(ChatColor.RED + "You already have this location!");
            } else if(practiceTool.giveCustomItemToPlayer(player, ChatColor.RED + "Inventory full, cannot practice!")){
                practice.persistLocationFor(player.getUniqueId(), practice.getPracticeUUID(otherPlayer, player.getWorld().getUID()), PracLocationType.ADHOC);
                player.sendMessage(ChatColor.GREEN + "Successfully copied " + otherPlayerDisplayName + "'s practice location!");
            }
        }
    }

}