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

import java.time.Duration;

import static net.kyori.adventure.text.Component.text;

public class PlayerListener implements Listener {

    //private int DeathTimer = 4;

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
            player.sendPlayerListHeaderAndFooter(
                    //Header
                    text("\n")
                            .append(text("Crystalized: ").color(NamedTextColor.LIGHT_PURPLE).append(text("KnockOff (Work in Progress)").color(NamedTextColor.GOLD)))
                            .append(text("\n")),

                    //Footer
                    text("\n Expect bugs since this is an early version and isn't complete. \n If you find any please report to TotallyNoCallum on the Crystalized Discord ")
                            .append(text("\n https://github.com/Project-Crystalized ").color(NamedTextColor.GRAY))
            );
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
            attacker.showTitle(Title.title(text(" "), text("[\uE103] " + player.getName()), Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(1), Duration.ofMillis(250))));
            attacker.playSound(attacker, "crystalized:effect.ally_kill", 50, 1);
        }
        pd.addDeath(1);
        pd.isPlayerDead = true;
        Location loc = new Location(Bukkit.getWorld("world"), knockoff.getInstance().mapdata.getCurrentMiddleXLength(), knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 10, knockoff.getInstance().mapdata.getCurrentMiddleZLength());
        player.teleport(loc);

        pd.takeawayLife(1); // takes away 1 life
        if (pd.getLives() > 0) { //If the player has lives left this code runs

            if (pd.getLives() == 4) {
                pd.setDeathtimer(4);
            } else if (pd.getLives() == 3) {
                pd.setDeathtimer(8);
            } else if (pd.getLives() == 2) {
                pd.setDeathtimer(10);
            } else if (pd.getLives() == 1) {
                pd.setDeathtimer(12);
            } else {
                pd.setDeathtimer(4); //fallback value in case somehow all of above statements are false
            }

            new BukkitRunnable() {
                public void run() {
                    player.sendActionBar(Component.translatable("crystalized.game.knockoff.respawn1")
                            .append(Component.text(pd.getDeathtimer()))
                            .append(Component.translatable("crystalized.game.knockoff.respawn2")));

                    if (pd.getDeathtimer() == 2) {
                        player.playSound(player, "crystalized:effect.knockoff_countdown", 50, 1);
                    } else if (pd.getDeathtimer() == 1) {
                        player.playSound(player, "crystalized:effect.knockoff_countdown", 50, 1.25F);
                    } else if (pd.getDeathtimer() == 0) {
                        player.playSound(player, "crystalized:effect.knockoff_countdown",50, 1.5F);
                    } else if (pd.getDeathtimer() == -1) {
                        player.playSound(player, "crystalized:effect.knockoff_countdown", 50, 2);
                        tpPlayersBack(player);
                        player.setGameMode(GameMode.SURVIVAL);
                        pd.setDeathtimer(0);
                        pd.isPlayerDead = false;
                        cancel();
                    }

                    if (!pd.isPlayerDead) {
                        cancel();
                    }
                    pd.setDeathtimer(pd.getDeathtimer() - 1);
                }
            }.runTaskTimer(knockoff.getInstance(), 1, 20);
        } else {
            if (pd.getLives() < 0) {
                //we kick the player if their lives is less than 0. To prevent cheating and to possibly catch bugs where players may die twice
                player.kick(Component.text("You're eliminated from the game but you have somehow died again. and/or your lives is measured in negative numbers! Please report this bug to the Crystalized devs.").color(NamedTextColor.RED));
            }
            pd.takeawayLife(1); // takes away 1 life
            Bukkit.getServer().sendMessage(text("[")
                    .append(Component.text("\uE103").color(NamedTextColor.RED))
                    .append(Component.text("] "))
                    .append(Component.text(player.getName())
                            .append(Component.text(" has been eliminated from the game!"))));  // TODO make this a translatable text component
            player.getPlayer().sendMessage(text("Your final stats: Kills: " + pd.getKills() + " Deaths: " + pd.getDeaths()));
            pd.isPlayerDead = true;
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
