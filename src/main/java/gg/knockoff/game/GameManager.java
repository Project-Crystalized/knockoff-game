package gg.knockoff.game;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.google.gson.JsonArray;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
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
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
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

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
    public static int LastSectionPlaceLocationX = 1000;
    public static int LastSectionPlaceLocationY = 0;
    public static int LastSectionPlaceLocationZ = 1000;
    public ArrayList<String> PlayerList = new ArrayList<String>();
    public static String GameState = "game"; //can be "game" (game running), "end" (game ending)

    //Can be "solo" or "team"
    public static String GameType = "Solo";

    public static int Round = 0;
    public static int RoundCounter =0;

    public GameManager() {//Start of the game
        knockoff.getInstance().reloadConfig();
        Bukkit.getServer().sendMessage(text("Starting Game! \n(Note: the server might lag slightly)"));
        GameState = "game";
        for (Entity e : Bukkit.getWorld("world").getEntities()) {
            if (e instanceof TextDisplay) {
                e.remove();
            }
        }
        if (Bukkit.getOnlinePlayers().size() > 13) {
            GameType = "duos";
        } else {
            GameType = "solo";
        }

        PlayerList.clear();
        teams = new Teams();

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

        MapManager.CopyRandomMapSection();
        MapManager.PlaceCurrentlySelectedSection();
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

        new BukkitRunnable() {
            int timer = 0;
            @Override
            public void run() {
                switch (timer) {
                    case 7:
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.showTitle(Title.title(translatable("crystalized.game.generic.go").color(GOLD), text(" "),
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
                                            .append(Component.text("1").color(RED))
                                    ,Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(1), Duration.ofSeconds(1))));
                            player.playSound(player, "crystalized:effect.countdown", 50, 1);
                        }
                        break;
                    case 5:
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.showTitle(Title.title(translatable("crystalized.game.generic.startingin").color(GREEN), text("3").color(GRAY)
                                            .append(Component.text(" 2").color(RED))
                                            .append(Component.text(" 1").color(GRAY))
                                    ,Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(1), Duration.ofSeconds(1))));
                            player.playSound(player, "crystalized:effect.countdown", 50, 1);
                        }
                        break;
                    case 4:
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.showTitle(Title.title(translatable("crystalized.game.generic.startingin").color(GREEN), text("3").color(RED)
                                            .append(Component.text(" 2 1").color(GRAY))
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
                    if (!knockoff.getInstance().getConfig().getBoolean("tourneys.manual_powerup_spawning")) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.playSound(p, "minecraft:block.note_block.pling", 50, 2);
                        }
                        //Will pick a random number between 1 and 20, if its Even it will fire "if", otherwise "else"
                        //Did this because "getRandomNumber(1, 2) == 1" almost always returns 1
                        Server s = Bukkit.getServer();
                        FloodgateApi floodgateapi = FloodgateApi.getInstance();
                        if (knockoff.getInstance().getRandomNumber(1, 20) % 2 == 0) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (floodgateapi.isFloodgatePlayer(p.getUniqueId())) {
                                    p.sendMessage(Component.text("-".repeat(40)));
                                } else {
                                    p.sendMessage(Component.text(" ".repeat(55)).decoration(TextDecoration.STRIKETHROUGH,  true));
                                }
                            }
                            s.sendMessage(Component.text(" "));
                            s.sendMessage(Component.translatable("crystalized.game.knockoff.chat.powerup").color(DARK_AQUA));
                            s.sendMessage(Component.text(" "));
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (floodgateapi.isFloodgatePlayer(p.getUniqueId())) {
                                    p.sendMessage(Component.text("-".repeat(40)));
                                } else {
                                    p.sendMessage(Component.text(" ".repeat(55)).decoration(TextDecoration.STRIKETHROUGH,  true));
                                }
                            }
                            SpawnRandomPowerup(null);
                        } else {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (floodgateapi.isFloodgatePlayer(p.getUniqueId())) {
                                    p.sendMessage(Component.text("-".repeat(40)));
                                } else {
                                    p.sendMessage(Component.text(" ".repeat(55)).decoration(TextDecoration.STRIKETHROUGH,  true));
                                }
                            }
                            s.sendMessage(Component.text(" "));
                            s.sendMessage(Component.translatable("crystalized.game.knockoff.chat.powerup2").color(DARK_AQUA));
                            s.sendMessage(Component.text(" "));
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (floodgateapi.isFloodgatePlayer(p.getUniqueId())) {
                                    p.sendMessage(Component.text("-".repeat(40)));
                                } else {
                                    p.sendMessage(Component.text(" ".repeat(55)).decoration(TextDecoration.STRIKETHROUGH,  true));
                                }
                            }
                            SpawnRandomPowerup(null);
                            SpawnRandomPowerup(null);
                        }
                    }

                }
                if (GameManager.RoundCounter == 0 && GameManager.GameState.equals("game")) {
                    if (!knockoff.getInstance().getConfig().getBoolean("tourneys.manual_map_movement")) {
                        GameManager.CloneNewMapSection();
                        RoundCounter = 60;
                        Round++;
                    }
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0 ,20);



        new BukkitRunnable() {
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    PlayerData pd = getPlayerData(p);
                    if (p.getLocation().clone().add(0,-1,0).getBlock().getType().equals(Material.MANGROVE_LEAVES)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, 0, false, true, true));
                    }

                    //p.sendActionBar(text("" + pd.percent + "%"));

                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 1);

        //TODO clean this shit up this is a mess
        new BukkitRunnable() {
            @Override
            public void run() {
                if (knockoff.getInstance().DevMode) {
                    //This line provides debug info on the current map section
                    Bukkit.getServer().sendActionBar(Component.text("[Debugging] Section data " + knockoff.getInstance().mapdata.currentsection + ". X:" + knockoff.getInstance().mapdata.getCurrentXLength() + ". Y:" + knockoff.getInstance().mapdata.getCurrentYLength() + ". Z:" + knockoff.getInstance().mapdata.getCurrentZLength() + ". MX:" + knockoff.getInstance().mapdata.getCurrentMiddleXLength() + ". MY:" + knockoff.getInstance().mapdata.getCurrentMiddleYLength() + ". MZ:" + knockoff.getInstance().mapdata.getCurrentMiddleZLength()));
                }
                //Should stop this bukkitrunnable once the game ends
                if (knockoff.getInstance().GameManager == null) {cancel();}

                for (Player p : Bukkit.getOnlinePlayers()) {

                    if (p.getLocation().getY() < -30) {//instantly kills the player when they get knocked into the void
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
            if (GameManager.GameType.equals("solo")) {
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
                                p.sendMessage(Component.text("-".repeat(40)).color(GOLD));
                            } else {
                                p.sendMessage(Component.text(" ".repeat(55)).color(GOLD).decoration(TextDecoration.STRIKETHROUGH,  true));
                            }
                        }
                        Bukkit.getServer().sendMessage(Component.text("")
                                .append(Component.text("\n").append(Component.translatable("crystalized.game.knockoff.name").color(GOLD)).append(Component.text(" \uE108").color(WHITE)))
                                .append(Component.text("\n").append(Component.translatable("crystalized.game.generic.gameresults").color(BLUE)))
                        );
                        if (playerDatas.size() > 0) {
                            PlayerData first = playerDatas.get(0);
                            Bukkit.getServer().sendMessage(Component.text("   1st. ")
                                    .append(Component.text(first.player)).color(GREEN).append(text(" ".repeat(20 - first.player.length())))
                                    .append(Component.text("" + first.kills))
                            );
                        }
                        if (playerDatas.size() > 1) {
                            PlayerData second = playerDatas.get(1);
                            Bukkit.getServer().sendMessage(Component.text("   2nd. ")
                                    .append(Component.text(second.player)).color(YELLOW).append(text(" ".repeat(20 - second.player.length())))
                                    .append(Component.text("" + second.kills))
                            );
                        }
                        if (playerDatas.size() > 2) {
                            PlayerData third = playerDatas.get(2);
                            Bukkit.getServer().sendMessage(Component.text("   3rd. ")
                                    .append(Component.text(third.player)).color(YELLOW).append(text(" ".repeat(20 - third.player.length())))
                                    .append(Component.text("" + third.kills))
                            );
                        }

                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (floodgateapi.isFloodgatePlayer(p.getUniqueId())) {
                                p.sendMessage(Component.text("-".repeat(40)).color(GOLD));
                            } else {
                                p.sendMessage(Component.text(" ".repeat(55)).color(GOLD).decoration(TextDecoration.STRIKETHROUGH,  true));
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

            im.itemName(Component.translatable("crystalized.item.nexusblock.name"));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.translatable("crystalized.item.nexusblock.desc").color(DARK_GRAY));
            lore.add(Component.translatable("crystalized.item.nexusblock.desc2").color(DARK_GRAY));
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

        for (PlayerData pd : playerDatas) {
            Bukkit.getLogger().warning(pd.player);
        }

        return null;
    }

    @SuppressWarnings("deprication") //FAWE has deprecation notices from WorldEdit that's printed in console when compiled
    private static void SetupFirstSpawns() {
        if (!Teams.blue.isEmpty()) {
            com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(SectionPlaceLocationX + 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), SectionPlaceLocationZ + 5),
                    BlockVector3.at(SectionPlaceLocationX + 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), SectionPlaceLocationZ + 7));
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
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentZLength() - 5),
                    BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentZLength() - 7));
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
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), SectionPlaceLocationZ + 5),
                    BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), SectionPlaceLocationZ + 7));
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
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(SectionPlaceLocationX + 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentZLength() - 5),
                    BlockVector3.at(SectionPlaceLocationX + 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentZLength() - 7));
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
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(SectionPlaceLocationX + 15, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), SectionPlaceLocationZ + 5),
                    BlockVector3.at(SectionPlaceLocationX + 17, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), SectionPlaceLocationZ + 7));
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
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 15, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentZLength() - 5),
                    BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 17, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentZLength() - 7));
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
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 15, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), SectionPlaceLocationZ + 5),
                    BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 17, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), SectionPlaceLocationZ + 7));
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
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(SectionPlaceLocationX + 15, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentZLength() - 5),
                    BlockVector3.at(SectionPlaceLocationX + 17, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentZLength() - 7));
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
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(SectionPlaceLocationX + 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), SectionPlaceLocationZ + 15),
                    BlockVector3.at(SectionPlaceLocationX + 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), SectionPlaceLocationZ + 17));
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
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentZLength() - 15),
                    BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentZLength() - 17));
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
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), SectionPlaceLocationZ + 15),
                    BlockVector3.at(knockoff.getInstance().mapdata.getCurrentXLength() - 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), SectionPlaceLocationZ + 17));
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
            CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(SectionPlaceLocationX + 5, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentZLength() - 15),
                    BlockVector3.at(SectionPlaceLocationX + 7, knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentZLength() - 17));
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
                Location blueloc = new Location(w, SectionPlaceLocationX + 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, SectionPlaceLocationZ + 6);
                p.teleport(blueloc);
            } else if (Teams.GetPlayerTeam(p).equals("cyan")) {
                Location cyanloc = new Location(w, knockoff.getInstance().mapdata.getCurrentXLength() - 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, knockoff.getInstance().mapdata.getCurrentZLength() - 6);
                p.teleport(cyanloc);
            } else if (Teams.GetPlayerTeam(p).equals("green")) {
                Location greenloc = new Location(w, knockoff.getInstance().mapdata.getCurrentXLength() - 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, SectionPlaceLocationZ + 6);
                p.teleport(greenloc);
            } else if (Teams.GetPlayerTeam(p).equals("lemon")) {
                Location greenloc = new Location(w, SectionPlaceLocationX + 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, knockoff.getInstance().mapdata.getCurrentZLength() - 6);
                p.teleport(greenloc);
            } else if (Teams.GetPlayerTeam(p).equals("lime")) { //Yes im aware this has blueloc as its variable, I copy pasted the first 4 lol
                Location blueloc = new Location(w, SectionPlaceLocationX + 16, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, SectionPlaceLocationZ + 6);
                p.teleport(blueloc);
            } else if (Teams.GetPlayerTeam(p).equals("magenta")) {
                Location cyanloc = new Location(w, knockoff.getInstance().mapdata.getCurrentXLength() - 16, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, knockoff.getInstance().mapdata.getCurrentZLength() - 6);
                p.teleport(cyanloc);
            } else if (Teams.GetPlayerTeam(p).equals("orange")) {
                Location greenloc = new Location(w, knockoff.getInstance().mapdata.getCurrentXLength() - 16, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, SectionPlaceLocationZ + 6);
                p.teleport(greenloc);
            } else if (Teams.GetPlayerTeam(p).equals("peach")) {
                Location greenloc = new Location(w, SectionPlaceLocationX + 16, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, knockoff.getInstance().mapdata.getCurrentZLength() - 6);
                p.teleport(greenloc);
            } else if (Teams.GetPlayerTeam(p).equals("purple")) {
                Location blueloc = new Location(w, SectionPlaceLocationX + 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, SectionPlaceLocationZ + 16);
                p.teleport(blueloc);
            } else if (Teams.GetPlayerTeam(p).equals("red")) {
                Location cyanloc = new Location(w, knockoff.getInstance().mapdata.getCurrentXLength() - 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, knockoff.getInstance().mapdata.getCurrentZLength() - 16);
                p.teleport(cyanloc);
            } else if (Teams.GetPlayerTeam(p).equals("white")) {
                Location greenloc = new Location(w, knockoff.getInstance().mapdata.getCurrentXLength() - 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, SectionPlaceLocationZ + 16);
                p.teleport(greenloc);
            } else if (Teams.GetPlayerTeam(p).equals("yellow")) {
                Location greenloc = new Location(w, SectionPlaceLocationX + 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, knockoff.getInstance().mapdata.getCurrentZLength() - 16);
                p.teleport(greenloc);
            } else {
                Location loc = new Location(w, knockoff.getInstance().mapdata.getCurrentMiddleXLength(), knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 10, knockoff.getInstance().mapdata.getCurrentMiddleZLength());
                p.teleport(loc);
            }
            p.lookAt(knockoff.getInstance().mapdata.getCurrentMiddleXLength(), knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentMiddleZLength(), LookAnchor.EYES);
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
        if (b.getType().equals(Material.MANGROVE_LEAVES) || b.getType().equals(Material.AIR)) {
            //Do nothing
        } else {
            String blockString = b.getType().toString().toLowerCase();
            if (blockString.contains("slab")) {
                b.setType(Material.CUT_COPPER_SLAB);
            } else if (blockString.contains("stairs")) {
                b.setType(Material.CUT_COPPER_STAIRS);
            } else if (blockString.contains("glass")) {
                if (blockString.contains("pane")) {
                    b.setType(Material.PINK_STAINED_GLASS_PANE);
                } else {
                    b.setType(Material.PINK_STAINED_GLASS);
                }
            } else {
                b.setType(Material.AMETHYST_BLOCK);
            }

        }
    }

    public static void startBreakingCrystal(Block b) {
        if (b.getType().equals(Material.AIR)) {return;} //dont crystallize nothing lol
        if (blocksCrystallizing.contains(b)) {
            return;
        } else {
            blocksCrystallizing.add(b);
            new BukkitRunnable() {
                float breaking = 0.0F;
                int entityID = knockoff.getInstance().getRandomNumber(1, 10000);
                public void run() {
                    FloodgateApi fApi = FloodgateApi.getInstance();
                    if (breaking == 1F || breaking > 1F) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (!fApi.isFloodgatePlayer(p.getUniqueId())) { //To prevent lagging on low end bedrock devices
                                p.sendBlockDamage(b.getLocation(), 0, entityID);
                            }
                            p.playSound(b.getLocation(), "minecraft:block.amethyst_block.break", 1, 1);
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
                        if (!fApi.isFloodgatePlayer(p.getUniqueId())) { //To prevent lagging on low end bedrock devices
                            p.sendBlockDamage(b.getLocation(), breaking, entityID);
                        }
                    }
                    breaking = breaking + 0.2F;
                }
            }.runTaskTimer(knockoff.getInstance(), knockoff.getInstance().getRandomNumber(0, 4), knockoff.getInstance().getRandomNumber(13, 20));
        }
    }

    //For map decaying mostly
    public static void startBreakingCrystal(Block b, int addedDelay, int addedPeriod) {
        if (b.getType().equals(Material.AIR)) {return;} //dont crystallize nothing lol
        if (blocksCrystallizing.contains(b)) {
            return;
        } else {
            blocksCrystallizing.add(b);
            new BukkitRunnable() {
                float breaking = 0.0F;
                int entityID = knockoff.getInstance().getRandomNumber(1, 10000);
                public void run() {
                    convertBlocktoCrystal(b);
                    FloodgateApi fApi = FloodgateApi.getInstance();
                    if (breaking == 1F || breaking > 1F) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (!fApi.isFloodgatePlayer(p.getUniqueId())) { //To prevent lagging on low end bedrock devices
                                p.sendBlockDamage(b.getLocation(), 0, entityID);
                            }
                            p.playSound(b.getLocation(), "minecraft:block.amethyst_block.break", 1, 1);
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
                        if (!fApi.isFloodgatePlayer(p.getUniqueId())) { //To prevent lagging on low end bedrock devices
                            p.sendBlockDamage(b.getLocation(), breaking, entityID);
                        }
                    }
                    breaking = breaking + 0.2F;
                }
            }.runTaskTimer(knockoff.getInstance(), addedDelay, addedPeriod);
        }
    }
}

