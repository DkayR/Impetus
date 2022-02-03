package ca.dklink750.impetus.utils;

import org.bukkit.entity.Player;

public class PlayerInventory {
    public Boolean inventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }
}
