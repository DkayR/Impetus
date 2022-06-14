package ca.dklink750.impetus.utils;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
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

    public CustomItem(Material itemMat, Integer amount, String displayName, String lore) {
        this.itemMat = itemMat;
        this.amount = amount;
        this.displayName = displayName;
        this.loreList = Arrays.asList(lore);
        this.nbtTag = "";
        this.nbtData = "";
        this.setItemStackData();
    }

    public CustomItem(Material itemMat, String displayName, String lore, String nbtTag, String nbtData) {
        this.itemMat = itemMat;
        this.displayName = displayName;
        this.loreList = Arrays.asList(lore);
        this.amount = 1;
        this.nbtTag = nbtTag;
        this.nbtData = nbtData;
        this.setItemStackData();
    }

    public CustomItem(Material itemMat, String displayName, List<String> lore, String nbtTag, String nbtData) {
        this.itemMat = itemMat;
        this.displayName = displayName;
        this.loreList = lore;
        this.amount = 1;
        this.nbtTag = nbtTag;
        this.nbtData = nbtData;
        this.setItemStackData();
    }

    public CustomItem(Material itemMat, String displayName, String lore) {
        this.itemMat = itemMat;
        this.displayName = displayName;
        this.loreList = Arrays.asList(lore);
        this.amount = 1;
        this.nbtTag = "";
        this.nbtData = "";
        this.setItemStackData();
    }


    public CustomItem(Material itemMat, Integer amount) {
        this.itemMat = itemMat;
        this.amount = amount;
        this.loreList = null;
        this.displayName = "";
        this.nbtTag = "";
        this.nbtData = "";
        this.setItemStackData();
    }

    public CustomItem(Material itemMat) {
        this.itemMat = itemMat;
        this.amount = 1;
        this.loreList = null;
        this.displayName = "";
        this.nbtTag = "";
        this.nbtData = "";
        this.setItemStackData();
    }

    public CustomItem(Material itemMat, String displayName) {
        this.itemMat = itemMat;
        this.amount = 1;
        this.loreList = null;
        this.displayName = displayName;
        this.nbtTag = "";
        this.nbtData = "";
        this.setItemStackData();
    }

    private void setItemStackData() {
        this.customItem = new ItemStack(this.itemMat, this.amount);
        ItemMeta itemMeta = this.customItem.getItemMeta();
        if (!this.displayName.isEmpty()) {
            itemMeta.setDisplayName(this.displayName);
        }
        if (this.loreList != null && !this.loreList.isEmpty()) {
            itemMeta.setLore(this.loreList);
        }
        this.customItem.setItemMeta(itemMeta);
        if (!this.nbtTag.isEmpty() && !this.nbtData.isEmpty()) {
            this.customItem = NBTEditor.set(customItem, nbtTag, nbtData);
        }
    }

    public ItemStack getItemStack() {
        return customItem;
    }

    public boolean giveCustomItemToPlayer(Player currentPlayer, String invFullMsg, String alreadyHasItemMsg) {
        boolean gavePlayerItem = true;
        boolean invFull = this.playerInventory.inventoryFull(currentPlayer);
        if (!currentPlayer.getInventory().contains(this.customItem)) {
            if (!invFull) {
                currentPlayer.getInventory().addItem(this.customItem);
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
        if (!currentPlayer.getInventory().contains(this.customItem)) {
            if (!invFull) {
                currentPlayer.getInventory().addItem(this.customItem);
            } else {
                currentPlayer.sendMessage(invFullMsg);
                gavePlayerItem = false;
            }
        }
        return gavePlayerItem;
    }

    public Material getItemMat() {
        return itemMat;
    }

    public int getAmount() {
        return amount;
    }

    public String getDisplayName() {
        return displayName;
    }
}
