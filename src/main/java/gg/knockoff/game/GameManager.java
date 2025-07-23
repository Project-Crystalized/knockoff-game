package gg.knockoff.game;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.fastasyncworldedit.core.Fawe;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockState;
import io.papermc.paper.entity.LookAnchor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.geysermc.floodgate.api.FloodgateApi;

import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class GameManager { //I honestly think this entire class could be optimised because of how long it is
    public static List<PlayerData> playerDatas;
    public Teams teams;
    public HazardsManager hazards = new HazardsManager();
    public static List<Block> blocksCrystallizing = new ArrayList<>();

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
        Custom,
    }

    //public static String GameType = "Solo";
    public static GameTypes GameType;
    public static mapDirections plannedDirection;

    public static int Round = 0;
    public static int RoundCounter =0;

    enum mapDirections{
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
            @Override
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

        new BukkitRunnable() {
            @Override
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
                    cancel();
                }

                if (RoundCounter == 7 && GameManager.GameState.equals("game") && !showdownModeStarted) {
                    decideMapDirection(); //TODO spawn particles
                }

                if (RoundCounter == 0 && GameManager.GameState.equals("game") && !showdownModeStarted) {
                    if (!knockoff.getInstance().getConfig().getBoolean("tourneys.manual_map_movement")) {
                        GameManager.CloneNewMapSection();
                        RoundCounter = 60;
                        Round++;
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
                        p.showTitle(Title.title(text("" + getMapArrowToMid(p)), translatable("crystalized.game.knockoff.chat.movetosafety2").color(RED), Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(1), Duration.ofMillis(0))));
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
                        } else if (((Item) e).getItemStack().getType().equals(Material.WIND_CHARGE)
                                || ((Item) e).getItemStack().equals(KnockoffItem.BoxingGlove)) {
                            //do nothing
                        } else {
                            e.remove();
                        }
                    }
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0 ,1);
    }

    public static void StartEndGame(String WinningTeam) {
        GameState = "end";
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (GameManager.GameType.equals(GameTypes.StanderedSolos)) {
								Player lastPlayer = Bukkit.getPlayer(Teams.get_team_from_string(WinningTeam).getFirst());
                player.showTitle(Title.title(lastPlayer.displayName(), translatable("crystalized.game.knockoff.win").color(YELLOW),
										Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(5), Duration.ofMillis(1000))));
            } else {
                player.showTitle(Title.title(text("teamed win"), text("placeholder text"), Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(5), Duration.ofMillis(1000))));
            }
            player.playSound(player, "crystalized:effect.ls_game_won", 50, 1);
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

    //TODO this is ugly
    @SuppressWarnings("deprication") //FAWE has deprecation notices from WorldEdit that's printed in console when compiled
    private static void SetupFirstSpawns() {
        MapData md = knockoff.getInstance().mapdata;
        int offset = md.currentSection.getAsJsonObject().get("spawn_offset").getAsInt();
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
    
    private static void spawnSpawnPlatformAndTP(Location middleLoc, String team, Material glassType) {
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
            b.setType(glassType);
        }

        for (String s : Teams.get_team_from_string(team)) {
            Player p = Bukkit.getPlayer(s);
            p.teleportAsync(middleLoc.clone().add(0, 2, 0));
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
        DropPowerup.DropPowerup(new Location(Bukkit.getWorld("world"), blockloc.getBlockX(), blockloc.getBlockY() + 1, blockloc.getBlockZ()), powerup);
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
        switch (knockoff.getInstance().getRandomNumber(1, 3)) {
            case 1 -> {
                GameManager.plannedDirection = mapDirections.EAST;
            }
            case 2 -> {
                GameManager.plannedDirection = mapDirections.SOUTH;
            }
            case 3 -> {
                GameManager.plannedDirection = mapDirections.WEST;
            }
        }
    }
}

class MapManager {
    public static int LastXLength = 0;
    public static int LastYLength = 0;
    public static int LastZLength = 0;

