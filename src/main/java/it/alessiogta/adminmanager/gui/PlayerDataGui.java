package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerDataGui extends BaseGui {

    private final List<PlayerDataInfo> playerDataList;
    private final Player admin;

    public PlayerDataGui(Player admin, int page) {
        super(admin, formatTitle(), page);
        this.admin = admin;
        this.playerDataList = loadPlayerData();
        setupPlayerDataItems();

        // Rebuild navigation buttons now that playerDataList is loaded
        setupNavigationButtons();
    }

    private static String formatTitle() {
        String baseTitle = TranslationManager.translate("PlayerData", "title", "&6&lPlayer Data");
        return ChatColor.translateAlternateColorCodes('&', baseTitle);
    }

    private List<PlayerDataInfo> loadPlayerData() {
        List<PlayerDataInfo> dataList = new ArrayList<>();

        if (!Bukkit.getWorlds().isEmpty()) {
            World mainWorld = Bukkit.getWorlds().get(0);
            File playerDataFolder = new File(mainWorld.getWorldFolder(), "playerdata");

            if (playerDataFolder.exists() && playerDataFolder.isDirectory()) {
                File[] files = playerDataFolder.listFiles((dir, name) -> name.endsWith(".dat"));
                if (files != null) {
                    for (File file : files) {
                        try {
                            // Extract UUID from filename (e.g., "123e4567-e89b-12d3-a456-426614174000.dat")
                            String uuidString = file.getName().replace(".dat", "");
                            UUID uuid = UUID.fromString(uuidString);
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

                            // Get last modified date
                            long lastModified = file.lastModified();

                            dataList.add(new PlayerDataInfo(uuid, offlinePlayer.getName(), lastModified));
                        } catch (IllegalArgumentException e) {
                            // Skip invalid UUID files
                        }
                    }
                }
            }
        }

        // Sort by last modified date (most recent first)
        dataList.sort((a, b) -> Long.compare(b.lastModified, a.lastModified));

        return dataList;
    }

    private void setupPlayerDataItems() {
        int startSlot = 0;
        int itemsPerPage = 45; // Excluding last row for navigation buttons
        int startIndex = (getPage() - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, playerDataList.size());

        for (int i = startIndex; i < endIndex; i++) {
            PlayerDataInfo info = playerDataList.get(i);
            setItem(startSlot++, createPlayerDataHead(info));
        }
    }

    @Override
    protected void setupNavigationButtons() {
        int itemsPerPage = 45;
        // Handle case where playerDataList is not yet initialized (called from super constructor)
        int totalPlayers = (playerDataList != null) ? playerDataList.size() : 0;

        // Back to Config Manager button (slot 45)
        String backTitle = TranslationManager.translate("PlayerData", "back_button_title", "&cIndietro");
        String backLore = TranslationManager.translate("PlayerData", "back_button_lore", "&7Torna a Config Manager");
        setItem(45, createItem(Material.DARK_OAK_DOOR, backTitle, backLore));

        // Previous page button (slot 47) - only if there's a previous page
        if (getPage() > 1) {
            String prevTitle = TranslationManager.translate("PlayerData", "previous_page", "&ePagina precedente");
            setItem(47, createItem(Material.ARROW, prevTitle));
        }

        // Info button (slot 49) - shows total count
        String infoTitle = TranslationManager.translate("PlayerData", "info_title", "&6Info");
        String infoLore = TranslationManager.translate("PlayerData", "info_lore",
            "&7Totale giocatori: &e{count}\n&7Pagina: &e{page}&7/&e{maxPage}")
            .replace("{count}", String.valueOf(totalPlayers))
            .replace("{page}", String.valueOf(getPage()))
            .replace("{maxPage}", String.valueOf(Math.max(1, (int) Math.ceil(totalPlayers / (double) itemsPerPage))));
        setItem(49, createItem(Material.BOOK, infoTitle, infoLore.split("\n")));

        // Next page button (slot 51) - only if there are more players
        if (totalPlayers > getPage() * itemsPerPage) {
            String nextTitle = TranslationManager.translate("PlayerData", "next_page", "&ePagina successiva");
            setItem(51, createItem(Material.ARROW, nextTitle));
        }
    }

    private ItemStack createPlayerDataHead(PlayerDataInfo info) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(info.uuid));

            String displayName = info.playerName != null ? info.playerName : "Unknown";
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e" + displayName));

            // Format last modified date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String lastSeen = dateFormat.format(new Date(info.lastModified));

            List<String> lore = new ArrayList<>();
            // UUID line - translated
            String uuidLine = TranslationManager.translate("PlayerData", "player_uuid", "&7UUID: &f{uuid}")
                .replace("{uuid}", info.uuid.toString());
            lore.add(ChatColor.translateAlternateColorCodes('&', uuidLine));

            // Last seen line - translated
            String lastSeenLine = TranslationManager.translate("PlayerData", "player_last_seen", "&7Ultimo accesso: &f{date}")
                .replace("{date}", lastSeen);
            lore.add(ChatColor.translateAlternateColorCodes('&', lastSeenLine));

            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("PlayerData", "player_lore", "&e&lLEFT: &7Visualizza dettagli")));

            meta.setLore(lore);
            skull.setItemMeta(meta);
        }

        return skull;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        // Navigation buttons
        if (slot == 45) { // Back to Config Manager
            handleBack(clicker);
        } else if (slot == 47 && getPage() > 1) { // Previous page
            clicker.closeInventory();
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new PlayerDataGui(clicker, getPage() - 1).open()
            );
        } else if (slot == 51) { // Next page
            int itemsPerPage = 45;
            if (playerDataList.size() > getPage() * itemsPerPage) {
                clicker.closeInventory();
                Bukkit.getScheduler().runTask(
                    Bukkit.getPluginManager().getPlugin("AdminManager"),
                    () -> new PlayerDataGui(clicker, getPage() + 1).open()
                );
            }
        } else if (slot >= 0 && slot < 45) { // Player data item clicked
            int itemsPerPage = 45;
            int startIndex = (getPage() - 1) * itemsPerPage;
            int playerIndex = startIndex + slot;

            if (playerIndex < playerDataList.size()) {
                PlayerDataInfo info = playerDataList.get(playerIndex);
                handlePlayerDataClick(info, clicker);
            }
        }
    }

    private void handlePlayerDataClick(PlayerDataInfo info, Player clicker) {
        // Open PlayerDataDetailGui with detailed info and actions
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(info.uuid);

        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerDataDetailGui(clicker, offlinePlayer, getPage()).open()
        );
    }

    private void handleBack(Player clicker) {
        // Don't close inventory - open ServerManagerGui directly
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new ServerManagerGui(clicker).open()
        );
    }

    @Override
    public void open() {
        inventory = build();
        admin.openInventory(inventory);
    }

    // Inner class to hold player data info
    private static class PlayerDataInfo {
        final UUID uuid;
        final String playerName;
        final long lastModified;

        PlayerDataInfo(UUID uuid, String playerName, long lastModified) {
            this.uuid = uuid;
            this.playerName = playerName;
            this.lastModified = lastModified;
        }
    }
}
