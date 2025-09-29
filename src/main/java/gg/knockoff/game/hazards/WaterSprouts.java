package gg.knockoff.game.hazards;

import gg.knockoff.game.MapManager;
import gg.knockoff.game.knockoff;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class WaterSprouts extends hazard {

    public WaterSprouts(String name) {
        super(name);
    }

    @Override
    public void start() {
        name = "watersprouts";
        //Play sound to indicate this hazard started
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, "minecraft:item.trident.riptide_1", 1, 1);
        }
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                translatable("crystalized.game.knockoff.hazard.watersprouts").color(NamedTextColor.BLUE),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        new BukkitRunnable() {
            int timer = 7;

            public void run() {
                spawnWaterSprout();
                if (timer == 0 || knockoff.getInstance().GameManager == null) {
                    cancel();
                }
                timer--;
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 20);
    }

    private static void spawnWaterSprout() {
        List<Location> locs = new ArrayList<>();
        Location center = getValidSpot(false);
        if (knockoff.getInstance().GameManager == null) {return;}
        locs.add(center);
        locs.add(center.clone().add(1, 0, 0)); //right
        locs.add(center.clone().add(-1, 0, 0)); //left
        locs.add(center.clone().add(0, 0, 1)); //south
        locs.add(center.clone().add(0, 0, -1)); //north

        for (Location l : locs) {
            if (MapManager.isInsideCurrentSection(l)) {
                l.getBlock().setType(Material.SOUL_SAND);
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(center, "minecraft:item.trident.riptide_3", 3, 1);
        }

        new BukkitRunnable() {
            int timer = 7;
            public void run() {
                for (Location l : locs) {
                    l.add(0, 1, 0);
                    if (MapManager.isInsideCurrentSection(l)) {
                        l.getBlock().setType(Material.WATER); //This can replace blocks, not my problem
                    }
                }
                timer--;
                if (timer == 0) {
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 3);
    }
}
