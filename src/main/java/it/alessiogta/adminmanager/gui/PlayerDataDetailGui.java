package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerDataDetailGui extends BaseGui {

    private final Player admin;
    private final OfflinePlayer targetPlayer;
    private final int previousPage;

    public PlayerDataDetailGui(Player admin, OfflinePlayer targetPlayer, int previousPage) {
        super(admin, formatTitle(targetPlayer), 1);
        this.admin = admin;
        this.targetPlayer = targetPlayer;
        this.previousPage = previousPage;
        setupDetailItems();
    }

    private static String formatTitle(OfflinePlayer targetPlayer) {
        String playerName = targetPlayer.getName() != null ? targetPlayer.getName() : "Unknown";
        String baseTitle = TranslationManager.translate("PlayerDataDetail", "title", "&6&lDati: {player}")
            .replace("{player}", playerName);
        return ChatColor.translateAlternateColorCodes('&', baseTitle);
    }

    @Override
    protected void setupNavigationButtons() {
        // Custom layout - no automatic navigation
    }

    private void setupDetailItems() {
        boolean isOnline = targetPlayer.isOnline();

        // Row 1: Player Head (slot 4)
        setItem(4, createPlayerHead());

        // Row 2: Info Items
        setItem(10, createInfoItem());
        setItem(13, createStatusItem(isOnline));
        setItem(16, createTimeInfoItem());

        // Row 3-4: Action Buttons
        if (isOnline) {
            // Online player actions
            setItem(19, createTeleportToButton());
            setItem(21, createTeleportHereButton());
            setItem(23, createViewInventoryButton());
            setItem(25, createManageButton());
        } else {
            // Offline player message
            setItem(22, createOfflineInfoItem());
        }

        // Statistics button (slot 31) - Available for both online and offline (centered)
        setItem(31, createStatsButton());

        // Back button (slot 49)
        setItem(49, createBackButton());
    }

    private ItemStack createPlayerHead() {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(targetPlayer);
            String displayName = targetPlayer.getName() != null ? targetPlayer.getName() : "Unknown";
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&l" + displayName));
            skull.setItemMeta(meta);
        }

        return skull;
    }

    private ItemStack createInfoItem() {
        String title = TranslationManager.translate("PlayerDataDetail", "info_title", "&6Informazioni");

        String uuidStr = targetPlayer.getUniqueId().toString();
        String hasPlayed = targetPlayer.hasPlayedBefore() ? "&aYes" : "&cNo";

        String lore = TranslationManager.translate("PlayerDataDetail", "info_lore",
            "&7UUID: &f{uuid}\n&7Ha giocato prima: {played}")
            .replace("{uuid}", uuidStr)
            .replace("{played}", hasPlayed);

        return createItem(Material.BOOK, title, lore.split("\n"));
    }

    private ItemStack createStatusItem(boolean isOnline) {
        Material material = isOnline ? Material.LIME_DYE : Material.GRAY_DYE;
        String status = isOnline ? "&aOnline" : "&cOffline";

        String title = TranslationManager.translate("PlayerDataDetail", "status_title", "&6Stato: {status}")
            .replace("{status}", status);

        String lore = isOnline
            ? TranslationManager.translate("PlayerDataDetail", "status_online", "&7Il giocatore è attualmente connesso")
            : TranslationManager.translate("PlayerDataDetail", "status_offline", "&7Il giocatore è offline");

        return createItem(material, title, lore);
    }

    private ItemStack createTimeInfoItem() {
        String title = TranslationManager.translate("PlayerDataDetail", "time_title", "&6Tempo");

        List<String> lore = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // First played
        if (targetPlayer.hasPlayedBefore() && targetPlayer.getFirstPlayed() > 0) {
            String firstPlayed = dateFormat.format(new Date(targetPlayer.getFirstPlayed()));
            String firstLine = TranslationManager.translate("PlayerDataDetail", "first_played", "&7Primo accesso: &f{date}")
                .replace("{date}", firstPlayed);
            lore.add(ChatColor.translateAlternateColorCodes('&', firstLine));
        }

        // Last played
        if (targetPlayer.hasPlayedBefore() && targetPlayer.getLastPlayed() > 0) {
            String lastPlayed = dateFormat.format(new Date(targetPlayer.getLastPlayed()));
            String lastLine = TranslationManager.translate("PlayerDataDetail", "last_played", "&7Ultimo accesso: &f{date}")
                .replace("{date}", lastPlayed);
            lore.add(ChatColor.translateAlternateColorCodes('&', lastLine));
        }

        // Time since last seen
        if (!targetPlayer.isOnline() && targetPlayer.getLastPlayed() > 0) {
            long timeSince = System.currentTimeMillis() - targetPlayer.getLastPlayed();
            String timeSinceStr = formatTimeSince(timeSince);
            String sinceLine = TranslationManager.translate("PlayerDataDetail", "time_since", "&7Tempo trascorso: &f{time}")
                .replace("{time}", timeSinceStr);
            lore.add(ChatColor.translateAlternateColorCodes('&', sinceLine));
        }

        ItemStack item = new ItemStack(Material.CLOCK);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private String formatTimeSince(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + " giorni";
        if (hours > 0) return hours + " ore";
        if (minutes > 0) return minutes + " minuti";
        return seconds + " secondi";
    }

    private ItemStack createTeleportToButton() {
        String title = TranslationManager.translate("PlayerDataDetail", "teleport_to_title", "&eTeleporta a giocatore");
        String lore = TranslationManager.translate("PlayerDataDetail", "teleport_to_lore", "&7Teletrasportati alla posizione del giocatore");
        return createItem(Material.ENDER_PEARL, title, lore);
    }

    private ItemStack createTeleportHereButton() {
        String title = TranslationManager.translate("PlayerDataDetail", "teleport_here_title", "&eTeleporta qui");
        String lore = TranslationManager.translate("PlayerDataDetail", "teleport_here_lore", "&7Teletrasporta il giocatore da te");
        return createItem(Material.ENDER_EYE, title, lore);
    }

    private ItemStack createViewInventoryButton() {
        String title = TranslationManager.translate("PlayerDataDetail", "view_inventory_title", "&6Visualizza Inventario");
        String lore = TranslationManager.translate("PlayerDataDetail", "view_inventory_lore", "&7Apri l'inventario del giocatore");
        return createItem(Material.CHEST, title, lore);
    }

    private ItemStack createManageButton() {
        String title = TranslationManager.translate("PlayerDataDetail", "manage_title", "&dGestisci Giocatore");
        String lore = TranslationManager.translate("PlayerDataDetail", "manage_lore", "&7Apri il menu di gestione completo");
        return createItem(Material.DIAMOND_SWORD, title, lore);
    }

    private ItemStack createOfflineInfoItem() {
        String title = TranslationManager.translate("PlayerDataDetail", "offline_info_title", "&cGiocatore Offline");
        String lore = TranslationManager.translate("PlayerDataDetail", "offline_info_lore",
            "&7Il giocatore non è attualmente\n&7connesso al server.\n\n&7Le azioni sono limitate.");
        return createItem(Material.BARRIER, title, lore.split("\n"));
    }

    private ItemStack createStatsButton() {
        String title = TranslationManager.translate("PlayerDataDetail", "stats_title", "&aStatistiche");
        String lore = TranslationManager.translate("PlayerDataDetail", "stats_lore",
            "&7Visualizza le statistiche del giocatore\n&7Blocchi rotti, mob uccisi, ecc.");
        return createItem(Material.WRITABLE_BOOK, title, lore.split("\n"));
    }

    private ItemStack createBackButton() {
        String title = TranslationManager.translate("PlayerDataDetail", "back_button_title", "&cIndietro");
        String lore = TranslationManager.translate("PlayerDataDetail", "back_button_lore", "&7Torna alla lista player data");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        // Statistics and back button work for both online and offline
        if (slot == 31) {
            handleStats(clicker);
            return;
        }
        if (slot == 49) {
            handleBack(clicker);
            return;
        }

        // Online player actions only
        if (!targetPlayer.isOnline()) {
            return;
        }

        Player onlineTarget = targetPlayer.getPlayer();
        if (onlineTarget == null) return;

        switch (slot) {
            case 19: handleTeleportTo(clicker, onlineTarget); break;
            case 21: handleTeleportHere(clicker, onlineTarget); break;
            case 23: handleViewInventory(clicker, onlineTarget); break;
            case 25: handleManage(clicker, onlineTarget); break;
        }
    }

    private void handleTeleportTo(Player admin, Player target) {
        admin.closeInventory();
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> {
                admin.teleport(target.getLocation());
                String message = TranslationManager.translate("PlayerDataDetail", "teleported_to",
                    "&aTeletrasportato a &e{player}")
                    .replace("{player}", target.getName());
                admin.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        );
    }

    private void handleTeleportHere(Player admin, Player target) {
        admin.closeInventory();
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> {
                target.teleport(admin.getLocation());
                String adminMsg = TranslationManager.translate("PlayerDataDetail", "teleported_here",
                    "&e{player} &ateletrasportato da te")
                    .replace("{player}", target.getName());
                admin.sendMessage(ChatColor.translateAlternateColorCodes('&', adminMsg));

                String targetMsg = TranslationManager.translate("PlayerDataDetail", "you_were_teleported",
                    "&aSei stato teletrasportato da &e{admin}")
                    .replace("{admin}", admin.getName());
                target.sendMessage(ChatColor.translateAlternateColorCodes('&', targetMsg));
            }
        );
    }

    private void handleViewInventory(Player admin, Player target) {
        // Open PlayerInventoryGui
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerInventoryGui(admin, target).open()
        );
    }

    private void handleManage(Player admin, Player target) {
        // Open PlayerManage GUI
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerManage(admin, target).open()
        );
    }

    private void handleStats(Player clicker) {
        // Open PlayerStatsGui
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerStatsGui(clicker, targetPlayer, previousPage).open()
        );
    }

    private void handleBack(Player clicker) {
        // Return to PlayerDataGui at the same page
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerDataGui(clicker, previousPage).open()
        );
    }

    @Override
    public void open() {
        inventory = build();
        admin.openInventory(inventory);
    }
}
