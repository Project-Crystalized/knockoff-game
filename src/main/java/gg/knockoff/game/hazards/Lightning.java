package gg.knockoff.game.hazards;

import gg.knockoff.game.knockoff;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class Lightning extends hazard {

    public Lightning(String name) {
        super(name);
    }

    @Override
    public void start() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                translatable("crystalized.game.knockoff.hazard.lightning").color(NamedTextColor.AQUA),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        new BukkitRunnable() {
            int timer = 12;
            public void run() {
                Location loc = getValidSpot(true);
                spawnLightningRod(loc);
                timer --;
                if (timer == 0) {
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 3);
    }

    private static void spawnLightningRod(Location loc) {
        TextDisplay name = loc.getWorld().spawn(loc.clone().add(0, 1.5, 0), TextDisplay.class, entity -> {
            entity.setBillboard(Display.Billboard.CENTER);
        });
        loc.getBlock().setType(Material.LIGHTNING_ROD);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(loc, "minecraft:block.copper.place", 1, 1);
        }
        new BukkitRunnable() {
            int timer = knockoff.getInstance().getRandomNumber(4, 6);
            public void run() {
                if (knockoff.getInstance().GameManager == null) {
                    name.remove();
                    cancel();
                }
                if (timer == 0) {
                    loc.getBlock().setType(Material.AIR);
                    loc.getWorld().spawn(loc, LightningStrike.class);
                    name.remove();
                    cancel();
                }
                name.text(text("LIGHTNING STRIKE IN ").color(RED).append(text(timer).color(WHITE)));
                timer--;
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 20);
    }
}
