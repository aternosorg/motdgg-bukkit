package gg.motd.bukkit;

import gg.motd.api.MOTD;
import gg.motd.api.SaveResponse;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.logging.Level;

public class MOTDEditorCommand implements CommandExecutor {
    protected final MOTDCommand parent;

    public MOTDEditorCommand(MOTDCommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        String serverIcon = null;
        try {
            serverIcon = "data:image\\/png;base64," + Base64.getEncoder()
                    .encodeToString(Files.readAllBytes(Paths.get("server-icon.png")));
        }
        catch (IOException ignored) {

        }

        Server server = sender.getServer();
        if (parent.plugin.motd == null) {
            parent.plugin.motd = new MOTD()
                    .setText(server.getMotd())
                    .setFavicon(serverIcon);
        }

        boolean success;
        try {
            SaveResponse response = parent.plugin.motd.save(parent.plugin.getClient());
            parent.plugin.motd = response.getMotd();
            success = response.isSuccess();
        } catch (IOException e) {
            parent.plugin.adventure().sender(sender).sendMessage(Component
                    .text("Failed to save the MOTD to the motd.gg API. Check your log for details.")
                    .color(NamedTextColor.RED)
            );
            parent.plugin.getLogger().log(Level.SEVERE, "Failed to save the MOTD to the motd.gg API: ", e);
            return true;
        }

        if (!success || parent.plugin.motd == null) {
            parent.plugin.adventure().sender(sender).sendMessage(Component
                    .text("Failed to save the MOTD to the motd.gg API.")
                    .color(NamedTextColor.RED)
            );
            parent.log(Level.SEVERE, "Failed to save the MOTD to the motd.gg API.");
            return true;
        }

        Component message = Component.empty();
        message = message.append(Component.text("Edit your MOTD here: "));
        message = message.append(Component
                        .text(parent.plugin.motd.getUrl())
                        .color(NamedTextColor.AQUA)
                        .clickEvent(ClickEvent.openUrl(parent.plugin.motd.getSessionUrL()))
        );
        parent.plugin.adventure().sender(sender).sendMessage(message);
        return true;
    }
}
