package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WhitelistEditorGui extends BaseGui {

    private final Player admin;
    private final List<OfflinePlayer> whitelistedPlayers;

    public WhitelistEditorGui(Player admin) {
        super(admin, TranslationManager.translate("WhitelistEditor", "title", "&fWhitelist Editor"), 1);
        this.admin = admin;
        this.whitelistedPlayers = new ArrayList<>(Bukkit.getWhitelistedPlayers());
        setupGuiItems();
    }

    @Override
    protected void setupNavigationButtons() {
        // Custom navigation
        // Back button at slot 49
        String backTitle = TranslationManager.translate("WhitelistEditor", "back_button_title", "&cIndietro");
        String backLore = TranslationManager.translate("WhitelistEditor", "back_button_lore", "&7Torna a Server Manager");
        setItem(49, createItem(Material.DARK_OAK_DOOR, backTitle, backLore));

        // Add player button at slot 45
        String addTitle = TranslationManager.translate("WhitelistEditor", "add_button_title", "&aAggiungi Giocatore");
        String addLore = TranslationManager.translate("WhitelistEditor", "add_button_lore",
            "&7Aggiungi un giocatore online\n&7alla whitelist");
        setItem(45, createItem(Material.LIME_DYE, addTitle, addLore.split("\n")));

        // Clear whitelist button at slot 53
        String clearTitle = TranslationManager.translate("WhitelistEditor", "clear_button_title", "&cCancella Whitelist");
        String clearLore = TranslationManager.translate("WhitelistEditor", "clear_button_lore",
            "&7Rimuovi tutti i giocatori\n&7dalla whitelist");
        setItem(53, createItem(Material.BARRIER, clearTitle, clearLore.split("\n")));
    }

    private void setupGuiItems() {
        // Display whitelisted players (max 45 slots)
        int slot = 0;
        for (OfflinePlayer player : whitelistedPlayers) {
            if (slot >= 45) break; // Max 45 slots for players

            setItem(slot, createWhitelistedPlayerHead(player));
            slot++;
        }

        // If no players, show info
        if (whitelistedPlayers.isEmpty()) {
            String title = TranslationManager.translate("WhitelistEditor", "empty_title", "&7Whitelist Vuota");
            String lore = TranslationManager.translate("WhitelistEditor", "empty_lore",
                "&7Nessun giocatore in whitelist\n&7Clicca sul pulsante verde\n&7per aggiungerne uno");
            setItem(22, createItem(Material.BARRIER, title, lore.split("\n")));
        }
    }

    private ItemStack createWhitelistedPlayerHead(OfflinePlayer player) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("WhitelistEditor", "player_name", "&a" + player.getName())));

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("WhitelistEditor", "player_uuid", "&7UUID: &f" + player.getUniqueId())));
            lore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("WhitelistEditor", "player_status",
                    "&7Stato: " + (player.isOnline() ? "&aOnline" : "&cOffline"))));
            lore.add("");
            lore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("WhitelistEditor", "player_action", "&c&lClick: &7Rimuovi dalla whitelist")));

            meta.setLore(lore);
            skull.setItemMeta(meta);
        }

        return skull;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        if (slot == 49) {
            // Back button
            handleBack(clicker);
            return;
        }

        if (slot == 45) {
            // Add player button
            handleAddPlayer(clicker);
            return;
        }

        if (slot == 53) {
            // Clear whitelist button
            handleClearWhitelist(clicker);
            return;
        }

        // Check if clicked on a whitelisted player head
        if (slot >= 0 && slot < 45 && slot < whitelistedPlayers.size()) {
            handleRemovePlayer(slot, clicker);
        }
    }

    private void handleAddPlayer(Player clicker) {
        // Get first online player not in whitelist
        OfflinePlayer toAdd = null;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.isWhitelisted()) {
                toAdd = online;
                break;
            }
        }

        if (toAdd == null) {
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("WhitelistEditor", "no_players_to_add",
                    "&cNessun giocatore online da aggiungere!")));
            return;
        }

        toAdd.setWhitelisted(true);
        String message = TranslationManager.translate("WhitelistEditor", "player_added",
            "&a{player} aggiunto alla whitelist!")
            .replace("{player}", toAdd.getName());
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        // Refresh GUI
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new WhitelistEditorGui(clicker).open()
        );
    }

    private void handleRemovePlayer(int slot, Player clicker) {
        OfflinePlayer toRemove = whitelistedPlayers.get(slot);
        toRemove.setWhitelisted(false);

        String message = TranslationManager.translate("WhitelistEditor", "player_removed",
            "&c{player} rimosso dalla whitelist!")
            .replace("{player}", toRemove.getName());
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        // Refresh GUI
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new WhitelistEditorGui(clicker).open()
        );
    }

    private void handleClearWhitelist(Player clicker) {
        Set<OfflinePlayer> whitelisted = Bukkit.getWhitelistedPlayers();
        int count = whitelisted.size();

        for (OfflinePlayer player : whitelisted) {
            player.setWhitelisted(false);
        }

        String message = TranslationManager.translate("WhitelistEditor", "whitelist_cleared",
            "&cRimossi &e{count} &cgiocatori dalla whitelist!")
            .replace("{count}", String.valueOf(count));
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        // Refresh GUI
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new WhitelistEditorGui(clicker).open()
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
}
