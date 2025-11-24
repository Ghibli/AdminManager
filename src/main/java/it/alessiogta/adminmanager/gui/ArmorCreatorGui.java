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

    private final Player admin;
    private final Player targetPlayer;
    private ItemStack helmet, chestplate, leggings, boots;
    private int helmetProtection = 0;
    private int chestplateProtection = 0;
    private int leggingsProtection = 0;
    private int bootsProtection = 0;
    private int bootsProjectileProtection = 0;

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
        this.admin = player;
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

        // Center: Armor preview with enchantment books
        // Helmet at 13, Protection book at 14
        // Chestplate at 22, Protection book at 23
        // Leggings at 31, Protection book at 32
        // Boots at 40, Protection book at 41, Projectile Protection at 42
        updateArmorPreview();
        updateEnchantmentBooks();

        // Bottom row: Control buttons
        setItem(47, createYourArmorButton());
        setItem(48, createClearButton());
        setItem(50, createGiveArmorButton());

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
        // Clear preview area (center column slots: 13, 22, 31, 40)
        setItem(13, helmet != null ? helmet.clone() : createEmptySlot("HELMET"));
        setItem(22, chestplate != null ? chestplate.clone() : createEmptySlot("CHESTPLATE"));
        setItem(31, leggings != null ? leggings.clone() : createEmptySlot("LEGGINGS"));
        setItem(40, boots != null ? boots.clone() : createEmptySlot("BOOTS"));
    }

    private ItemStack createEmptySlot(String name) {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§7" + name);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void updateEnchantmentBooks() {
        // Helmet Protection (slot 14)
        setItem(14, createProtectionBook("HELMET", helmetProtection));

        // Chestplate Protection (slot 23)
        setItem(23, createProtectionBook("CHESTPLATE", chestplateProtection));

        // Leggings Protection (slot 32)
        setItem(32, createProtectionBook("LEGGINGS", leggingsProtection));

        // Boots Protection (slot 41)
        setItem(41, createProtectionBook("BOOTS", bootsProtection));

        // Boots Projectile Protection (slot 42)
        setItem(42, createProjectileProtectionBook(bootsProjectileProtection));
    }

    private ItemStack createProtectionBook(String armorPiece, int level) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§9§lPROTECTION " + (level > 0 ? level : ""));
            meta.setLore(Arrays.asList(
                "§7For: §e" + armorPiece,
                "§7Level: §f" + level,
                "§7",
                "§aLEFT §7Cycle 1→2→3→4"
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createProjectileProtectionBook(int level) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§b§lPROJECTILE PROT " + (level > 0 ? level : ""));
            meta.setLore(Arrays.asList(
                "§7For: §eBOOTS",
                "§7Level: §f" + level,
                "§7",
                "§aLEFT §7Cycle 1→2→3→4"
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

        // Enchantment books
        else if (slot == 14) handleHelmetProtectionClick();
        else if (slot == 23) handleChestplateProtectionClick();
        else if (slot == 32) handleLeggingsProtectionClick();
        else if (slot == 41) handleBootsProtectionClick();
        else if (slot == 42) handleBootsProjectileProtectionClick();

        // Control buttons (bottom row)
        else if (slot == 47) handleYourArmorClick();
        else if (slot == 48) handleClearClick();
        else if (slot == 50) handleGiveArmorClick();
        else if (slot == 53) handleExitClick();

        else event.setCancelled(true);
    }

    private void handleMaterialClick(ArmorMaterial material, ClickType clickType) {
        if (clickType == ClickType.DOUBLE_CLICK) {
            // Create full set
            helmet = new ItemStack(material.getHelmet());
            chestplate = new ItemStack(material.getChestplate());
            leggings = new ItemStack(material.getLeggings());
            boots = new ItemStack(material.getBoots());

            // Apply existing enchantments
            applyEnchantmentsToArmor();
        } else if (clickType == ClickType.LEFT) {
            // Create helmet
            helmet = new ItemStack(material.getHelmet());
            applyHelmetEnchantments();
        } else if (clickType == ClickType.RIGHT) {
            // Create chestplate
            chestplate = new ItemStack(material.getChestplate());
            applyChestplateEnchantments();
        } else if (clickType == ClickType.SHIFT_LEFT) {
            // Create leggings
            leggings = new ItemStack(material.getLeggings());
            applyLeggingsEnchantments();
        } else if (clickType == ClickType.SHIFT_RIGHT) {
            // Create boots
            boots = new ItemStack(material.getBoots());
            applyBootsEnchantments();
        }

        updateArmorPreview();
    }

    private void handleHelmetProtectionClick() {
        // Cycle 1 -> 2 -> 3 -> 4 -> 1
        helmetProtection = helmetProtection == 4 ? 1 : (helmetProtection == 0 ? 1 : helmetProtection + 1);
        applyHelmetEnchantments();
        updateArmorPreview();
        updateEnchantmentBooks();
    }

    private void handleChestplateProtectionClick() {
        // Cycle 1 -> 2 -> 3 -> 4 -> 1
        chestplateProtection = chestplateProtection == 4 ? 1 : (chestplateProtection == 0 ? 1 : chestplateProtection + 1);
        applyChestplateEnchantments();
        updateArmorPreview();
        updateEnchantmentBooks();
    }

    private void handleLeggingsProtectionClick() {
        // Cycle 1 -> 2 -> 3 -> 4 -> 1
        leggingsProtection = leggingsProtection == 4 ? 1 : (leggingsProtection == 0 ? 1 : leggingsProtection + 1);
        applyLeggingsEnchantments();
        updateArmorPreview();
        updateEnchantmentBooks();
    }

    private void handleBootsProtectionClick() {
        // Cycle 1 -> 2 -> 3 -> 4 -> 1
        bootsProtection = bootsProtection == 4 ? 1 : (bootsProtection == 0 ? 1 : bootsProtection + 1);
        applyBootsEnchantments();
        updateArmorPreview();
        updateEnchantmentBooks();
    }

    private void handleBootsProjectileProtectionClick() {
        // Cycle 1 -> 2 -> 3 -> 4 -> 1
        bootsProjectileProtection = bootsProjectileProtection == 4 ? 1 : (bootsProjectileProtection == 0 ? 1 : bootsProjectileProtection + 1);
        applyBootsEnchantments();
        updateArmorPreview();
        updateEnchantmentBooks();
    }

    private void applyHelmetEnchantments() {
        if (helmet != null && helmetProtection > 0) {
            helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, helmetProtection);
        }
    }

    private void applyChestplateEnchantments() {
        if (chestplate != null && chestplateProtection > 0) {
            chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, chestplateProtection);
        }
    }

    private void applyLeggingsEnchantments() {
        if (leggings != null && leggingsProtection > 0) {
            leggings.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, leggingsProtection);
        }
    }

    private void applyBootsEnchantments() {
        if (boots != null) {
            if (bootsProtection > 0) {
                boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, bootsProtection);
            }
            if (bootsProjectileProtection > 0) {
                boots.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, bootsProjectileProtection);
            }
        }
    }

    private void applyEnchantmentsToArmor() {
        applyHelmetEnchantments();
        applyChestplateEnchantments();
        applyLeggingsEnchantments();
        applyBootsEnchantments();
    }

    private void handleYourArmorClick() {
        // Show player's current armor
        Player sender = admin;
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
        helmetProtection = 0;
        chestplateProtection = 0;
        leggingsProtection = 0;
        bootsProtection = 0;
        bootsProjectileProtection = 0;

        updateArmorPreview();
        updateEnchantmentBooks();

        admin.sendMessage("§cArmor cleared!");
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
            admin.sendMessage("§aGave " + given + " armor piece(s) to §e" + targetPlayer.getName());
            if (targetPlayer.isOnline()) {
                targetPlayer.sendMessage("§aYou received armor from an admin!");
            }
        } else {
            admin.sendMessage("§cNo armor to give!");
        }
    }

    private void handleExitClick() {
        admin.closeInventory();
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerManage(admin, targetPlayer).open()
        );
    }

    private void updateInfoPanel() {
        setItem(52, createInfoPanel());
    }
}
