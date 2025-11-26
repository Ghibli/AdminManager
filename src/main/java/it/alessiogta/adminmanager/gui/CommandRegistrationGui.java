package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistrationGui extends BaseGui {

    private final Player admin;
    private final Map<Integer, String> slotToCommand = new HashMap<>();

    // Command configurations: command name -> default material
    private static final Map<String, Material> COMMAND_MATERIALS = new HashMap<String, Material>() {{
        put("banlist", Material.WRITABLE_BOOK);
        put("player", Material.PLAYER_HEAD);
        put("unban", Material.IRON_BARS);
        put("god", Material.GOLDEN_APPLE);
        put("kickall", Material.TNT);
        put("gamemode", Material.COMMAND_BLOCK);
        put("freeze", Material.ICE);
        put("fly", Material.ELYTRA);
        put("heal", Material.POTION);
        put("teleport", Material.ENDER_PEARL);
        put("economy", Material.EMERALD);
        put("vanish", Material.GLASS);
    }};

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

        // Initialize commands config if not exists
        if (!plugin.getConfig().contains("commands")) {
            for (String command : COMMAND_MATERIALS.keySet()) {
                plugin.getConfig().set("commands." + command + ".enabled", true);
            }
            plugin.saveConfig();
        }

        // Setup command buttons
        int slot = 0;
        for (Map.Entry<String, Material> entry : COMMAND_MATERIALS.entrySet()) {
            if (slot >= 45) break; // Max 45 slots for commands

            String command = entry.getKey();
            slotToCommand.put(slot, command);
            setItem(slot, createCommandButton(command, entry.getValue()));
            slot++;
        }

        // Back button at slot 49
        setItem(49, createBackButton());
    }

    private ItemStack createCommandButton(String command, Material material) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AdminManager");
        boolean enabled = plugin.getConfig().getBoolean("commands." + command + ".enabled", true);

        String status = enabled ? "&aEnabled" : "&cDisabled";
        String statusColor = enabled ? "&a" : "&c";

        String title = TranslationManager.translate("CommandRegistration", "command_" + command + "_title",
            "&f/" + command);
        String lore = TranslationManager.translate("CommandRegistration", "command_" + command + "_lore",
            statusColor + "→ " + status + "\n\n&e&lLEFT: &7Toggle");

        return createItem(material, title, lore.split("\n"));
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
