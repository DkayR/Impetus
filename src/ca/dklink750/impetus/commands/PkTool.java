package ca.dklink750.impetus.commands;

import ca.dklink750.impetus.utils.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PkTool implements CommandExecutor {
    final private CustomItem parkourTool = new CustomItem(Material.NETHER_STAR, ChatColor.AQUA + "Parkour Tool", ChatColor.GRAY + "Right click blocks in the world to customize", "pktool", "pktool");

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            parkourTool.giveCustomItemToPlayer(player, ChatColor.RED + "Inventory full, cannot get parkour tool!", ChatColor.RED + "You already have a parkour tool!");
        }
        return true;
    }
}
