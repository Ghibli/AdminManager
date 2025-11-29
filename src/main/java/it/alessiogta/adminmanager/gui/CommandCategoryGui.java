package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CommandCategoryGui extends BaseGui {

    private final Player admin;
    private final Map<Integer, CommandCategory> slotToCategory = new HashMap<>();

    public CommandCategoryGui(Player admin) {
        super(admin, TranslationManager.translate("CommandCategory", "title", "&6&lGestione Comandi"), 1);
        this.admin = admin;
        setupGuiItems();
    }

    @Override
    protected void setupNavigationButtons() {
        // Disable BaseGui's automatic navigation buttons
    }

    private void setupGuiItems() {
        // Layout:Categorie disposte in modo ordinato
        // Row 2: Admin, Player, Teleport
        // Row 3: World, Economy, Utility
        // Row 4: Altri
        // Bottom: Back button

        int[] slots = {10, 12, 14, 19, 21, 23, 31};
        CommandCategory[] categories = CommandCategory.values();

        for (int i = 0; i < categories.length && i < slots.length; i++) {
            CommandCategory category = categories[i];
            int slot = slots[i];
            slotToCategory.put(slot, category);
            setItem(slot, createCategoryButton(category));
        }

        // Back button (slot 49)
        setItem(49, createBackButton());
    }

    private ItemStack createCategoryButton(CommandCategory category) {
        String title = TranslationManager.translate("CommandCategory", "category_" + category.name().toLowerCase() + "_title",
            "&6&l" + category.getDisplayName());

        String lore = TranslationManager.translate("CommandCategory", "category_" + category.name().toLowerCase() + "_lore",
            "&7Gestisci i comandi di tipo\n&e" + category.getDisplayName() + "\n\n&e&lCLICK: &7Apri categoria");

        return createItem(category.getIcon(), title, lore.split("\n"));
    }

    private ItemStack createBackButton() {
        String title = TranslationManager.translate("CommandCategory", "back_button_title", "&cIndietro");
        String lore = TranslationManager.translate("CommandCategory", "back_button_lore", "&7Torna a Server Manager");
        return createItem(Material.DARK_OAK_DOOR, title, lore);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        Player clicker = (Player) event.getWhoClicked();

        if (slot == 49) {
            // Back button
            handleBack(clicker);
            return;
        }

        // Check if clicked slot has a category
        if (slotToCategory.containsKey(slot)) {
            CommandCategory category = slotToCategory.get(slot);
            handleCategoryClick(category, clicker);
        }
    }

    private void handleCategoryClick(CommandCategory category, Player clicker) {
        // Deregister listener and open CommandRegistrationGui filtered by category
        org.bukkit.event.HandlerList.unregisterAll(this);
        clicker.closeInventory();
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new CommandRegistrationGui(clicker, category, 1).open()
        );
    }

    private void handleBack(Player clicker) {
        // Deregister listener and return to ServerManagerGui
        org.bukkit.event.HandlerList.unregisterAll(this);
        clicker.closeInventory();
        Bukkit.getScheduler().runTask(
            Bukkit.getPluginManager().getPlugin("AdminManager"),
            () -> new ServerManagerGui(clicker).open()
        );
    }

    @Override
    public void open() {
        inventory = build();
        admin.openInventory(inventory);
    }
}
