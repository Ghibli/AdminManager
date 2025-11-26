package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class PlayerEnderChestGui extends BaseGui {

    private final Player targetPlayer;

    public PlayerEnderChestGui(Player player, Player targetPlayer) {
        super(player, "§5§lEnderChest §8- §e" + targetPlayer.getName(), 1);
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
        ItemStack item = new ItemStack(Material.DARK_OAK_DOOR);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c§lBack");
            meta.setLore(Arrays.asList("§7Return to Player Management"));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createClearButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§5§lClear EnderChest");
            meta.setLore(Arrays.asList(
                "§7Clear all items from",
                "§e" + targetPlayer.getName() + "§7's enderchest",
                "§7",
                "§cThis cannot be undone!"
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createRefreshButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§a§lRefresh");
            meta.setLore(Arrays.asList("§7Update enderchest view"));
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
            clicker.sendMessage("§eThis is a view-only enderchest. Use the clear button to remove items.");
        } else {
            event.setCancelled(true);
        }
    }
}
