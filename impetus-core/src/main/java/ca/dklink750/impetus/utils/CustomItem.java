package ca.dklink750.impetus.utils;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomItem {
    final private PlayerInventory playerInventory = new PlayerInventory();
    final private Material itemMat;
    final private String displayName;
    final private Integer amount;
    final private String nbtTag;
    final private String nbtData;
    final private List<String> loreList;
    private ItemStack customItem;

    public CustomItem(Material itemMat, String displayName, int amount, List<String> lore, String nbtTag, String nbtData) {
        this.itemMat = itemMat;
        this.displayName = displayName;
        this.amount = amount;
        this.loreList = lore;
        this.nbtTag = nbtTag;
        this.nbtData = nbtData;
        this.setItemStackData();
    }

    public CustomItem(Material itemMat, String displayName, String lore, String nbtTag, String nbtData) {
        this(itemMat, displayName, 1, Collections.singletonList(lore), nbtTag, nbtData);
    }

    public CustomItem(Material itemMat, String displayName, List<String> lore, String nbtTag, String nbtData) {
        this(itemMat, displayName, 1, lore, nbtTag, nbtData);
    }

    private void setItemStackData() {
        customItem = new ItemStack(itemMat, amount);
        ItemMeta itemMeta = customItem.getItemMeta();
        if (!displayName.isEmpty()) {
            itemMeta.setDisplayName(displayName);
        }
        if (loreList != null && !loreList.isEmpty()) {
            itemMeta.setLore(loreList);
        }
        this.customItem.setItemMeta(itemMeta);
        if (!nbtTag.isEmpty() && !nbtData.isEmpty()) {
            this.customItem = NBTEditor.set(customItem, nbtTag, nbtData);
        }
    }

    public boolean giveCustomItemToPlayer(Player currentPlayer, String invFullMsg, String alreadyHasItemMsg) {
        boolean gavePlayerItem = true;
        boolean invFull = this.playerInventory.inventoryFull(currentPlayer);
        if (!currentPlayer.getInventory().contains(customItem)) {
            if (!invFull) {
                currentPlayer.getInventory().addItem(customItem);
            } else {
                currentPlayer.sendMessage(invFullMsg);
                gavePlayerItem = false;
            }
        } else {
            currentPlayer.sendMessage(alreadyHasItemMsg);
            gavePlayerItem = false;
        }
        return gavePlayerItem;
    }

    public boolean giveCustomItemToPlayer(Player currentPlayer, String invFullMsg) {
        boolean gavePlayerItem = true;
        boolean invFull = this.playerInventory.inventoryFull(currentPlayer);
        if (!currentPlayer.getInventory().contains(customItem)) {
            if (!invFull) {
                currentPlayer.getInventory().addItem(customItem);
            } else {
                currentPlayer.sendMessage(invFullMsg);
                gavePlayerItem = false;
            }
        }
        return gavePlayerItem;
    }
}
