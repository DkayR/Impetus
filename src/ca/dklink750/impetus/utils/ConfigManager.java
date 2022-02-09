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
    private List<Material> ineractablesToCancel = new ArrayList<>();
    private List<Material> activatorBlockTypes = new ArrayList<>();
    private boolean practiceOnDrop;
    private boolean makePluginItemsDroppable;
    private boolean destroyBlocksWhileHoldingPracticeTool;
    private boolean destroyActivatorBlocksWithoutPkTool;
    private boolean displayTimer;

    public ConfigManager(FileConfiguration config) {
        this.config = config;
        loadDatabaseInfo();
        loadNonInteractable();
        loadActivatorBlockTypes();
        this.practiceOnDrop = config.getBoolean("practice.practiceOnDrop");
        this.makePluginItemsDroppable = config.getBoolean("practice.makePluginItemsDroppable");
        this.destroyBlocksWhileHoldingPracticeTool = config.getBoolean("practice.destroyBlocksWhileHoldingPracticeTool");
        this.destroyActivatorBlocksWithoutPkTool = config.getBoolean("practice.destroyActivatorBlocksWithoutPkTool");
        this.displayTimer = config.getBoolean("practice.displayTimer");
    }

    private void loadNonInteractable() {
        List<String> materialNames = config.getStringList("practice.cancelInteractions");
        if(materialNames != null) {
            for(String materialName : materialNames) {
                Material materialFromName = Material.matchMaterial(materialName);
                if(materialFromName != null) {
                    this.ineractablesToCancel.add(materialFromName);
                }
            }
        }
        else {
            this.ineractablesToCancel = null;
        }
    }

    private void loadActivatorBlockTypes() {
        List<String> activatorBlockTypes = config.getStringList("practice.activatorBlocks");
        if(activatorBlockTypes != null) {
            for(String activatorBlockTypeName : activatorBlockTypes) {
                Material activtorBlockTypeFromName = Material.matchMaterial(activatorBlockTypeName);
                if(activtorBlockTypeFromName != null) {
                    this.activatorBlockTypes.add(activtorBlockTypeFromName);
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

    public List<Material> getIneractablesToCancel() {
        return ineractablesToCancel;
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

    public boolean getDisplayTimer() {
        return displayTimer;
    }

    public void setPracticeOnDrop(boolean practiceOnDrop) { this.practiceOnDrop = practiceOnDrop; }

    public void setMakePluginItemsDroppable(boolean makePluginItemsDroppable) { this.makePluginItemsDroppable = makePluginItemsDroppable; }

    public void setDestroyBlocksWhileHoldingPracticeTool(boolean destroyBlocksWhileHoldingPracticeTool) { this.destroyBlocksWhileHoldingPracticeTool = destroyBlocksWhileHoldingPracticeTool; }

    public void setDestroyActivatorBlocksWithoutPkTool(boolean destroyActivatorBlocksWithoutPkTool) { this.destroyActivatorBlocksWithoutPkTool = destroyActivatorBlocksWithoutPkTool; }

    public void setDisplayTimer(boolean displayTimer) { this.displayTimer = displayTimer; }
}
