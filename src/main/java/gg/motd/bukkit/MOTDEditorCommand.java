package gg.motd.bukkit;

import gg.motd.api.MOTD;
import gg.motd.api.SaveResponse;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
            sender.sendMessage(ChatColor.RED + "Failed to save the MOTD to the motd.gg API. Check your log for details.");
            parent.plugin.getLogger().log(Level.SEVERE, "Failed to save the MOTD to the motd.gg API: ", e);
            return true;
        }

        if (!success || parent.plugin.motd == null) {
            sender.sendMessage(ChatColor.RED + "Failed to save the MOTD to the motd.gg API.");
            parent.log(Level.SEVERE, "Failed to save the MOTD to the motd.gg API.");
            return true;
        }

        if (sender instanceof ConsoleCommandSender || !parent.plugin.hasSpigotMethod()) {
            sender.sendMessage(ChatColor.GREEN + "Edit your MOTD here: " + ChatColor.AQUA +
                    parent.plugin.motd.getSessionUrL());
        }
        else {
            TextComponent message = new TextComponent();
            message.setText("Edit your MOTD here: ");
            message.setColor(ChatColor.GREEN.asBungee());

            TextComponent link = new TextComponent();
            link.setColor(ChatColor.AQUA.asBungee());
            link.setText(parent.plugin.motd.getUrl());
            link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, parent.plugin.motd.getSessionUrL()));
            message.addExtra(link);

            sender.spigot().sendMessage(message);
        }

        return true;
    }
}
