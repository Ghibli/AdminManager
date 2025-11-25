package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SpeedControlGui extends BaseGui {

    private final Player targetPlayer;
    private final Player admin;
    private final SpeedType speedType;

    public enum SpeedType {
        WALK,
        FLY
    }

    public SpeedControlGui(Player player, Player targetPlayer, SpeedType speedType) {
        super(player, formatTitle(targetPlayer, speedType), 1);
        this.admin = player;
        this.targetPlayer = targetPlayer;
        this.speedType = speedType;
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
        // SpeedControlGui uses custom button layout
    }

    private static String formatTitle(Player targetPlayer, SpeedType speedType) {
        String typeStr = speedType == SpeedType.WALK ? "Walk" : "Fly";
        return String.format("§a[§6%s§a] §7%s Speed", targetPlayer.getName(), typeStr);
    }

    private void setupGuiItems() {
        // Row 2: Speed controls
        setItem(10, createDecreaseButton());
        setItem(13, createSpeedDisplayButton());
        setItem(16, createIncreaseButton());

        // Row 3: Reset button
        setItem(22, createResetButton());

        // Exit button (moved to slot 45 to prevent cursor auto-positioning)
        setItem(45, createExitButton());
    }

    private ItemStack createDecreaseButton() {
        String title = TranslationManager.translate("SpeedControl", "decrease_title", "&c- Decrease");
        String lore = TranslationManager.translate("SpeedControl", "decrease_lore", "&7Click to decrease speed");
        return createItem(Material.RED_CONCRETE, title, lore);
    }

    private ItemStack createSpeedDisplayButton() {
        float currentSpeed = speedType == SpeedType.WALK ?
                targetPlayer.getWalkSpeed() : targetPlayer.getFlySpeed();

        String title = TranslationManager.translate("SpeedControl", "display_title", "&eCurrent Speed");
        String lore = TranslationManager.translate("SpeedControl", "display_lore", "&7Speed: &f{speed}")
                .replace("{speed}", String.format("%.2f", currentSpeed));

        return createItem(Material.CLOCK, title, lore);
    }

    private ItemStack createIncreaseButton() {
        String title = TranslationManager.translate("SpeedControl", "increase_title", "&a+ Increase");
        String lore = TranslationManager.translate("SpeedControl", "increase_lore", "&7Click to increase speed");
        return createItem(Material.GREEN_CONCRETE, title, lore);
    }

    private ItemStack createResetButton() {
        float defaultSpeed = speedType == SpeedType.WALK ? 0.2f : 0.1f;
        String title = TranslationManager.translate("SpeedControl", "reset_title", "&6Reset");
        String lore = TranslationManager.translate("SpeedControl", "reset_lore", "&7Reset to default ({default})")
                .replace("{default}", String.format("%.1f", defaultSpeed));
        return createItem(Material.BARRIER, title, lore);
    }

    private ItemStack createExitButton() {
        String title = TranslationManager.translate("SpeedControl", "back_button_title", "&cBack");
        String lore = TranslationManager.translate("SpeedControl", "back_button_lore", "&aReturn to player management");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();

        switch (slot) {
            case 10: handleDecreaseClick(event); break;
            case 13: event.setCancelled(true); break; // Just display, no action
            case 16: handleIncreaseClick(event); break;
            case 22: handleResetClick(event); break;
            case 45: handleExitClick(event); break; // Moved from slot 49
            default: event.setCancelled(true); break;
        }
    }

    private void handleDecreaseClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        float currentSpeed = speedType == SpeedType.WALK ?
                targetPlayer.getWalkSpeed() : targetPlayer.getFlySpeed();

        float newSpeed = Math.max(-1.0f, currentSpeed - 0.1f);

        if (speedType == SpeedType.WALK) {
            targetPlayer.setWalkSpeed(newSpeed);
        } else {
            targetPlayer.setFlySpeed(newSpeed);
        }

        String message = TranslationManager.translate("SpeedControl", "speed_decreased",
                "&aSpeed decreased to &e{speed}")
                .replace("{speed}", String.format("%.2f", newSpeed));
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        // Refresh display button without closing GUI
        refreshSlot(13, createSpeedDisplayButton());
    }

    private void handleIncreaseClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        float currentSpeed = speedType == SpeedType.WALK ?
                targetPlayer.getWalkSpeed() : targetPlayer.getFlySpeed();

        float newSpeed = Math.min(1.0f, currentSpeed + 0.1f);

        if (speedType == SpeedType.WALK) {
            targetPlayer.setWalkSpeed(newSpeed);
        } else {
            targetPlayer.setFlySpeed(newSpeed);
        }

        String message = TranslationManager.translate("SpeedControl", "speed_increased",
                "&aSpeed increased to &e{speed}")
                .replace("{speed}", String.format("%.2f", newSpeed));
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        // Refresh display button without closing GUI
        refreshSlot(13, createSpeedDisplayButton());
    }

    private void handleResetClick(InventoryClickEvent event) {
        Player sender = (Player) event.getWhoClicked();
        float defaultSpeed = speedType == SpeedType.WALK ? 0.2f : 0.1f;

        if (speedType == SpeedType.WALK) {
            targetPlayer.setWalkSpeed(defaultSpeed);
        } else {
            targetPlayer.setFlySpeed(defaultSpeed);
        }

        String message = TranslationManager.translate("SpeedControl", "speed_reset",
                "&aSpeed reset to default &e{speed}")
                .replace("{speed}", String.format("%.1f", defaultSpeed));
        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

        // Refresh display button without closing GUI
        refreshSlot(13, createSpeedDisplayButton());
    }

    private void handleExitClick(InventoryClickEvent event) {
        new PlayerManage((Player) event.getWhoClicked(), targetPlayer).open();
    }
}
