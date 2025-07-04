package gg.knockoff.game;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.geysermc.floodgate.api.FloodgateApi;

import static net.kyori.adventure.text.Component.text;

public final class knockoff extends JavaPlugin {

    public final MapData mapdata = new MapData();
    public boolean is_force_starting = false;
    public GameManager GameManager;
    public boolean DevMode = false;
    public ProtocolManager protocolmanager;
    private static boolean GameCountdownStarted = false;
    //public ConfigData ConfigData;

    private int PlayerStartLimit = 2;
    private int configVersion = 0;

    @Override @SuppressWarnings("deprication") //FAWE has deprecation notices from WorldEdit that's printed in console when compiled
    public void onEnable() {
        protocolmanager = ProtocolLibrary.getProtocolManager();
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new DamagePercentage(), this);
        this.getServer().getPluginManager().registerEvents(new CrystalBlocks(), this);

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "crystalized:knockoff");
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "crystalized:main");

        Bukkit.getWorld("world").setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 20);
        Bukkit.getWorld("world").setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);

        saveResource("config.yml", false);
        if (getConfig().getInt("version") != 1) {
            configVersion = getConfig().getInt("version");
            getLogger().log(Level.SEVERE, "Invalid Version, Please update your config. Expecting 1 but found " + configVersion + ". You may experience fatal issues.");
        }

        //This is weird
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("knockoff");
            command.then(Commands.literal("start").executes(ctx -> {
                Entity p = ctx.getSource().getExecutor();
                if (p.isOp()) {
                    if (knockoff.getInstance().GameManager == null) {
                        knockoff.getInstance().DevMode = false;
                        knockoff.getInstance().is_force_starting = true;
                    } else {
                        ctx.getSource().getExecutor().sendMessage(text("[!] A game is already in progress. Please wait until the game is over to use this command again").color(NamedTextColor.RED));
                    }
                } else {
                    p.sendMessage(text("[!] You need Operator permissions to use this command."));
                }
                return Command.SINGLE_SUCCESS;
            }));
            command.then(Commands.literal("end").executes(ctx -> {
                Entity p = ctx.getSource().getExecutor();
                if (p.isOp()) {
                    if (knockoff.getInstance().GameManager != null) {
                        knockoff.getInstance().DevMode = false;
                        knockoff.getInstance().GameManager.ForceEndGame();
                    } else {
                        ctx.getSource().getExecutor().sendMessage(text("[!] This command cannot be used in the queue").color(NamedTextColor.RED));
                    }
                } else {
                    p.sendMessage(text("[!] You need Operator permissions to use this command."));
                }
                return Command.SINGLE_SUCCESS;
            }));
            command.then(Commands.literal("spawn_powerup")
                    .then(Commands.literal("Random").executes(ctx -> {commandSpawnPowerup(ctx.getSource().getExecutor(),null); return Command.SINGLE_SUCCESS;}))
                    .then(Commands.literal("Boost_Orb").executes(ctx -> {commandSpawnPowerup(ctx.getSource().getExecutor(),"BoostOrb"); return Command.SINGLE_SUCCESS;}))
                    .then(Commands.literal("Bridge_Orb").executes(ctx -> {commandSpawnPowerup(ctx.getSource().getExecutor(),"BridgeOrb"); return Command.SINGLE_SUCCESS;}))
                    .then(Commands.literal("Explosive_Orb").executes(ctx -> {commandSpawnPowerup(ctx.getSource().getExecutor(),"ExplosiveOrb"); return Command.SINGLE_SUCCESS;}))
                    .then(Commands.literal("Grappling_Orb").executes(ctx -> {commandSpawnPowerup(ctx.getSource().getExecutor(),"GrapplingOrb"); return Command.SINGLE_SUCCESS;}))
                    .then(Commands.literal("Knockout_Orb").executes(ctx -> {commandSpawnPowerup(ctx.getSource().getExecutor(),"KnockoutOrb"); return Command.SINGLE_SUCCESS;}))
                    .then(Commands.literal("Cloud_Totem").executes(ctx -> {commandSpawnPowerup(ctx.getSource().getExecutor(),"CloudTotem"); return Command.SINGLE_SUCCESS;}))
                    .then(Commands.literal("Wind_Charge").executes(ctx -> {commandSpawnPowerup(ctx.getSource().getExecutor(),"WindCharge"); return Command.SINGLE_SUCCESS;}))
                    .then(Commands.literal("Boxing_Glove").executes(ctx -> {commandSpawnPowerup(ctx.getSource().getExecutor(),"BoxingGlove"); return Command.SINGLE_SUCCESS;}))
            );
            command.then(Commands.literal("spawn_hazard")
                    .then(Commands.literal("TNT").executes(ctx -> {commandSpawnHazard(ctx.getSource().getExecutor(), HazardsManager.hazards.tnt); return Command.SINGLE_SUCCESS;}))
                    .then(Commands.literal("Slime_Time").executes(ctx -> {commandSpawnHazard(ctx.getSource().getExecutor(), HazardsManager.hazards.slimetime); return Command.SINGLE_SUCCESS;}))
                    .then(Commands.literal("Flying_Cars").executes(ctx -> {commandSpawnHazard(ctx.getSource().getExecutor(), HazardsManager.hazards.flyingcars); return Command.SINGLE_SUCCESS;}))
            );
            command.then(Commands.literal("moveMap").executes(ctx -> {
                Entity p = ctx.getSource().getExecutor();
                reloadConfig();
                if (p.isOp()) {
                    if (knockoff.getInstance().GameManager == null) {
                        p.sendMessage(text("[!] This cant be used in the waiting lobby."));
                        return Command.SINGLE_SUCCESS;
                    }
                    if (getConfig().getBoolean("tourneys.manual_map_movement") && getConfig().getBoolean("tourneys.enable")) {
                        knockoff.getInstance().GameManager.CloneNewMapSection();
                    } else {
                        p.sendMessage(text("[!] Manual Map movement is disabled in config.yml, This command cannot be used unless it is enabled"));
                    }
                } else {
                    p.sendMessage(text("[!] You need Operator permissions to use this command."));
                }
                return Command.SINGLE_SUCCESS;
            }));

            LiteralCommandNode<CommandSourceStack> buildCommand = command.build();
            commands.registrar().register(buildCommand);
        });

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
                        Bukkit.getServer().sendMessage(text("Too many players to start a game (hardcoded limit is 24). Please kick players off or limit your player count in server.properties."));
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
                        .append(text(" " + (timer + 1) ).color(NamedTextColor.DARK_GRAY))
                        .append(text(" " + timer).color(NamedTextColor.RED))
                        .append(text(" " + (timer - 1) ).color(NamedTextColor.DARK_GRAY))
                );
                timer--;
                if (!GameCountdownStarted) {
                    Bukkit.getServer().sendMessage(text("Game cancelled, too few players!").color(NamedTextColor.RED));
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

    //This should only be called inside commands, nowhere else
    public void commandSpawnPowerup(Entity commandSource, String powerup) {
        knockoff.getInstance().reloadConfig();
        FloodgateApi floodgateapi = FloodgateApi.getInstance();
        if (!commandSource.isOp()) {
            commandSource.sendMessage(text("[!] You need Operator permissions to use this command."));
        } else if (knockoff.getInstance().GameManager == null) {
            commandSource.sendMessage(text("[!] This cant be used in the waiting lobby."));
        } else if (!knockoff.getInstance().getConfig().getBoolean("tourneys.manual_powerup_spawning") && !knockoff.getInstance().getConfig().getBoolean("tourneys.enable")) {
            commandSource.sendMessage(text("[!] Manual Powerup Spawning is disabled in config.yml, This command cannot be used unless it is enabled"));
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, "minecraft:block.note_block.pling", 50, 2);
                if (floodgateapi.isFloodgatePlayer(p.getUniqueId())) {
                    p.sendMessage(Component.text("-".repeat(40)));
                } else {
                    p.sendMessage(Component.text(" ".repeat(55)).decoration(TextDecoration.STRIKETHROUGH,  true));
                }
                p.sendMessage(text(""));
                p.sendMessage(text("[!] An Admin has spawned a powerup!")); //TODO make this translatable
                p.sendMessage(text(""));
                if (floodgateapi.isFloodgatePlayer(p.getUniqueId())) {
                    p.sendMessage(Component.text("-".repeat(40)));
                } else {
                    p.sendMessage(Component.text(" ".repeat(55)).decoration(TextDecoration.STRIKETHROUGH,  true));
                }
            }
            knockoff.getInstance().GameManager.SpawnRandomPowerup(powerup);
        }
    }

    //Again, should only be called inside commands, nowhere else
    public void commandSpawnHazard(Entity commandSource, HazardsManager.hazards hazard) {
        knockoff.getInstance().reloadConfig();
        if (!commandSource.isOp()) {
            commandSource.sendMessage(text("[!] You need Operator permissions to use this command."));
        } else if (knockoff.getInstance().GameManager == null) {
            commandSource.sendMessage(text("[!] This cant be used in the waiting lobby."));
        } else if (!knockoff.getInstance().getConfig().getBoolean("tourneys.manual_hazard_control") && !knockoff.getInstance().getConfig().getBoolean("tourneys.enable")) {
            commandSource.sendMessage(text("[!] Manual Hazard Control is disabled in config.yml, This command cannot be used unless it is enabled"));
        } else {
            knockoff.getInstance().GameManager.hazards.NewHazard(hazard);
        }
    }
}
