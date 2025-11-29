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
    private final int previousPage;
    private final List<StatEntry> displayedStats;

    public PlayerStatsGui(Player admin, OfflinePlayer targetPlayer, int previousPage) {
        super(admin, formatTitle(targetPlayer), 1);
        this.admin = admin;
        this.targetPlayer = targetPlayer;
        this.previousPage = previousPage;
        this.displayedStats = loadPlayerStats();
        setupStatsItems();
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

    private List<StatEntry> loadPlayerStats() {
        List<StatEntry> stats = new ArrayList<>();

        if (Bukkit.getWorlds().isEmpty()) {
            return stats;
        }

        World mainWorld = Bukkit.getWorlds().get(0);
        File statsFile = new File(mainWorld.getWorldFolder(), "stats/" + targetPlayer.getUniqueId() + ".json");

        if (!statsFile.exists()) {
            // No stats file found - player never played or stats not generated
            return stats;
        }

        try (FileReader reader = new FileReader(statsFile)) {
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            if (root == null || !root.has("stats")) {
                return stats;
            }

            JsonObject statsObj = root.getAsJsonObject("stats");

            // Play time
            if (statsObj.has("minecraft:custom")) {
                JsonObject custom = statsObj.getAsJsonObject("minecraft:custom");
                if (custom.has("minecraft:play_time")) {
                    int ticks = custom.get("minecraft:play_time").getAsInt();
                    int hours = ticks / 20 / 3600;
                    int minutes = (ticks / 20 / 60) % 60;
                    stats.add(new StatEntry(Material.CLOCK, "Tempo di gioco",
                        hours + "h " + minutes + "m", 0));
                }

                // Deaths
                if (custom.has("minecraft:deaths")) {
                    int deaths = custom.get("minecraft:deaths").getAsInt();
                    stats.add(new StatEntry(Material.SKELETON_SKULL, "Morti",
                        String.valueOf(deaths), 1));
                }

                // Jumps
                if (custom.has("minecraft:jump")) {
                    int jumps = custom.get("minecraft:jump").getAsInt();
                    stats.add(new StatEntry(Material.RABBIT_FOOT, "Salti",
                        formatNumber(jumps), 2));
                }

                // Distance walked
                if (custom.has("minecraft:walk_one_cm")) {
                    int cm = custom.get("minecraft:walk_one_cm").getAsInt();
                    int meters = cm / 100;
                    stats.add(new StatEntry(Material.LEATHER_BOOTS, "Distanza camminata",
                        formatNumber(meters) + " m", 3));
                }
            }

            // Blocks mined
            if (statsObj.has("minecraft:mined")) {
                JsonObject mined = statsObj.getAsJsonObject("minecraft:mined");
                int totalMined = 0;
                for (String key : mined.keySet()) {
                    totalMined += mined.get(key).getAsInt();
                }
                if (totalMined > 0) {
                    stats.add(new StatEntry(Material.DIAMOND_PICKAXE, "Blocchi rotti",
                        formatNumber(totalMined), 4));
                }
            }

            // Mobs killed
            if (statsObj.has("minecraft:killed")) {
                JsonObject killed = statsObj.getAsJsonObject("minecraft:killed");
                int totalKilled = 0;
                for (String key : killed.keySet()) {
                    totalKilled += killed.get(key).getAsInt();
                }
                if (totalKilled > 0) {
                    stats.add(new StatEntry(Material.IRON_SWORD, "Mob uccisi",
                        formatNumber(totalKilled), 5));
                }
            }

            // Items crafted
            if (statsObj.has("minecraft:crafted")) {
                JsonObject crafted = statsObj.getAsJsonObject("minecraft:crafted");
                int totalCrafted = 0;
                for (String key : crafted.keySet()) {
                    totalCrafted += crafted.get(key).getAsInt();
                }
                if (totalCrafted > 0) {
                    stats.add(new StatEntry(Material.CRAFTING_TABLE, "Item craftati",
                        formatNumber(totalCrafted), 6));
                }
            }

            // Damage dealt
            if (statsObj.has("minecraft:custom")) {
                JsonObject custom = statsObj.getAsJsonObject("minecraft:custom");
                if (custom.has("minecraft:damage_dealt")) {
                    int damage = custom.get("minecraft:damage_dealt").getAsInt();
                    stats.add(new StatEntry(Material.DIAMOND_SWORD, "Danno inflitto",
                        formatNumber(damage / 10), 7));
                }
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("[AdminManager] Errore lettura statistiche per " +
                targetPlayer.getName() + ": " + e.getMessage());
        }

        // Sort by slot order
        stats.sort(Comparator.comparingInt(s -> s.slot));

        return stats;
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
        if (displayedStats.isEmpty()) {
            // No stats available
            setItem(22, createNoStatsItem());
            return;
        }

        // Display stats starting from slot 10
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
        for (int i = 0; i < Math.min(displayedStats.size(), slots.length); i++) {
            StatEntry stat = displayedStats.get(i);
            setItem(slots[i], createStatItem(stat));
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
            meta.setLore(lore);

            item.setItemMeta(meta);
        }

        return item;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        if (slot == 49) {
            handleBack(clicker);
        }
    }

    private void handleBack(Player clicker) {
        // Return to PlayerDataDetailGui
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerDataDetailGui(clicker, targetPlayer, previousPage).open()
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
        final int slot;

        StatEntry(Material material, String name, String value, int slot) {
            this.material = material;
            this.name = name;
            this.value = value;
            this.slot = slot;
        }
    }
}
