package ca.dklink750.impetus.commands;

import ca.dklink750.impetus.Practice;
import ca.dklink750.impetus.utils.PlayerInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Unprac implements CommandExecutor {
    final private Practice practice;
    final private PlayerInventory inv;

    public Unprac(Practice pracLocations) {
        this.practice = pracLocations;
        this.inv = new PlayerInventory();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("Impetus.unprac")) {
                if (practice.hasLocation(player.getUniqueId(), player.getWorld().getUID())) {
                    practice.unpractice(player.getUniqueId(), player.getWorld().getUID());
                    player.sendMessage(ChatColor.GREEN + "You are no longer in practice mode!");
                } else {
                    player.sendMessage(ChatColor.RED + "You aren't in practice mode!");
                }
                inv.removePracticeTool(player);
            }
            else {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
        }
        return true;
    }
}
