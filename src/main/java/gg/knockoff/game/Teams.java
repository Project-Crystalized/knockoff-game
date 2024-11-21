package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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

    public static final TextColor TEAM_BLUE = TextColor.color(0x0A42BB);
    public static final TextColor TEAM_CYAN = TextColor.color(0x157D91);
    public static final TextColor TEAM_GREEN = TextColor.color(0x0A971E);
    public static final TextColor TEAM_LEMON = TextColor.color(0xFFC500);
    public static final TextColor TEAM_LIME = TextColor.color(0x67E555);
    public static final TextColor TEAM_MAGENTA = TextColor.color(0xDA50E0);
    public static final TextColor TEAM_ORANGE = TextColor.color(0xFF7900);
    public static final TextColor TEAM_PEACH = TextColor.color(0xFF8775);
    public static final TextColor TEAM_PURPLE = TextColor.color(0x7525DC);
    public static final TextColor TEAM_RED = TextColor.color(0xF74036);
    public static final TextColor TEAM_WHITE = TextColor.color(0xFFFFFF);
    public static final TextColor TEAM_YELLOW = TextColor.color(0xFBE059);

    public Teams() {
        List<String> playerlist = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            playerlist.add(p.getName());
        }
        Collections.shuffle(playerlist);

        if (playerlist.size() > 13) {
            Bukkit.getLogger().log(Level.INFO, "Sorting Players into teams (duos)...");
        } else {
            Bukkit.getLogger().log(Level.INFO, "Sorting Players into teams (solo)...");
        }

        if (Bukkit.getOnlinePlayers().isEmpty()) {
            Bukkit.getServer().sendMessage(Component.text("\nStarting the game requires a player to be online. Please login to the server and try again.\n"));
            return;
        } else {
            if (playerlist.size() > 0) {
                if (blue.isEmpty()) {
                    blue.add(playerlist.get(0));
                    if (playerlist.size() > 12) {
                        blue.add(playerlist.get(13));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + blue + " in Team Blue");
                }
            }else {
                Bukkit.getLogger().log(Level.SEVERE, "Tried to add a player to team Blue but the player list is 0. Please report this as you shouldn't be able to get this error");
                return;
            }

            if (playerlist.size() > 1) { //If the player list is 2 or greater
                if (cyan.isEmpty()) {
                    cyan.add(playerlist.get(1));
                    if (playerlist.size() > 13) {
                        cyan.add(playerlist.get(14));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + cyan + " in Team Cyan");
                }
            }else {
                Bukkit.getLogger().log(Level.WARNING, "No player(s) available for Cyan team (FYI: Recommend getting an alt account or someone else to join. 2 or more players is recommended)");
                return;
            }

            if (playerlist.size() > 2) { //If the player list is 3 or greater
                if (green.isEmpty()) {
                    green.add(playerlist.get(2));
                    if (playerlist.size() > 14) {
                        green.add(playerlist.get(15));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + green + " in Team Green");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
                return;
            }

            if (playerlist.size() > 3) { //If the player list is 4 or greater
                if (lemon.isEmpty()) {
                    lemon.add(playerlist.get(3));
                    if (playerlist.size() > 15) {
                        lemon.add(playerlist.get(16));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + lemon + " in Team Lemon");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
                return;
            }

            if (playerlist.size() > 4) { //If the player list is 5 or greater
                if (lime.isEmpty()) {
                    lime.add(playerlist.get(4));
                    if (playerlist.size() > 16) {
                        lime.add(playerlist.get(17));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + lime + " in Team Lime");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
                return;
            }

            if (playerlist.size() > 5) { //If the player list is 6 or greater
                if (magenta.isEmpty()) {
                    magenta.add(playerlist.get(5));
                    if (playerlist.size() > 17) {
                        magenta.add(playerlist.get(18));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + magenta + " in Team Magenta");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
                return;
            }

            if (playerlist.size() > 6) { //If the player list is 7 or greater
                if (orange.isEmpty()) {
                    orange.add(playerlist.get(6));
                    if (playerlist.size() > 18) {
                        orange.add(playerlist.get(19));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + orange + " in Team Orange");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Orange team");
                return;
            }

            if (playerlist.size() > 7) { //If the player list is 8 or greater
                if (peach.isEmpty()) {
                    peach.add(playerlist.get(7));
                    if (playerlist.size() > 19) {
                        peach.add(playerlist.get(20));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + peach + " in Team Peach");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Peach team");
                return;
            }

            if (playerlist.size() > 8) { //If the player list is 9 or greater
                if (purple.isEmpty()) {
                    purple.add(playerlist.get(8));
                    if (playerlist.size() > 20) {
                        purple.add(playerlist.get(21));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + purple + " in Team Purple");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Purple team");
                return;
            }

            if (playerlist.size() > 9) { //If the player list is 10 or greater
                if (red.isEmpty()) {
                    red.add(playerlist.get(9));
                    if (playerlist.size() > 21) {
                        red.add(playerlist.get(22));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + red + " in Team Red");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Red team");
                return;
            }

            if (playerlist.size() > 10) { //If the player list is 11 or greater
                if (white.isEmpty()) {
                    white.add(playerlist.get(10));
                    if (playerlist.size() > 22) {
                        white.add(playerlist.get(23));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + white + " in Team White");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for White team");
                return;
            }

            if (playerlist.size() > 11) { //If the player list is 12
                if (yellow.isEmpty()) {
                    yellow.add(playerlist.get(11));
                    if (playerlist.size() > 23) {
                        yellow.add(playerlist.get(24));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + yellow + " in Team Yellow");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Yellow team");
                return;
            }
        }
        Bukkit.getLogger().log(Level.INFO, "Successfully sorted every online player into Teams");
    }



    public static String GetPlayerTeam(Player player) {
        //Bukkit.getLogger().log(Level.INFO, "Figuring out " + player.getName() + "'s Team...");
        if (blue.contains(player.getName())) {
            return "blue";
        } else if (cyan.contains(player.getName())) {
            return "cyan";
        } else if (green.contains(player.getName())) {
            return "green";
        } else if (lemon.contains(player.getName())) {
            return "lemon";
        } else if (lime.contains(player.getName())) {
            return "lime";
        } else if (magenta.contains(player.getName())) {
            return "magenta";
        } else if (orange.contains(player.getName())) {
            return "orange";
        } else if (peach.contains(player.getName())) {
            return "peach";
        } else if (purple.contains(player.getName())) {
            return "purple";
        } else if (red.contains(player.getName())) {
            return "red";
        } else if (white.contains(player.getName())) {
            return "white";
        } else if (yellow.contains(player.getName())) {
            return "yellow";
        } else {
            return null;
        }
    }
}
