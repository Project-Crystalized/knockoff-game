package gg.knockoff.game.hazards;

import gg.knockoff.game.knockoff;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.time.Duration;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class PoisonBushes extends hazard {

    public PoisonBushes(String name) {
        super(name);
    }

    @Override
    public void start() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                translatable("crystalized.game.knockoff.hazard.poisonbushes").color(NamedTextColor.GREEN),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        new BukkitRunnable() {
            int timer = 6;
            public void run() {
                spawnBush();
                if (timer == 0) {
                    cancel();
                }
                timer--;
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 2);
    }

    private static void spawnBush() {
        Location blockloc2 = getValidSpot(true);
        if (knockoff.getInstance().GameManager == null) {return;}

        blockloc2.getBlock().setType(Material.MANGROVE_LEAVES);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(blockloc2, "minecraft:block.cherry_leaves.place", 1, 1);
        }
        blockloc2.clone().add(new Vector(1,0,0)).getBlock().setType(Material.MANGROVE_LEAVES);
        blockloc2.clone().add(new Vector(0,0,1)).getBlock().setType(Material.MANGROVE_LEAVES);
        blockloc2.clone().add(new Vector(1,0,1)).getBlock().setType(Material.MANGROVE_LEAVES);

        //Make the bushes look different and slightly less boring and predictable
        int i = knockoff.getInstance().getRandomNumber(0, 2);
        switch (i) {
            case 1 -> {
                blockloc2.clone().add(new Vector(-1,0,0)).getBlock().setType(Material.MANGROVE_LEAVES);
                blockloc2.clone().add(new Vector(-1,0,-1)).getBlock().setType(Material.MANGROVE_LEAVES);
                blockloc2.clone().add(new Vector(0,0,-1)).getBlock().setType(Material.MANGROVE_LEAVES);
                blockloc2.clone().add(new Vector(0,1,0)).getBlock().setType(Material.MANGROVE_LEAVES);
            }
            case 2 -> {
                blockloc2.clone().add(new Vector(1,1,0)).getBlock().setType(Material.MANGROVE_LEAVES);
                blockloc2.clone().add(new Vector(0,1,1)).getBlock().setType(Material.MANGROVE_LEAVES);
                blockloc2.clone().add(new Vector(1,1,1)).getBlock().setType(Material.MANGROVE_LEAVES);
                blockloc2.clone().add(new Vector(0,1,0)).getBlock().setType(Material.MANGROVE_LEAVES);
            }
        }
    }
}
