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
        // Provider info (center top)
        setItem(4, createProviderInfoButton());

        // Row 2: Main statistics
        setItem(10, createTotalMoneyButton());        // Total money in circulation
        setItem(12, createAverageBalanceButton());     // Average balance
        setItem(14, createMedianBalanceButton());      // Median balance
        setItem(16, createRangeButton());              // Min → Max range

        // Row 3: Player extremes
        setItem(20, createTopPlayerButton());          // Richest player
        setItem(24, createBottomPlayerButton());       // Poorest player

        // Row 4: Distribution analysis
        setItem(31, createWealthDistributionButton()); // Rich/Middle/Poor counts
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

    private ItemStack createMedianBalanceButton() {
        java.util.List<Double> balances = new java.util.ArrayList<>();

        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            balances.add(EconomyManager.getBalance(player));
        }

        double median = 0;
        if (!balances.isEmpty()) {
            java.util.Collections.sort(balances);
            int size = balances.size();
            if (size % 2 == 0) {
                median = (balances.get(size / 2 - 1) + balances.get(size / 2)) / 2;
            } else {
                median = balances.get(size / 2);
            }
        }

        String title = "&6&lMediana Balance";
        String lore = "&7Mediana: &e" + EconomyManager.format(median) + "\n&7Valore centrale più rappresentativo";
        return createItem(Material.EMERALD, title, lore.split("\n"));
    }

    private ItemStack createRangeButton() {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        int count = 0;

        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            double balance = EconomyManager.getBalance(player);
            if (balance < min) min = balance;
            if (balance > max) max = balance;
            count++;
        }

        if (count == 0) {
            min = 0;
            max = 0;
        }

        String title = "&6&lRange Balance";
        String lore = "&7Min: &c" + EconomyManager.format(min) + "\n&7Max: &a" + EconomyManager.format(max);
        return createItem(Material.COMPASS, title, lore.split("\n"));
    }

    private ItemStack createTopPlayerButton() {
        Player topPlayer = null;
        double topBalance = Double.MIN_VALUE;

        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            double balance = EconomyManager.getBalance(player);
            if (balance > topBalance) {
                topBalance = balance;
                topPlayer = player;
            }
        }

        if (topPlayer == null) {
            String title = "&6&lPlayer più Ricco";
            String lore = "&7Nessun player online";
            return createItem(Material.GOLD_INGOT, title, lore);
        }

        // Use player head with real texture
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        org.bukkit.inventory.meta.SkullMeta meta = (org.bukkit.inventory.meta.SkullMeta) playerHead.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(topPlayer);
            meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                "&6&l👑 " + topPlayer.getName()));

            java.util.List<String> loreList = new java.util.ArrayList<>();
            loreList.add(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                "&7Balance: &a" + EconomyManager.format(topBalance)));
            loreList.add("");
            loreList.add(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                "&ePlayer più ricco del server"));
            meta.setLore(loreList);

            playerHead.setItemMeta(meta);
        }

        return playerHead;
    }

    private ItemStack createBottomPlayerButton() {
        Player bottomPlayer = null;
        double bottomBalance = Double.MAX_VALUE;

        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            double balance = EconomyManager.getBalance(player);
            if (balance < bottomBalance) {
                bottomBalance = balance;
                bottomPlayer = player;
            }
        }

        if (bottomPlayer == null) {
            String title = "&6&lPlayer più Povero";
            String lore = "&7Nessun player online";
            return createItem(Material.IRON_NUGGET, title, lore);
        }

        // Use player head with real texture
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        org.bukkit.inventory.meta.SkullMeta meta = (org.bukkit.inventory.meta.SkullMeta) playerHead.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(bottomPlayer);
            meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                "&7" + bottomPlayer.getName()));

            java.util.List<String> loreList = new java.util.ArrayList<>();
            loreList.add(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                "&7Balance: &c" + EconomyManager.format(bottomBalance)));
            loreList.add("");
            loreList.add(org.bukkit.ChatColor.translateAlternateColorCodes('&',
                "&ePlayer più povero del server"));
            meta.setLore(loreList);

            playerHead.setItemMeta(meta);
        }

        return playerHead;
    }

    private ItemStack createWealthDistributionButton() {
        int rich = 0;      // > 10000
        int middle = 0;    // 1000 - 10000
        int poor = 0;      // < 1000
        int negative = 0;  // < 0

        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            double balance = EconomyManager.getBalance(player);
            if (balance < 0) {
                negative++;
            } else if (balance < 1000) {
                poor++;
            } else if (balance < 10000) {
                middle++;
            } else {
                rich++;
            }
        }

        String title = "&6&lDistribuzione Ricchezza";
        java.util.List<String> loreLines = new java.util.ArrayList<>();
        loreLines.add("&a▲ Ricchi (>10k): &e" + rich);
        loreLines.add("&e■ Medi (1k-10k): &e" + middle);
        loreLines.add("&c▼ Poveri (<1k): &e" + poor);
        if (negative > 0) {
            loreLines.add("&4✖ Negativi: &e" + negative);
        }

        String lore = String.join("\n", loreLines);
        return createItem(Material.BOOK, title, lore.split("\n"));
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
            // Admin mode: all statistics are non-interactive (display only)
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
