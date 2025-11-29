package it.alessiogta.adminmanager.gui;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class PlayerStatsCategoryGui extends BaseGui {

    private final Player admin;
    private final OfflinePlayer targetPlayer;
    private final String category;
    private final int returnPage;
    private final List<StatEntry> categoryStats;

    public PlayerStatsCategoryGui(Player admin, OfflinePlayer targetPlayer, String category, int returnPage, int currentPage) {
        super(admin, formatTitle(targetPlayer, category, currentPage), currentPage);
        this.admin = admin;
        this.targetPlayer = targetPlayer;
        this.category = category;
        this.returnPage = returnPage;
        this.categoryStats = loadCategoryStats();
        setupStatsItems();

        // Rebuild navigation buttons now that categoryStats is loaded
        setupNavigationButtons();
    }

    // Convenience constructor for first page
    public PlayerStatsCategoryGui(Player admin, OfflinePlayer targetPlayer, String category, int returnPage) {
        this(admin, targetPlayer, category, returnPage, 1);
    }

    private static String formatTitle(OfflinePlayer targetPlayer, String category, int page) {
        String playerName = targetPlayer.getName() != null ? targetPlayer.getName() : "Unknown";
        String categoryName = formatCategoryName(category.replace("minecraft:", ""));
        String baseTitle = TranslationManager.translate("PlayerStatsCategory", "title", "&a&l{category}: {player}")
            .replace("{category}", categoryName)
            .replace("{player}", playerName);
        // Add page number to make each page unique for listener identification
        return ChatColor.translateAlternateColorCodes('&', baseTitle) + " §8[" + page + "]";
    }

    @Override
    protected void setupNavigationButtons() {
        int itemsPerPage = 45;
        int totalStats = (categoryStats != null) ? categoryStats.size() : 0;

        // Back button (slot 45)
        String backTitle = TranslationManager.translate("PlayerStatsCategory", "back_button_title", "&cIndietro");
        String backLore = TranslationManager.translate("PlayerStatsCategory", "back_button_lore", "&7Torna alle categorie");
        setItem(45, createItem(Material.DARK_OAK_DOOR, backTitle, backLore));

        // Previous page button (slot 47) - only if there's a previous page
        if (getPage() > 1) {
            String prevTitle = TranslationManager.translate("PlayerStatsCategory", "previous_page", "&ePagina precedente");
            setItem(47, createItem(Material.ARROW, prevTitle));
        }

        // Info button (slot 49) - shows total count
        String infoTitle = TranslationManager.translate("PlayerStatsCategory", "info_title", "&6Info");
        String infoLore = TranslationManager.translate("PlayerStatsCategory", "info_lore",
            "&7Totale statistiche: &e{count}\n&7Pagina: &e{page}&7/&e{maxPage}")
            .replace("{count}", String.valueOf(totalStats))
            .replace("{page}", String.valueOf(getPage()))
            .replace("{maxPage}", String.valueOf(Math.max(1, (int) Math.ceil(totalStats / (double) itemsPerPage))));
        setItem(49, createItem(Material.BOOK, infoTitle, infoLore.split("\n")));

        // Next page button (slot 51) - only if there are more stats
        if (totalStats > getPage() * itemsPerPage) {
            String nextTitle = TranslationManager.translate("PlayerStatsCategory", "next_page", "&ePagina successiva");
            setItem(51, createItem(Material.ARROW, nextTitle));
        }
    }

    private List<StatEntry> loadCategoryStats() {
        List<StatEntry> stats = new ArrayList<>();

        if (Bukkit.getWorlds().isEmpty()) {
            return stats;
        }

        World mainWorld = Bukkit.getWorlds().get(0);
        File statsFile = new File(mainWorld.getWorldFolder(), "stats/" + targetPlayer.getUniqueId() + ".json");

        if (!statsFile.exists()) {
            return stats;
        }

        try (FileReader reader = new FileReader(statsFile)) {
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            if (root == null || !root.has("stats")) {
                return stats;
            }

            JsonObject statsObj = root.getAsJsonObject("stats");

            // Load only stats from this category
            if (statsObj.has(category)) {
                JsonObject categoryStats = statsObj.getAsJsonObject(category);

                for (Map.Entry<String, JsonElement> entry : categoryStats.entrySet()) {
                    String statKey = entry.getKey();
                    int value = entry.getValue().getAsInt();

                    // Format stat name and value
                    String displayName = formatStatName(statKey);
                    String displayValue = formatStatValue(statKey, value);
                    Material icon = getIconForStat(statKey);

                    stats.add(new StatEntry(icon, displayName, displayValue));
                }
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("[AdminManager] Errore lettura statistiche per " +
                targetPlayer.getName() + ": " + e.getMessage());
        }

        // Sort alphabetically by display name
        stats.sort(Comparator.comparing(s -> s.name));

        return stats;
    }

    private String formatStatName(String statKey) {
        // Remove "minecraft:" prefix
        String cleanKey = statKey.replace("minecraft:", "");

        // Convert snake_case to Title Case
        String[] words = cleanKey.split("_");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (result.length() > 0) result.append(" ");
            result.append(word.substring(0, 1).toUpperCase())
                  .append(word.substring(1).toLowerCase());
        }

        return result.toString();
    }

    private String formatStatValue(String statKey, int value) {
        // Special formatting for specific stats
        if (statKey.contains("_one_cm")) {
            // Distance in cm -> meters
            return formatNumber(value / 100) + " m";
        } else if (statKey.equals("minecraft:play_time") || statKey.equals("minecraft:total_world_time")) {
            // Time in ticks -> hours and minutes
            int hours = value / 20 / 3600;
            int minutes = (value / 20 / 60) % 60;
            return hours + "h " + minutes + "m";
        } else if (statKey.equals("minecraft:damage_dealt") || statKey.equals("minecraft:damage_taken")) {
            // Damage (divide by 10 for half-hearts)
            return formatNumber(value / 10);
        } else {
            // Default: just format the number
            return formatNumber(value);
        }
    }

    private Material getIconForStat(String statKey) {
        String key = statKey.toLowerCase();

        // Custom stats
        if (category.equals("minecraft:custom")) {
            if (key.contains("play_time")) return Material.CLOCK;
            if (key.contains("death")) return Material.SKELETON_SKULL;
            if (key.contains("jump")) return Material.RABBIT_FOOT;
            if (key.contains("walk") || key.contains("sprint") || key.contains("crouch")) return Material.LEATHER_BOOTS;
            if (key.contains("damage_dealt")) return Material.DIAMOND_SWORD;
            if (key.contains("damage_taken")) return Material.SHIELD;
            if (key.contains("fly")) return Material.ELYTRA;
            if (key.contains("swim")) return Material.FISHING_ROD;
            return Material.PAPER;
        }

        // For other categories, try to get the material from the stat name
        return getMaterialFromName(statKey, getCategoryFallbackIcon());
    }

    private Material getCategoryFallbackIcon() {
        switch (category) {
            case "minecraft:mined": return Material.DIAMOND_PICKAXE;
            case "minecraft:killed": return Material.IRON_SWORD;
            case "minecraft:killed_by": return Material.SKELETON_SKULL;
            case "minecraft:crafted": return Material.CRAFTING_TABLE;
            case "minecraft:used": return Material.STICK;
            case "minecraft:broken": return Material.WOODEN_PICKAXE;
            case "minecraft:picked_up": return Material.CHEST;
            case "minecraft:dropped": return Material.DROPPER;
            default: return Material.PAPER;
        }
    }

    private Material getMaterialFromName(String statKey, Material fallback) {
        try {
            // Remove "minecraft:" prefix and convert to uppercase
            String materialName = statKey.replace("minecraft:", "").toUpperCase();
            Material material = Material.getMaterial(materialName);

            // Check if material exists AND is a valid item (not just a block)
            if (material != null && material.isItem()) {
                return material;
            }
            return fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    private static String formatCategoryName(String category) {
        switch (category) {
            case "custom": return "Generali";
            case "mined": return "Blocchi Scavati";
            case "killed": return "Mob Uccisi";
            case "killed_by": return "Ucciso Da";
            case "crafted": return "Item Craftati";
            case "used": return "Item Usati";
            case "broken": return "Strumenti Rotti";
            case "picked_up": return "Item Raccolti";
            case "dropped": return "Item Droppati";
            default: return category;
        }
    }

    private String formatNumber(int number) {
        if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000.0);
        } else if (number >= 1000) {
            return String.format("%.1fK", number / 1000.0);
        }
        return String.valueOf(number);
    }

    private void setupStatsItems() {
        if (categoryStats.isEmpty()) {
            // No stats available for this category
            setItem(22, createNoStatsItem());
            return;
        }

        // Display stats with pagination (45 items per page)
        int startSlot = 0;
        int itemsPerPage = 45;
        int startIndex = (getPage() - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, categoryStats.size());

        for (int i = startIndex; i < endIndex; i++) {
            StatEntry stat = categoryStats.get(i);
            setItem(startSlot++, createStatItem(stat));
        }
    }

    private ItemStack createNoStatsItem() {
        String title = TranslationManager.translate("PlayerStatsCategory", "no_stats_title", "&cNessuna statistica");
        String lore = TranslationManager.translate("PlayerStatsCategory", "no_stats_lore",
            "&7Nessuna statistica in questa categoria.");
        return createItem(Material.BARRIER, title, lore.split("\n"));
    }

    private ItemStack createStatItem(StatEntry stat) {
        try {
            ItemStack item = new ItemStack(stat.material);
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e" + stat.name));

                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Valore: &f" + stat.value));
                meta.setLore(lore);

                item.setItemMeta(meta);
            }

            return item;
        } catch (Exception e) {
            // Fallback to PAPER if material is invalid
            return createItem(Material.PAPER, "&e" + stat.name, "&7Valore: &f" + stat.value);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        // Navigation buttons
        if (slot == 45) { // Back
            handleBack(clicker);
        } else if (slot == 47 && getPage() > 1) { // Previous page
            // Deregister current listener before opening new GUI to prevent multiple listeners
            org.bukkit.event.HandlerList.unregisterAll(this);
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> {
                    PlayerStatsCategoryGui newGui = new PlayerStatsCategoryGui(clicker, targetPlayer, category, returnPage, getPage() - 1);
                    clicker.openInventory(newGui.build());
                }
            );
        } else if (slot == 51) { // Next page
            int itemsPerPage = 45;
            if (categoryStats.size() > getPage() * itemsPerPage) {
                // Deregister current listener before opening new GUI to prevent multiple listeners
                org.bukkit.event.HandlerList.unregisterAll(this);
                Bukkit.getScheduler().runTask(
                    Bukkit.getPluginManager().getPlugin("AdminManager"),
                    () -> {
                        PlayerStatsCategoryGui newGui = new PlayerStatsCategoryGui(clicker, targetPlayer, category, returnPage, getPage() + 1);
                        clicker.openInventory(newGui.build());
                    }
                );
            }
        }
    }

    private void handleBack(Player clicker) {
        // Deregister current listener before returning to categories
        org.bukkit.event.HandlerList.unregisterAll(this);
        // Return to PlayerStatsGui (categories)
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerStatsGui(clicker, targetPlayer, returnPage).open()
        );
    }

    @Override
    public void open() {
        inventory = build();
        admin.openInventory(inventory);
    }

    // Inner class to hold stat entry info
    private static class StatEntry {
        final Material material;
        final String name;
        final String value;

        StatEntry(Material material, String name, String value) {
            this.material = material;
            this.name = name;
            this.value = value;
        }
    }
}
