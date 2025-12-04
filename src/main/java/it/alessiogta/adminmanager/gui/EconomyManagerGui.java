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
    private final boolean adminMode; // true = admin overview, false = single player management

    // Constructor for managing a specific player (called from PlayerManage)
    public EconomyManagerGui(Player player, Player targetPlayer) {
        super(player, formatTitle(targetPlayer), 1);
        this.admin = player;
        this.targetPlayer = targetPlayer;
        this.adminMode = false;
        setupGuiItems();
    }

    // Constructor for admin economy overview (called from ServerManager)
    public EconomyManagerGui(Player player) {
        super(player, TranslationManager.translate("EconomyManager", "admin_title", "&6&lEconomy Manager"), 1);
        this.admin = player;
        this.targetPlayer = null;
        this.adminMode = true;
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
        // EconomyManagerGui uses custom button layout
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

        if (adminMode) {
            // Admin overview mode
            setupAdminView();
        } else {
            // Single player management mode
            setupPlayerManagement();
        }

        // Exit button (moved to slot 45 to prevent cursor auto-positioning)
        setItem(45, createExitButton());

        // Fill slot 49 with decorative glass to prevent accidental clicks
        setItem(49, createDecorativeGlass());
    }

    private void setupAdminView() {
        // Provider info
        setItem(4, createProviderInfoButton());

        // Online players with balances
        java.util.List<Player> onlinePlayers = new java.util.ArrayList<>(org.bukkit.Bukkit.getOnlinePlayers());
        int[] playerSlots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

        for (int i = 0; i < Math.min(onlinePlayers.size(), playerSlots.length); i++) {
            Player player = onlinePlayers.get(i);
            setItem(playerSlots[i], createPlayerBalanceButton(player));
        }

        // Statistics
        setItem(10, createTotalMoneyButton());
        setItem(16, createAverageBalanceButton());
    }

    private void setupPlayerManagement() {
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

    // ========== ADMIN MODE BUTTONS ==========

    private ItemStack createProviderInfoButton() {
        org.bukkit.plugin.RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp =
            org.bukkit.Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

        if (rsp != null && rsp.getProvider() != null) {
            String providerName = rsp.getPlugin().getName();
            String currencyName = rsp.getProvider().currencyNamePlural();

            String title = "&6&lProvider: &e" + providerName;
            String lore = "&7Valuta: &f" + currencyName + "\n&7Priorità: &f" + rsp.getPriority().name();
            return createItem(Material.EMERALD_BLOCK, title, lore.split("\n"));
        } else {
            return createItem(Material.BARRIER, "&cNessun provider", "&7Errore di configurazione");
        }
    }

    private ItemStack createPlayerBalanceButton(Player player) {
        double balance = EconomyManager.getBalance(player);

        // Use player head texture
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        org.bukkit.inventory.meta.SkullMeta meta = (org.bukkit.inventory.meta.SkullMeta) playerHead.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                "&e" + player.getName()));

            java.util.List<String> loreList = new java.util.ArrayList<>();
            loreList.add(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                "&7Balance: &a" + EconomyManager.format(balance)));
            loreList.add("");
            loreList.add(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                "&e&lCLICK: &7Gestisci economia"));
            meta.setLore(loreList);

            playerHead.setItemMeta(meta);
        }

        return playerHead;
    }

    private ItemStack createTotalMoneyButton() {
        double total = 0;
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            total += EconomyManager.getBalance(player);
        }

        String title = "&6&lTotale in Circolazione";
        String lore = "&7Online: &e" + EconomyManager.format(total) + "\n&7Player: &e" + org.bukkit.Bukkit.getOnlinePlayers().size();
        return createItem(Material.GOLD_BLOCK, title, lore.split("\n"));
    }

    private ItemStack createAverageBalanceButton() {
        double total = 0;
        int count = org.bukkit.Bukkit.getOnlinePlayers().size();

        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            total += EconomyManager.getBalance(player);
        }

        double average = count > 0 ? total / count : 0;

        String title = "&6&lMedia Balance";
        String lore = "&7Media: &e" + EconomyManager.format(average) + "\n&7Player online: &e" + count;
        return createItem(Material.DIAMOND, title, lore.split("\n"));
    }

    // ========== PLAYER MANAGEMENT BUTTONS ==========

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
        Player clicker = (Player) event.getWhoClicked();

        if (!EconomyManager.isEnabled()) {
            if (slot == 45) {
                handleExitClick(event);
            } else {
                event.setCancelled(true);
            }
            return;
        }

        if (adminMode) {
            // Admin mode: handle player head clicks to open their economy management
            int[] playerSlots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
            for (int playerSlot : playerSlots) {
                if (slot == playerSlot) {
                    handlePlayerClick(event, slot);
                    return;
                }
            }

            // Other slots are non-interactive in admin mode
            if (slot == 45) {
                handleExitClick(event);
            } else {
                event.setCancelled(true);
            }
        } else {
            // Player management mode: existing behavior
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
    }

    private void handlePlayerClick(InventoryClickEvent event, int slot) {
        // Find which player was clicked based on slot
        java.util.List<Player> onlinePlayers = new java.util.ArrayList<>(org.bukkit.Bukkit.getOnlinePlayers());
        int[] playerSlots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

        for (int i = 0; i < playerSlots.length; i++) {
            if (playerSlots[i] == slot && i < onlinePlayers.size()) {
                Player targetPlayer = onlinePlayers.get(i);
                Player clicker = (Player) event.getWhoClicked();

                // Open player-specific economy management GUI
                org.bukkit.Bukkit.getScheduler().runTask(
                    org.bukkit.Bukkit.getPluginManager().getPlugin("AdminManager"),
                    () -> new EconomyManagerGui(clicker, targetPlayer).open()
                );
                return;
            }
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
        Player clicker = (Player) event.getWhoClicked();

        org.bukkit.Bukkit.getScheduler().runTask(
            org.bukkit.Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> {
                if (adminMode) {
                    // Return to ServerManager
                    new ServerManagerGui(clicker).open();
                } else {
                    // Return to PlayerManage
                    new PlayerManage(clicker, targetPlayer).open();
                }
            }
        );
    }
}