    public static void CloneNewMapSection() {
        GameManager.LastSectionPlaceLocationX = GameManager.SectionPlaceLocationX;
        GameManager.LastSectionPlaceLocationY = GameManager.SectionPlaceLocationY;
        GameManager.LastSectionPlaceLocationZ = GameManager.SectionPlaceLocationZ;
        LastXLength = knockoff.getInstance().mapdata.CurrentXLength;
        LastYLength = knockoff.getInstance().mapdata.CurrentYLength;
        LastZLength = knockoff.getInstance().mapdata.CurrentZLength;
        Bukkit.getServer().sendMessage(translatable("crystalized.game.knockoff.chat.movetosafety1").color(GOLD)
                .append(translatable("crystalized.game.knockoff.chat.movetosafety2").color(RED).decoration(TextDecoration.BOLD, true))
        );
        //CopyRandomMapSection();
        knockoff.getInstance().GameManager.mapMoving = true;

        //In the case the command is used instead of this being called naturally
        if (GameManager.plannedDirection.equals(GameManager.mapDirections.undecided)) {
            GameManager.decideMapDirection();
        }

        switch (GameManager.plannedDirection) {
            case GameManager.mapDirections.EAST:
                GameManager.SectionPlaceLocationX = GameManager.LastSectionPlaceLocationX + LastXLength;
                GameManager.SectionPlaceLocationZ = GameManager.LastSectionPlaceLocationZ;
                break;
            case GameManager.mapDirections.SOUTH:
                GameManager.SectionPlaceLocationX = GameManager.LastSectionPlaceLocationX;
                GameManager.SectionPlaceLocationZ = GameManager.LastSectionPlaceLocationZ + LastZLength;
                break;
            case GameManager.mapDirections.WEST:
                GameManager.SectionPlaceLocationX = GameManager.LastSectionPlaceLocationX - knockoff.getInstance().mapdata.CurrentXLength;
                GameManager.SectionPlaceLocationZ = GameManager.LastSectionPlaceLocationZ;
                break;
        }

        placeNewSection();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(Title.title(text(""), translatable("crystalized.game.knockoff.chat.movetosafety2").color(RED), Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(4), Duration.ofMillis(500))));
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, "minecraft:entity.illusioner.mirror_move", 50, 2);
            p.playSound(p, "minecraft:entity.illusioner.prepare_blindness", 50, 0.5F);
        }
        turnMapIntoCrystals();
        DecayMapSection();
    }

    public static void turnMapIntoCrystals() {
        List<Block> blockList = new ArrayList<>();
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        try (EditSession editSession = Fawe.instance().getWorldEdit().newEditSession(world)) {
            Region region = new CuboidRegion(
                    BlockVector3.at(
                            GameManager.LastSectionPlaceLocationX,
                            GameManager.LastSectionPlaceLocationY,
                            GameManager.LastSectionPlaceLocationZ
                    ),
                    BlockVector3.at(
                            GameManager.LastSectionPlaceLocationX + LastXLength -1,
                            GameManager.LastSectionPlaceLocationY + LastYLength -1, //Subtracting 1 to prevent a bug where section borders are caught within this
                            GameManager.LastSectionPlaceLocationZ + LastZLength -1
                    )
            );
            for (BlockVector3 bV3 : region) {
                Block b = new Location(Bukkit.getWorld("world"), bV3.x(), bV3.y(), bV3.z()).getBlock();
                if (!b.isEmpty()) {
                    blockList.add(b);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }

        GameManager gm = knockoff.getInstance().GameManager;

        for (Block b : blockList) {
            gm.startBreakingCrystal(b, knockoff.getInstance().getRandomNumber(2 * 20, 8 * 20), knockoff.getInstance().getRandomNumber(3 * 20, 5 * 20), true);
        }
    }

    public static void DecayMapSection() {
        //WorldEdit/FAWE API documentation is ass, gl understanding this

        //TODO this code is shit but idk how to improve it well
        //Filling crystals with air, this has a delay compared to the previous BukkitRunnable
        //This is literally copy pasted code but with the material changed to AIR
        new BukkitRunnable() {
            int XPos = 0;

            @Override
            public void run() {
                if (knockoff.getInstance().GameManager == null) {cancel();}
                switch (GameManager.plannedDirection) {
                    case GameManager.mapDirections.EAST -> {
                        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
                        if ((GameManager.LastSectionPlaceLocationX + XPos) == (GameManager.LastSectionPlaceLocationX + LastXLength + 1)) {
                            finishDecay();
                            cancel();
                        } else {
                            try (EditSession editSession = Fawe.instance().getWorldEdit().newEditSession((com.sk89q.worldedit.world.World) world)) {
                                Region region = new CuboidRegion(
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + XPos,
                                                GameManager.LastSectionPlaceLocationY - 20,
                                                GameManager.LastSectionPlaceLocationZ
                                        ),
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + XPos - 5,
                                                GameManager.LastSectionPlaceLocationY + LastYLength,
                                                GameManager.LastSectionPlaceLocationZ + LastZLength
                                        )
                                );
                                //Mask mask = new BlockMask(editSession.getExtent(), new BaseBlock(BlockTypes.AIR));
                                ExistingBlockMask mask = new ExistingBlockMask(editSession.getExtent());
                                RandomPattern pat = new RandomPattern();
                                BlockState a = BukkitAdapter.adapt(Material.AIR.createBlockData());
                                pat.add(a, 1);
                                editSession.replaceBlocks(region, mask, pat);
                                editSession.flushQueue();
                            } catch (Exception e) {
                                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                                e.printStackTrace();
                            }
                            XPos++;
                        }
                    }
                    case GameManager.mapDirections.SOUTH -> {
                        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
                        if ((GameManager.LastSectionPlaceLocationZ + XPos) == (GameManager.LastSectionPlaceLocationZ + LastZLength + 1)) {
                            finishDecay();
                            cancel();
                        } else {
                            try (EditSession editSession = Fawe.instance().getWorldEdit().newEditSession((com.sk89q.worldedit.world.World) world)) {
                                Region region = new CuboidRegion(
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX,
                                                GameManager.LastSectionPlaceLocationY - 20,
                                                GameManager.LastSectionPlaceLocationZ + XPos
                                        ),
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + LastXLength,
                                                GameManager.LastSectionPlaceLocationY + LastYLength,
                                                GameManager.LastSectionPlaceLocationZ + XPos
                                        )
                                );
                                //Mask mask = new BlockMask(editSession.getExtent(), new BaseBlock(BlockTypes.AIR));
                                ExistingBlockMask mask = new ExistingBlockMask(editSession.getExtent());
                                RandomPattern pat = new RandomPattern();
                                BlockState a = BukkitAdapter.adapt(Material.AIR.createBlockData());
                                pat.add(a, 1);
                                editSession.replaceBlocks(region, mask, pat);
                                editSession.flushQueue();
                            } catch (Exception e) {
                                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                                e.printStackTrace();
                            }
                            XPos++; //cba renaming
                        }
                    }
                    case GameManager.mapDirections.WEST -> {
                        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
                        if ((GameManager.LastSectionPlaceLocationX + XPos) == (GameManager.LastSectionPlaceLocationX + LastXLength + 1)) {
                            finishDecay();
                            cancel();
                        } else {
                            try (EditSession editSession = Fawe.instance().getWorldEdit().newEditSession((com.sk89q.worldedit.world.World) world)) {
                                Region region = new CuboidRegion(
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + LastXLength - XPos,
                                                GameManager.LastSectionPlaceLocationY - 20,
                                                GameManager.LastSectionPlaceLocationZ
                                        ),
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + LastXLength - XPos + 5,
                                                GameManager.LastSectionPlaceLocationY + LastYLength,
                                                GameManager.LastSectionPlaceLocationZ + LastZLength
                                        )
                                );
                                //Mask mask = new BlockMask(editSession.getExtent(), new BaseBlock(BlockTypes.AIR));
                                ExistingBlockMask mask = new ExistingBlockMask(editSession.getExtent());
                                RandomPattern pat = new RandomPattern();
                                BlockState a = BukkitAdapter.adapt(Material.AIR.createBlockData());
                                pat.add(a, 1);
                                editSession.replaceBlocks(region, mask, pat);
                                editSession.flushQueue();
                            } catch (Exception e) {
                                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                                e.printStackTrace();
                            }
                            XPos++;
                        }
                    }
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 8 * 20, 7);
    }

    private static void finishDecay() {
        GameManager.LastSectionPlaceLocationX = -1000;
        GameManager.LastSectionPlaceLocationY = 0;
        GameManager.LastSectionPlaceLocationZ = -1000;
        knockoff.getInstance().GameManager.mapMoving = false;
        GameManager.plannedDirection = GameManager.mapDirections.undecided;
    }

    //DEPRECATED
    //public static void CopyRandomMapSection() {
    //    knockoff.getInstance().mapdata.getrandommapsection();
    //}

    public static void placeNewSection() {
        //JsonArray data = knockoff.getInstance().mapdata.getCurrentsection();
        JsonElement sectionData = knockoff.getInstance().mapdata.getNewRandomSection();
        JsonObject sectionJson = sectionData.getAsJsonObject();
        JsonArray from = sectionJson.get("from").getAsJsonArray();
        JsonArray to = sectionJson.get("to").getAsJsonArray();
        World world = Bukkit.getWorld("world");

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            CuboidRegion region = new CuboidRegion(
                    BukkitAdapter.adapt(world),
                    BlockVector3.at(from.get(0).getAsInt(), from.get(1).getAsInt(), from.get(2).getAsInt()),
                    BlockVector3.at(to.get(0).getAsInt(), to.get(1).getAsInt(), to.get(2).getAsInt())
            );
            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                    BukkitAdapter.adapt(world), region, clipboard, region.getMinimumPoint()
            );
            Operations.complete(forwardExtentCopy);

            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(GameManager.SectionPlaceLocationX, GameManager.SectionPlaceLocationY, GameManager.SectionPlaceLocationZ))
                    .build();
            Operations.complete(operation);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }
        if (!knockoff.getInstance().DevMode) {
            //Could be optimised, this needs to use FAWE's API, but we're using commands instead since idk how the api works for this
            Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> {
                String a = sectionJson.get("remove_block").getAsString();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/world \"world\"");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos1 " + GameManager.SectionPlaceLocationX + "," + GameManager.SectionPlaceLocationY + "," + GameManager.SectionPlaceLocationZ);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos2 " + knockoff.getInstance().mapdata.getCurrentXLength() + "," + knockoff.getInstance().mapdata.getCurrentYLength() + "," + knockoff.getInstance().mapdata.getCurrentZLength());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/replace " + a + " air");
            }, 2);
        }
    }


    //For these 2 booleans, I dont think theres a better way of doing these
    //We could make a worldedit region and do shit with that, but I feel like making worldedit actions everytime this is called is stupid - Callum
    public static boolean isInsideCurrentSection(Location loc) {
        if (!(
                loc.getBlockY() > knockoff.getInstance().mapdata.getCurrentYLength() || loc.getBlockY() < (GameManager.SectionPlaceLocationY - 20)
                || loc.getBlockX() > knockoff.getInstance().mapdata.getCurrentXLength() || loc.getBlockX() < GameManager.SectionPlaceLocationX
                || loc.getBlockZ() > knockoff.getInstance().mapdata.getCurrentZLength() || loc.getBlockZ() < GameManager.SectionPlaceLocationZ
        )) {
            return true;
        }
        return false;
    }

    public static boolean isInsideDecayingSection(Location loc) {
        if (!(
                loc.getBlockY() > GameManager.LastSectionPlaceLocationY + MapManager.LastYLength || loc.getBlockY() < (GameManager.LastSectionPlaceLocationY - 20)
                || loc.getBlockX() > GameManager.LastSectionPlaceLocationX + MapManager.LastXLength || loc.getBlockX() < GameManager.LastSectionPlaceLocationX
                || loc.getBlockZ() > GameManager.LastSectionPlaceLocationZ + MapManager.LastZLength || loc.getBlockZ() < GameManager.LastSectionPlaceLocationZ
        )) {
            return true;
        }
        return false;
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

        //Footer
        // Could be optimised too
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
            if (pd.isOnline) {
                if (pd.isPlayerDead) {
                    if (pd.isEliminated) {
                        StatsPlayerList = text("")
                                .append(StatsPlayerList)
                                .append(text("\n \uE139 "))
                                .append(player.displayName().color(DARK_GRAY).decoration(TextDecoration.STRIKETHROUGH, true))
                                .append(text(" \uE101 ")
                                .append(text(pd.getKills()))
                                .append(text(" \uE103 "))
                                .append(text(pd.getDeaths()))
                            );
                    } else {
                        StatsPlayerList = text("")
                                .append(StatsPlayerList)
                                .append(text("\n " + getDeathTimerIcon(player) + " "))
                                .append(player.displayName())
                                .append(text(" \uE101 ")
                                .append(text(pd.getKills()))
                                .append(text(" \uE103 "))
                                .append(text(pd.getDeaths()))
                            );
                    }
                } else {
                    StatsPlayerList = text("")
                            .append(StatsPlayerList)
                            .append(text("\n \uE138 "))
                            .append(player.displayName())
                            .append(text(" \uE101 ")
                            .append(text(pd.getKills()))
                            .append(text(" \uE103 "))
                            .append(text(pd.getDeaths()))
                    );
                }
            } else {
                StatsPlayerList = text("")
                        .append(StatsPlayerList)
                        .append(text("\n [Disconnected] "))
                        .append(player.displayName())
                        .append(text(" \uE101 ")
                        .append(text(pd.getKills()))
                        .append(text(" \uE103 "))
                        .append(text(pd.getDeaths()))
                    );
            }
        }


        p.sendPlayerListFooter(text("")
                .append(text("---------------------------------------------------").color(GRAY)
                .append(StatsPlayerList).color(WHITE)
                .append(text("\n---------------------------------------------------\n\n").color(GRAY))
                .append(text("Knockoff Version: " + knockoff.getInstance().getDescription().getVersion()).color(DARK_GRAY)))
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

    public static final List<hazards> HazardList = new ArrayList<>();
    private static final List<NamespacedKey> CarsList = new ArrayList<>();
    private static boolean IsHazardOver = false;

    public enum hazards{
        tnt,
        slimetime,
        flyingcars,
        poisonbushes,
        flooriscrystals,
        splitmapinhalf,
        train,
        watersprouts,
    }

    public HazardsManager() {
        IsHazardOver = true;
        HazardList.clear();
        HazardList.add(hazards.tnt);
        HazardList.add(hazards.slimetime);
        HazardList.add(hazards.flyingcars);
        HazardList.add(hazards.poisonbushes);
        HazardList.add(hazards.flooriscrystals);
        HazardList.add(hazards.splitmapinhalf);
        HazardList.add(hazards.watersprouts);

        CarsList.clear();
        CarsList.add(new NamespacedKey("crystalized", "models/car/abby_car"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/abby_minicar"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/abby_minicar2"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/abby_truck"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/beat_up_truck"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/broken_car"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/firetruck"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/military_bus"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/military_van"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/taxi"));

        new BukkitRunnable() {
            int timer = knockoff.getInstance().getRandomNumber(30, 60);
            public void run() {
                if (knockoff.getInstance().GameManager == null) {
                    cancel();
                }
                if (timer == 0 && !knockoff.getInstance().GameManager.showdownModeStarted) {
                    timer = knockoff.getInstance().getRandomNumber(30, 60);
                    NewHazard(HazardList.get(knockoff.getInstance().getRandomNumber(0, HazardList.size())));
                }

                timer--;
            }
        }.runTaskTimer(knockoff.getInstance(), 0 ,20);
    }

    public void NewHazard(hazards type) {
        IsHazardOver = false;
        switch (type) {
            case hazards.tnt:
                title(type, null); //Due to TNT's different translation string, we set the color to null since its already set in its special switch case.
                break;
            case hazards.slimetime:
                title(type, GREEN);
                break;
            case hazards.poisonbushes:
                title(type, DARK_GREEN);
                break;
            case hazards.flooriscrystals, splitmapinhalf:
                title(type, LIGHT_PURPLE);
                break;
            case hazards.flyingcars, hazards.watersprouts:
                title(type, BLUE);
                break;
            case hazards.train:
                title(type, GRAY);
                break;
            default:
                break;
        }

        //If using intellij, would recommend collapsing these case statements by the arrow at the left side of the IDE
        switch (type) {
            case tnt -> {
                new BukkitRunnable() {
                    int timer = 0;

                    @Override
                    public void run() {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                            if (!pd.isPlayerDead) {
                                Location loc = new Location(player.getWorld(), player.getX(), player.getY() + 10, player.getZ(), player.getYaw(), player.getPitch());
                                TNTPrimed TNT = player.getWorld().spawn(loc, TNTPrimed.class, entity -> {

                                });
                            }
                            player.playSound(player, "minecraft:entity.tnt.primed", 50, 1);
                        }
                        if (timer == 3 || knockoff.getInstance().GameManager == null) {
                            IsHazardOver = true;
                            cancel();
                        }
                        timer++;
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 40);
            }
            case slimetime -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 12 * 20, 2, false, false, true));
                    player.playSound(player, "minecraft:block.conduit.activate", 50, 1);
                }
                new BukkitRunnable() {
                    int timer = 0;

                    @Override
                    public void run() {
                        if (timer == 12) { //This should last the jump boost's duration
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.playSound(player, "minecraft:block.conduit.deactivate", 50, 1);
                            }
                            IsHazardOver = true;
                            cancel();
                        }
                        timer++;
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 20);
            }
            case flyingcars -> {
                new BukkitRunnable() {
                    int timer = 0;

                    @Override
                    public void run() {
                        if (timer == 12) { //This should last the jump boost's duration
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.playSound(player, "minecraft:block.beacon.deactivate", 50, 1);
                            }
                            IsHazardOver = true;
                            cancel();
                        }
                        if (knockoff.getInstance().GameManager == null) {
                            cancel();
                        }
                        spawnFlyingCar();
                        timer++;
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 20);
            }
            case poisonbushes -> {
                new BukkitRunnable() {
                    int timer = 6;

                    public void run() {
                        spawnBush();
                        if (timer == 0) {
                            IsHazardOver = true;
                            cancel();
                        }
                        timer--;
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 2);
            }
            case flooriscrystals -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player, "minecraft:block.conduit.activate", 50, 1);
                }
                new BukkitRunnable() {
                    public void run() {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
                                Location below = p.getLocation().add(0, -1, 0);
                                //This is dumb, but this should make the radius bigger than 1 singular block
                                crystal(below.getBlock());
                                crystal(below.clone().add(0.5, 0, 0).getBlock());
                                crystal(below.clone().add(0, 0, 0.5).getBlock());
                                crystal(below.clone().add(-0.5, 0, 0).getBlock());
                                crystal(below.clone().add(0, 0, -0.5).getBlock());
                                crystal(below.clone().add(0, -1, 0).getBlock());
                                crystal(below.clone().add(0, 1, 0).getBlock());
                            }
                        }
                        if (IsHazardOver || knockoff.getInstance().GameManager == null) {
                            cancel();
                        }
                    }

                    void crystal(Block b) {
                        GameManager gm = knockoff.getInstance().GameManager;
                        gm.startBreakingCrystal(b, knockoff.getInstance().getRandomNumber(0, 4), knockoff.getInstance().getRandomNumber(13, 20), true);
                    }

                }.runTaskTimer(knockoff.getInstance(), 3, 1);
                new BukkitRunnable() {
                    int timer = 10;

                    public void run() {
                        timer--;
                        if (timer == 0) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.playSound(p, "minecraft:block.conduit.deactivate", 50, 1);
                            }

                            IsHazardOver = true;
                            cancel();
                        }
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 20);
            }
            case splitmapinhalf -> {
                //TODO WIP
                List<Block> blockList = new ArrayList<>();

                com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
                try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                    MapData md = knockoff.getInstance().mapdata;
                    int X1;
                    int X2;
                    int Z1;
                    int Z2;
                    int corruptionSize;
                    int offset = knockoff.getInstance().getRandomNumber(-5, 5);

                    switch (knockoff.getInstance().getRandomNumber(1, 10)) {
                        case 2, 4, 6, 8, 10-> {
                            //X axis
                            corruptionSize = 4; //md.CurrentXLength / 8;
                            X1 = md.getCurrentMiddleXLength() - corruptionSize + offset;
                            X2 = md.getCurrentMiddleXLength() + corruptionSize + offset;
                            Z1 = GameManager.SectionPlaceLocationZ;
                            Z2 = md.getCurrentZLength();
                        }
                        default -> {
                            //Z axis
                            corruptionSize = 4; //md.CurrentZLength / 8;
                            X1 = GameManager.SectionPlaceLocationX;
                            X2 = md.getCurrentXLength();
                            Z1 = md.getCurrentMiddleZLength() - corruptionSize + offset;
                            Z2 = md.getCurrentMiddleZLength() + corruptionSize + offset;
                        }
                    }
                    CuboidRegion region = new CuboidRegion(
                            world,
                            BlockVector3.at(
                                    X1,
                                    GameManager.SectionPlaceLocationY,
                                    Z1),
                            BlockVector3.at(
                                    X2,
                                    md.getCurrentYLength(),
                                    Z2)
                    );
                    for (BlockVector3 bV3 : region) {
                        Block b = new Location(Bukkit.getWorld("world"), bV3.x(), bV3.y(), bV3.z()).getBlock();
                        if (!b.isEmpty()) {
                            blockList.add(b);
                        }
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p, "minecraft:entity.wither.spawn", 1, 2);
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                    e.printStackTrace();
                }
                for (Block b : blockList) {
                    GameManager.startBreakingCrystal(b, knockoff.getInstance().getRandomNumber(1, 18), knockoff.getInstance().getRandomNumber(13, 20), true);
                }

                IsHazardOver = true; //temp
            }
            case train -> {
                boolean goZinsteadofX;
                switch (knockoff.getInstance().getRandomNumber(0, 10)) {
                    case 0, 2, 4, 6, 8, 10 -> {
                        goZinsteadofX = false;
                    }
                    default -> {
                        goZinsteadofX = true;
                    }
                }
                //If we want this hazard to be actually effective and not go some random direction nobody is at, we should try to target 1 player
                List<Player> playerList = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    playerList.add(p);
                }
                Player randomPlayer = playerList.get(knockoff.getInstance().getRandomNumber(0, playerList.size()));

                new BukkitRunnable() {
                    int timer = 3;
                    //int Z = knockoff.getInstance().getRandomNumber(GameManager.SectionPlaceLocationZ + 5, knockoff.getInstance().mapdata.getCurrentZLength() - 5);
                    //int Y = knockoff.getInstance().getRandomNumber(GameManager.SectionPlaceLocationY + 4, knockoff.getInstance().mapdata.getCurrentYLength() - 10);
                    double Z = randomPlayer.getZ();
                    double Y = randomPlayer.getY();
                    public void run() {
                        if (goZinsteadofX) {
                            //Z
                            Z = randomPlayer.getX();
                            spawnTrain(
                                    GameManager.SectionPlaceLocationZ,
                                    knockoff.getInstance().mapdata.getCurrentZLength(),
                                    Z,
                                    Y,
                                    "models/car/abby_truck", //TODO placeholder model
                                    true
                            );
                        } else {
                            //X
                            spawnTrain(
                                    GameManager.SectionPlaceLocationX,
                                    knockoff.getInstance().mapdata.getCurrentXLength(),
                                    Z,
                                    Y,
                                    "models/car/abby_truck", //TODO placeholder model
                                    false
                            );
                        }

                        timer--;
                        if (timer == 0) {
                            cancel();
                        }
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 5);
            }
            case watersprouts -> {
                //Play sound to indicate this hazard started
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p, "minecraft:item.trident.riptide_1", 1, 1);
                }
                new BukkitRunnable() {
                    int timer = 7;

                    public void run() {
                        spawnWaterSprout();
                        if (timer == 0 || knockoff.getInstance().GameManager == null) {
                            cancel();
                            IsHazardOver = true; //temp
                        }
                        timer--;
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 20);

            }
        }
    }

    private static void title(hazards h, TextColor color) {
        Component HazardMessage = translatable("crystalized.game.knockoff.chat.hazard").color(GOLD);
        switch (h) {
            case hazards.tnt -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(HazardMessage.append(translatable("block.minecraft.tnt").color(RED)));
                    p.showTitle(
                            Title.title(
                                    HazardMessage, translatable("block.minecraft.tnt").color(RED),
                                    Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000)))
                    );
                }
            }
            default -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(HazardMessage.append(translatable("crystalized.game.knockoff.hazard." + h).color(color)));
                    p.showTitle(
                            Title.title(
                                    HazardMessage, translatable("crystalized.game.knockoff.hazard." + h).color(color),
                                    Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000)))
                    );
                }
            }
        }
    }

    private static Location getValidSpot(boolean get2loc) {
        boolean IsValidSpot = false;
        Location blockloc = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        Location blockloc2 = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        while (!IsValidSpot && knockoff.getInstance().GameManager != null) {
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
            if ((!blockloc.getBlock().isEmpty()) && blockloc2.getBlock().isEmpty()) {
                IsValidSpot = true;
            } else {
                IsValidSpot = false;
            }
        }
        if (get2loc) {
            return blockloc2;
        } else {
            return blockloc;
        }
    }

    public static void spawnFlyingCar() {
        Location validLoc = getValidSpot(false);
        Location loc = new Location(Bukkit.getWorld("world"), validLoc.getX(), knockoff.getInstance().mapdata.getCurrentYLength() + 13, validLoc.getZ());
        ItemStack item = new ItemStack(Material.CHARCOAL);
        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(CarsList.get(knockoff.getInstance().getRandomNumber(0, CarsList.size())));
        item.setItemMeta(meta);

        //No idea how to launch a fireball from the server, this is the next best thing I guess
        ArmorStand tempentity = Bukkit.getWorld("world").spawn(loc, ArmorStand.class, entity -> {
            entity.setRotation(0, 90);
        });

        Fireball ball = tempentity.launchProjectile(Fireball.class, tempentity.getEyeLocation().getDirection());
        ball.getLocation().add(ball.getVelocity().normalize().multiply(1.05));
        ball.setYield(6);
        ball.setVisualFire(false);
        //ball.setVisibleByDefault(false); //Does weird ass visual bugs, dont uncomment this

        ArmorStand car = Bukkit.getWorld("world").spawn(loc, ArmorStand.class, entity -> {
            entity.getEquipment().setHelmet(item);
            entity.setVisible(false);
            ball.addPassenger(entity);
            entity.setGlowing(true);
        });
        tempentity.remove();

        new BukkitRunnable() {
            public void run() {
                if (ball.isDead()) {
                    car.remove();
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 1);
    }

    //This can override blocks but who cares, maps get copy pasted for gameplay anyways
    private static void spawnBush() {
        Location blockloc2 = getValidSpot(true);
        if (knockoff.getInstance().GameManager == null) {return;}

        blockloc2.getBlock().setType(Material.MANGROVE_LEAVES);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(blockloc2, "minecraft:block.cherry_leaves.place", 1, 1);
        }
        blockloc2.clone().add(new Vector(1,0,0)).getBlock().setType(Material.MANGROVE_LEAVES);
        blockloc2.clone().add(new Vector(0,0,1)).getBlock().setType(Material.MANGROVE_LEAVES);
        blockloc2.clone().add(new Vector(1,0,1)).getBlock().setType(Material.MANGROVE_LEAVES);

        //Make the bushes look different and slightly less boring and predictable
        int i = knockoff.getInstance().getRandomNumber(0, 2);
        switch (i) {
            case 1 -> {
                blockloc2.clone().add(new Vector(-1,0,0)).getBlock().setType(Material.MANGROVE_LEAVES);
                blockloc2.clone().add(new Vector(-1,0,-1)).getBlock().setType(Material.MANGROVE_LEAVES);
                blockloc2.clone().add(new Vector(0,0,-1)).getBlock().setType(Material.MANGROVE_LEAVES);
                blockloc2.clone().add(new Vector(0,1,0)).getBlock().setType(Material.MANGROVE_LEAVES);
            }
            case 2 -> {
                blockloc2.clone().add(new Vector(1,1,0)).getBlock().setType(Material.MANGROVE_LEAVES);
                blockloc2.clone().add(new Vector(0,1,1)).getBlock().setType(Material.MANGROVE_LEAVES);
                blockloc2.clone().add(new Vector(1,1,1)).getBlock().setType(Material.MANGROVE_LEAVES);
                blockloc2.clone().add(new Vector(0,1,0)).getBlock().setType(Material.MANGROVE_LEAVES);
            }
            default -> {
                //Do nothing
            }
        }
    }

    private static void spawnWaterSprout() {
        List<Location> locs = new ArrayList<>();
        Location center = getValidSpot(false);
        if (knockoff.getInstance().GameManager == null) {return;}
        locs.add(center);
        locs.add(center.clone().add(1, 0, 0)); //right
        locs.add(center.clone().add(-1, 0, 0)); //left
        locs.add(center.clone().add(0, 0, 1)); //south
        locs.add(center.clone().add(0, 0, -1)); //north

        for (Location l : locs) {
            if (MapManager.isInsideCurrentSection(l)) {
                l.getBlock().setType(Material.SOUL_SAND);
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(center, "minecraft:item.trident.riptide_3", 3, 1);
        }

        new BukkitRunnable() {
            int timer = 7;
            public void run() {
                for (Location l : locs) {
                    l.add(0, 1, 0);
                    if (MapManager.isInsideCurrentSection(l)) {
                        l.getBlock().setType(Material.WATER); //This can replace blocks, not my problem
                    }
                }
                timer--;
                if (timer == 0) {
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 3);
    }

    //bool value; if true will treat startX and endX as Z values and double Z as an X value. for more randomness - Callum
    private static void spawnTrain(int startX, int endX, double Z, double Y, String itemModel, boolean swapXandZ) {
        Location loc = new Location(Bukkit.getWorld("world"),
                startX,
                Y,
                Z,
                -90, 0
        );
        if (swapXandZ) {
            loc = new Location(loc.getWorld(), Z, Y, startX, 0, 0);
        }
        ArmorStand train = loc.getWorld().spawn(loc, ArmorStand.class, entity -> {
            ItemStack item = new ItemStack(Material.CHARCOAL);
            ItemMeta meta = item.getItemMeta();
            meta.setItemModel(new NamespacedKey("crystalized", itemModel));
            item.setItemMeta(meta);
            entity.setItem(EquipmentSlot.HEAD, item);
            entity.addDisabledSlots(EquipmentSlot.HEAD);
            entity.addDisabledSlots(EquipmentSlot.HAND);
            entity.addDisabledSlots(EquipmentSlot.OFF_HAND);
            entity.setInvisible(true);
            entity.setInvulnerable(true);
            entity.setGlowing(true);
        });
        BoundingBox hitbox = train.getBoundingBox();
        hitbox.resize(4, 4, 4, -4, -4, -4);
        new BukkitRunnable() {
            public void run() {
                if (knockoff.getInstance().GameManager == null ||
                        ( (!swapXandZ && train.getLocation().getX() > endX) || (swapXandZ && train.getLocation().getZ() > endX) )
                ) {
                    train.remove();
                    cancel();
                }
                if (swapXandZ) {
                    train.setVelocity(new Vector(0, 0.2, 0.8));
                } else {
                    train.setVelocity(new Vector(0.8, 0.2, 0));
                }


                //player knockback
                for (Entity e : train.getNearbyEntities(4, 4, 4)) {
                    if (e instanceof Player p) {
                        PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
                        p.setVelocity(new Vector(0.5, 3, 0));
                        pd.percent = pd.percent + knockoff.getInstance().getRandomNumber(40, 60);
                    }
                }

                for (Block b : getNearbyBlocks(train.getLocation(), 5, 5, 5)) {
                    if (!b.isEmpty()) {
                        b.breakNaturally(true);
                    }
                }
            }

            //this is dumb, dont have a better way of doing this. might be unsafe also
            Set<Block> getNearbyBlocks(Location center, int x, int y, int z) {
                Set<Block> list = new HashSet<>();
                Location loc = center.subtract(x/2, y/2, z/2);
                int X = x;
                while (X != 0) {
                    int Y = y;
                    while (Y != 0) {
                        int Z = z;
                        while (Z != 0) {
                            list.add(loc.clone().add(X, Y, Z).getBlock());
                            Z--;
                        }
                        Y--;
                    }
                    X--;
                }

                return list;
            }

        }.runTaskTimer(knockoff.getInstance(), 1, 5);
    }
}
