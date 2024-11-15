package gg.knockoff.game;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;

public final class knockoff extends JavaPlugin {

    public final MapData mapdata = new MapData();
    public boolean is_force_starting = false;
    public GameManager GameManager;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getLogger().log(Level.INFO, "KnockOff Plugin Enabled!");

        Bukkit.getWorld("world").setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 20);

        DebugCommands dc = new DebugCommands();
        this.getCommand("force_start").setExecutor(dc);
        new BukkitRunnable() {

            @Override
            public void run() {
                if (GameManager != null) {
                    return;
                }

                if (is_force_starting) {
                    GameManager = new GameManager();
                    is_force_starting = false;
                    return;
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 20);
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "KnockOff Plugin Disabled!");
    }

    public static knockoff getInstance() {
        return getPlugin(knockoff.class);
    }
}
