package gg.motd.bukkit;

import gg.motd.api.APIClient;
import gg.motd.api.MOTD;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;

import java.lang.reflect.Method;
import java.util.Objects;

public class MOTDGGPlugin extends JavaPlugin {

    protected APIClient client;

    protected CachedServerIcon icon = null;

    protected MOTD motd = null;

    @Override
    public void onEnable() {
        Objects.requireNonNull(this.getCommand("motdgg"))
                .setExecutor(new MOTDCommand(this));

        String pluginVersion = this.getDescription().getVersion();
        client = new APIClient("motdgg-bukkit/" + pluginVersion + "/" + this.getServer().getVersion());
        getServer().getPluginManager().registerEvents(new ServerListPingEventHandler(this), this);
    }

    public APIClient getClient() {
        return client;
    }

    public boolean hasSpigotMethod() {
        Class<CommandSender> sender = CommandSender.class;
        try {
            Method spigot = sender.getMethod("spigot");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
