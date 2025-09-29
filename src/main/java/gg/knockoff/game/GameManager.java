package gg.knockoff.game;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.fastasyncworldedit.core.Fawe;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
import gg.knockoff.game.CustomEntities.MapParticles;
import gg.knockoff.game.hazards.*;
import io.papermc.paper.entity.LookAnchor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.geysermc.floodgate.api.FloodgateApi;

import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class GameManager { //I honestly think this entire class could be optimised because of how long it is
    public static List<PlayerData> playerDatas;
    public Teams teams;
    public HazardsManager hazards = new HazardsManager();
    public static List<Block> blocksCrystallizing = new ArrayList<>();
    public static List<MapParticles> particles = new ArrayList<>();

    public static int SectionPlaceLocationX = 1000;
    public static int SectionPlaceLocationY = 0;
    public static int SectionPlaceLocationZ = 1000;
    public static int LastSectionPlaceLocationX = -1000;
    public static int LastSectionPlaceLocationY = 0;
    public static int LastSectionPlaceLocationZ = -1000;
    public ArrayList<String> PlayerList = new ArrayList<String>();
    public static String GameState = "game"; //can be "game" (game running), "end" (game ending)
    public static boolean showdownModeEnabled = false;
    public static boolean showdownModeStarted = false; //This is enabled when its effects actually start, above checks if its enabled in config.
    public static boolean mapMoving = false;

    public enum GameTypes {
        StanderedSolos,
        StanderedDuos,
        StanderedTrios,
        StanderedSquads,
        Custom,

        SplitPlayerCountInHalf,
    }

    //public static String GameType = "Solo";
    public static GameTypes GameType;
    public static mapDirections plannedDirection = mapDirections.undecided;

    public static int Round = 0;
    public static int RoundCounter =0;

    public enum mapDirections{
        undecided,
        EAST,
        SOUTH,
        WEST,
    }

    public GameManager(GameTypes type) {//Start of the game
        knockoff.getInstance().reloadConfig();
        Bukkit.getServer().sendMessage(text("Starting Game! \n(Note: the server might lag slightly)"));
        GameState = "game";
        for (Entity e : Bukkit.getWorld("world").getEntities()) {
            if (e instanceof TextDisplay) {
                e.remove();
            }
        }
        GameType = type;

        PlayerList.clear();
        teams = new Teams(GameType);
        showdownModeStarted = false;

        // Sets the target area to air to prevent previous game's sections to interfere with the current game
        // Could be optimised, Filling all this in 1 go and/or in larger spaces causes your server to most likely go out of memory or not respond for a good while
        // Plus this lags the server anyway
        Bukkit.getLogger().log(Level.INFO, "Making room for knockoff map, This may lag your server depending on how good it is");
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(1100, -30, 1100), BlockVector3.at(1000, 40, 1000));
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
            RandomPattern pat = new RandomPattern();
            BlockState air = BukkitAdapter.adapt(Material.AIR.createBlockData());
            pat.add(air, 1);
            editSession.setBlocks((Region) selection, pat);
        }  catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }

        MapManager.placeNewSection();
        new BukkitRunnable() {
            @Override
            public void run() {
                playerDatas = new ArrayList<PlayerData>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerData p = new PlayerData(player);
                    playerDatas.add(p);
                }
            }
        }.runTaskLater(knockoff.getInstance(), 1);

        SetupFirstSpawns();

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.getInventory().clear();
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.unlistPlayer(p);
            }
            PlayerList.add(p.getName());
            WorldBorder PlayerBorder = Bukkit.getServer().createWorldBorder();
            p.setWorldBorder(PlayerBorder);
            PlayerBorder.setCenter(p.getX() + 0.5, p.getZ() + 0.5);
            PlayerBorder.setSize(3);

            GiveTeamItems(p);
            if (Teams.GetPlayerTeam(p) == "spectator") {
                p.setGameMode(GameMode.SPECTATOR);
            } else {
                p.setGameMode(GameMode.ADVENTURE);
                CustomPlayerNametags.CustomPlayerNametags(p);
            }
            ScoreboardManager.SetPlayerScoreboard(p);
            Teams.SetPlayerDisplayNames(p);


            p.setSneaking(true);
            p.setSneaking(false);
        }
        TeamStatus.Init();
        GameManager.Round = 1;
        GameManager.RoundCounter = 30;

        if (knockoff.getInstance().getConfig().getBoolean("other.showdown")) {
            if (Bukkit.getOnlinePlayers().size() < 2) {
                Bukkit.getServer().sendMessage(text("Showdown mode disabled due to player size."));
            } else {
                showdownModeEnabled = true;
            }
        }

        new BukkitRunnable() {
            int timer = 0;
            public void run() {
                switch (timer) {
                    case 7:
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.showTitle(Title.title(translatable("crystalized.game.knockoff.go").color(GOLD), text(" "),
                                    Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(1), Duration.ofSeconds(1))));
                            player.playSound(player, "crystalized:effect.countdown_end", 50, 1);
                            player.getWorldBorder().reset();
                        }
                        StartGameLoop();
                        cancel();
                        break;
                    case 6:
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.showTitle(Title.title(translatable("crystalized.game.generic.startingin").color(GREEN), text("3 2 ").color(GRAY)
                                            .append(text("1").color(RED))
                                    ,Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(1), Duration.ofSeconds(1))));
                            player.playSound(player, "crystalized:effect.countdown", 50, 1);
                        }
                        break;
                    case 5:
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.showTitle(Title.title(translatable("crystalized.game.generic.startingin").color(GREEN), text("3").color(GRAY)
                                            .append(text(" 2").color(RED))
                                            .append(text(" 1").color(GRAY))
                                    ,Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(1), Duration.ofSeconds(1))));
                            player.playSound(player, "crystalized:effect.countdown", 50, 1);
                        }
                        break;
                    case 4:
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.showTitle(Title.title(translatable("crystalized.game.generic.startingin").color(GREEN), text("3").color(RED)
                                            .append(text(" 2 1").color(GRAY))
                                    ,Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(1), Duration.ofSeconds(1))));
                            player.playSound(player, "crystalized:effect.countdown", 50, 1);
                        }
                        break;
                    case 1:
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            PlayerData pd = getPlayerData(p);
                            p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(pd.getLives() * 2);
                        }
                        break;
                }
                timer++;
            }
        }.runTaskTimer(knockoff.getInstance(), 1 ,20);

        //This is to prevent players from walking through the starting border (eg, from Bedrock clients or hacked clients walking through the border)
        for (TeamData td : Teams.team_datas_without_spectator) {

        }
        new BukkitRunnable() {
            int timer = 7 * 20;
            public void run() {
                timer--;
                if (timer == 0) {
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 1);

        new BukkitRunnable() {
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (knockoff.getInstance().GameManager == null) {cancel();}
                    if (knockoff.getInstance().GameManager != null) {
                        TabMenu.SendTabMenu(p);
                    }
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1 ,5);

    }

    private void StartGameLoop() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Teams.GetPlayerTeam(p) != "spectator") {
                p.setGameMode(GameMode.SURVIVAL);
            } else {
                p.setGameMode(GameMode.SPECTATOR);
                PlayerData pd = getPlayerData(p);
                pd.isEliminated = true;
                pd.lives = 0;
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (knockoff.getInstance().GameManager == null) {cancel();}
                if (knockoff.getInstance().DevMode) {
                    Round = 0;
                    RoundCounter = 0;
                    cancel();
                }

                GameManager.RoundCounter--;
                if (GameManager.RoundCounter == 30 && GameManager.GameState.equals("game")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p, "minecraft:block.note_block.pling", 50, 2);
                    }
                    Server s = Bukkit.getServer();
                    FloodgateApi floodgateapi = FloodgateApi.getInstance();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (floodgateapi.isFloodgatePlayer(p.getUniqueId())) {
                            p.sendMessage(text("-".repeat(40)));
                        } else {
                            p.sendMessage(text(" ".repeat(55)).decoration(TextDecoration.STRIKETHROUGH,  true));
                        }
                    }
                    //Will pick a random number between 1 and 20, if its Even it will fire "if", otherwise "else"
                    //Did this because "getRandomNumber(1, 2) == 1" almost always returns 1
                    if (knockoff.getInstance().getRandomNumber(1, 20) % 2 == 0) {
                        s.sendMessage(text(" "));
                        s.sendMessage(translatable("crystalized.game.knockoff.chat.powerup").color(DARK_AQUA));
                        s.sendMessage(text(" "));
                        SpawnRandomPowerup(null);
                    } else {
                        s.sendMessage(text(" "));
                        s.sendMessage(translatable("crystalized.game.knockoff.chat.powerup2").color(DARK_AQUA));
                        s.sendMessage(text(" "));
                        SpawnRandomPowerup(null);
                        SpawnRandomPowerup(null);
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (floodgateapi.isFloodgatePlayer(p.getUniqueId())) {
                            p.sendMessage(text("-".repeat(40)));
                        } else {
                            p.sendMessage(text(" ".repeat(55)).decoration(TextDecoration.STRIKETHROUGH,  true));
                        }
                    }
                }
                if (TeamStatus.getAliveTeams().size() == 2 && showdownModeEnabled) {
                    startShowdown();
                    cancel();
                }
                if (showdownModeStarted) {
                    cancel();
                }

                if (RoundCounter == 9 && GameManager.GameState.equals("game") && !showdownModeStarted) {
                    particles.clear();
                    MapData md = knockoff.getInstance().mapdata;
                    for (int i = 0; i < 8; i++) {
                        particles.add(new MapParticles(new Location(Bukkit.getWorld("world"),
                                knockoff.getInstance().getRandomNumber(SectionPlaceLocationX, md.getCurrentXLength()),
                                knockoff.getInstance().getRandomNumber(md.getCurrentYLength() - 7, md.getCurrentYLength() + 2),
                                knockoff.getInstance().getRandomNumber(SectionPlaceLocationZ, md.getCurrentZLength()))
                        ));
                    }
                }
                if (RoundCounter == 5 && GameManager.GameState.equals("game") && !showdownModeStarted) {
                    decideMapDirection();
                }

                if (RoundCounter == 0 && GameManager.GameState.equals("game") && !showdownModeStarted) {
                    if (!knockoff.getInstance().getConfig().getBoolean("tourneys.manual_map_movement")) {
                        GameManager.CloneNewMapSection();
                        RoundCounter = 60;
                        Round++;
                        for (MapParticles mp : particles) {
                            mp.isMoving = true;
                        }
                        new BukkitRunnable() {
                            public void run() {
                                SpawnRandomPowerup(null);
                                cancel();
                            }
                        }.runTaskTimer(knockoff.getInstance(), 5, 1);

                    }
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0 ,20);

        new BukkitRunnable() {
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    PlayerData pd = getPlayerData(p);
                    if (knockoff.getInstance().GameManager == null) {
                        cancel();
                    }
                    if (p.getLocation().clone().add(0,-1,0).getBlock().getType().equals(Material.MANGROVE_LEAVES)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, 0, false, true, true));
                    }

                    Location loc = p.getLocation();
                    if (MapManager.isInsideDecayingSection(loc)) {
                        p.showTitle(Title.title(
                                text(" "),
                                //translatable("crystalized.game.knockoff.chat.movetosafety2").color(RED),
                                text(getMapArrowToMid(p) + " ").append(translatable("crystalized.game.knockoff.chat.movetosafety2").color(RED)).append(text(" " + getMapArrowToMid(p))),
                                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(1), Duration.ofMillis(0)))
                        );
                    }

                    //for water sprout hazard
                    if (p.isInWater()) {
                        pd.percent++;
                    }

                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 1);

        //TODO clean this shit up this is a mess
        new BukkitRunnable() {
            @Override
            public void run() {
                //Should stop this bukkitrunnable once the game ends
                if (knockoff.getInstance().GameManager == null) {cancel();}

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getLocation().getY() < -20) {//instantly kills the player when they get knocked into the void
                        Location loc = new Location(Bukkit.getWorld("world"), knockoff.getInstance().mapdata.getCurrentMiddleXLength(), knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 10, knockoff.getInstance().mapdata.getCurrentMiddleZLength());
                        p.teleport(loc);
                        if (p.getGameMode().equals(GameMode.SURVIVAL)) {
                            p.setHealth(0);
                        }
                    }
                }
                for (Entity e : Bukkit.getWorld("world").getEntities()) {
                    if (e instanceof Item) {
                        if (((Item) e).getItemStack().getType().equals(Material.COAL)) {
                            if (!((Item) e).getItemStack().getItemMeta().hasItemModel()) {
                                e.remove();
                            }
                            //do nothing, material.coal and wind charges is powerups so we dont clear them
                            //TODO this might become a mess
                        } else if (
                                ((Item) e).getItemStack().getType().equals(Material.WIND_CHARGE)
                                        || ((Item) e).getItemStack().equals(KnockoffItem.BoxingGlove)
                                        || ((Item) e).getItemStack().equals(KnockoffItem.TrialChamberHazardKey)
                                        || ((Item) e).getItemStack().equals(KnockoffItem.TrialChamberMace)
                        ) {

                        } else {
                            e.remove();
                        }
                    } else if (e.getLocation().getY() < -20) {
                        if (e instanceof Breeze) {
                            MapData md = knockoff.getInstance().mapdata;
                            e.teleport(new Location(Bukkit.getWorld("world"),
                                    md.getCurrentMiddleXLength() + knockoff.getInstance().getRandomNumber(-5, 5),
                                    md.CurrentYLength,
                                    md.getCurrentMiddleZLength() + knockoff.getInstance().getRandomNumber(-5, 5)
                                    )
                            );
                        }
                    }
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0 ,1);
    }

    public static void StartEndGame(String WinningTeam, TeamData td) {
        GameState = "end";
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (GameManager.GameType.equals(GameTypes.StanderedSolos)) {
                Player lastPlayer = Bukkit.getPlayer(Teams.get_team_from_string(WinningTeam).getFirst());
                player.showTitle(Title.title(
                        lastPlayer.displayName(),
                        translatable("crystalized.game.knockoff.win").color(YELLOW),
                        Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(5), Duration.ofMillis(1000)))
                );
                player.sendMessage(lastPlayer.displayName().append(text(" ")).append(translatable("crystalized.game.knockoff.win").color(YELLOW)));
            } else {
                player.showTitle(Title.title(
                        text(td.symbol).append(translatable("crystalized.game.generic.team." + td.name).color(TextColor.color(td.color.asRGB()))).append(text(td.symbol)),
                        translatable("crystalized.game.knockoff.win").color(YELLOW),
                        Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(5), Duration.ofMillis(1000)))
                );
                player.sendMessage(
                        text(td.symbol).append(translatable("crystalized.game.generic.team." + td.name).color(TextColor.color(td.color.asRGB()))).append(text(td.symbol))
                                .append(text(" ")).append(translatable("crystalized.game.knockoff.win").color(YELLOW))
                );
            }
            if (Teams.GetPlayerTeam(player).equals(td.name)) {
                player.playSound(player, "crystalized:effect.ls_game_won", 50, 1);
            } else {
                player.playSound(player, "crystalized:effect.ls_game_lost", 50, 1);
            }
        }
        KnockoffDatabase.save_game(WinningTeam);
        new BukkitRunnable() {
            int timer = 0;
            FloodgateApi floodgateapi = FloodgateApi.getInstance();

            @Override
            public void run() {
                switch (timer) {
                    case 2:
                        Collections.sort(playerDatas, new PlayerDataComparator());
                        Collections.reverse(playerDatas);
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (floodgateapi.isFloodgatePlayer(p.getUniqueId())) {
                                p.sendMessage(text("-".repeat(40)).color(GOLD));
                            } else {
                                p.sendMessage(text(" ".repeat(55)).color(GOLD).decoration(TextDecoration.STRIKETHROUGH,  true));
                            }
                        }
                        Bukkit.getServer().sendMessage(text("")
                                .append(text("\n").append(translatable("crystalized.game.knockoff.name").color(GOLD)).append(text(" \uE108").color(WHITE)))
                                .append(text("\n").append(translatable("crystalized.game.generic.gameresults").color(BLUE)))
                        );
                        if (playerDatas.size() > 0) {
                            PlayerData first = playerDatas.get(0);
                            Bukkit.getServer().sendMessage(text("   1st. ")
                                    .append(text(first.player)).color(GREEN).append(text(" ".repeat(20 - first.player.length())))
                                    .append(text("" + first.kills))
                            );
                        }
                        if (playerDatas.size() > 1) {
                            PlayerData second = playerDatas.get(1);
                            Bukkit.getServer().sendMessage(text("   2nd. ")
                                    .append(text(second.player)).color(YELLOW).append(text(" ".repeat(20 - second.player.length())))
                                    .append(text("" + second.kills))
                            );
                        }
                        if (playerDatas.size() > 2) {
                            PlayerData third = playerDatas.get(2);
                            Bukkit.getServer().sendMessage(text("   3rd. ")
                                    .append(text(third.player)).color(YELLOW).append(text(" ".repeat(20 - third.player.length())))
                                    .append(text("" + third.kills))
                            );
                        }

                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (floodgateapi.isFloodgatePlayer(p.getUniqueId())) {
                                p.sendMessage(text("-".repeat(40)).color(GOLD));
                            } else {
                                p.sendMessage(text(" ".repeat(55)).color(GOLD).decoration(TextDecoration.STRIKETHROUGH,  true));
                            }
                        }
                        break;
                    case 12, 13, 14:
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player, "minecraft:block.note_block.hat", SoundCategory.MASTER,50, 1); //TODO placeholder sound
                        }
                        break;
                    case 15:
                        ForceEndGame();
                        cancel();
                        break;
                }
                timer++;
            }
        }.runTaskTimer(knockoff.getInstance(), 20,20);
    }

    public static void ForceEndGame() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/world \"world\"");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos1 " + SectionPlaceLocationX + "," + SectionPlaceLocationY + "," + SectionPlaceLocationZ);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos2 " + knockoff.getInstance().mapdata.getCurrentXLength() + "," + knockoff.getInstance().mapdata.getCurrentYLength() + "," + knockoff.getInstance().mapdata.getCurrentZLength());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/set air");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos1 " + LastSectionPlaceLocationX + "," + LastSectionPlaceLocationY + "," + LastSectionPlaceLocationZ);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos2 " + (LastSectionPlaceLocationX + MapManager.LastXLength) + "," + (LastSectionPlaceLocationY+ MapManager.LastYLength) + "," + (LastSectionPlaceLocationZ + MapManager.LastZLength));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/set air");

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.kick(text(""));
        }

				// send players back to lobby
        //This causes a bug where players stay on too long, so a new game starts and everything becomes bugged
        /*
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF("lobby");
				for (Player p : Bukkit.getOnlinePlayers()) {
						p.sendPluginMessage(knockoff.getInstance(), "crystalized:main", out.toByteArray());
				}
                */

        for (Entity e : Bukkit.getWorld("world").getEntities()) {
            if (e instanceof TextDisplay) {
                e.remove();
            }
        }

        SectionPlaceLocationX = 1000;
        SectionPlaceLocationY = 0;
        SectionPlaceLocationZ = 1000;
        knockoff.getInstance().GameManager.teams = null;
        knockoff.getInstance().GameManager.hazards = null;
        knockoff.getInstance().GameManager = null;
    }

    public static void GiveTeamItems(Player player) {
        ItemStack item = new ItemStack(Material.AMETHYST_BLOCK, 64);
        ItemMeta im = item.getItemMeta();
        PlayerInventory inv = player.getInventory();

        //for debugging
        //Bukkit.getLogger().log(Level.INFO, "[GAMEMANAGER] Player " + player.getName() + "Is in Team " + Teams.GetPlayerTeam(player));

        if (Teams.GetPlayerTeam(player) != "spectator") {
            TeamData td = TeamData.get_team_data(Teams.GetPlayerTeam(player));
            im.setItemModel(td.item_model);
            inv.setChestplate(colorArmor(td.color, new ItemStack(Material.LEATHER_CHESTPLATE)));
            inv.setLeggings(colorArmor(td.color, new ItemStack(Material.LEATHER_LEGGINGS)));
            inv.setBoots(colorArmor(td.color, new ItemStack(Material.LEATHER_BOOTS)));

            im.itemName(translatable("crystalized.item.nexusblock.name"));
            List<Component> lore = new ArrayList<>();
            lore.add(translatable("crystalized.item.nexusblock.desc").color(DARK_GRAY));
            lore.add(translatable("crystalized.item.nexusblock.desc2").color(DARK_GRAY));
            im.lore(lore);
            item.setItemMeta(im);
            player.getInventory().addItem(item);
        }
    }

    private static ItemStack colorArmor(Color c, ItemStack i) {
        LeatherArmorMeta lam = (LeatherArmorMeta) i.getItemMeta();
        lam.setColor(c);
        lam.setUnbreakable(true);
        i.setItemMeta(lam);
        return i;
    }

    public PlayerData getPlayerData(Player p) {
        for (PlayerData pd : playerDatas) {
            if (pd.player.equals(p.getName())) {
                return pd;
            }
        }
        Bukkit.getServer().sendMessage(text("error occured, a player didnt have associated data"));
        Bukkit.getLogger().warning("player name: " + p.getName());
        Thread.dumpStack();

        for (PlayerData pd : playerDatas) {
            Bukkit.getLogger().warning(pd.player);
        }

        return null;
    }

    @SuppressWarnings("deprication") //FAWE has deprecation notices from WorldEdit that's printed in console when compiled
    private static void SetupFirstSpawns() {
        MapData md = knockoff.getInstance().mapdata;
        int offset = md.currentSection.getAsJsonObject().get("spawn_offset").getAsInt();

        //TODO Misherop uncomment this for new spawn thingy
        /*
        new BukkitRunnable() {
            public void run() {
                for (TeamData td : Teams.team_datas_without_spectator) {
                    spawnSpawnPlatformAndTP(Teams.get_team_from_string(td.name), td.name);
                }
                cancel();
            }
        }.runTaskTimer(knockoff.getInstance(), 10, 1);
        */

        //EVERYTHING BELOW WILL BE REMOVED, Misherop comment everything below so it doesn't run

        if (!Teams.blue.isEmpty()) {
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(SectionPlaceLocationX + 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset , SectionPlaceLocationZ + 5),
                    BlockVector3.at(SectionPlaceLocationX + 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, SectionPlaceLocationZ + 7));
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                RandomPattern pat = new RandomPattern();
                BlockState a = BukkitAdapter.adapt(Material.BLUE_STAINED_GLASS.createBlockData());
                pat.add(a, 1);
                editSession.setBlocks((Region) selection, pat);
            }  catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                e.printStackTrace();
            }
        }
        if (!Teams.cyan.isEmpty()) {
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 5),
                    BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 7));
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                RandomPattern pat = new RandomPattern();
                BlockState a = BukkitAdapter.adapt(Material.CYAN_STAINED_GLASS.createBlockData());
                pat.add(a, 1);
                editSession.setBlocks((Region) selection, pat);
            }  catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                e.printStackTrace();
            }
        }
        if (!Teams.green.isEmpty()) {
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, SectionPlaceLocationZ + 5),
                    BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, SectionPlaceLocationZ + 7));
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                RandomPattern pat = new RandomPattern();
                BlockState a = BukkitAdapter.adapt(Material.GREEN_STAINED_GLASS.createBlockData());
                pat.add(a, 1);
                editSession.setBlocks((Region) selection, pat);
            }  catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                e.printStackTrace();
            }
        }
        if (!Teams.lemon.isEmpty()) {
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(SectionPlaceLocationX + 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 5),
                    BlockVector3.at(SectionPlaceLocationX + 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 7));
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                RandomPattern pat = new RandomPattern();
                BlockState a = BukkitAdapter.adapt(Material.YELLOW_STAINED_GLASS.createBlockData());
                pat.add(a, 1);
                editSession.setBlocks((Region) selection, pat);
            }  catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                e.printStackTrace();
            }
        }
        if (!Teams.lime.isEmpty()) {
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(SectionPlaceLocationX + 15, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, SectionPlaceLocationZ + 5),
                    BlockVector3.at(SectionPlaceLocationX + 17, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, SectionPlaceLocationZ + 7));
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                RandomPattern pat = new RandomPattern();
                BlockState a = BukkitAdapter.adapt(Material.LIME_STAINED_GLASS.createBlockData());
                pat.add(a, 1);
                editSession.setBlocks((Region) selection, pat);
            }  catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                e.printStackTrace();
            }
        }
        if (!Teams.magenta.isEmpty()) {
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 15, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 5),
                    BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 17, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 7));
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                RandomPattern pat = new RandomPattern();
                BlockState a = BukkitAdapter.adapt(Material.MAGENTA_STAINED_GLASS.createBlockData());
                pat.add(a, 1);
                editSession.setBlocks((Region) selection, pat);
            }  catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                e.printStackTrace();
            }
        }
        if (!Teams.orange.isEmpty()) {
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 15, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, SectionPlaceLocationZ + 5),
                    BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 17, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, SectionPlaceLocationZ + 7));
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                RandomPattern pat = new RandomPattern();
                BlockState a = BukkitAdapter.adapt(Material.ORANGE_STAINED_GLASS.createBlockData());
                pat.add(a, 1);
                editSession.setBlocks((Region) selection, pat);
            }  catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                e.printStackTrace();
            }
        }
        if (!Teams.peach.isEmpty()) {
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(SectionPlaceLocationX + 15, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 5),
                    BlockVector3.at(SectionPlaceLocationX + 17, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 7));
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                RandomPattern pat = new RandomPattern();
                BlockState a = BukkitAdapter.adapt(Material.PINK_STAINED_GLASS.createBlockData());
                pat.add(a, 1);
                editSession.setBlocks((Region) selection, pat);
            }  catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                e.printStackTrace();
            }
        }
        if (!Teams.purple.isEmpty()) {
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(SectionPlaceLocationX + 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, SectionPlaceLocationZ + 15),
                    BlockVector3.at(SectionPlaceLocationX + 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, SectionPlaceLocationZ + 17));
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                RandomPattern pat = new RandomPattern();
                BlockState a = BukkitAdapter.adapt(Material.PURPLE_STAINED_GLASS.createBlockData());
                pat.add(a, 1);
                editSession.setBlocks((Region) selection, pat);
            }  catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                e.printStackTrace();
            }
        }
        if (!Teams.red.isEmpty()) {
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 15),
                    BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 17));
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                RandomPattern pat = new RandomPattern();
                BlockState a = BukkitAdapter.adapt(Material.RED_STAINED_GLASS.createBlockData());
                pat.add(a, 1);
                editSession.setBlocks((Region) selection, pat);
            }  catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                e.printStackTrace();
            }
        }
        if (!Teams.white.isEmpty()) {
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, SectionPlaceLocationZ + 15),
                    BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, SectionPlaceLocationZ + 17));
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                RandomPattern pat = new RandomPattern();
                BlockState a = BukkitAdapter.adapt(Material.WHITE_STAINED_GLASS.createBlockData());
                pat.add(a, 1);
                editSession.setBlocks((Region) selection, pat);
            }  catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                e.printStackTrace();
            }
        }
        if (!Teams.yellow.isEmpty()) {
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(SectionPlaceLocationX + 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 15),
                    BlockVector3.at(SectionPlaceLocationX + 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 17));
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                RandomPattern pat = new RandomPattern();
                BlockState a = BukkitAdapter.adapt(Material.YELLOW_STAINED_GLASS.createBlockData());
                pat.add(a, 1);
                editSession.setBlocks((Region) selection, pat);
            }  catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                e.printStackTrace();
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
						World w = Bukkit.getWorld("world");
            if (Teams.GetPlayerTeam(p).equals("blue")) {
                Location blueloc = new Location(w, SectionPlaceLocationX + 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3 + offset, SectionPlaceLocationZ + 6);
                p.teleport(blueloc);
            } else if (Teams.GetPlayerTeam(p).equals("cyan")) {
                Location cyanloc = new Location(w, knockoff.getInstance().mapdata.getCurrentXLength() - 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3 + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 6);
                p.teleport(cyanloc);
            } else if (Teams.GetPlayerTeam(p).equals("green")) {
                Location greenloc = new Location(w, knockoff.getInstance().mapdata.getCurrentXLength() - 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3 + offset, SectionPlaceLocationZ + 6);
                p.teleport(greenloc);
            } else if (Teams.GetPlayerTeam(p).equals("lemon")) {
                Location greenloc = new Location(w, SectionPlaceLocationX + 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3 + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 6);
                p.teleport(greenloc);
            } else if (Teams.GetPlayerTeam(p).equals("lime")) { //Yes im aware this has blueloc as its variable, I copy pasted the first 4 lol
                Location blueloc = new Location(w, SectionPlaceLocationX + 16, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3 + offset, SectionPlaceLocationZ + 6);
                p.teleport(blueloc);
            } else if (Teams.GetPlayerTeam(p).equals("magenta")) {
                Location cyanloc = new Location(w, knockoff.getInstance().mapdata.getCurrentXLength() - 16, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3 + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 6);
                p.teleport(cyanloc);
            } else if (Teams.GetPlayerTeam(p).equals("orange")) {
                Location greenloc = new Location(w, knockoff.getInstance().mapdata.getCurrentXLength() - 16, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3 + offset, SectionPlaceLocationZ + 6);
                p.teleport(greenloc);
            } else if (Teams.GetPlayerTeam(p).equals("peach")) {
                Location greenloc = new Location(w, SectionPlaceLocationX + 16, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3 + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 6);
                p.teleport(greenloc);
            } else if (Teams.GetPlayerTeam(p).equals("purple")) {
                Location blueloc = new Location(w, SectionPlaceLocationX + 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3 + offset, SectionPlaceLocationZ + 16);
                p.teleport(blueloc);
            } else if (Teams.GetPlayerTeam(p).equals("red")) {
                Location cyanloc = new Location(w, knockoff.getInstance().mapdata.getCurrentXLength() - 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3 + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 16);
                p.teleport(cyanloc);
            } else if (Teams.GetPlayerTeam(p).equals("white")) {
                Location greenloc = new Location(w, knockoff.getInstance().mapdata.getCurrentXLength() - 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3 + offset, SectionPlaceLocationZ + 16);
                p.teleport(greenloc);
            } else if (Teams.GetPlayerTeam(p).equals("yellow")) {
                Location greenloc = new Location(w, SectionPlaceLocationX + 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3 + offset, knockoff.getInstance().mapdata.getCurrentZLength() - 16);
                p.teleport(greenloc);
            } else {
                Location loc = new Location(w, knockoff.getInstance().mapdata.getCurrentMiddleXLength(), knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 10 + offset, knockoff.getInstance().mapdata.getCurrentMiddleZLength());
                p.teleport(loc);
            }
            p.lookAt(knockoff.getInstance().mapdata.getCurrentMiddleXLength(), knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentMiddleZLength(), LookAnchor.EYES);
        }

    }
    
    private static void spawnSpawnPlatformAndTP(List<String> players, String team) {
        Location middleLoc = new Location(Bukkit.getWorld("world"),
                knockoff.getInstance().getRandomNumber(GameManager.SectionPlaceLocationX, knockoff.getInstance().mapdata.getCurrentXLength()) + 0.5,
                knockoff.getInstance().mapdata.getCurrentMiddleYLength() + knockoff.getInstance().getRandomNumber(5, 8), // TODO temp
                knockoff.getInstance().getRandomNumber(GameManager.SectionPlaceLocationZ, knockoff.getInstance().mapdata.getCurrentZLength()) + 0.5);
        if (knockoff.getInstance().GameManager == null || players.isEmpty()) {
            return;
        }
        List<Block> tempBlockList = new ArrayList<>();
        tempBlockList.add(middleLoc.getBlock());
        tempBlockList.add(middleLoc.clone().add(1, 0, 0).getBlock());
        tempBlockList.add(middleLoc.clone().add(-1, 0, 0).getBlock());
        tempBlockList.add(middleLoc.clone().add(0, 0, 1).getBlock());
        tempBlockList.add(middleLoc.clone().add(1, 0, 1).getBlock());
        tempBlockList.add(middleLoc.clone().add(-1, 0, 1).getBlock());
        tempBlockList.add(middleLoc.clone().add(0, 0, -1).getBlock());
        tempBlockList.add(middleLoc.clone().add(1, 0, -1).getBlock());
        tempBlockList.add(middleLoc.clone().add(-1, 0, -1).getBlock());

        for (Block b : tempBlockList) {
            switch (team) {
                case "blue", "cyan", "green", "lemon" -> {
                    b.setType(Material.WHITE_GLAZED_TERRACOTTA);
                }
                case "lime", "magenta", "orange", "peach" -> {
                    b.setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                }
                case "purple", "white", "yellow", "red" -> {
                    b.setType(Material.GRAY_GLAZED_TERRACOTTA);
                }
                case "weak", "strong" -> {
                    b.setType(Material.BLACK_GLAZED_TERRACOTTA);
                }
            }
            Directional dir = (Directional) b.getBlockData();

            //set direction to match the item model's model
            switch (team) {
                case "blue", "lime", "purple", "weak" -> {
                    dir.setFacing(BlockFace.EAST);
                }
                case "cyan", "magenta", "red", "strong" -> {
                    dir.setFacing(BlockFace.NORTH);
                }
                case "green", "orange", "white" -> {
                    dir.setFacing(BlockFace.SOUTH);
                }
                case "lemon", "peach", "yellow" -> {
                    dir.setFacing(BlockFace.WEST);
                }
            }

            b.setBlockData(dir);
            b.getState().update();
            GameManager.startBreakingCrystal(b, 4 * 20, knockoff.getInstance().getRandomNumber(20, 30), false);
        }

        Location ploc = new Location(Bukkit.getWorld("world"), middleLoc.getX(), middleLoc.getY() + 2, middleLoc.getZ());
        for (String s : players) {
            Player p = Bukkit.getPlayer(s);
            p.sendMessage(text("ploc: X:" + ploc.x() + " Y:" + ploc.y() + " Z:" + ploc.z()));
            p.teleport(ploc);
            p.lookAt(knockoff.getInstance().mapdata.getCurrentMiddleXLength(),
                    knockoff.getInstance().mapdata.getCurrentMiddleYLength(),
                    knockoff.getInstance().mapdata.getCurrentMiddleZLength(), LookAnchor.EYES
            );
        }
    }

    public static void CloneNewMapSection() {
        MapManager.CloneNewMapSection();
    }

    public static void SpawnRandomPowerup(String pu) {
        String powerup;
        if (pu == null) {
            powerup = KnockoffItem.ItemList.get(knockoff.getInstance().getRandomNumber(0, KnockoffItem.ItemList.size())).toString();
        } else {
            powerup = pu;
        }
        boolean IsValidSpot = false;
        Location blockloc = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        Location blockloc2 = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        while (!IsValidSpot && knockoff.getInstance().GameManager != null) { //Last check is here to prevent a rare crash
            blockloc = new Location(Bukkit.getWorld("world"),
                    knockoff.getInstance().getRandomNumber(GameManager.SectionPlaceLocationX, knockoff.getInstance().mapdata.getCurrentXLength()) + 0.5,
                    knockoff.getInstance().getRandomNumber(GameManager.SectionPlaceLocationY, knockoff.getInstance().mapdata.getCurrentYLength()),
                    knockoff.getInstance().getRandomNumber(GameManager.SectionPlaceLocationZ, knockoff.getInstance().mapdata.getCurrentZLength()) + 0.5
            );
            blockloc2 = new Location(Bukkit.getWorld("world"),
                    blockloc.getX(),
                    blockloc.getY() + 1,
                    blockloc.getZ()
            );
            if ((!blockloc.getBlock().getType().equals(Material.AIR)) && blockloc2.getBlock().getType().equals(Material.AIR)) {
                IsValidSpot = true;
            } else {
                IsValidSpot = false;
            }
        }
        KnockoffItem.DropPowerup(new Location(Bukkit.getWorld("world"), blockloc.getBlockX(), blockloc.getBlockY() + 1, blockloc.getBlockZ()), powerup);
    }

    public static void convertBlocktoCrystal(Block b) {
        if (!(b.getType().equals(Material.MANGROVE_LEAVES) || b.isEmpty() || b.getType().equals(Material.LIGHT))) {
            String blockString = b.getType().toString().toLowerCase();
            if (Tag.SLABS.isTagged(b.getType())) {
                BlockData bd = b.getBlockData();
                BlockData bd2 = Material.CUT_COPPER_SLAB.createBlockData();
                bd.copyTo(bd2);
                b.setBlockData(bd2);
            } else if (Tag.STAIRS.isTagged(b.getType())) {
                BlockData bd = b.getBlockData();
                BlockData bd2 = Material.CUT_COPPER_STAIRS.createBlockData();
                bd.copyTo(bd2);
                b.setBlockData(bd2);
            } else if (blockString.contains("glass")) {
                if (blockString.contains("pane")) {
                    BlockData bd = b.getBlockData();
                    BlockData bd2 = Material.PINK_STAINED_GLASS_PANE.createBlockData();
                    bd.copyTo(bd2);
                    b.setBlockData(bd2);
                } else {
                    b.setType(Material.PINK_STAINED_GLASS);
                }
            } else if (Tag.WOOL_CARPETS.isTagged(b.getType()) || Tag.RAILS.isTagged(b.getType())) {
                b.setType(Material.PINK_CARPET);
            } else if (Tag.FENCES.isTagged(b.getType()) || Tag.WALLS.isTagged(b.getType())) {
                b.setType(Material.PINK_STAINED_GLASS_PANE);
            }

            else {
                b.setType(Material.AMETHYST_BLOCK);
            }
        }
    }

    public static void startBreakingCrystal(Block b, int addedDelay, int addedPeriod, boolean convert) {
        if (b.getType().equals(Material.AIR)) {return;} //dont crystallize nothing lol
        if (blocksCrystallizing.contains(b)) {
            return;
        } else {
            blocksCrystallizing.add(b);
            new BukkitRunnable() {
                float breaking = 0.0F;
                int entityID = knockoff.getInstance().getRandomNumber(1, 10000);
                public void run() {
                    if (convert) {
                        convertBlocktoCrystal(b);
                    }
                    FloodgateApi fApi = FloodgateApi.getInstance();
                    if (breaking == 1F || breaking > 1F) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (!fApi.isFloodgatePlayer(p.getUniqueId()) && !(blocksCrystallizing.size() > 250)) { //To prevent lagging on low end bedrock devices
                                p.sendBlockDamage(b.getLocation(), 0, entityID);
                            }
                            if (!b.isEmpty() && b.getLocation().getNearbyEntities(10, 10, 10).contains(p)) {
                                p.playSound(b.getLocation(), "minecraft:block.amethyst_block.break", 1, 1);
                            }
                        }
                        b.setType(Material.AIR);
                        blocksCrystallizing.remove(b);
                        cancel();
                    }
                    if (b.getType().equals(Material.AIR)) { //For if the blocks get broken during this
                        blocksCrystallizing.remove(b);
                        cancel();
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!fApi.isFloodgatePlayer(p.getUniqueId()) && !(blocksCrystallizing.size() > 250)) { //To prevent lagging on low end bedrock devices
                            p.sendBlockDamage(b.getLocation(), breaking, entityID);
                        }
                    }
                    breaking = breaking + 0.2F;
                }
            }.runTaskTimer(knockoff.getInstance(), addedDelay, addedPeriod);
        }
    }

    private static String getMapArrowToMid(Player p) {
        Location p_loc = p.getLocation().clone();
        p_loc.setY(0);
        p_loc.setPitch(0);

        MapData md = knockoff.getInstance().mapdata;
        Location loc = new Location(Bukkit.getWorld("world"), md.getCurrentMiddleXLength(), md.getCurrentMiddleYLength(), md.getCurrentMiddleZLength());
        loc.setY(0);

        Vector blockDirection = loc.subtract(p_loc).toVector().normalize();

        double x1 = blockDirection.getX();
        double z1 = blockDirection.getZ();
        double x2 = p_loc.getDirection().getX();
        double z2 = p_loc.getDirection().getZ();

        double angle = Math.toDegrees(Math.atan2(x1 * z2 - z1 * x2, x1 * x2 + z1 * z2));

        if (angle >= -22.5 && angle <= 22.5) {
            return "\uE110";
        } else if (angle <= 67.5 && angle > 0) {
            return "\uE117";
        } else if (angle <= 112.5 && angle > 0) {
            return "\uE116";
        } else if (angle <= 157.5 && angle > 0) {
            return "\uE115";
        } else if (angle >= -67.5 && angle < 0) {
            return "\uE111";
        } else if (angle >= -112.5 && angle < 0) {
            return "\uE112";
        } else if (angle >= -157.5 && angle < 0) {
            return "\uE113";
        } else if (angle <= 190 && angle >= -190) {
            return "\uE114";
        } else {
            return "?";
        }
    }

    public static List<Block> showdownBlockList = new ArrayList<>();

    public static void startShowdown() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
            if (pd.lives > 1) {
                pd.lives = 1;
                p.sendMessage(text("You have one life remaining and will not respawn when you die!").color(NamedTextColor.RED)); //TODO translatable
                p.playSound(p, "minecraft:block.note_block.pling", 1, 0.5f);
            }
        }
        showdownModeStarted = true;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showTitle(Title.title(
                    text("SHOWDOWN").color(WHITE).decoration(TextDecoration.BOLD, true), //TODO make this translatable
                    translatable("HAS BEGUN!").color(WHITE).decoration(TextDecoration.BOLD, true),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(4), Duration.ofSeconds(1)))
            );
            p.playSound(p, "minecraft:entity.lightning_bolt.thunder", 1, 1);
        }
        showdownCrystallizeMap();
    }

    private static void showdownCrystallizeMap() {
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        try (EditSession editSession = Fawe.instance().getWorldEdit().newEditSession(world)) {
            MapData md = knockoff.getInstance().mapdata;
            Region region = new CuboidRegion(
                    BlockVector3.at(
                            GameManager.SectionPlaceLocationX,
                            GameManager.SectionPlaceLocationY,
                            GameManager.SectionPlaceLocationZ
                    ),
                    BlockVector3.at(
                            GameManager.SectionPlaceLocationX + md.CurrentXLength,
                            GameManager.SectionPlaceLocationY + md.CurrentYLength,
                            GameManager.SectionPlaceLocationZ + md.CurrentZLength
                    )
            );
            for (BlockVector3 bV3 : region) {
                Block b = new Location(Bukkit.getWorld("world"), bV3.x(), bV3.y(), bV3.z()).getBlock();
                if (!b.isEmpty()) {
                    showdownBlockList.add(b);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }

        for (Block b : showdownBlockList) {
            GameManager.startBreakingCrystal(b, knockoff.getInstance().getRandomNumber(4 * 20, 25 * 20), knockoff.getInstance().getRandomNumber(20, 8 * 20), true);
        }
    }

    public static void decideMapDirection() {
        switch (knockoff.getInstance().getRandomNumber(1, 9)) {
            case 1, 4, 7 -> {
                GameManager.plannedDirection = mapDirections.EAST;
            }
            case 2, 5, 8 -> {
                GameManager.plannedDirection = mapDirections.SOUTH;
            }
            case 3, 6, 9 -> {
                GameManager.plannedDirection = mapDirections.WEST;
            }
        }
    }
}

class TabMenu {
    static Component StatsPlayerList = text("");

    public static void SendTabMenu(Player p) {
        StatsPlayerList = text("");

        //Header
        p.sendPlayerListHeader(
                text("\n")
                        .append(text("Crystalized: ").color(LIGHT_PURPLE).append(text("Knockoff").color(GOLD)))
                        .append(text("\n"))
        );


        for (TeamData td : Teams.team_datas) {
            List<String> team = Teams.get_team_from_string(td.name);
            for (String string : team) {
                Player player = Bukkit.getPlayer(string);
                PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                if (player != null) {
                    StatsPlayerList = StatsPlayerList.append();
                    if (pd.isOnline) {
                        if (pd.isPlayerDead) {
                            if (pd.isEliminated) {
                                StatsPlayerList = text("")
                                        .append(StatsPlayerList)
                                        .append(text("\n \uE139 ")
                                        );
                            } else {
                                StatsPlayerList = text("")
                                        .append(StatsPlayerList)
                                        .append(text("\n " + getDeathTimerIcon(player) + " ")
                                        );
                            }
                        } else {
                            StatsPlayerList = text("")
                                    .append(StatsPlayerList)
                                    .append(text("\n \uE138 ")
                                    );
                        }
                    } else {
                        StatsPlayerList = text("")
                                .append(StatsPlayerList)
                                .append(text("\n [Disconnected] ")
                                );
                    }
                    if (!pd.cachedRankIcon_full.equals(text(""))) {
                        StatsPlayerList = StatsPlayerList
                                .append(pd.cachedRankIcon_full).append(text(" "));
                    }
                    if (pd.isEliminated) {
                        StatsPlayerList = StatsPlayerList
                                .append(player.displayName().color(DARK_GRAY).decoration(TextDecoration.STRIKETHROUGH, true));
                    } else {
                        StatsPlayerList = StatsPlayerList
                                .append(player.displayName());
                    }
                    StatsPlayerList = StatsPlayerList
                            .append(text(" \uE101 "))
                            .append(text(pd.getKills()))
                            .append(text(" \uE103 "))
                            .append(text(pd.getDeaths()));
                }
            }
        }

        p.sendPlayerListFooter(text("")
                .append(text("---------------------------------------------------").color(GRAY))
                .append(StatsPlayerList).color(WHITE)
                .append(text("\n---------------------------------------------------\n\n").color(GRAY))
                .append(text("Knockoff Version: " + knockoff.getInstance().getDescription().getVersion()).color(DARK_GRAY))
        );
    }

    private static String getDeathTimerIcon(Player p) {
        PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
        //TODO figure out this shit I hate maths

        return "\uE130";

        //return "unknown";
    }
}

class KnockoffProtocolLib {

    public static PacketAdapter make_allys_glow() {
        return new PacketAdapter(knockoff.getInstance(), PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                GameManager gc = knockoff.getInstance().GameManager;
                PacketContainer packet = event.getPacket();
                Player updated_player = get_player_by_entity_id(packet.getIntegers().read(0));
                if (gc == null
                        || updated_player == null
                        || Teams.GetPlayerTeam(updated_player) != Teams.GetPlayerTeam(event.getPlayer())) {
                    return;
                }
                event.setPacket(packet = packet.deepClone());
                List<WrappedDataValue> wrappedData = packet.getDataValueCollectionModifier().read(0);
                for (WrappedDataValue wdv : wrappedData) {
                    if (wdv.getIndex() == 0) {
                        byte b = (byte) wdv.getValue();
                        b |= 0b01000000;
                        wdv.setValue(b);
                    }
                }
            }
        };
    }

    private static Player get_player_by_entity_id(int id) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getEntityId() == id) {
                return player;
            }
        }
        return null;
    }
}

