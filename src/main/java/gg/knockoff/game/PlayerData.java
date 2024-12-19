package gg.knockoff.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;

public class PlayerData { //This class probably isn't optimised, but it works so who cares

    public String player;
    public boolean isPlayerDead = false;
    public boolean isEliminated = false;
    public int lives = 5;
    public int kills = 0;
    public int deaths = 0;
    public int damagepercentage = 0;
    public int deathtimer = 0;
    public boolean DamagePercentageStopTimer = false;
    public boolean isOnline = true;
    public int blocksplaced = 0;
    public int blocksbroken = 0;
    public int powerupscollected = 0;
    public int powerupsused = 0;

    public PlayerData(Player p) {
        player = p.getName();
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
        deathtimer = amt;
    }

    public int getDeathtimer() {
        return this.deathtimer;
    }

    public int getDamagepercentage() {
        return this.damagepercentage;
    }

    public void changepercentage(int amt) {
        Player p = Bukkit.getPlayer(player);
        damagepercentage += amt;
        Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> {
            DamagePercentageTimer();
        }, 1);
    }

    private void DamagePercentageTimer() {
        if (damagepercentage > 0) {
            new BukkitRunnable() {
                int timer = 0;
                @Override
                public void run() {
                    if (DamagePercentageStopTimer) {
                        DamagePercentageStopTimer = false;
                        cancel();
                    }
                    switch (timer) {
                        case 3:
                            SetDamagePrecentageTo0();
                            cancel();
                        default:
                            //do nothing
                    }
                    timer++;
                }
            }.runTaskTimer(knockoff.getInstance(), 20 ,1);
        }
    }

    private void SetDamagePrecentageTo0() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (DamagePercentageStopTimer || damagepercentage == 0) {
                    DamagePercentageStopTimer = false;
                    cancel();
                } else {
                    damagepercentage--;
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 10 ,1);
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