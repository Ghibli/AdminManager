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
 * GUI for selecting special tools (Bow, Crossbow, Fishing Rod, Trident, Shears)
 * These tools don't have material variants, so they skip the material selector
 */
public class SpecialToolsGui extends BaseGui {

    private final Player admin;
    private final Player targetPlayer;
    private final Map<Integer, ToolType> slotToTool = new HashMap<>();

    public SpecialToolsGui(Player admin, Player targetPlayer) {
        super(admin, TranslationManager.translate("ToolCreator", "special_tools_title", "&6&lSpecial Tools"), 1);
        this.admin = admin;
        this.targetPlayer = targetPlayer;
        setupGuiItems();
    }

    @Override
    protected void setupNavigationButtons() {
        // Disable BaseGui's automatic navigation buttons
    }

    private void setupGuiItems() {
        // Layout: Special tools arranged in a row
        // Bow, Crossbow, Fishing Rod, Trident, Shears

        int[] slots = {11, 13, 15, 20, 24}; // Centered layout
        ToolType[] specialTools = {
            ToolType.BOW,
            ToolType.CROSSBOW,
            ToolType.FISHING_ROD,
            ToolType.TRIDENT,
            ToolType.SHEARS
        };

        for (int i = 0; i < specialTools.length && i < slots.length; i++) {
            ToolType tool = specialTools[i];
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
            "&7Crea un/a " + tool.getDisplayName().toLowerCase() + "\n&7personalizzato/a\n\n&e&lCLICK: &7Personalizza");

        return createItem(tool.getIcon(), title, lore.split("\n"));
    }

    private ItemStack createTargetPlayerInfo() {
        String title = TranslationManager.translate("ToolCreator", "target_player_title",
            "&6&lGiocatore Target");

        String lore = TranslationManager.translate("ToolCreator", "target_player_lore",
            "&7Gli strumenti creati verranno\n&7dati a: &e{player}")
            .replace("{player}", targetPlayer.getName());

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        org.bukkit.inventory.meta.SkullMeta meta = (org.bukkit.inventory.meta.SkullMeta) playerHead.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(targetPlayer);
            meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', title));

            java.util.List<String> loreList = new java.util.ArrayList<>();
            for (String line : lore.split("\n")) {
                loreList.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(loreList);

            playerHead.setItemMeta(meta);
        }

        return playerHead;
    }

    private ItemStack createBackButton() {
        String title = TranslationManager.translate("ToolCreator", "back_button_title", "&cIndietro");
        String lore = TranslationManager.translate("ToolCreator", "back_to_tools_lore", "&7Torna alla selezione strumento");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        if (slot == 49) {
            // Back button - return to main tool creator
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
        // Special tools don't have material variants, go directly to customization
        // Use a dummy material (WOOD) since it will be ignored
        org.bukkit.event.HandlerList.unregisterAll(this);
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new ToolCustomizationGui(clicker, targetPlayer, tool, ToolMaterial.WOOD).open()
        );
    }

    private void handleBack(Player clicker) {
        // Return to main Tool Creator GUI
        org.bukkit.event.HandlerList.unregisterAll(this);
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new ToolCreatorGui(clicker, targetPlayer).open()
        );
    }

    @Override
    public void open() {
        inventory = build();
        admin.openInventory(inventory);
    }
}
