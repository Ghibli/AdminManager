package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameRuleChatListener implements Listener {

    // Store pending inputs: PlayerUUID -> InputData
    private static final Map<UUID, InputData> pendingInputs = new HashMap<>();

    public static void registerInput(Player player, World world, GameRule<Integer> rule, String ruleName) {
        pendingInputs.put(player.getUniqueId(), new InputData(world, rule, ruleName));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Check if player has pending input
        if (!pendingInputs.containsKey(playerId)) {
            return;
        }

        // Cancel the chat event to prevent the message from being broadcasted
        event.setCancelled(true);

        InputData data = pendingInputs.remove(playerId);
        String input = event.getMessage().trim();

        // Check for cancel
        if (input.equalsIgnoreCase("cancel")) {
            String message = TranslationManager.translate("GameRuleValue", "input_cancelled",
                "&cInput annullato. Torno alla GUI Game Rules...");
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

            // Reopen GameRulesGui
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new GameRulesGui(player, data.world).open()
            );
            return;
        }

        // Try to parse as integer
        try {
            int value = Integer.parseInt(input);

            if (value < 0) {
                String message = TranslationManager.translate("GameRuleValue", "input_negative",
                    "&cErrore: Il valore deve essere >= 0. Riprova o scrivi &ecancel");
                player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
                // Re-register for another attempt
                pendingInputs.put(playerId, data);
                return;
            }

            // Apply the value
            data.world.setGameRule(data.rule, value);

            String message = TranslationManager.translate("GameRuleValue", "input_success",
                "&a&lSUCCESS: &eRegola &6{rule} &eimpostata a &6{value} &ein &6{world}")
                .replace("{rule}", data.ruleName)
                .replace("{value}", String.valueOf(value))
                .replace("{world}", data.world.getName());
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

            // Reopen GameRulesGui
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new GameRulesGui(player, data.world).open()
            );

        } catch (NumberFormatException e) {
            String message = TranslationManager.translate("GameRuleValue", "input_invalid",
                "&cErrore: Devi inserire un numero valido. Riprova o scrivi &ecancel");
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
            // Re-register for another attempt
            pendingInputs.put(playerId, data);
        }
    }

    // Clean up pending input for a player (e.g., when they log out)
    public static void cancelInput(UUID playerId) {
        pendingInputs.remove(playerId);
    }

    // Inner class to store input data
    private static class InputData {
        final World world;
        final GameRule<Integer> rule;
        final String ruleName;

        InputData(World world, GameRule<Integer> rule, String ruleName) {
            this.world = world;
            this.rule = rule;
            this.ruleName = ruleName;
        }
    }
}
