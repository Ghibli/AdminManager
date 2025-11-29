package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GameRuleValueGui extends BaseGui {

    private final Player admin;
    private final World targetWorld;
    private final GameRule<Integer> rule;
    private final String ruleName;
    private int currentValue;

    // Preset values for common game rules
    private static final Map<String, int[]> RULE_PRESETS = new HashMap<String, int[]>() {{
        put("randomTickSpeed", new int[]{0, 1, 3, 10, 20, 100, 1000});
        put("spawnRadius", new int[]{0, 5, 10, 15, 20, 50});
        put("maxEntityCramming", new int[]{0, 8, 24, 50, 100});
        put("playersSleepingPercentage", new int[]{0, 1, 50, 100});
        put("maxCommandChainLength", new int[]{1000, 10000, 65536, 100000});
    }};

    // Default presets for rules not in the map
    private static final int[] DEFAULT_PRESETS = new int[]{0, 1, 5, 10, 50, 100, 1000};

    public GameRuleValueGui(Player admin, World world, GameRule<Integer> rule, String ruleName) {
        super(admin, TranslationManager.translate("GameRuleValue", "title", "&6&lModifica: {rule}")
            .replace("{rule}", ruleName), 1);
        this.admin = admin;
        this.targetWorld = world;
        this.rule = rule;
        this.ruleName = ruleName;
        this.currentValue = world.getGameRuleValue(rule);
        setupGuiItems();
    }

    @Override
    protected void setupNavigationButtons() {
        // Disable BaseGui's automatic navigation buttons
    }

    private void setupGuiItems() {
        // Current value display (slot 4)
        setItem(4, createCurrentValueDisplay());

        // Preset values (slots 10-16)
        int[] presets = RULE_PRESETS.getOrDefault(ruleName, DEFAULT_PRESETS);
        int slot = 10;
        for (int preset : presets) {
            if (slot > 16) break;
            setItem(slot, createPresetButton(preset));
            slot++;
        }

        // Decrement buttons
        setItem(19, createAdjustButton(-100, Material.RED_CONCRETE, "&c-100"));
        setItem(20, createAdjustButton(-10, Material.ORANGE_CONCRETE, "&6-10"));
        setItem(21, createAdjustButton(-1, Material.YELLOW_CONCRETE, "&e-1"));

        // Increment buttons
        setItem(23, createAdjustButton(1, Material.LIME_CONCRETE, "&a+1"));
        setItem(24, createAdjustButton(10, Material.GREEN_CONCRETE, "&2+10"));
        setItem(25, createAdjustButton(100, Material.DARK_GREEN_CONCRETE, "&2+100"));

        // Manual input button (slot 31)
        setItem(31, createManualInputButton());

        // Cancel button (slot 45)
        setItem(45, createCancelButton());

        // Confirm button (slot 49)
        setItem(49, createConfirmButton());
    }

    private ItemStack createCurrentValueDisplay() {
        String title = TranslationManager.translate("GameRuleValue", "current_value_title",
            "&6&lValore Corrente");
        String lore = TranslationManager.translate("GameRuleValue", "current_value_lore",
            "&7Regola: &e{rule}\n&7Mondo: &e{world}\n\n&6→ &e{value}")
            .replace("{rule}", ruleName)
            .replace("{world}", targetWorld.getName())
            .replace("{value}", String.valueOf(currentValue));

        return createItem(Material.PAPER, title, lore.split("\n"));
    }

    private ItemStack createPresetButton(int value) {
        String title = TranslationManager.translate("GameRuleValue", "preset_title", "&e{value}")
            .replace("{value}", String.valueOf(value));

        String lore = TranslationManager.translate("GameRuleValue", "preset_lore",
            "&7Imposta il valore a &e{value}\n\n&e&lCLICK: &7Imposta")
            .replace("{value}", String.valueOf(value));

        Material material = value == 0 ? Material.BARRIER :
                          value < 10 ? Material.GOLD_NUGGET :
                          value < 100 ? Material.GOLD_INGOT : Material.GOLD_BLOCK;

        return createItem(material, title, lore.split("\n"));
    }

    private ItemStack createAdjustButton(int delta, Material material, String coloredDelta) {
        String title = TranslationManager.translate("GameRuleValue", "adjust_title", coloredDelta);

        String lore = TranslationManager.translate("GameRuleValue", "adjust_lore",
            "&7Modifica il valore di &e{delta}\n&7Valore risultante: &e{result}\n\n&e&lCLICK: &7Modifica")
            .replace("{delta}", coloredDelta)
            .replace("{result}", String.valueOf(Math.max(0, currentValue + delta)));

        return createItem(material, title, lore.split("\n"));
    }

    private ItemStack createManualInputButton() {
        String title = TranslationManager.translate("GameRuleValue", "manual_input_title",
            "&d&lInput Manuale");
        String lore = TranslationManager.translate("GameRuleValue", "manual_input_lore",
            "&7Inserisci un valore personalizzato\n&7tramite chat\n\n&e&lCLICK: &7Attiva input");

        return createItem(Material.WRITABLE_BOOK, title, lore.split("\n"));
    }

    private ItemStack createCancelButton() {
        String title = TranslationManager.translate("GameRuleValue", "cancel_title", "&cAnnulla");
        String lore = TranslationManager.translate("GameRuleValue", "cancel_lore",
            "&7Annulla le modifiche\n&7e torna indietro");

        return createItem(Material.DARK_OAK_DOOR, title, lore.split("\n"));
    }

    private ItemStack createConfirmButton() {
        String title = TranslationManager.translate("GameRuleValue", "confirm_title", "&a&lConferma");
        String lore = TranslationManager.translate("GameRuleValue", "confirm_lore",
            "&7Applica il valore &e{value}\n&7al mondo &6{world}")
            .replace("{value}", String.valueOf(currentValue))
            .replace("{world}", targetWorld.getName());

        return createItem(Material.LIME_DYE, title, lore.split("\n"));
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        if (slot == 45) {
            // Cancel - back to GameRulesGui
            handleCancel(clicker);
            return;
        }

        if (slot == 49) {
            // Confirm - apply value
            handleConfirm(clicker);
            return;
        }

        if (slot == 31) {
            // Manual input
            handleManualInput(clicker);
            return;
        }

        // Preset buttons (10-16)
        if (slot >= 10 && slot <= 16) {
            int[] presets = RULE_PRESETS.getOrDefault(ruleName, DEFAULT_PRESETS);
            int index = slot - 10;
            if (index < presets.length) {
                setCurrentValue(presets[index]);
            }
            return;
        }

        // Adjustment buttons
        switch (slot) {
            case 19: adjustValue(-100); break;
            case 20: adjustValue(-10); break;
            case 21: adjustValue(-1); break;
            case 23: adjustValue(1); break;
            case 24: adjustValue(10); break;
            case 25: adjustValue(100); break;
        }
    }

    private void setCurrentValue(int value) {
        this.currentValue = Math.max(0, value); // Prevent negative values
        refreshGui();
    }

    private void adjustValue(int delta) {
        setCurrentValue(currentValue + delta);
    }

    private void refreshGui() {
        // Refresh display and buttons
        refreshSlot(4, createCurrentValueDisplay());
        refreshSlot(49, createConfirmButton());

        // Refresh adjustment buttons with new result values
        refreshSlot(19, createAdjustButton(-100, Material.RED_CONCRETE, "&c-100"));
        refreshSlot(20, createAdjustButton(-10, Material.ORANGE_CONCRETE, "&6-10"));
        refreshSlot(21, createAdjustButton(-1, Material.YELLOW_CONCRETE, "&e-1"));
        refreshSlot(23, createAdjustButton(1, Material.LIME_CONCRETE, "&a+1"));
        refreshSlot(24, createAdjustButton(10, Material.GREEN_CONCRETE, "&2+10"));
        refreshSlot(25, createAdjustButton(100, Material.DARK_GREEN_CONCRETE, "&2+100"));
    }

    private void handleConfirm(Player clicker) {
        // Apply the value to the world
        targetWorld.setGameRule(rule, currentValue);

        String message = TranslationManager.translate("GameRuleValue", "value_applied",
            "&a&lSUCCESS: &eRegola &6{rule} &eimpostata a &6{value} &ein &6{world}")
            .replace("{rule}", ruleName)
            .replace("{value}", String.valueOf(currentValue))
            .replace("{world}", targetWorld.getName());
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        // Deregister listener and return to GameRulesGui
        org.bukkit.event.HandlerList.unregisterAll(this);
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new GameRulesGui(clicker, targetWorld).open()
        );
    }

    private void handleCancel(Player clicker) {
        // Deregister listener and return to GameRulesGui without applying
        org.bukkit.event.HandlerList.unregisterAll(this);
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new GameRulesGui(clicker, targetWorld).open()
        );
    }

    private void handleManualInput(Player clicker) {
        // Register player for chat input
        GameRuleChatListener.registerInput(clicker, targetWorld, rule, ruleName);

        clicker.closeInventory();

        String message = TranslationManager.translate("GameRuleValue", "input_prompt",
            "&6Inserisci il valore per &e{rule} &6in chat\n&7Scrivi &ccancel &7per annullare")
            .replace("{rule}", ruleName);
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
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
