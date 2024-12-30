package it.alessiogta.adminmanager.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MuteManager {

    private static final File MUTE_FILE = new File("plugins/AdminManager/LOG/mute_players.yml");
    private static FileConfiguration muteConfig;

    public static void initialize() {
        if (!MUTE_FILE.exists()) {
            try {
                if (MUTE_FILE.getParentFile().mkdirs() && MUTE_FILE.createNewFile()) {
                    Bukkit.getLogger().info("[AdminManager] Creato il file mute_players.yml");
                }
            } catch (IOException e) {
                Bukkit.getLogger().severe("[AdminManager] Impossibile creare il file mute_players.yml: " + e.getMessage());
            }
        }
        muteConfig = YamlConfiguration.loadConfiguration(MUTE_FILE);

        if (muteConfig == null) {
            Bukkit.getLogger().severe("[AdminManager] Impossibile caricare il file mute_players.yml!");
        }
    }

    public static boolean isMuted(UUID uuid) {
        if (muteConfig == null) {
            Bukkit.getLogger().warning("[AdminManager] muteConfig non è inizializzato!");
            return false;
        }

        // Ricarica il file per garantire la sincronizzazione
        muteConfig = YamlConfiguration.loadConfiguration(MUTE_FILE);

        boolean isMuted = muteConfig.getBoolean(uuid.toString(), false);
        Bukkit.getLogger().info("[AdminManager] Stato di mute per " + uuid + ": " + isMuted);
        return isMuted;
    }



    public static void mutePlayer(UUID uuid) {
        if (muteConfig == null) {
            Bukkit.getLogger().severe("[AdminManager] muteConfig non è inizializzato! Impossibile mutare il giocatore.");
            return;
        }

        // Imposta il valore a true senza condizioni
        muteConfig.set(uuid.toString(), true);
        saveMuteFile();

        // Log dello stato aggiornato
        Bukkit.getLogger().info("[AdminManager] Giocatore " + uuid + " è stato mutato correttamente.");
    }


    public static void unmutePlayer(UUID uuid) {
        if (muteConfig == null) {
            Bukkit.getLogger().severe("[AdminManager] muteConfig non è inizializzato! Impossibile smutare il giocatore.");
            return;
        }

        // Imposta il valore a false senza condizioni
        muteConfig.set(uuid.toString(), false);
        saveMuteFile();

        // Log dello stato aggiornato
        Bukkit.getLogger().info("[AdminManager] Giocatore " + uuid + " è stato smutato correttamente.");
    }

    private static void saveMuteFile() {
        try {
            muteConfig.save(MUTE_FILE);
            Bukkit.getLogger().info("[AdminManager] mute_players.yml salvato correttamente.");
        } catch (IOException e) {
            Bukkit.getLogger().severe("[AdminManager] Impossibile salvare il file mute_players.yml: " + e.getMessage());
        }
    }
}