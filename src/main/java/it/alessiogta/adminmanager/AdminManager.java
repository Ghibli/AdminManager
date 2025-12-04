package it.alessiogta.adminmanager;

import it.alessiogta.adminmanager.commands.AdminManagerCommand;
import it.alessiogta.adminmanager.commands.AdminManagerTabCompleter;
import it.alessiogta.adminmanager.gui.GameRuleChatListener;
import it.alessiogta.adminmanager.listeners.CommandBlockListener;
import it.alessiogta.adminmanager.listeners.PlayerControlListener;
import it.alessiogta.adminmanager.listeners.ToolCreatorChatListener;
import it.alessiogta.adminmanager.listeners.WorldGeneratorChatListener;
import it.alessiogta.adminmanager.utils.EconomyManager;
import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class AdminManager extends JavaPlugin {
    private static AdminManager instance;


    @Override
    public void onEnable() {
        instance = this;

        // Messaggio di avvio
        getLogger().info("AdminManager è stato abilitato!");

        // Caricamento configurazione
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Inizializzazione delle traduzioni
        String language = config.getString("language", "it_IT");
        TranslationManager.loadTranslations(language);

        // Inizializzazione Vault Economy
        if (EconomyManager.setupEconomy()) {
            getLogger().info("Vault economy integration enabled!");
        } else {
            getLogger().warning("Vault not found! Economy features will be disabled.");
        }

        // Inizializzazione bStats
        new it.alessiogta.adminmanager.metrics.Metrics(this, 28217);
        getLogger().info("bStats metrics enabled!");

        // Registrazione dei comandi
        registerCommands();

        // Inizializzazione delle GUI
        initializeGui();

        // Caricamento mondi custom
        loadCustomWorlds();

        // Registrazione degli eventi
        HandlerList.unregisterAll(this); // Evita registrazioni multiple
        getServer().getPluginManager().registerEvents(new PlayerControlListener(), this);
        getServer().getPluginManager().registerEvents(new WorldGeneratorChatListener(), this);
        getServer().getPluginManager().registerEvents(new GameRuleChatListener(), this);
        getServer().getPluginManager().registerEvents(new CommandBlockListener(), this);
        getServer().getPluginManager().registerEvents(new ToolCreatorChatListener(), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this); // Deregistra tutti i listener associati a questo plugin
        // Messaggio di arresto del plugin
        getLogger().info("AdminManager è stato disabilitato.");
    }

    private void registerCommands() {
        // Registra il comando principale /adminm
        getCommand("adminm").setExecutor(new AdminManagerCommand(getConfig()));
        getCommand("adminm").setTabCompleter(new AdminManagerTabCompleter());
    }

    private void initializeGui() {
        // Inizializzazione di eventuali GUI prefabbricate o statiche
        // GuiManager.registerGui("example", new ExampleGui(...));
    }

    private void loadCustomWorlds() {
        getLogger().info("Scansione e caricamento mondi custom...");

        // Get world container directory
        java.io.File worldContainer = getServer().getWorldContainer();
        java.io.File[] folders = worldContainer.listFiles();

        if (folders == null) {
            getLogger().warning("Impossibile leggere la directory dei mondi!");
            return;
        }

        // Default worlds that Bukkit loads automatically - skip these
        java.util.Set<String> defaultWorlds = new java.util.HashSet<>();
        defaultWorlds.add("world");
        defaultWorlds.add("world_nether");
        defaultWorlds.add("world_the_end");

        // Get current custom worlds list from config
        java.util.List<String> configWorlds = getConfig().getStringList("custom-worlds");
        java.util.Set<String> discoveredWorlds = new java.util.HashSet<>();
        int loadedCount = 0;
        int skippedCount = 0;

        // Scan all folders in world container
        for (java.io.File folder : folders) {
            // Skip if not a directory
            if (!folder.isDirectory()) {
                continue;
            }

            String worldName = folder.getName();

            // Skip default Bukkit worlds
            if (defaultWorlds.contains(worldName)) {
                continue;
            }

            // Skip common non-world folders
            if (worldName.equals("plugins") || worldName.equals("logs") ||
                worldName.equals("cache") || worldName.startsWith(".")) {
                continue;
            }

            // Check if this is a valid Minecraft world (has level.dat)
            java.io.File levelDat = new java.io.File(folder, "level.dat");
            if (!levelDat.exists()) {
                continue; // Not a valid world folder
            }

            // Valid world found!
            discoveredWorlds.add(worldName);

            // Check if already loaded
            if (getServer().getWorld(worldName) != null) {
                getLogger().info("Mondo '" + worldName + "' già caricato.");
                skippedCount++;
                continue;
            }

            // Load the world
            try {
                org.bukkit.WorldCreator creator = new org.bukkit.WorldCreator(worldName);
                org.bukkit.World world = creator.createWorld();

                if (world != null) {
                    getLogger().info("Mondo '" + worldName + "' caricato con successo!");
                    loadedCount++;
                } else {
                    getLogger().warning("Impossibile caricare il mondo '" + worldName + "'");
                }
            } catch (Exception e) {
                getLogger().severe("Errore durante il caricamento del mondo '" + worldName + "': " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Update config with all discovered worlds
        if (!discoveredWorlds.isEmpty()) {
            // Convert set to sorted list for cleaner config
            java.util.List<String> sortedWorlds = new java.util.ArrayList<>(discoveredWorlds);
            java.util.Collections.sort(sortedWorlds);

            // Only update config if the list changed
            if (!sortedWorlds.equals(configWorlds)) {
                getConfig().set("custom-worlds", sortedWorlds);
                saveConfig();
                getLogger().info("Config aggiornato con " + sortedWorlds.size() + " mondi trovati.");
            }
        }

        getLogger().info("Scansione completata: " + loadedCount + " mondi caricati, " +
                        skippedCount + " già presenti, " + discoveredWorlds.size() + " totali trovati.");
    }

    public static AdminManager getInstance() {
        return instance;
    }
}