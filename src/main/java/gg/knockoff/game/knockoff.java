package gg.knockoff.game;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;

public final class knockoff extends JavaPlugin {

    public final MapData mapdata = new MapData();
    public boolean is_force_starting = false;
    public GameManager GameManager;
    public boolean DevMode = false;

    @Override @SuppressWarnings("deprication") //FAWE has deprecation notices from WorldEdit that's printed in console when compiled
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new CrystalBlocks(), this);

        Bukkit.getWorld("world").setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 20);
        Bukkit.getWorld("world").setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);

        DebugCommands dc = new DebugCommands();
        this.getCommand("force_start").setExecutor(dc);
        new BukkitRunnable() {

            @Override
            public void run() {
                if (GameManager != null) {
                    return;
                }

                if (is_force_starting) {
                    if (knockoff.getInstance().getServer().getOnlinePlayers().size() > 24) {//24 is the max player limit for now
                        Bukkit.getServer().sendMessage(Component.text("Too many players to start a game (hardcoded limit is 24). Please kick players off or limit your player count in server.properties."));
                        return;
                    } else {
                        GameManager = new GameManager();
                        is_force_starting = false;
                        return;
                    }
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 20);
        getLogger().log(Level.INFO, "KnockOff Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Knockoff Plugin Disabling. If this is a reload, We highly recommend restarting instead");
    }

    public static knockoff getInstance() {
        return getPlugin(knockoff.class);
    }
}
