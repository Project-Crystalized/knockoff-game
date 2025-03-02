package gg.knockoff.game;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;

public final class knockoff extends JavaPlugin {

    public final MapData mapdata = new MapData();
    public boolean is_force_starting = false;
    public GameManager GameManager;
    public boolean DevMode = false;
    public ProtocolManager protocolmanager;
    private static boolean GameCountdownStarted = false;
    //public ConfigData ConfigData;

    private int PlayerStartLimit = 2;

    @Override @SuppressWarnings("deprication") //FAWE has deprecation notices from WorldEdit that's printed in console when compiled
    public void onEnable() {
        protocolmanager = ProtocolLibrary.getProtocolManager();
        //ConfigData = new ConfigData();
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new DamagePercentage(), this);
        this.getServer().getPluginManager().registerEvents(new CrystalBlocks(), this);

				this.getServer().getMessenger().registerOutgoingPluginChannel(this, "crystalized:knockoff");
				this.getServer().getMessenger().registerOutgoingPluginChannel(this, "crystalized:main");

        Bukkit.getWorld("world").setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 20);
        Bukkit.getWorld("world").setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);

        Commands dc = new Commands();
        this.getCommand("knockoff").setExecutor(dc);
        this.getCommand("knockoff_give").setExecutor(dc);
        this.getCommand("knockoff_dropitem").setExecutor(dc);
        this.getCommand("knockoff_debug").setExecutor(dc);

        KnockoffDatabase.setup_databases();
        KnockoffItem.SetupKnockoffItems();

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
                        GameCountdownStarted = false;
                        new BukkitRunnable() {
                            public void run() {

								// signal that the game has started to the proxy
                                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                                out.writeUTF("start_game");
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    out.writeUTF(p.getName());
                                }
                                Player p = (Player) Bukkit.getOnlinePlayers().toArray()[0];
                                p.sendPluginMessage(knockoff.getInstance(), "crystalized:knockoff", out.toByteArray());
                                GameManager = new GameManager();
                                cancel();
                            }

                        }.runTaskTimer(knockoff.getInstance(), 1, 20);
                    }
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (GameManager != null) {
                    //do nothing, game has started
                    GameCountdownStarted = false;
                } else {
                    if (!GameCountdownStarted) {
                        if (Bukkit.getOnlinePlayers().size() > PlayerStartLimit || Bukkit.getOnlinePlayers().size() == PlayerStartLimit) {
                            GameCountdown();
                        }
                    } else if (Bukkit.getOnlinePlayers().size() < PlayerStartLimit) {
                        GameCountdownStarted = false;
                    }
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 20);

        protocolmanager.addPacketListener(KnockoffProtocolLib.make_allys_glow());
        getLogger().log(Level.INFO, "KnockOff Plugin Enabled!");

    }

    private static void GameCountdown() {
        GameCountdownStarted = true;
        new BukkitRunnable() {
            int timer = 15;
            @Override
            public void run() {
                Bukkit.getServer().sendActionBar(Component.translatable("crystalized.game.generic.startingin").color(NamedTextColor.GREEN)
                        .append(Component.text(" " + (timer + 1) ).color(NamedTextColor.DARK_GRAY))
                        .append(Component.text(" " + timer).color(NamedTextColor.RED))
                        .append(Component.text(" " + (timer - 1) ).color(NamedTextColor.DARK_GRAY))
                );
                timer--;
                if (!GameCountdownStarted) {
                    Bukkit.getServer().sendMessage(Component.text("Game cancelled, too few players!").color(NamedTextColor.RED));
                    GameCountdownStarted = false;
                    cancel();
                }
                if (timer == 0) {
                    knockoff.getInstance().is_force_starting = true;
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 20);
    }


    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Knockoff Plugin Disabling. If this is a reload, We highly recommend restarting instead");
    }

    public static knockoff getInstance() {
        return getPlugin(knockoff.class);
    }

    //I hate how this isn't available normally in Java, I copy-pasted this off a website lol
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
