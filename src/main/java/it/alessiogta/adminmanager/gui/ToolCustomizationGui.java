package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.listeners.ToolCreatorChatListener;
import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI for customizing a tool with enchantments, name, lore, etc.
 */
public class ToolCustomizationGui extends BaseGui {

    private final Player admin;
    private final Player targetPlayer;
    private final ToolType toolType;
    private final ToolMaterial toolMaterial;

    // Tool being customized
    private ItemStack tool;

    // Customization data
    private String customName = null;
    private List<String> customLore = new ArrayList<>();
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private boolean unbreakable = false;
    private int customDurability = -1; // -1 means use default

    public ToolCustomizationGui(Player admin, Player targetPlayer, ToolType toolType, ToolMaterial toolMaterial) {
        super(admin, TranslationManager.translate("ToolCreator", "customization_title",
            "&6&lPersonalizza - {material} {tool}")
            .replace("{material}", toolMaterial.getDisplayName())
            .replace("{tool}", toolType.getDisplayName()), 6);
        this.admin = admin;
        this.targetPlayer = targetPlayer;
        this.toolType = toolType;
        this.toolMaterial = toolMaterial;

        // Create base tool
        this.tool = new ItemStack(toolType.getMaterial(toolMaterial));

        setupGuiItems();
    }

    @Override
    protected void setupNavigationButtons() {
        // Disable BaseGui's automatic navigation buttons
    }

    private void setupGuiItems() {
        // Top row: Headers with translations
        setItem(1, createSectionHeader(
            TranslationManager.translate("ToolCreator", "properties_header", "&6&lPROPRIETÀ"),
            Material.NAME_TAG));
        setItem(4, createSectionHeader(
            TranslationManager.translate("ToolCreator", "preview_header", "&9&lANTEPRIMA"),
            Material.SPYGLASS));
        setItem(7, createSectionHeader(
            TranslationManager.translate("ToolCreator", "enchants_header", "&5&lINCATESIMI"),
            Material.ENCHANTING_TABLE));

        // Left column: Customization options
        setItem(10, createNameButton());
        setItem(19, createLoreButton());
        setItem(28, createDurabilityButton());
        setItem(37, createUnbreakableButton());

        // Center column: Preview and stats (MAIN FOCUS)
        setItem(12, createBorderGlass(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "&9§l║"));
        setItem(13, createPreviewSlot());
        setItem(14, createBorderGlass(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "&9§l║"));

        setItem(21, createBorderGlass(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "&9§l║"));
        setItem(22, createToolStatsButton());
        setItem(23, createBorderGlass(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "&9§l║"));

        setItem(30, createBorderGlass(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "&9§l║"));
        setItem(31, createToolInfoButton());
        setItem(32, createBorderGlass(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "&9§l║"));

        // Right side: Enchantments (using ENCHANTED_BOOK for all)
        setupEnchantmentButtons();

        // Bottom row: Control buttons
        setItem(45, createBackButton());
        setItem(46, createResetButton());
        setItem(48, createCopyHeldItemButton());
        setItem(49, createGiveButton());
        setItem(53, createQuickGiveButton());
    }

