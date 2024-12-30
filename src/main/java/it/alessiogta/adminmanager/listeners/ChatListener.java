package it.alessiogta.adminmanager.listeners;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Ricarica lo stato dal file per garantire la sincronizzazione
        boolean isMuted = MuteManager.isMuted(player.getUniqueId());

        // Log dello stato per debug
        Bukkit.getLogger().info("[AdminManager] Stato di mute rilevato per " + player.getName() + ": " + isMuted);

        if (isMuted) {
            // Blocca il messaggio
            event.setCancelled(true);

            // Messaggio al giocatore mutato
            String muteChatMessage = TranslationManager.translate(
                    "PlayerManageGui",
                    "mute_chat_message",
                    "&cSei mutato e non puoi parlare!"
            );
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', muteChatMessage));

            // Notifica allo staff
            String muteNotifyMessage = TranslationManager.translate(
                    "PlayerManageGui",
                    "mute_notify_message",
                    "&e{player} ha tentato di parlare ma è mutato!"
            ).replace("{player}", player.getName());

            for (Player staff : player.getServer().getOnlinePlayers()) {
                if (staff.hasPermission("adminmanager.notify.mute")) {
                    staff.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', muteNotifyMessage));
                }
            }
        }
    }
}
