package it.alessiogta.adminmanager.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MuteManager {

    private static final File MUTE_FILE = new File("plugins/AdminManager/LOG", "mute_players.yml");
    private static FileConfiguration muteConfig;
    private static final Set<UUID> mutedPlayers = new HashSet<>();
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Inizializza il MuteManager e carica i giocatori mutati dal file
     */
    public static void initialize() {
        if (!MUTE_FILE.getParentFile().exists()) {
            MUTE_FILE.getParentFile().mkdirs();
        }

        if (!MUTE_FILE.exists()) {
            try {
                MUTE_FILE.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        muteConfig = YamlConfiguration.loadConfiguration(MUTE_FILE);
        loadMutedPlayers();
    }

    /**
     * Carica i giocatori mutati dal file di configurazione
     */
    private static void loadMutedPlayers() {
        lock.writeLock().lock();
        try {
            mutedPlayers.clear();
            if (muteConfig.contains("muted-players")) {
                for (String uuidString : muteConfig.getStringList("muted-players")) {
                    try {
                        mutedPlayers.add(UUID.fromString(uuidString));
                    } catch (IllegalArgumentException e) {
                        System.err.println("[AdminManager] UUID non valido nel file mute: " + uuidString);
                    }
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Salva i giocatori mutati nel file di configurazione
     */
    private static void saveMutedPlayers() {
        lock.readLock().lock();
        try {
            Set<String> uuidStrings = new HashSet<>();
            for (UUID uuid : mutedPlayers) {
                uuidStrings.add(uuid.toString());
            }
            muteConfig.set("muted-players", uuidStrings.stream().toList());
            muteConfig.save(MUTE_FILE);
        } catch (IOException e) {
            System.err.println("[AdminManager] Errore durante il salvataggio del file mute:");
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Verifica se un giocatore è mutato
     *
     * @param playerUUID UUID del giocatore
     * @return true se il giocatore è mutato, false altrimenti
     */
    public static boolean isMuted(UUID playerUUID) {
        lock.readLock().lock();
        try {
            // Ricarica dal file per garantire sincronizzazione
            loadMutedPlayers();
            return mutedPlayers.contains(playerUUID);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Muta un giocatore
     *
     * @param playerUUID UUID del giocatore da mutare
     */
    public static void mutePlayer(UUID playerUUID) {
        lock.writeLock().lock();
        try {
            mutedPlayers.add(playerUUID);
            saveMutedPlayers();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Rimuove il mute da un giocatore
     *
     * @param playerUUID UUID del giocatore
     */
    public static void unmutePlayer(UUID playerUUID) {
        lock.writeLock().lock();
        try {
            mutedPlayers.remove(playerUUID);
            saveMutedPlayers();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Toggle dello stato di mute di un giocatore
     *
     * @param playerUUID UUID del giocatore
     * @return true se ora è mutato, false se è stato unmutato
     */
    public static boolean toggleMute(UUID playerUUID) {
        lock.writeLock().lock();
        try {
            if (mutedPlayers.contains(playerUUID)) {
                unmutePlayer(playerUUID);
                return false;
            } else {
                mutePlayer(playerUUID);
                return true;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Ricarica la configurazione dal file
     */
    public static void reload() {
        loadMutedPlayers();
    }
}
