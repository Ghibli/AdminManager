package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.PlayerLogger;
import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerManage extends BaseGui {

    private final Player targetPlayer;

    public PlayerManage(Player player, Player targetPlayer) {
        super(player, formatTitle(targetPlayer), 1); // GUI con una sola pagina
        this.targetPlayer = targetPlayer;
        setupGuiItems();
    }

    private static String formatTitle(Player targetPlayer) {
        return String.format("§a[§6%s§a] §7+ details", targetPlayer.getName()); // Hard-coded titolo
    }

    private void setupGuiItems() {
        setItem(10, createTeleportButton());
        setItem(11, createTpToMeButton());
        setItem(13, createKickButton());
        setItem(14, createBanButton());
        setItem(49, createExitButton());
    }

    private ItemStack createTeleportButton() {
        String title = TranslationManager.translate("PlayerManage", "teleport_title", "&aTeletrasporto");
        String lore = TranslationManager.translate("PlayerManage", "teleport_lore", "&7Teletrasportati da {player}")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.ENDER_PEARL, title, lore);
    }

    private ItemStack createTpToMeButton() {
        String title = TranslationManager.translate("PlayerManage", "tp_to_me_title", "&bTP da me");
        String lore = TranslationManager.translate("PlayerManage", "tp_to_me_lore", "&7Teletrasporta {player} da te")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.COMPASS, title, lore);
    }

    private ItemStack createKickButton() {
        String title = TranslationManager.translate("PlayerManage", "kick_title", "&cEspelli dal server");
        String lore = TranslationManager.translate("PlayerManage", "kick_lore", "&7Espelli dal server {player}")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.IRON_DOOR, title, lore);
    }

    private ItemStack createBanButton() {
        String title = TranslationManager.translate("PlayerManage", "ban_title", "&4Ban dal server");
        String lore = TranslationManager.translate("PlayerManage", "ban_lore", "&7Banna {player} dal server")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.RED_BANNER, title, lore);
    }

    private ItemStack createExitButton() {
        String title = TranslationManager.translate("PlayerManage", "back_button_title", "&cIndietro");
        String lore = TranslationManager.translate("PlayerManage", "back_button_lore", "&aRitorna alla lista giocatori");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();

        switch (slot) {
            case 10:
                handleTeleportClick(event);
                break;
            case 11:
                handleTpToMeClick(event);
                break;
            case 13:
                handleKickClick(event);
                break;
            case 14:
                handleBanClick(event);
                break;
            case 49:
                handleExitClick(event);
                break;
            default:
                event.setCancelled(true);
                break;
        }
    }

    private void handleTeleportClick(InventoryClickEvent event) {
        event.getWhoClicked().teleport(targetPlayer.getLocation());
        String teleportMessage = TranslationManager.translate("PlayerManage", "teleport_message", "&aTi sei teletrasportato da &e{player}")
                .replace("{player}", targetPlayer.getName());
        event.getWhoClicked().sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', teleportMessage));
    }

    private void handleTpToMeClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        if (targetPlayer.isOnline()) {
            targetPlayer.teleport(sender.getLocation());
            String successMessage = TranslationManager.translate("PlayerManage", "tp_to_me_success", "&a{player} è stato teletrasportato da te")
                    .replace("{player}", targetPlayer.getName());
            sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', successMessage));
        } else {
            String failureMessage = TranslationManager.translate("PlayerManage", "tp_to_me_failure", "&c{player} non è online!")
                    .replace("{player}", targetPlayer.getName());
            sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', failureMessage));
        }
    }

    private void handleKickClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        if (targetPlayer.isOnline()) {
            String kickMessage = TranslationManager.translate("PlayerManage", "kick_message", "&c{player} è stato espulso dal server")
                    .replace("{player}", targetPlayer.getName());
            String kickReason = TranslationManager.translate("PlayerManage", "kick_reason", "&cSei stato espulso dal server!");
            targetPlayer.kickPlayer(org.bukkit.ChatColor.translateAlternateColorCodes('&', kickReason));
            sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', kickMessage));
            PlayerLogger.logKick(sender.getName(), targetPlayer.getName());
        } else {
            String failureMessage = TranslationManager.translate("PlayerManage", "kick_failure", "&c{player} non è online!")
                    .replace("{player}", targetPlayer.getName());
            sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', failureMessage));
        }
    }

    private void handleBanClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        if (targetPlayer.isOnline()) {
            String banMessage = TranslationManager.translate("PlayerManage", "ban_message", "&4{player} è stato bannato dal server")
                    .replace("{player}", targetPlayer.getName());
            String banReason = TranslationManager.translate("PlayerManage", "ban_reason", "&cSei stato bannato dal server!");
            String source = TranslationManager.translate("PlayerManage", "source", "Bannato da {player} tramite Admin Manager")
                    .replace("{player}", sender.getName());
            // Utilizza il sistema di ban ufficiale
            Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(targetPlayer.getName(),
                    org.bukkit.ChatColor.translateAlternateColorCodes('&', banReason),
                    null, // Durata: null per ban permanente
                    source);
            // Espelle immediatamente il giocatore
            targetPlayer.kickPlayer(org.bukkit.ChatColor.translateAlternateColorCodes('&', banReason));
            // Invia messaggio di conferma al mittente
            sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', banMessage));
            // Registra il ban nel log
            PlayerLogger.logBan(sender.getName(), targetPlayer.getName());
        } else {
            String failureMessage = TranslationManager.translate("PlayerManage", "ban_failure", "&c{player} non è online!")
                    .replace("{player}", targetPlayer.getName());
            sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', failureMessage));
        }
    }
    private void handleExitClick(InventoryClickEvent event) {
        new PlayerListGui((Player) event.getWhoClicked(), 1).open();
    }
}
