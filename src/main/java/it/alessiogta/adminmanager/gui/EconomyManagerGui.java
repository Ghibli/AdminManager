package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.EconomyManager;
import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EconomyManagerGui extends BaseGui {

    private final Player targetPlayer;
    private final Player admin;

    public EconomyManagerGui(Player player, Player targetPlayer) {
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

    private static String formatTitle(Player targetPlayer) {
        return String.format("§a[§6%s§a] §7Economy", targetPlayer.getName());
    }

    private void setupGuiItems() {
        if (!EconomyManager.isEnabled()) {
            // Show error message if Vault is not available
            setItem(22, createErrorButton());
            setItem(45, createExitButton());
            return;
        }

        // Row 1: Add money
        setItem(0, createAddButton(10));
        setItem(1, createAddButton(100));
        setItem(2, createAddButton(1000));
        setItem(3, createAddButton(10000));

        // Row 2: Remove money
        setItem(9, createRemoveButton(10));
        setItem(10, createRemoveButton(100));
        setItem(11, createRemoveButton(1000));
        setItem(12, createRemoveButton(10000));

        // Row 3: Balance display
        setItem(22, createBalanceButton());

        // Exit button (moved to slot 45 to prevent cursor auto-positioning)
        setItem(45, createExitButton());
    }

    private ItemStack createAddButton(double amount) {
        String title = TranslationManager.translate("EconomyManager", "add_money_title", "&a+ {amount}")
                .replace("{amount}", EconomyManager.format(amount));
        String lore = TranslationManager.translate("EconomyManager", "add_money_lore", "&7Give {amount} to {player}")
                .replace("{amount}", EconomyManager.format(amount))
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.EMERALD, title, lore);
    }

    private ItemStack createRemoveButton(double amount) {
        String title = TranslationManager.translate("EconomyManager", "remove_money_title", "&c- {amount}")
                .replace("{amount}", EconomyManager.format(amount));
        String lore = TranslationManager.translate("EconomyManager", "remove_money_lore", "&7Take {amount} from {player}")
                .replace("{amount}", EconomyManager.format(amount))
                .replace("{player}", targetPlayer.getName());
        return createItem(Material.REDSTONE, title, lore);
    }

    private ItemStack createBalanceButton() {
        double balance = EconomyManager.getBalance(targetPlayer);
        String title = TranslationManager.translate("EconomyManager", "balance_title", "&eBalance");
        String lore = TranslationManager.translate("EconomyManager", "balance_lore", "&7Current: &f{balance}")
                .replace("{balance}", EconomyManager.format(balance));
        return createItem(Material.GOLD_INGOT, title, lore);
    }

    private ItemStack createErrorButton() {
        String title = TranslationManager.translate("EconomyManager", "error_title", "&cVault Not Found");
        String lore = TranslationManager.translate("EconomyManager", "error_lore", "&7Economy features are disabled\n&7Install Vault and an economy plugin");
        return createItem(Material.BARRIER, title, lore);
    }

    private ItemStack createExitButton() {
        String title = TranslationManager.translate("EconomyManager", "back_button_title", "&cBack");
        String lore = TranslationManager.translate("EconomyManager", "back_button_lore", "&aReturn to player management");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();

        if (!EconomyManager.isEnabled()) {
            if (slot == 45) {
                handleExitClick(event);
            } else {
                event.setCancelled(true);
            }
            return;
        }

        switch (slot) {
            // Row 1: Add money
            case 0: handleMoneyClick(event, 10, true); break;
            case 1: handleMoneyClick(event, 100, true); break;
            case 2: handleMoneyClick(event, 1000, true); break;
            case 3: handleMoneyClick(event, 10000, true); break;

            // Row 2: Remove money
            case 9: handleMoneyClick(event, 10, false); break;
            case 10: handleMoneyClick(event, 100, false); break;
            case 11: handleMoneyClick(event, 1000, false); break;
            case 12: handleMoneyClick(event, 10000, false); break;

            // Balance display
            case 22: event.setCancelled(true); break;

            // Exit (moved to slot 45)
            case 45: handleExitClick(event); break;

            default: event.setCancelled(true); break;
        }
    }

    private void handleMoneyClick(InventoryClickEvent event, double amount, boolean add) {
        Player sender = (Player) event.getWhoClicked();

        boolean success;
        if (add) {
            success = EconomyManager.deposit(targetPlayer, amount);
        } else {
            success = EconomyManager.withdraw(targetPlayer, amount);
        }

        if (success) {
            String message = TranslationManager.translate("EconomyManager",
                    add ? "money_added_message" : "money_removed_message",
                    add ? "&aAdded {amount} to {player}'s balance" : "&cRemoved {amount} from {player}'s balance")
                    .replace("{amount}", EconomyManager.format(amount))
                    .replace("{player}", targetPlayer.getName());
            sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

            if (targetPlayer.isOnline()) {
                String playerMessage = TranslationManager.translate("EconomyManager",
                        add ? "money_received_notification" : "money_taken_notification",
                        add ? "&aYou received {amount}!" : "&cYou lost {amount}!")
                        .replace("{amount}", EconomyManager.format(amount));
                targetPlayer.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', playerMessage));
            }
        } else {
            String message = TranslationManager.translate("EconomyManager", "transaction_failed",
                    "&cTransaction failed! {player} may not have enough money.")
                    .replace("{player}", targetPlayer.getName());
            sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
        }

        // Refresh balance display without closing GUI
        refreshSlot(22, createBalanceButton());
    }

    private void handleExitClick(InventoryClickEvent event) {
        new PlayerManage((Player) event.getWhoClicked(), targetPlayer).open();
    }
}
