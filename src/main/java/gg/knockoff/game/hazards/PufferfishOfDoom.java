package gg.knockoff.game.hazards;

import gg.knockoff.game.knockoff;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.PufferFish;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class PufferfishOfDoom extends hazard {

    public PufferfishOfDoom(String name) {
        super(name);
    }

    @Override
    public void start() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                translatable("crystalized.game.knockoff.hazard.pufferfish").color(NamedTextColor.YELLOW),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        new BukkitRunnable() {
            int timer = knockoff.getInstance().getRandomNumber(8, 12);
            public void run() {
                spawnPufferfish(getValidSpot(true));
                timer--;
                if (timer == 0) {
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 1);
    }

    private static void spawnPufferfish(Location loc) {
        PufferFish fish = loc.getWorld().spawn(loc, PufferFish.class, entity -> {
            entity.setCustomNameVisible(true);
            entity.setPuffState(2);
            entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(1);
        });
        new BukkitRunnable() {
            int health;
            int maxhealth = (int) fish.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
            public void run() {
                health = (int) fish.getHealth();
                if (knockoff.getInstance() == null) {
                    fish.remove();
                    cancel();
                }
                if (fish.getHealth() == 0) {
                    cancel();
                }
                fish.customName(text("\uE11A" + "\uE11B".repeat(health) + "\uE11C".repeat(maxhealth - health) + "\uE11D"));
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 1);
    }
}
