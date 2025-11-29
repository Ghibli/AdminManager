package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ServerManagerGui extends BaseGui {

    private final Player admin;

    public ServerManagerGui(Player admin) {
        super(admin, TranslationManager.translate("ServerManager", "title", "&6&lServer Manager"), 1);
        this.admin = admin;
        setupGuiItems();
    }

    @Override
    protected void setupNavigationButtons() {
        // Disable BaseGui's automatic navigation buttons
        // ServerManagerGui uses custom layout
    }

    private void setupGuiItems() {
        // Row 2: Server controls & Management
        setItem(10, createStopServerButton());
        setItem(11, createRestartServerButton());
        setItem(13, createPlayerDataButton());
        setItem(15, createWhitelistButton());
        setItem(16, createGameRulesButton());

        // Row 3: World & Server operations
        setItem(19, createReloadServerButton());
        setItem(20, createSaveWorldButton());
        setItem(24, createEconomyProviderButton());
        setItem(25, createClearEntitiesButton());

        // Row 4: Additional tools (if needed)
        setItem(30, createToolsYmlButton());
        setItem(31, createCommandRegistrationButton());
        setItem(32, createLanguageButton());

        // Row 5: Config Manager
        setItem(40, createConfigManagerButton());

        // Back button (slot 49)
        setItem(49, createBackButton());
    }

    // ========== BUTTON CREATORS ==========

    private ItemStack createReloadServerButton() {
        String title = TranslationManager.translate("ServerManager", "reload_server_title", "&aRELOAD SERVER");
        String lore = TranslationManager.translate("ServerManager", "reload_server_lore", "&7Ricarica tutti i plugin del server");
        return createItem(Material.LIME_DYE, title, lore);
    }

    private ItemStack createRestartServerButton() {
        String title = TranslationManager.translate("ServerManager", "restart_server_title", "&eRESTART SERVER");
        String lore = TranslationManager.translate("ServerManager", "restart_server_lore", "&7Riavvia completamente il server\n&c&lATTENZIONE: Richiede script esterno!");
        return createItem(Material.REDSTONE, title, lore);
    }

    private ItemStack createStopServerButton() {
        String title = TranslationManager.translate("ServerManager", "stop_server_title", "&cSTOP SERVER");
        String lore = TranslationManager.translate("ServerManager", "stop_server_lore", "&7Arresta il server\n&c&lATTENZIONE: Azione irreversibile!");
        return createItem(Material.BARRIER, title, lore);
    }

    private ItemStack createClearEntitiesButton() {
        String title = TranslationManager.translate("ServerManager", "clear_entities_title", "&6CLEAR ENTITIES");
        String lore = TranslationManager.translate("ServerManager", "clear_entities_lore", "&7Rimuovi tutte le entità (mob, item, ecc.)");
        return createItem(Material.FIRE_CHARGE, title, lore);
    }

    private ItemStack createEconomyProviderButton() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");
        boolean vaultEnabled = plugin.getConfig().getBoolean("economy.vault-provider", false);

        String status = vaultEnabled ? "&aON" : "&cOFF";
        String action = vaultEnabled ? "Disabilita" : "Abilita";

        String title = TranslationManager.translate("ServerManager", "economy_provider_title", "&6Economy Provider: {status}")
            .replace("{status}", status);
        String lore = TranslationManager.translate("ServerManager", "economy_provider_lore",
            "&7Usa questo plugin come provider\n&7economico principale con Vault\n&e&lClick: &7{action}")
            .replace("{action}", action);

        Material material = vaultEnabled ? Material.EMERALD : Material.COAL;
        return createItem(material, title, lore.split("\n"));
    }

    private ItemStack createLanguageButton() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");
        String currentLang = plugin.getConfig().getString("language", "it_IT");

        String title = TranslationManager.translate("ServerManager", "language_title", "&bLANGUAGE");
        String lore = TranslationManager.translate("ServerManager", "language_lore",
            "&7Lingua corrente: &e{lang}\n&7Click per cambiare lingua")
            .replace("{lang}", currentLang);

        return createItem(Material.BOOK, title, lore.split("\n"));
    }

    private ItemStack createToolsYmlButton() {
        String title = TranslationManager.translate("ServerManager", "tools_yml_title", "&eTools.yml");
        String lore = TranslationManager.translate("ServerManager", "tools_yml_lore",
            "&e&lLEFT: &7Ricarica config\n&c&lSHIFT+RIGHT: &7Ripristina defaults");
        return createItem(Material.DIAMOND_PICKAXE, title, lore);
    }

    private ItemStack createConfigManagerButton() {
        String title = TranslationManager.translate("ServerManager", "config_manager_title", "&dConfig Manager");
        String lore = TranslationManager.translate("ServerManager", "config_manager_lore",
            "&e&lLEFT: &7Apri GUI gestione config");
        return createItem(Material.WRITABLE_BOOK, title, lore);
    }

    private ItemStack createGameRulesButton() {
        String title = TranslationManager.translate("ServerManager", "game_rules_title", "&6Game Rules");
        String lore = TranslationManager.translate("ServerManager", "game_rules_lore",
            "&7Gestisci le regole del server\n&e&lLEFT: &7Apri GUI game rules");
        return createItem(Material.COMPARATOR, title, lore.split("\n"));
    }

    private ItemStack createCommandRegistrationButton() {
        String title = TranslationManager.translate("ServerManager", "command_registration_title", "&aCommand Registration");
        String lore = TranslationManager.translate("ServerManager", "command_registration_lore",
            "&e&lLEFT: &7Apri GUI comandi");
        return createItem(Material.COMMAND_BLOCK, title, lore);
    }

    private ItemStack createSaveWorldButton() {
        String title = TranslationManager.translate("ServerManager", "save_world_title", "&eSAVE WORLD");
        String lore = TranslationManager.translate("ServerManager", "save_world_lore",
            "&e&lLEFT: &7Salva tutti i mondi");
        return createItem(Material.GRASS_BLOCK, title, lore);
    }

    private ItemStack createWhitelistButton() {
        boolean whitelistEnabled = Bukkit.hasWhitelist();

        String status = whitelistEnabled ? "&aON" : "&cOFF";
        String action = whitelistEnabled ? "Disabilita" : "Abilita";

        String title = TranslationManager.translate("ServerManager", "whitelist_title", "&fWHITELIST: {status}")
            .replace("{status}", status);
        String lore = TranslationManager.translate("ServerManager", "whitelist_lore",
            "&e&lLEFT: &7{action} whitelist\n&c&lSHIFT+RIGHT: &7Modifica whitelist")
            .replace("{action}", action);

        Material material = whitelistEnabled ? Material.LIME_DYE : Material.GRAY_DYE;
        return createItem(material, title, lore.split("\n"));
    }

    private ItemStack createPlayerDataButton() {
        // Count player data files from Bukkit's main world playerdata folder (dynamic)
        int playerCount = 0;

        if (!Bukkit.getWorlds().isEmpty()) {
            World mainWorld = Bukkit.getWorlds().get(0);
            File playerDataFolder = new File(mainWorld.getWorldFolder(), "playerdata");

            if (playerDataFolder.exists() && playerDataFolder.isDirectory()) {
                File[] files = playerDataFolder.listFiles((dir, name) -> name.endsWith(".dat"));
                if (files != null) {
                    playerCount = files.length;
                }
            }
        }

        String title = TranslationManager.translate("ServerManager", "player_data_title", "&6Player Data");
        String loreText = TranslationManager.translate("ServerManager", "player_data_lore",
            "&7Giocatori registrati: &e{count}\n\n&e&lLEFT: &7Visualizza dettagli")
            .replace("{count}", String.valueOf(playerCount));
        return createItem(Material.CHEST, title, loreText.split("\n"));
    }

    private ItemStack createBackButton() {
        String title = TranslationManager.translate("ServerManager", "back_button_title", "&cIndietro");
        String lore = TranslationManager.translate("ServerManager", "back_button_lore", "&7Torna alla lista giocatori");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    // ========== CLICK HANDLERS ==========

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        switch (slot) {
            case 10: handleStopServer(clicker); break;
            case 11: handleRestartServer(clicker); break;
            case 13: handlePlayerData(clicker); break;
            case 15: handleWhitelist(event, clicker); break;
            case 16: handleGameRules(clicker); break;
            case 19: handleReloadServer(clicker); break;
            case 20: handleSaveWorld(clicker); break;
            case 24: handleEconomyProviderToggle(clicker); break;
            case 25: handleClearEntities(clicker); break;
            case 30: handleToolsYml(event, clicker); break;
            case 31: handleCommandRegistration(clicker); break;
            case 32: handleLanguageSwitch(clicker); break;
            case 40: handleConfigManager(clicker); break;
            case 49: handleBack(clicker); break;
        }
    }

    private void handleReloadServer(Player clicker) {
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
            TranslationManager.translate("ServerManager", "reload_server_message", "&aRicaricamento server in corso...")));

        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> Bukkit.reload()
        );
    }

    private void handleRestartServer(Player clicker) {
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
            TranslationManager.translate("ServerManager", "restart_server_message", "&eRiavvio server in corso...")));

        Bukkit.broadcastMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
            "&c&lSERVER RESTART - Il server verrà riavviato tra 5 secondi!"));

        Bukkit.getScheduler().runTaskLater(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> Bukkit.spigot().restart(),
            100L // 5 seconds
        );
    }

    private void handleStopServer(Player clicker) {
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
            TranslationManager.translate("ServerManager", "stop_server_message", "&cArresto server in corso...")));

        Bukkit.broadcastMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
            "&c&lSERVER STOP - Il server verrà arrestato tra 5 secondi!"));

        Bukkit.getScheduler().runTaskLater(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> Bukkit.shutdown(),
            100L // 5 seconds
        );
    }

    private void handleClearEntities(Player clicker) {
        int totalCleared = 0;

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                // Clear items, monsters, animals, but not players
                if (entity.getType() != EntityType.PLAYER) {
                    entity.remove();
                    totalCleared++;
                }
            }
        }

        String message = TranslationManager.translate("ServerManager", "clear_entities_message",
            "&aRimosse &e{count} &aentità dal server")
            .replace("{count}", String.valueOf(totalCleared));
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
    }

    private void handleEconomyProviderToggle(Player clicker) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");
        boolean currentValue = plugin.getConfig().getBoolean("economy.vault-provider", false);
        plugin.getConfig().set("economy.vault-provider", !currentValue);
        plugin.saveConfig();

        String status = !currentValue ? "&aabilitato" : "&cdisabilitato";
        String message = TranslationManager.translate("ServerManager", "economy_provider_toggled",
            "&6Economy Provider " + status + "&6. Riavvia il server per applicare.");
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        // Refresh button at new position (slot 24)
        refreshSlot(24, createEconomyProviderButton());
    }

    private void handleLanguageSwitch(Player clicker) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");
        String currentLang = plugin.getConfig().getString("language", "it_IT");

        // Cycle through available languages
        String newLang;
        switch (currentLang) {
            case "it_IT": newLang = "en_EN"; break;
            case "en_EN": newLang = "it_IT"; break;
            default: newLang = "it_IT";
        }

        plugin.getConfig().set("language", newLang);
        plugin.saveConfig();

        // Reload translations immediately
        TranslationManager.reloadTranslations(newLang);

        String message = TranslationManager.translate("ServerManager", "language_changed",
            "&bLingua cambiata in: &e" + newLang + " &b- Traduzioni applicate immediatamente!");
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        // Refresh button at new position (slot 32)
        refreshSlot(32, createLanguageButton());
    }

    private void handleToolsYml(InventoryClickEvent event, Player clicker) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");

        if (event.isShiftClick() && event.isRightClick()) {
            // Restore defaults - copy default tools.yml from resources
            File toolsFile = new File(plugin.getDataFolder(), "tools.yml");
            if (toolsFile.exists()) {
                toolsFile.delete();
            }

            // Save default file
            plugin.saveResource("tools.yml", true);

            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ServerManager", "tools_yml_restore", "&cTools.yml ripristinato ai valori predefiniti!")));
        } else {
            // Reload tools.yml
            // Reload the file from disk
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ServerManager", "tools_yml_reload", "&aTools.yml ricaricato!")));
        }
    }

    private void handleConfigManager(Player clicker) {
        // Don't close inventory - open Config Manager GUI directly
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new ConfigManagerGui(clicker).open()
        );
    }

    private void handleCommandRegistration(Player clicker) {
        // Don't close inventory - open Command Category GUI directly
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new CommandCategoryGui(clicker).open()
        );
    }

    private void handleGameRules(Player clicker) {
        // Don't close inventory - open World Selector GUI
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new WorldSelectorGui(clicker).open()
        );
    }

    private void handleSaveWorld(Player clicker) {
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
            TranslationManager.translate("ServerManager", "save_world_message", "&eSalvataggio mondi in corso...")));

        for (World world : Bukkit.getWorlds()) {
            world.save();
        }

        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
            TranslationManager.translate("ServerManager", "save_world_complete", "&aTutti i mondi sono stati salvati!")));
    }

    private void handleWhitelist(InventoryClickEvent event, Player clicker) {
        if (event.isShiftClick() && event.isRightClick()) {
            // Edit whitelist - Open WhitelistEditor GUI
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new WhitelistEditorGui(clicker).open()
            );
        } else {
            // Toggle whitelist
            boolean currentValue = Bukkit.hasWhitelist();
            Bukkit.setWhitelist(!currentValue);

            String status = !currentValue ? "&aabilitata" : "&cdisabilitata";
            String message = TranslationManager.translate("ServerManager", "whitelist_toggled",
                "&fWhitelist " + status);
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

            // Refresh button at new position (slot 15)
            refreshSlot(15, createWhitelistButton());
        }
    }

    private void handlePlayerData(Player clicker) {
        // Open PlayerDataGui
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerDataGui(clicker, 1).open()
        );
    }

    private void handleBack(Player clicker) {
        // Don't close inventory - open PlayerListGui directly to preserve cursor position
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerListGui(clicker, 1).open()
        );
    }

    // Helper method to refresh a single slot
    private void refreshSlot(int slot, ItemStack item) {
        setItem(slot, item);
        if (inventory != null) {
            inventory.setItem(slot, item);
        }
    }

    @Override
    public void open() {
        inventory = build();
        admin.openInventory(inventory);
    }
}
