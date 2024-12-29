package it.alessiogta.adminmanager.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;

public class TranslationManager {

    private static FileConfiguration translations;

    public static void loadTranslations(String locale) {
        File translationFile = new File("plugins/AdminManager/locale", locale + ".yml");

        // Se il file non esiste, copia il predefinito dalle risorse
        if (!translationFile.exists()) {
            try {
                translationFile.getParentFile().mkdirs(); // Crea le directory necessarie
                InputStream defaultStream = TranslationManager.class.getResourceAsStream("/locale/" + locale + ".yml");
                if (defaultStream != null) {
                    Files.copy(defaultStream, translationFile.toPath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Carica il file di traduzione
        translations = YamlConfiguration.loadConfiguration(translationFile);
    }

    public static String translate(String key) {
        String value = translations.getString("message." + key, "&c" + key);
        return ChatColor.translateAlternateColorCodes('&', value);
    }
}