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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class Prac implements CommandExecutor {
    final private PracLocation myPracLocations;
    final private Database database;
    final private CustomItem practiceTool = new CustomItem(Material.SLIME_BALL, ChatColor.GREEN + "Return", Arrays.asList("Right click: Return to current practice location", "Left click: Cycle through practice locations", "Drop: Create new practice location"), "prac", "prac");

    public Prac(PracLocation pracLocations, Database database) {
        this.myPracLocations = pracLocations;
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            World currentWorld = player.getWorld();

            // Hack to not give item twice when calling via drop event
            if (args.length > 0 && args[0].equals("noitem")) {
                    pracAtCurrentLocation(player);
            }
            else {
                if (practiceTool.giveCustomItemToPlayer(player, ChatColor.RED + "Inventory full, cannot practice!")) {
                    if (args.length > 0) {
                        User userToCheck = new User(database);
                        if (userToCheck.playerExists(args[0])) {
                            UUID otherPlayer = userToCheck.getPlayerUUIDFromUserName(args[0]);
                            if (myPracLocations.containsPlayer(otherPlayer, currentWorld.getUID())) {
                                if (args.length > 1 && args[1].equals("all")) {
                                    copyAllPracLocationsFromOtherPlayer(player, otherPlayer, args[0]);
                                } else {
                                    copyCurrentPracLocationFromOtherPlayer(player, otherPlayer, args[0]);
                                }
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
        return true;
    }

    private void pracAtCurrentLocation(Player player) {
        if (!this.myPracLocations.containsPlayer(player.getUniqueId(), player.getWorld().getUID())) {
            player.sendMessage(ChatColor.GREEN + "You are now in practice mode!");
        } else {
            player.sendMessage(ChatColor.GREEN + "Added new practice checkpoint!");
        }
        myPracLocations.registerNewLocation(player, player.getLocation(), PracLocationType.ADHOC);
    }

    private void copyCurrentPracLocationFromOtherPlayer(Player player, UUID otherPlayer, String otherPlayerDisplayName) {
        if (myPracLocations.associateLocation(player, myPracLocations.getCurrentWorldPracLocationUUID(otherPlayer, player.getWorld()), PracLocationType.ADHOC)) {
            player.sendMessage(ChatColor.GREEN + "Successfully copied " + otherPlayerDisplayName + "'s current practice location!");
        } else {
            player.sendMessage(ChatColor.RED + "You already have " + otherPlayerDisplayName + "'s current location!");
        }
    }

    private void copyAllPracLocationsFromOtherPlayer(Player player, UUID otherPlayer, String otherPlayerDisplayName) {
        ArrayList<String> currentWorldPracticeLocationUUIDs = myPracLocations.getWorldPracticeLocationsUUID(otherPlayer, player.getWorld().getUID());
        boolean copiedALocation = false;
        for (String currentWorldPracticeLocationUUID : currentWorldPracticeLocationUUIDs) {
            if (myPracLocations.associateLocation(player, currentWorldPracticeLocationUUID, PracLocationType.ADHOC)) {
                copiedALocation = true;
            }
        }
        if (copiedALocation) {
            player.sendMessage(ChatColor.GREEN + "Successfully copied all of " + otherPlayerDisplayName + "'s current practice locations!");
        } else {
            player.sendMessage(ChatColor.RED + "You already have all of " + otherPlayerDisplayName + "'s locations!");
        }
    }
}