class MapManager {
    public static int LastXLength = 0;
    public static int LastYLength = 0;
    public static int LastZLength = 0;
    static String MoveDir = "";

    public static void CloneNewMapSection() {
        GameManager.LastSectionPlaceLocationX = GameManager.SectionPlaceLocationX;
        GameManager.LastSectionPlaceLocationY = GameManager.SectionPlaceLocationY;
        GameManager.LastSectionPlaceLocationZ = GameManager.SectionPlaceLocationZ;
        LastXLength = knockoff.getInstance().mapdata.CurrentXLength;
        LastYLength = knockoff.getInstance().mapdata.CurrentYLength;
        LastZLength = knockoff.getInstance().mapdata.CurrentZLength;
        Bukkit.getServer().sendMessage(Component.translatable("crystalized.game.knockoff.chat.movetosafety1").color(GOLD)
                .append(Component.translatable("crystalized.game.knockoff.chat.movetosafety2").color(RED).decoration(TextDecoration.BOLD, true))
        );
        CopyRandomMapSection();

        switch (knockoff.getInstance().getRandomNumber(1, 3)) {
            case 1:
                MoveDir = "EAST";
                GameManager.SectionPlaceLocationX = GameManager.LastSectionPlaceLocationX + LastXLength;
                GameManager.SectionPlaceLocationZ = GameManager.LastSectionPlaceLocationZ;
                break;
            case 2:
                MoveDir = "SOUTH";
                GameManager.SectionPlaceLocationX = GameManager.LastSectionPlaceLocationX;
                GameManager.SectionPlaceLocationZ = GameManager.LastSectionPlaceLocationZ + LastZLength;
                break;
            case 3:
                MoveDir = "WEST";
                GameManager.SectionPlaceLocationX = GameManager.LastSectionPlaceLocationX - knockoff.getInstance().mapdata.CurrentXLength;
                GameManager.SectionPlaceLocationZ = GameManager.LastSectionPlaceLocationZ;
                break;
        }


        PlaceCurrentlySelectedSection();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(Title.title(text(""), Component.translatable("crystalized.game.knockoff.chat.movetosafety2").color(RED), Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(4), Duration.ofMillis(500))));
            //
        }


        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, "minecraft:entity.illusioner.mirror_move", 50, 2);
            p.playSound(p, "minecraft:entity.illusioner.prepare_blindness", 50, 0.5F);
            p.playSound(p, "minecraft:block.conduit.ambient", 50, 1);
        }
        turnMapIntoCrystals();
        DecayMapSection();
    }

    //Unused for now, planning to use these for particles
    enum mapDirections{
        undecided,
        EAST,
        SOUTH,
        WEST,
    }

    public static void turnMapIntoCrystals() {
        List<Block> blockList = new ArrayList<>();
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        try (EditSession editSession = com.fastasyncworldedit.core.Fawe.instance().getWorldEdit().newEditSession((com.sk89q.worldedit.world.World) world)) {
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
            //I do this hopefully to make performance on shitty bedrock devices slightly better
            switch (knockoff.getInstance().getRandomNumber(0, 10)) {
                case 0, 2, 4, 6, 8 -> {
                    gm.startBreakingCrystal(b, knockoff.getInstance().getRandomNumber(25, 6 * 20), knockoff.getInstance().getRandomNumber(40, 70));
                }
            }
        }
    }

    public static void DecayMapSection() {
        //WorldEdit/FAWE API documentation is ass, gl understanding this

        /*
        //DEPRECATED Turning map into Crystals
        new BukkitRunnable() {
            int XPos = 0;

            @Override
            public void run() {
                if (knockoff.getInstance().GameManager == null) {cancel();}
                switch (MoveDir) {
                    case "EAST" -> {
                        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
                        if ((GameManager.LastSectionPlaceLocationX + XPos) == (GameManager.LastSectionPlaceLocationX + LastXLength + 1)) {
                            cancel();
                        } else {
                            try (EditSession editSession = com.fastasyncworldedit.core.Fawe.instance().getWorldEdit().newEditSession((com.sk89q.worldedit.world.World) world)) {
                                Region region = new CuboidRegion(
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + XPos,
                                                GameManager.LastSectionPlaceLocationY,
                                                GameManager.LastSectionPlaceLocationZ
                                        ),
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + XPos,
                                                GameManager.LastSectionPlaceLocationY + LastYLength,
                                                GameManager.LastSectionPlaceLocationZ + LastZLength
                                        )
                                );
                                //Mask mask = new BlockMask(editSession.getExtent(), new BaseBlock(BlockTypes.AIR));
                                ExistingBlockMask mask = new ExistingBlockMask(editSession.getExtent());
                                RandomPattern pat = new RandomPattern();
                                BlockState a = BukkitAdapter.adapt(Material.AMETHYST_BLOCK.createBlockData());
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
                    case "SOUTH" -> {
                        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
                        if ((GameManager.LastSectionPlaceLocationZ + XPos) == (GameManager.LastSectionPlaceLocationZ + LastZLength + 1)) {
                            cancel();
                        } else {
                            try (EditSession editSession = com.fastasyncworldedit.core.Fawe.instance().getWorldEdit().newEditSession((com.sk89q.worldedit.world.World) world)) {
                                Region region = new CuboidRegion(
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX,
                                                GameManager.LastSectionPlaceLocationY,
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
                                BlockState a = BukkitAdapter.adapt(Material.AMETHYST_BLOCK.createBlockData());
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
                    case "WEST" -> {
                        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
                        if ((GameManager.LastSectionPlaceLocationX + XPos) == (GameManager.LastSectionPlaceLocationX + LastXLength + 1)) {
                            cancel();
                        } else {
                            try (EditSession editSession = com.fastasyncworldedit.core.Fawe.instance().getWorldEdit().newEditSession((com.sk89q.worldedit.world.World) world)) {
                                Region region = new CuboidRegion(
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + LastXLength - XPos,
                                                GameManager.LastSectionPlaceLocationY,
                                                GameManager.LastSectionPlaceLocationZ
                                        ),
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + LastXLength - XPos,
                                                GameManager.LastSectionPlaceLocationY + LastYLength,
                                                GameManager.LastSectionPlaceLocationZ + LastZLength
                                        )
                                );
                                //Mask mask = new BlockMask(editSession.getExtent(), new BaseBlock(BlockTypes.AIR));
                                ExistingBlockMask mask = new ExistingBlockMask(editSession.getExtent());
                                RandomPattern pat = new RandomPattern();
                                BlockState a = BukkitAdapter.adapt(Material.AMETHYST_BLOCK.createBlockData());
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
        }.runTaskTimer(knockoff.getInstance(), 0, 10);
         */

        //TODO this code is shit but idk how to improve it well
        //Filling crystals with air, this has a delay compared to the previous BukkitRunnable
        //This is literally copy pasted code but with the material changed to AIR
        new BukkitRunnable() {
            int XPos = 0;

            @Override
            public void run() {
                if (knockoff.getInstance().GameManager == null) {cancel();}
                switch (MoveDir) {
                    case "EAST" -> {
                        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
                        if ((GameManager.LastSectionPlaceLocationX + XPos) == (GameManager.LastSectionPlaceLocationX + LastXLength + 1)) {
                            //TODO, Clear area
                            cancel();
                        } else {
                            try (EditSession editSession = com.fastasyncworldedit.core.Fawe.instance().getWorldEdit().newEditSession((com.sk89q.worldedit.world.World) world)) {
                                Region region = new CuboidRegion(
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + XPos,
                                                GameManager.LastSectionPlaceLocationY,
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
                    case "SOUTH" -> {
                        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
                        if ((GameManager.LastSectionPlaceLocationZ + XPos) == (GameManager.LastSectionPlaceLocationZ + LastZLength + 1)) {
                            cancel();
                        } else {
                            try (EditSession editSession = com.fastasyncworldedit.core.Fawe.instance().getWorldEdit().newEditSession((com.sk89q.worldedit.world.World) world)) {
                                Region region = new CuboidRegion(
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX,
                                                GameManager.LastSectionPlaceLocationY,
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
                    case "WEST" -> {
                        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
                        if ((GameManager.LastSectionPlaceLocationX + XPos) == (GameManager.LastSectionPlaceLocationX + LastXLength + 1)) {
                            cancel();
                        } else {
                            try (EditSession editSession = com.fastasyncworldedit.core.Fawe.instance().getWorldEdit().newEditSession((com.sk89q.worldedit.world.World) world)) {
                                Region region = new CuboidRegion(
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + LastXLength - XPos,
                                                GameManager.LastSectionPlaceLocationY,
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
        }.runTaskTimer(knockoff.getInstance(), 7 * 20, 5);
    }

    public static void CopyRandomMapSection() {
        knockoff.getInstance().mapdata.getrandommapsection();
    }

    public static void PlaceCurrentlySelectedSection() {
        JsonArray data = knockoff.getInstance().mapdata.getCurrentsection();
        World world = Bukkit.getWorld("world");
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(world), BlockVector3.at(data.get(1).getAsInt(), data.get(2).getAsInt(), data.get(3).getAsInt()), BlockVector3.at(data.get(4).getAsInt(), data.get(5).getAsInt(), data.get(6).getAsInt()));
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
            String a = knockoff.getInstance().mapdata.getCurrentsection().get(7).getAsString().toLowerCase();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/world \"world\"");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos1 " + GameManager.SectionPlaceLocationX + "," + GameManager.SectionPlaceLocationY + "," + GameManager.SectionPlaceLocationZ);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos2 " + knockoff.getInstance().mapdata.getCurrentXLength() + "," + knockoff.getInstance().mapdata.getCurrentYLength() + "," + knockoff.getInstance().mapdata.getCurrentZLength());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/replace " + a + " air");
        }
    }
}

class TabMenu {
    //static String StatsPlayerList = "";
    static Component StatsPlayerList = text("");

    public static void SendTabMenu(Player p) {
        StatsPlayerList = text("");

        //Header
        p.sendPlayerListHeader(
                text("\n")
                        .append(text("Crystalized: ").color(LIGHT_PURPLE).append(text("KnockOff (Work in Progress)").color(GOLD)))
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
                                .append(player.displayName())
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
    }

    public HazardsManager() {
        IsHazardOver = true;
        HazardList.clear();
        HazardList.add(hazards.tnt);
        HazardList.add(hazards.slimetime);
        HazardList.add(hazards.flyingcars);
        HazardList.add(hazards.poisonbushes);
        HazardList.add(hazards.flooriscrystals);

        CarsList.clear();
        CarsList.add(new NamespacedKey("crystalized", "models/car/military_bus"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/military_van"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/abby_car"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/beat_up_truck"));

        new BukkitRunnable() {
            int timer = knockoff.getInstance().getRandomNumber(30, 60);
            public void run() {
                if (knockoff.getInstance().GameManager == null || knockoff.getInstance().getConfig().getBoolean("tourneys.manual_map_movement")) {
                    cancel();
                }
                if (timer == 0) {
                    timer = knockoff.getInstance().getRandomNumber(30, 60);
                    NewHazard(HazardList.get(knockoff.getInstance().getRandomNumber(0, HazardList.size())));
                }

                timer--;
            }
        }.runTaskTimer(knockoff.getInstance(), 0 ,20);
    }

    public void NewHazard(hazards type) {
        IsHazardOver = false;
        Component HazardMessage = translatable("crystalized.game.knockoff.chat.hazard").color(NamedTextColor.GOLD);

        for (Player player : Bukkit.getOnlinePlayers()) {
            switch (type) {
                case hazards.tnt:
                    player.sendMessage(HazardMessage.append(translatable("block.minecraft.tnt").color(NamedTextColor.RED)));
                    player.showTitle(
                            Title.title(
                                    HazardMessage, translatable("block.minecraft.tnt").color(NamedTextColor.RED),
                                    Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000)))
                    );
                    break;
                case hazards.slimetime:
                    player.sendMessage(HazardMessage.append(translatable("crystalized.game.knockoff.hazard.slimetime").color(NamedTextColor.GREEN)));
                    player.showTitle(
                            Title.title(
                                    HazardMessage, translatable("crystalized.game.knockoff.hazard.slimetime").color(NamedTextColor.GREEN),
                                    Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000)))
                    );
                    break;
                case hazards.flyingcars:
                    player.sendMessage(HazardMessage.append(translatable("crystalized.game.knockoff.hazard.flyingcars").color(NamedTextColor.BLUE)));
                    player.showTitle(
                            Title.title(
                                    HazardMessage, translatable("crystalized.game.knockoff.hazard.flyingcars").color(NamedTextColor.BLUE),
                                    Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000)))
                    );
                    break;
                case hazards.poisonbushes:
                    player.sendMessage(HazardMessage.append(translatable("crystalized.game.knockoff.hazard.poisonbushes").color(DARK_GREEN)));
                    player.showTitle(
                            Title.title(
                                    HazardMessage, translatable("crystalized.game.knockoff.hazard.poisonbushes").color(DARK_GREEN),
                                    Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000)))
                    );
                    break;
                case hazards.flooriscrystals:
                    player.sendMessage(HazardMessage.append(translatable("crystalized.game.knockoff.hazard.flooriscrystals").color(LIGHT_PURPLE)));
                    player.showTitle(
                            Title.title(
                                    HazardMessage, translatable("crystalized.game.knockoff.hazard.flooriscrystals").color(LIGHT_PURPLE),
                                    Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000)))
                    );
                    break;
                default:
                    break;
            }
        }

        switch (type) {
            case hazards.tnt:
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
                            player.playSound(player, "minecraft:entity.tnt.primed",  50, 1);
                        }
                        if (timer == 3) {
                            IsHazardOver = true;
                            cancel();
                        }
                        timer++;
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 40);
                break;

            case hazards.slimetime:
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
                break;

            case hazards.flyingcars:
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
                        if (knockoff.getInstance().GameManager == null) {cancel();}
                        spawnFlyingCar();
                        timer++;
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 20);
                break;
            case hazards.poisonbushes:
                new BukkitRunnable() {
                    int timer = 6;
                    public void run() {
                        spawnBush();
                        if (timer == 0) {
                            IsHazardOver = true;
                            cancel();
                        }
                        timer --;
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 2);
                break;

            case hazards.flooriscrystals:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player, "minecraft:block.conduit.activate", 50, 1);
                }
                new BukkitRunnable() {
                    public void run() {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            Location below = p.getLocation().add(0, -1, 0);
                            //This is dumb, but this should make the radius bigger than 1 singular block
                            crystal(below.getBlock());
                            crystal(below.clone().add(0.5, 0, 0).getBlock());
                            crystal(below.clone().add(0, 0, 0.5).getBlock());
                            crystal(below.clone().add(-0.5, 0, 0).getBlock());
                            crystal(below.clone().add(0, 0, -0.5).getBlock());
                            crystal(below.clone().add(0, -1, 0).getBlock());

                        }
                        if (IsHazardOver) {
                            cancel();
                        }
                    }

                    void crystal(Block b) {
                        GameManager gm = knockoff.getInstance().GameManager;
                        gm.convertBlocktoCrystal(b);
                        gm.startBreakingCrystal(b);
                    }

                }.runTaskTimer(knockoff.getInstance(), 3, 1);

                new BukkitRunnable() {
                    int timer = 10;
                    public void run() {
                        timer --;
                        if (timer == 0) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.playSound(p, "minecraft:block.conduit.deactivate", 50, 1);
                            }

                            IsHazardOver = true;
                            cancel();
                        }
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 20);
                break;
        }
    }

    public static void spawnFlyingCar() {
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
            if ((!blockloc.getBlock().getType().equals(Material.AIR)) && blockloc2.getBlock().getType().equals(Material.AIR)) {
                IsValidSpot = true;
            } else {
                IsValidSpot = false;
            }
        }
        if (knockoff.getInstance().GameManager == null) {
            return;
        }

        Location loc = new Location(Bukkit.getWorld("world"), blockloc.getX(), knockoff.getInstance().mapdata.getCurrentYLength() + 13, blockloc.getZ());
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
            if ((!blockloc.getBlock().getType().equals(Material.AIR)) && blockloc2.getBlock().getType().equals(Material.AIR)) {
                IsValidSpot = true;
            } else {
                IsValidSpot = false;
            }
        }
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
}
