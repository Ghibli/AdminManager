package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldSelectorGui extends BaseGui {

    private final Player admin;

    // Static map to track pending teleports (admin UUID -> target world)
    public static final Map<UUID, World> pendingTeleports = new HashMap<>();

    public WorldSelectorGui(Player admin) {
        super(admin, TranslationManager.translate("WorldSelector", "title", "&6&lWorld Selector"), 1);
        this.admin = admin;
        setupGuiItems();
    }

    @Override
    protected void setupNavigationButtons() {
        // Back button at slot 49
        setItem(49, createBackButton());
    }

    private void setupGuiItems() {
        List<World> worlds = Bukkit.getWorlds();

        // Display worlds in multiple rows (21 slots total = 3 rows x 7 slots)
        // Row 2: slots 10-16
        // Row 3: slots 19-25
        // Row 4: slots 28-34
        int[] slots = {
            10, 11, 12, 13, 14, 15, 16,  // Row 2 (7 slots)
            19, 20, 21, 22, 23, 24, 25,  // Row 3 (7 slots)
            28, 29, 30, 31, 32, 33, 34   // Row 4 (7 slots)
        };

        int slotIndex = 0;

        for (World world : worlds) {
            if (slotIndex >= slots.length) break; // Max 21 worlds

            int slot = slots[slotIndex];
            setItem(slot, createWorldButton(world));
            slotIndex++;
        }

        // Create World button at slot 45
        setItem(45, createCreateWorldButton());

        setupNavigationButtons();
    }

    private ItemStack createWorldButton(World world) {
        String worldName = world.getName();
        Material material = getWorldMaterial(world);

        String title = TranslationManager.translate("WorldSelector", "world_title", "&e{world}")
            .replace("{world}", worldName);

        String lore = TranslationManager.translate("WorldSelector", "world_lore",
            "&7Environment: &f{env}\n&7Players: &f{players}\n&7Game Rules: &f{rules}\n\n&e&lLEFT: &7Game Rules\n&e&lRIGHT: &7Teleport here\n&e&lSHIFT+RIGHT: &7Teleport player")
            .replace("{env}", getEnvironmentName(world.getEnvironment()))
            .replace("{players}", String.valueOf(world.getPlayers().size()))
            .replace("{rules}", String.valueOf(getActiveRulesCount(world)));

        return createItem(material, title, lore.split("\n"));
    }

    private Material getWorldMaterial(World world) {
        switch (world.getEnvironment()) {
            case NETHER:
                return Material.NETHERRACK;
            case THE_END:
                return Material.END_STONE;
            case NORMAL:
            default:
                return Material.GRASS_BLOCK;
        }
    }

    private String getEnvironmentName(World.Environment env) {
        switch (env) {
            case NETHER:
                return TranslationManager.translate("WorldSelector", "env_nether", "Nether");
            case THE_END:
                return TranslationManager.translate("WorldSelector", "env_end", "End");
            case NORMAL:
            default:
                return TranslationManager.translate("WorldSelector", "env_normal", "Overworld");
        }
    }

    private int getActiveRulesCount(World world) {
        // Count how many boolean game rules are enabled
        int count = 0;
        try {
            if (Boolean.TRUE.equals(world.getGameRuleValue(org.bukkit.GameRule.DO_FIRE_TICK))) count++;
            if (Boolean.TRUE.equals(world.getGameRuleValue(org.bukkit.GameRule.MOB_GRIEFING))) count++;
            if (Boolean.TRUE.equals(world.getGameRuleValue(org.bukkit.GameRule.KEEP_INVENTORY))) count++;
            if (Boolean.TRUE.equals(world.getGameRuleValue(org.bukkit.GameRule.DO_MOB_SPAWNING))) count++;
            if (Boolean.TRUE.equals(world.getGameRuleValue(org.bukkit.GameRule.DO_MOB_LOOT))) count++;
            if (Boolean.TRUE.equals(world.getGameRuleValue(org.bukkit.GameRule.DO_TILE_DROPS))) count++;
            if (Boolean.TRUE.equals(world.getGameRuleValue(org.bukkit.GameRule.NATURAL_REGENERATION))) count++;
            if (Boolean.TRUE.equals(world.getGameRuleValue(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE))) count++;
        } catch (Exception ignored) {}
        return count;
    }

    private ItemStack createBackButton() {
        String title = TranslationManager.translate("WorldSelector", "back_button_title", "&cIndietro");
        String lore = TranslationManager.translate("WorldSelector", "back_button_lore", "&7Torna a Server Manager");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    private ItemStack createCreateWorldButton() {
        String title = TranslationManager.translate("WorldSelector", "create_world_title", "&a&lCrea Nuovo Mondo");
        String loreText = TranslationManager.translate("WorldSelector", "create_world_lore",
            "&7Configura e genera un nuovo mondo\n&7Overworld, Nether o End personalizzati\n\n&e&lClick: &7Apri World Generator");
        return createItem(Material.GRASS_BLOCK, title, loreText.split("\n"));
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

        if (slot == 45) {
            // Create World button
            handleCreateWorld(clicker);
            return;
        }

        // Check if clicked slot has a world
        List<World> worlds = Bukkit.getWorlds();
        int[] slots = {
            10, 11, 12, 13, 14, 15, 16,  // Row 2 (7 slots)
            19, 20, 21, 22, 23, 24, 25,  // Row 3 (7 slots)
            28, 29, 30, 31, 32, 33, 34   // Row 4 (7 slots)
        };

        for (int i = 0; i < slots.length && i < worlds.size(); i++) {
            if (slot == slots[i]) {
                handleWorldClick(worlds.get(i), clicker, event);
                return;
            }
        }
    }

    private void handleWorldClick(World world, Player clicker, InventoryClickEvent event) {
        // Left click = Game Rules
        if (event.isLeftClick()) {
            openGameRulesGui(world, clicker);
        }
        // Right click = Teleport yourself
        else if (event.isRightClick() && !event.isShiftClick()) {
            teleportToWorld(world, clicker, clicker);
        }
        // Shift + Right click = Select player to teleport
        else if (event.isRightClick() && event.isShiftClick()) {
            openPlayerSelectorForTeleport(world, clicker);
        }
    }

    private void openGameRulesGui(World world, Player clicker) {
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new GameRulesGui(clicker, world).open()
        );
    }

    private void teleportToWorld(World world, Player teleporter, Player admin) {
        Location spawn = world.getSpawnLocation();

        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> {
                teleporter.teleport(spawn);

                String message = TranslationManager.translate("WorldSelector", "teleport_success",
                    "&aTeleported to &e{world}&a!")
                    .replace("{world}", world.getName());
                admin.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

                // If teleporting another player, notify them
                if (!teleporter.equals(admin)) {
                    String playerMessage = TranslationManager.translate("WorldSelector", "teleport_notification",
                        "&aYou have been teleported to &e{world}&a by &6{admin}")
                        .replace("{world}", world.getName())
                        .replace("{admin}", admin.getName());
                    teleporter.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', playerMessage));
                }
            }
        );
    }

    private void openPlayerSelectorForTeleport(World world, Player admin) {
        // Store the world for this admin
        pendingTeleports.put(admin.getUniqueId(), world);

        // Open player list GUI
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new PlayerListGui(admin, 1).open()
        );

        String message = TranslationManager.translate("WorldSelector", "select_player_to_teleport",
            "&eSelect a player to teleport to &6{world}")
            .replace("{world}", world.getName());
        admin.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
    }

    private void handleBack(Player clicker) {
        // Open ServerManagerGui
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new ServerManagerGui(clicker).open()
        );
    }

    private void handleCreateWorld(Player clicker) {
        // Open WorldGeneratorGui
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new WorldGeneratorGui(clicker).open()
        );
    }

    @Override
    public void open() {
        // Refresh world list before opening (in case new worlds were created)
        setupGuiItems();
        inventory = build();
        admin.openInventory(inventory);
    }
}
