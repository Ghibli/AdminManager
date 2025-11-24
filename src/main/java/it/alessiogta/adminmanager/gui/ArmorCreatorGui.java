package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class ArmorCreatorGui extends BaseGui {

    private final Player targetPlayer;
    private final Map<ArmorPiece, ItemStack> armorPieces;

    public enum ArmorPiece {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS
    }

    public enum ArmorMaterial {
        CHAINMAIL(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS),
        NETHERITE(Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS),
        DIAMOND(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS),
        IRON(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS),
        GOLDEN(Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS),
        LEATHER(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);

        private final Material helmet, chestplate, leggings, boots;

        ArmorMaterial(Material helmet, Material chestplate, Material leggings, Material boots) {
            this.helmet = helmet;
            this.chestplate = chestplate;
            this.leggings = leggings;
            this.boots = boots;
        }

        public Material getHelmet() { return helmet; }
        public Material getChestplate() { return chestplate; }
        public Material getLeggings() { return leggings; }
        public Material getBoots() { return boots; }
    }

    public ArmorCreatorGui(Player player, Player targetPlayer) {
        super(player, formatTitle(targetPlayer), 1);
        this.targetPlayer = targetPlayer;
        this.armorPieces = new HashMap<>();
        setupGuiItems();
    }

    private static String formatTitle(Player targetPlayer) {
        return String.format("§a[§6%s§a] §7Armor Creator", targetPlayer.getName());
    }

    private void setupGuiItems() {
        // Left column: Materials
        setItem(0, createMaterialButton(ArmorMaterial.CHAINMAIL));
        setItem(9, createMaterialButton(ArmorMaterial.NETHERITE));
        setItem(18, createMaterialButton(ArmorMaterial.DIAMOND));
        setItem(27, createMaterialButton(ArmorMaterial.IRON));
        setItem(36, createMaterialButton(ArmorMaterial.GOLDEN));
        setItem(45, createMaterialButton(ArmorMaterial.LEATHER));

        // Center: Armor preview
        updateArmorPreview();

        // Bottom row: Action buttons
        setItem(47, createClearButton());
        setItem(48, createProtectionButton());
        setItem(50, createGiveArmorButton());

        // Exit button
        setItem(53, createExitButton());
    }

    private ItemStack createMaterialButton(ArmorMaterial material) {
        String name = material.name().substring(0, 1) + material.name().substring(1).toLowerCase();
        String title = TranslationManager.translate("ArmorCreator", "material_" + material.name().toLowerCase() + "_title", "&e" + name);

        StringBuilder lore = new StringBuilder();
        lore.append(TranslationManager.translate("ArmorCreator", "material_lore_left", "&7LEFT: Helmet\n"));
        lore.append(TranslationManager.translate("ArmorCreator", "material_lore_right", "&7RIGHT: Chestplate\n"));
        lore.append(TranslationManager.translate("ArmorCreator", "material_lore_shift_left", "&7SHIFT+LEFT: Leggings\n"));
        lore.append(TranslationManager.translate("ArmorCreator", "material_lore_shift_right", "&7SHIFT+RIGHT: Boots\n"));
        lore.append(TranslationManager.translate("ArmorCreator", "material_lore_double", "&7DOUBLE CLICK: All"));

        return createItem(material.getHelmet(), title, lore.toString());
    }

    private void updateArmorPreview() {
        // Helmet preview
        ItemStack helmet = armorPieces.get(ArmorPiece.HELMET);
        setItem(13, helmet != null ? helmet.clone() : createEmptySlot("Helmet"));

        // Chestplate preview
        ItemStack chestplate = armorPieces.get(ArmorPiece.CHESTPLATE);
        setItem(22, chestplate != null ? chestplate.clone() : createEmptySlot("Chestplate"));

        // Leggings preview
        ItemStack leggings = armorPieces.get(ArmorPiece.LEGGINGS);
        setItem(31, leggings != null ? leggings.clone() : createEmptySlot("Leggings"));

        // Boots preview
        ItemStack boots = armorPieces.get(ArmorPiece.BOOTS);
        setItem(40, boots != null ? boots.clone() : createEmptySlot("Boots"));
    }

    private ItemStack createEmptySlot(String pieceName) {
        String title = TranslationManager.translate("ArmorCreator", "empty_slot_title", "&7Empty " + pieceName);
        return createItem(Material.GRAY_STAINED_GLASS_PANE, title, "");
    }

    private ItemStack createClearButton() {
        String title = TranslationManager.translate("ArmorCreator", "clear_title", "&cClear All");
        String lore = TranslationManager.translate("ArmorCreator", "clear_lore", "&7Remove all armor pieces");
        return createItem(Material.BARRIER, title, lore);
    }

    private ItemStack createProtectionButton() {
        String title = TranslationManager.translate("ArmorCreator", "protection_title", "&9Protection");
        String lore = TranslationManager.translate("ArmorCreator", "protection_lore", "&7Click to add Protection IV");
        return createItem(Material.ENCHANTED_BOOK, title, lore);
    }

    private ItemStack createGiveArmorButton() {
        String title = TranslationManager.translate("ArmorCreator", "give_armor_title", "&aGive Armor");
        String lore = TranslationManager.translate("ArmorCreator", "give_armor_lore", "&7Give armor to {player}")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.ARMOR_STAND, title, lore);
    }

    private ItemStack createExitButton() {
        String title = TranslationManager.translate("ArmorCreator", "back_button_title", "&cBack");
        String lore = TranslationManager.translate("ArmorCreator", "back_button_lore", "&aReturn to player management");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        ClickType clickType = event.getClick();

        // Material buttons (left column)
        if (slot == 0) handleMaterialClick(event, ArmorMaterial.CHAINMAIL, clickType);
        else if (slot == 9) handleMaterialClick(event, ArmorMaterial.NETHERITE, clickType);
        else if (slot == 18) handleMaterialClick(event, ArmorMaterial.DIAMOND, clickType);
        else if (slot == 27) handleMaterialClick(event, ArmorMaterial.IRON, clickType);
        else if (slot == 36) handleMaterialClick(event, ArmorMaterial.GOLDEN, clickType);
        else if (slot == 45) handleMaterialClick(event, ArmorMaterial.LEATHER, clickType);

        // Action buttons
        else if (slot == 47) handleClearClick(event);
        else if (slot == 48) handleProtectionClick(event);
        else if (slot == 50) handleGiveArmorClick(event);
        else if (slot == 53) handleExitClick(event);

        else event.setCancelled(true);
    }

    private void handleMaterialClick(InventoryClickEvent event, ArmorMaterial material, ClickType clickType) {
        Player sender = (Player) event.getWhoClicked();

        switch (clickType) {
            case LEFT:
                armorPieces.put(ArmorPiece.HELMET, new ItemStack(material.getHelmet()));
                break;
            case RIGHT:
                armorPieces.put(ArmorPiece.CHESTPLATE, new ItemStack(material.getChestplate()));
                break;
            case SHIFT_LEFT:
                armorPieces.put(ArmorPiece.LEGGINGS, new ItemStack(material.getLeggings()));
                break;
            case SHIFT_RIGHT:
                armorPieces.put(ArmorPiece.BOOTS, new ItemStack(material.getBoots()));
                break;
            case DOUBLE_CLICK:
                armorPieces.put(ArmorPiece.HELMET, new ItemStack(material.getHelmet()));
                armorPieces.put(ArmorPiece.CHESTPLATE, new ItemStack(material.getChestplate()));
                armorPieces.put(ArmorPiece.LEGGINGS, new ItemStack(material.getLeggings()));
                armorPieces.put(ArmorPiece.BOOTS, new ItemStack(material.getBoots()));
                break;
            default:
                event.setCancelled(true);
                return;
        }

        // Refresh GUI
        updateArmorPreview();
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ArmorCreator", "armor_updated", "&aArmor updated!")));
    }

    private void handleClearClick(InventoryClickEvent event) {
        armorPieces.clear();
        updateArmorPreview();

        Player sender = (Player) event.getWhoClicked();
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ArmorCreator", "armor_cleared", "&cArmor cleared!")));
    }

    private void handleProtectionClick(InventoryClickEvent event) {
        // Add Protection IV to all armor pieces
        for (ItemStack armor : armorPieces.values()) {
            if (armor != null) {
                armor.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
            }
        }

        updateArmorPreview();
        Player sender = (Player) event.getWhoClicked();
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ArmorCreator", "enchantment_added", "&aProtection IV added!")));
    }

    private void handleGiveArmorClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();

        if (armorPieces.isEmpty()) {
            sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                    TranslationManager.translate("ArmorCreator", "no_armor", "&cNo armor to give!")));
            return;
        }

        // Give armor to target player
        for (Map.Entry<ArmorPiece, ItemStack> entry : armorPieces.entrySet()) {
            if (entry.getValue() != null) {
                targetPlayer.getInventory().addItem(entry.getValue().clone());
            }
        }

        String message = TranslationManager.translate("ArmorCreator", "armor_given", "&aArmor given to {player}!")
                .replace("{player}", targetPlayer.getName());
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        if (targetPlayer.isOnline()) {
            String playerMessage = TranslationManager.translate("ArmorCreator", "armor_received", "&aYou received armor!");
            targetPlayer.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', playerMessage));
        }
    }

    private void handleExitClick(InventoryClickEvent event) {
        new PlayerManage((Player) event.getWhoClicked(), targetPlayer).open();
    }
}
