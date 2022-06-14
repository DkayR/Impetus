package ca.dklink750.impetus.commands;

import ca.dklink750.impetus.utils.TimerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimerCommand implements CommandExecutor {

    private final TimerManager timer;

    public TimerCommand(TimerManager timer) {
        this.timer = timer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            if(player.hasPermission("Impetus.timer")) {
                if(args.length > 0) {
                    executeTimerCommand(player, args[0]);
                } else {
                    displayHelp(player);
                }
            } else {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
        }
        return true;
    }

    private void executeTimerCommand(Player player, String subCommand) {
        switch(subCommand.toUpperCase()) {
            case "TOGGLE":
                toggleTimer(player);
                break;
            case "RESET":
                resetTimer(player);
                break;
            case "PAUSE":
                togglePause(player);
                break;
            default:
                displayHelp(player);
        }
    }

    private void toggleTimer(Player player) {
        if(timer.toggleDisplayTimer(player)) {
            player.sendMessage(ChatColor.GREEN + "Showing timer!");
        } else {
            player.sendMessage(ChatColor.GREEN + "Hiding timer!");
        }
    }

    private void resetTimer(Player player) {
        if(timer.resetTimer(player)) {
            player.sendMessage(ChatColor.GREEN + "Timer reset!");
        } else {
            player.sendMessage(ChatColor.RED + "You do not have a timer to reset!");
        }
    }

    private void togglePause(Player player) {
        if(timer.togglePause(player)) {
            player.sendMessage(ChatColor.GREEN + "Toggling timer pause...");
        } else {
            player.sendMessage(ChatColor.RED + "You do not have a timer to pause!");
        }
    }

    private void displayHelp(Player player) {
        player.sendMessage(createHelpLine("toggle", "Hides or shows the timer (stats are still saved!)"));
        player.sendMessage(createHelpLine("reset", "Resets timer attempts and time elapsed"));
        player.sendMessage(createHelpLine("pause", "Stops or resumes the timer"));
    }



    private String createHelpLine(String subcommand, String description) {
        return ChatColor.YELLOW + "/timer " + subcommand + ChatColor.GRAY + " - " + ChatColor.AQUA + description;
    }
}
