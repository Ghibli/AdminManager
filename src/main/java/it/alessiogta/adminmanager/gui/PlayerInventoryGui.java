package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class PlayerInventoryGui extends BaseGui {

    private final Player admin;
    private final Player targetPlayer;

    public PlayerInventoryGui(Player player, Player targetPlayer) {
        super(player, "§9§lInventory §8- §e" + targetPlayer.getName(), 1);
        this.admin = player;
        this.targetPlayer = targetPlayer;
    }

    @Override
    public Inventory build() {
        // Create a 6-row inventory (54 slots)
        Inventory inv = Bukkit.createInventory(null, 54, "§9§lInventory §8- §e" + targetPlayer.getName());

        // Copy player's inventory (slots 0-35) and armor (36-39)
        ItemStack[] contents = targetPlayer.getInventory().getContents();

        // Main inventory (slots 9-35 in player inventory = slots 9-35 in GUI)
        for (int i = 9; i < 36; i++) {
            if (contents[i] != null) {
                inv.setItem(i, contents[i].clone());
            }
        }

        // Hotbar (slots 0-8 in player inventory = slots 36-44 in GUI)
        for (int i = 0; i < 9; i++) {
            if (contents[i] != null) {
                inv.setItem(36 + i, contents[i].clone());
            }
        }

        // Armor slots (helmet, chest, legs, boots) - show in top row
        ItemStack[] armor = targetPlayer.getInventory().getArmorContents();
        if (armor[3] != null) inv.setItem(0, armor[3].clone()); // Helmet
        if (armor[2] != null) inv.setItem(1, armor[2].clone()); // Chestplate
        if (armor[1] != null) inv.setItem(2, armor[1].clone()); // Leggings
        if (armor[0] != null) inv.setItem(3, armor[0].clone()); // Boots

        // Offhand
        if (contents[40] != null) inv.setItem(4, contents[40].clone());

        // Bottom row: Control buttons
        inv.setItem(45, createBackButton());
        inv.setItem(49, createClearButton());
        inv.setItem(53, createRefreshButton());

        return inv;
    }

    private ItemStack createBackButton() {
        String title = TranslationManager.translate("PlayerManage", "inventory_back_button_title", "&c&lBack");
        String lore = TranslationManager.translate("PlayerManage", "inventory_back_button_lore", "&7Return to Player Management");

        ItemStack item = new ItemStack(Material.DARK_OAK_DOOR);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', title));
            meta.setLore(Arrays.asList(org.bukkit.ChatColor.translateAlternateColorCodes('&', lore)));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createClearButton() {
        String title = TranslationManager.translate("PlayerManage", "inventory_clear_button_title", "&c&lClear Inventory");
        String loreText = TranslationManager.translate("PlayerManage", "inventory_clear_button_lore",
            "&7Clear all items from\n&e{player}&7's inventory\n&7\n&cThis cannot be undone!")
            .replace("{player}", targetPlayer.getName());

        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', title));

            String[] loreLines = loreText.split("\n");
            java.util.List<String> loreList = new java.util.ArrayList<>();
            for (String line : loreLines) {
                loreList.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(loreList);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createRefreshButton() {
        String title = TranslationManager.translate("PlayerManage", "inventory_refresh_button_title", "&a&lRefresh");
        String lore = TranslationManager.translate("PlayerManage", "inventory_refresh_button_lore", "&7Update inventory view");

        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', title));
            meta.setLore(Arrays.asList(org.bukkit.ChatColor.translateAlternateColorCodes('&', lore)));
            item.setItemMeta(meta);
        }

        return item;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        // Control buttons
        if (slot == 45) {
            // Back button - don't close inventory to preserve cursor position
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new PlayerManage(clicker, targetPlayer).open()
            );
        } else if (slot == 49) {
            // Clear button
            event.setCancelled(true);
            targetPlayer.getInventory().clear();

            String message = TranslationManager.translate("PlayerManage", "clear_inventory_message",
                "§cCleared inventory of {player}").replace("{player}", targetPlayer.getName());
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

            if (targetPlayer.isOnline()) {
                String playerMessage = TranslationManager.translate("PlayerManage", "inventory_cleared_notification",
                    "§cYour inventory has been cleared!");
                targetPlayer.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', playerMessage));
            }

            // Close current inventory to properly unregister listener
            clicker.closeInventory();

            // Refresh view
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new PlayerInventoryGui(clicker, targetPlayer).open()
            );
        } else if (slot == 53) {
            // Refresh button
            event.setCancelled(true);

            // Close current inventory to properly unregister listener
            clicker.closeInventory();

            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new PlayerInventoryGui(clicker, targetPlayer).open()
            );
        } else if (slot < 45) {
            // Allow interaction with inventory items
            // Items will be modified in the display inventory, not the actual player inventory
            // To actually modify player inventory, we'd need to sync on close
            event.setCancelled(true);

            String message = TranslationManager.translate("PlayerManage", "inventory_view_only_message",
                "&eThis is a view-only inventory. Use the clear button to remove items.");
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
        } else {
            event.setCancelled(true);
        }
    }

    @Override
    public void open() {
        // Re-register event listener if it was unregistered
        try {
            HandlerList.unregisterAll(this);
            Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("AdminManager"));
        } catch (Exception e) {
            // Already registered or other error - continue
        }

        // Build and open inventory
        inventory = build();
        admin.openInventory(inventory);
    }
}
