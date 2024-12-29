package it.alessiogta.adminmanager.gui;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class GuiManager {

    private static final Map<String, BaseGui> guiMap = new HashMap<>();

    /**
     * Registra una GUI nel manager.
     * @param identifier Identificativo univoco della GUI.
     * @param gui La GUI da registrare.
     */
    public static void registerGui(String identifier, BaseGui gui) {
        guiMap.put(identifier, gui);
    }

    /**
     * Ottiene una GUI registrata.
     * @param identifier Identificativo della GUI.
     * @return La GUI registrata o null se non trovata.
     */
    public static BaseGui getGui(String identifier) {
        return guiMap.get(identifier);
    }

    /**
     * Crea e apre una GUI per un giocatore.
     * @param identifier Identificativo della GUI.
     * @param player Giocatore a cui mostrare la GUI.
     * @param page Numero di pagina della GUI.
     */
    public static void openGui(String identifier, Player player, int page) {
        BaseGui gui = guiMap.get(identifier);
        if (gui != null) {
            gui.open();
        } else {
            player.sendMessage("§cGUI non trovata: " + identifier);
        }
    }

    /**
     * Rimuove una GUI dal manager.
     * @param identifier Identificativo della GUI.
     */
    public static void unregisterGui(String identifier) {
        guiMap.remove(identifier);
    }
}
