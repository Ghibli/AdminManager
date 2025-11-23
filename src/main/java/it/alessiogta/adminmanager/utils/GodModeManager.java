package it.alessiogta.adminmanager.utils;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Gestisce i giocatori in God Mode (invincibili)
 */
public class GodModeManager {

    private static final Set<UUID> godModePlayers = new HashSet<>();

    /**
     * Verifica se un giocatore ha god mode attivo
     */
    public static boolean hasGodMode(UUID playerUUID) {
        return godModePlayers.contains(playerUUID);
    }

    /**
     * Verifica se un giocatore ha god mode attivo
     */
    public static boolean hasGodMode(Player player) {
        return hasGodMode(player.getUniqueId());
    }

    /**
     * Abilita god mode per un giocatore
     */
    public static void enableGodMode(UUID playerUUID) {
        godModePlayers.add(playerUUID);
    }

    /**
     * Abilita god mode per un giocatore
     */
    public static void enableGodMode(Player player) {
        enableGodMode(player.getUniqueId());
    }

    /**
     * Disabilita god mode per un giocatore
     */
    public static void disableGodMode(UUID playerUUID) {
        godModePlayers.remove(playerUUID);
    }

    /**
     * Disabilita god mode per un giocatore
     */
    public static void disableGodMode(Player player) {
        disableGodMode(player.getUniqueId());
    }

    /**
     * Toggle god mode di un giocatore
     * @return true se ora ha god mode, false se è stato disabilitato
     */
    public static boolean toggleGodMode(UUID playerUUID) {
        if (hasGodMode(playerUUID)) {
            disableGodMode(playerUUID);
            return false;
        } else {
            enableGodMode(playerUUID);
            return true;
        }
    }

    /**
     * Toggle god mode di un giocatore
     * @return true se ora ha god mode, false se è stato disabilitato
     */
    public static boolean toggleGodMode(Player player) {
        return toggleGodMode(player.getUniqueId());
    }

    /**
     * Rimuove un giocatore dalla lista quando si disconnette
     */
    public static void cleanup(UUID playerUUID) {
        godModePlayers.remove(playerUUID);
    }
}
