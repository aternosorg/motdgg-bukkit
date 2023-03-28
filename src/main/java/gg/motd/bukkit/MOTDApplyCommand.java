package gg.motd.bukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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
                parent.plugin.adventure().sender(sender).sendMessage(Component
                        .text("No MOTD id specified. Use /motdgg apply <url|id>")
                        .color(NamedTextColor.RED)
                );
                return true;
            }

            id = matcher.group(1);
        }

        parent.plugin.adventure().sender(sender).sendMessage(Component.empty()
                .append(Component.text("Applying MOTD "))
                .append(Component
                        .text("https://motd.gg/" + id)
                        .color(NamedTextColor.AQUA)
                        .clickEvent(ClickEvent.openUrl("https://motd.gg/" + id))
                )
        );

        try {
            // set live MOTD
            parent.plugin.motd = parent.getPlugin().getClient().getMotd(id);
            parent.plugin.motd.setSession(session);
        } catch (IOException e) {
            parent.plugin.adventure().sender(sender).sendMessage(Component
                    .text("Failed to fetch the MOTD from the motd.gg API. Check your log for details.")
                    .color(NamedTextColor.RED)
            );
            parent.plugin.getLogger().log(Level.SEVERE, "Failed to fetch the MOTD from the motd.gg API: ", e);
            return true;
        }

        boolean success = true;
        // write MOTD to server.properties
        try {
            Path propertiesPath = Paths.get("server.properties");
            Properties properties = new Properties();
            properties.load(Files.newInputStream(propertiesPath));
            properties.setProperty("motd", parent.plugin.motd.getText());
            properties.store(Files.newOutputStream(propertiesPath), null);
        }
        catch (IOException e) {
            parent.plugin.adventure().sender(sender).sendMessage(Component
                    .text("Failed to write server.properties. Check your log for details.")
                    .color(NamedTextColor.RED)
            );
            parent.plugin.getLogger().log(Level.SEVERE, "Failed to write server.properties: ", e);
            success = false;
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

                // set live server icon
                try {
                    parent.plugin.icon = sender.getServer().loadServerIcon(new File("server-icon.png"));
                }
                catch (Exception e) {
                    parent.plugin.adventure().sender(sender).sendMessage(Component
                            .text("Failed to load server icon. You will have to restart your server to apply the change. " +
                                    "Check your log for details.")
                            .color(NamedTextColor.RED)
                    );
                    parent.plugin.getLogger().log(Level.SEVERE, "Failed to load server icon: ", e);
                    success = false;
                }
            } catch (IOException e) {
                parent.plugin.adventure().sender(sender).sendMessage(Component
                        .text("Failed to write server-icon.png. Check your log for details.")
                        .color(NamedTextColor.RED)
                );
                parent.plugin.getLogger().log(Level.SEVERE, "Failed to write server-icon.png: ", e);
                success = false;
            }
        }

        if (success) {
            parent.plugin.adventure().sender(sender).sendMessage(Component
                    .text("Applied new MOTD and server icon.")
                    .color(NamedTextColor.GREEN)
            );
        }

        return true;
    }
}
