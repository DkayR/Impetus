package ca.dklink750.impetus.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerInventory {
    private final HeldItemUtil heldItemUtil;
    public PlayerInventory() {
        heldItemUtil = new HeldItemUtil();
    }
    public Boolean inventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public void removePracticeTool(Player player) {
        for(ItemStack item : player.getInventory().getContents()) {
            if(isPracticeTool(item)) {
                player.getInventory().remove(item);
            }
        }
    }

    public boolean containsPracticeTool(Player player) {
        boolean result = false;
        for(ItemStack item : player.getInventory().getContents()) {
            if(isPracticeTool(item)) {
                result = true;
            }
        }
        return result;
    }

    public boolean isPracticeTool(ItemStack item) {
        return item != null && item.getType() == Material.SLIME_BALL && heldItemUtil.isPracticeTool(item);
    }
}
