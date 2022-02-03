package ca.dklink750.impetus.commands;

import ca.dklink750.impetus.PracLocation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Unprac implements CommandExecutor {
    final private PracLocation myPracLocations;

    public Unprac(PracLocation pracLocations) {
        this.myPracLocations = pracLocations;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (this.myPracLocations.containsPlayer(player.getUniqueId(), player.getWorld().getUID())) {
                if (args.length > 0 && args[0].equals("all")) {
                    this.myPracLocations.removeAllPracticeLocations(player.getUniqueId(), player.getWorld().getUID());
                } else {
                    this.myPracLocations.removeCurrentPracticeLocation(player.getUniqueId(), player.getWorld().getUID());
                }
                if (!this.myPracLocations.containsPlayer(player.getUniqueId(), player.getWorld().getUID())) {
                    player.getInventory().remove(Material.SLIME_BALL);
                    player.sendMessage(ChatColor.GREEN + "You are no longer in practice mode!");
                } else {
                    player.sendMessage(ChatColor.GREEN + "Removed practice location(s)!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You aren't in practice mode!");
            }
        }
        return true;
    }
}
