package ca.dklink750.impetus.utils;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeldItemUtil {
    // Checks if player is holding the practice tool
    public boolean isPracticeTool(ItemStack heldItem) {
        return "prac".equals(NBTEditor.getString(heldItem, "prac"));
    }

    // Checks if player is holding the parkour tool
    public boolean isPkTool(ItemStack heldItem) {
        return "pktool".equals(NBTEditor.getString(heldItem, "pktool"));
    }

    // Checks if player is holding setter emerald
    public boolean isCoordSetter(ItemStack heldItem) {
        return "coordsetter".equals(NBTEditor.getString(heldItem, "coordsetter"));
    }

    public void removeItemInHand(Player player) {
        player.getInventory().setItemInHand(new ItemStack(Material.AIR));
    }

    public boolean isHoldingItem(Player player) {
        return (player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR));
    }

}
