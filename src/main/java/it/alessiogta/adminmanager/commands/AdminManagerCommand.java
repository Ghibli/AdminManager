package it.alessiogta.adminmanager.commands;

import it.alessiogta.adminmanager.AdminManager;
import it.alessiogta.adminmanager.gui.PlayerListGui;
import it.alessiogta.adminmanager.utils.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AdminManagerCommand implements CommandExecutor {

    private final FileConfiguration config;

    public AdminManagerCommand(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Controllo permission base per tutti i comandi
        if (!sender.hasPermission("adminmanager.use")) {
            sender.sendMessage("§cNon hai il permesso per usare questo comando.");
            return true;
        }

        if (args.length == 0) {
            // Comando base /adminm (apre la GUI per i giocatori)
            if (sender instanceof Player) {
                Player player = (Player) sender;
                new PlayerListGui(player, 1).open();
                return true;
            } else {
                sender.sendMessage("§cSolo i giocatori possono usare questo comando.");
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("reload")) {
            String reloadPermission = config.getString("permissions.reload", "adminmanager.reload");

            if (!sender.hasPermission(reloadPermission)) {
                sender.sendMessage("§cNon hai il permesso per ricaricare il plugin.");
                return true;
            }

            AdminManager plugin = (AdminManager) Bukkit.getPluginManager().getPlugin("AdminManager");

            if (plugin != null) {
                plugin.reloadConfig(); // Ricarica il file di configurazione

                // Ricarica i file di traduzione
                String language = plugin.getConfig().getString("language", "en_EN");
                TranslationManager.loadTranslations(language);

                sender.sendMessage("§aConfigurazione e file di traduzione ricaricati con successo.");
            } else {
                sender.sendMessage("§cImpossibile ricaricare il plugin.");
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("info")) {
            AdminManager plugin = (AdminManager) Bukkit.getPluginManager().getPlugin("AdminManager");
            String version = plugin != null ? plugin.getDescription().getVersion() : "Unknown";

            sender.sendMessage("§8§m------------------------------------");
            sender.sendMessage("");
            sender.sendMessage(" §6§lAdmin Manager §7v" + version);
            sender.sendMessage(" §7Made with §c❤ §7by §fAlessioGTA");
            sender.sendMessage("");
            sender.sendMessage("§8§m------------------------------------");

            return true;
        }

        sender.sendMessage("§cArgomento non riconosciuto. Usa: /adminm [reload|info]");
        return false;
    }
}