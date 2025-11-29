package it.alessiogta.adminmanager.gui;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum CommandCategory {
    ADMIN("Admin", Material.REDSTONE_BLOCK, Arrays.asList(
        "kick", "ban", "banlist", "unban", "op", "deop", "pardon",
        "whitelist", "minecraft:kick", "minecraft:ban"
    )),

    PLAYER("Player", Material.PLAYER_HEAD, Arrays.asList(
        "gamemode", "give", "clear", "effect", "enchant", "xp", "experience",
        "heal", "feed", "fly", "god", "invsee", "kill", "suicide"
    )),

    TELEPORT("Teleport", Material.ENDER_PEARL, Arrays.asList(
        "tp", "teleport", "tphere", "tphere", "spawn", "home", "sethome",
        "warp", "setwarp", "back", "tpa", "tpaccept", "tpdeny"
    )),

    WORLD("World", Material.GRASS_BLOCK, Arrays.asList(
        "time", "weather", "gamerule", "seed", "setworldspawn",
        "difficulty", "worldborder", "spawnpoint"
    )),

    ECONOMY("Economy", Material.GOLD_INGOT, Arrays.asList(
        "eco", "economy", "pay", "balance", "bal", "money", "give"
    )),

    UTILITY("Utility", Material.WRITABLE_BOOK, Arrays.asList(
        "help", "list", "plugins", "pl", "reload", "rl", "stop",
        "save-all", "save-on", "save-off", "version", "ver", "about"
    )),

    OTHER("Altri", Material.CHEST, Arrays.asList());

    private final String displayName;
    private final Material icon;
    private final List<String> commands;

    CommandCategory(String displayName, Material icon, List<String> commands) {
        this.displayName = displayName;
        this.icon = icon;
        this.commands = commands;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public List<String> getCommands() {
        return commands;
    }

    /**
     * Categorize a command based on its name
     */
    public static CommandCategory categorize(String command) {
        String cmd = command.toLowerCase();

        // Remove plugin prefix if present
        if (cmd.contains(":")) {
            cmd = cmd.split(":")[1];
        }

        // Check each category
        for (CommandCategory category : values()) {
            if (category == OTHER) continue; // Skip OTHER for now

            // Exact match
            if (category.commands.contains(cmd)) {
                return category;
            }

            // Pattern matching for common command variations
            for (String pattern : category.commands) {
                if (cmd.startsWith(pattern) || cmd.contains(pattern)) {
                    return category;
                }
            }
        }

        // Default to OTHER
        return OTHER;
    }
}
