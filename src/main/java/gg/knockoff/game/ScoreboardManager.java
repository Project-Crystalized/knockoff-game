package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.geysermc.floodgate.api.FloodgateApi;

import static net.kyori.adventure.text.Component.text;

public class ScoreboardManager {

    public static void SetPlayerScoreboard(Player player) {

        FloodgateApi floodgateapi = FloodgateApi.getInstance();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Component title = text("\uE108 ").color(NamedTextColor.WHITE).append(text("KnockOff (WIP)").color(NamedTextColor.GOLD));
        Objective obj = scoreboard.registerNewObjective("main", Criteria.DUMMY, title);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (floodgateapi.isFloodgatePlayer(player.getUniqueId())) {
            /*
            As far as I know, Bedrock/Geyser doesn't really support text translations that change based on your
            game's language, So we write the text in English manually. Plus the Scoreboard on TubNet was
            different on Bedrock compared to Java so we're keeping the same tradition - Callum
            */
            //Bukkit.getServer().sendMessage(text("[SCOREBOARD] Player " + player + " is Bedrock"));

            obj.getScore("7").setScore(7);
            obj.getScore("7").customName(Component.text("  "));

            obj.getScore("6").setScore(6);
            if (Teams.GetPlayerTeam(player).equals("blue")) {
                obj.getScore("6").customName(Component.text("Team: ").append(text("Blue").color(Teams.TEAM_BLUE)));
            } else if (Teams.GetPlayerTeam(player).equals("cyan")) {
                obj.getScore("6").customName(Component.text("Team: ").append(text("Cyan").color(Teams.TEAM_CYAN)));
            } else if (Teams.GetPlayerTeam(player).equals("green")) {
                obj.getScore("6").customName(Component.text("Team: ").append(text("Green").color(Teams.TEAM_GREEN)));
            } else if (Teams.GetPlayerTeam(player).equals("lemon")) {
                obj.getScore("6").customName(Component.text("Team: ").append(text("Lemon").color(Teams.TEAM_LEMON)));
            } else if (Teams.GetPlayerTeam(player).equals("lime")) {
                obj.getScore("6").customName(Component.text("Team: ").append(text("Lime").color(Teams.TEAM_LIME)));
            } else if (Teams.GetPlayerTeam(player).equals("magenta")) {
                obj.getScore("6").customName(Component.text("Team: ").append(text("Magenta").color(Teams.TEAM_MAGENTA)));
            } else if (Teams.GetPlayerTeam(player).equals("orange")) {
                obj.getScore("6").customName(Component.text("Team: ").append(text("Orange").color(Teams.TEAM_ORANGE)));
            } else if (Teams.GetPlayerTeam(player).equals("peach")) {
                obj.getScore("6").customName(Component.text("Team: ").append(text("Peach").color(Teams.TEAM_PEACH)));
            } else if (Teams.GetPlayerTeam(player).equals("purple")) {
                obj.getScore("6").customName(Component.text("Team: ").append(text("Purple").color(Teams.TEAM_PURPLE)));
            } else if (Teams.GetPlayerTeam(player).equals("red")) {
                obj.getScore("6").customName(Component.text("Team: ").append(text("Red").color(Teams.TEAM_RED)));
            } else if (Teams.GetPlayerTeam(player).equals("white")) {
                obj.getScore("6").customName(Component.text("Team: ").append(text("White").color(Teams.TEAM_WHITE)));
            } else if (Teams.GetPlayerTeam(player).equals("yellow")) {
                obj.getScore("6").customName(Component.text("Team: ").append(text("Yellow").color(Teams.TEAM_YELLOW)));
            } else {
                obj.getScore("6").customName(Component.text("Team: Unknown"));
            }

            obj.getScore("5").setScore(5);
            obj.getScore("5").customName(Component.text("Round: "));

            obj.getScore("4").setScore(4);
            obj.getScore("4").customName(Component.text("Next Round: "));

            obj.getScore("3").setScore(3);
            obj.getScore("3").customName(Component.text("Lives: "));

            obj.getScore("2").setScore(2);
            obj.getScore("2").customName(Component.text("Kills: "));

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
            if (Teams.GetPlayerTeam(player).equals("blue")) {
                TeamName.suffix(Component.translatable("crystalized.game.knockoff.team.blue").color(Teams.TEAM_BLUE));
            } else if (Teams.GetPlayerTeam(player).equals("cyan")) {
                TeamName.suffix(Component.translatable("crystalized.game.knockoff.team.cyan").color(Teams.TEAM_CYAN));
            } else if (Teams.GetPlayerTeam(player).equals("green")) {
                TeamName.suffix(Component.translatable("crystalized.game.knockoff.team.green").color(Teams.TEAM_GREEN));
            } else if (Teams.GetPlayerTeam(player).equals("lemon")) {
                TeamName.suffix(Component.translatable("crystalized.game.knockoff.team.lemon").color(Teams.TEAM_LEMON));
            } else if (Teams.GetPlayerTeam(player).equals("lime")) {
                TeamName.suffix(Component.translatable("crystalized.game.knockoff.team.lime").color(Teams.TEAM_LIME));
            } else if (Teams.GetPlayerTeam(player).equals("magenta")) {
                TeamName.suffix(Component.translatable("crystalized.game.knockoff.team.magenta").color(Teams.TEAM_MAGENTA));
            } else if (Teams.GetPlayerTeam(player).equals("orange")) {
                TeamName.suffix(Component.translatable("crystalized.game.knockoff.team.orange").color(Teams.TEAM_ORANGE));
            } else if (Teams.GetPlayerTeam(player).equals("peach")) {
                TeamName.suffix(Component.translatable("crystalized.game.knockoff.team.peach").color(Teams.TEAM_PEACH));
            } else if (Teams.GetPlayerTeam(player).equals("purple")) {
                TeamName.suffix(Component.translatable("crystalized.game.knockoff.team.purple").color(Teams.TEAM_PURPLE));
            } else if (Teams.GetPlayerTeam(player).equals("red")) {
                TeamName.suffix(Component.translatable("crystalized.game.knockoff.team.red").color(Teams.TEAM_RED));
            } else if (Teams.GetPlayerTeam(player).equals("white")) {
                TeamName.suffix(Component.translatable("crystalized.game.knockoff.team.white").color(Teams.TEAM_WHITE));
            } else if (Teams.GetPlayerTeam(player).equals("yellow")) {
                TeamName.suffix(Component.translatable("crystalized.game.knockoff.team.yellow").color(Teams.TEAM_YELLOW));
            } else {
                TeamName.suffix(text("Unknown").color(NamedTextColor.WHITE));
            }
            obj.getScore("8").setScore(8);
        }

        obj.getScore("0").setScore(0);
        obj.getScore("0").customName(text("crystalized.cc ").color(TextColor.color(0xc4b50a)).append(text("(ServID)").color(NamedTextColor.GRAY)));

        Team RoundCount = scoreboard.registerNewTeam("RoundCount");
        RoundCount.addEntry("6");
        RoundCount.suffix(text("RoundCount Placeholder"));
        obj.getScore("6").setScore(6);

        Team NextRound = scoreboard.registerNewTeam("NextRound");
        NextRound.addEntry("5");
        NextRound.suffix(text("NextRound Placeholder"));
        obj.getScore("5").setScore(5);

        Team LivesCount = scoreboard.registerNewTeam("LivesCount");
        LivesCount.addEntry("3");
        LivesCount.suffix(text("LivesCount Placeholder"));
        obj.getScore("3").setScore(3);

        Team KillCount = scoreboard.registerNewTeam("KillCount");
        KillCount.addEntry("2");
        KillCount.suffix(text("KillCount Placeholder"));
        obj.getScore("2").setScore(2);

        player.setScoreboard(scoreboard);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (knockoff.getInstance().GameManager == null) {
                    cancel();
                } else {
                    PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                    if (floodgateapi.isFloodgatePlayer(player.getUniqueId())) {
                        obj.getScore("5").customName(Component.text("Round: ").append(text("TBA")));
                        obj.getScore("4").customName(Component.text("Next Round: ").append(text("TBA")));
                        obj.getScore("3").customName(Component.text("Lives: ").append(text(pd.getLives())));
                        obj.getScore("2").customName(Component.text("Kills: ").append(text(pd.getKills())));
                    } else {
                        RoundCount.suffix(text("TBA"));
                        NextRound.suffix(text("TBA"));
                        LivesCount.suffix(text(pd.getLives()));
                        KillCount.suffix(text(pd.getKills()));
                    }
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 20 ,1);
    }
}

class Queue_Scoreboard {
    //TODO obvious what to do here
}