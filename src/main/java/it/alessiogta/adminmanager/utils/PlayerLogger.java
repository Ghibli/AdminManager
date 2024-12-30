package it.alessiogta.adminmanager.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class PlayerLogger {

    private static final String LOG_FOLDER = "plugins/AdminManager/LOG";
    private static final String BAN_LOG_FILE = "player_ban.log";
    private static final String KICK_LOG_FILE = "player_kick.log";
    private static DateTimeFormatter formatter;
    private static ZoneId timeZone;
    private static String kickLogMessage;
    private static String banLogMessage;

    public static void initialize(FileConfiguration config) {
        // Configura il fuso orario
        String timeZoneConfig = config.getString("log_timezone", "UTC");
        try {
            timeZone = ZoneId.of(timeZoneConfig);
        } catch (Exception e) {
            Bukkit.getLogger().warning("[AdminManager] Fuso orario non valido: " + timeZoneConfig + ". Defaulting to UTC.");
            timeZone = ZoneId.of("UTC");
        }

        // Configura il formato della data
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(timeZone);

        // Legge i messaggi di log dal config
        kickLogMessage = config.getString("log_messages.kick", "[%s] [%s] ha espulso %s dal server");
        banLogMessage = config.getString("log_messages.ban", "[%s] [%s] ha bannato %s dal server");

        // Crea la cartella e i file di log se non esistono
        createLogFile(LOG_FOLDER, BAN_LOG_FILE);
        createLogFile(LOG_FOLDER, KICK_LOG_FILE);
    }

    private static void createLogFile(String folderPath, String fileName) {
        File folder = new File(folderPath);
        if (!folder.exists() && !folder.mkdirs()) {
            Bukkit.getLogger().severe("[AdminManager] Impossibile creare la directory dei log: " + folderPath);
        }

        File file = new File(folderPath, fileName);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Bukkit.getLogger().info("[AdminManager] Creato il file di log: " + fileName);
                }
            } catch (IOException e) {
                Bukkit.getLogger().severe("[AdminManager] Errore durante la creazione del file di log: " + fileName + ". " + e.getMessage());
            }
        }
    }

    public static void logKick(String executor, String target) {
        logToFile(KICK_LOG_FILE, String.format(kickLogMessage, getCurrentTimestamp(), executor, target));
    }

    public static void logBan(String executor, String target) {
        logToFile(BAN_LOG_FILE, String.format(banLogMessage, getCurrentTimestamp(), executor, target));
    }

    private static String getCurrentTimestamp() {
        return LocalDateTime.now(timeZone).format(formatter);
    }

    private static void logToFile(String fileName, String message) {
        File logFile = new File(LOG_FOLDER, fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            Bukkit.getLogger().severe("[AdminManager] Errore durante la scrittura nel file di log: " + fileName + ". " + e.getMessage());
        }
    }
}