package it.alessiogta.adminmanager.listeners;

import it.alessiogta.adminmanager.gui.ToolCustomizationGui;
import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener for handling chat input in Tool Creator GUI
 */
public class ToolCreatorChatListener implements Listener {

    private static final Map<UUID, InputData> pendingInputs = new HashMap<>();

    public static class InputData {
        public ToolCustomizationGui gui;
        public InputType type;

        public InputData(ToolCustomizationGui gui, InputType type) {
            this.gui = gui;
            this.type = type;
        }
    }

    public enum InputType {
        NAME,    // Setting tool name
        LORE     // Adding lore line
    }

    /**
     * Register a player for chat input
     */
    public static void registerInput(Player player, ToolCustomizationGui gui, InputType type) {
        pendingInputs.put(player.getUniqueId(), new InputData(gui, type));
    }

    /**
     * Cancel pending input for a player
     */
    public static void cancelInput(Player player) {
        pendingInputs.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!pendingInputs.containsKey(playerId)) {
            return;
        }

        // Cancel the event to prevent chat broadcast
        event.setCancelled(true);

        InputData data = pendingInputs.get(playerId);
        String input = event.getMessage().trim();

        // Check for cancel
        if (input.equalsIgnoreCase("cancel")) {
            pendingInputs.remove(playerId);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                TranslationManager.translate("ToolCreator", "input_cancelled", "&cInput annullato.")));

            // Reopen GUI
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> data.gui.reopen()
            );
            return;
        }

        // Process input based on type
        if (data.type == InputType.NAME) {
            handleNameInput(player, data.gui, input);
        } else if (data.type == InputType.LORE) {
            handleLoreInput(player, data.gui, input);
        }

        // Remove from pending
        pendingInputs.remove(playerId);
    }

    private void handleNameInput(Player player, ToolCustomizationGui gui, String input) {
        // Run on main thread
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> {
                gui.setCustomName(input);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    TranslationManager.translate("ToolCreator", "name_set",
                        "&aNome impostato: {name}")
                        .replace("{name}", input)));
                gui.reopen();
            }
        );
    }

    private void handleLoreInput(Player player, ToolCustomizationGui gui, String input) {
        // Run on main thread
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> {
                gui.addLoreLine(input);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    TranslationManager.translate("ToolCreator", "lore_added",
                        "&aRiga aggiunta alla lore")));
                gui.reopen();
            }
        );
    }
}
