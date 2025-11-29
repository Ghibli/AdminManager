package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.*;

public class CommandRegistrationGui extends BaseGui {

    private final Player admin;
    private final Map<Integer, String> slotToCommand = new HashMap<>();

    public CommandRegistrationGui(Player admin) {
        super(admin, TranslationManager.translate("CommandRegistration", "title", "&aCommand Registration"), 1);
        this.admin = admin;
        setupGuiItems();
    }

    @Override
    protected void setupNavigationButtons() {
        // Disable BaseGui's automatic navigation buttons
    }

    private void setupGuiItems() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");

        // Get all registered commands from the server
        Map<String, Command> commands = getServerCommands();

        // Filter and sort commands
        List<String> commandNames = new ArrayList<>(commands.keySet());
        commandNames.sort(String::compareToIgnoreCase);

        // Initialize commands config if not exists
        for (String command : commandNames) {
            if (!plugin.getConfig().contains("commands." + command + ".enabled")) {
                plugin.getConfig().set("commands." + command + ".enabled", true);
            }
        }
        plugin.saveConfig();

        // Setup command buttons (max 45 slots)
        int slot = 0;
        for (String command : commandNames) {
            if (slot >= 45) break;

            slotToCommand.put(slot, command);
            setItem(slot, createCommandButton(command, commands.get(command)));
            slot++;
        }

        // Back button at slot 49
        setItem(49, createBackButton());
    }

    /**
     * Get all registered commands from the server using reflection
     */
    private Map<String, Command> getServerCommands() {
        Map<String, Command> commands = new HashMap<>();

        try {
            // Use reflection to access CommandMap
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getServer());

            // Get all registered commands
            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);

            // Filter out aliases and minecraft internal commands
            Set<Command> uniqueCommands = new HashSet<>(knownCommands.values());
            for (Command cmd : uniqueCommands) {
                String name = cmd.getName().toLowerCase();
                // Skip minecraft internal commands and duplicates
                if (!name.startsWith("minecraft:") && !commands.containsKey(name)) {
                    commands.put(name, cmd);
                }
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("[AdminManager] Could not scan server commands: " + e.getMessage());
            // Fallback: add at least the adminm command
            commands.put("adminm", Bukkit.getPluginCommand("adminm"));
        }

        return commands;
    }

    private ItemStack createCommandButton(String command, Command commandObj) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");
        boolean enabled = plugin.getConfig().getBoolean("commands." + command + ".enabled", true);

        String status = enabled ? "&a→ Enabled" : "&c→ Disabled";

        // Get command description if available
        String description = "";
        if (commandObj != null && commandObj.getDescription() != null && !commandObj.getDescription().isEmpty()) {
            description = "\n&8" + commandObj.getDescription();
        }

        String title = TranslationManager.translate("CommandRegistration", "command_" + command + "_title",
            "&f/" + command);
        String lore = TranslationManager.translate("CommandRegistration", "command_" + command + "_lore",
            "{status}" + description + "\n\n&e&lLEFT: &7Toggle")
            .replace("{status}", status);

        // Choose icon based on command name
        Material icon = getCommandIcon(command);
        return createItem(icon, title, lore.split("\n"));
    }

    /**
     * Get appropriate icon for a command based on its name
     */
    private Material getCommandIcon(String command) {
        String cmd = command.toLowerCase();

        // Common command icons
        if (cmd.contains("ban") && !cmd.contains("unban")) return Material.BARRIER;
        if (cmd.contains("unban")) return Material.IRON_BARS;
        if (cmd.contains("kick")) return Material.TNT;
        if (cmd.contains("player") || cmd.contains("pl")) return Material.PLAYER_HEAD;
        if (cmd.contains("god") || cmd.contains("invincible")) return Material.GOLDEN_APPLE;
        if (cmd.contains("gamemode") || cmd.contains("gm")) return Material.COMMAND_BLOCK;
        if (cmd.contains("freeze")) return Material.ICE;
        if (cmd.contains("fly")) return Material.ELYTRA;
        if (cmd.contains("heal")) return Material.POTION;
        if (cmd.contains("tp") || cmd.contains("teleport")) return Material.ENDER_PEARL;
        if (cmd.contains("eco") || cmd.contains("money") || cmd.contains("pay")) return Material.EMERALD;
        if (cmd.contains("vanish") || cmd.contains("v")) return Material.GLASS;
        if (cmd.contains("world")) return Material.GRASS_BLOCK;
        if (cmd.contains("time")) return Material.CLOCK;
        if (cmd.contains("weather")) return Material.SNOWBALL;
        if (cmd.contains("give")) return Material.CHEST;
        if (cmd.contains("kill")) return Material.SKELETON_SKULL;
        if (cmd.contains("admin") || cmd.contains("manage")) return Material.COMMAND_BLOCK;
        if (cmd.contains("help") || cmd.contains("?")) return Material.BOOK;
        if (cmd.contains("list")) return Material.WRITABLE_BOOK;
        if (cmd.contains("msg") || cmd.contains("tell") || cmd.contains("whisper")) return Material.PAPER;
        if (cmd.contains("warp")) return Material.COMPASS;
        if (cmd.contains("home")) return Material.RED_BED;
        if (cmd.contains("spawn")) return Material.RESPAWN_ANCHOR;
        if (cmd.contains("back")) return Material.ARROW;

        // Default icon
        return Material.COMMAND_BLOCK;
    }

    private ItemStack createBackButton() {
        String title = TranslationManager.translate("CommandRegistration", "back_button_title", "&cIndietro");
        String lore = TranslationManager.translate("CommandRegistration", "back_button_lore", "&7Torna a Server Manager");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        if (slot == 49) {
            // Back button
            handleBack(clicker);
            return;
        }

        // Check if clicked slot has a command
        if (slotToCommand.containsKey(slot)) {
            handleCommandToggle(slot, clicker);
        }
    }

    private void handleCommandToggle(int slot, Player clicker) {
        String command = slotToCommand.get(slot);
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");

        // Toggle command status
        boolean currentStatus = plugin.getConfig().getBoolean("commands." + command + ".enabled", true);
        plugin.getConfig().set("commands." + command + ".enabled", !currentStatus);
        plugin.saveConfig();

        String status = !currentStatus ? "&aabilitato" : "&cdisabilitato";
        String message = TranslationManager.translate("CommandRegistration", "command_toggled",
            "&fComando &e/" + command + " " + status);
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        // Refresh button
        Material material = COMMAND_MATERIALS.get(command);
        refreshSlot(slot, createCommandButton(command, material));
    }

    private void handleBack(Player clicker) {
        // Don't close inventory - open ServerManagerGui directly
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new ServerManagerGui(clicker).open()
        );
    }

    private void refreshSlot(int slot, ItemStack item) {
        setItem(slot, item);
        if (inventory != null) {
            inventory.setItem(slot, item);
        }
    }

    @Override
    public void open() {
        inventory = build();
        admin.openInventory(inventory);
    }
}
