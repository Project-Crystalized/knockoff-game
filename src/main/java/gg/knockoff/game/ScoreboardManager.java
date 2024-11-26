package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import static net.kyori.adventure.text.Component.text;

public class ScoreboardManager {

    public static void SetPlayerScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Team blue = scoreboard.registerNewTeam("blue");
        blue.color(NamedTextColor.DARK_BLUE);
        blue.setAllowFriendlyFire(false);
        blue.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        if (Teams.GetPlayerTeam(player).equals("blue")) {
            blue.addPlayer(player);
        }

        Component title = text("\uE108 KnockOff (WIP)").color(NamedTextColor.GOLD);
        Objective obj = scoreboard.registerNewObjective("main", Criteria.DUMMY, title);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        obj.getScore("9").setScore(9);
        obj.getScore("9").customName(text("     "));

        obj.getScore("8").setScore(8);
        obj.getScore("8").customName(text("Team: "));

        obj.getScore("7").setScore(7);
        obj.getScore("7").customName(text("   "));

        obj.getScore("6").setScore(6);
        obj.getScore("6").customName(text("Round "));

        obj.getScore("5").setScore(5);
        obj.getScore("5").customName(text("Next round: "));

        obj.getScore("4").setScore(4);
        obj.getScore("4").customName(text("  "));

        obj.getScore("3").setScore(3);
        obj.getScore("3").customName(text("Lives Left: "));

        obj.getScore("2").setScore(2);
        obj.getScore("2").customName(text("Kills: "));

        obj.getScore("1").setScore(1);
        obj.getScore("1").customName(text(" "));

        obj.getScore("0").setScore(0);
        obj.getScore("0").customName(text("crystalized.cc ").color(TextColor.color(0xc4b50a)).append(text("(ServID)").color(NamedTextColor.GRAY)));

        Team TeamName = scoreboard.registerNewTeam("Team");
        TeamName.addEntry("8");
        if (Teams.GetPlayerTeam(player).equals("blue")) {
            TeamName.suffix(text("Blue").color(Teams.TEAM_BLUE));
        } else if (Teams.GetPlayerTeam(player).equals("cyan")) {
            TeamName.suffix(text("Cyan").color(Teams.TEAM_CYAN));
        } else if (Teams.GetPlayerTeam(player).equals("green")) {
            TeamName.suffix(text("Green").color(Teams.TEAM_GREEN));
        } else if (Teams.GetPlayerTeam(player).equals("lemon")) {
            TeamName.suffix(text("Lemon").color(Teams.TEAM_LEMON));
        } else if (Teams.GetPlayerTeam(player).equals("lime")) {
            TeamName.suffix(text("Lime").color(Teams.TEAM_LIME));
        } else if (Teams.GetPlayerTeam(player).equals("magenta")) {
            TeamName.suffix(text("Magenta").color(Teams.TEAM_MAGENTA));
        } else if (Teams.GetPlayerTeam(player).equals("orange")) {
            TeamName.suffix(text("Orange").color(Teams.TEAM_ORANGE));
        } else if (Teams.GetPlayerTeam(player).equals("peach")) {
            TeamName.suffix(text("Peach").color(Teams.TEAM_PEACH));
        } else if (Teams.GetPlayerTeam(player).equals("purple")) {
            TeamName.suffix(text("Purple").color(Teams.TEAM_PURPLE));
        } else if (Teams.GetPlayerTeam(player).equals("red")) {
            TeamName.suffix(text("Red").color(Teams.TEAM_RED));
        } else if (Teams.GetPlayerTeam(player).equals("white")) {
            TeamName.suffix(text("White").color(Teams.TEAM_WHITE));
        } else if (Teams.GetPlayerTeam(player).equals("yellow")) {
            TeamName.suffix(text("Yellow").color(Teams.TEAM_YELLOW));
        } else {
            TeamName.suffix(text("Unknown").color(NamedTextColor.WHITE));
        }
        obj.getScore("8").setScore(8);

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
                PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                RoundCount.suffix(text("TBA"));
                NextRound.suffix(text("TBA"));
                LivesCount.suffix(text(pd.getLives()));
                KillCount.suffix(text(pd.getKills()));
            }
        }.runTaskTimer(knockoff.getInstance(), 20 ,1);
    }
}

class Queue_Scoreboard {
    //TODO obvious what to do here
}