package ca.dklink750.impetus.utils;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeldItemUtil {
    // Checks if player is holding the practice tool
    public boolean isPracticeTool(ItemStack heldItem) {
        net.minecraft.server.v1_8_R3.ItemStack nmsVer = CraftItemStack.asNMSCopy(heldItem);
        NBTTagCompound compound = (nmsVer.hasTag()) ? nmsVer.getTag() : new NBTTagCompound();
        return "prac".equals(compound.getString("prac"));
    }

    // Checks if player is holding the parkour tool
    public boolean isPkTool(ItemStack heldItem) {
        net.minecraft.server.v1_8_R3.ItemStack nmsVer = CraftItemStack.asNMSCopy(heldItem);
        NBTTagCompound compound = (nmsVer.hasTag()) ? nmsVer.getTag() : new NBTTagCompound();

        return "pktool".equals(compound.getString("pktool"));
    }

    // Checks if player is holding setter emerald
    public boolean isCoordSetter(ItemStack heldItem) {
        net.minecraft.server.v1_8_R3.ItemStack nmsVer = CraftItemStack.asNMSCopy(heldItem);
        NBTTagCompound compound = (nmsVer.hasTag()) ? nmsVer.getTag() : new NBTTagCompound();

        return "coordsetter".equals(compound.getString("coordsetter"));
    }

    public void removeItemInHand(Player player) {
        player.getInventory().setItemInHand(new ItemStack(Material.AIR));
    }

    public boolean isHoldingItem(Player player) {
        return (player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR));
    }

}
