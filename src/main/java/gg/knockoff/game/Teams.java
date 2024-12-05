package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
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
            new BukkitRunnable() {
                @Override
                public void run() { //Clear every team when the game ends and everyone is kicked
                    if (knockoff.getInstance().GameManager == null) {
                        if (!blue.isEmpty()) {blue.clear();}
                        if (!cyan.isEmpty()) {cyan.clear();}
                        if (!green.isEmpty()) {green.clear();}
                        if (!lemon.isEmpty()) {lemon.clear();}
                        if (!lime.isEmpty()) {lime.clear();}
                        if (!magenta.isEmpty()) {magenta.clear();}
                        if (!orange.isEmpty()) {orange.clear();}
                        if (!peach.isEmpty()) {peach.clear();}
                        if (!purple.isEmpty()) {purple.clear();}
                        if (!red.isEmpty()) {red.clear();}
                        if (!white.isEmpty()) {white.clear();}
                        if (!yellow.isEmpty()) {yellow.clear();}
                        cancel();
                    }
                }
            }.runTaskTimer(knockoff.getInstance(), 20 ,1);

            if (playerlist.size() > 0) {
                if (blue.isEmpty()) {
                    blue.add(playerlist.get(0));
                    if (playerlist.size() > 12) {
                        blue.add(playerlist.get(12));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + blue + " in Team Blue");
                }
            }else {
                Bukkit.getLogger().log(Level.SEVERE, "Tried to add a player to team Blue but the player list is 0. Please report this as you shouldn't be able to get this error");
            }

            if (playerlist.size() > 1) { //If the player list is 2 or greater
                if (cyan.isEmpty()) {
                    cyan.add(playerlist.get(1));
                    if (playerlist.size() > 13) {
                        cyan.add(playerlist.get(13));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + cyan + " in Team Cyan");
                }
            }else {
                Bukkit.getLogger().log(Level.WARNING, "No player(s) available for Cyan team (FYI: Recommend getting an alt account or someone else to join. 2 or more players is recommended)");
            }

            if (playerlist.size() > 2) { //If the player list is 3 or greater
                if (green.isEmpty()) {
                    green.add(playerlist.get(2));
                    if (playerlist.size() > 14) {
                        green.add(playerlist.get(14));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + green + " in Team Green");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
            }

            if (playerlist.size() > 3) { //If the player list is 4 or greater
                if (lemon.isEmpty()) {
                    lemon.add(playerlist.get(3));
                    if (playerlist.size() > 15) {
                        lemon.add(playerlist.get(15));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + lemon + " in Team Lemon");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
            }

            if (playerlist.size() > 4) { //If the player list is 5 or greater
                if (lime.isEmpty()) {
                    lime.add(playerlist.get(4));
                    if (playerlist.size() > 16) {
                        lime.add(playerlist.get(16));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + lime + " in Team Lime");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
            }

            if (playerlist.size() > 5) { //If the player list is 6 or greater
                if (magenta.isEmpty()) {
                    magenta.add(playerlist.get(5));
                    if (playerlist.size() > 17) {
                        magenta.add(playerlist.get(17));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + magenta + " in Team Magenta");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
            }

            if (playerlist.size() > 6) { //If the player list is 7 or greater
                if (orange.isEmpty()) {
                    orange.add(playerlist.get(6));
                    if (playerlist.size() > 18) {
                        orange.add(playerlist.get(18));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + orange + " in Team Orange");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Orange team");
            }

            if (playerlist.size() > 7) { //If the player list is 8 or greater
                if (peach.isEmpty()) {
                    peach.add(playerlist.get(7));
                    if (playerlist.size() > 19) {
                        peach.add(playerlist.get(19));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + peach + " in Team Peach");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Peach team");
            }

            if (playerlist.size() > 8) { //If the player list is 9 or greater
                if (purple.isEmpty()) {
                    purple.add(playerlist.get(8));
                    if (playerlist.size() > 20) {
                        purple.add(playerlist.get(20));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + purple + " in Team Purple");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Purple team");
            }

            if (playerlist.size() > 9) { //If the player list is 10 or greater
                if (red.isEmpty()) {
                    red.add(playerlist.get(9));
                    if (playerlist.size() > 21) {
                        red.add(playerlist.get(21));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + red + " in Team Red");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Red team");
            }

            if (playerlist.size() > 10) { //If the player list is 11 or greater
                if (white.isEmpty()) {
                    white.add(playerlist.get(10));
                    if (playerlist.size() > 22) {
                        white.add(playerlist.get(22));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + white + " in Team White");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for White team");
            }

            if (playerlist.size() > 11) { //If the player list is 12
                if (yellow.isEmpty()) {
                    yellow.add(playerlist.get(11));
                    if (playerlist.size() > 23) {
                        yellow.add(playerlist.get(23));
                    }
                    Bukkit.getLogger().log(Level.INFO, "Player(s) " + yellow + " in Team Yellow");
                }
            }else {
                Bukkit.getLogger().log(Level.INFO, "No player(s) available for Yellow team");
            }
            TeamStatus.Init();
        }
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

    public static void SetPlayerDisplayNames(Player player) {
        if (blue.contains(player.getName())) {
            player.displayName(Component.text("\uE120 ").append(Component.text(player.getName()).color(TEAM_BLUE)));
        } else if (cyan.contains(player.getName())) {
            player.displayName(Component.text("\uE121 ").append(Component.text(player.getName()).color(TEAM_CYAN)));
        } else if (green.contains(player.getName())) {
            player.displayName(Component.text("\uE122 ").append(Component.text(player.getName()).color(TEAM_GREEN)));
        } else if (lemon.contains(player.getName())) {
            player.displayName(Component.text("\uE128 ").append(Component.text(player.getName()).color(TEAM_LEMON)));
        } else if (lime.contains(player.getName())) {
            player.displayName(Component.text("\uE123 ").append(Component.text(player.getName()).color(TEAM_LIME)));
        } else if (magenta.contains(player.getName())) {
            player.displayName(Component.text("\uE124 ").append(Component.text(player.getName()).color(TEAM_MAGENTA)));
        } else if (orange.contains(player.getName())) {
            player.displayName(Component.text("\uE129 ").append(Component.text(player.getName()).color(TEAM_ORANGE)));
        } else if (peach.contains(player.getName())) {
            player.displayName(Component.text("\uE12A ").append(Component.text(player.getName()).color(TEAM_PEACH)));
        } else if (purple.contains(player.getName())) {
            player.displayName(Component.text("\uE12B ").append(Component.text(player.getName()).color(TEAM_PURPLE)));
        } else if (red.contains(player.getName())) {
            player.displayName(Component.text("\uE125 ").append(Component.text(player.getName()).color(TEAM_RED)));
        } else if (white.contains(player.getName())) {
            player.displayName(Component.text("\uE126 ").append(Component.text(player.getName()).color(TEAM_WHITE)));
        } else if (yellow.contains(player.getName())) {
            player.displayName(Component.text("\uE127 ").append(Component.text(player.getName()).color(TEAM_YELLOW)));
        } else {
            player.displayName(Component.text("[Unknown Team]").append(Component.text(player.getName())));
        }
    }
}

class TeamStatus{

    public static String BlueStatus = "";
    public static String CyanStatus = "";
    public static String GreenStatus = "";
    public static String LemonStatus = "";
    public static String LimeStatus = "";
    public static String MagentaStatus = "";
    public static String OrangeStatus = "";
    public static String PeachStatus = "";
    public static String PurpleStatus = "";
    public static String RedStatus = "";
    public static String WhiteStatus = "";
    public static String YellowStatus = "";
    private static ArrayList TeamsList = new ArrayList();

    //I hate this class
    public static void Init() {

        //If blue is empty, set it to "dead", otherwise set it to "alive"
        //Also this specific case should never trigger, Blue is the first team player 1
        //is always in, You need at least 1 player to start a game
        if (Teams.blue.isEmpty()) {BlueStatus = "dead";} else {BlueStatus = "alive";}
        if (Teams.cyan.isEmpty()) {CyanStatus = "dead";} else {CyanStatus = "alive";}
        if (Teams.green.isEmpty()) {GreenStatus = "dead";} else {GreenStatus = "alive";}
        if (Teams.lemon.isEmpty()) {LemonStatus = "dead";} else {LemonStatus = "alive";}
        if (Teams.lime.isEmpty()) {LimeStatus = "dead";} else {LimeStatus = "alive";}
        if (Teams.magenta.isEmpty()) {MagentaStatus = "dead";} else {MagentaStatus = "alive";}
        if (Teams.orange.isEmpty()) {OrangeStatus = "dead";} else {OrangeStatus = "alive";}
        if (Teams.peach.isEmpty()) {PeachStatus = "dead";} else {PeachStatus = "alive";}
        if (Teams.purple.isEmpty()) {PurpleStatus = "dead";} else {PurpleStatus = "alive";}
        if (Teams.red.isEmpty()) {RedStatus = "dead";} else {RedStatus = "alive";}
        if (Teams.white.isEmpty()) {WhiteStatus = "dead";} else {WhiteStatus = "alive";}
        if (Teams.yellow.isEmpty()) {YellowStatus = "dead";} else {YellowStatus = "alive";}

        new BukkitRunnable() {
            @Override
            public void run() {

                // Check if all players in the team are alive. If not set them to dead
                if (knockoff.getInstance().GameManager == null) {cancel();} //Putting this in between these blocks of code to prevent errors in console. This stops the BukkitRunnable when the game ends

                //This is going get annoying to copy-paste for all 12 teams :cry:
                // Could be optimised, marking all the ugly code in this project with this phrase ig
                int blue = 0; int bi = 0;
                while (bi != Teams.blue.size()) {
                    Player p = Bukkit.getPlayer(Teams.blue.get(bi)); PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
                    if (!pd.isEliminated) {
                        blue++;
                    }
                    bi++;
                }
                if (blue == Teams.blue.size()) {BlueStatus = "alive";} else {BlueStatus = "dead";}

                if (knockoff.getInstance().GameManager == null) {cancel();}
                int cyan = 0; int ci = 0;
                while (ci != Teams.cyan.size()) {
                    Player p = Bukkit.getPlayer(Teams.cyan.get(ci)); PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
                    if (!pd.isEliminated) {
                        cyan++;
                    }
                    ci++;
                }
                if (cyan == Teams.cyan.size()) {CyanStatus = "alive";} else {CyanStatus = "dead";}

                if (knockoff.getInstance().GameManager == null) {cancel();}
                int green = 0; int gi = 0;
                while (gi != Teams.green.size()) {
                    Player p = Bukkit.getPlayer(Teams.green.get(gi)); PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
                    if (!pd.isEliminated) {
                        green++;
                    }
                    gi++;
                }
                if (green == Teams.green.size()) {GreenStatus = "alive";} else {GreenStatus = "dead";}

                if (knockoff.getInstance().GameManager == null) {cancel();}
                int lemon = 0; int li = 0;
                while (li != Teams.lemon.size()) {
                    Player p = Bukkit.getPlayer(Teams.lemon.get(li)); PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
                    if (!pd.isEliminated) {
                        lemon++;
                    }
                    li++;
                }
                if (lemon == Teams.lemon.size()) {LemonStatus = "alive";} else {LemonStatus = "dead";}

                if (knockoff.getInstance().GameManager == null) {cancel();}
                int lime = 0; int l2i = 0;
                while (l2i != Teams.lime.size()) {
                    Player p = Bukkit.getPlayer(Teams.lime.get(l2i)); PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
                    if (!pd.isEliminated) {
                        lime++;
                    }
                    l2i++;
                }
                if (lime == Teams.lime.size()) {LimeStatus = "alive";} else {LimeStatus = "dead";}

                if (knockoff.getInstance().GameManager == null) {cancel();}
                int magenta = 0; int mi = 0;
                while (mi != Teams.magenta.size()) {
                    Player p = Bukkit.getPlayer(Teams.magenta.get(mi)); PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
                    if (!pd.isEliminated) {
                        magenta++;
                    }
                    mi++;
                }
                if (magenta == Teams.magenta.size()) {MagentaStatus = "alive";} else {MagentaStatus = "dead";}

                if (knockoff.getInstance().GameManager == null) {cancel();}
                int orange = 0; int oi = 0;
                while (oi != Teams.orange.size()) {
                    Player p = Bukkit.getPlayer(Teams.orange.get(oi)); PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
                    if (!pd.isEliminated) {
                        orange++;
                    }
                    oi++;
                }
                if (orange == Teams.orange.size()) {OrangeStatus = "alive";} else {OrangeStatus = "dead";}

                if (knockoff.getInstance().GameManager == null) {cancel();}
                int peach = 0; int pi = 0;
                while (pi != Teams.peach.size()) {
                    Player p = Bukkit.getPlayer(Teams.peach.get(pi)); PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
                    if (!pd.isEliminated) {
                        peach++;
                    }
                    pi++;
                }
                if (peach == Teams.peach.size()) {PeachStatus = "alive";} else {PeachStatus = "dead";}

                //TODO i fucking hate this but idk how else to format this well
                // Could be optimised especially
                if (BlueStatus.equals("alive") && CyanStatus.equals("dead") && GreenStatus.equals("dead") && LemonStatus.equals("dead") && LimeStatus.equals("dead") && MagentaStatus.equals("dead") && OrangeStatus.equals("dead")
                        && PeachStatus.equals("dead") && PurpleStatus.equals("dead") && RedStatus.equals("dead") && WhiteStatus.equals("dead") && YellowStatus.equals("dead")) {
                    GameManager.StartEndGame("blue");
                } else if (BlueStatus.equals("dead") && CyanStatus.equals("alive") && GreenStatus.equals("dead") && LemonStatus.equals("dead") && LimeStatus.equals("dead") && MagentaStatus.equals("dead") && OrangeStatus.equals("dead")
                        && PeachStatus.equals("dead") && PurpleStatus.equals("dead") && RedStatus.equals("dead") && WhiteStatus.equals("dead") && YellowStatus.equals("dead")) {
                    GameManager.StartEndGame("cyan");
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 20, 1);
    }
}

class CustomPlayerNametags{
    public static void CustomPlayerNametags(Player player) {

        Location ploc = new Location(player.getWorld(), player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        TextDisplay displayfront = ploc.getWorld().spawn(ploc, TextDisplay.class, entity -> {
            entity.setBillboard(Display.Billboard.CENTER);
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                if (knockoff.getInstance().GameManager == null || !player.isOnline()) {
                    displayfront.remove();
                    cancel();
                } else {
                    PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                    if (pd.isPlayerDead) {
                        displayfront.text(Component.text(""));
                    } else {
                        displayfront.text(Component.text("")
                                .append(player.displayName())
                                .append(Component.text("\nKB: "))
                                .append(Component.text(pd.getDamagepercentage()))
                                .append(Component.text("% | L: "))
                                .append(Component.text(pd.getLives()))
                        );
                    }

                    Location ploc = new Location(player.getWorld(), player.getX(), player.getY() + 2.5, player.getZ(), player.getYaw(), player.getPitch());
                    displayfront.teleport(ploc);
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 20, 1);
    }
}