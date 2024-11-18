package gg.knockoff.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerData {

    public String player;
    private int lives = 5;
    private int kills = 0;
    private int deaths = 0;

    public PlayerData(Player p) {
        player = p.getName();
    }




    public int getLives() {
        return this.lives;
    }

    public void takeawayLife(int amt) { //Unlike the other calls in this class, this takes away lives. be careful when using this
        Player p = Bukkit.getPlayer(player);
        lives -= amt;
    }

    public int getKills() {
        return this.kills;
    }

    public void addKill(int amt) {
        Player p = Bukkit.getPlayer(player);
        kills += amt;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void addDeath(int amt) {
        Player p = Bukkit.getPlayer(player);
        deaths += amt;
    }
}
