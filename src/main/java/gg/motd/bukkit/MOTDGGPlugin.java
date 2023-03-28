package gg.motd.bukkit;

import gg.motd.api.APIClient;
import gg.motd.api.MOTD;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MOTDGGPlugin extends JavaPlugin {

    protected APIClient client;

    protected CachedServerIcon icon = null;

    protected MOTD motd = null;

    protected BukkitAudiences adventure;

    @Override
    public void onEnable() {
        Objects.requireNonNull(this.getCommand("motdgg"))
                .setExecutor(new MOTDCommand(this));

        String pluginVersion = this.getDescription().getVersion();
        this.client = new APIClient("motdgg-bukkit/" + pluginVersion + "/" + this.getServer().getVersion());
        this.adventure = BukkitAudiences.create(this);
        getServer().getPluginManager().registerEvents(new ServerListPingEventHandler(this), this);
    }

    @Override
    public void onDisable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        this.client = null;
    }

    public APIClient getClient() {
        return client;
    }

    /**
     * @return get the adventure adapter
     */
    public @NotNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }
}
