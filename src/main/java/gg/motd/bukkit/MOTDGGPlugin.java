package gg.motd.bukkit;

import gg.motd.api.APIClient;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class MOTDGGPlugin extends JavaPlugin {

    protected APIClient
            client;

    @Override
    public void onEnable() {
        Objects.requireNonNull(this.getCommand("motdgg"))
                .setExecutor(new MOTDCommand(this));

        String pluginVersion = this.getDescription().getVersion();
        client = new APIClient("motdgg-bukkit/" + pluginVersion + "/" + this.getServer().getVersion());
    }

    public APIClient getClient() {
        return client;
    }
}
