package it.alessiogta.adminmanager.commands;

import it.alessiogta.adminmanager.AdminManager;
import it.alessiogta.adminmanager.gui.PlayerListGui;
import it.alessiogta.adminmanager.utils.MuteManager;
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

                // Ricarica il MuteManager
                MuteManager.reload();

                sender.sendMessage("§aConfigurazione e file di traduzione ricaricati con successo.");
            } else {
                sender.sendMessage("§cImpossibile ricaricare il plugin.");
            }

            return true;
        }

        sender.sendMessage("§cArgomento non riconosciuto. Usa: /adminm reload");
        return false;
    }
}