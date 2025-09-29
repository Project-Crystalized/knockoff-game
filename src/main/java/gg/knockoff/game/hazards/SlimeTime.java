package gg.knockoff.game.hazards;

import gg.knockoff.game.knockoff;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class SlimeTime extends hazard {

    public SlimeTime(String name) {
        super(name);
    }

    @Override
    public void start() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 12 * 20, 2, false, false, true));
            player.playSound(player, "minecraft:block.conduit.activate", 50, 1);
        }
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                translatable("crystalized.game.knockoff.hazard.slimetime").color(NamedTextColor.GREEN),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        new BukkitRunnable() {
            int timer = 0;
            public void run() {
                if (timer == 12) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player, "minecraft:block.conduit.deactivate", 50, 1);
                    }
                    cancel();
                }
                timer++;
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 20);
    }
}