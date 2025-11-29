package it.alessiogta.adminmanager.gui;

import com.google.gson.Gson;
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
    private final int returnPage;
    private final Map<String, Integer> categoryCounts;

    public PlayerStatsGui(Player admin, OfflinePlayer targetPlayer, int returnPage) {
        super(admin, formatTitle(targetPlayer), 1);
        this.admin = admin;
        this.targetPlayer = targetPlayer;
        this.returnPage = returnPage;
        this.categoryCounts = loadCategoryCounts();
        setupCategoryItems();
    }

    private static String formatTitle(OfflinePlayer targetPlayer) {
        String playerName = targetPlayer.getName() != null ? targetPlayer.getName() : "Unknown";
        String baseTitle = TranslationManager.translate("PlayerStats", "title", "&a&lStatistiche: {player}")
            .replace("{player}", playerName);
        return ChatColor.translateAlternateColorCodes('&', baseTitle);
    }

    @Override
    protected void setupNavigationButtons() {
        // Back button (slot 49)
        String backTitle = TranslationManager.translate("PlayerStats", "back_button_title", "&cIndietro");
        String backLore = TranslationManager.translate("PlayerStats", "back_button_lore", "&7Torna ai dettagli giocatore");
        setItem(49, createItem(Material.DARK_OAK_DOOR, backTitle, backLore));
    }

    private Map<String, Integer> loadCategoryCounts() {
        Map<String, Integer> counts = new HashMap<>();

        if (Bukkit.getWorlds().isEmpty()) {
            return counts;
        }

        World mainWorld = Bukkit.getWorlds().get(0);
        File statsFile = new File(mainWorld.getWorldFolder(), "stats/" + targetPlayer.getUniqueId() + ".json");

        if (!statsFile.exists()) {
            return counts;
        }

        try (FileReader reader = new FileReader(statsFile)) {
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            if (root != null && root.has("stats")) {
                JsonObject statsObj = root.getAsJsonObject("stats");

                // Count stats in each category
                for (String category : statsObj.keySet()) {
                    JsonObject categoryStats = statsObj.getAsJsonObject(category);
                    counts.put(category, categoryStats.size());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("[AdminManager] Errore lettura statistiche per " +
                targetPlayer.getName() + ": " + e.getMessage());
        }

        return counts;
    }

    private void setupCategoryItems() {
        if (categoryCounts.isEmpty()) {
            // No stats available
            setItem(22, createNoStatsItem());
            return;
        }

        // Display category buttons
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
        int slotIndex = 0;

        // Define categories with icons
        String[] categoryOrder = {
            "minecraft:custom",
            "minecraft:mined",
            "minecraft:killed",
            "minecraft:killed_by",
            "minecraft:crafted",
            "minecraft:used",
            "minecraft:broken",
            "minecraft:picked_up",
            "minecraft:dropped"
        };

        for (String category : categoryOrder) {
            if (categoryCounts.containsKey(category) && slotIndex < slots.length) {
                setItem(slots[slotIndex++], createCategoryButton(category, categoryCounts.get(category)));
            }
        }
    }

    private ItemStack createCategoryButton(String category, int count) {
        String categoryName = category.replace("minecraft:", "");
        Material icon = getCategoryIcon(category);

        String title = TranslationManager.translate("PlayerStats", "category_" + categoryName + "_title",
            "&e" + formatCategoryName(categoryName));
        String lore = TranslationManager.translate("PlayerStats", "category_lore",
            "&7Statistiche: &e{count}\n\n&e&lClick: &7Visualizza dettagli")
            .replace("{count}", String.valueOf(count));

        return createItem(icon, title, lore.split("\n"));
    }

    private String formatCategoryName(String category) {
        // Convert category names to readable format
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

    private Material getCategoryIcon(String category) {
        switch (category) {
            case "minecraft:custom": return Material.CLOCK;
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

    private ItemStack createNoStatsItem() {
        String title = TranslationManager.translate("PlayerStats", "no_stats_title", "&cNessuna statistica");
        String lore = TranslationManager.translate("PlayerStats", "no_stats_lore",
            "&7Il giocatore non ha ancora\n&7generato statistiche.");
        return createItem(Material.BARRIER, title, lore.split("\n"));
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        if (slot == 49) {
            handleBack(clicker);
            return;
        }

        // Check if clicked on a category button
        int[] categorySlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
        String[] categoryOrder = {
            "minecraft:custom",
            "minecraft:mined",
            "minecraft:killed",
            "minecraft:killed_by",
            "minecraft:crafted",
            "minecraft:used",
            "minecraft:broken",
            "minecraft:picked_up",
            "minecraft:dropped"
        };

        for (int i = 0; i < categorySlots.length && i < categoryOrder.length; i++) {
            if (slot == categorySlots[i] && categoryCounts.containsKey(categoryOrder[i])) {
                handleCategoryClick(categoryOrder[i], clicker);
                return;
            }
        }
    }

    private void handleCategoryClick(String category, Player clicker) {
        // Open PlayerStatsCategoryGui for this category
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerStatsCategoryGui(clicker, targetPlayer, category, returnPage).open()
        );
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
}
