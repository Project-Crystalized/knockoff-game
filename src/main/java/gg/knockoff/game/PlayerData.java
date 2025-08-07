package gg.knockoff.game;

import com.destroystokyo.paper.ParticleBuilder;
import gg.crystalized.lobby.Lobby_plugin;
import gg.crystalized.lobby.Ranks;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;

import static net.kyori.adventure.text.Component.text;
import static org.bukkit.Particle.DUST;

public class PlayerData { //This class probably isn't optimised, but it works so who cares

    public String player;
    public Component cachedRankIcon_small = text("?");
    public Component cachedRankIcon_full = text("rank");
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
    private int percentLimit = 300;

    public PlayerData(Player p) {
        player = p.getName();
        cachedRankIcon_small = Ranks.getIcon(Bukkit.getOfflinePlayer(player));
        cachedRankIcon_full = Ranks.getRankWithName(p);

        new BukkitRunnable() {
            int timer = 0;
            int savedPercent = -1;
            int timerMoveToSafety = -1;
            int timerOver100Particles = -1;

            public void run() {
                //percent shit
                if (percent > 0) {
                    if (savedPercent != percent) {
                        savedPercent = percent;
                        timer = 6 * 20;
                    } else {
                        timer --;
                        if (timer == 0) {
                            percent--;
                            savedPercent = percent;
                            timer = 1;
                        }
                        else if (p.getGameMode().equals(GameMode.SPECTATOR)) {
                            percent = 0;
                            savedPercent = -1;
                            timer = 0;
                        }
                    }
                } else {
                    timer = 0;
                    savedPercent = -1;
                }
                if (percent > percentLimit) {
                    percent = 300; //% limit
                }

                //move to safety conduit sound loop
                if (MapManager.isInsideDecayingSection(p.getLocation())) {
                    if (timerMoveToSafety < 0) {
                        timerMoveToSafety = 4 * 20;
                        p.playSound(p, "minecraft:block.conduit.ambient", 2, 1);
                    }
                    timerMoveToSafety--;
                } else {
                    timerMoveToSafety = -1;
                }

                //Particles when over 100%
                if (percent > 99) {
                    if (timerOver100Particles < 0) {
                        timerOver100Particles = 4;
                    }
                    if (timerOver100Particles == 0) {
                        //Performance might be ass on bedrock because of this, cant do shit about it
                        ParticleBuilder builder = new ParticleBuilder(DUST);
                        builder.color(Color.GRAY);
                        builder.location(p.getLocation());
                        builder.count(percent / 3);
                        builder.offset(0.25, 0.25, 0.25);
                        builder.spawn();
                    }
                    timerOver100Particles--;
                } else {
                    timerOver100Particles = -1;
                }

                //percent on actionbar
                if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
                    //p.sendActionBar(text("" + percent + "% | T:" + timer + " SP:" + savedPercent));
                    p.sendActionBar(text(percentToFont(percent + "%"))); //TODO make this coloured somehow
                    float exp = (float) percent / 100;
                    if (exp > 1.0) {
                        exp = 1.0f;
                    }
                    p.setExp(exp);
                } else {
                    p.setExp(0);
                }
                p.setLevel(0);

                if (knockoff.getInstance().GameManager == null) {
                    cancel();
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