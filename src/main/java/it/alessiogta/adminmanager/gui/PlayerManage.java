package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.FreezeManager;
import it.alessiogta.adminmanager.utils.GodModeManager;
import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerManage extends BaseGui {

    private final Player targetPlayer;
    private final Player admin;

    public PlayerManage(Player player, Player targetPlayer) {
        super(player, formatTitle(targetPlayer), 1);
        this.admin = player;
        this.targetPlayer = targetPlayer;
        setupGuiItems();
    }

    @Override
    public void open() {
        inventory = build();
        admin.openInventory(inventory);
    }

    private void refreshSlot(int slot, ItemStack item) {
        setItem(slot, item);
        if (inventory != null) {
            inventory.setItem(slot, item);
        }
    }

    @Override
    protected void setupNavigationButtons() {
        // Disable BaseGui's automatic navigation buttons
        // PlayerManage uses custom button layout
    }

    private static String formatTitle(Player targetPlayer) {
        return String.format("§a[§6%s§a] §7Manage", targetPlayer.getName());
    }

    private void setupGuiItems() {
        // Row 1: Teleport & Movement
        setItem(2, createTeleportButton());
        setItem(3, createTpToMeButton());
        setItem(4, createSpawnTeleportButton());
        setItem(5, createFreezeButton());
        setItem(7, createFlyButton());

        // Row 2: Health & State
        setItem(18, createHealButton());
        setItem(19, createFeedButton());
        setItem(22, createGodModeButton());
        setItem(23, createClearEffectsButton());
        setItem(20, createKillButton());

        // Row 3: Speed & Gamemode
        setItem(0, createWalkSpeedButton());
        setItem(8, createFlySpeedButton());
        setItem(25, createGamemodeToggleButton());

        // Row 4: Inventory Management
        setItem(37, createInventoryButton());
        setItem(38, createEnderChestButton());

        // Row 5: Items & Special
        setItem(40, createGiveItemButton());
        setItem(41, createGetSkullButton());
        setItem(34, createArmorCreatorButton());
        setItem(35, createToolCreatorButton());
        setItem(43, createEconomyButton());

        // Row 6: Exit (moved to slot 45 to prevent cursor auto-positioning)
        setItem(45, createExitButton());

        // Fill slot 49 with decorative glass to prevent accidental clicks
        setItem(49, createDecorativeGlass());
    }

    private ItemStack createDecorativeGlass() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        org.bukkit.inventory.meta.ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            glass.setItemMeta(meta);
        }
        return glass;
    }

    // ========== TELEPORT BUTTONS ==========

    private ItemStack createTeleportButton() {
        String title = TranslationManager.translate("PlayerManage", "teleport_title", "&aTeleport");
        String lore = TranslationManager.translate("PlayerManage", "teleport_lore", "&7Teleport to {player}")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.ENDER_PEARL, title, lore);
    }

    private ItemStack createTpToMeButton() {
        String title = TranslationManager.translate("PlayerManage", "tp_to_me_title", "&aTP to me");
        String lore = TranslationManager.translate("PlayerManage", "tp_to_me_lore", "&7Teleport {player} to you")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.COMPASS, title, lore);
    }

    private ItemStack createFreezeButton() {
        boolean isFrozen = FreezeManager.isFrozen(targetPlayer);
        String title = TranslationManager.translate("PlayerManage",
                isFrozen ? "unfreeze_title" : "freeze_title",
                isFrozen ? "&aUnfreeze" : "&bFreeze");
        String lore = TranslationManager.translate("PlayerManage",
                isFrozen ? "unfreeze_lore" : "freeze_lore",
                isFrozen ? "&7Unfreeze {player}" : "&7Freeze {player}")
                .replace("{player}", targetPlayer.getName());
        return createItem(isFrozen ? Material.LIGHT_BLUE_DYE : Material.BLUE_ICE, title, lore);
    }

    private ItemStack createFlyButton() {
        boolean canFly = targetPlayer.getAllowFlight();
        String title = TranslationManager.translate("PlayerManage",
                canFly ? "fly_disable_title" : "fly_enable_title",
                canFly ? "&cDisable Fly" : "&aEnable Fly");
        String lore = TranslationManager.translate("PlayerManage",
                canFly ? "fly_disable_lore" : "fly_enable_lore",
                canFly ? "&7Disable fly for {player}" : "&7Enable fly for {player}")
                .replace("{player}", targetPlayer.getName());
        return createItem(canFly ? Material.FEATHER : Material.ELYTRA, title, lore);
    }

    private ItemStack createGetSkullButton() {
        String title = TranslationManager.translate("PlayerManage", "get_skull_title", "&6Get Skull");
        String lore = TranslationManager.translate("PlayerManage", "get_skull_lore", "&7Get {player}'s skull")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.PLAYER_HEAD, title, lore);
    }

    private ItemStack createSpawnTeleportButton() {
        String title = TranslationManager.translate("PlayerManage", "spawn_teleport_title", "&bTP to Spawn");
        String lore = TranslationManager.translate("PlayerManage", "spawn_teleport_lore", "&7Teleport {player} to spawn")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.RESPAWN_ANCHOR, title, lore);
    }

    private ItemStack createEconomyButton() {
        String title = TranslationManager.translate("PlayerManage", "economy_title", "&6Economy");
        String lore = TranslationManager.translate("PlayerManage", "economy_lore", "&7Manage {player}'s money")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.GOLD_INGOT, title, lore);
    }

    private ItemStack createArmorCreatorButton() {
        String title = TranslationManager.translate("PlayerManage", "armor_creator_title", "&dArmor Creator");
        String lore = TranslationManager.translate("PlayerManage", "armor_creator_lore", "&7Create and give armor to {player}")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.DIAMOND_CHESTPLATE, title, lore);
    }

    private ItemStack createToolCreatorButton() {
        String title = TranslationManager.translate("PlayerManage", "tool_creator_title", "&6Tool Creator");
        String lore = TranslationManager.translate("PlayerManage", "tool_creator_lore", "&7Create and give tools to {player}")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.DIAMOND_PICKAXE, title, lore);
    }

    // ========== PLAYER STATE BUTTONS ==========

    private ItemStack createHealButton() {
        String title = TranslationManager.translate("PlayerManage", "heal_title", "&cHeal");
        String lore = TranslationManager.translate("PlayerManage", "heal_lore", "&7Restore health and food for {player}")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.GOLDEN_APPLE, title, lore);
    }

    private ItemStack createFeedButton() {
        String title = TranslationManager.translate("PlayerManage", "feed_title", "&6Feed");
        String lore = TranslationManager.translate("PlayerManage", "feed_lore", "&7Restore food for {player}")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.COOKED_BEEF, title, lore);
    }

    private ItemStack createKillButton() {
        String title = TranslationManager.translate("PlayerManage", "kill_title", "&4Kill");
        String lore = TranslationManager.translate("PlayerManage", "kill_lore", "&7Kill {player}")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.SKELETON_SKULL, title, lore);
    }

    private ItemStack createClearEffectsButton() {
        String title = TranslationManager.translate("PlayerManage", "clear_effects_title", "&eClear Effects");
        String lore = TranslationManager.translate("PlayerManage", "clear_effects_lore", "&7Remove all potion effects from {player}")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.MILK_BUCKET, title, lore);
    }

    private ItemStack createGodModeButton() {
        boolean hasGodMode = GodModeManager.hasGodMode(targetPlayer);
        String title = TranslationManager.translate("PlayerManage",
                hasGodMode ? "godmode_disable_title" : "godmode_enable_title",
                hasGodMode ? "&cDisable God Mode" : "&6Enable God Mode");
        String lore = TranslationManager.translate("PlayerManage",
                hasGodMode ? "godmode_disable_lore" : "godmode_enable_lore",
                hasGodMode ? "&7Disable invincibility for {player}" : "&7Make {player} invincible")
                .replace("{player}", targetPlayer.getName());
        return createItem(hasGodMode ? Material.DIAMOND_CHESTPLATE : Material.GOLDEN_CHESTPLATE, title, lore);
    }

    private ItemStack createWalkSpeedButton() {
        float walkSpeed = targetPlayer.getWalkSpeed();
        String title = TranslationManager.translate("PlayerManage", "walk_speed_title", "&eWalk Speed");
        String lore = TranslationManager.translate("PlayerManage", "walk_speed_lore", "&7Current: &f{speed}\n&7Click to modify")
                .replace("{speed}", String.format("%.2f", walkSpeed));
        return createItem(Material.LEATHER_BOOTS, title, lore);
    }

    private ItemStack createFlySpeedButton() {
        float flySpeed = targetPlayer.getFlySpeed();
        String title = TranslationManager.translate("PlayerManage", "fly_speed_title", "&bFly Speed");
        String lore = TranslationManager.translate("PlayerManage", "fly_speed_lore", "&7Current: &f{speed}\n&7Click to modify")
                .replace("{speed}", String.format("%.2f", flySpeed));
        return createItem(Material.PHANTOM_MEMBRANE, title, lore);
    }

    private ItemStack createGiveItemButton() {
        String title = TranslationManager.translate("PlayerManage", "give_item_title", "&dGive Item");
        String lore = TranslationManager.translate("PlayerManage", "give_item_lore", "&7Give item in hand to {player}")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.DROPPER, title, lore);
    }

    // ========== INVENTORY BUTTONS ==========

    private ItemStack createInventoryButton() {
        String title = TranslationManager.translate("PlayerManage", "inventory_title", "&9View Inventory");
        String lore = TranslationManager.translate("PlayerManage", "inventory_lore", "&7View {player}'s inventory")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.CHEST, title, lore);
    }

    private ItemStack createEnderChestButton() {
        String title = TranslationManager.translate("PlayerManage", "enderchest_title", "&5View EnderChest");
        String lore = TranslationManager.translate("PlayerManage", "enderchest_lore", "&7View {player}'s enderchest")
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.ENDER_CHEST, title, lore);
    }

    // ========== GAMEMODE BUTTONS ==========

    private ItemStack createGamemodeToggleButton() {
        GameMode currentMode = targetPlayer.getGameMode();

        String title = TranslationManager.translate("PlayerManage", "gamemode_toggle_title", "&eGamemode");

        // Build lore with all gamemodes, highlighting current one
        java.util.List<String> loreLines = new java.util.ArrayList<>();
        loreLines.add(TranslationManager.translate("PlayerManage", "gamemode_toggle_lore", "&7Click to cycle"));
        loreLines.add(""); // Empty line for spacing

        GameMode[] modes = {GameMode.SURVIVAL, GameMode.CREATIVE, GameMode.ADVENTURE, GameMode.SPECTATOR};
        for (GameMode mode : modes) {
            String modeName = translateGamemodeName(mode);
            if (mode == currentMode) {
                loreLines.add("&a✓ " + modeName);
            } else {
                loreLines.add("&7  " + modeName);
            }
        }

        Material material;
        switch (currentMode) {
            case SURVIVAL: material = Material.IRON_SWORD; break;
            case CREATIVE: material = Material.GRASS_BLOCK; break;
            case ADVENTURE: material = Material.MAP; break;
            case SPECTATOR: material = Material.GLASS; break;
            default: material = Material.PAPER;
        }

        return createItem(material, title, loreLines.toArray(new String[0]));
    }

    private String translateGamemodeName(GameMode gameMode) {
        String key = "gamemode_" + gameMode.name().toLowerCase() + "_name";
        String defaultName = gameMode.name().substring(0, 1) + gameMode.name().substring(1).toLowerCase();
        return TranslationManager.translate("PlayerManage", key, defaultName);
    }

    // ========== EXIT BUTTON ==========

    private ItemStack createExitButton() {
        String title = TranslationManager.translate("PlayerManage", "back_button_title", "&cBack");
        String lore = TranslationManager.translate("PlayerManage", "back_button_lore", "&aReturn to player list");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    // ========== CLICK HANDLERS ==========

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();

        switch (slot) {
            // Row 1: Teleport & Movement
            case 2: handleTeleportClick(event); break;
            case 3: handleTpToMeClick(event); break;
            case 4: handleSpawnTeleportClick(event); break;
            case 5: handleFreezeClick(event); break;
            case 7: handleFlyClick(event); break;

            // Row 2: Health & State
            case 18: handleHealClick(event); break;
            case 19: handleFeedClick(event); break;
            case 22: handleGodModeClick(event); break;
            case 23: handleClearEffectsClick(event); break;
            case 20: handleKillClick(event); break;

            // Row 3: Speed & Gamemode
            case 0: handleWalkSpeedClick(event); break;
            case 8: handleFlySpeedClick(event); break;
            case 25: handleGamemodeToggleClick(event); break;

            // Row 4: Inventory Management
            case 37: handleInventoryClick(event); break;
            case 38: handleEnderChestClick(event); break;

            // Row 5: Items & Special
            case 40: handleGiveItemClick(event); break;
            case 41: handleGetSkullClick(event); break;
            case 34: handleArmorCreatorClick(event); break;
            case 35: handleToolCreatorClick(event); break;
            case 43: handleEconomyClick(event); break;

            // Row 6: Exit (slot 45)
            case 45: handleExitClick(event); break;

            default: event.setCancelled(true); break;
        }
    }

    // ========== TELEPORT HANDLERS ==========

    private void handleTeleportClick(InventoryClickEvent event) {
        event.getWhoClicked().teleport(targetPlayer.getLocation());
        String message = TranslationManager.translate("PlayerManage", "teleport_message", "&aYou teleported to &e{player}")
                .replace("{player}", targetPlayer.getName());
        event.getWhoClicked().sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
    }

    private void handleTpToMeClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        if (targetPlayer.isOnline()) {
            targetPlayer.teleport(sender.getLocation());
            String message = TranslationManager.translate("PlayerManage", "tp_to_me_success", "&e{player} &ahas been teleported to you")
                    .replace("{player}", targetPlayer.getName());
            sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
        } else {
            String message = TranslationManager.translate("PlayerManage", "tp_to_me_failure", "&e{player} &cis not online!")
                    .replace("{player}", targetPlayer.getName());
            sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    private void handleFreezeClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        boolean isFrozen = FreezeManager.toggleFreeze(targetPlayer);

        String message = TranslationManager.translate("PlayerManage",
                isFrozen ? "freeze_message" : "unfreeze_message",
                isFrozen ? "&c{player} has been frozen" : "&a{player} has been unfrozen")
                .replace("{player}", targetPlayer.getName());
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        if (targetPlayer.isOnline()) {
            String playerMessage = TranslationManager.translate("PlayerManage",
                    isFrozen ? "freeze_notification" : "unfreeze_notification",
                    isFrozen ? "&cYou have been frozen!" : "&aYou have been unfrozen!");
            targetPlayer.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', playerMessage));
        }

        // Refresh button at new position (slot 5)
        refreshSlot(5, createFreezeButton());
    }

    private void handleFlyClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        boolean newFlyState = !targetPlayer.getAllowFlight();

        targetPlayer.setAllowFlight(newFlyState);
        if (!newFlyState && targetPlayer.isFlying()) {
            targetPlayer.setFlying(false);
        }

        String message = TranslationManager.translate("PlayerManage",
                newFlyState ? "fly_enabled_message" : "fly_disabled_message",
                newFlyState ? "&aFly enabled for {player}" : "&cFly disabled for {player}")
                .replace("{player}", targetPlayer.getName());
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        // Refresh button at new position (slot 7)
        refreshSlot(7, createFlyButton());
    }

    private void handleGetSkullClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        org.bukkit.inventory.meta.SkullMeta skullMeta = (org.bukkit.inventory.meta.SkullMeta) skull.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(targetPlayer);
            skull.setItemMeta(skullMeta);
        }

        sender.getInventory().addItem(skull);

        String message = TranslationManager.translate("PlayerManage", "get_skull_message", "&aYou received {player}'s skull")
                .replace("{player}", targetPlayer.getName());
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
    }

    private void handleSpawnTeleportClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();

        // Check if there's a custom spawn command configured
        String spawnCommand = Bukkit.getPluginManager().getPlugin("AdminManager").getConfig().getString("spawn_command", "");

        if (spawnCommand != null && !spawnCommand.isEmpty()) {
            // Execute custom spawn command
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), spawnCommand + " " + targetPlayer.getName());
        } else {
            // Teleport to world spawn
            targetPlayer.teleport(targetPlayer.getWorld().getSpawnLocation());
        }

        String message = TranslationManager.translate("PlayerManage", "spawn_teleport_message", "&a{player} has been teleported to spawn")
                .replace("{player}", targetPlayer.getName());
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        if (targetPlayer.isOnline()) {
            String playerMessage = TranslationManager.translate("PlayerManage", "spawn_teleport_notification", "&aYou have been teleported to spawn!");
            targetPlayer.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', playerMessage));
        }
    }

    private void handleEconomyClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        // Don't close inventory - open new GUI directly to preserve cursor position
        Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new EconomyManagerGui(sender, targetPlayer).open()
        );
    }

    private void handleArmorCreatorClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        // Don't close inventory - open new GUI directly to preserve cursor position
        Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new ArmorCreatorGui(sender, targetPlayer).open()
        );
    }

    private void handleToolCreatorClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        // Don't close inventory - open new GUI directly to preserve cursor position
        Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new ToolCreatorGui(sender, targetPlayer).open()
        );
    }

    // ========== PLAYER STATE HANDLERS ==========

    private void handleHealClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        targetPlayer.setHealth(targetPlayer.getMaxHealth());
        targetPlayer.setFoodLevel(20);
        targetPlayer.setSaturation(20);

        String message = TranslationManager.translate("PlayerManage", "heal_message", "&a{player} has been healed")
                .replace("{player}", targetPlayer.getName());
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
    }

    private void handleFeedClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        targetPlayer.setFoodLevel(20);
        targetPlayer.setSaturation(20);

        String message = TranslationManager.translate("PlayerManage", "feed_message", "&a{player} has been fed")
                .replace("{player}", targetPlayer.getName());
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
    }

    private void handleKillClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        targetPlayer.setHealth(0);

        String message = TranslationManager.translate("PlayerManage", "kill_message", "&c{player} has been killed")
                .replace("{player}", targetPlayer.getName());
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
    }

    private void handleClearEffectsClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        for (PotionEffect effect : targetPlayer.getActivePotionEffects()) {
            targetPlayer.removePotionEffect(effect.getType());
        }

        String message = TranslationManager.translate("PlayerManage", "clear_effects_message", "&aCleared all effects from {player}")
                .replace("{player}", targetPlayer.getName());
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
    }

    private void handleGodModeClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        boolean hasGodMode = GodModeManager.toggleGodMode(targetPlayer);

        String message = TranslationManager.translate("PlayerManage",
                hasGodMode ? "godmode_enabled_message" : "godmode_disabled_message",
                hasGodMode ? "&6God mode enabled for {player}" : "&cGod mode disabled for {player}")
                .replace("{player}", targetPlayer.getName());
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        if (targetPlayer.isOnline()) {
            String playerMessage = TranslationManager.translate("PlayerManage",
                    hasGodMode ? "godmode_notification_on" : "godmode_notification_off",
                    hasGodMode ? "&6You are now invincible!" : "&cYou are no longer invincible!");
            targetPlayer.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', playerMessage));
        }

        // Refresh button at new position (slot 22)
        refreshSlot(22, createGodModeButton());
    }

    private void handleWalkSpeedClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        // Don't close inventory - open new GUI directly to preserve cursor position
        Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new SpeedControlGui(sender, targetPlayer, SpeedControlGui.SpeedType.WALK).open()
        );
    }

    private void handleFlySpeedClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        // Don't close inventory - open new GUI directly to preserve cursor position
        Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new SpeedControlGui(sender, targetPlayer, SpeedControlGui.SpeedType.FLY).open()
        );
    }

    private void handleGiveItemClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        ItemStack itemInHand = sender.getInventory().getItemInMainHand();

        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            String message = TranslationManager.translate("PlayerManage", "give_item_no_item", "&cYou must hold an item in your hand!");
            sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
            return;
        }

        // Clone the item to give to the player
        ItemStack itemToGive = itemInHand.clone();
        targetPlayer.getInventory().addItem(itemToGive);

        String itemName = itemToGive.getType().name().toLowerCase().replace("_", " ");
        String message = TranslationManager.translate("PlayerManage", "give_item_message", "&aGave {amount}x {item} to {player}")
                .replace("{amount}", String.valueOf(itemToGive.getAmount()))
                .replace("{item}", itemName)
                .replace("{player}", targetPlayer.getName());
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        if (targetPlayer.isOnline()) {
            String playerMessage = TranslationManager.translate("PlayerManage", "give_item_notification", "&aYou received {amount}x {item}!")
                    .replace("{amount}", String.valueOf(itemToGive.getAmount()))
                    .replace("{item}", itemName);
            targetPlayer.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', playerMessage));
        }
    }

    // ========== INVENTORY HANDLERS ==========

    private void handleInventoryClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        // Don't close inventory - open new GUI directly to preserve cursor position
        Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new PlayerInventoryGui(sender, targetPlayer).open()
        );
    }

    private void handleEnderChestClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        // Don't close inventory - open new GUI directly to preserve cursor position
        Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new PlayerEnderChestGui(sender, targetPlayer).open()
        );
    }

    // ========== GAMEMODE HANDLERS ==========

    private void handleGamemodeToggleClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        GameMode currentMode = targetPlayer.getGameMode();

        // Cycle to next gamemode
        GameMode nextMode;
        switch (currentMode) {
            case SURVIVAL: nextMode = GameMode.CREATIVE; break;
            case CREATIVE: nextMode = GameMode.ADVENTURE; break;
            case ADVENTURE: nextMode = GameMode.SPECTATOR; break;
            case SPECTATOR: nextMode = GameMode.SURVIVAL; break;
            default: nextMode = GameMode.SURVIVAL;
        }

        targetPlayer.setGameMode(nextMode);

        String modeName = translateGamemodeName(nextMode);
        String message = TranslationManager.translate("PlayerManage", "gamemode_changed_message", "&eSet {player}'s gamemode to {mode}")
                .replace("{player}", targetPlayer.getName())
                .replace("{mode}", modeName);
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        if (targetPlayer.isOnline()) {
            String playerMessage = TranslationManager.translate("PlayerManage", "gamemode_notification", "&eYour gamemode has been changed to {mode}")
                    .replace("{mode}", modeName);
            targetPlayer.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', playerMessage));
        }

        // Refresh button at new position (slot 25)
        refreshSlot(25, createGamemodeToggleButton());
    }

    // ========== EXIT HANDLER ==========

    private void handleExitClick(InventoryClickEvent event) {
        new PlayerListGui((Player) event.getWhoClicked(), 1).open();
    }
}
