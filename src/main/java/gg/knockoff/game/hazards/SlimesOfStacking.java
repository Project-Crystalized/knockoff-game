package gg.knockoff.game.hazards;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Slime;

import java.time.Duration;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class SlimesOfStacking extends hazard {

    public SlimesOfStacking(String name) {
        super(name);
    }

    @Override
    public void start() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                translatable("crystalized.game.knockoff.hazard.slimesofstacking").color(NamedTextColor.GREEN),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        spawnSlime(getValidSpot(true));
    }

    private static void spawnSlime(Location loc) {
        Slime slime3 = loc.getWorld().spawn(loc, Slime.class, entity->{
            entity.setSize(3);
        });
        Slime slime2 = loc.getWorld().spawn(loc, Slime.class, entity->{
            entity.setSize(2);
        });
        Slime slime1 = loc.getWorld().spawn(loc, Slime.class, entity->{
            entity.setSize(1);
        });
        slime3.addPassenger(slime2);
        slime2.addPassenger(slime1);
    }
}
