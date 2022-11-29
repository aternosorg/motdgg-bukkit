package gg.motd.bukkit;

import gg.motd.api.MOTD;
import gg.motd.api.SaveResponse;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.logging.Level;

public class MOTDEditorCommand implements CommandExecutor {
    protected MOTDCommand parent;

    public MOTDEditorCommand(MOTDCommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String serverIcon = null;
        try {
            serverIcon = "data:image\\/png;base64," + Base64.getEncoder()
                    .encodeToString(Files.readAllBytes(Paths.get("server-icon.png")));
        }
        catch (IOException ignored) {

        }

        Server server = sender.getServer();
        MOTD motd = new MOTD()
                .setText(server.getMotd())
                .setFavicon(serverIcon);

        boolean success;
        try {
            SaveResponse response = motd.save(parent.plugin.getClient());
            motd = response.getMotd();
            success = response.isSuccess();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "Failed to save the MOTD to the motd.gg API. Check your log for details.");
            parent.plugin.getLogger().log(Level.SEVERE, "Failed to save the MOTD to the motd.gg API: ", e);
            return true;
        }

        if (!success || motd == null) {
            sender.sendMessage(ChatColor.RED + "Failed to save the MOTD to the motd.gg API.");
            parent.log(Level.SEVERE, "Failed to save the MOTD to the motd.gg API.");
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "Edit your MOTD here: " + ChatColor.AQUA +
                "https://motd.gg/" + motd.getId() + "?s=" + motd.getSession());
        return true;
    }
}
