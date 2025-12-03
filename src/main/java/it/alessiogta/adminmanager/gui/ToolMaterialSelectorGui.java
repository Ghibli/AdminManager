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
 * GUI for selecting the material for the chosen tool type
 */
public class ToolMaterialSelectorGui extends BaseGui {

    private final Player admin;
    private final Player targetPlayer;
    private final ToolType toolType;
    private final Map<Integer, ToolMaterial> slotToMaterial = new HashMap<>();

    public ToolMaterialSelectorGui(Player admin, Player targetPlayer, ToolType toolType) {
        super(admin, TranslationManager.translate("ToolCreator", "material_selector_title",
            "&6&lMateriale - {tool}")
            .replace("{tool}", toolType.getDisplayName()), 1);
        this.admin = admin;
        this.targetPlayer = targetPlayer;
        this.toolType = toolType;
        setupGuiItems();
    }

    @Override
    protected void setupNavigationButtons() {
        // Disable BaseGui's automatic navigation buttons
    }

    private void setupGuiItems() {
        // Layout: Materials arranged in a row
        // Row 2: Wood, Stone, Iron, Gold, Diamond, Netherite
        // Top: Tool type info
        // Bottom: Back button

        int[] slots = {10, 11, 12, 14, 15, 16}; // Centered layout
        ToolMaterial[] materials = ToolMaterial.values();

        for (int i = 0; i < materials.length && i < slots.length; i++) {
            ToolMaterial material = materials[i];
            int slot = slots[i];
            slotToMaterial.put(slot, material);
            setItem(slot, createMaterialButton(material));
        }

        // Tool type info
        setItem(4, createToolTypeInfo());

        // Target player info
        setItem(22, createTargetPlayerInfo());

        // Back button (slot 49)
        setItem(49, createBackButton());
    }

    private ItemStack createMaterialButton(ToolMaterial material) {
        Material toolMaterial = toolType.getMaterial(material);

        String title = TranslationManager.translate("ToolCreator", "material_" + material.name().toLowerCase() + "_title",
            material.getDisplayName() + " " + toolType.getDisplayName());

        String durability = String.valueOf(material.getBaseDurability(toolType));
        String lore = TranslationManager.translate("ToolCreator", "material_lore",
            "&7Durabilità: &e{durability}\n\n&e&lCLICK: &7Personalizza strumento")
            .replace("{durability}", durability);

        return createItem(toolMaterial, title, lore.split("\n"));
    }

    private ItemStack createToolTypeInfo() {
        String title = TranslationManager.translate("ToolCreator", "tool_type_info_title",
            "&e&lTipo Strumento");

        String lore = TranslationManager.translate("ToolCreator", "tool_type_info_lore",
            "&7Stai creando: &e{tool}\n&7Seleziona il materiale")
            .replace("{tool}", toolType.getDisplayName());

        return createItem(toolType.getIcon(), title, lore.split("\n"));
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
            // Back button
            handleBack(clicker);
            return;
        }

        // Check if clicked slot has a material
        if (slotToMaterial.containsKey(slot)) {
            ToolMaterial material = slotToMaterial.get(slot);
            handleMaterialClick(material, clicker);
        }
    }

    private void handleMaterialClick(ToolMaterial material, Player clicker) {
        // Deregister listener (inventory closes automatically)
        org.bukkit.event.HandlerList.unregisterAll(this);
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new ToolCustomizationGui(clicker, targetPlayer, toolType, material).open()
        );
    }

    private void handleBack(Player clicker) {
        // Deregister listener (inventory closes automatically)
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
