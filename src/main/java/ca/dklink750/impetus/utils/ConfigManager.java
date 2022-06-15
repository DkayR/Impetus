package ca.dklink750.impetus.utils;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private final FileConfiguration config;
    private String host;
    private String port;
    private String databaseName;
    private String username;
    private String password;
    private String script;
    private List<Material> interactablesToCancel = new ArrayList<>();
    private List<Material> activatorBlockTypes = new ArrayList<>();
    private boolean practiceOnDrop;
    private boolean makePluginItemsDroppable;
    private boolean destroyBlocksWhileHoldingPracticeTool;
    private boolean destroyActivatorBlocksWithoutPkTool;
    private boolean teleportWhenInVoid;
    private boolean displayTimer;
    private boolean destroyActivatorsOnExplode;
    private boolean destroyActivatorsOnPhysics;
    private boolean destroyActivatorsOnPistonExtend;

    public ConfigManager(FileConfiguration config) {
        this.config = config;
        loadDatabaseInfo();
        loadNonInteractable();
        loadActivatorBlockTypes();
        this.practiceOnDrop = config.getBoolean("practice.practiceOnDrop");
        this.makePluginItemsDroppable = config.getBoolean("practice.makePluginItemsDroppable");
        this.destroyBlocksWhileHoldingPracticeTool = config.getBoolean("practice.destroyBlocksWhileHoldingPracticeTool");
        this.destroyActivatorBlocksWithoutPkTool = config.getBoolean("practice.destroyActivatorBlocksWithoutPkTool");
        this.teleportWhenInVoid = config.getBoolean("practice.teleportWhenInVoid");
        this.displayTimer = config.getBoolean("practice.displayTimer");
        this.destroyActivatorsOnExplode = config.getBoolean("practice.destroyActivatorsOnExplode");
        this.destroyActivatorsOnPhysics = config.getBoolean("practice.destroyActivatorsOnPhysics");
        this.destroyActivatorsOnPistonExtend = config.getBoolean("practice.destroyActivatorsOnPistonExtend");
    }

    private void loadNonInteractable() {
        List<String> materialNames = config.getStringList("practice.cancelInteractions");
        if(materialNames != null) {
            for(String materialName : materialNames) {
                Material materialFromName = Material.matchMaterial(materialName);
                if(materialFromName != null) {
                    this.interactablesToCancel.add(materialFromName);
                }
            }
        }
        else {
            this.interactablesToCancel = null;
        }
    }

    private void loadActivatorBlockTypes() {
        List<String> activatorBlockTypes = config.getStringList("practice.activatorBlocks");
        if(activatorBlockTypes != null) {
            for(String activatorBlockTypeName : activatorBlockTypes) {
                Material activatorBlockTypeFromName = Material.matchMaterial(activatorBlockTypeName);
                if(activatorBlockTypeFromName != null) {
                    this.activatorBlockTypes.add(activatorBlockTypeFromName);
                }
            }
        }
        else {
            this.activatorBlockTypes = null;
        }
    }

    private void loadDatabaseInfo() {
        this.host = config.getString("database.host");
        this.port = config.getString("database.port");
        this.databaseName = config.getString("database.databaseName");
        this.username = config.getString("database.username");
        this.password = config.getString("database.password");
        this.script = config.getString("database.script");
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getScript() {
        return script;
    }

    public List<Material> getInteractablesToCancel() {
        return interactablesToCancel;
    }

    public List<Material> getActivatorBlockTypes() {
        return activatorBlockTypes;
    }

    public boolean getPracticeOnDrop() {
        return practiceOnDrop;
    }

    public boolean getMakePluginItemsDroppable() {
        return makePluginItemsDroppable;
    }

    public boolean getDestroyBlocksWhileHoldingPracticeTool() {
        return destroyBlocksWhileHoldingPracticeTool;
    }

    public boolean getDestroyActivatorBlocksWithoutPkTool() {
        return destroyActivatorBlocksWithoutPkTool;
    }

    public boolean getTeleportWhenInVoid() { return teleportWhenInVoid; }

    public boolean getDisplayTimer() { return displayTimer; }

    public boolean getDestroyActivatorsOnExplode() { return destroyActivatorsOnExplode; }

    public boolean getDestroyActivatorsOnPhysics() { return destroyActivatorsOnPhysics; }

    public boolean getDestroyActivatorsOnPistonExtend () { return destroyActivatorsOnPistonExtend; }

    public void setDestroyActivatorsOnPistonExtend(boolean destroyActivatorsOnPistonExtend) { this.destroyActivatorsOnPistonExtend = destroyActivatorsOnPistonExtend; }

    public void setDestroyActivatorsOnPhysics(boolean destroyActivatorsOnPhysics) { this.destroyActivatorsOnPhysics = destroyActivatorsOnPhysics; }

    public void setDestroyActivatorsOnExplode(boolean destroyActivatorsOnExplode) { this.destroyActivatorsOnExplode = destroyActivatorsOnExplode; }

    public void setPracticeOnDrop(boolean practiceOnDrop) { this.practiceOnDrop = practiceOnDrop; }

    public void setMakePluginItemsDroppable(boolean makePluginItemsDroppable) { this.makePluginItemsDroppable = makePluginItemsDroppable; }

    public void setDestroyBlocksWhileHoldingPracticeTool(boolean destroyBlocksWhileHoldingPracticeTool) { this.destroyBlocksWhileHoldingPracticeTool = destroyBlocksWhileHoldingPracticeTool; }

    public void setDestroyActivatorBlocksWithoutPkTool(boolean destroyActivatorBlocksWithoutPkTool) { this.destroyActivatorBlocksWithoutPkTool = destroyActivatorBlocksWithoutPkTool; }

    public void setTeleportWhenInVoid(boolean teleportWhenInVoid) { this.teleportWhenInVoid = teleportWhenInVoid; }

    public void setDisplayTimer(boolean displayTimer) { this.displayTimer = displayTimer; }
}
