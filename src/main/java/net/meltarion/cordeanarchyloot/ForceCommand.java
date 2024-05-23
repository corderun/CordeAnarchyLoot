package net.meltarion.cordeanarchyloot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.TimeZone;

public class ForceCommand implements CommandExecutor {
    private final CordeAnarchyLoot plugin;

    public ForceCommand(CordeAnarchyLoot plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        } else if (!sender.hasPermission("cordeanarchyloot.admin")) {
            sender.sendMessage("§c§lMeltarion §fКоманда не найдена или у вас нет прав.");
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("respawn")) {
            this.plugin.respawn();
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            this.plugin.reloadConfig();
            sender.sendMessage("Конфиг перезагружен");
            return true;
        } else if (args.length != 0) {
            sender.sendMessage("§c§lMeltarion §fКоманда не найдена или у вас нет прав.");
            return true;
        } else if (args.length == 0) {
            sender.sendMessage("§c§lMeltarion §fКоманда не найдена или у вас нет прав.");
            return true;
        } else {
            return true;
        }
    }
}
