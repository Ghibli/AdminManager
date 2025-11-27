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

public class PlayerEnderChestGui extends BaseGui {

    private final Player admin;
    private final Player targetPlayer;

    public PlayerEnderChestGui(Player player, Player targetPlayer) {
        super(player, "§5§lEnderChest §8- §e" + targetPlayer.getName(), 1);
        this.admin = player;
        this.targetPlayer = targetPlayer;
    }

    @Override
    public Inventory build() {
        // Create a 4-row inventory (36 slots total: 27 for enderchest + 9 for controls)
        Inventory inv = Bukkit.createInventory(null, 36, "§5§lEnderChest §8- §e" + targetPlayer.getName());

        // Copy enderchest contents (27 slots)
        ItemStack[] contents = targetPlayer.getEnderChest().getContents();
        for (int i = 0; i < 27; i++) {
            if (contents[i] != null) {
                inv.setItem(i, contents[i].clone());
            }
        }

        // Bottom row: Control buttons
        inv.setItem(27, createBackButton());
        inv.setItem(31, createClearButton());
        inv.setItem(35, createRefreshButton());

        return inv;
    }

    private ItemStack createBackButton() {
        String title = TranslationManager.translate("PlayerManage", "enderchest_back_button_title", "&c&lBack");
        String lore = TranslationManager.translate("PlayerManage", "enderchest_back_button_lore", "&7Return to Player Management");

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
        String title = TranslationManager.translate("PlayerManage", "enderchest_clear_button_title", "&5&lClear EnderChest");
        String loreText = TranslationManager.translate("PlayerManage", "enderchest_clear_button_lore",
            "&7Clear all items from\n&e{player}&7's enderchest\n&7\n&cThis cannot be undone!")
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
        String title = TranslationManager.translate("PlayerManage", "enderchest_refresh_button_title", "&a&lRefresh");
        String lore = TranslationManager.translate("PlayerManage", "enderchest_refresh_button_lore", "&7Update enderchest view");

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
        if (slot == 27) {
            // Back button - don't close inventory to preserve cursor position
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new PlayerManage(clicker, targetPlayer).open()
            );
        } else if (slot == 31) {
            // Clear button
            event.setCancelled(true);
            targetPlayer.getEnderChest().clear();

            String message = TranslationManager.translate("PlayerManage", "clear_enderchest_message",
                "§5Cleared enderchest of {player}").replace("{player}", targetPlayer.getName());
            clicker.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

            if (targetPlayer.isOnline()) {
                String playerMessage = TranslationManager.translate("PlayerManage", "enderchest_cleared_notification",
                    "§5Your enderchest has been cleared!");
                targetPlayer.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', playerMessage));
            }

            // Refresh view - don't close inventory to preserve cursor position
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new PlayerEnderChestGui(clicker, targetPlayer).open()
            );
        } else if (slot == 35) {
            // Refresh button - don't close inventory to preserve cursor position
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new PlayerEnderChestGui(clicker, targetPlayer).open()
            );
        } else if (slot < 27) {
            // Allow interaction with enderchest items
            event.setCancelled(true);

            String message = TranslationManager.translate("PlayerManage", "enderchest_view_only_message",
                "&eThis is a view-only enderchest. Use the clear button to remove items.");
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