class HazardsManager {

    public static final List<gg.knockoff.game.hazards.hazard> hazards = new ArrayList<>();

    public HazardsManager() {
        MapData md = knockoff.getInstance().mapdata;

        hazards.clear();
        hazards.add(new gg.knockoff.game.hazards.TNT("tnt"));
        hazards.add(new SlimeTime("slimetime"));
        hazards.add(new FlyingCars("flyingcars"));
        hazards.add(new PoisonBushes("poisonbushes"));
        hazards.add(new FloorIsCrystals("flooriscrystals"));
        hazards.add(new SplitMapInHalf("splitmapinhalf"));
        hazards.add(new Train("train"));
        hazards.add(new WaterSprouts("watersprouts"));

        hazards.add(new PufferfishOfDoom("pufferfish"));
        hazards.add(new BeeAttack("beeattack"));
        hazards.add(new SlimesOfStacking("slimesofstacking"));
        hazards.add(new Lightning("lightning"));

        //Temporary
        switch (md.map_nameString) {
            case "Free Trial" -> {hazards.add(new Exclusive_TrialChamber("TrialChamber"));}
        }

        new BukkitRunnable() {
            int timer = knockoff.getInstance().getRandomNumber(30, 60);
            public void run() {
                if (knockoff.getInstance().GameManager == null) {
                    cancel();
                }
                if (timer == 0 && !knockoff.getInstance().GameManager.showdownModeStarted) {
                    timer = knockoff.getInstance().getRandomNumber(30, 60);
                    NewHazard(hazards.get(knockoff.getInstance().getRandomNumber(0, hazards.size())));
                }

                timer--;
            }
        }.runTaskTimer(knockoff.getInstance(), 0 ,20);
    }

    public void NewHazard(hazard type) {
        try {
            type.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public hazard getHazard(String name) {
        for (hazard h : hazards) {
            if (h.name.equals(name)) {
                return h;
            }
        }
        return null;
    }
}
