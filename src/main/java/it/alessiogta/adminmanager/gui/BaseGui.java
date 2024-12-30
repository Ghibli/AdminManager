package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.GuiUtils;
import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseGui implements Listener {

    private final String title;
    private final int size = 54;
    private final Map<Integer, ItemStack> items = new HashMap<>();
    private final int page;
    private final Player player;

    public BaseGui(Player player, String title, int page) {
        this.player = player;
        this.title = title;
        this.page = page;

        // Registra l'evento per rendere le icone immobili
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("AdminManager"));

        // Aggiungi pulsanti di navigazione
        setupNavigationButtons();
    }

    public int getPage() {
        return this.page;
    }

    private void setupNavigationButtons() {
        int totalItems = items.size();
        int itemsPerPage = 45;

        // Mostra il pulsante indietro solo se c'è una pagina precedente
        if (page > 1) {
            items.put(47, createItem(Material.PAPER, TranslationManager.translate("PlayerListGui", "previous_page", "Pagina precedente")));
        }

        // Mostra il pulsante avanti solo se ci sono più elementi
        if (totalItems > page * itemsPerPage) {
            items.put(51, createItem(Material.PAPER, TranslationManager.translate("PlayerListGui", "next_page", "Pagina successiva")));
        }

        // Pulsante di uscita
        items.put(49, createItem(Material.DARK_OAK_DOOR, TranslationManager.translate("PlayerListGui", "exit", "Esci")));
    }


    public Inventory build() {
        Inventory inventory = Bukkit.createInventory(null, size, title);

        // Aggiungi tutti gli oggetti
        items.forEach(inventory::setItem);

        // Riempimento dei pannelli decorativi
        GuiUtils.fillGuiSlots(inventory, Bukkit.getPluginManager().getPlugin("AdminManager").getConfig());

        return inventory;
    }

    public void open() {
        player.openInventory(build());
    }

    protected void setItem(int slot, ItemStack itemStack) {
        items.put(slot, itemStack);
    }

    protected ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(title) && event.getWhoClicked() == player) {
            event.setCancelled(true); // Rende le icone immobili
            handleClick(event); // Permette di gestire i clic personalizzati
        }
    }

    public abstract void handleClick(InventoryClickEvent event);

    protected ItemStack createItem(Material material, String title, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(title);
            if (lore != null) {
                meta.setLore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

}
