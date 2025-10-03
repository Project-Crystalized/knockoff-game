package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.geysermc.floodgate.api.FloodgateApi;

import static net.kyori.adventure.text.Component.*;

public class ScoreboardManager {

    public static void SetPlayerScoreboard(Player player) {

        FloodgateApi floodgateapi = FloodgateApi.getInstance();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        //This should run after Teams.java is initalized and has sorted the teams
        //Fuck this part also, took me a while to figure out, and the same code copy pasted 12 times isn't helping it being shit - Callum
        Team sbblue = scoreboard.registerNewTeam("sbblue");
        sbblue.color(NamedTextColor.DARK_BLUE);
        sbblue.setAllowFriendlyFire(false);
        sbblue.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Teams.blue.contains(p.getName())) {
                sbblue.addPlayer(p);
            }
        }
        Team sbcyan = scoreboard.registerNewTeam("sbcyan");
        sbcyan.color(NamedTextColor.DARK_AQUA);
        sbcyan.setAllowFriendlyFire(false);
        sbcyan.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Teams.cyan.contains(p.getName())) {
                sbcyan.addPlayer(p);
            }
        }
        Team sbgreen = scoreboard.registerNewTeam("sbgreen");
        sbgreen.color(NamedTextColor.DARK_GREEN);
        sbgreen.setAllowFriendlyFire(false);
        sbgreen.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Teams.green.contains(p.getName())) {
                sbgreen.addPlayer(p);
            }
        }
        Team sblemon = scoreboard.registerNewTeam("sblemon");
        sblemon.color(NamedTextColor.YELLOW);
        sblemon.setAllowFriendlyFire(false);
        sblemon.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Teams.lemon.contains(p.getName())) {
                sblemon.addPlayer(p);
            }
        }
        Team sblime = scoreboard.registerNewTeam("sblime");
        sblime.color(NamedTextColor.GREEN);
        sblime.setAllowFriendlyFire(false);
        sblime.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Teams.lime.contains(p.getName())) {
                sblime.addPlayer(p);
            }
        }
        Team sbmagenta = scoreboard.registerNewTeam("sbmagenta");
        sbmagenta.color(NamedTextColor.LIGHT_PURPLE);
        sbmagenta.setAllowFriendlyFire(false);
        sbmagenta.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Teams.magenta.contains(p.getName())) {
                sbmagenta.addPlayer(p);
            }
        }
        Team sborange = scoreboard.registerNewTeam("sborange");
        sborange.color(NamedTextColor.GOLD);
        sborange.setAllowFriendlyFire(false);
        sborange.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Teams.orange.contains(p.getName())) {
                sborange.addPlayer(p);
            }
        }
        Team sbpeach = scoreboard.registerNewTeam("sbpeach");
        sbpeach.color(NamedTextColor.RED);
        sbpeach.setAllowFriendlyFire(false);
        sbpeach.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Teams.peach.contains(p.getName())) {
                sbpeach.addPlayer(p);
            }
        }
        Team sbpurple = scoreboard.registerNewTeam("sbpurple");
        sbpurple.color(NamedTextColor.DARK_PURPLE);
        sbpurple.setAllowFriendlyFire(false);
        sbpurple.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Teams.purple.contains(p.getName())) {
                sbpurple.addPlayer(p);
            }
        }
        Team sbred = scoreboard.registerNewTeam("sbred");
        sbred.color(NamedTextColor.RED);
        sbred.setAllowFriendlyFire(false);
        sbred.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Teams.red.contains(p.getName())) {
                sbred.addPlayer(p);
            }
        }
        Team sbwhite = scoreboard.registerNewTeam("sbwhite");
        sbwhite.color(NamedTextColor.WHITE);
        sbwhite.setAllowFriendlyFire(false);
        sbwhite.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Teams.white.contains(p.getName())) {
                sbwhite.addPlayer(p);
            }
        }
        Team sbyellow = scoreboard.registerNewTeam("sbyellow");
        sbyellow.color(NamedTextColor.YELLOW);
        sbyellow.setAllowFriendlyFire(false);
        sbyellow.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Teams.yellow.contains(p.getName())) {
                sbyellow.addPlayer(p);
            }
        }

        Component title = text("\uE12E ").color(NamedTextColor.WHITE).append(translatable("crystalized.game.knockoff.name").color(NamedTextColor.GOLD));
        Objective obj = scoreboard.registerNewObjective("main", Criteria.DUMMY, title);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (floodgateapi.isFloodgatePlayer(player.getUniqueId())) {
            /*
            As far as I know, Bedrock/Geyser doesn't really support text translations that change based on your
            game's language, So we write the text in English manually. Plus the Scoreboard on TubNet was
            different on Bedrock compared to Java so we're keeping the same tradition - Callum
            */

            obj.getScore("7").setScore(7);
            obj.getScore("7").customName(Component.text("  "));

            obj.getScore("6").setScore(6);
						TeamData td = TeamData.get_team_data(Teams.GetPlayerTeam(player));
            if (td == null) {
                obj.getScore("6").customName(Component.text("Team: Unknown"));
            } else {
            		obj.getScore("6").customName(Component.translatable("crystalized.game.generic.team").append(text(": "))
                		.append(Component.translatable("crystalized.game.generic.team."+td.name).color(TextColor.color(td.color.asRGB()))));
						}

            obj.getScore("5").setScore(5);
            obj.getScore("5").customName(Component.text("Round"));

            obj.getScore("4").setScore(4);
            obj.getScore("4").customName(Component.text("Next Round"));

            obj.getScore("3").setScore(3);
            obj.getScore("3").customName(Component.text("Lives"));

            obj.getScore("2").setScore(2);
            obj.getScore("2").customName(Component.text("Kills"));

            obj.getScore("1").setScore(1);
            obj.getScore("1").customName(text(" "));

        } else { //Java scoreboard
            //Bukkit.getServer().sendMessage(text("[SCOREBOARD] Player " + player + " is Java"));
            obj.getScore("9").setScore(9);
            obj.getScore("9").customName(text("     "));

            obj.getScore("8").setScore(8);
            obj.getScore("8").customName(Component.translatable("crystalized.game.generic.team").append(text(": ")));

            obj.getScore("7").setScore(7);
            obj.getScore("7").customName(text("   "));

            obj.getScore("6").setScore(6);
            obj.getScore("6").customName(Component.translatable("crystalized.game.knockoff.round").append(text(": ")));

            obj.getScore("5").setScore(5);
            obj.getScore("5").customName(Component.translatable("crystalized.game.knockoff.nextround").append(text(": ")));

            obj.getScore("4").setScore(4);
            obj.getScore("4").customName(text("  "));

            obj.getScore("3").setScore(3);
            obj.getScore("3").customName(Component.translatable("crystalized.game.knockoff.lives").append(text(": ")));

            obj.getScore("2").setScore(2);
            obj.getScore("2").customName(Component.translatable("crystalized.game.generic.kills").append(text(": ")));

            obj.getScore("1").setScore(1);
            obj.getScore("1").customName(text(" "));

            Team TeamName = scoreboard.registerNewTeam("Team");
            TeamName.addEntry("8");
						TeamData td = TeamData.get_team_data(Teams.GetPlayerTeam(player));
            if (td == null) {
                TeamName.suffix(text("Spectator").color(NamedTextColor.WHITE));
            } else {
            		TeamName.suffix(Component.translatable("crystalized.game.generic.team."+td.name).color(TextColor.color(td.color.asRGB())));
			}
            obj.getScore("8").setScore(8);
        }

        obj.getScore("0").setScore(0);
        obj.getScore("0").customName(text("crystalized.cc ").color(TextColor.color(0xc4b50a)).append(text("(ServID)").color(NamedTextColor.GRAY)));

        Team RoundCount = scoreboard.registerNewTeam("RoundCount");
        RoundCount.addEntry("6");
        RoundCount.suffix(text(""));
        obj.getScore("6").setScore(6);

        Team NextRound = scoreboard.registerNewTeam("NextRound");
        NextRound.addEntry("5");
        NextRound.suffix(text(""));
        obj.getScore("5").setScore(5);

        Team LivesCount = scoreboard.registerNewTeam("LivesCount");
        LivesCount.addEntry("3");
        LivesCount.suffix(text(""));
        obj.getScore("3").setScore(3);

        Team KillCount = scoreboard.registerNewTeam("KillCount");
        KillCount.addEntry("2");
        KillCount.suffix(text(""));
        obj.getScore("2").setScore(2);

        player.setScoreboard(scoreboard);

        new BukkitRunnable() {
            public void run() {
                if (knockoff.getInstance().GameManager == null) {
                    cancel();
                } else {
                    PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                    if (pd == null) {return;}
                    if (floodgateapi.isFloodgatePlayer(player.getUniqueId())) {
                        if (knockoff.getInstance().getConfig().getBoolean("tourneys.manual_map_movement")) {
                            obj.getScore("5").customName(Component.translatable("crystalized.game.knockoff.round").append(text(": ")).append(text("??")));
                            obj.getScore("4").customName(Component.translatable("crystalized.game.knockoff.nextround").append(text(": ")).append(text("??")));
                        } else {
                            obj.getScore("5").customName(Component.translatable("crystalized.game.knockoff.round").append(text(": ")).append(text(GameManager.Round)));
                            obj.getScore("4").customName(Component.translatable("crystalized.game.knockoff.nextround").append(text(": ")).append(text(GameManager.RoundCounter)));
                        }

                        obj.getScore("3").customName(Component.translatable("crystalized.game.knockoff.lives").append(text(": ")).append(text(pd.getLives())));
                        obj.getScore("2").customName(Component.translatable("crystalized.game.generic.kills").append(text(": ")).append(text(pd.getKills())));
                    } else {
                        if (knockoff.getInstance().getConfig().getBoolean("tourneys.manual_map_movement")) {
                            RoundCount.suffix(text("??"));
                            NextRound.suffix(text("??"));
                        } else {
                            RoundCount.suffix(text(GameManager.Round));
                            NextRound.suffix(text(GameManager.RoundCounter));
                        }

                        LivesCount.suffix(text(pd.getLives()));
                        KillCount.suffix(text(pd.getKills()));
                    }
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 2 ,1);
    }
}

