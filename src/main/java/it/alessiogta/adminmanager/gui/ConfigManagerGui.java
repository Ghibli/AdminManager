package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ConfigManagerGui extends BaseGui {

    private final Player admin;

    public ConfigManagerGui(Player admin) {
        super(admin, TranslationManager.translate("ConfigManager", "title", "&d&lSimple Admin Config Manager"), 1);
        this.admin = admin;
        setupGuiItems();
    }

    @Override
    protected void setupNavigationButtons() {
        // Disable BaseGui's automatic navigation buttons
    }

    private void setupGuiItems() {
        // Row 2: Main config files
        setItem(10, createConfigYmlButton());
        setItem(12, createTranslationsButton());

        // Row 3: GUI configs (7 slots: 19-25)
        setItem(19, createGuiConfigButton("ArmorCreator", Material.DIAMOND_CHESTPLATE));
        setItem(20, createGuiConfigButton("CommandRegistration", Material.COMMAND_BLOCK));
        setItem(21, createGuiConfigButton("ConfigManager", Material.WRITABLE_BOOK));
        setItem(22, createGuiConfigButton("EconomyManager", Material.GOLD_INGOT));
        setItem(23, createGuiConfigButton("GameRules", Material.BOOK));
        setItem(24, createGuiConfigButton("PlayerListGui", Material.PLAYER_HEAD));
        setItem(25, createGuiConfigButton("PlayerManage", Material.DIAMOND_SWORD));

        // Row 4: More GUI configs (7 slots: 28-34)
        setItem(28, createGuiConfigButton("ServerManager", Material.COMMAND_BLOCK));
        setItem(29, createGuiConfigButton("SpeedControl", Material.FEATHER));
        setItem(30, createGuiConfigButton("WhitelistEditor", Material.PAPER));
        setItem(31, createGuiConfigButton("WorldGenerator", Material.GRASS_BLOCK));
        setItem(32, createGuiConfigButton("WorldSelector", Material.COMPASS));

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
        String loreText = TranslationManager.translate("ConfigManager", "translations_lore",
            "&7Lingua corrente: &e{lang}\n\n&e&lLEFT: &7Switch language")
            .replace("{lang}", currentLang);
        return createItem(Material.BOOK, title, loreText.split("\n"));
    }

    private ItemStack createGuiConfigButton(String guiName, Material icon) {
        String title = TranslationManager.translate("ConfigManager", "gui_config_title", "&d{gui}.yml")
            .replace("{gui}", guiName);
        String lore = TranslationManager.translate("ConfigManager", "gui_config_lore",
            "&7Configurazione GUI\n\n&e&lLEFT: &7Reload");
        return createItem(icon, title, lore.split("\n"));
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
            case 12: handleTranslations(clicker); break;
            // GUI configs row 3 (19-25)
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            // GUI configs row 4 (28-32)
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
                handleGuiConfig(slot, clicker);
                break;
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
            "&bLingua cambiata in: &e{lang} &b- Traduzioni applicate immediatamente!")
            .replace("{lang}", newLang);
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        // Refresh button
        refreshSlot(12, createTranslationsButton());
    }

    private void handleGuiConfig(int slot, Player clicker) {
        // Map slots to GUI names
        String guiName = null;
        switch (slot) {
            case 19: guiName = "ArmorCreator"; break;
            case 20: guiName = "CommandRegistration"; break;
            case 21: guiName = "ConfigManager"; break;
            case 22: guiName = "EconomyManager"; break;
            case 23: guiName = "GameRules"; break;
            case 24: guiName = "PlayerListGui"; break;
            case 25: guiName = "PlayerManage"; break;
            case 28: guiName = "ServerManager"; break;
            case 29: guiName = "SpeedControl"; break;
            case 30: guiName = "WhitelistEditor"; break;
            case 31: guiName = "WorldGenerator"; break;
            case 32: guiName = "WorldSelector"; break;
        }

        if (guiName != null) {
            // Reload translations for this GUI
            TranslationManager.reloadTranslations(
                Bukkit.getPluginManager().getPlugin("AdminManager").getConfig().getString("language", "it_IT")
            );

            String message = TranslationManager.translate("ConfigManager", "gui_config_reloaded",
                "&d{gui}.yml ricaricato!")
                .replace("{gui}", guiName);
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
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
