package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public class PlayerListGui extends BaseGui {

    private final List<Player> onlinePlayers;

    public PlayerListGui(Player player, int page) {
        super(player, formatTitle(), page); // Titolo dinamico formattato
        this.onlinePlayers = (List<Player>) Bukkit.getOnlinePlayers();
        setupPlayerHeads();
    }

    private static String formatTitle() {
        int onlineCount = Bukkit.getOnlinePlayers().size();
        int maxSlots = Bukkit.getMaxPlayers();
        String baseTitle = TranslationManager.translate("player_list_title");
        return String.format("%s §a§l%d§0§l/%d", baseTitle, onlineCount, maxSlots);
    }

    private void setupPlayerHeads() {
        int startSlot = 0; // Slot iniziale
        int itemsPerPage = 45; // Escludendo l'ultima riga per i pulsanti di navigazione

        int startIndex = (getPage() - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, onlinePlayers.size());

        for (int i = startIndex; i < endIndex; i++) {
            Player target = onlinePlayers.get(i);
            setItem(startSlot++, createPlayerHead(target.getUniqueId(), target.getName()));
        }
    }

    private ItemStack createPlayerHead(UUID uuid, String name) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
            meta.setDisplayName("§a" + name); // Nome del giocatore come titolo
            skull.setItemMeta(meta);
        }
        return skull;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot >= 0 && slot < 45) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.PLAYER_HEAD) {
                Player target = Bukkit.getPlayerExact(clickedItem.getItemMeta().getDisplayName().substring(2));
                if (target != null) {
                    // Azioni da eseguire sul giocatore selezionato
                    event.getWhoClicked().sendMessage("§aHai selezionato " + target.getName());
                }
            }
        }
        event.setCancelled(true);
    }
}