package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseGui {

    private final String title;
    private final int size = 54;
    private final Map<Integer, ItemStack> items = new HashMap<>();
    private final int page;
    private final Player player;

    public int getPage() {
        return this.page;
    }

    public BaseGui(Player player, String title, int page) {
        this.player = player;
        this.title = title;
        this.page = page;

        // Aggiungi pulsanti di navigazione e uscita
        setupNavigationButtons();
    }

    private void setupNavigationButtons() {
        // Pulsante di uscita
        items.put(49, createItem(Material.DARK_OAK_DOOR, TranslationManager.translate("exit")));

        // Pulsante per tornare indietro
        if (page > 1) {
            items.put(47, createItem(Material.PAPER, TranslationManager.translate("previous_page")));
        }

        // Pulsante per andare avanti
        items.put(51, createItem(Material.PAPER, TranslationManager.translate("next_page")));
    }

    public Inventory build() {
        Inventory inventory = Bukkit.createInventory(null, size, title);

        // Aggiungi tutti gli oggetti
        items.forEach(inventory::setItem);

        return inventory;
    }

    public void open() {
        player.openInventory(build());
    }

    protected void setItem(int slot, ItemStack itemStack) {
        items.put(slot, itemStack);
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public abstract void handleClick(InventoryClickEvent event);
}