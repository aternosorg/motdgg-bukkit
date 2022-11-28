package gg.motd.bukkit;

import gg.motd.api.MOTD;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class MOTDApplyCommand implements CommandExecutor {
    protected MOTDCommand parent;

    public MOTDApplyCommand(MOTDCommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String id = Pattern.compile("(?:https?://motd\\.gg/)?([a-zA-Z0-9]+)(?:\\..*)?").matcher(args[0]).group(0);
        if (id == null) {
            return false;
        }

        MOTD motd;
        try {
            motd = parent.getPlugin().getClient().getMotd(id);
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "Failed to fetch the MOTD from the motd.gg API. Check your log for details.");
            parent.plugin.getLogger().log(Level.SEVERE, "Failed to fetch the MOTD from the motd.gg API: ", e);
            return true;
        }

        // TODO: set live MOTD
        // TODO: set live server icon?
        // apparently I need to override the ServerListPingEvent

        // write MOTD to server.properties
        try {
            Path propertiesPath = Paths.get("server.properties");
            Properties properties = new Properties();
            properties.load(Files.newInputStream(propertiesPath));
            properties.setProperty("motd", motd.getText());
            properties.store(Files.newOutputStream(propertiesPath), null);
        }
        catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "Failed to write server.properties. Check your log for details.");
            parent.plugin.getLogger().log(Level.SEVERE, "Failed to write server.properties: ", e);
            return true;
        }

        // TODO: write server icon to server-icon.png?

        return true;
    }
}
