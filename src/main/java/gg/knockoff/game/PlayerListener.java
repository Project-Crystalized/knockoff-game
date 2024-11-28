package gg.knockoff.game;

import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static net.kyori.adventure.text.Component.text;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (knockoff.getInstance().GameManager == null) {
            GameManager gameManager = knockoff.getInstance().GameManager;

            player.teleport(knockoff.getInstance().mapdata.get_que_spawn(player.getWorld()));
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            player.setGameMode(GameMode.ADVENTURE);
            player.removePotionEffect(PotionEffectType.REGENERATION);
            player.removePotionEffect(PotionEffectType.HUNGER);
            player.removePotionEffect(PotionEffectType.RESISTANCE);
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Integer.MAX_VALUE, 1, false, false, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 255, false, false, false));
        } else {
            player.kick(Component.text("A game is currently is progress, try joining again later.").color(NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
        event.setCancelled(true);
        player.setGameMode(GameMode.SPECTATOR);
        if (player.getKiller() == null) {
            Bukkit.getServer().sendMessage(text("[\uE103] ")
                    .append(player.displayName())
                    .append(Component.text(" Died")));
        } else {
            Bukkit.getServer().sendMessage(text("[\uE103] ")
                    .append(player.displayName())
                    .append(Component.text(" was knocked off by "))
                    .append(player.getKiller().displayName()));
            Player attacker = player.getKiller();
            PlayerData pda = knockoff.getInstance().GameManager.getPlayerData(attacker);
            pda.addKill(1);
            attacker.showTitle(Title.title(text(" "), text("" + player)));
            attacker.playSound(attacker, "crystalized:effect.kill_streak_1", 50, 1);
        }
        if (pd.getLives() > 0) { //If the player has lives left this code run
            pd.addDeath(1);
            pd.isPlayerDead = true;
            Location loc = new Location(Bukkit.getWorld("world"), knockoff.getInstance().mapdata.getCurrentMiddleXLength(), knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 10, knockoff.getInstance().mapdata.getCurrentMiddleZLength());
            player.teleport(loc);
            if (pd.getLives() == 5 || pd.getLives() == 4) { //Couldn't use outside variables in bukkitrunnables which sucks. Next best thing I came up with is repeated code lol
                new BukkitRunnable() {
                    int timer = 5;

                    public void run() {
                        player.sendActionBar(Component.translatable("crystalized.game.knockoff.respawn1")
                                .append(Component.text(timer))
                                .append(Component.translatable("crystalized.game.knockoff.respawn2")));
                        timer -= 1;
                        if (timer == -1) {
                            tpPlayersBack(player);

                            player.setGameMode(GameMode.SURVIVAL);
                            pd.isPlayerDead = false;
                            cancel();
                        }
                    }
                }.runTaskTimer(knockoff.getInstance(), 1, 20);
            }
            if (pd.getLives() == 3) {
                new BukkitRunnable() {
                    int timer = 10;

                    public void run() {
                        player.sendActionBar(Component.translatable("crystalized.game.knockoff.respawn1")
                                .append(Component.text(timer))
                                .append(Component.translatable("crystalized.game.knockoff.respawn2")));
                        timer -= 1;
                        if (timer == -1) {
                            tpPlayersBack(player);

                            pd.isPlayerDead = false;
                            player.setGameMode(GameMode.SURVIVAL);
                            cancel();
                        }
                    }
                }.runTaskTimer(knockoff.getInstance(), 1, 20);
            }
            if (pd.getLives() == 2) {
                new BukkitRunnable() {
                    int timer = 15;

                    public void run() {
                        player.sendActionBar(Component.translatable("crystalized.game.knockoff.respawn1")
                                .append(Component.text(timer))
                                .append(Component.translatable("crystalized.game.knockoff.respawn2")));
                        timer -= 1;
                        if (timer == -1) {
                            tpPlayersBack(player);

                            pd.isPlayerDead = false;
                            player.setGameMode(GameMode.SURVIVAL);
                            cancel();
                        }
                    }
                }.runTaskTimer(knockoff.getInstance(), 1, 20);
            }
            if (pd.getLives() == 1) {
                new BukkitRunnable() {
                    int timer = 15;

                    public void run() {
                        player.sendActionBar(Component.translatable("crystalized.game.knockoff.respawn1")
                                .append(Component.text(timer))
                                .append(Component.translatable("crystalized.game.knockoff.respawn2")));
                        timer -= 1;
                        if (timer == -1) {
                            tpPlayersBack(player);

                            player.setGameMode(GameMode.SURVIVAL);
                            pd.isPlayerDead = false;
                            player.sendMessage(Component.text("[!] You are on your last life! Be careful from now on")); // TODO make this a translatable text component
                            cancel();
                        }
                    }
                }.runTaskTimer(knockoff.getInstance(), 1, 20);
            }
            pd.takeawayLife(1); // takes away 1 life
        } else { //Player has no lives, so we make them unable to play and put them in spectator
            if (pd.getLives() < 0) {
                //we kick the player if their lives is less than 0. To prevent cheating and to possibly catch bugs where players may die twice
                player.kick(Component.text("You're eliminated from the game but you have somehow died again. and/or your lives is measured in negative numbers! Please report this bug to the Crystalized devs.").color(NamedTextColor.RED));
            }
            pd.takeawayLife(1); // takes away 1 life
            //player.sendMessage(Component.text("[!] You are eliminated from the game!"));
            Bukkit.getServer().sendMessage(text("[")
                    .append(Component.text("\uE103").color(NamedTextColor.RED))
                    .append(Component.text("] "))
                    .append(Component.text(player.getName())
                    .append(Component.text(" has been eliminated from the game!"))));  // TODO make this a translatable text component
            player.getPlayer().sendMessage(text("Your final stats: Kills: " + pd.getKills() + " Deaths: " + pd.getDeaths()));
            pd.isPlayerDead = true;
        }
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);
        if (knockoff.getInstance().GameManager == null) {
            Bukkit.getServer().sendMessage(Component.text("")
                    .append(player.displayName())
                    .append(Component.text(": "))
                    .append(event.message()));
        } else {
            if (Teams.GetPlayerTeam(player).equals("blue")) {
                Bukkit.getServer().sendMessage(Component.text("")
                        .append(player.displayName().color(Teams.TEAM_BLUE))
                        .append(Component.text(": "))
                        .append(event.message().color(Teams.TEAM_BLUE)));
            } else if (Teams.GetPlayerTeam(player).equals("cyan")) {
                Bukkit.getServer().sendMessage(Component.text("")
                        .append(player.displayName().color(Teams.TEAM_CYAN))
                        .append(Component.text(": "))
                        .append(event.message().color(Teams.TEAM_CYAN)));
            } else if (Teams.GetPlayerTeam(player).equals("green")) {
                Bukkit.getServer().sendMessage(Component.text("")
                        .append(player.displayName().color(Teams.TEAM_GREEN))
                        .append(Component.text(": "))
                        .append(event.message().color(Teams.TEAM_GREEN)));
            } else if (Teams.GetPlayerTeam(player).equals("lemon")) {
                Bukkit.getServer().sendMessage(Component.text("")
                        .append(player.displayName().color(Teams.TEAM_LEMON))
                        .append(Component.text(": "))
                        .append(event.message().color(Teams.TEAM_LEMON)));
            } else if (Teams.GetPlayerTeam(player).equals("lime")) {
                Bukkit.getServer().sendMessage(Component.text("")
                        .append(player.displayName().color(Teams.TEAM_LIME))
                        .append(Component.text(": "))
                        .append(event.message().color(Teams.TEAM_LIME)));
            } else if (Teams.GetPlayerTeam(player).equals("magenta")) {
                Bukkit.getServer().sendMessage(Component.text("")
                        .append(player.displayName().color(Teams.TEAM_MAGENTA))
                        .append(Component.text(": "))
                        .append(event.message().color(Teams.TEAM_MAGENTA)));
            } else if (Teams.GetPlayerTeam(player).equals("orange")) {
                Bukkit.getServer().sendMessage(Component.text("")
                        .append(player.displayName().color(Teams.TEAM_ORANGE))
                        .append(Component.text(": "))
                        .append(event.message().color(Teams.TEAM_ORANGE)));
            } else if (Teams.GetPlayerTeam(player).equals("peach")) {
                Bukkit.getServer().sendMessage(Component.text("")
                        .append(player.displayName().color(Teams.TEAM_PEACH))
                        .append(Component.text(": "))
                        .append(event.message().color(Teams.TEAM_PEACH)));
            } else if (Teams.GetPlayerTeam(player).equals("purple")) {
                Bukkit.getServer().sendMessage(Component.text("")
                        .append(player.displayName().color(Teams.TEAM_PURPLE))
                        .append(Component.text(": "))
                        .append(event.message().color(Teams.TEAM_PURPLE)));
            } else if (Teams.GetPlayerTeam(player).equals("red")) {
                Bukkit.getServer().sendMessage(Component.text("")
                        .append(player.displayName().color(Teams.TEAM_RED))
                        .append(Component.text(": "))
                        .append(event.message().color(Teams.TEAM_RED)));
            } else if (Teams.GetPlayerTeam(player).equals("white")) {
                Bukkit.getServer().sendMessage(Component.text("") //bit pointless adding .color(Teams.TEAM_WHITE) here but whatever
                        .append(player.displayName().color(Teams.TEAM_WHITE))
                        .append(Component.text(": "))
                        .append(event.message().color(Teams.TEAM_WHITE)));
            } else if (Teams.GetPlayerTeam(player).equals("yellow")) {
                Bukkit.getServer().sendMessage(Component.text("")
                        .append(player.displayName().color(Teams.TEAM_YELLOW))
                        .append(Component.text(": "))
                        .append(event.message().color(Teams.TEAM_YELLOW)));
            } else {
                Bukkit.getServer().sendMessage(Component.text("")
                        .append(player.displayName())
                        .append(Component.text(": "))
                        .append(event.message()));
            }
        }
    }

    private static void tpPlayersBack(Player p) { //TODO temporary for now

        int SectionPlaceLocationX = GameManager.SectionPlaceLocationX;
        int SectionPlaceLocationZ = GameManager.SectionPlaceLocationZ;

        if (Teams.GetPlayerTeam(p).equals("blue")) {
            Location blueloc = new Location(Bukkit.getWorld("world"), SectionPlaceLocationX + 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, SectionPlaceLocationZ + 6);
            p.teleport(blueloc);
        }
        else if (Teams.GetPlayerTeam(p).equals("cyan")) {
            Location cyanloc = new Location(Bukkit.getWorld("world"), knockoff.getInstance().mapdata.getCurrentXLength() - 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, knockoff.getInstance().mapdata.getCurrentZLength() - 6);
            p.teleport(cyanloc);
        }
        else if (Teams.GetPlayerTeam(p).equals("green")) {
            Location greenloc = new Location(Bukkit.getWorld("world"), knockoff.getInstance().mapdata.getCurrentXLength() - 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, SectionPlaceLocationZ + 6);
            p.teleport(greenloc);
        }
        else if (Teams.GetPlayerTeam(p).equals("lemon")) {
            Location greenloc = new Location(Bukkit.getWorld("world"), SectionPlaceLocationX + 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, knockoff.getInstance().mapdata.getCurrentZLength() - 6);
            p.teleport(greenloc);
        }
        else if (Teams.GetPlayerTeam(p).equals("lime")) { //Yes im aware this has blueloc as its variable, I copy pasted the first 4 lol
            Location blueloc = new Location(Bukkit.getWorld("world"), SectionPlaceLocationX + 16, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, SectionPlaceLocationZ + 6);
            p.teleport(blueloc);
        }
        else if (Teams.GetPlayerTeam(p).equals("magenta")) {
            Location cyanloc = new Location(Bukkit.getWorld("world"), knockoff.getInstance().mapdata.getCurrentXLength() - 16, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, knockoff.getInstance().mapdata.getCurrentZLength() - 6);
            p.teleport(cyanloc);
        }
        else if (Teams.GetPlayerTeam(p).equals("orange")) {
            Location greenloc = new Location(Bukkit.getWorld("world"), knockoff.getInstance().mapdata.getCurrentXLength() - 16, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, SectionPlaceLocationZ + 6);
            p.teleport(greenloc);
        }
        else if (Teams.GetPlayerTeam(p).equals("peach")) {
            Location greenloc = new Location(Bukkit.getWorld("world"), SectionPlaceLocationX + 16, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, knockoff.getInstance().mapdata.getCurrentZLength() - 6);
            p.teleport(greenloc);
        }
        else if (Teams.GetPlayerTeam(p).equals("purple")) {
            Location blueloc = new Location(Bukkit.getWorld("world"), SectionPlaceLocationX + 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, SectionPlaceLocationZ + 16);
            p.teleport(blueloc);
        }
        else if (Teams.GetPlayerTeam(p).equals("red")) {
            Location cyanloc = new Location(Bukkit.getWorld("world"), knockoff.getInstance().mapdata.getCurrentXLength() - 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, knockoff.getInstance().mapdata.getCurrentZLength() - 16);
            p.teleport(cyanloc);
        }
        else if (Teams.GetPlayerTeam(p).equals("white")) {
            Location greenloc = new Location(Bukkit.getWorld("world"), knockoff.getInstance().mapdata.getCurrentXLength() - 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, SectionPlaceLocationZ + 16);
            p.teleport(greenloc);
        }
        else if (Teams.GetPlayerTeam(p).equals("yellow")) {
            Location greenloc = new Location(Bukkit.getWorld("world"), SectionPlaceLocationX + 6, knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 3, knockoff.getInstance().mapdata.getCurrentZLength() - 16);
            p.teleport(greenloc);
        }
        else {
            Location loc = new Location(Bukkit.getWorld("world"), knockoff.getInstance().mapdata.getCurrentMiddleXLength(), knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 10, knockoff.getInstance().mapdata.getCurrentMiddleZLength());
            p.teleport(loc);
        }
        p.lookAt(knockoff.getInstance().mapdata.getCurrentMiddleXLength(), knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentMiddleZLength(), LookAnchor.EYES);
    }

    @EventHandler
    public void PlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
