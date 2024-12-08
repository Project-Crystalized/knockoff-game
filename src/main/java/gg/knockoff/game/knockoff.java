package gg.knockoff.game;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kyori.adventure.text.Component;
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
    public ProtocolManager protocolmanager;
    //public ConfigData ConfigData;

    @Override @SuppressWarnings("deprication") //FAWE has deprecation notices from WorldEdit that's printed in console when compiled
    public void onEnable() {
        protocolmanager = ProtocolLibrary.getProtocolManager();
        //ConfigData = new ConfigData();
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new DamagePercentage(), this);
        this.getServer().getPluginManager().registerEvents(new CrystalBlocks(), this);

        Bukkit.getWorld("world").setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 20);
        Bukkit.getWorld("world").setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);

        Commands dc = new Commands();
        this.getCommand("knockoff").setExecutor(dc);
        this.getCommand("knockoff_give").setExecutor(dc);
        this.getCommand("knockoff_dropitem").setExecutor(dc);
        this.getCommand("knockoff_debug").setExecutor(dc);

        new BukkitRunnable() {

            @Override
            public void run() {
                if (GameManager != null) {
                    return;
                }
                if (is_force_starting) {
                    if (knockoff.getInstance().getServer().getOnlinePlayers().size() > 24) {//24 is the max player limit for now
                        Bukkit.getServer().sendMessage(Component.text("Too many players to start a game (hardcoded limit is 24). Please kick players off or limit your player count in server.properties."));
                        is_force_starting = false;
                        return;
                    } else {
                        is_force_starting = false;
                        new BukkitRunnable() {
                            int timer = 3;
                            public void run() {
                                GameManager = new GameManager();
                                cancel();
                                /*
                                switch (timer) {
                                    case -2:

                                    case -1:
                                        break; // should prevent the "GO!" from appearing twice
                                    case 0:
                                        for (Player p : Bukkit.getOnlinePlayers()) {
                                            p.sendTitle("GO!", " ", 5, 20, 0);
                                            p.playSound(p, "crystalized:effect.countdown_end", 50, 1);
                                        }
                                        break;
                                    case 1:
                                        for (Player p : Bukkit.getOnlinePlayers()) {
                                            p.sendTitle("1", " ", 5, 20, 0);
                                            p.playSound(p, "crystalized:effect.countdown", 50, 1);
                                        }
                                        break;
                                    case 2:
                                        for (Player p : Bukkit.getOnlinePlayers()) {
                                            p.sendTitle("2", " ", 5, 20, 0);
                                            p.playSound(p, "crystalized:effect.countdown", 50, 1);
                                        }
                                        break;
                                    case 3:
                                        for (Player p : Bukkit.getOnlinePlayers()) {
                                            p.sendTitle("3", " ", 5, 20, 0);
                                            p.playSound(p, "crystalized:effect.countdown", 50, 1);
                                        }
                                        break;
                                    default:
                                        break; //This shouldn't trigger
                                }
                                timer--;
                                 */
                            }

                        }.runTaskTimer(knockoff.getInstance(), 1, 20);
                    }
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 20);

        protocolmanager.addPacketListener(KnockoffProtocolLib.make_allys_glow());
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
