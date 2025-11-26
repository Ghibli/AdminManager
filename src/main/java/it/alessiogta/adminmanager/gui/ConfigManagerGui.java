package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ConfigManagerGui extends BaseGui {

    private final Player admin;

    public ConfigManagerGui(Player admin) {
        super(admin, TranslationManager.translate("ConfigManager", "title", "&dConfig Manager"), 1);
        this.admin = admin;
        setupGuiItems();
    }

    @Override
    protected void setupNavigationButtons() {
        // Disable BaseGui's automatic navigation buttons
    }

    private void setupGuiItems() {
        // Row 2: Config files
        setItem(10, createConfigYmlButton());
        setItem(11, createToolsYmlButton());
        setItem(12, createTranslationsButton());
        setItem(13, createPlayerDataButton());

        // Row 3: GUI configs
        setItem(19, createGuiConfigButton("PlayerListGui"));
        setItem(20, createGuiConfigButton("PlayerManage"));
        setItem(21, createGuiConfigButton("ServerManager"));
        setItem(22, createGuiConfigButton("ArmorCreator"));

        // Back button at slot 49
        setItem(49, createBackButton());
    }

    private ItemStack createConfigYmlButton() {
        String title = TranslationManager.translate("ConfigManager", "config_yml_title", "&eConfig.yml");
        String lore = TranslationManager.translate("ConfigManager", "config_yml_lore",
            "&7File di configurazione principale\n\n&e&lLEFT: &7Reload\n&c&lSHIFT+RIGHT: &7Restore defaults");
        return createItem(Material.PAPER, title, lore.split("\n"));
    }

    private ItemStack createToolsYmlButton() {
        String title = TranslationManager.translate("ConfigManager", "tools_yml_title", "&eTools.yml");
        String lore = TranslationManager.translate("ConfigManager", "tools_yml_lore",
            "&7Configurazione strumenti\n\n&e&lLEFT: &7Reload\n&c&lSHIFT+RIGHT: &7Restore defaults");
        return createItem(Material.DIAMOND_PICKAXE, title, lore.split("\n"));
    }

    private ItemStack createTranslationsButton() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");
        String currentLang = plugin.getConfig().getString("language", "it_IT");

        String title = TranslationManager.translate("ConfigManager", "translations_title", "&bTranslations");
        String lore = TranslationManager.translate("ConfigManager", "translations_lore",
            "&7Lingua corrente: &e" + currentLang + "\n\n&e&lLEFT: &7Switch language");
        return createItem(Material.BOOK, title, lore.split("\n"));
    }

    private ItemStack createPlayerDataButton() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");
        File playerDataFolder = new File(plugin.getDataFolder(), "playerdata");
        int playerCount = 0;
        if (playerDataFolder.exists() && playerDataFolder.isDirectory()) {
            playerCount = playerDataFolder.listFiles().length;
        }

        String title = TranslationManager.translate("ConfigManager", "player_data_title", "&6Player Data");
        String lore = TranslationManager.translate("ConfigManager", "player_data_lore",
            "&7File salvati: &e" + playerCount + "\n\n&e&lLEFT: &7View details");
        return createItem(Material.CHEST, title, lore.split("\n"));
    }

    private ItemStack createGuiConfigButton(String guiName) {
        String title = TranslationManager.translate("ConfigManager", "gui_config_title", "&d" + guiName + ".yml");
        String lore = TranslationManager.translate("ConfigManager", "gui_config_lore",
            "&7Configurazione GUI\n\n&e&lLEFT: &7Reload");
        return createItem(Material.WRITABLE_BOOK, title, lore.split("\n"));
    }

    private ItemStack createBackButton() {
        String title = TranslationManager.translate("ConfigManager", "back_button_title", "&cIndietro");
        String lore = TranslationManager.translate("ConfigManager", "back_button_lore", "&7Torna a Server Manager");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        switch (slot) {
            case 10: handleConfigYml(event, clicker); break;
            case 11: handleToolsYml(event, clicker); break;
            case 12: handleTranslations(clicker); break;
            case 13: handlePlayerData(clicker); break;
            case 19:
            case 20:
            case 21:
            case 22: handleGuiConfig(slot, clicker); break;
            case 49: handleBack(clicker); break;
        }
    }

    private void handleConfigYml(InventoryClickEvent event, Player clicker) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");

        if (event.isShiftClick() && event.isRightClick()) {
            // Restore defaults
            File configFile = new File(plugin.getDataFolder(), "config.yml");
            if (configFile.exists()) {
                configFile.delete();
            }
            plugin.saveDefaultConfig();
            plugin.reloadConfig();

            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ConfigManager", "config_restored", "&cConfig.yml ripristinato!")));
        } else {
            // Reload
            plugin.reloadConfig();
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ConfigManager", "config_reloaded", "&aConfig.yml ricaricato!")));
        }
    }

    private void handleToolsYml(InventoryClickEvent event, Player clicker) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");

        if (event.isShiftClick() && event.isRightClick()) {
            // Restore defaults
            File toolsFile = new File(plugin.getDataFolder(), "tools.yml");
            if (toolsFile.exists()) {
                toolsFile.delete();
            }
            plugin.saveResource("tools.yml", true);

            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ConfigManager", "tools_restored", "&cTools.yml ripristinato!")));
        } else {
            // Reload
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ConfigManager", "tools_reloaded", "&aTools.yml ricaricato!")));
        }
    }

    private void handleTranslations(Player clicker) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");
        String currentLang = plugin.getConfig().getString("language", "it_IT");

        // Cycle language
        String newLang = currentLang.equals("it_IT") ? "en_EN" : "it_IT";
        plugin.getConfig().set("language", newLang);
        plugin.saveConfig();

        // Reload translations immediately
        TranslationManager.reloadTranslations(newLang);

        String message = TranslationManager.translate("ConfigManager", "language_changed",
            "&bLingua cambiata in: &e" + newLang + " &b- Traduzioni applicate immediatamente!");
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        // Refresh button
        refreshSlot(12, createTranslationsButton());
    }

    private void handlePlayerData(Player clicker) {
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
            TranslationManager.translate("ConfigManager", "player_data_message", "&6Visualizzazione dati player...")));
        // Future: could open a detailed view
    }

    private void handleGuiConfig(int slot, Player clicker) {
        String[] guiNames = {"PlayerListGui", "PlayerManage", "ServerManager", "ArmorCreator"};
        int index = slot - 19;
        if (index >= 0 && index < guiNames.length) {
            String guiName = guiNames[index];
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ConfigManager", "gui_config_reloaded",
                    "&d" + guiName + ".yml ricaricato!")));
        }
    }

    private void handleBack(Player clicker) {
        // Don't close inventory - open ServerManagerGui directly
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new ServerManagerGui(clicker).open()
        );
    }

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
