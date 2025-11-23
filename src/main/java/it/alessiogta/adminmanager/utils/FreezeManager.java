package it.alessiogta.adminmanager.utils;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Gestisce i giocatori congelati (frozen) che non possono muoversi
 */
public class FreezeManager {

    private static final Set<UUID> frozenPlayers = new HashSet<>();

    /**
     * Verifica se un giocatore è congelato
     */
    public static boolean isFrozen(UUID playerUUID) {
        return frozenPlayers.contains(playerUUID);
    }

    /**
     * Verifica se un giocatore è congelato
     */
    public static boolean isFrozen(Player player) {
        return isFrozen(player.getUniqueId());
    }

    /**
     * Congela un giocatore
     */
    public static void freezePlayer(UUID playerUUID) {
        frozenPlayers.add(playerUUID);
    }

    /**
     * Congela un giocatore
     */
    public static void freezePlayer(Player player) {
        freezePlayer(player.getUniqueId());
    }

    /**
     * Scongela un giocatore
     */
    public static void unfreezePlayer(UUID playerUUID) {
        frozenPlayers.remove(playerUUID);
    }

    /**
     * Scongela un giocatore
     */
    public static void unfreezePlayer(Player player) {
        unfreezePlayer(player.getUniqueId());
    }

    /**
     * Toggle freeze di un giocatore
     * @return true se ora è frozen, false se è stato unfrozen
     */
    public static boolean toggleFreeze(UUID playerUUID) {
        if (isFrozen(playerUUID)) {
            unfreezePlayer(playerUUID);
            return false;
        } else {
            freezePlayer(playerUUID);
            return true;
        }
    }

    /**
     * Toggle freeze di un giocatore
     * @return true se ora è frozen, false se è stato unfrozen
     */
    public static boolean toggleFreeze(Player player) {
        return toggleFreeze(player.getUniqueId());
    }

    /**
     * Rimuove un giocatore dalla lista quando si disconnette
     */
    public static void cleanup(UUID playerUUID) {
        frozenPlayers.remove(playerUUID);
    }
}
