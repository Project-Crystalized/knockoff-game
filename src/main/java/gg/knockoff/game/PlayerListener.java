package gg.knockoff.game;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
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
import java.util.UUID;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.text;

public class PlayerListener implements Listener {

    //private int DeathTimer = 4;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (knockoff.getInstance().GameManager == null) {
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
            new QueueScoreBoard(player);
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
                    .append(Component.translatable("crystalized.game.knockoff.chat.deathgeneric")));
        } else {
            Bukkit.getServer().sendMessage(text("[\uE103] ")
                    .append(player.displayName())
                    .append(Component.translatable("crystalized.game.knockoff.chat.deathknockoff"))
                    .append(player.getKiller().displayName()));
            Player attacker = player.getKiller();
            PlayerData pda = knockoff.getInstance().GameManager.getPlayerData(attacker);
            pda.addKill(1);
            attacker.showTitle(Title.title(text(" "), text("[\uE103] ").append(player.displayName()), Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(1), Duration.ofMillis(250))));
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
                        if (GameManager.GameState.equals("game")) {
                            tpPlayersBack(player);
                            player.setGameMode(GameMode.SURVIVAL);
                            pd.setDeathtimer(0);
                            pd.isPlayerDead = false;
                        }
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
            Bukkit.getServer().sendMessage(text("[")
                    .append(Component.text("\uE103").color(NamedTextColor.RED))
                    .append(Component.text("] "))
                    .append(player.displayName())
                            .append(Component.translatable("crystalized.game.knockoff.chat.eliminated")));
            player.getPlayer().sendMessage(text("Your final stats: Kills: " + pd.getKills() + " Deaths: " + pd.getDeaths()));
            pd.isPlayerDead = true;
            pd.isEliminated = true;
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);
        Bukkit.getServer().sendMessage(Component.text("")
                .append(player.displayName())
                .append(Component.text(": "))
                .append(event.message()));
    }

    private static void tpPlayersBack(Player p) {

        Location blockloc = new Location(Bukkit.getWorld("world"),
                knockoff.getInstance().getRandomNumber(GameManager.SectionPlaceLocationX, knockoff.getInstance().mapdata.getCurrentXLength()) + 0.5,
                knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 5, //TODO temp
                knockoff.getInstance().getRandomNumber(GameManager.SectionPlaceLocationZ, knockoff.getInstance().mapdata.getCurrentZLength()) + 0.5
        );
        Location ploc = new Location(Bukkit.getWorld("world"), blockloc.getX(), blockloc.getY() + 2, blockloc.getZ());
        switch (Teams.GetPlayerTeam(p)) {
            case "blue" -> {
                blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
                Directional dir = (Directional) blockloc.getBlock().getBlockData();
                dir.setFacing(BlockFace.EAST);
                blockloc.getBlock().setBlockData(dir);
            }
            case "cyan" -> {
                blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
                Directional dir = (Directional) blockloc.getBlock().getBlockData();
                dir.setFacing(BlockFace.NORTH);
                blockloc.getBlock().setBlockData(dir);
                }
            case "green" -> {
                blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
                Directional dir = (Directional) blockloc.getBlock().getBlockData();
                dir.setFacing(BlockFace.SOUTH);
                blockloc.getBlock().setBlockData(dir);
            }
            case "lemon" -> {
                blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
                Directional dir = (Directional) blockloc.getBlock().getBlockData();
                dir.setFacing(BlockFace.WEST);
                blockloc.getBlock().setBlockData(dir);
            }
            case "lime" -> {
                blockloc.getBlock().setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                Directional dir = (Directional) blockloc.getBlock().getBlockData();
                dir.setFacing(BlockFace.EAST);
                blockloc.getBlock().setBlockData(dir);
            }
            case "magenta" -> {
                blockloc.getBlock().setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                Directional dir = (Directional) blockloc.getBlock().getBlockData();
                dir.setFacing(BlockFace.NORTH);
                blockloc.getBlock().setBlockData(dir);
            }
            case "orange" -> {
                blockloc.getBlock().setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                Directional dir = (Directional) blockloc.getBlock().getBlockData();
                dir.setFacing(BlockFace.SOUTH);
                blockloc.getBlock().setBlockData(dir);
            }
            case "peach" -> {
                blockloc.getBlock().setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                Directional dir = (Directional) blockloc.getBlock().getBlockData();
                dir.setFacing(BlockFace.WEST);
                blockloc.getBlock().setBlockData(dir);
            }
            case "purple" -> {
                blockloc.getBlock().setType(Material.GRAY_GLAZED_TERRACOTTA);
                Directional dir = (Directional) blockloc.getBlock().getBlockData();
                dir.setFacing(BlockFace.EAST);
                blockloc.getBlock().setBlockData(dir);
            }
            case "red" -> {
                blockloc.getBlock().setType(Material.GRAY_GLAZED_TERRACOTTA);
                Directional dir = (Directional) blockloc.getBlock().getBlockData();
                dir.setFacing(BlockFace.NORTH);
                blockloc.getBlock().setBlockData(dir);
            }
            case "white" -> {
                blockloc.getBlock().setType(Material.GRAY_GLAZED_TERRACOTTA);
                Directional dir = (Directional) blockloc.getBlock().getBlockData();
                dir.setFacing(BlockFace.SOUTH);
                blockloc.getBlock().setBlockData(dir);
            }
            case "yellow" -> {
                blockloc.getBlock().setType(Material.GRAY_GLAZED_TERRACOTTA);
                Directional dir = (Directional) blockloc.getBlock().getBlockData();
                dir.setFacing(BlockFace.WEST);
                blockloc.getBlock().setBlockData(dir);
            }
        }
        blockloc.getBlock().getState().update();
        p.teleport(ploc);
        p.lookAt(knockoff.getInstance().mapdata.getCurrentMiddleXLength(), knockoff.getInstance().mapdata.getCurrentMiddleYLength(), knockoff.getInstance().mapdata.getCurrentMiddleZLength(), LookAnchor.EYES);
    }

    @EventHandler
    public void PlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void OnPlayerDisconnect(PlayerConnectionCloseEvent event) {
        if (knockoff.getInstance().GameManager != null) {
            Teams.DisconnectPlayer(event.getPlayerName());
        }
        if (knockoff.getInstance().GameManager != null && Bukkit.getOnlinePlayers().equals(0)) {
            Bukkit.getLogger().log(Level.INFO, "All players have disconnected. The Game will now end.");
            knockoff.getInstance().GameManager.ForceEndGame();
        }
    }
}
