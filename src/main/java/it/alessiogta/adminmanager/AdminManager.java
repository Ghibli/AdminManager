package it.alessiogta.adminmanager;

import it.alessiogta.adminmanager.commands.AdminManagerCommand;
import it.alessiogta.adminmanager.commands.AdminManagerTabCompleter;
import it.alessiogta.adminmanager.listeners.ChatListener;
import it.alessiogta.adminmanager.utils.PlayerLogger;
import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class AdminManager extends JavaPlugin {
    private static AdminManager instance;


    @Override
    public void onEnable() {
        int muteCheckCounter = 0; // Resetta il contatore all'avvio
        instance = this;

        // Messaggio di avvio
        getLogger().info("AdminManager è stato abilitato!");

        // Caricamento configurazione
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Inizializzazione delle traduzioni
        String language = config.getString("language", "it_IT");
        TranslationManager.loadTranslations(language);

        // Inizializzazione del sistema di log
        PlayerLogger.initialize(config);

        // Registrazione dei comandi
        registerCommands();

        // Inizializzazione delle GUI
        initializeGui();

        // Registrazione degli eventi
        HandlerList.unregisterAll(this); // Evita registrazioni multiple
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
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
    public static AdminManager getInstance() {
        return instance;
    }
}