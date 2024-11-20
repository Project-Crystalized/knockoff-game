package gg.knockoff.game;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Integer.MAX_VALUE, 1, false, false, true));
        } else {
            player.kick(Component.text("A game is currently is progress, try joining again later.").color(NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        // if game isn't going, cancel event
        GameManager gc = knockoff.getInstance().GameManager;
        if (gc == null) {
            e.setCancelled(true);
            return;
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
        if (pd.getLives() > 0) { //If the player has lives left this code runs
            //TODO TP player to near the top of the current map section
            if (pd.getLives() == 5 || pd.getLives() == 4) { //Couldn't use outside variables in bukkitrunnables which sucks. Next best thing I came up with is repeated code lol
                new BukkitRunnable() {
                    int timer = 5;
                    public void run() {
                        player.sendActionBar(Component.text("You will respawn in " + timer + " seconds.")); //TODO make this a translatable text component
                        timer -= 1;
                        if (timer == -1) {
                            //TODO implement function to TP player on the current map section again, make a new spawnpoint like how the beginning of the game is
                            player.setGameMode(GameMode.SURVIVAL);
                            cancel();
                        }
                    }
                }.runTaskTimer(knockoff.getInstance(), 1, 20);
            }
            if (pd.getLives() == 3) {
                new BukkitRunnable() {
                    int timer = 10;
                    public void run() {
                        player.sendActionBar(Component.text("You will respawn in " + timer + " seconds.")); //TODO make this a translatable text component
                        timer -= 1;
                        if (timer == -1) {
                            //TODO implement function to TP player on the current map section again, make a new spawnpoint like how the beginning of the game is
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
                        player.sendActionBar(Component.text("You will respawn in " + timer + " seconds.")); //TODO make this a translatable text component
                        timer -= 1;
                        if (timer == -1) {
                            //TODO implement function to TP player on the current map section again, make a new spawnpoint like how the beginning of the game is
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
                        player.sendActionBar(Component.text("You will respawn in " + timer + " seconds.")); //TODO make this a translatable text component
                        timer -= 1;
                        if (timer == -1) {
                            //TODO implement function to TP player on the current map section again, make a new spawnpoint like how the beginning of the game is
                            player.setGameMode(GameMode.SURVIVAL);
                            player.sendMessage(Component.text("[!] You are on your last life! Be careful from now on")); // TODO make this a translatable text component
                            cancel();
                        }
                    }
                }.runTaskTimer(knockoff.getInstance(), 1, 20);
            }
            pd.takeawayLife(1); // takes away 1 life
        } else { //Player has no lives, so we make them unable to player and put them in spectator
            if (pd.getLives() < 0) {
                //we kick the player if their lives is less than 0. To prevent cheating and to possibly catch bugs where players may die twice
                player.kick(Component.text("You're eliminated from the game but you have somehow died again. and/or your lives is measured in negative numbers! Please report this bug to the Crystalized devs.").color(NamedTextColor.RED));
            }
            pd.takeawayLife(1); // takes away 1 life
            player.sendMessage(Component.text("[!] You are eliminated from the game!")); // TODO make this a translatable text component
        }
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }
    }
}
