package it.alessiogta.adminmanager.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class TranslationManager {

    private static final Map<String, FileConfiguration> translations = new HashMap<>();
    private static String currentLanguage;

    public static void loadTranslations(String language) {
        currentLanguage = language;

        // Directory principale delle traduzioni
        File localeFolder = new File("plugins/AdminManager/locale");
        if (!localeFolder.exists() && !localeFolder.mkdirs()) {
            Bukkit.getLogger().warning("[AdminManager] Impossibile creare la directory: " + localeFolder.getPath());
            return;
        }

        // Lingue supportate
        String[] supportedLanguages = {"it_IT", "en_EN"}; // Aggiungi altre lingue se necessario

        for (String lang : supportedLanguages) {
            File languageFolder = new File(localeFolder, lang);

            // Crea la sottocartella della lingua
            if (!languageFolder.exists() && !languageFolder.mkdirs()) {
                Bukkit.getLogger().warning("[AdminManager] Impossibile creare la directory: " + languageFolder.getPath());
                continue;
            }

            // Copia i file di traduzione predefiniti per ogni lingua
            String[] guiFiles = {
                "PlayerListGui.yml",
                "PlayerManage.yml",
                "ServerManager.yml",
                "SpeedControl.yml",
                "EconomyManager.yml",
                "ArmorCreator.yml",
                "CommandRegistration.yml",
                "ConfigManager.yml",
                "WhitelistEditor.yml",
                "GameRules.yml"
            };

            for (String guiFile : guiFiles) {
                File file = new File(languageFolder, guiFile);
                if (!file.exists()) {
                    copyDefaultTranslationFile(lang, guiFile);
                }

                // Carica il file solo per la lingua attualmente selezionata
                if (lang.equals(language)) {
                    translations.put(guiFile.replace(".yml", ""), YamlConfiguration.loadConfiguration(file));
                }
            }
        }
    }

    private static void copyDefaultTranslationFile(String language, String guiFile) {
        try {
            InputStream defaultFile = TranslationManager.class.getResourceAsStream("/locale/" + language + "/" + guiFile);
            if (defaultFile != null) {
                File targetFile = new File("plugins/AdminManager/locale/" + language, guiFile);
                Files.copy(defaultFile, targetFile.toPath());
                Bukkit.getLogger().info("[AdminManager] Copiato file di traduzione predefinito: " + targetFile.getPath());
            } else {
                Bukkit.getLogger().warning("[AdminManager] File di traduzione predefinito non trovato: " + guiFile);
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("[AdminManager] Errore durante la copia del file di traduzione: " + e.getMessage());
        }
    }


    public static String translate(String guiName, String key, String defaultValue) {
        FileConfiguration config = translations.get(guiName);
        String value = defaultValue;

        if (config != null) {
            value = config.getString(key, defaultValue);
        }

        return org.bukkit.ChatColor.translateAlternateColorCodes('&', value);
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Ricarica le traduzioni per la lingua specificata
     * Svuota la cache e ricarica tutti i file di traduzione
     */
    public static void reloadTranslations(String newLanguage) {
        translations.clear();
        loadTranslations(newLanguage);
        Bukkit.getLogger().info("[AdminManager] Traduzioni ricaricate per la lingua: " + newLanguage);
    }
}