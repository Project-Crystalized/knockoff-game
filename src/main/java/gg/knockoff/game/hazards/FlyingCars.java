package gg.knockoff.game.hazards;

import gg.knockoff.game.knockoff;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class FlyingCars extends hazard {

    private static final List<String> CarsList = Arrays.asList(
            "models/car/abby_car", "models/car/abby_minicar", "models/car/abby_minicar2", "models/car/abby_truck",
            "models/car/beat_up_truck", "models/car/broken_car", "models/car/firetruck", "models/car/military_bus",
            "models/car/military_van", "models/car/taxi"
    );

    public FlyingCars(String name) {
        super(name);
    }

    @Override
    public void start() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                translatable("crystalized.game.knockoff.hazard.flyingcars").color(NamedTextColor.BLUE),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        new BukkitRunnable() {
            int timer = 0;
            public void run() {
                if (timer == 12) { //This should last the jump boost's duration
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player, "minecraft:block.beacon.deactivate", 50, 1);
                    }
                    cancel();
                }
                if (knockoff.getInstance().GameManager == null) {
                    cancel();
                }
                spawnFlyingCar();
                timer++;
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 20);
    }

    private static void spawnFlyingCar() {
        Location validLoc = getValidSpot(false);
        Location loc = new Location(Bukkit.getWorld("world"), validLoc.getX(), knockoff.getInstance().mapdata.getCurrentYLength() + 13, validLoc.getZ());
        ItemStack item = new ItemStack(Material.CHARCOAL);
        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(new NamespacedKey("crystalized", CarsList.get(knockoff.getInstance().getRandomNumber(0, CarsList.size()))));
        item.setItemMeta(meta);

        //No idea how to launch a fireball from the server, this is the next best thing I guess
        ArmorStand tempentity = Bukkit.getWorld("world").spawn(loc, ArmorStand.class, entity -> {
            entity.setRotation(0, 90);
        });

        Fireball ball = tempentity.launchProjectile(Fireball.class, tempentity.getEyeLocation().getDirection());
        ball.getLocation().add(ball.getVelocity().normalize().multiply(1.05));
        ball.setYield(6);
        ball.setVisualFire(TriState.FALSE);

        ArmorStand car = Bukkit.getWorld("world").spawn(loc, ArmorStand.class, entity -> {
            entity.getEquipment().setHelmet(item);
            entity.setInvisible(true);
            ball.addPassenger(entity);
            entity.setGlowing(true);
        });
        tempentity.remove();

        new BukkitRunnable() {
            public void run() {
                if (ball.isDead()) {
                    car.remove();
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 1);
    }
}
