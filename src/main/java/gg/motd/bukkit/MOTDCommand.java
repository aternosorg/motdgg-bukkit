package gg.motd.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public class MOTDCommand implements CommandExecutor {
    protected final MOTDGGPlugin plugin;

    public final HashMap<String, CommandExecutor> subCommands = new HashMap<>();

    public MOTDCommand(MOTDGGPlugin plugin) {
        this.plugin = plugin;
        this.registerSubCommands();
    }

    private void registerSubCommands() {
        this.subCommands.put("editor", new MOTDEditorCommand(this));
        this.subCommands.put("apply", new MOTDEditorCommand(this));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("motdgg")) {
            sender.sendMessage(ChatColor.RED + "You don't have the permissions required to execute this command.");
            return true;
        }

        CommandExecutor subCommand = subCommands.get(args[0]);
        if (subCommand == null) {
            return false;
        }

        return subCommand.onCommand(sender, command, s, Arrays.copyOfRange(args, 1, args.length));
    }

    /**
     * log a message
     *
     * @param level   log level
     * @param message log message
     */
    public void log(Level level, String message) {
        plugin.getLogger().log(level, message);
    }

    public MOTDGGPlugin getPlugin() {
        return plugin;
    }
}
