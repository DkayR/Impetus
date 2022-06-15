package ca.dklink750.impetus.utils;

import ca.dklink750.impetus.Impetus;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Timer {
    // In ticks
    private double timeElapsed;
    private int attempts;

    final private Impetus plugin;
    final private Player player;
    private boolean display;
    private boolean paused;
    BukkitTask task;

    public Timer(Impetus plugin, Player player, int attempts, double timeElapsed, boolean display) {
        this.plugin = plugin;
        this.player = player;
        this.attempts = attempts;
        this.timeElapsed = timeElapsed;
        this.display = display;
        paused = false;
    }

    public void start() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if(display) {
                    sendActionBar();
                }
                if(!paused) {
                    timeElapsed++;
                }
            }
        }.runTaskTimer(plugin, 0, 0);
    }

    private void sendActionBar() {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(ChatColor.GRAY + "Time Elapsed: " + ChatColor.AQUA + getFormattedTime(timeElapsed) + ChatColor.GRAY + " - " + "Attempts: " + ChatColor.AQUA + attempts), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private String getFormattedTime(double timeElapsed) {
        double totalSeconds = timeElapsed / 20;
        double seconds = totalSeconds % 60;
        double minutes = (totalSeconds - seconds) % (60 * 60) / 60;
        double hours = (totalSeconds - seconds - (minutes * 60)) % (60 * 60 * 60) / (60 * 60);
        return String.format("%02.0f:%02.0f:%02.0f", hours, minutes, seconds);
    }

    public void end() {
        task.cancel();
    }

    public double getTimeElapsed() {
        return timeElapsed;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPause(boolean pause) {
        this.paused = pause;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public void setTimeElapsed(int timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public void incrementAttempts() {
        if(!paused){
            attempts++;
        }
    }
}
