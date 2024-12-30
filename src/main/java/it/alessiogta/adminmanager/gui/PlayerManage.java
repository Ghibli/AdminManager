package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.MuteManager;
import it.alessiogta.adminmanager.utils.PlayerLogger;
import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PlayerManage extends BaseGui {

    private final Player targetPlayer;

    public PlayerManage(Player player, Player targetPlayer) {
        super(player, formatTitle(targetPlayer), 1); // GUI con una sola pagina
        this.targetPlayer = targetPlayer;

        // Configura l'icona di teletrasporto
        String teleportTitle = TranslationManager.translate("PlayerManage", "teleport_title", "&aTeletrasporto");
        String teleportLore = TranslationManager.translate("PlayerManage", "teleport_lore", "&7Teletrasportati da {player}").replace("{player}", targetPlayer.getName());
        setItem(10, createItem(Material.ENDER_PEARL, teleportTitle, teleportLore));

        // Configura l'icona di teletrasporto del giocatore da te
        String tpToMeTitle = TranslationManager.translate("PlayerManage", "tp_to_me_title", "&bTP da me");
        String tpToMeLore = TranslationManager.translate("PlayerManage", "tp_to_me_lore", "&7Teletrasporta {player} da te").replace("{player}", targetPlayer.getName());
        setItem(11, createItem(Material.COMPASS, tpToMeTitle, tpToMeLore));

        // Configura l'icona di espulsione
        String kickTitle = TranslationManager.translate("PlayerManage", "kick_title", "&cEspelli dal server");
        String kickLore = TranslationManager.translate("PlayerManage", "kick_lore", "&7Espelli dal server {player}")
                .replace("{player}", targetPlayer.getName());
        setItem(13, createItem(Material.IRON_DOOR, kickTitle, kickLore));

        // Configura l'icona di ban
        String banTitle = TranslationManager.translate("PlayerManage", "ban_title", "&4Ban dal server");
        String banLore = TranslationManager.translate("PlayerManage", "ban_lore", "&7Banna {player} dal server").replace("{player}", targetPlayer.getName());
        setItem(14, createItem(Material.RED_BANNER, banTitle, banLore));

        // Configura l'icona di uscita
        String title = TranslationManager.translate("PlayerManage", "back_button_title", "&cIndietro");
        String lore = TranslationManager.translate("PlayerManage", "back_button_lore", "&aRitorna alla lista giocatori");

        //Icona mute generata dal mute manager SLOT 15
        updateMuteIcon();

        setItem(49, createItem(Material.DARK_OAK_DOOR, title, lore));
    }

    private static String formatTitle(Player targetPlayer) {
        return String.format("§a[§6%s§a] §7+ details", targetPlayer.getName()); // Hard-coded titolo
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();

        if (slot == 10) { // Slot dell'icona di teletrasporto
            event.getWhoClicked().teleport(targetPlayer.getLocation());
            String teleportMessage = TranslationManager.translate("PlayerManage", "teleport_message", "&aTi sei teletrasportato da &e{player}").replace("{player}", targetPlayer.getName());
            event.getWhoClicked().sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', teleportMessage));
            return;
        }

        if (slot == 11) { // Slot dell'icona di teletrasporto del giocatore da te
            Player sender = (Player) event.getWhoClicked();
            if (targetPlayer.isOnline()) {
                targetPlayer.teleport(sender.getLocation());
                String successMessage = TranslationManager.translate("PlayerManage", "tp_to_me_success", "&a{player} è stato teletrasportato da te").replace("{player}", targetPlayer.getName());
                sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', successMessage));
            } else {
                String failureMessage = TranslationManager.translate("PlayerManage", "tp_to_me_failure", "&c{player} non è online!").replace("{player}", targetPlayer.getName());
                sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', failureMessage));
            }
            return;
        }

        if (slot == 13) { // Slot dell'icona di espulsione
            Player sender = (Player) event.getWhoClicked();
            if (targetPlayer.isOnline()) {
                String kickMessage = TranslationManager.translate("PlayerManage", "kick_message", "&c{player} è stato espulso dal server").replace("{player}", targetPlayer.getName());
                String kickReason = TranslationManager.translate("PlayerManage", "kick_reason", "&cSei stato espulso dal server!");
                targetPlayer.kickPlayer(org.bukkit.ChatColor.translateAlternateColorCodes('&', kickReason));
                sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', kickMessage));
                PlayerLogger.logKick(sender.getName(), targetPlayer.getName());
            } else {
                String failureMessage = TranslationManager.translate("PlayerManage", "kick_failure", "&c{player} non è online!").replace("{player}", targetPlayer.getName());
                sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', failureMessage));
            }
            return;
        }

        if (slot == 14) { // Slot dell'icona di ban
            Player sender = (Player) event.getWhoClicked();
            if (targetPlayer.isOnline()) {
                String banMessage = TranslationManager.translate("PlayerManage", "ban_message", "&4{player} è stato bannato dal server").replace("{player}", targetPlayer.getName());
                String banReason = TranslationManager.translate("PlayerManage", "ban_reason", "&cSei stato bannato dal server!");
                String source = TranslationManager.translate("PlayerManage", "source", "Bannato da {player} tramite Admin Manager").replace("{player}", sender.getName());
                // Utilizza il sistema di ban ufficiale
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(targetPlayer.getName(),
                        org.bukkit.ChatColor.translateAlternateColorCodes('&', banReason),
                        null, // Durata: null per ban permanente
                        source
                );
                // Espelle immediatamente il giocatore
                targetPlayer.kickPlayer(org.bukkit.ChatColor.translateAlternateColorCodes('&', banReason));
                // Invia messaggio di conferma al mittente
                sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', banMessage));
                // Registra il ban nel log
                PlayerLogger.logBan(sender.getName(), targetPlayer.getName());
            } else {
                String failureMessage = TranslationManager.translate("PlayerManage", "ban_failure", "&c{player} non è online!").replace("{player}", targetPlayer.getName());
                sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', failureMessage));
            }
            return;
        }

        if (slot == 15) { // Slot mute
            boolean isMuted = MuteManager.isMuted(targetPlayer.getUniqueId());

            if (isMuted) {
                // Smuta il giocatore
                MuteManager.unmutePlayer(targetPlayer.getUniqueId());
                event.getWhoClicked().sendMessage(
                        TranslationManager.translate("PlayerManageGui", "unmute_message", "&a{player} non è più mutato").replace("{player}", targetPlayer.getName()));
            } else {
                // Muta il giocatore
                MuteManager.mutePlayer(targetPlayer.getUniqueId());
                event.getWhoClicked().sendMessage(
                        TranslationManager.translate("PlayerManageGui", "mute_message", "&c{player} è stato mutato").replace("{player}", targetPlayer.getName()));
            }
            // Aggiorna la GUI e cambia l'icona
            updateMuteIcon();
            open(); // Riapre la GUI aggiornata
            return;
        }

        if (slot == 49) { // Slot dell'icona di uscita
            new PlayerListGui((Player) event.getWhoClicked(), 1).open();
            return;
        }
        event.setCancelled(true); // Impedisce lo spostamento delle icone

    }

    private void updateMuteIcon() {
        boolean isMuted = MuteManager.isMuted(targetPlayer.getUniqueId());

        String title = isMuted
                ? TranslationManager.translate("PlayerManageGui", "mute_unmute_title", "&cRimuovi mute")
                : TranslationManager.translate("PlayerManageGui", "mute_title", "&aMuta giocatore");

        String lore = isMuted
                ? TranslationManager.translate("PlayerManageGui", "mute_unmute_lore", "&7Rimuovi il mute da {player}")
                .replace("{player}", targetPlayer.getName())
                : TranslationManager.translate("PlayerManageGui", "mute_lore", "&7Muta {player}")
                .replace("{player}", targetPlayer.getName());

        Material icon = isMuted ? Material.REDSTONE_TORCH : Material.BELL;
        setItem(15, createItem(icon, title, lore));
    }

}