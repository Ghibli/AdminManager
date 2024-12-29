package it.alessiogta.adminmanager.commands;

import it.alessiogta.adminmanager.gui.PlayerListGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminManagerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            new PlayerListGui(player, 1).open();
        } else {
            sender.sendMessage("§cSolo i giocatori possono usare questo comando.");
        }
        return true;
    }
}