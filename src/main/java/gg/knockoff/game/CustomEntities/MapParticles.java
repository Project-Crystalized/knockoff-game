package gg.knockoff.game.CustomEntities;

import com.destroystokyo.paper.ParticleBuilder;
import gg.knockoff.game.GameManager;
import gg.knockoff.game.MapData;
import gg.knockoff.game.MapManager;
import gg.knockoff.game.knockoff;
import io.papermc.paper.entity.LookAnchor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static net.kyori.adventure.text.Component.text;
import static org.bukkit.Particle.DUST;

public class MapParticles {
    Location location;
    ArmorStand stand;
    public boolean isMoving = false;

    public MapParticles(Location loc) {
        location = loc;
        stand = loc.getWorld().spawn(loc, ArmorStand.class, entity -> {
            entity.setSmall(true);
            entity.getAttribute(Attribute.SCALE).setBaseValue(0.35);
            entity.customName(text("MapParticles"));
            entity.setDisabledSlots(EquipmentSlot.HAND);
            entity.setDisabledSlots(EquipmentSlot.OFF_HAND);
            entity.setDisabledSlots(EquipmentSlot.HEAD);

            entity.setCustomNameVisible(false);
            entity.setInvisible(true);
        });

        new BukkitRunnable() {
            MapData md = knockoff.getInstance().mapdata;
            public void run() {
                switch (GameManager.plannedDirection) {
                    case SOUTH -> {
                        stand.setVelocity(new Vector(0, 0.165, 0.2));
                    }
                    case WEST -> {
                        stand.setVelocity(new Vector(-0.2, 0.165, 0));
                    }
                    case EAST -> {
                        stand.setVelocity(new Vector(0.2, 0.165, 0));
                    }
                    default -> {
                        stand.setVelocity(new Vector(0, 0.165, 0));
                    }
                }

                if (isMoving) {
                    stand.lookAt(md.getCurrentMiddleXLength(), md.getCurrentMiddleYLength(), md.getCurrentMiddleZLength(), LookAnchor.EYES);
                    moveTowardsNewSection(new Location(Bukkit.getWorld("world"), md.getCurrentMiddleXLength(), md.getCurrentMiddleYLength(), md.getCurrentMiddleZLength()));
                    cancel();
                }
                if (knockoff.getInstance().GameManager == null) {
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 5);

        new BukkitRunnable() {
            public void run() {
                ParticleBuilder builder = new ParticleBuilder(DUST);
                builder.color(Color.PURPLE);
                builder.location(stand.getLocation());
                builder.count(3);
                builder.offset(0.1, 0.1, 0.1);
                builder.spawn();

                if (knockoff.getInstance().GameManager == null || stand.isDead()) {
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 1);
    }

    private void moveTowardsNewSection(Location newSection) {
        isMoving = true;
        new BukkitRunnable() {
            int x;
            int y;
            int z;
            public void run() {
                Location loc = stand.getLocation();

                int bx = (int) newSection.getX();
                int by = (int) newSection.getY();
                int bz = (int) newSection.getZ();
                int px = (int) loc.getX();
                int py = (int) loc.getY();
                int pz = (int) loc.getZ();

                x = (bx - px);
                y = (int) (by - py + 0.5);
                z = (bz - pz);

                try {
                    stand.setVelocity(new Vector(x, y, z).normalize().multiply(0.25));
                } catch (Exception ignored) {}

                if (MapManager.isInsideCurrentSection(loc) || knockoff.getInstance().GameManager == null || stand.isDead()) {
                    stand.remove();
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 2, 5);
    }
}
