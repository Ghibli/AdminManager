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
        // Row 1: Server Control (slots 0-3)
        setItem(0, createReloadServerButton());
        setItem(1, createRestartServerButton());
        setItem(2, createStopServerButton());
        setItem(3, createClearEntitiesButton());

        // Row 2: Economy & Language (slots 9-11)
        setItem(9, createEconomyProviderButton());
        setItem(11, createLanguageButton());

        // Row 3: Tools.yml (slots 18-19)
        setItem(18, createToolsYmlButton());

        // Row 4: Config Manager (slots 27)
        setItem(27, createConfigManagerButton());

        // Row 5: Command Registration, Save World, Whitelist (slots 36-38)
        setItem(36, createCommandRegistrationButton());
        setItem(37, createSaveWorldButton());
        setItem(38, createWhitelistButton());

        // Row 6: Config.yml (slots 45-46) + Back button (slot 49)
        setItem(45, createConfigYmlButton());
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
        String title = TranslationManager.translate("ServerManager", "economy_provider_title", "&6Economy Provider: " + status);
        String lore = TranslationManager.translate("ServerManager", "economy_provider_lore",
            "&7Usa questo plugin come provider\n&7economico principale con Vault\n&e&lClick: &7" + (vaultEnabled ? "Disabilita" : "Abilita"));

        Material material = vaultEnabled ? Material.EMERALD : Material.COAL;
        return createItem(material, title, lore);
    }

    private ItemStack createLanguageButton() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");
        String currentLang = plugin.getConfig().getString("language", "it_IT");

        String title = TranslationManager.translate("ServerManager", "language_title", "&bLANGUAGE");
        String lore = TranslationManager.translate("ServerManager", "language_lore",
            "&7Lingua corrente: &e" + currentLang + "\n&7Click per cambiare lingua");

        return createItem(Material.BOOK, title, lore);
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
        String title = TranslationManager.translate("ServerManager", "whitelist_title", "&fWHITELIST: " + status);
        String lore = TranslationManager.translate("ServerManager", "whitelist_lore",
            "&e&lLEFT: &7" + (whitelistEnabled ? "Disabilita" : "Abilita") + " whitelist\n&c&lSHIFT+RIGHT: &7Modifica whitelist");

        Material material = whitelistEnabled ? Material.LIME_DYE : Material.GRAY_DYE;
        return createItem(material, title, lore);
    }

    private ItemStack createConfigYmlButton() {
        String title = TranslationManager.translate("ServerManager", "config_yml_title", "&eConfig.yml");
        String lore = TranslationManager.translate("ServerManager", "config_yml_lore",
            "&e&lLEFT: &7Ricarica config\n&c&lSHIFT+RIGHT: &7Ripristina defaults");
        return createItem(Material.PAPER, title, lore);
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
            case 0: handleReloadServer(clicker); break;
            case 1: handleRestartServer(clicker); break;
            case 2: handleStopServer(clicker); break;
            case 3: handleClearEntities(clicker); break;
            case 9: handleEconomyProviderToggle(clicker); break;
            case 11: handleLanguageSwitch(clicker); break;
            case 18: handleToolsYml(event, clicker); break;
            case 27: handleConfigManager(clicker); break;
            case 36: handleCommandRegistration(clicker); break;
            case 37: handleSaveWorld(clicker); break;
            case 38: handleWhitelist(event, clicker); break;
            case 45: handleConfigYml(event, clicker); break;
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
        String restartFile = Bukkit.getPluginManager().getPlugin("AdminManager").getConfig().getString("restart-script", "start.bat");
        File file = new File(restartFile);

        if (file.exists()) {
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ServerManager", "restart_server_message", "&eRiavvio server in corso...")));

            Bukkit.broadcastMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                "&c&lSERVER RESTART - Il server verrà riavviato tra 5 secondi!"));

            Bukkit.getScheduler().runTaskLater(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> {
                    Bukkit.spigot().restart();
                },
                100L // 5 seconds
            );
        } else {
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ServerManager", "restart_file_not_found", "&cFile di riavvio non trovato: " + restartFile)));
        }
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

        // Refresh button
        refreshSlot(9, createEconomyProviderButton());
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

        String message = TranslationManager.translate("ServerManager", "language_changed",
            "&bLingua cambiata in: &e" + newLang + " &b- Ricarica il server per applicare");
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        // Refresh button
        refreshSlot(11, createLanguageButton());
    }

    private void handleToolsYml(InventoryClickEvent event, Player clicker) {
        if (event.isShiftClick() && event.isRightClick()) {
            // Restore defaults
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ServerManager", "tools_yml_restore", "&cRipristino tools.yml ai valori predefiniti...")));
            // TODO: Implement restore defaults logic
        } else {
            // Reload
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ServerManager", "tools_yml_reload", "&aRicaricamento tools.yml...")));
            // TODO: Implement reload logic
        }
    }

    private void handleConfigManager(Player clicker) {
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
            TranslationManager.translate("ServerManager", "config_manager_message", "&dApertura Config Manager...")));
        // TODO: Implement Config Manager GUI
    }

    private void handleCommandRegistration(Player clicker) {
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
            TranslationManager.translate("ServerManager", "command_registration_message", "&aApertura Command Registration...")));
        // TODO: Implement Command Registration GUI
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
            // Edit whitelist (TODO: implement GUI)
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ServerManager", "whitelist_edit", "&fApertura editor whitelist...")));
            // TODO: Implement Whitelist Editor GUI
        } else {
            // Toggle whitelist
            boolean currentValue = Bukkit.hasWhitelist();
            Bukkit.setWhitelist(!currentValue);

            String status = !currentValue ? "&aabilitata" : "&cdisabilitata";
            String message = TranslationManager.translate("ServerManager", "whitelist_toggled",
                "&fWhitelist " + status);
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

            // Refresh button
            refreshSlot(38, createWhitelistButton());
        }
    }

    private void handleConfigYml(InventoryClickEvent event, Player clicker) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");

        if (event.isShiftClick() && event.isRightClick()) {
            // Restore defaults
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ServerManager", "config_yml_restore", "&cRipristino config.yml ai valori predefiniti...")));
            // TODO: Implement restore defaults logic
        } else {
            // Reload config
            plugin.reloadConfig();
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ServerManager", "config_yml_reload", "&aConfig.yml ricaricato!")));
        }
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
