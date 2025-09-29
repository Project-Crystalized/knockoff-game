package gg.knockoff.game.hazards;

import gg.knockoff.game.knockoff;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class BeeAttack extends hazard {

    public BeeAttack(String name) {
        super(name);
    }

    @Override
    public void start() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                translatable("crystalized.game.knockoff.hazard.beeattack").color(NamedTextColor.YELLOW),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        spawnBeeHive(getValidSpot(true));
        spawnBeeHive(getValidSpot(true));
    }

    private static void spawnBeeHive(Location loc) {
        Location loc2 = loc.clone().add(0, 1, 0);
        loc.getBlock().setType(Material.OAK_FENCE);
        loc.clone().add(0, 1, 0).getBlock().setType(Material.BEEHIVE);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(loc, "minecraft:block.wood.place", 1, 1);
        }
        new BukkitRunnable() {
            int BeesAmount = knockoff.getInstance().getRandomNumber(3, 6);
            public void run() {
                if (knockoff.getInstance().GameManager == null) {
                    loc.getBlock().setType(Material.AIR);
                    loc2.getBlock().setType(Material.AIR);
                    cancel();
                }

                //get random player
                List<Player> playerList = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
                        playerList.add(p);
                    }
                }
                Player randomPlayer = playerList.get(knockoff.getInstance().getRandomNumber(0, playerList.size()));

                spawnBee(loc.clone().add(0, 2, 0), randomPlayer);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(loc.clone().add(0, 2, 0), "minecraft:block.beehive.exit", 1, 1);
                }
                BeesAmount--;
                if (BeesAmount == 0) {
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 20);
    }

    private static void spawnBee(Location loc, Entity player) {
        Bee bee = loc.getWorld().spawn(loc, Bee.class, entity-> {
            entity.setTarget((LivingEntity) player);
            entity.setCustomNameVisible(true);
            entity.setBeeStingersInBody(10);
            entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(2);
        });
        new BukkitRunnable() {
            int health;
            int maxhealth = (int) bee.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
            int timer = 15 * 20;
            public void run() {
                health = (int) bee.getHealth();
                if (knockoff.getInstance() == null || timer == 0 || health == 0) {
                    bee.damage(20); //should kill
                    cancel();
                }
                timer --;
                bee.setBeeStingerCooldown(5);
                bee.setAnger(20);
                bee.customName(text("\uE11A" + "\uE11B".repeat(health) + "\uE11C".repeat(maxhealth - health) + "\uE11D").append(text(" (" + timer/20 + "s)")));
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 1);
    }
}
