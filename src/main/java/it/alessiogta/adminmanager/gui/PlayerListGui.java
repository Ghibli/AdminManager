package it.alessiogta.adminmanager.gui;

import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerListGui extends BaseGui {

    private final List<Player> onlinePlayers;
    private boolean isClosing = false;

    public PlayerListGui(Player player, int page) {
        super(player, formatTitle(), page); // Usa il metodo statico per il titolo
        this.onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        setupPlayerHeads();
    }

    private static String formatTitle() {
        int onlineCount = Bukkit.getOnlinePlayers().size();
        int maxSlots = Bukkit.getMaxPlayers();
        String baseTitle = TranslationManager.translate("PlayerListGui", "player_list_title", "Giocatori Online");

        // Costruisci la stringa e applica i colori
        String rawTitle = String.format("%s &2&l%d&0-&1&l%d", baseTitle, onlineCount, maxSlots);
        return ChatColor.translateAlternateColorCodes('&', rawTitle);
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

            // Aggiunta del lore
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&',TranslationManager.translate("PlayerListGui", "uuid", "UUID: %uuid%").replace("%uuid%", uuid.toString())));
            lore.add(ChatColor.translateAlternateColorCodes('&',TranslationManager.translate("PlayerListGui", "ping", "Ping: %ping% ms").replace("%ping%" , getPing(name))));
            lore.add(ChatColor.translateAlternateColorCodes('&',TranslationManager.translate("PlayerListGui", "world","Mondo: %world%").replace("%world%", Bukkit.getWorlds().get(0).getName())));
            lore.add(ChatColor.translateAlternateColorCodes('&',TranslationManager.translate("PlayerListGui","ip","IP: %ip%").replace("%ip%",getPlayerIP(name))));
            lore.add(ChatColor.translateAlternateColorCodes('&',TranslationManager.translate("PlayerListGui","coordinates" , "Coordinate: %coordinates%").replace("%coordinates%" , getPlayerCoordinates(name))));
            lore.add(ChatColor.translateAlternateColorCodes('&',TranslationManager.translate("PlayerListGui" , "action" , "Clicca per Gestire")));
            meta.setLore(lore);

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
                    new PlayerManage((Player) event.getWhoClicked(), target).open();
                }
            }
        }

        // Gestisce il pulsante "Chiudi" (slot 49)
        if (slot == 49) {
            if (event.getWhoClicked().getOpenInventory().getType() != InventoryType.CHEST) {
                return;
            }
            if (isClosing) {
                return; // Evita esecuzioni multiple
            }
            isClosing = true;
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage(TranslationManager.translate("PlayerListGui", "exit_message", "&a Hai chiuso &6&lAdmin Manager :-)"));
            return;
        }
        event.setCancelled(true);
    }

    private String getPing(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            // Usa Reflection o API del server per ottenere il ping
            return String.valueOf(player.getPing());
        }
        return "N/A";
    }

    private String getPlayerWorld(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            return player.getWorld().getName();
        }
        return "N/A";
    }

    private String getPlayerIP(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null && player.getAddress() != null) {
            return player.getAddress().getAddress().getHostAddress();
        }
        return "N/A";
    }

    private String getPlayerCoordinates(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            return "X: " + player.getLocation().getBlockX() + ", " +
                   "Y: " + player.getLocation().getBlockY() + ", " +
                   "Z: " + player.getLocation().getBlockZ();
        }
        return "N/A";
    }
}