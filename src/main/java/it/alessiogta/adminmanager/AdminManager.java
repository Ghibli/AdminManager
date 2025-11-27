package it.alessiogta.adminmanager;

import it.alessiogta.adminmanager.commands.AdminManagerCommand;
import it.alessiogta.adminmanager.commands.AdminManagerTabCompleter;
import it.alessiogta.adminmanager.listeners.PlayerControlListener;
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
        // Load custom worlds from config
        java.util.List<String> customWorlds = getConfig().getStringList("custom-worlds");

        if (customWorlds.isEmpty()) {
            return;
        }

        getLogger().info("Caricamento " + customWorlds.size() + " mondi custom...");

        for (String worldName : customWorlds) {
            try {
                // Check if world already loaded
                if (getServer().getWorld(worldName) != null) {
                    getLogger().info("Mondo '" + worldName + "' già caricato.");
                    continue;
                }

                // Check if world folder exists
                java.io.File worldFolder = new java.io.File(getServer().getWorldContainer(), worldName);
                if (!worldFolder.exists()) {
                    getLogger().warning("Cartella del mondo '" + worldName + "' non trovata! Rimuovo dalla lista.");
                    customWorlds.remove(worldName);
                    getConfig().set("custom-worlds", customWorlds);
                    saveConfig();
                    continue;
                }

                // Load the world
                org.bukkit.WorldCreator creator = new org.bukkit.WorldCreator(worldName);
                org.bukkit.World world = creator.createWorld();

                if (world != null) {
                    getLogger().info("Mondo custom '" + worldName + "' caricato con successo!");
                } else {
                    getLogger().warning("Impossibile caricare il mondo '" + worldName + "'");
                }
            } catch (Exception e) {
                getLogger().severe("Errore durante il caricamento del mondo '" + worldName + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static AdminManager getInstance() {
        return instance;
    }
}