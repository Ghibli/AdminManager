package it.alessiogta.adminmanager.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Level;

public class GuiUtils {

    public static void fillGuiSlots(Inventory gui, FileConfiguration config) {
        boolean fillSlots = config.getBoolean("fill_empty_slots", true);
        boolean fillLastRowOnly = config.getBoolean("fill_last_row_only", false);
        String panelColor = config.getString("panel_color", "BLACK").toUpperCase();
        String panelColorLastRow = config.getString("panel_color_last_row", "GRAY").toUpperCase();

        Material panelMaterial = Material.getMaterial(panelColor + "_STAINED_GLASS_PANE");
        Material panelMaterialLastRow = Material.getMaterial(panelColorLastRow + "_STAINED_GLASS_PANE");

        if (panelMaterial == null) {
            Bukkit.getLogger().log(Level.WARNING, "[AdminManager] Invalid panel color in config.yml: " + panelColor);
            panelMaterial = Material.BLACK_STAINED_GLASS_PANE;
        }

        if (panelMaterialLastRow == null) {
            Bukkit.getLogger().log(Level.WARNING, "[AdminManager] Invalid panel color for last row in config.yml: " + panelColorLastRow);
            panelMaterialLastRow = Material.GRAY_STAINED_GLASS_PANE;
        }

        // Riempie solo gli slot principali (0-44) se fill_empty_slots è true
        if (fillSlots) {
            ItemStack fillerItem = createFillerItem(panelMaterial);
            for (int i = 0; i < 45; i++) {
                if (gui.getItem(i) == null) {
                    gui.setItem(i, fillerItem);
                }
            }
        }

        // Riempie solo la riga finale (45-53) se fill_last_row_only è true
        if (fillLastRowOnly) {
            ItemStack fillerItemLastRow = createFillerItem(panelMaterialLastRow);
            for (int i = 45; i < 54; i++) {
                if (gui.getItem(i) == null) {
                    gui.setItem(i, fillerItemLastRow);
                }
            }
        }
    }

    private static ItemStack createFillerItem(Material material) {
        ItemStack fillerItem = new ItemStack(material);
        ItemMeta fillerMeta = fillerItem.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            fillerItem.setItemMeta(fillerMeta);
        }
        return fillerItem;
    }
}