package gg.motd.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPingEventHandler implements Listener {
    protected final MOTDGGPlugin plugin;

    ServerListPingEventHandler(MOTDGGPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void handleServerPing(ServerListPingEvent event) {
        if (this.plugin.motd != null && this.plugin.motd.getText() != null) {
            event.setMotd(this.plugin.motd.getText());
        }
        if (this.plugin.icon != null) {
            event.setServerIcon(plugin.icon);
        }
    }
}
