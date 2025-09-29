package gg.knockoff.game.hazards;

import gg.knockoff.game.PlayerData;
import gg.knockoff.game.knockoff;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class TNT extends hazard {

    public TNT(String s) {
        super(s);
    }

    @Override
    public void start() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                translatable("block.minecraft.tnt").color(NamedTextColor.RED),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        new BukkitRunnable() {
            int timer = 0;
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                    if (!pd.isPlayerDead) {
                        Location loc = new Location(player.getWorld(), player.getX(), player.getY() + 10, player.getZ(), player.getYaw(), player.getPitch());
                        TNTPrimed TNT = player.getWorld().spawn(loc, TNTPrimed.class, entity -> {

                        });
                    }
                    player.playSound(player, "minecraft:entity.tnt.primed", 50, 1);
                }
                if (timer == 3 || knockoff.getInstance().GameManager == null) {
                    cancel();
                }
                timer++;
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 40);
    }
}
