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

        // Caricamento configurazione
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Inizializzazione delle traduzioni
        String language = config.getString("language", "it_IT");
        TranslationManager.loadTranslations(language);

        // Inizializzazione Vault Economy
        boolean vaultEnabled = EconomyManager.setupEconomy();

        // Banner di avvio
        printStartupBanner(vaultEnabled);

        // Inizializzazione bStats con grafici personalizzati
        setupMetrics();

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
            }
        }
    }

    private void setupMetrics() {
        it.alessiogta.adminmanager.metrics.Metrics metrics = new it.alessiogta.adminmanager.metrics.Metrics(this, 28217);

        // 1. SimplePie: Lingua del plugin
        metrics.addCustomChart(new it.alessiogta.adminmanager.metrics.Metrics.SimplePie("plugin_language", () -> {
            return getConfig().getString("language", "it_IT");
        }));

        // 2. SimplePie: Server software (Bukkit/Spigot/Paper/Purpur)
        metrics.addCustomChart(new it.alessiogta.adminmanager.metrics.Metrics.SimplePie("server_software", () -> {
            String version = getServer().getVersion().toLowerCase();
            if (version.contains("paper")) return "Paper";
            if (version.contains("purpur")) return "Purpur";
            if (version.contains("spigot")) return "Spigot";
            return "Bukkit";
        }));

        // 3. SimplePie: Versione Minecraft
        metrics.addCustomChart(new it.alessiogta.adminmanager.metrics.Metrics.SimplePie("minecraft_version", () -> {
            String version = getServer().getBukkitVersion();
            // Estrae solo la versione principale (es: 1.21.3 da 1.21.3-R0.1-SNAPSHOT)
            return version.split("-")[0];
        }));

        // 4. SimplePie: Economy provider
        metrics.addCustomChart(new it.alessiogta.adminmanager.metrics.Metrics.SimplePie("economy_provider", () -> {
            if (!EconomyManager.isEnabled()) {
                return "None (No Vault)";
            }
            String provider = EconomyManager.getEconomyProvider();
            return provider != null ? provider : "Unknown";
        }));

        // 5. SimplePie: Versione Java
        metrics.addCustomChart(new it.alessiogta.adminmanager.metrics.Metrics.SimplePie("java_version", () -> {
            String version = System.getProperty("java.version");
            // Semplifica versione (es: 17.0.1 -> Java 17)
            String majorVersion = version.split("\\.")[0];
            if (version.startsWith("1.8")) {
                return "Java 8";
            }
            return "Java " + majorVersion;
        }));

        // 6. AdvancedPie: Numero di mondi custom (raggruppati)
        metrics.addCustomChart(new it.alessiogta.adminmanager.metrics.Metrics.AdvancedPie("custom_worlds_range", () -> {
            java.util.Map<String, Integer> worldsMap = new java.util.HashMap<>();
            int customWorldCount = 0;

            // Conta mondi custom (escludi world, world_nether, world_the_end)
            for (org.bukkit.World world : getServer().getWorlds()) {
                String name = world.getName();
                if (!name.equals("world") && !name.equals("world_nether") && !name.equals("world_the_end")) {
                    customWorldCount++;
                }
            }

            // Raggruppa in range
            String range;
            if (customWorldCount == 0) {
                range = "0 worlds";
            } else if (customWorldCount <= 3) {
                range = "1-3 worlds";
            } else if (customWorldCount <= 6) {
                range = "4-6 worlds";
            } else if (customWorldCount <= 10) {
                range = "7-10 worlds";
            } else {
                range = "10+ worlds";
            }

            worldsMap.put(range, 1);
            return worldsMap;
        }));

        // 7. SingleLineChart: Numero totale di mondi custom
        metrics.addCustomChart(new it.alessiogta.adminmanager.metrics.Metrics.SingleLineChart("total_custom_worlds", () -> {
            int customWorldCount = 0;
            for (org.bukkit.World world : getServer().getWorlds()) {
                String name = world.getName();
                if (!name.equals("world") && !name.equals("world_nether") && !name.equals("world_the_end")) {
                    customWorldCount++;
                }
            }
            return customWorldCount;
        }));

        // 8. AdvancedPie: Sistema operativo
        metrics.addCustomChart(new it.alessiogta.adminmanager.metrics.Metrics.AdvancedPie("operating_system", () -> {
            java.util.Map<String, Integer> osMap = new java.util.HashMap<>();
            String os = System.getProperty("os.name").toLowerCase();

            String osName;
            if (os.contains("win")) {
                osName = "Windows";
            } else if (os.contains("mac")) {
                osName = "macOS";
            } else if (os.contains("nux") || os.contains("nix")) {
                osName = "Linux";
            } else {
                osName = "Other";
            }

            osMap.put(osName, 1);
            return osMap;
        }));
    }

    private void printStartupBanner(boolean vaultEnabled) {
        String version = getDescription().getVersion();
        String vaultStatus = vaultEnabled
            ? "§aVault Hook ✓"
            : "§cVault Hook ✗";

        Bukkit.getConsoleSender().sendMessage("§6=============================================");
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("   §6§lAdmin Manager §7(v" + version + ")");
        Bukkit.getConsoleSender().sendMessage("   §7Developed with §c♥ §7by §fAlessioGTA");
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("   §aThe plugin that helps you manage your server!");
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("   " + vaultStatus);
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("§6=============================================");
    }

    public static AdminManager getInstance() {
        return instance;
    }
}