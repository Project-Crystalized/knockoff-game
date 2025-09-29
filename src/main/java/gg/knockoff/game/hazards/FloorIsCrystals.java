package gg.knockoff.game.hazards;

import gg.knockoff.game.GameManager;
import gg.knockoff.game.knockoff;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class FloorIsCrystals extends hazard {

    boolean IsHazardOver = false;

    public FloorIsCrystals(String name) {
        super(name);
    }

    @Override
    public void start() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                translatable("crystalized.game.knockoff.hazard.flooriscrystals").color(NamedTextColor.LIGHT_PURPLE),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        IsHazardOver = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player, "minecraft:block.conduit.activate", 50, 1);
        }
        new BukkitRunnable() {
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
                        Location below = p.getLocation().add(0, -1, 0);
                        //This is dumb, but this should make the radius bigger than 1 singular block
                        crystal(below.getBlock());
                        crystal(below.clone().add(0.5, 0, 0).getBlock());
                        crystal(below.clone().add(0, 0, 0.5).getBlock());
                        crystal(below.clone().add(-0.5, 0, 0).getBlock());
                        crystal(below.clone().add(0, 0, -0.5).getBlock());
                        crystal(below.clone().add(0, -1, 0).getBlock());
                        crystal(below.clone().add(0, 1, 0).getBlock());
                    }
                }
                if (IsHazardOver || knockoff.getInstance().GameManager == null) {
                    cancel();
                }
            }
            void crystal(Block b) {
                GameManager gm = knockoff.getInstance().GameManager;
                gm.startBreakingCrystal(b, knockoff.getInstance().getRandomNumber(0, 4), knockoff.getInstance().getRandomNumber(13, 20), true);
            }
        }.runTaskTimer(knockoff.getInstance(), 3, 1);

        new BukkitRunnable() {
            int timer = 10;

            public void run() {
                timer--;
                if (timer == 0) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p, "minecraft:block.conduit.deactivate", 50, 1);
                    }

                    IsHazardOver = true;
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 20);
    }
}