    private ItemStack createSectionHeader(String name, Material icon) {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createBorderGlass(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createToolInfoButton() {
        String title = "&6&lInfo Strumento";
        List<String> loreLines = new ArrayList<>();
        loreLines.add("&7§m━━━━━━━━━━━━━━━━");
        loreLines.add("&7Tipo: &e" + toolType.getDisplayName());
        loreLines.add("&7Materiale: " + toolMaterial.getDisplayName());
        loreLines.add("&7Giocatore: &e" + targetPlayer.getName());
        loreLines.add("&7§m━━━━━━━━━━━━━━━━");

        String lore = String.join("\n", loreLines);
        return createItem(Material.BOOK, title, lore.split("\n"));
    }

    private void setupEnchantmentButtons() {
        // Right column slots: 16, 17, 25, 26, 34, 35, 43, 44, 52
        // All enchantments use ENCHANTED_BOOK with glow effect
        if (toolType == ToolType.SWORD) {
            setItem(16, createEnchantButton("SHARPNESS", 5));
            setItem(17, createEnchantButton("SMITE", 5));
            setItem(25, createEnchantButton("BANE_OF_ARTHROPODS", 5));
            setItem(26, createEnchantButton("KNOCKBACK", 2));
            setItem(34, createEnchantButton("FIRE_ASPECT", 2));
            setItem(35, createEnchantButton("LOOTING", 3));
            setItem(43, createEnchantButton("SWEEPING", 3));
            setItem(44, createEnchantButton("UNBREAKING", 3));
            setItem(52, createEnchantButton("MENDING", 1));
        } else if (toolType == ToolType.PICKAXE) {
            setItem(16, createEnchantButton("EFFICIENCY", 5));
            setItem(17, createEnchantButton("FORTUNE", 3));
            setItem(25, createEnchantButton("SILK_TOUCH", 1));
            setItem(26, createEnchantButton("UNBREAKING", 3));
            setItem(34, createEnchantButton("MENDING", 1));
        } else if (toolType == ToolType.AXE) {
            setItem(16, createEnchantButton("SHARPNESS", 5));
            setItem(17, createEnchantButton("EFFICIENCY", 5));
            setItem(25, createEnchantButton("FORTUNE", 3));
            setItem(26, createEnchantButton("SILK_TOUCH", 1));
            setItem(34, createEnchantButton("UNBREAKING", 3));
            setItem(35, createEnchantButton("MENDING", 1));
        } else if (toolType == ToolType.SHOVEL) {
            setItem(16, createEnchantButton("EFFICIENCY", 5));
            setItem(17, createEnchantButton("FORTUNE", 3));
            setItem(25, createEnchantButton("SILK_TOUCH", 1));
            setItem(26, createEnchantButton("UNBREAKING", 3));
            setItem(34, createEnchantButton("MENDING", 1));
        } else if (toolType == ToolType.HOE) {
            setItem(16, createEnchantButton("EFFICIENCY", 5));
            setItem(17, createEnchantButton("FORTUNE", 3));
            setItem(25, createEnchantButton("SILK_TOUCH", 1));
            setItem(26, createEnchantButton("UNBREAKING", 3));
            setItem(34, createEnchantButton("MENDING", 1));
        }
    }

    private ItemStack createNameButton() {
        String title = TranslationManager.translate("ToolCreator", "name_button_title", "&e&lNome Custom");
        String currentName = customName != null ? customName : "&7Nessuno";
        String lore = TranslationManager.translate("ToolCreator", "name_button_lore",
            "&7Attuale: " + currentName + "\n\n&e&lCLICK: &7Imposta nome (via chat)")
            .replace("{name}", currentName);
        return createItem(Material.NAME_TAG, title, lore.split("\n"));
    }

    private ItemStack createLoreButton() {
        String title = TranslationManager.translate("ToolCreator", "lore_button_title", "&e&lLore Custom");
        int lines = customLore.size();
        String lore = TranslationManager.translate("ToolCreator", "lore_button_lore",
            "&7Righe: &e{lines}\n\n&e&lLEFT: &7Aggiungi riga\n&c&lRIGHT: &7Rimuovi ultima riga")
            .replace("{lines}", String.valueOf(lines));
        return createItem(Material.WRITABLE_BOOK, title, lore.split("\n"));
    }

    private ItemStack createDurabilityButton() {
        int maxDurability = toolMaterial.getBaseDurability(toolType);
        int current = customDurability == -1 ? maxDurability : customDurability;

        String title = TranslationManager.translate("ToolCreator", "durability_button_title", "&e&lDurabilità");
        String lore = TranslationManager.translate("ToolCreator", "durability_button_lore",
            "&7Attuale: &e{current}&7/&e{max}\n\n&e&lLEFT: &7+10 durabilità\n&c&lRIGHT: &7-10 durabilità\n&6&lSHIFT+LEFT: &7Max durabilità")
            .replace("{current}", String.valueOf(current))
            .replace("{max}", String.valueOf(maxDurability));

        return createItem(Material.ANVIL, title, lore.split("\n"));
    }

    private ItemStack createUnbreakableButton() {
        String status = unbreakable ? "&a&lATTIVO" : "&c&lDISATTIVO";
        String title = TranslationManager.translate("ToolCreator", "unbreakable_button_title", "&e&lIndistruttibile");
        String lore = TranslationManager.translate("ToolCreator", "unbreakable_button_lore",
            "&7Stato: " + status + "\n\n&e&lCLICK: &7Toggle")
            .replace("{status}", status);

        Material icon = unbreakable ? Material.BEDROCK : Material.IRON_BLOCK;
        return createItem(icon, title, lore.split("\n"));
    }

    private ItemStack createEnchantButton(String enchantType, int maxLevel) {
        String displayName = getEnchantDisplayName(enchantType);
        int currentLevel = enchantments.getOrDefault(getEnchantmentByType(enchantType), 0);

        // Use displayName directly for title (no translation needed)
        String title = "&9" + displayName;

        String lore = TranslationManager.translate("ToolCreator", "enchant_button_lore",
            "&7Max: &f{max}\n&7Attuale: &e{current}\n\n&e&lLEFT: &7Liv 1  &b&lRIGHT: &7Liv 2\n&6&lSHIFT+LEFT: &7Liv 3  &c&lSHIFT+RIGHT: &7Liv {max}")
            .replace("{max}", String.valueOf(maxLevel))
            .replace("{current}", currentLevel > 0 ? "Livello " + currentLevel : "Nessuno");

        // Always use ENCHANTED_BOOK for all enchantments
        ItemStack item = createItem(Material.ENCHANTED_BOOK, title, lore.split("\n"));

        // Add glow effect when enchantment is active
        if (currentLevel > 0) {
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }

        return item;
    }

    private String getEnchantDisplayName(String type) {
        switch (type) {
            case "SHARPNESS": return "Affilatura";
            case "SMITE": return "Rovina";
            case "BANE_OF_ARTHROPODS": return "Flagello degli Artropodi";
            case "KNOCKBACK": return "Contraccolpo";
            case "FIRE_ASPECT": return "Aspetto Igneo";
            case "LOOTING": return "Saccheggio";
            case "SWEEPING": return "Spazzata";
            case "EFFICIENCY": return "Efficienza";
            case "FORTUNE": return "Fortuna";
            case "SILK_TOUCH": return "Tocco di Velluto";
            case "UNBREAKING": return "Indistruttibilità";
            case "MENDING": return "Rammendo";
            default: return type;
        }
    }

    private Enchantment getEnchantmentByType(String type) {
        switch (type) {
            case "SHARPNESS": return Enchantment.DAMAGE_ALL;
            case "SMITE": return Enchantment.DAMAGE_UNDEAD;
            case "BANE_OF_ARTHROPODS": return Enchantment.DAMAGE_ARTHROPODS;
            case "KNOCKBACK": return Enchantment.KNOCKBACK;
            case "FIRE_ASPECT": return Enchantment.FIRE_ASPECT;
            case "LOOTING": return Enchantment.LOOT_BONUS_MOBS;
            case "SWEEPING": return Enchantment.SWEEPING_EDGE;
            case "EFFICIENCY": return Enchantment.DIG_SPEED;
            case "FORTUNE": return Enchantment.LOOT_BONUS_BLOCKS;
            case "SILK_TOUCH": return Enchantment.SILK_TOUCH;
            case "UNBREAKING": return Enchantment.DURABILITY;
            case "MENDING": return Enchantment.MENDING;
            default: return null;
        }
    }

    private ItemStack createPreviewSlot() {
        // Apply all customizations to preview
        ItemStack preview = tool.clone();
        ItemMeta meta = preview.getItemMeta();

        if (meta != null) {
            // Apply name
            if (customName != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', customName));
            }

            // Apply lore
            if (!customLore.isEmpty()) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : customLore) {
                    coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(coloredLore);
            }

            // Apply unbreakable
            meta.setUnbreakable(unbreakable);

            // Apply durability
            if (meta instanceof Damageable && customDurability != -1) {
                int maxDurability = toolMaterial.getBaseDurability(toolType);
                int damage = maxDurability - customDurability;
                ((Damageable) meta).setDamage(Math.max(0, damage));
            }

            preview.setItemMeta(meta);
        }

        // Apply enchantments
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            preview.addUnsafeEnchantment(entry.getKey(), entry.getValue());
        }

        return preview;
    }

