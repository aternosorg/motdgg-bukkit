package gg.motd.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MOTDCommand implements CommandExecutor, TabExecutor {
    protected final MOTDGGPlugin plugin;

    public final HashMap<String, CommandExecutor> subCommands = new HashMap<>();

    public MOTDCommand(MOTDGGPlugin plugin) {
        this.plugin = plugin;
        this.registerSubCommands();
    }

    private void registerSubCommands() {
        this.subCommands.put("editor", new MOTDEditorCommand(this));
        this.subCommands.put("apply", new MOTDApplyCommand(this));
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!sender.hasPermission("motdgg")) {
            sender.sendMessage(ChatColor.RED + "You don't have the permissions required to execute this command.");
            return true;
        }

        CommandExecutor subCommand = args.length == 0 ? null : subCommands.get(args[0]);
        if (subCommand == null) {
            return false;
        }

        return subCommand.onCommand(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
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

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return subCommands.keySet()
                    .stream()
                    .filter(arg -> arg.contains(args[0]))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
