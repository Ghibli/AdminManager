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

public class PlayerStatsGui extends BaseGui {

    private final Player admin;
    private final OfflinePlayer targetPlayer;
    private final int returnPage; // Page to return to in PlayerDataGui
    private final List<StatEntry> allStats;

    public PlayerStatsGui(Player admin, OfflinePlayer targetPlayer, int returnPage, int currentPage) {
        super(admin, formatTitle(targetPlayer), currentPage);
        this.admin = admin;
        this.targetPlayer = targetPlayer;
        this.returnPage = returnPage;
        this.allStats = loadAllPlayerStats();
        setupStatsItems();

        // Rebuild navigation buttons now that allStats is loaded
        setupNavigationButtons();
    }

    // Convenience constructor for first page
    public PlayerStatsGui(Player admin, OfflinePlayer targetPlayer, int returnPage) {
        this(admin, targetPlayer, returnPage, 1);
    }

    private static String formatTitle(OfflinePlayer targetPlayer) {
        String playerName = targetPlayer.getName() != null ? targetPlayer.getName() : "Unknown";
        String baseTitle = TranslationManager.translate("PlayerStats", "title", "&a&lStatistiche: {player}")
            .replace("{player}", playerName);
        return ChatColor.translateAlternateColorCodes('&', baseTitle);
    }

    @Override
    protected void setupNavigationButtons() {
        int itemsPerPage = 45;
        int totalStats = (allStats != null) ? allStats.size() : 0;

        // Back button (slot 45)
        String backTitle = TranslationManager.translate("PlayerStats", "back_button_title", "&cIndietro");
        String backLore = TranslationManager.translate("PlayerStats", "back_button_lore", "&7Torna ai dettagli giocatore");
        setItem(45, createItem(Material.DARK_OAK_DOOR, backTitle, backLore));

        // Previous page button (slot 47) - only if there's a previous page
        if (getPage() > 1) {
            String prevTitle = TranslationManager.translate("PlayerStats", "previous_page", "&ePagina precedente");
            setItem(47, createItem(Material.ARROW, prevTitle));
        }

        // Info button (slot 49) - shows total count
        String infoTitle = TranslationManager.translate("PlayerStats", "info_title", "&6Info");
        String infoLore = TranslationManager.translate("PlayerStats", "info_lore",
            "&7Totale statistiche: &e{count}\n&7Pagina: &e{page}&7/&e{maxPage}")
            .replace("{count}", String.valueOf(totalStats))
            .replace("{page}", String.valueOf(getPage()))
            .replace("{maxPage}", String.valueOf(Math.max(1, (int) Math.ceil(totalStats / (double) itemsPerPage))));
        setItem(49, createItem(Material.BOOK, infoTitle, infoLore.split("\n")));

        // Next page button (slot 51) - only if there are more stats
        if (totalStats > getPage() * itemsPerPage) {
            String nextTitle = TranslationManager.translate("PlayerStats", "next_page", "&ePagina successiva");
            setItem(51, createItem(Material.ARROW, nextTitle));
        }
    }

    private List<StatEntry> loadAllPlayerStats() {
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

            // Load all statistics from all categories
            for (String category : statsObj.keySet()) {
                JsonObject categoryStats = statsObj.getAsJsonObject(category);

                for (Map.Entry<String, JsonElement> entry : categoryStats.entrySet()) {
                    String statKey = entry.getKey();
                    int value = entry.getValue().getAsInt();

                    // Format stat name and value
                    String displayName = formatStatName(category, statKey);
                    String displayValue = formatStatValue(category, statKey, value);
                    Material icon = getIconForStat(category, statKey);

                    stats.add(new StatEntry(icon, displayName, displayValue, category));
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

    private String formatStatName(String category, String statKey) {
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

    private String formatStatValue(String category, String statKey, int value) {
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

    private Material getIconForStat(String category, String statKey) {
        // Try to match icon based on stat name
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
        }

        // Mined stats
        if (category.equals("minecraft:mined")) {
            return getMaterialFromName(statKey, Material.DIAMOND_PICKAXE);
        }

        // Killed stats
        if (category.equals("minecraft:killed")) {
            return Material.IRON_SWORD;
        }

        // Killed by stats
        if (category.equals("minecraft:killed_by")) {
            return Material.SKELETON_SKULL;
        }

        // Crafted stats
        if (category.equals("minecraft:crafted")) {
            return getMaterialFromName(statKey, Material.CRAFTING_TABLE);
        }

        // Used stats
        if (category.equals("minecraft:used")) {
            return getMaterialFromName(statKey, Material.STICK);
        }

        // Broken stats
        if (category.equals("minecraft:broken")) {
            return getMaterialFromName(statKey, Material.WOODEN_PICKAXE);
        }

        // Picked up stats
        if (category.equals("minecraft:picked_up")) {
            return getMaterialFromName(statKey, Material.CHEST);
        }

        // Dropped stats
        if (category.equals("minecraft:dropped")) {
            return getMaterialFromName(statKey, Material.DROPPER);
        }

        // Default
        return Material.PAPER;
    }

    private Material getMaterialFromName(String statKey, Material fallback) {
        try {
            // Remove "minecraft:" prefix and convert to uppercase
            String materialName = statKey.replace("minecraft:", "").toUpperCase();
            Material material = Material.getMaterial(materialName);
            return (material != null) ? material : fallback;
        } catch (Exception e) {
            return fallback;
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
        if (allStats.isEmpty()) {
            // No stats available
            setItem(22, createNoStatsItem());
            return;
        }

        // Display stats with pagination (45 items per page)
        int startSlot = 0;
        int itemsPerPage = 45;
        int startIndex = (getPage() - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, allStats.size());

        for (int i = startIndex; i < endIndex; i++) {
            StatEntry stat = allStats.get(i);
            setItem(startSlot++, createStatItem(stat));
        }
    }

    private ItemStack createNoStatsItem() {
        String title = TranslationManager.translate("PlayerStats", "no_stats_title", "&cNessuna statistica");
        String lore = TranslationManager.translate("PlayerStats", "no_stats_lore",
            "&7Il giocatore non ha ancora\n&7generato statistiche.");
        return createItem(Material.BARRIER, title, lore.split("\n"));
    }

    private ItemStack createStatItem(StatEntry stat) {
        ItemStack item = new ItemStack(stat.material);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e" + stat.name));

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Valore: &f" + stat.value));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8Categoria: " + stat.category.replace("minecraft:", "")));
            meta.setLore(lore);

            item.setItemMeta(meta);
        }

        return item;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        // Navigation buttons
        if (slot == 45) { // Back
            handleBack(clicker);
        } else if (slot == 47 && getPage() > 1) { // Previous page
            clicker.closeInventory();
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new PlayerStatsGui(clicker, targetPlayer, returnPage, getPage() - 1).open()
            );
        } else if (slot == 51) { // Next page
            int itemsPerPage = 45;
            if (allStats.size() > getPage() * itemsPerPage) {
                clicker.closeInventory();
                Bukkit.getScheduler().runTask(
                    Bukkit.getPluginManager().getPlugin("AdminManager"),
                    () -> new PlayerStatsGui(clicker, targetPlayer, returnPage, getPage() + 1).open()
                );
            }
        }
    }

    private void handleBack(Player clicker) {
        // Return to PlayerDataDetailGui
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerDataDetailGui(clicker, targetPlayer, returnPage).open()
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
        final String category;

        StatEntry(Material material, String name, String value, String category) {
            this.material = material;
            this.name = name;
            this.value = value;
            this.category = category;
        }
    }
}
