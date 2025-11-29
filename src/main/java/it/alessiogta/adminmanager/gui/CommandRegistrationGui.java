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
    private final Map<String, Command> serverCommands;
    private final List<String> allCommandNames;

    public CommandRegistrationGui(Player admin, int page) {
        super(admin, TranslationManager.translate("CommandRegistration", "title", "&aCommand Registration"), page);
        this.admin = admin;
        this.serverCommands = getServerCommands();
        this.allCommandNames = new ArrayList<>(serverCommands.keySet());
        this.allCommandNames.sort(String::compareToIgnoreCase);
        setupGuiItems();
    }

    // Convenience constructor for first page
    public CommandRegistrationGui(Player admin) {
        this(admin, 1);
    }

    @Override
    protected void setupNavigationButtons() {
        // Skip if not initialized yet (called from BaseGui constructor)
        if (allCommandNames == null) {
            return;
        }

        int itemsPerPage = 45;
        int totalCommands = allCommandNames.size();

        // Previous page (slot 47)
        if (getPage() > 1) {
            String prevTitle = TranslationManager.translate("CommandRegistration", "previous_page", "&ePagina precedente");
            setItem(47, createItem(Material.ARROW, prevTitle));
        }

        // Info button (slot 49)
        int maxPage = (int) Math.ceil(totalCommands / (double) itemsPerPage);
        String infoTitle = TranslationManager.translate("CommandRegistration", "info_title", "&6Info");
        String infoLore = TranslationManager.translate("CommandRegistration", "info_lore",
            "&7Totale comandi: &e{count}\n&7Pagina: &e{page}&7/&e{maxPage}")
            .replace("{count}", String.valueOf(totalCommands))
            .replace("{page}", String.valueOf(getPage()))
            .replace("{maxPage}", String.valueOf(maxPage));
        setItem(49, createItem(Material.BOOK, infoTitle, infoLore.split("\n")));

        // Next page (slot 51)
        if (totalCommands > getPage() * itemsPerPage) {
            String nextTitle = TranslationManager.translate("CommandRegistration", "next_page", "&ePagina successiva");
            setItem(51, createItem(Material.ARROW, nextTitle));
        }

        // Back button (slot 45)
        setItem(45, createBackButton());
    }

    private void setupGuiItems() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");

        // Initialize commands config if not exists
        for (String command : allCommandNames) {
            if (!plugin.getConfig().contains("commands." + command + ".enabled")) {
                plugin.getConfig().set("commands." + command + ".enabled", true);
            }
        }
        plugin.saveConfig();

        // Calculate pagination
        int itemsPerPage = 45;
        int startIndex = (getPage() - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, allCommandNames.size());

        // Setup command buttons for current page
        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            String command = allCommandNames.get(i);
            slotToCommand.put(slot, command);
            setItem(slot, createCommandButton(command, serverCommands.get(command)));
            slot++;
        }

        // Setup navigation buttons
        setupNavigationButtons();
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

        // Navigation buttons
        if (slot == 45) {
            // Back button
            handleBack(clicker);
            return;
        } else if (slot == 47 && getPage() > 1) {
            // Previous page - deregister listener to prevent multiple listeners
            org.bukkit.event.HandlerList.unregisterAll(this);
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new CommandRegistrationGui(clicker, getPage() - 1).open()
            );
            return;
        } else if (slot == 49) {
            // Info button - do nothing
            return;
        } else if (slot == 51) {
            // Next page
            int itemsPerPage = 45;
            if (allCommandNames.size() > getPage() * itemsPerPage) {
                // Deregister listener to prevent multiple listeners
                org.bukkit.event.HandlerList.unregisterAll(this);
                Bukkit.getScheduler().runTask(
                    Bukkit.getPluginManager().getPlugin("AdminManager"),
                    () -> new CommandRegistrationGui(clicker, getPage() + 1).open()
                );
            }
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

        // Refresh button with updated status
        Command commandObj = serverCommands.get(command);
        refreshSlot(slot, createCommandButton(command, commandObj));
    }

    private void handleBack(Player clicker) {
        // Deregister listener before returning to ServerManagerGui
        org.bukkit.event.HandlerList.unregisterAll(this);
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
