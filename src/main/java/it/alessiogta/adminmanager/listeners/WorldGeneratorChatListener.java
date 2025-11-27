package it.alessiogta.adminmanager.listeners;

import it.alessiogta.adminmanager.gui.WorldGeneratorGui;
import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class WorldGeneratorChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Check if player is awaiting input
        if (!WorldGeneratorGui.awaitingInput.containsKey(playerUUID)) {
            return;
        }

        // Cancel the event so the message doesn't appear in public chat
        event.setCancelled(true);

        WorldGeneratorGui.InputData inputData = WorldGeneratorGui.awaitingInput.get(playerUUID);
        String input = event.getMessage().trim();

        // Check for cancel command
        if (input.equalsIgnoreCase("annulla") || input.equalsIgnoreCase("cancel")) {
            WorldGeneratorGui.awaitingInput.remove(playerUUID);

            String message = TranslationManager.translate("WorldGenerator", "input_cancelled",
                "&cInput annullato.");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

            // Reopen GUI on main thread
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> inputData.gui.open()
            );
            return;
        }

        // Process input based on type
        if (inputData.type == WorldGeneratorGui.InputType.WORLD_NAME) {
            handleWorldNameInput(player, input, inputData.gui);
        } else if (inputData.type == WorldGeneratorGui.InputType.SEED) {
            handleSeedInput(player, input, inputData.gui);
        }

        // Remove from awaiting map
        WorldGeneratorGui.awaitingInput.remove(playerUUID);
    }

    private void handleWorldNameInput(Player player, String input, WorldGeneratorGui gui) {
        // Validate world name (no spaces, alphanumeric + underscore)
        if (!input.matches("[a-zA-Z0-9_]+")) {
            String error = TranslationManager.translate("WorldGenerator", "invalid_name",
                "&c&lERRORE: &eIl nome può contenere solo lettere, numeri e underscore!");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', error));

            // Reopen GUI
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> gui.open()
            );
            return;
        }

        // Check if world already exists
        if (Bukkit.getWorld(input) != null) {
            String error = TranslationManager.translate("WorldGenerator", "world_exists",
                "&c&lERRORE: &eEsiste già un mondo con questo nome!");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', error));

            // Reopen GUI
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> gui.open()
            );
            return;
        }

        // Set the world name
        gui.setWorldName(input);

        String success = TranslationManager.translate("WorldGenerator", "name_set",
            "&aNome mondo impostato: &e{name}")
            .replace("{name}", input);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', success));

        // Reopen GUI on main thread
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> gui.open()
        );
    }

    private void handleSeedInput(Player player, String input, WorldGeneratorGui gui) {
        // Seed can be any string
        gui.setSeed(input);

        String success = TranslationManager.translate("WorldGenerator", "seed_set",
            "&aSeed impostato: &e{seed}")
            .replace("{seed}", input);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', success));

        // Reopen GUI on main thread
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> gui.open()
        );
    }
}
