package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Main hub GUI for selecting which tool type to create
 */
public class ToolCreatorGui extends BaseGui {

    private final Player admin;
    private final Player targetPlayer;
    private final Map<Integer, ToolType> slotToTool = new HashMap<>();

    public ToolCreatorGui(Player admin, Player targetPlayer) {
        super(admin, TranslationManager.translate("ToolCreator", "title", "&6&lTool Creator"), 1);
        this.admin = admin;
        this.targetPlayer = targetPlayer;
        setupGuiItems();
    }

    @Override
    protected void setupNavigationButtons() {
        // Disable BaseGui's automatic navigation buttons
    }

    private void setupGuiItems() {
        // Layout: Tools arranged in a row
        // Row 2: Sword, Pickaxe, Shovel, Hoe, Axe
        // Bottom: Back button

        int[] slots = {10, 12, 14, 16, 19}; // Centered layout
        ToolType[] tools = ToolType.values();

        for (int i = 0; i < tools.length && i < slots.length; i++) {
            ToolType tool = tools[i];
            int slot = slots[i];
            slotToTool.put(slot, tool);
            setItem(slot, createToolButton(tool));
        }

        // Target player info
        setItem(4, createTargetPlayerInfo());

        // Back button (slot 49)
        setItem(49, createBackButton());
    }

    private ItemStack createToolButton(ToolType tool) {
        String title = TranslationManager.translate("ToolCreator", "tool_" + tool.name().toLowerCase() + "_title",
            "&e&l" + tool.getDisplayName());

        String lore = TranslationManager.translate("ToolCreator", "tool_" + tool.name().toLowerCase() + "_lore",
            "&7Crea una " + tool.getDisplayName().toLowerCase() + "\n&7personalizzata\n\n&e&lCLICK: &7Seleziona materiale");

        return createItem(tool.getIcon(), title, lore.split("\n"));
    }

    private ItemStack createTargetPlayerInfo() {
        String title = TranslationManager.translate("ToolCreator", "target_player_title",
            "&6&lGiocatore Target");

        String lore = TranslationManager.translate("ToolCreator", "target_player_lore",
            "&7Gli strumenti creati verranno\n&7dati a: &e{player}")
            .replace("{player}", targetPlayer.getName());

        return createItem(Material.PLAYER_HEAD, title, lore.split("\n"));
    }

    private ItemStack createBackButton() {
        String title = TranslationManager.translate("ToolCreator", "back_button_title", "&cIndietro");
        String lore = TranslationManager.translate("ToolCreator", "back_button_lore", "&7Torna a Player Manage");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        if (slot == 49) {
            // Back button
            handleBack(clicker);
            return;
        }

        // Check if clicked slot has a tool type
        if (slotToTool.containsKey(slot)) {
            ToolType tool = slotToTool.get(slot);
            handleToolClick(tool, clicker);
        }
    }

    private void handleToolClick(ToolType tool, Player clicker) {
        // Deregister listener (inventory closes automatically)
        org.bukkit.event.HandlerList.unregisterAll(this);
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new ToolMaterialSelectorGui(clicker, targetPlayer, tool).open()
        );
    }

    private void handleBack(Player clicker) {
        // Deregister listener (inventory closes automatically)
        org.bukkit.event.HandlerList.unregisterAll(this);
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerManage(clicker, targetPlayer).open()
        );
    }

    @Override
    public void open() {
        inventory = build();
        admin.openInventory(inventory);
    }
}
