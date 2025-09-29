package gg.knockoff.game.hazards;

import com.fastasyncworldedit.core.Fawe;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import gg.knockoff.game.*;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Breeze;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class Exclusive_TrialChamber extends hazard {

    public Exclusive_TrialChamber(String name) {
        super(name);
    }

    @Override
    public void start() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                translatable("crystalized.game.knockoff.hazard.TrialChamber").color(NamedTextColor.AQUA),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        Location loc;
        List<Block> blockList = new ArrayList<>();

        //Check for Trial Spawners beore picking random spot
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        try (EditSession editSession = Fawe.instance().getWorldEdit().newEditSession(world)) {
            MapData md = knockoff.getInstance().mapdata;
            Region region = new CuboidRegion(
                    BlockVector3.at(
                            GameManager.SectionPlaceLocationX,
                            GameManager.SectionPlaceLocationY,
                            GameManager.SectionPlaceLocationZ
                    ),
                    BlockVector3.at(
                            GameManager.SectionPlaceLocationX + md.CurrentXLength,
                            GameManager.SectionPlaceLocationY + md.CurrentYLength,
                            GameManager.SectionPlaceLocationZ + md.CurrentZLength
                    )
            );
            for (BlockVector3 bV3 : region) {
                Block b = new Location(Bukkit.getWorld("world"), bV3.x(), bV3.y(), bV3.z()).getBlock();
                if (b.getType().equals(Material.TRIAL_SPAWNER)) {
                    blockList.add(b);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }
        if (blockList.isEmpty()) {
            loc = getValidSpot(true);
        } else {
            loc = blockList.get(knockoff.getInstance().getRandomNumber(0, blockList.size())).getLocation();
        }

        spawnTrialChamber(loc);
    }

    private static void spawnTrialChamber(Location loc) {
        loc.getBlock().setType(Material.TRIAL_SPAWNER);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(loc, "minecraft:block.trial_spawner.ominous_activate", 1, 1);
            p.playSound(loc, "minecraft:block.trial_spawner.spawn_mob", 50, 1);
        }
        List<Breeze> entitiesSpawned = new ArrayList<>();

        new BukkitRunnable() {
            int timer = knockoff.getInstance().getRandomNumber(2, 5);
            Location spawnLoc = loc.clone().add(0, 1, 0);

            public void run() {
                if (knockoff.getInstance().GameManager == null) {
                    cancel();
                }
                if (timer == 0) {
                    Breeze oneWithKey = entitiesSpawned.get(knockoff.getInstance().getRandomNumber(0, entitiesSpawned.size()));
                    oneWithKey.getAttribute(Attribute.MAX_HEALTH).setBaseValue(4);
                    oneWithKey.getAttribute(Attribute.SCALE).setBaseValue(1.5);
                    oneWithKey.setHealth(4);
                    cancel();
                }
                spawnLoc.getWorld().spawn(spawnLoc, Breeze.class, entity-> {
                    entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(2);
                    entity.setCustomNameVisible(true);
                    entitiesSpawned.add(entity);

                });
                timer--;
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 1);

        //For entitiesSpawned
        new BukkitRunnable() {
            public void run() {
                if (knockoff.getInstance().GameManager == null || entitiesSpawned.isEmpty()) {
                    cancel();
                }
                try {
                    for (Breeze b : entitiesSpawned) {
                        if (knockoff.getInstance().GameManager == null) {
                            b.remove();
                        }
                        int maxhealth = (int) b.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
                        int health = (int) b.getHealth();
                        b.customName(text("\uE11A" + "\uE11B".repeat(health) + "\uE11C".repeat(maxhealth - health) + "\uE11D"));
                        if (health == 0) {
                            b.damage(20); //should kill
                        }
                        if (b.isDead()) {
                            if (maxhealth == 4.0) {
                                Location keyLoc = loc.clone().add(0, 1, 0);
                                KnockoffItem.DropPowerup(b.getLocation(), "TrialChamberHazardKey");
                            }
                            entitiesSpawned.remove(b);
                        }
                    }
                } catch (Exception ex) {
                    //above causes a ConcurrentModificationException, no idea how to sort it and there isn't a better way of doing this
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 1);
    }
}
