package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class Teams {

    public static List<String> blue = new ArrayList<>();
    public static List<String> cyan = new ArrayList<>();
    public static List<String> green = new ArrayList<>();
    public static List<String> lemon = new ArrayList<>();
    public static List<String> lime = new ArrayList<>();
    public static List<String> magenta = new ArrayList<>();
    public static List<String> orange = new ArrayList<>();
    public static List<String> peach = new ArrayList<>();
    public static List<String> purple = new ArrayList<>();
    public static List<String> red = new ArrayList<>();
    public static List<String> white = new ArrayList<>();
    public static List<String> yellow = new ArrayList<>();

    public Teams() {
        Bukkit.getLogger().log(Level.INFO, "Sorting Players into teams (solo)...");
        List<String> playerlist = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            playerlist.add(p.getName());
        }
        Collections.shuffle(playerlist);
        if (playerlist.size() > 13) {//13 is the max limit since there is only 12 teams. This is for a solos game
            Bukkit.getServer().sendMessage(Component.text("Too many players to start a game (hardcoded limit is 13). Please kick players off or limit your player count."));
            return;
        }
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            Bukkit.getServer().sendMessage(Component.text("\nStarting the game requires a player to be online. Please login to the server and try again.\n"));
            return;
        } else {
            if (playerlist.size() > 0) {
                if (blue.isEmpty()) {
                    blue.add(playerlist.get(0));
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + blue + " in Team Blue");
                }
            }else {
                Bukkit.getLogger().log(Level.SEVERE, "Tried to add a player to team Blue but the player list is 0. Please report this as you shouldn't be able to get this error");
                return;
            }

            if (playerlist.size() > 1) { //If the player list is 2 or greater
                if (cyan.isEmpty()) {
                    cyan.add(playerlist.get(1));
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + cyan + " in Team Cyan");
                }
            }else {
                Bukkit.getLogger().log(Level.WARNING, "No player(s) available for Cyan team (FYI: Recommend getting an alt account or someone else to join. 2 or more players is recommended)");
                return;
            }

            if (playerlist.size() > 2) { //If the player list is 3 or greater
                if (green.isEmpty()) {
                    green.add(playerlist.get(2));
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + green + " in Team Green");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
                return;
            }

            if (playerlist.size() > 3) { //If the player list is 4 or greater
                if (lemon.isEmpty()) {
                    lemon.add(playerlist.get(3));
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + lemon + " in Team Lemon");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
                return;
            }

            if (playerlist.size() > 4) { //If the player list is 5 or greater
                if (lime.isEmpty()) {
                    lime.add(playerlist.get(4));
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + lime + " in Team Lime");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
                return;
            }

            if (playerlist.size() > 5) { //If the player list is 6 or greater
                if (magenta.isEmpty()) {
                    magenta.add(playerlist.get(5));
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + magenta + " in Team Magenta");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
                return;
            }

            if (playerlist.size() > 6) { //If the player list is 7 or greater
                if (orange.isEmpty()) {
                    orange.add(playerlist.get(6));
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + orange + " in Team Orange");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Orange team");
                return;
            }

            if (playerlist.size() > 7) { //If the player list is 8 or greater
                if (peach.isEmpty()) {
                    peach.add(playerlist.get(7));
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + peach + " in Team Peach");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Peach team");
                return;
            }

            if (playerlist.size() > 8) { //If the player list is 9 or greater
                if (purple.isEmpty()) {
                    purple.add(playerlist.get(8));
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + purple + " in Team Purple");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Purple team");
                return;
            }

            if (playerlist.size() > 9) { //If the player list is 10 or greater
                if (red.isEmpty()) {
                    red.add(playerlist.get(9));
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + red + " in Team Red");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Red team");
                return;
            }

            if (playerlist.size() > 10) { //If the player list is 11 or greater
                if (white.isEmpty()) {
                    white.add(playerlist.get(10));
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + white + " in Team White");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for White team");
                return;
            }

            if (playerlist.size() > 11) { //If the player list is 12 (or greater but this isn't allowed as of now lol)
                if (yellow.isEmpty()) {
                    yellow.add(playerlist.get(11));
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + yellow + " in Team Yellow");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Yellow team");
                return;
            }
        }
        Bukkit.getLogger().log(Level.INFO, "Successfully sorted every online player into Teams");
    }
}
