package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameRulesGui extends BaseGui {

    private final Player admin;
    private final World targetWorld;
    private final Map<Integer, GameRule<?>> slotToRule = new HashMap<>();

    // Game rules with their materials
    private static final Map<String, Material> RULE_MATERIALS = new HashMap<String, Material>() {{
        put("announceAdvancements", Material.BOOK);
        put("blockExplosionDropDecay", Material.TNT);
        put("commandBlockOutput", Material.COMMAND_BLOCK);
        put("disableElytraMovementCheck", Material.ELYTRA);
        put("disableRaids", Material.BELL);
        put("doDaylightCycle", Material.CLOCK);
        put("doEntityDrops", Material.ITEM_FRAME);
        put("doFireTick", Material.FLINT_AND_STEEL);
        put("doInsomnia", Material.PHANTOM_MEMBRANE);
        put("doImmediateRespawn", Material.RED_BED);
        put("doLimitedCrafting", Material.CRAFTING_TABLE);
        put("doMobLoot", Material.DIAMOND);
        put("doMobSpawning", Material.ZOMBIE_SPAWN_EGG);
        put("doPatrolSpawning", Material.PILLAGER_SPAWN_EGG);
        put("doTileDrops", Material.GRASS_BLOCK);
        put("doTraderSpawning", Material.WANDERING_TRADER_SPAWN_EGG);
        put("doVinesSpread", Material.VINE);
        put("doWeatherCycle", Material.WATER_BUCKET);
        put("doWardenSpawning", Material.SCULK_SENSOR);
        put("drowningDamage", Material.POTION);
        put("fallDamage", Material.FEATHER);
        put("fireDamage", Material.LAVA_BUCKET);
        put("freezeDamage", Material.POWDER_SNOW_BUCKET);
        put("forgiveDeadPlayers", Material.TOTEM_OF_UNDYING);
        put("globalSoundEvents", Material.NOTE_BLOCK);
        put("keepInventory", Material.CHEST);
        put("lavaDamage", Material.MAGMA_BLOCK);
        put("logAdminCommands", Material.WRITABLE_BOOK);
        put("maxCommandChainLength", Material.CHAIN_COMMAND_BLOCK);
        put("maxEntityCramming", Material.PISTON);
        put("mobExplosionDropDecay", Material.TNT_MINECART);
        put("mobGriefing", Material.CREEPER_HEAD);
        put("naturalRegeneration", Material.GOLDEN_APPLE);
        put("playersSleepingPercentage", Material.WHITE_BED);
        put("pvp", Material.DIAMOND_SWORD);
        put("randomTickSpeed", Material.REDSTONE_TORCH);
        put("reducedDebugInfo", Material.BARRIER);
        put("sendCommandFeedback", Material.COMMAND_BLOCK_MINECART);
        put("showDeathMessages", Material.SKELETON_SKULL);
        put("spawnRadius", Material.COMPASS);
        put("tntExplosionDropDecay", Material.TNT_MINECART);
        put("universalAnger", Material.FIRE_CHARGE);
    }};

    public GameRulesGui(Player admin, World world) {
        super(admin, TranslationManager.translate("GameRules", "title", "&6&lGame Rules - {world}")
            .replace("{world}", world.getName()), 1);
        this.admin = admin;
        this.targetWorld = world;
        setupGuiItems();
    }

    @Override
    protected void setupNavigationButtons() {
        // Disable BaseGui's automatic navigation buttons
    }

    private void setupGuiItems() {
        // Setup game rule buttons
        int slot = 0;
        for (Map.Entry<String, Material> entry : RULE_MATERIALS.entrySet()) {
            if (slot >= 45) break; // Max 45 slots for rules

            String ruleName = entry.getKey();
            GameRule<?> rule = GameRule.getByName(ruleName);

            if (rule != null && targetWorld.getGameRuleValue(rule) != null) {
                slotToRule.put(slot, rule);
                setItem(slot, createGameRuleButton(ruleName, rule, entry.getValue(), targetWorld));
                slot++;
            }
        }

        // Reset World button at slot 45 (only for Nether and End)
        if (targetWorld.getEnvironment() == World.Environment.NETHER ||
            targetWorld.getEnvironment() == World.Environment.THE_END) {
            setItem(45, createResetWorldButton());
        }

        // Back button at slot 49
        setItem(49, createBackButton());
    }

    private ItemStack createGameRuleButton(String ruleName, GameRule<?> rule, Material material, World world) {
        Object value = world.getGameRuleValue(rule);
        boolean enabled = false;
        String valueStr = "";

        if (value instanceof Boolean) {
            enabled = (Boolean) value;
            valueStr = enabled ? "&aEnabled" : "&cDisabled";
        } else if (value instanceof Integer) {
            valueStr = "&e" + value;
        } else {
            valueStr = "&7" + String.valueOf(value);
        }

        String statusColor = enabled ? "&a" : "&c";

        String title = TranslationManager.translate("GameRules", "rule_" + ruleName + "_title",
            "&f" + ruleName);

        String description = TranslationManager.translate("GameRules", "rule_" + ruleName + "_description",
            "&7Game rule");

        String lore = description + "\n" +
                     statusColor + "→ " + valueStr + "\n\n" +
                     "&e&lLEFT: &7Toggle";

        return createItem(material, title, lore.split("\n"));
    }

    private ItemStack createBackButton() {
        String title = TranslationManager.translate("GameRules", "back_button_title", "&cIndietro");
        String lore = TranslationManager.translate("GameRules", "back_button_lore", "&7Torna a World Selector");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    private ItemStack createResetWorldButton() {
        String worldType = targetWorld.getEnvironment() == World.Environment.THE_END ? "End" : "Nether";
        Material material = targetWorld.getEnvironment() == World.Environment.THE_END ?
            Material.DRAGON_EGG : Material.NETHERRACK;

        String title = TranslationManager.translate("GameRules", "reset_world_title", "&c&lReset {world}")
            .replace("{world}", worldType);

        String lore = TranslationManager.translate("GameRules", "reset_world_lore",
            "&7Rigenera completamente il mondo\n&7Teletrasporta tutti i giocatori\n&7e resetta il mondo allo stato iniziale\n\n&c&lWARNING: &7Azione irreversibile!\n&e&lClick: &7Reset world")
            .split("\n");

        return createItem(material, title, lore);
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

        if (slot == 45 && (targetWorld.getEnvironment() == World.Environment.NETHER ||
                          targetWorld.getEnvironment() == World.Environment.THE_END)) {
            // Reset World button
            handleResetWorld(clicker);
            return;
        }

        // Check if clicked slot has a game rule
        if (slotToRule.containsKey(slot)) {
            handleGameRuleToggle(slot, clicker);
        }
    }

    private void handleGameRuleToggle(int slot, Player clicker) {
        GameRule<?> rule = slotToRule.get(slot);
        Object currentValue = targetWorld.getGameRuleValue(rule);

        // Toggle boolean rules
        if (currentValue instanceof Boolean) {
            boolean newValue = !(Boolean) currentValue;

            // Apply to target world only
            targetWorld.setGameRule((GameRule<Boolean>) rule, newValue);

            String status = newValue ? "&aabilitata" : "&cdisabilitata";
            String message = TranslationManager.translate("GameRules", "rule_toggled_single",
                "&6Regola &e{rule} " + status + " &6in &e{world}")
                .replace("{rule}", rule.getName())
                .replace("{world}", targetWorld.getName());
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

            // Refresh button
            String ruleName = rule.getName();
            Material material = RULE_MATERIALS.get(ruleName);
            if (material != null) {
                refreshSlot(slot, createGameRuleButton(ruleName, rule, material, targetWorld));
            }
        } else {
            // For non-boolean rules, send info message
            String message = TranslationManager.translate("GameRules", "rule_not_boolean",
                "&cQuesta regola non è un toggle. Valore corrente: &e{value}")
                .replace("{value}", String.valueOf(currentValue));
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    private void handleBack(Player clicker) {
        // Don't close inventory - open WorldSelectorGui
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new WorldSelectorGui(clicker).open()
        );
    }

    private void handleResetWorld(Player clicker) {
        String worldName = targetWorld.getName();
        String worldType = targetWorld.getEnvironment() == World.Environment.THE_END ? "End" : "Nether";

        clicker.closeInventory();

        String startMessage = TranslationManager.translate("GameRules", "reset_world_start",
            "&c&lRESET WORLD: &eInizio reset del mondo &6{world}&e...")
            .replace("{world}", worldType);
        clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', startMessage));

        // Broadcast to all players
        String broadcastMessage = TranslationManager.translate("GameRules", "reset_world_broadcast",
            "&c&lWARNING: &eIl mondo &6{world} &everrà resettato tra &c3 secondi&e!")
            .replace("{world}", worldType);
        Bukkit.broadcastMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', broadcastMessage));

        // Wait 3 seconds before reset
        Bukkit.getScheduler().runTaskLater(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> performWorldReset(worldName, worldType, clicker),
            60L // 3 seconds (60 ticks)
        );
    }

    private void performWorldReset(String worldName, String worldType, Player initiator) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            initiator.sendMessage(org.bukkit.ChatColor.RED + "Errore: Mondo non trovato!");
            return;
        }

        // 1. Teleport all players in this world to spawn
        World mainWorld = Bukkit.getWorlds().get(0);
        Location spawn = mainWorld.getSpawnLocation();

        List<Player> playersInWorld = world.getPlayers();
        for (Player player : playersInWorld) {
            player.teleport(spawn);
            String tpMessage = TranslationManager.translate("GameRules", "reset_world_teleported",
                "&6Sei stato teletrasportato allo spawn perché il &c{world} &6sta per essere resettato!")
                .replace("{world}", worldType);
            player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', tpMessage));
        }

        // 2. Save the world
        world.save();

        // 3. Unload the world
        boolean unloaded = Bukkit.unloadWorld(world, false);
        if (!unloaded) {
            initiator.sendMessage(org.bukkit.ChatColor.RED + "Errore: Impossibile unload del mondo!");
            return;
        }

        // 4. Delete world folder
        File worldFolder = world.getWorldFolder();
        try {
            deleteWorldFolder(worldFolder);

            String successMessage = TranslationManager.translate("GameRules", "reset_world_success",
                "&a&lSUCCESS: &eIl mondo &6{world} &eè stato resettato! Verrà rigenerato al prossimo accesso.")
                .replace("{world}", worldType);
            Bukkit.broadcastMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', successMessage));

        } catch (IOException e) {
            String errorMessage = TranslationManager.translate("GameRules", "reset_world_error",
                "&c&lERRORE: &eNon è stato possibile eliminare il mondo. Controlla i permessi del server.")
                .replace("{world}", worldType);
            initiator.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', errorMessage));
            e.printStackTrace();
        }
    }

    private void deleteWorldFolder(File path) throws IOException {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorldFolder(file);
                    } else {
                        Files.delete(file.toPath());
                    }
                }
            }
            Files.delete(path.toPath());
        }
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