class QueueScoreBoard{
    public QueueScoreBoard(Player player) {
        FloodgateApi floodgateapi = FloodgateApi.getInstance();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Component title = text("\uE12E ").color(NamedTextColor.WHITE).append(translatable("crystalized.game.knockoff.name").color(NamedTextColor.GOLD));
        Objective obj = scoreboard.registerNewObjective("main", Criteria.DUMMY, title);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        obj.getScore("5").setScore(5);
        obj.getScore("5").customName(text("  "));

        obj.getScore("4").setScore(4);
        obj.getScore("4").customName(translatable("crystalized.game.knockoff.queue.playing"));

        obj.getScore("3").setScore(3);
        obj.getScore("3").customName(text(" "));

        obj.getScore("2").setScore(2);
        obj.getScore("2").customName(translatable("crystalized.game.knockoff.queue.waiting"));

        obj.getScore("1").setScore(1);
        obj.getScore("1").customName(text(""));

        obj.getScore("0").setScore(0);
        obj.getScore("0").customName(text("crystalized.cc ").color(TextColor.color(0xc4b50a)).append(text("(ServID)").color(NamedTextColor.GRAY)));

        player.setScoreboard(scoreboard);

        Team QueuePlayer = scoreboard.registerNewTeam("QueuePlayers");
        QueuePlayer.addEntry("2");
        QueuePlayer.suffix(text(""));
        obj.getScore("2").setScore(2);

        Team QueueMap = scoreboard.registerNewTeam("QueueMap");
        QueueMap.addEntry("4");
        QueueMap.suffix(text(""));
        obj.getScore("4").setScore(4);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (floodgateapi.isFloodgatePlayer(player.getUniqueId())) {
                    obj.getScore("2").customName(Component.text("Waiting for Players: ")
                            .append(Component.text("(" + Bukkit.getOnlinePlayers().size()))
                            .append(Component.text("/"))
                            .append(Component.text("" + Bukkit.getMaxPlayers()))
                            .append(Component.text(")"))
                    );
                    obj.getScore("4").customName(text("You are playing on: " + knockoff.getInstance().mapdata.map_name));
                } else {
                    QueuePlayer.suffix(
                            Component.text("(")
                                    .append(Component.text("" + Bukkit.getOnlinePlayers().size()))
                                    .append(Component.text("/"))
                                    .append(Component.text("" + Bukkit.getMaxPlayers()))
                                    .append(Component.text(")"))
                    );
                    QueueMap.suffix(knockoff.getInstance().mapdata.map_name);
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0 ,1);
    }
}
