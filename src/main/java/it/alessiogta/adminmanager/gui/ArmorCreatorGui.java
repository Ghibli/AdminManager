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

    // Armor pieces being composed
    private ItemStack helmet, chestplate, leggings, boots;

    // Enchantments for each piece
    private Map<String, Integer> helmetEnchants = new HashMap<>();
    private Map<String, Integer> chestplateEnchants = new HashMap<>();
    private Map<String, Integer> leggingsEnchants = new HashMap<>();
    private Map<String, Integer> bootsEnchants = new HashMap<>();

    // Current material selection
    private ArmorMaterial currentMaterial = ArmorMaterial.DIAMOND;

    public enum ArmorMaterial {
        CHAINMAIL("§fChainmail", Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE,
                  Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS),
        NETHERITE("§8Netherite", Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE,
                  Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS),
        DIAMOND("§bDiamond", Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
                Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS),
        IRON("§7Iron", Material.IRON_HELMET, Material.IRON_CHESTPLATE,
             Material.IRON_LEGGINGS, Material.IRON_BOOTS),
        GOLDEN("§eGolden", Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE,
               Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS),
        LEATHER("§6Leather", Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
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

        public String getDisplayName() { return displayName; }
        public Material getHelmet() { return helmetMat; }
        public Material getChestplate() { return chestplateMat; }
        public Material getLeggings() { return leggingsMat; }
        public Material getBoots() { return bootsMat; }
    }

    public ArmorCreatorGui(Player player, Player targetPlayer) {
        super(player, "§6§lArmor Creator §8- §e" + targetPlayer.getName(), 6);
        this.admin = player;
        this.targetPlayer = targetPlayer;
        setupGuiItems();
    }

    @Override
    public void open() {
        inventory = build();
        admin.openInventory(inventory);
    }

    private void refreshSlot(int slot, ItemStack item) {
        setItem(slot, item);
        if (inventory != null) {
            inventory.setItem(slot, item);
        }
    }

    private void setupGuiItems() {
        // Fill background
        fillBackground();

        // Section headers
        setItem(0, createSectionHeader("§6§lMATERIALS", Material.GOLD_INGOT));
        setItem(9, createSectionHeader("§e§lCOMPOSITION", Material.CRAFTING_TABLE));
        setItem(17, createSectionHeader("§9§lENCHANTMENTS", Material.ENCHANTING_TABLE));

        // Material selection (top row) with gold border
        ItemStack goldBorder = createBorderGlass(Material.ORANGE_STAINED_GLASS_PANE, "§6§l▌");
        setItem(10, goldBorder);
        setItem(16, goldBorder);

        setItem(1, createMaterialSelector(ArmorMaterial.LEATHER));
        setItem(2, createMaterialSelector(ArmorMaterial.CHAINMAIL));
        setItem(3, createMaterialSelector(ArmorMaterial.IRON));
        setItem(4, createMaterialSelector(ArmorMaterial.GOLDEN));
        setItem(5, createMaterialSelector(ArmorMaterial.DIAMOND));
        setItem(6, createMaterialSelector(ArmorMaterial.NETHERITE));

        // Armor composition area (center) - Dynamic ADD buttons
        setItem(11, createDynamicAddButton("HELMET"));
        setItem(20, createDynamicAddButton("CHESTPLATE"));
        setItem(29, createDynamicAddButton("LEGGINGS"));
        setItem(38, createDynamicAddButton("BOOTS"));

        // Preview slots (right of add buttons)
        updatePreview();

        // Enchantment buttons (right side) with purple border
        ItemStack purpleBorder = createBorderGlass(Material.PURPLE_STAINED_GLASS_PANE, "§5§l▌");
        setItem(14, purpleBorder);
        setItem(23, purpleBorder);
        setItem(32, purpleBorder);
        setItem(41, purpleBorder);

        setItem(15, createEnchantButton("PROTECTION", 4));
        setItem(16, createEnchantButton("UNBREAKING", 3));
        setItem(24, createEnchantButton("THORNS", 3));
        setItem(25, createEnchantButton("FIRE_PROTECTION", 4));
        setItem(33, createEnchantButton("BLAST_PROTECTION", 4));
        setItem(34, createEnchantButton("PROJECTILE_PROTECTION", 4));

        // Control buttons (bottom) with green border
        ItemStack greenBorder = createBorderGlass(Material.LIME_STAINED_GLASS_PANE, "§a§l▌");
        setItem(37, greenBorder);
        setItem(43, greenBorder);

        setItem(45, createCompositionStatusButton());
        setItem(46, createCopyMyArmorButton());
        setItem(47, createHelpButton());
        setItem(48, createClearButton());
        setItem(49, createFullSetButton());
        setItem(50, createGiveButton());
        setItem(52, createArmorStatsButton());
        setItem(53, createExitButton());
    }

    private ItemStack createSectionHeader(String name, Material icon) {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createBorderGlass(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createDynamicAddButton(String pieceType) {
        Material mat;
        String name;
        boolean hasItem = false;

        switch (pieceType) {
            case "HELMET":
                mat = currentMaterial.getHelmet();
                name = "§e§lHELMET";
                hasItem = helmet != null;
                break;
            case "CHESTPLATE":
                mat = currentMaterial.getChestplate();
                name = "§e§lCHESTPLATE";
                hasItem = chestplate != null;
                break;
            case "LEGGINGS":
                mat = currentMaterial.getLeggings();
                name = "§e§lLEGGINGS";
                hasItem = leggings != null;
                break;
            case "BOOTS":
                mat = currentMaterial.getBoots();
                name = "§e§lBOOTS";
                hasItem = boots != null;
                break;
            default:
                mat = Material.BARRIER;
                name = "§cUnknown";
        }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            if (hasItem) {
                meta.setLore(Arrays.asList(
                    "§7Material: " + currentMaterial.getDisplayName(),
                    "§7Status: §a✔ Added",
                    "§7",
                    "§cRIGHT §7Remove from composition"
                ));
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            } else {
                meta.setLore(Arrays.asList(
                    "§7Material: " + currentMaterial.getDisplayName(),
                    "§7Status: §c✘ Not added",
                    "§7",
                    "§aLEFT §7Add to composition"
                ));
            }
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createCompositionStatusButton() {
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            int pieces = 0;
            if (helmet != null) pieces++;
            if (chestplate != null) pieces++;
            if (leggings != null) pieces++;
            if (boots != null) pieces++;

            int percentage = (pieces * 100) / 4;

            meta.setDisplayName("§6§l⚡ COMPOSITION STATUS");
            meta.setLore(Arrays.asList(
                "§7§m━━━━━━━━━━━━━━━━",
                "§7Helmet:     " + (helmet != null ? "§a✔ " + getMaterialName(helmet) : "§c✘ Empty"),
                "§7Chestplate: " + (chestplate != null ? "§a✔ " + getMaterialName(chestplate) : "§c✘ Empty"),
                "§7Leggings:   " + (leggings != null ? "§a✔ " + getMaterialName(leggings) : "§c✘ Empty"),
                "§7Boots:      " + (boots != null ? "§a✔ " + getMaterialName(boots) : "§c✘ Empty"),
                "§7§m━━━━━━━━━━━━━━━━",
                "§eProgress: §a" + percentage + "% §7(" + pieces + "/4)"
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createCopyMyArmorButton() {
        ItemStack item = new ItemStack(Material.ARMOR_STAND);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§b§lCopy My Armor");
            meta.setLore(Arrays.asList(
                "§7Copy the armor you're wearing",
                "§7to the composition",
                "§7",
                "§aClick to copy"
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createHelpButton() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§e§lHOW TO USE");
            meta.setLore(Arrays.asList(
                "§7§m━━━━━━━━━━━━━━━━",
                "§e1. §7Select material (top row)",
                "§e2. §7Add pieces (left column)",
                "§e3. §7Add enchantments (right)",
                "§e4. §7Give armor (bottom)",
                "§7§m━━━━━━━━━━━━━━━━",
                "§7Preview shows your composition"
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createArmorStatsButton() {
        ItemStack item = new ItemStack(Material.SHIELD);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            double defense = 0;
            double toughness = 0;
            double knockbackRes = 0;

            // Calculate armor stats based on material
            if (helmet != null) defense += getArmorDefense(helmet, "HELMET");
            if (chestplate != null) defense += getArmorDefense(chestplate, "CHESTPLATE");
            if (leggings != null) defense += getArmorDefense(leggings, "LEGGINGS");
            if (boots != null) defense += getArmorDefense(boots, "BOOTS");

            if (currentMaterial == ArmorMaterial.DIAMOND || currentMaterial == ArmorMaterial.NETHERITE) {
                if (helmet != null) toughness += 2;
                if (chestplate != null) toughness += 2;
                if (leggings != null) toughness += 2;
                if (boots != null) toughness += 2;
            }

            if (currentMaterial == ArmorMaterial.NETHERITE) {
                if (helmet != null) knockbackRes += 10;
                if (chestplate != null) knockbackRes += 10;
                if (leggings != null) knockbackRes += 10;
                if (boots != null) knockbackRes += 10;
            }

            meta.setDisplayName("§6§lARMOR STATS");
            meta.setLore(Arrays.asList(
                "§7§m━━━━━━━━━━━━━━━━",
                "§9Defense: §f+" + (int)defense,
                "§cToughness: §f+" + (int)toughness,
                "§bKnockback Res: §f+" + (int)knockbackRes + "%",
                "§7§m━━━━━━━━━━━━━━━━"
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private double getArmorDefense(ItemStack item, String type) {
        String materialName = currentMaterial.name();

        switch (materialName) {
            case "LEATHER":
                switch (type) {
                    case "HELMET": return 1;
                    case "CHESTPLATE": return 3;
                    case "LEGGINGS": return 2;
                    case "BOOTS": return 1;
                }
                break;
            case "CHAINMAIL":
            case "GOLDEN":
                switch (type) {
                    case "HELMET": return 2;
                    case "CHESTPLATE": return 5;
                    case "LEGGINGS": return 3;
                    case "BOOTS": return 1;
                }
                break;
            case "IRON":
                switch (type) {
                    case "HELMET": return 2;
                    case "CHESTPLATE": return 6;
                    case "LEGGINGS": return 5;
                    case "BOOTS": return 2;
                }
                break;
            case "DIAMOND":
            case "NETHERITE":
                switch (type) {
                    case "HELMET": return 3;
                    case "CHESTPLATE": return 8;
                    case "LEGGINGS": return 6;
                    case "BOOTS": return 3;
                }
                break;
        }
        return 0;
    }

    private String getMaterialName(ItemStack item) {
        if (item == null) return "None";
        String name = item.getType().name();
        if (name.contains("DIAMOND")) return "§bDiamond";
        if (name.contains("NETHERITE")) return "§8Netherite";
        if (name.contains("IRON")) return "§7Iron";
        if (name.contains("GOLDEN")) return "§eGolden";
        if (name.contains("CHAINMAIL")) return "§fChainmail";
        if (name.contains("LEATHER")) return "§6Leather";
        return "§7Unknown";
    }

    private void fillBackground() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            glass.setItemMeta(meta);
        }

        // Fill border and empty spaces
        for (int i = 0; i < 54; i++) {
            if (i == 0 || i == 7 || i == 8 || i == 17 || i == 18 || i == 26 || i == 27
                || i == 35 || i == 36 || i == 44 || i == 46 || i == 47 || i == 51 || i == 52) {
                setItem(i, glass);
            }
        }
    }

    private ItemStack createMaterialSelector(ArmorMaterial material) {
        ItemStack item = new ItemStack(material.getChestplate());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            boolean selected = material == currentMaterial;
            meta.setDisplayName((selected ? "§a§l➤ " : "") + material.getDisplayName() + " Armor");
            meta.setLore(Arrays.asList(
                "§7",
                selected ? "§a§l✔ Selected" : "§eClick to select"
            ));
            item.setItemMeta(meta);
        }

        if (material == currentMaterial) {
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }

        return item;
    }

    private void updatePreview() {
        // Preview frame with colored glass
        ItemStack blueGlass = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta glassMeta = blueGlass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName("§9§l▌ PREVIEW ▌");
            blueGlass.setItemMeta(glassMeta);
        }

        // Frame around preview
        refreshSlot(12, blueGlass);
        refreshSlot(21, blueGlass);
        refreshSlot(30, blueGlass);
        refreshSlot(39, blueGlass);

        // Actual preview slots
        refreshSlot(13, helmet != null ? helmet.clone() : createEmptySlot("§7Helmet Empty"));
        refreshSlot(22, chestplate != null ? chestplate.clone() : createEmptySlot("§7Chestplate Empty"));
        refreshSlot(31, leggings != null ? leggings.clone() : createEmptySlot("§7Leggings Empty"));
        refreshSlot(40, boots != null ? boots.clone() : createEmptySlot("§7Boots Empty"));

        // Update add buttons to show current material and status
        refreshSlot(11, createDynamicAddButton("HELMET"));
        refreshSlot(20, createDynamicAddButton("CHESTPLATE"));
        refreshSlot(29, createDynamicAddButton("LEGGINGS"));
        refreshSlot(38, createDynamicAddButton("BOOTS"));
    }

    private ItemStack createEmptySlot(String name) {
        ItemStack item = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList("§8Click ADD button to add"));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createEnchantButton(String enchantType, int maxLevel) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();

        String displayName;
        switch (enchantType) {
            case "PROTECTION": displayName = "§9Protection"; break;
            case "UNBREAKING": displayName = "§bUnbreaking"; break;
            case "THORNS": displayName = "§dThorns"; break;
            case "FIRE_PROTECTION": displayName = "§cFire Protection"; break;
            case "BLAST_PROTECTION": displayName = "§8Blast Protection"; break;
            case "PROJECTILE_PROTECTION": displayName = "§fProjectile Protection"; break;
            default: displayName = enchantType;
        }

        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(Arrays.asList(
                "§7Max Level: §f" + maxLevel,
                "§7",
                "§aLEFT §7Add to selected piece",
                "§7Click piece preview to select"
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createClearButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c§lClear All");
            meta.setLore(Arrays.asList("§7Remove all armor pieces"));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createFullSetButton() {
        ItemStack item = new ItemStack(Material.ARMOR_STAND);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§a§lCreate Full Set");
            meta.setLore(Arrays.asList(
                "§7Add all 4 pieces",
                "§7Material: " + currentMaterial.getDisplayName()
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createGiveButton() {
        ItemStack item = new ItemStack(Material.LIME_DYE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§a§lGive Armor");
            meta.setLore(Arrays.asList(
                "§7Give composed armor to",
                "§e" + targetPlayer.getName()
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createExitButton() {
        ItemStack item = new ItemStack(Material.DARK_OAK_DOOR);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c§lBack");
            item.setItemMeta(meta);
        }

        return item;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        ClickType clickType = event.getClick();

        // Material selection
        if (slot == 1) selectMaterial(ArmorMaterial.LEATHER);
        else if (slot == 2) selectMaterial(ArmorMaterial.CHAINMAIL);
        else if (slot == 3) selectMaterial(ArmorMaterial.IRON);
        else if (slot == 4) selectMaterial(ArmorMaterial.GOLDEN);
        else if (slot == 5) selectMaterial(ArmorMaterial.DIAMOND);
        else if (slot == 6) selectMaterial(ArmorMaterial.NETHERITE);

        // Add/Remove pieces
        else if (slot == 11) handlePieceClick("HELMET", clickType);
        else if (slot == 20) handlePieceClick("CHESTPLATE", clickType);
        else if (slot == 29) handlePieceClick("LEGGINGS", clickType);
        else if (slot == 38) handlePieceClick("BOOTS", clickType);

        // Preview selection for enchanting
        else if (slot == 13) admin.sendMessage("§aHelmet selected for enchanting");
        else if (slot == 22) admin.sendMessage("§aChestplate selected for enchanting");
        else if (slot == 31) admin.sendMessage("§aLeggings selected for enchanting");
        else if (slot == 40) admin.sendMessage("§aBoots selected for enchanting");

        // Enchantments (simplified for now)
        else if (slot == 15) admin.sendMessage("§7Enchantment system coming soon");
        else if (slot == 16) admin.sendMessage("§7Enchantment system coming soon");
        else if (slot == 24) admin.sendMessage("§7Enchantment system coming soon");
        else if (slot == 25) admin.sendMessage("§7Enchantment system coming soon");
        else if (slot == 33) admin.sendMessage("§7Enchantment system coming soon");
        else if (slot == 34) admin.sendMessage("§7Enchantment system coming soon");

        // Control buttons
        else if (slot == 45) {} // Status button (informational only)
        else if (slot == 46) handleCopyMyArmor();
        else if (slot == 47) {} // Help button (informational only)
        else if (slot == 48) handleClear();
        else if (slot == 49) handleFullSet();
        else if (slot == 50) handleGive();
        else if (slot == 52) {} // Stats button (informational only)
        else if (slot == 53) handleExit();

        else event.setCancelled(true);
    }

    private void selectMaterial(ArmorMaterial material) {
        currentMaterial = material;

        // Update material selectors
        refreshSlot(1, createMaterialSelector(ArmorMaterial.LEATHER));
        refreshSlot(2, createMaterialSelector(ArmorMaterial.CHAINMAIL));
        refreshSlot(3, createMaterialSelector(ArmorMaterial.IRON));
        refreshSlot(4, createMaterialSelector(ArmorMaterial.GOLDEN));
        refreshSlot(5, createMaterialSelector(ArmorMaterial.DIAMOND));
        refreshSlot(6, createMaterialSelector(ArmorMaterial.NETHERITE));

        updatePreview();
        refreshSlot(45, createCompositionStatusButton());
        refreshSlot(49, createFullSetButton());
        refreshSlot(52, createArmorStatsButton());

        admin.sendMessage("§aMaterial changed to " + material.getDisplayName());
    }

    private void handlePieceClick(String pieceType, ClickType clickType) {
        if (clickType == ClickType.LEFT) {
            // Add piece
            switch (pieceType) {
                case "HELMET":
                    helmet = new ItemStack(currentMaterial.getHelmet());
                    admin.sendMessage("§a+ Added helmet to composition");
                    break;
                case "CHESTPLATE":
                    chestplate = new ItemStack(currentMaterial.getChestplate());
                    admin.sendMessage("§a+ Added chestplate to composition");
                    break;
                case "LEGGINGS":
                    leggings = new ItemStack(currentMaterial.getLeggings());
                    admin.sendMessage("§a+ Added leggings to composition");
                    break;
                case "BOOTS":
                    boots = new ItemStack(currentMaterial.getBoots());
                    admin.sendMessage("§a+ Added boots to composition");
                    break;
            }
        } else if (clickType == ClickType.RIGHT) {
            // Remove piece
            switch (pieceType) {
                case "HELMET":
                    helmet = null;
                    admin.sendMessage("§c- Removed helmet from composition");
                    break;
                case "CHESTPLATE":
                    chestplate = null;
                    admin.sendMessage("§c- Removed chestplate from composition");
                    break;
                case "LEGGINGS":
                    leggings = null;
                    admin.sendMessage("§c- Removed leggings from composition");
                    break;
                case "BOOTS":
                    boots = null;
                    admin.sendMessage("§c- Removed boots from composition");
                    break;
            }
        }

        updatePreview();
        refreshSlot(45, createCompositionStatusButton());
        refreshSlot(52, createArmorStatsButton());
    }

    private void handleClear() {
        helmet = null;
        chestplate = null;
        leggings = null;
        boots = null;
        helmetEnchants.clear();
        chestplateEnchants.clear();
        leggingsEnchants.clear();
        bootsEnchants.clear();

        updatePreview();
        refreshSlot(45, createCompositionStatusButton());
        refreshSlot(52, createArmorStatsButton());

        admin.sendMessage("§cComposition cleared!");
    }

    private void handleFullSet() {
        helmet = new ItemStack(currentMaterial.getHelmet());
        chestplate = new ItemStack(currentMaterial.getChestplate());
        leggings = new ItemStack(currentMaterial.getLeggings());
        boots = new ItemStack(currentMaterial.getBoots());

        updatePreview();
        refreshSlot(45, createCompositionStatusButton());
        refreshSlot(52, createArmorStatsButton());

        admin.sendMessage("§aFull set added to composition!");
    }

    private void handleCopyMyArmor() {
        ItemStack[] armorContents = admin.getInventory().getArmorContents();

        if (armorContents[3] != null && armorContents[3].getType() != Material.AIR) {
            helmet = armorContents[3].clone();
        }
        if (armorContents[2] != null && armorContents[2].getType() != Material.AIR) {
            chestplate = armorContents[2].clone();
        }
        if (armorContents[1] != null && armorContents[1].getType() != Material.AIR) {
            leggings = armorContents[1].clone();
        }
        if (armorContents[0] != null && armorContents[0].getType() != Material.AIR) {
            boots = armorContents[0].clone();
        }

        int copied = 0;
        if (helmet != null) copied++;
        if (chestplate != null) copied++;
        if (leggings != null) copied++;
        if (boots != null) copied++;

        updatePreview();
        refreshSlot(45, createCompositionStatusButton());
        refreshSlot(52, createArmorStatsButton());

        if (copied > 0) {
            admin.sendMessage("§aCopied §e" + copied + " §aarmor piece(s) from your equipment!");
        } else {
            admin.sendMessage("§cYou're not wearing any armor!");
        }
    }

    private void handleGive() {
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
            admin.sendMessage("§aGiven §e" + given + " §aarmor piece(s) to §e" + targetPlayer.getName());
            targetPlayer.sendMessage("§aYou received §e" + given + " §aarmor piece(s) from §e" + admin.getName());
        } else {
            admin.sendMessage("§cNo armor in composition to give!");
        }
    }

    private void handleExit() {
        admin.closeInventory();
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerManage(admin, targetPlayer).open()
        );
    }
}