    private ItemStack createToolStatsButton() {
        String title = TranslationManager.translate("ToolCreator", "stats_button_title", "&6&lStatistiche Strumento");

        List<String> loreLines = new ArrayList<>();
        loreLines.add("&7§m━━━━━━━━━━━━━━━━");
        loreLines.add("&7Tipo: &e" + toolType.getDisplayName());
        loreLines.add("&7Materiale: " + toolMaterial.getDisplayName());
        loreLines.add("&7Durabilità: &e" + toolMaterial.getBaseDurability(toolType));

        if (toolType == ToolType.SWORD) {
            loreLines.add("&7Danno: &c+" + toolMaterial.getAttackDamage());
        } else {
            loreLines.add("&7Velocità Mining: &b" + toolMaterial.getMiningSpeed());
        }

        loreLines.add("&7Incantesimi: &d" + enchantments.size());
        loreLines.add("&7§m━━━━━━━━━━━━━━━━");

        String lore = String.join("\n", loreLines);
        return createItem(Material.BOOK, title, lore.split("\n"));
    }

    private ItemStack createBackButton() {
        String title = TranslationManager.translate("ToolCreator", "back_button_title", "&cIndietro");
        String lore = TranslationManager.translate("ToolCreator", "back_to_material_lore", "&7Torna alla selezione materiale");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    private ItemStack createResetButton() {
        String title = TranslationManager.translate("ToolCreator", "reset_button_title", "&c&lReset");
        String lore = TranslationManager.translate("ToolCreator", "reset_button_lore", "&7Rimuovi tutte le personalizzazioni");
        return createItem(Material.BARRIER, title, lore);
    }

    private ItemStack createCopyHeldItemButton() {
        String title = TranslationManager.translate("ToolCreator", "copy_held_title", "&b&lCopia Strumento in Mano");
        String lore = TranslationManager.translate("ToolCreator", "copy_held_lore", "&7Copia lo strumento che tieni\n&7in mano nella composizione");
        return createItem(Material.ARMOR_STAND, title, lore.split("\n"));
    }

    private ItemStack createGiveButton() {
        String title = TranslationManager.translate("ToolCreator", "give_button_title", "&a&lDai Strumento");
        String lore = TranslationManager.translate("ToolCreator", "give_button_lore",
            "&7Dai lo strumento a:\n&e{player}")
            .replace("{player}", targetPlayer.getName());
        return createItem(Material.LIME_DYE, title, lore.split("\n"));
    }

    private ItemStack createQuickGiveButton() {
        String title = TranslationManager.translate("ToolCreator", "quick_give_title", "&2&lDai Set Base");
        String lore = TranslationManager.translate("ToolCreator", "quick_give_lore",
            "&7Dai un set base senza\n&7personalizzazioni a:\n&e{player}")
            .replace("{player}", targetPlayer.getName());
        return createItem(Material.GREEN_DYE, title, lore.split("\n"));
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();
        ClickType clickType = event.getClick();

        // Customization buttons
        if (slot == 10) handleNameClick(clicker);
        else if (slot == 19) handleLoreClick(clicker, clickType);
        else if (slot == 28) handleDurabilityClick(clickType);
        else if (slot == 37) handleUnbreakableClick();

        // Enchantment buttons - right column
        else if (slot == 16 || slot == 17 || slot == 25 || slot == 26 ||
                 slot == 34 || slot == 35 || slot == 43 || slot == 44 || slot == 52) {
            handleEnchantmentSlotClick(slot, clickType);
        }

        // Control buttons
        else if (slot == 45) handleBack(clicker);
        else if (slot == 46) handleReset();
        else if (slot == 48) handleCopyHeld(clicker);
        else if (slot == 49) handleGive();
        else if (slot == 53) handleQuickGive();
    }

    private void handleNameClick(Player clicker) {
        clicker.sendMessage(ChatColor.translateAlternateColorCodes('&',
            TranslationManager.translate("ToolCreator", "name_prompt",
            "&6Scrivi il nome per lo strumento in chat\n&7Scrivi &ccancel &7per annullare")));
        clicker.closeInventory();

        // Register chat listener
        ToolCreatorChatListener.registerInput(clicker, this, ToolCreatorChatListener.InputType.NAME);
    }

    private void handleLoreClick(Player clicker, ClickType clickType) {
        if (clickType == ClickType.LEFT) {
            // Add line
            clicker.sendMessage(ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ToolCreator", "lore_prompt",
                "&6Scrivi una riga di lore in chat\n&7Scrivi &ccancel &7per annullare")));
            clicker.closeInventory();

            // Register chat listener
            ToolCreatorChatListener.registerInput(clicker, this, ToolCreatorChatListener.InputType.LORE);
        } else if (clickType == ClickType.RIGHT) {
            // Remove last line
            if (!customLore.isEmpty()) {
                customLore.remove(customLore.size() - 1);
                refreshCustomizationButtons();
                admin.sendMessage("§cRimossa ultima riga di lore");
            }
        }
    }

    private void handleDurabilityClick(ClickType clickType) {
        int maxDurability = toolMaterial.getBaseDurability(toolType);
        int current = customDurability == -1 ? maxDurability : customDurability;

        if (clickType == ClickType.SHIFT_LEFT) {
            customDurability = maxDurability;
            admin.sendMessage("§aDurabilità impostata al massimo: " + maxDurability);
        } else if (clickType == ClickType.LEFT) {
            customDurability = Math.min(current + 10, maxDurability);
            admin.sendMessage("§aDurabilità aumentata a: " + customDurability);
        } else if (clickType == ClickType.RIGHT) {
            customDurability = Math.max(current - 10, 1);
            admin.sendMessage("§cDurabilità diminuita a: " + customDurability);
        }

        refreshCustomizationButtons();
    }

    private void handleUnbreakableClick() {
        unbreakable = !unbreakable;
        admin.sendMessage(unbreakable ? "§aIndistruttibile: ATTIVO" : "§cIndistruttibile: DISATTIVO");
        refreshCustomizationButtons();
    }

    private void handleEnchantmentSlotClick(int slot, ClickType clickType) {
        String enchantType = getEnchantTypeBySlot(slot);
        if (enchantType == null) return;

        int maxLevel = getMaxLevelForEnchant(enchantType);
        int level = 0;

        switch (clickType) {
            case LEFT: level = 1; break;
            case RIGHT: level = 2; break;
            case SHIFT_LEFT: level = 3; break;
            case SHIFT_RIGHT: level = maxLevel; break;
            default: return;
        }

        if (level > maxLevel) {
            admin.sendMessage("§cLivello massimo per questo incantesimo: " + maxLevel);
            return;
        }

        Enchantment enchant = getEnchantmentByType(enchantType);
        if (enchant != null) {
            enchantments.put(enchant, level);
            admin.sendMessage("§aAggiunto " + getEnchantDisplayName(enchantType) + " " + level);
            refreshEnchantmentButtons();
        }
    }

    private String getEnchantTypeBySlot(int slot) {
        // Right column slots: 16, 17, 25, 26, 34, 35, 43, 44, 52
        if (toolType == ToolType.SWORD) {
            switch (slot) {
                case 16: return "SHARPNESS";
                case 17: return "SMITE";
                case 25: return "BANE_OF_ARTHROPODS";
                case 26: return "KNOCKBACK";
                case 34: return "FIRE_ASPECT";
                case 35: return "LOOTING";
                case 43: return "SWEEPING";
                case 44: return "UNBREAKING";
                case 52: return "MENDING";
            }
        } else if (toolType == ToolType.AXE) {
            switch (slot) {
                case 16: return "SHARPNESS";
                case 17: return "EFFICIENCY";
                case 25: return "FORTUNE";
                case 26: return "SILK_TOUCH";
                case 34: return "UNBREAKING";
                case 35: return "MENDING";
            }
        } else {
            // For pickaxe, shovel, hoe
            switch (slot) {
                case 16: return "EFFICIENCY";
                case 17: return "FORTUNE";
                case 25: return "SILK_TOUCH";
                case 26: return "UNBREAKING";
                case 34: return "MENDING";
            }
        }
        return null;
    }

    private int getMaxLevelForEnchant(String enchantType) {
        switch (enchantType) {
            case "SHARPNESS":
            case "SMITE":
            case "BANE_OF_ARTHROPODS":
            case "EFFICIENCY":
                return 5;
            case "FORTUNE":
            case "LOOTING":
            case "SWEEPING":
            case "UNBREAKING":
                return 3;
            case "KNOCKBACK":
            case "FIRE_ASPECT":
                return 2;
            case "SILK_TOUCH":
            case "MENDING":
                return 1;
            default:
                return 1;
        }
    }

    private void handleBack(Player clicker) {
        org.bukkit.event.HandlerList.unregisterAll(this);
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new ToolMaterialSelectorGui(clicker, targetPlayer, toolType).open()
        );
    }

    private void handleReset() {
        customName = null;
        customLore.clear();
        enchantments.clear();
        unbreakable = false;
        customDurability = -1;

        refreshAll();
        admin.sendMessage("§cPersonalizzazioni azzerate!");
    }

    private void handleCopyHeld(Player clicker) {
        ItemStack held = clicker.getInventory().getItemInMainHand();
        if (held == null || held.getType() == Material.AIR) {
            admin.sendMessage("§cNon stai tenendo nessuno strumento!");
            return;
        }

        // Copy item data
        ItemMeta heldMeta = held.getItemMeta();
        if (heldMeta != null) {
            if (heldMeta.hasDisplayName()) {
                customName = heldMeta.getDisplayName();
            }
            if (heldMeta.hasLore()) {
                customLore = new ArrayList<>(heldMeta.getLore());
            }
            unbreakable = heldMeta.isUnbreakable();
        }

        enchantments.clear();
        enchantments.putAll(held.getEnchantments());

        refreshAll();
        admin.sendMessage("§aStrumento copiato con successo!");
    }

    private void handleGive() {
        ItemStack finalTool = createPreviewSlot();
        targetPlayer.getInventory().addItem(finalTool);

        admin.sendMessage("§aDato " + toolMaterial.getDisplayName() + " " +
            toolType.getDisplayName() + " §aa §e" + targetPlayer.getName());
        targetPlayer.sendMessage("§aHai ricevuto un " + toolMaterial.getDisplayName() +
            " " + toolType.getDisplayName() + " §ada §e" + admin.getName());
    }

    private void handleQuickGive() {
        ItemStack baseTool = new ItemStack(toolType.getMaterial(toolMaterial));
        targetPlayer.getInventory().addItem(baseTool);

        admin.sendMessage("§aDato " + toolMaterial.getDisplayName() + " " +
            toolType.getDisplayName() + " §abase a §e" + targetPlayer.getName());
    }

    private void refreshCustomizationButtons() {
        refreshSlot(10, createNameButton());
        refreshSlot(19, createLoreButton());
        refreshSlot(28, createDurabilityButton());
        refreshSlot(37, createUnbreakableButton());
        refreshSlot(13, createPreviewSlot());
        refreshSlot(22, createToolStatsButton());
    }

    private void refreshEnchantmentButtons() {
        setupEnchantmentButtons();
        refreshSlot(13, createPreviewSlot());
        refreshSlot(22, createToolStatsButton());
    }

    private void refreshAll() {
        refreshCustomizationButtons();
        refreshEnchantmentButtons();
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

    // ========== PUBLIC METHODS FOR CHAT LISTENER ==========

    /**
     * Set custom name for the tool (called by chat listener)
     */
    public void setCustomName(String name) {
        this.customName = name;
    }

    /**
     * Add a line to custom lore (called by chat listener)
     */
    public void addLoreLine(String line) {
        this.customLore.add(line);
    }

    /**
     * Reopen the GUI after chat input (called by chat listener)
     */
    public void reopen() {
        // Unregister old listener
        org.bukkit.event.HandlerList.unregisterAll(this);

        // Rebuild and reopen
        setupGuiItems();
        inventory = build();
        admin.openInventory(inventory);
    }
}
