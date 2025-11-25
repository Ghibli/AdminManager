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

public class PlayerInventoryGui extends BaseGui {

    private final Player targetPlayer;

    public PlayerInventoryGui(Player player, Player targetPlayer) {
        super(player, "§9§lInventory §8- §e" + targetPlayer.getName(), 1);
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
            meta.setDisplayName("§c§lClear Inventory");
            meta.setLore(Arrays.asList(
                "§7Clear all items from",
                "§e" + targetPlayer.getName() + "§7's inventory",
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
            meta.setLore(Arrays.asList("§7Update inventory view"));
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
            // Back button
            event.setCancelled(true);
            clicker.closeInventory();
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

            // Refresh view
            clicker.closeInventory();
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AdminManager"),
                () -> new PlayerInventoryGui(clicker, targetPlayer).open()
            );
        } else if (slot == 53) {
            // Refresh button
            event.setCancelled(true);
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
            clicker.sendMessage("§eThis is a view-only inventory. Use the clear button to remove items.");
        } else {
            event.setCancelled(true);
        }
    }
}
