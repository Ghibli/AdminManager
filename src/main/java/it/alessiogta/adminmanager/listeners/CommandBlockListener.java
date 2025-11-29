package it.alessiogta.adminmanager.listeners;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

public class CommandBlockListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Parse command (remove leading /)
        if (!message.startsWith("/")) {
            return;
        }

        String fullCommand = message.substring(1);
        String[] parts = fullCommand.split(" ");
        String command = parts[0].toLowerCase();

        // Remove plugin prefix if present (e.g., "bukkit:time" -> "time")
        if (command.contains(":")) {
            command = command.split(":")[1];
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");
        if (plugin == null) {
            return;
        }

        // Check if command is disabled in config
        boolean enabled = plugin.getConfig().getBoolean("commands." + command + ".enabled", true);

        if (!enabled) {
            // Command is disabled - block it
            event.setCancelled(true);

            String errorMessage = TranslationManager.translate("CommandRegistration", "command_disabled",
                "&cQuesto comando è stato disabilitato dall'amministratore.")
                .replace("{command}", command);
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', errorMessage));
        }
    }
}
