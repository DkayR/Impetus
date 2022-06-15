package ca.dklink750.impetus;

import ca.dklink750.impetus.commands.PkTool;
import ca.dklink750.impetus.commands.Prac;
import ca.dklink750.impetus.commands.TimerCommand;
import ca.dklink750.impetus.commands.Unprac;
import ca.dklink750.impetus.listeners.*;
import ca.dklink750.impetus.utils.ConfigManager;
import ca.dklink750.impetus.utils.TimerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Impetus extends JavaPlugin {

    private Impetus plugin;
    private Practice pracLocations;
    private Database database;
    private User user;
    private ActivatorBlock activatorBlock;
    private final FileConfiguration config = this.getConfig();
    private ConfigManager configManager;
    private TimerManager timer;

    @Override
    public void onEnable() {
        this.plugin = this;
        this.saveDefaultConfig();
        configManager = new ConfigManager(config);
        copySQLScript();

        // Initialize database
        database = new Database(getConfigManager());

        // Start the plugin when database is set up correctly
        if (database.isDatabaseInitialized()) {
            timer = new TimerManager(this.plugin, getMyDatabase());
            pracLocations = new Practice(getMyDatabase(), getTimer());
            user = new User(getMyDatabase());
            activatorBlock = new ActivatorBlock(getMyDatabase(), getPracLocations());

            // Register commands and event listeners
            registerCommands();
            registerEventListeners(getServer().getPluginManager());
        }
    }

    public Practice getPracLocations() {
        return pracLocations;
    }

    public Database getMyDatabase() {
        return database;
    }

    public User getUser() {
        return user;
    }

    public ConfigManager getConfigManager() { return configManager; }

    public ActivatorBlock getActivatorBlock() {
        return activatorBlock;
    }

    public TimerManager getTimer() {
        return timer;
    }

    // Extracts plugin's SQL script from the jar
    private void copySQLScript() {
        if (this.getResource(configManager.getScript()) != null) {
            this.saveResource(configManager.getScript(), true);
        }
    }

    // Initializes plugin commands
    private void registerCommands() {
        this.getCommand("prac").setExecutor(new Prac(getPracLocations(), getMyDatabase()));
        this.getCommand("unprac").setExecutor(new Unprac(getPracLocations()));
        this.getCommand("pktool").setExecutor(new PkTool());
        this.getCommand("timer").setExecutor(new TimerCommand(getTimer()));
    }

    private void registerEventListeners(PluginManager pluginManager) {
        pluginManager.registerEvents(new OnPlayerJoinEvent(getUser(), getPracLocations(), getConfigManager()), this);
        pluginManager.registerEvents(new OnPlayerInteractEvent(getActivatorBlock(), getPracLocations(), getConfigManager(), getTimer()), this);
        pluginManager.registerEvents(new OnPlayerDropEvent(getConfigManager()), this);
        pluginManager.registerEvents(new OnPlayerBreakEvent(getConfigManager(), getActivatorBlock()), this);
        pluginManager.registerEvents(new OnPlayerWorldChangeEvent(getPracLocations(), getConfigManager()), this);
        pluginManager.registerEvents(new OnBlockPhysicsEvent(getActivatorBlock()), this);
        pluginManager.registerEvents(new OnPlayerMoveEvent(getConfigManager(), getPracLocations()), this);
        pluginManager.registerEvents(new OnPlayerQuitEvent(getTimer()), this);
    }
}