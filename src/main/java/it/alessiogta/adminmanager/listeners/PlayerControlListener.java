package it.alessiogta.adminmanager.listeners;

import it.alessiogta.adminmanager.utils.FreezeManager;
import it.alessiogta.adminmanager.utils.GodModeManager;
import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener per controllare freeze e god mode dei giocatori
 */
public class PlayerControlListener implements Listener {

    /**
     * Blocca il movimento dei giocatori frozen
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Se il giocatore è frozen, blocca il movimento
        if (FreezeManager.isFrozen(player)) {
            // Controlla se si è effettivamente mosso (non solo rotazione testa)
            if (event.getFrom().getX() != event.getTo().getX() ||
                event.getFrom().getY() != event.getTo().getY() ||
                event.getFrom().getZ() != event.getTo().getZ()) {

                event.setCancelled(true);

                // Messaggio al giocatore
                String freezeMessage = TranslationManager.translate(
                    "PlayerManage",
                    "freeze_cannot_move",
                    "&cSei congelato e non puoi muoverti!"
                );
                player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', freezeMessage));
            }
        }
    }

    /**
     * Blocca i danni ai giocatori in god mode
     */
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            // Se il giocatore ha god mode, cancella il danno
            if (GodModeManager.hasGodMode(player)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Cleanup quando un giocatore si disconnette
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Rimuovi dalle liste
        FreezeManager.cleanup(player.getUniqueId());
        GodModeManager.cleanup(player.getUniqueId());
    }
}
