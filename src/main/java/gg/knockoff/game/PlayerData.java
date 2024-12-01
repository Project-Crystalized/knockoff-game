package gg.knockoff.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerData { //This class probably isn't optimised, but it works so who cares

    public String player;
    public boolean isPlayerDead = false;
    private int lives = 5;
    private int kills = 0;
    private int deaths = 0;
    private int damagepercentage = 0;
    private int deathtimer = 0;
    public boolean DamagePercentageStopTimer = false;

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
}
