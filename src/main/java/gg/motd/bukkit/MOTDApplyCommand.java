package gg.motd.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MOTDApplyCommand implements CommandExecutor {
    protected final MOTDCommand parent;

    public MOTDApplyCommand(MOTDCommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String id;
        String session = null;
        if (args.length == 0) {
            id = parent.plugin.motd.getId();
            session = parent.plugin.motd.getSession();
        }
        else {
            Matcher matcher = Pattern.compile("(?:https?://motd\\.gg/)?([a-zA-Z0-9]+)(?:\\..*)?")
                    .matcher(args[0]);
            if (!matcher.matches()) {
                sender.sendMessage(ChatColor.RED + "No motd id specified. Use /motdgg apply <url|id>");
                return true;
            }

            id = matcher.group(1);
        }

        try {
            parent.plugin.motd = parent.getPlugin().getClient().getMotd(id);
            parent.plugin.motd.setSession(session);
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "Failed to fetch the MOTD from the motd.gg API. Check your log for details.");
            parent.plugin.getLogger().log(Level.SEVERE, "Failed to fetch the MOTD from the motd.gg API: ", e);
            return true;
        }

        // set live MOTD
        sender.sendMessage(ChatColor.GREEN + "Applied new MOTD.");

        // write MOTD to server.properties
        try {
            Path propertiesPath = Paths.get("server.properties");
            Properties properties = new Properties();
            properties.load(Files.newInputStream(propertiesPath));
            properties.setProperty("motd", parent.plugin.motd.getText());
            properties.store(Files.newOutputStream(propertiesPath), null);
            sender.sendMessage(ChatColor.GREEN + "Saved new MOTD in the server.properties.");
        }
        catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "Failed to write server.properties. Check your log for details.");
            parent.plugin.getLogger().log(Level.SEVERE, "Failed to write server.properties: ", e);
        }

        // write server icon to server-icon.png
        if (parent.plugin.motd.getFavicon() != null) {
            try {
                String b64 = parent.plugin.motd.getFavicon().split(",")[1];
                byte[] imageByte = Base64.getDecoder().decode(b64);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                BufferedImage image = ImageIO.read(bis);
                bis.close();

                // write the image to a file
                ImageIO.write(image, "png", new File("server-icon.png"));
                sender.sendMessage(ChatColor.GREEN + "Wrote new server icon to server-icon.png.");

                // set live server icon
                try {
                    parent.plugin.icon = sender.getServer().loadServerIcon(new File("server-icon.png"));
                    sender.sendMessage(ChatColor.GREEN + "Applied new server icon.");
                }
                catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Failed to load server icon. You will have to restart your server to apply the change. Check your log for details.");
                    parent.plugin.getLogger().log(Level.SEVERE, "Failed to load server icon: ", e);
                }
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Failed to write server-icon.png. Check your log for details.");
                parent.plugin.getLogger().log(Level.SEVERE, "Failed to write server-icon.png: ", e);
            }
        }
        return true;
    }
}
