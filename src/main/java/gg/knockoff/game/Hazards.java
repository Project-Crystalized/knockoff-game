package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;

import static net.kyori.adventure.text.Component.translatable;

public class Hazards {
    public static final ArrayList HazardList = new ArrayList();
    private static boolean IsHazardOver = false;

    public static void StartHazards() {
        IsHazardOver = true;
        HazardList.clear();
        HazardList.add("TNT");
        HazardList.add("SlimeTime");

        new BukkitRunnable() {
            int timer = knockoff.getInstance().getRandomNumber(30, 60);
            @Override
            public void run() {
                if (knockoff.getInstance().GameManager == null) {cancel();}
                if (knockoff.getInstance().DevMode) {
                    Bukkit.getServer().sendMessage(Component.text("Hazards disabled due to developer mode."));
                    cancel();
                }

                if (timer == 0) {
                    timer = knockoff.getInstance().getRandomNumber(30, 60);
                    NewHazard();
                }
                if (IsHazardOver) {
                    timer--;
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0 ,20);
    }

    private static void NewHazard() {
        IsHazardOver = false;
        String Select = HazardList.get(knockoff.getInstance().getRandomNumber(0, HazardList.size())).toString(); //Will get a random hazard from the list

        Component HazardMessage = Component.translatable("crystalized.game.knockoff.chat.hazard").color(NamedTextColor.GOLD);

        for (Player player : Bukkit.getOnlinePlayers()) {
            switch (Select) {
                case "TNT":
                    player.sendMessage(HazardMessage.append(Component.translatable("block.minecraft.tnt").color(NamedTextColor.RED)));
                    player.showTitle(
                            Title.title(
                                    HazardMessage, translatable("block.minecraft.tnt").color(NamedTextColor.RED),
                                    Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000)))
                    );
                    break;
                case "SlimeTime":
                    player.sendMessage(HazardMessage.append(Component.translatable("crystalized.game.knockoff.hazard.slimetime").color(NamedTextColor.GREEN)));
                    player.showTitle(
                            Title.title(
                                    HazardMessage, translatable("crystalized.game.knockoff.hazard.slimetime").color(NamedTextColor.GREEN),
                                    Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000)))
                    );
                    break;
                default:
                    break;
            }
        }

        switch (Select) {
            case "TNT":
                new BukkitRunnable() {
                    int timer = 0;
                    @Override
                    public void run() {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                            if (!pd.isPlayerDead) {
                                Location loc = new Location(player.getWorld(), player.getX(), player.getY() + 10, player.getZ(), player.getYaw(), player.getPitch());
                                TNTPrimed TNT = player.getWorld().spawn(loc, TNTPrimed.class, entity -> {

                                });
                            }
                            player.playSound(player, "minecraft:entity.tnt.primed",  50, 1);
                        }
                        if (timer == 4) {
                            IsHazardOver = true;
                            cancel();
                        }
                        timer++;
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 40);
                break;
            case "SlimeTime":
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 10 * 20, 2, false, false, true));
                    player.playSound(player, "minecraft:block.conduit.activate", 50, 1);
                }
                new BukkitRunnable() {
                    int timer = 0;
                    @Override
                    public void run() {
                        if (timer == 10) { //This should last the jump boost's duration
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.playSound(player, "minecraft:block.beacon.deactivate", 50, 1);
                            }
                            IsHazardOver = true;
                        }
                        timer++;
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 20);
                break;
        }
    }
}
