package it.alessiogta.adminmanager.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * UpdateChecker - Checks for plugin updates using Modrinth API
 *
 * Project: AdminManager
 * Modrinth ID: D8ejFvdE
 * Modrinth URL: https://modrinth.com/plugin/simple-admin-manager/
 */
public class UpdateChecker {

    private static final String MODRINTH_API_URL = "https://api.modrinth.com/v2/project/D8ejFvdE/version";
    private static final String MODRINTH_PROJECT_URL = "https://modrinth.com/plugin/simple-admin-manager/";

    private final Plugin plugin;
    private final String currentVersion;

    private String latestVersion = null;
    private boolean isUpToDate = false;
    private boolean checkCompleted = false;

    public UpdateChecker(Plugin plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    /**
     * Check for updates asynchronously
     * @return CompletableFuture that completes when check is done
     */
    public CompletableFuture<Boolean> checkForUpdates() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(MODRINTH_API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "AdminManager/" + currentVersion);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();

                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                    );
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse JSON response using Gson (available in Spigot)
                    JsonParser parser = new JsonParser();
                    JsonArray versions = parser.parse(response.toString()).getAsJsonArray();

                    if (versions.size() > 0) {
                        // Get the latest version (first in the array)
                        JsonObject latestRelease = versions.get(0).getAsJsonObject();
                        latestVersion = latestRelease.get("version_number").getAsString();

                        // Compare versions
                        isUpToDate = currentVersion.equals(latestVersion);
                        checkCompleted = true;

                        return isUpToDate;
                    }
                }
            } catch (Exception e) {
                // Silent fail - network issues shouldn't crash the plugin
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
                checkCompleted = false;
            }

            return true; // Assume up to date if check fails
        });
    }

    /**
     * Get update status message for console (with colors)
     * @return Formatted message
     */
    public String getStatusMessage() {
        if (!checkCompleted) {
            return "§e⚠ Update Check Failed";
        }

        if (isUpToDate) {
            return "§a✓ Plugin is Up to Date";
        } else {
            return "§c✗ Update Available: v" + latestVersion;
        }
    }

    /**
     * Get detailed update message for console
     * @return Formatted message with link
     */
    public String getDetailedMessage() {
        if (!checkCompleted) {
            return "";
        }

        if (!isUpToDate) {
            return "§9Download: §b" + MODRINTH_PROJECT_URL;
        }

        return "";
    }

    /**
     * Get update status message for chat (with colors and link)
     * @return Formatted message with download link if update available
     */
    public String getChatMessage() {
        if (!checkCompleted) {
            return "§e[AdminManager] §7Update check failed. Please check manually.";
        }

        if (isUpToDate) {
            return "§a[AdminManager] Plugin is up to date! (v" + currentVersion + ")";
        } else {
            return "§e[AdminManager] §7New version available: §a" + latestVersion +
                   " §7(current: §c" + currentVersion + "§7)\n" +
                   "§bDownload: §9" + MODRINTH_PROJECT_URL;
        }
    }

    /**
     * Notify online operators about updates
     */
    public void notifyOps() {
        if (!checkCompleted || isUpToDate) {
            return; // Don't notify if check failed or up to date
        }

        String message = getChatMessage();

        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("adminmanager.use"))
                .forEach(player -> player.sendMessage(message));

            // Also log to console
            plugin.getLogger().info("New version available: " + latestVersion + " (current: " + currentVersion + ")");
            plugin.getLogger().info("Download: " + MODRINTH_PROJECT_URL);
        });
    }

    // Getters
    public boolean isUpToDate() {
        return isUpToDate;
    }

    public boolean isCheckCompleted() {
        return checkCompleted;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public static String getModrinthUrl() {
        return MODRINTH_PROJECT_URL;
    }
}
