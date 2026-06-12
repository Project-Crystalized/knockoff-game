package gg.knockoff.game.hazards;

import com.fastasyncworldedit.core.Fawe;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import gg.knockoff.game.GameManager;
import gg.knockoff.game.MapData;
import gg.knockoff.game.knockoff;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class Exclusive_Elementals extends hazard {

    public Exclusive_Elementals(String name) {
        super(name);
    }

    enum miniHazards{
        eruption,
        sheerCold,
        blockBreaker,
        howlingWind,
        corruptionZone, //TODO random potion effects spawn on torchflowers above waxed weather copper
        sixthElementHazard, //TODO
    }

    private boolean isOver = false; //used by howlingWind

    @Override
    public void start() {
        miniHazards type = figureOutMiniHazard();
        if (type == null) {
            knockoff.getInstance().getLogger().log(Level.SEVERE, "Elements Hazard triggered but miniHazards type is null. Either this is not the correct map and/or the required block was not found in the current section.");
            return;
        }
        switch (type) {
            case eruption -> {eruption();}
            case sheerCold -> {sheerCold();}
            case blockBreaker -> {blockBreaker();}
            case howlingWind -> {howlingWind();}
            case corruptionZone -> {corruptionZone();}
            default -> {
                knockoff.getInstance().getLogger().log(Level.SEVERE, "unknown/unimplemented type: " + type);
            }
        }
    }

    private miniHazards figureOutMiniHazard() {
        World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
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
                if (b.getType().equals(Material.MAGMA_BLOCK)) {return miniHazards.eruption;}
                else if (b.getType().equals(Material.BLUE_ICE)) {return miniHazards.sheerCold;}
                else if (b.getType().equals(Material.SOUL_SOIL)) {return miniHazards.blockBreaker;}
                else if (b.getType().equals(Material.WHITE_STAINED_GLASS)) {return miniHazards.howlingWind;}
                else if (b.getType().equals(Material.TORCHFLOWER)) {return miniHazards.corruptionZone;}
                else if (b.getType().equals(Material.OBSIDIAN)) {return miniHazards.sixthElementHazard;}
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }

        return null;
    }


    // Methods for different types of this hazard

    private void eruption() {
        List<Block> blockList = new ArrayList<>();
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                text("Elements (Eruption)").color(NamedTextColor.DARK_RED),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );

        //get blocks to spawn eurptions on
        World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        try (EditSession editSession = Fawe.instance().getWorldEdit().newEditSession(world)) {
            MapData md = knockoff.getInstance().mapdata;
            Region region = new CuboidRegion(
                    BlockVector3.at(GameManager.SectionPlaceLocationX, GameManager.SectionPlaceLocationY, GameManager.SectionPlaceLocationZ),
                    BlockVector3.at(GameManager.SectionPlaceLocationX + md.CurrentXLength, GameManager.SectionPlaceLocationY + md.CurrentYLength, GameManager.SectionPlaceLocationZ + md.CurrentZLength)
            );
            for (BlockVector3 bV3 : region) {
                Block b = new Location(Bukkit.getWorld("world"), bV3.x(), bV3.y(), bV3.z()).getBlock();
                if (b.getType().equals(Material.RESIN_BRICKS)) {
                    blockList.add(b);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }

        if (blockList.isEmpty()) {
            knockoff.getInstance().getLogger().log(Level.SEVERE, "No Resin Bricks on map for Elementals (Eurption) hazard.");
            return;
        }

        new BukkitRunnable() {
            int timer = knockoff.getInstance().getRandomNumber(3, 6);
            public void run() {
                if (timer == 0 || knockoff.getInstance().GameManager == null) {
                    cancel();
                }

                Collections.shuffle(blockList);
                Location loc = blockList.getFirst().getLocation().clone().add(0.5, 2, 0.5);
                Bukkit.getWorld("world").spawn(loc, Snowball.class, entity -> {
                    entity.setVelocity(new Vector(
                            knockoff.getInstance().getRandomNumber(-0.3D, 0.3D),
                            knockoff.getInstance().getRandomNumber(0.45D, 0.6D),
                            knockoff.getInstance().getRandomNumber(-0.3D, 0.3D)
                    ));
                    entity.setCustomNameVisible(false);
                    entity.customName(text("magma"));
                    entity.setItem(new ItemStack(Material.FIRE_CHARGE));
                });
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(loc, "minecraft:item.bucket.empty_lava", 2, 1); //TODO temporary sound
                }

                timer--;
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 30);
    }


    private void sheerCold() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                text("Elements (Sheer Cold)").color(NamedTextColor.AQUA),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, "minecraft:block.beacon.activate", 1, 1); //TODO placeholder sound
        }
        crystalsToIce(false);
        sheerCold_setCrystalCMD(1);
        new BukkitRunnable() {
            int timer = 10 * 20;
            public void run() {
                crystalsToIce(true);
                timer--;
                if (timer == 0) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        sheerCold_setCrystalCMD(0);
                        p.playSound(p, "minecraft:block.beacon.deactivate", 1, 1); //TODO placeholder sound
                    }
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 1);
    }

    private void sheerCold_setCrystalCMD(float i) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerInventory inv = p.getInventory();
            for (ItemStack item : inv) {
                if (item != null) {
                    if (item.getPersistentDataContainer().has(new NamespacedKey("knockoff", "iscrystal"))) {
                        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addFloat(i).build());
                    }
                }
            }
        }
    }

    private void crystalsToIce(boolean sounds) {
        World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        try (EditSession editSession = Fawe.instance().getWorldEdit().newEditSession(world)) {
            MapData md = knockoff.getInstance().mapdata;
            Region region = new CuboidRegion(
                    BlockVector3.at(GameManager.SectionPlaceLocationX, GameManager.SectionPlaceLocationY, GameManager.SectionPlaceLocationZ),
                    BlockVector3.at(GameManager.SectionPlaceLocationX + md.CurrentXLength, GameManager.SectionPlaceLocationY + md.CurrentYLength, GameManager.SectionPlaceLocationZ + md.CurrentZLength)
            );
            for (BlockVector3 bV3 : region) {
                Block b = new Location(Bukkit.getWorld("world"), bV3.x(), bV3.y(), bV3.z()).getBlock();
                if (
                        b.getType().equals(Material.WHITE_GLAZED_TERRACOTTA) ||
                                b.getType().equals(Material.LIGHT_GRAY_GLAZED_TERRACOTTA) ||
                                b.getType().equals(Material.GRAY_GLAZED_TERRACOTTA) ||
                                b.getType().equals(Material.BLACK_GLAZED_TERRACOTTA)
                ) {
                    b.setType(Material.FROSTED_ICE);
                    if (sounds) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.playSound(p, "minecraft:entity.generic.swim", 1, 1); //TODO temporary
                        }
                    }
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }
    }


    private void blockBreaker() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                text("Elements (Block Breaker)").color(GOLD),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, "minecraft:block.beacon.activate", 1, 1); //TODO placeholder sound
        }
        new BukkitRunnable() {
            int timer = 10 * 20;
            public void run() {
                blockBreakerEffect();
                timer--;
                if (timer == 0) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p, "minecraft:block.beacon.deactivate", 1, 1); //TODO placeholder sound
                    }
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 1);
    }

    private void blockBreakerEffect() {
        World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        try (EditSession editSession = Fawe.instance().getWorldEdit().newEditSession(world)) {
            MapData md = knockoff.getInstance().mapdata;
            Region region = new CuboidRegion(
                    BlockVector3.at(GameManager.SectionPlaceLocationX, GameManager.SectionPlaceLocationY, GameManager.SectionPlaceLocationZ),
                    BlockVector3.at(GameManager.SectionPlaceLocationX + md.CurrentXLength, GameManager.SectionPlaceLocationY + md.CurrentYLength, GameManager.SectionPlaceLocationZ + md.CurrentZLength)
            );
            for (BlockVector3 bV3 : region) {
                Block b = new Location(Bukkit.getWorld("world"), bV3.x(), bV3.y(), bV3.z()).getBlock();
                if (
                        b.getType().equals(Material.WHITE_GLAZED_TERRACOTTA) ||
                                b.getType().equals(Material.LIGHT_GRAY_GLAZED_TERRACOTTA) ||
                                b.getType().equals(Material.GRAY_GLAZED_TERRACOTTA) ||
                                b.getType().equals(Material.BLACK_GLAZED_TERRACOTTA)
                ) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p, "minecraft:block.rooted_dirt.place", 1, 1); //TODO temporary
                    }
                    b.setType(Material.ROOTED_DIRT);
                    GameManager.startBreakingCrystal(b, 1, 30, false);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }
    }


    private void howlingWind() {
        isOver = false;
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                text("Elements (Howling Wind)").color(NamedTextColor.WHITE),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        howlingWindDirections dir;

        switch (knockoff.getInstance().getRandomNumber(1, 8)) {
            case 1, 5 -> {dir = howlingWindDirections.NORTH;}
            case 2, 6 -> {dir = howlingWindDirections.EAST;}
            case 3, 7 -> {dir = howlingWindDirections.SOUTH;}
            default -> {dir = howlingWindDirections.WEST;}
        }

        // (breeze) wind charges and main loop
        new BukkitRunnable() {
            int timer = 10 * 20;
            GameManager gm = knockoff.getInstance().GameManager;
            MapData md = knockoff.getInstance().mapdata;
            public void run() {
                if (knockoff.getInstance().GameManager == null || timer == 0) {
                    isOver = true;
                    cancel();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p, "minecraft:block.conduit.deactivate", 1, 1);
                    }
                }

                // spawn (breeze) wind charges in dir
                WindCharge w = Bukkit.getWorld("world").spawn(
                        new Location(Bukkit.getWorld("world"),
                                knockoff.getInstance().getRandomNumber(gm.SectionPlaceLocationX - 15, md.getCurrentXLength() + 15),
                                knockoff.getInstance().getRandomNumber(gm.SectionPlaceLocationY + 5, md.getCurrentYLength() - 5),
                                knockoff.getInstance().getRandomNumber(gm.SectionPlaceLocationZ - 15, md.getCurrentZLength() + 15)
                        ),
                        WindCharge.class
                );
                BreezeWindCharge bw = Bukkit.getWorld("world").spawn(
                        new Location(Bukkit.getWorld("world"),
                                knockoff.getInstance().getRandomNumber(gm.SectionPlaceLocationX - 15, md.getCurrentXLength() + 15),
                                knockoff.getInstance().getRandomNumber(gm.SectionPlaceLocationY + 5, md.getCurrentYLength() - 5),
                                knockoff.getInstance().getRandomNumber(gm.SectionPlaceLocationZ - 15, md.getCurrentZLength() + 15)
                        ),
                        BreezeWindCharge.class
                );
                w.setVelocity(dir.dir);
                bw.setVelocity(dir.dir);
                timer--;
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 1);

        //player effects
        new BukkitRunnable() {
            public void run() {
                if (isOver || knockoff.getInstance().GameManager == null) {
                    cancel();
                }

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.setVelocity(p.getVelocity().add(dir.entity_dir));
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 1, 4);

    }

    enum howlingWindDirections{
        NORTH(new Vector(0, 0, -1), new Vector(0, 0, -0.05)),
        EAST(new Vector(1, 0, 0), new Vector(0.05, 0, 0)),
        SOUTH(new Vector(0, 0, 1), new Vector(0, 0, 0.05)),
        WEST(new Vector(-1, 0, 0), new Vector(-0.05, 0, 0)),
        ;

        Vector dir;
        Vector entity_dir;
        howlingWindDirections(Vector dir, Vector entity_dir) {
            this.dir = dir;
            this.entity_dir = entity_dir;
        }
    }


    enum corruptionZoneEffects{
        jumpBoost(PotionEffectType.JUMP_BOOST, "Jump Boost", 4, 20 * 8),
        speed(PotionEffectType.SPEED, "Speed", 4, 20 * 4),
        strength(PotionEffectType.STRENGTH, "Strength", 3, 20 * 8),
        ;

        final PotionEffectType ef;
        final String name;
        final int ampLimit;
        final int ticks;
        corruptionZoneEffects(PotionEffectType ef, String name, int ampLimit, int ticks) {
            this.ef = ef;
            this.name = name;
            this.ampLimit = ampLimit;
            this.ticks = ticks;
        }
    }

    private void corruptionZone() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                text("Elements (Corruption Zone)").color(NamedTextColor.GREEN),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );

        List<Block> blockList = new ArrayList<>();
        World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        try (EditSession editSession = Fawe.instance().getWorldEdit().newEditSession(world)) {
            MapData md = knockoff.getInstance().mapdata;
            Region region = new CuboidRegion(
                    BlockVector3.at(GameManager.SectionPlaceLocationX, GameManager.SectionPlaceLocationY, GameManager.SectionPlaceLocationZ),
                    BlockVector3.at(GameManager.SectionPlaceLocationX + md.CurrentXLength, GameManager.SectionPlaceLocationY + md.CurrentYLength, GameManager.SectionPlaceLocationZ + md.CurrentZLength)
            );
            for (BlockVector3 bV3 : region) {
                Block b = new Location(Bukkit.getWorld("world"), bV3.x(), bV3.y(), bV3.z()).getBlock();
                if (b.getType().equals(Material.TORCHFLOWER)) {
                    blockList.add(b);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }

        new BukkitRunnable() {
            int timer = 0;
            ArmorStand aoeEntity1;
            corruptionZoneEffects effect = null;
            public void run() {
                switch (timer) {
                    case 1 -> {
                        //decide effect
                        switch (knockoff.getInstance().getRandomNumber(1, 6)) {
                            case 1, 4 -> {effect = corruptionZoneEffects.jumpBoost;}
                            case 2, 5 -> {effect = corruptionZoneEffects.speed;}
                            case 3, 6 -> {effect = corruptionZoneEffects.strength;}
                        }

                        Collections.shuffle(blockList);
                        aoeEntity1 = Bukkit.getWorld("world").spawn(blockList.get(knockoff.getInstance().getRandomNumber(0, blockList.size())).getLocation(), ArmorStand.class, entity -> {
                            aoeEntity1.setGravity(false);
                            aoeEntity1.getAttribute(Attribute.SCALE).setBaseValue(0.1);
                            aoeEntity1.setGlowing(true);
                        });

                    }
                    case 20 * 2, 20 * 6 -> {
                        summonAOE(aoeEntity1.getLocation(), effect);
                    }
                    case 20 * 12 -> {
                        aoeEntity1.remove();
                        cancel();
                    }
                }
                timer++;
            }
        }.runTaskTimer(knockoff.getInstance(), 1,1);
    }

    private void summonAOE(Location loc, corruptionZoneEffects effect) {
        TextDisplay text = loc.getWorld().spawn(loc, TextDisplay.class, entity -> {
            entity.setBillboard(Display.Billboard.CENTER);
            entity.text(text(" "));
        });
        new BukkitRunnable() {
            int timer = 20 * 5;
            public void run() {
                text.text(text(effect.name + " in: " + timer/20));
                timer--;
                if (knockoff.getInstance().GameManager == null) {
                    text.remove();
                    cancel();
                }
                if (timer == 0) {
                    for (Entity e : text.getNearbyEntities(5, 80, 5)) {
                        if (e instanceof LivingEntity le) {
                            int amp;
                            try {
                                amp = le.getPotionEffect(effect.ef).getAmplifier();
                            } catch (NullPointerException ex) {
                                amp = -1;
                            }

                            le.addPotionEffect(new PotionEffect(effect.ef, effect.ticks, amp + 1, false, true, true));
                        }
                    }
                    text.remove();
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 2, 1);
    }
}
