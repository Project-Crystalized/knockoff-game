package gg.knockoff.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;

import static net.kyori.adventure.text.Component.text;

public class PlayerData { //This class probably isn't optimised, but it works so who cares

    public String player;
    public boolean isPlayerDead = false;
    public boolean isEliminated = false;
    public int lives = 5;
    public int kills = 0;
    public int deaths = 0;
    public int deathtimer = 0;
    public int startingDeathTimerInt = 0;
    public boolean isOnline = true;
    public int blocksplaced = 0;
    public int blocksbroken = 0;
    public int powerupscollected = 0;
    public int powerupsused = 0;
    public int percent = 0;

    public PlayerData(Player p) {
        player = p.getName();

        new BukkitRunnable() {
            int timer = 0;
            int savedPercent = -1;
            public void run() {
                if (percent > 0) {
                    if (savedPercent != percent) {
                        savedPercent = percent;
                        timer = 4 * 20;
                    } else {
                        timer --;
                        if (timer == 0) {
                            percent--;
                            savedPercent = percent;
                            timer = 4;
                        }
                    }
                } else {
                    timer = 0;
                    savedPercent = -1;
                }

                if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
                    //p.sendActionBar(text("" + percent + "% | T:" + timer + " SP:" + savedPercent));
                    p.sendActionBar(text(percentToFont("" + percent + "%")));
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 1);
    }

    private String percentToFont(String input) {
        String output = "";
        for (char c : input.toCharArray()) {
            switch (c) {
                case '0' -> {output = output + "\uE210";}
                case '1' -> {output = output + "\uE211";}
                case '2' -> {output = output + "\uE212";}
                case '3' -> {output = output + "\uE213";}
                case '4' -> {output = output + "\uE214";}
                case '5' -> {output = output + "\uE215";}
                case '6' -> {output = output + "\uE216";}
                case '7' -> {output = output + "\uE217";}
                case '8' -> {output = output + "\uE218";}
                case '9' -> {output = output + "\uE219";}
                case '%' -> {output = output + "\uE21A";}
                default -> {output = output + "?";}
            }
        }
        return output;
    }

    public int getLives() {
        return this.lives;
    }

    public void takeawayLife(int amt) { //Unlike the other calls in this class, this takes away lives. be careful when using this
        lives -= amt;
    }

    public int getKills() {
        return this.kills;
    }

    public void addKill(int amt) {
        kills += amt;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void addDeath(int amt) {
        deaths += amt;
    }

    public void setDeathtimer(int amt) {
        startingDeathTimerInt = amt;
        deathtimer = amt;
    }

    public int getDeathtimer() {
        return this.deathtimer;
    }

    public int calc_player_score() {
        return kills;
    }
}

class PlayerDataComparator implements Comparator<PlayerData> {
    @Override
    public int compare(PlayerData arg0, PlayerData arg1) {
        return arg0.calc_player_score() - arg1.calc_player_score();
    }
}