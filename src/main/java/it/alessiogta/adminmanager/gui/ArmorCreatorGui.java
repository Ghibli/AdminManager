package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ArmorCreatorGui extends BaseGui {

    private final Player targetPlayer;
    private ItemStack helmet, chestplate, leggings, boots;
    private int protectionLevel = 0;
    private int projectileProtectionLevel = 0;

    public enum ArmorMaterial {
        CHAINMAIL("Chainmail", Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE,
                  Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS),
        NETHERITE("Netherite", Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE,
                  Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS),
        DIAMOND("Diamond", Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
                Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS),
        IRON("Iron", Material.IRON_HELMET, Material.IRON_CHESTPLATE,
             Material.IRON_LEGGINGS, Material.IRON_BOOTS),
        GOLDEN("Golden", Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE,
               Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS),
        LEATHER("Leather", Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
                Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);

        private final String displayName;
        private final Material helmetMat, chestplateMat, leggingsMat, bootsMat;

        ArmorMaterial(String displayName, Material helmet, Material chestplate, Material leggings, Material boots) {
            this.displayName = displayName;
            this.helmetMat = helmet;
            this.chestplateMat = chestplate;
            this.leggingsMat = leggings;
            this.bootsMat = boots;
        }

        public Material getHelmet() { return helmetMat; }
        public Material getChestplate() { return chestplateMat; }
        public Material getLeggings() { return leggingsMat; }
        public Material getBoots() { return bootsMat; }
        public String getDisplayName() { return displayName; }
    }

    public ArmorCreatorGui(Player player, Player targetPlayer) {
        super(player, formatTitle(targetPlayer), 1);
        this.targetPlayer = targetPlayer;
        setupGuiItems();
    }

    private static String formatTitle(Player targetPlayer) {
        return "§6§lARMOR CREATOR";
    }

    private void setupGuiItems() {
        // Left column: Material buttons (slots 0, 9, 18, 27, 36, 45)
        setItem(0, createMaterialButton(ArmorMaterial.CHAINMAIL));
        setItem(9, createMaterialButton(ArmorMaterial.NETHERITE));
        setItem(18, createMaterialButton(ArmorMaterial.DIAMOND));
        setItem(27, createMaterialButton(ArmorMaterial.IRON));
        setItem(36, createMaterialButton(ArmorMaterial.GOLDEN));
        setItem(45, createMaterialButton(ArmorMaterial.LEATHER));

        // Center: Armor preview (two 3x3 areas)
        // First preview area: slots 10-12, 19-21, 28-30
        // Second preview area: slots 14-16, 23-25, 32-34
        updateArmorPreview();

        // Bottom row: Control buttons
        setItem(46, createProtectionButton());
        setItem(47, createYourArmorButton());
        setItem(48, createClearButton());
        setItem(49, createProjectileProtectionButton());
        setItem(50, createGiveArmorButton());

        // Right side info panel (slot 52)
        setItem(52, createInfoPanel());

        // Exit button (slot 53)
        setItem(53, createExitButton());
    }

    private ItemStack createMaterialButton(ArmorMaterial material) {
        ItemStack item = new ItemStack(material.getHelmet());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§e§l" + material.getDisplayName().toUpperCase());
            meta.setLore(Arrays.asList(
                "§7",
                "§eDOUBLE CLICK §7ALL",
                "§7",
                "§eLEFT §7HELMET",
                "§eRIGHT §7CHESTPLATE",
                "§eLEFT + SHIFT §7LEGGINGS",
                "§eRIGHT + SHIFT §7BOOTS"
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private void updateArmorPreview() {
        // Clear preview areas first
        for (int i = 10; i <= 12; i++) setItem(i, new ItemStack(Material.AIR));
        for (int i = 19; i <= 21; i++) setItem(i, new ItemStack(Material.AIR));
        for (int i = 28; i <= 30; i++) setItem(i, new ItemStack(Material.AIR));

        for (int i = 14; i <= 16; i++) setItem(i, new ItemStack(Material.AIR));
        for (int i = 23; i <= 25; i++) setItem(i, new ItemStack(Material.AIR));
        for (int i = 32; i <= 34; i++) setItem(i, new ItemStack(Material.AIR));

        // First preview area (left) - Show armor pieces
        if (helmet != null) setItem(11, helmet.clone());
        if (chestplate != null) setItem(20, chestplate.clone());
        if (leggings != null) setItem(29, leggings.clone());

        // Second preview area (right) - Show armor pieces
        if (helmet != null) setItem(15, helmet.clone());
        if (chestplate != null) setItem(24, chestplate.clone());
        if (leggings != null) setItem(33, leggings.clone());

        // Boots in bottom of second area
        if (boots != null) {
            setItem(30, boots.clone());
            setItem(34, boots.clone());
        }
    }

    private ItemStack createProtectionButton() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§9§lPROTECTION");
            meta.setLore(Arrays.asList(
                "§7Current level: §f" + protectionLevel,
                "§7",
                "§aLEFT §7OPEN GUI"
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createProjectileProtectionButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§9PROJECTILE PROTECTION " + projectileProtectionLevel);
            meta.setLore(Arrays.asList(
                "§7",
                "§aLEFT §7ADD FOR BOOTS"
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createYourArmorButton() {
        ItemStack item = new ItemStack(Material.ARMOR_STAND);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§e§lYOUR ARMOR");
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createClearButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c§lCLEAR");
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createGiveArmorButton() {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§a§lGIVE ARMOR");
            meta.setLore(Arrays.asList(
                "§7Give armor to §e" + targetPlayer.getName()
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createInfoPanel() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6§lPROTECTION " + protectionLevel);
            meta.setLore(Arrays.asList(
                "§7",
                "§aLEFT §7ADD FOR CHESTPLATE"
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createExitButton() {
        ItemStack item = new ItemStack(Material.DARK_OAK_DOOR);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c§lBACK");
            item.setItemMeta(meta);
        }

        return item;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        ClickType clickType = event.getClick();

        // Material buttons (left column)
        if (slot == 0) handleMaterialClick(ArmorMaterial.CHAINMAIL, clickType);
        else if (slot == 9) handleMaterialClick(ArmorMaterial.NETHERITE, clickType);
        else if (slot == 18) handleMaterialClick(ArmorMaterial.DIAMOND, clickType);
        else if (slot == 27) handleMaterialClick(ArmorMaterial.IRON, clickType);
        else if (slot == 36) handleMaterialClick(ArmorMaterial.GOLDEN, clickType);
        else if (slot == 45) handleMaterialClick(ArmorMaterial.LEATHER, clickType);

        // Control buttons (bottom row)
        else if (slot == 46) handleProtectionClick(clickType);
        else if (slot == 47) handleYourArmorClick();
        else if (slot == 48) handleClearClick();
        else if (slot == 49) handleProjectileProtectionClick();
        else if (slot == 50) handleGiveArmorClick();
        else if (slot == 53) handleExitClick();

        else event.setCancelled(true);
    }

    private void handleMaterialClick(ArmorMaterial material, ClickType clickType) {
        Player sender = (Player) player;

        if (clickType == ClickType.DOUBLE_CLICK) {
            // Create full set
            helmet = new ItemStack(material.getHelmet());
            chestplate = new ItemStack(material.getChestplate());
            leggings = new ItemStack(material.getLeggings());
            boots = new ItemStack(material.getBoots());
        } else if (clickType == ClickType.LEFT) {
            // Create helmet
            helmet = new ItemStack(material.getHelmet());
        } else if (clickType == ClickType.RIGHT) {
            // Create chestplate
            chestplate = new ItemStack(material.getChestplate());
        } else if (clickType == ClickType.SHIFT_LEFT) {
            // Create leggings
            leggings = new ItemStack(material.getLeggings());
        } else if (clickType == ClickType.SHIFT_RIGHT) {
            // Create boots
            boots = new ItemStack(material.getBoots());
        }

        updateArmorPreview();
        updateInfoPanel();
    }

    private void handleProtectionClick(ClickType clickType) {
        if (clickType == ClickType.LEFT) {
            // Cycle protection level 0 -> 1 -> 2 -> 3 -> 4 -> 0
            protectionLevel = (protectionLevel + 1) % 5;

            // Apply to all pieces
            if (protectionLevel > 0) {
                if (helmet != null) helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protectionLevel);
                if (chestplate != null) chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protectionLevel);
                if (leggings != null) leggings.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protectionLevel);
                if (boots != null) boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protectionLevel);
            } else {
                if (helmet != null) helmet.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
                if (chestplate != null) chestplate.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
                if (leggings != null) leggings.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
                if (boots != null) boots.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
            }

            updateArmorPreview();
            setItem(46, createProtectionButton());
            updateInfoPanel();
        }
    }

    private void handleProjectileProtectionClick() {
        projectileProtectionLevel = (projectileProtectionLevel + 1) % 5;

        if (boots != null) {
            if (projectileProtectionLevel > 0) {
                boots.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, projectileProtectionLevel);
            } else {
                boots.removeEnchantment(Enchantment.PROTECTION_PROJECTILE);
            }
        }

        updateArmorPreview();
        setItem(49, createProjectileProtectionButton());
    }

    private void handleYourArmorClick() {
        // Show player's current armor
        Player sender = (Player) player;
        ItemStack[] armor = targetPlayer.getInventory().getArmorContents();

        sender.sendMessage("§e" + targetPlayer.getName() + "'s armor:");
        sender.sendMessage("§7Helmet: " + (armor[3] != null ? armor[3].getType().name() : "None"));
        sender.sendMessage("§7Chestplate: " + (armor[2] != null ? armor[2].getType().name() : "None"));
        sender.sendMessage("§7Leggings: " + (armor[1] != null ? armor[1].getType().name() : "None"));
        sender.sendMessage("§7Boots: " + (armor[0] != null ? armor[0].getType().name() : "None"));
    }

    private void handleClearClick() {
        helmet = null;
        chestplate = null;
        leggings = null;
        boots = null;
        protectionLevel = 0;
        projectileProtectionLevel = 0;

        updateArmorPreview();
        setItem(46, createProtectionButton());
        setItem(49, createProjectileProtectionButton());
        updateInfoPanel();

        player.sendMessage("§cArmor cleared!");
    }

    private void handleGiveArmorClick() {
        int given = 0;

        if (helmet != null) {
            targetPlayer.getInventory().addItem(helmet.clone());
            given++;
        }
        if (chestplate != null) {
            targetPlayer.getInventory().addItem(chestplate.clone());
            given++;
        }
        if (leggings != null) {
            targetPlayer.getInventory().addItem(leggings.clone());
            given++;
        }
        if (boots != null) {
            targetPlayer.getInventory().addItem(boots.clone());
            given++;
        }

        if (given > 0) {
            player.sendMessage("§aGave " + given + " armor piece(s) to §e" + targetPlayer.getName());
            if (targetPlayer.isOnline()) {
                targetPlayer.sendMessage("§aYou received armor from an admin!");
            }
        } else {
            player.sendMessage("§cNo armor to give!");
        }
    }

    private void handleExitClick() {
        player.closeInventory();
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerManage((Player) player, targetPlayer).open()
        );
    }

    private void updateInfoPanel() {
        setItem(52, createInfoPanel());
    }
}
