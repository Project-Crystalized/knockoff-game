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
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
                if (timer == 0 || knockoff.getInstance().gameManager == null) {
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
            GameManager gm = knockoff.getInstance().gameManager;
            MapData md = knockoff.getInstance().mapdata;
            public void run() {
                if (knockoff.getInstance().gameManager == null || timer == 0) {
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
                if (isOver || knockoff.getInstance().gameManager == null) {
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
        //More ideas for effects if invisability and levitation, but levitation could end up being more harming
        //While invis could be a little bit over powered, so for now slow falling was added.
        //Made them last longer in seconds, otherwise it just runs out almost imiditely
        jumpBoost(PotionEffectType.JUMP_BOOST, "Jump Boost", 4, 20 * 30),
        speed(PotionEffectType.SPEED, "Speed", 4, 20 * 40),
        strength(PotionEffectType.STRENGTH, "Strength", 3, 20 * 70),
        slowFalling(PotionEffectType.SLOW_FALLING, "Slow Falling", 0, 20 * 60),
        ;

        final PotionEffectType ef;
        final String name;
        //Not sure if amplification limit was the plan, but I think it is a good idea to not go crazy high
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
        //Added a blcocklist empty check, for safety
        if (blockList.isEmpty()) {
            //will show a warning if no torchflowers were found
            knockoff.getInstance().getLogger().warning("No torch flowers were found to create a coruption zone");
            return;
        }

        new BukkitRunnable() {
            int timer = 0;
            ArmorStand aoeEntity1;
            corruptionZoneEffects effect = null;
            public void run() {
                switch (timer) {
                    case 1 -> {


                        Collections.shuffle(blockList);
                        //made sure blockList.size() is - 1, as arrays and lists start at 0 index, so this will be the last valid index
                        //From java doc: IndexOutOfBoundsException – if the index is out of range (index < 0 || index >= size())
                        //Must not be equal or bigger to size, or smaller than zero.
                        aoeEntity1 = Bukkit.getWorld("world").spawn(blockList.get(knockoff.getInstance().
                                getRandomNumber(0, blockList.size() - 1)).getLocation(), ArmorStand.class, entity -> {
                            //As armor stand has taken on the entity replaced aoeEntity so it should be set correctly here
                            entity.setGravity(false);
                            //Made sure to get scale first and add a null check so if scale is null nothing would happen
                            AttributeInstance scale = entity.getAttribute(Attribute.SCALE);
                            if(scale != null){
                                scale.setBaseValue(0.1);
                            }
                            //Made sure it is invisible
                            entity.setInvisible(true);
                            //makes sure that arrmor stand is a marker for a small collision box
                            entity.setMarker(true);
                            entity.setGlowing(true);

                        });

                    }
                    //Changed so it starts at 1 sec, instead of 2 sec, letting count down fully go through to 5 sec
                    //No longer overlaps
                    case 20 * 1, 20 * 6 -> {
                        //Moved the effect desion here, as mite said
                        // so that it changes after giving, so it could either stack up. Or amplification grows if same one
                        //decide effect
                        //added extra numbers for 4th effect
                        switch (knockoff.getInstance().getRandomNumber(1, 8)) {
                            case 1, 5 -> {effect = corruptionZoneEffects.jumpBoost;}
                            case 2, 6 -> {effect = corruptionZoneEffects.speed;}
                            case 3, 7 -> {effect = corruptionZoneEffects.strength;}
                            case 4, 8 -> {effect = corruptionZoneEffects.slowFalling;}
                        }
                        summonAOE(aoeEntity1.getLocation(), effect);
                    }
                    case 20 * 12 -> {
                        aoeEntity1.remove();
                        cancel();
                    }
                }

                if (aoeEntity1 != null) {
                    //the location of the particles with a slight offset.
                    Location particleLoc = aoeEntity1.getLocation().clone().add(0.5, 1.0, 0.5);
                    //aded a circle particles around with the same as effect giving radius so it is easier to know where to stand
                    double radius = 5.0;
                    //It will spawn 32 paritcle around
                    //Similiar to dragon arrow circle
                    for (int i = 0; i < 32; i++) {
                        //Calculating the angle where each particle must be
                        double angle = 2 * Math.PI * i / 32;
                        //Calculating the x and z location
                        double x = Math.cos(angle) * radius;
                        double z = Math.sin(angle) * radius;
                        //Adds the circle offset location
                        Location circleLoc = particleLoc.clone().add(x, 0, z);
                        //Spawns the aqua dust particle, at it's respective location in a circle
                        particleLoc.getWorld().spawnParticle(Particle.DUST, circleLoc,
                                1,
                                0,
                                0,
                                0,
                                0,
                                new Particle.DustOptions(Color.AQUA, 2.0f)
                        );
                    }
                    // Soul fire Particles spawn around the torch flower, with slight offset
                    //Inside that circle
                    //Reduced the amount as mite asked
                    particleLoc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc,
                            2,
                            2.5,
                            0.5,
                            2.5,
                            0.01
                    );
                    // The blue trial particles that mite suggested
                    particleLoc.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS, particleLoc,
                            1,
                            2.5,
                            0.5,
                            2.5,
                            0.01
                    );
                    //This spawns the locator particles in the air, going up
                    for (double y = 1; y <= 20; y += 0.5) {
                        //copies where the rest of particles spawn and adds y each time
                        //By the end should be + 20 original height, creating a pillar of dust particles
                        Location locatorLoc = particleLoc.clone().add(0, y, 0);
                        //spawns one aqua dust particle per loop, creating 38 total, attracting player attention, so it is easier to find
                        particleLoc.getWorld().spawnParticle(Particle.DUST, locatorLoc,
                                1,
                                0,
                                0,
                                0,
                                0,
                                new Particle.DustOptions(Color.AQUA, 2.0f)
                        );
                    }
                }

                timer++;
            }
        }.runTaskTimer(knockoff.getInstance(), 1,1);
    }

    private void summonAOE(Location loc, corruptionZoneEffects effect) {
        //Added height for the text location so it easier to see aboce tghe flower
        Location textLoc = loc.clone().add(0.5, 1.5, 0.5);
        TextDisplay text = loc.getWorld().spawn(textLoc, TextDisplay.class, entity -> {
            entity.setBillboard(Display.Billboard.CENTER);
            entity.text(text(" "));
        });
        new BukkitRunnable() {
            //DONE: Need to make sure that text can't overlap with the next text effect. So will rewrite this part a bit

            int timer = 20 * 5;
            public void run() {
                //changed the colour to yellow so it is easier to see
                text.text(text(effect.name + " in: " + timer/20).color(NamedTextColor.YELLOW));
                timer--;
                if (knockoff.getInstance().gameManager == null) {
                    text.remove();
                    cancel();
                    //made sure it returns as well.
                    return;
                }
                if (timer == 0) {
                    //Made the whole logic work with online players, and so that the player needs to be withing 5 blocks height wise as well
                    //As the previous height was allowing players to be much higher
                    for (Player player : Bukkit.getOnlinePlayers()) {

                        //For extra safety makes sure that players in other worlds are ignored
                        //Though it should not happen
                        if (!player.getWorld().equals(loc.getWorld())) {
                            continue;
                        }
                        //Makes sure that players who are in spectator are ignored
                        if(player.getGameMode() == GameMode.SPECTATOR){
                            continue;
                        }
                        //The radius in which the effect will be applied
                        double radius = 5.0;

                        //Only affects players withing the 5 block radius of the corruption zone
                        //distance squared, should be a bit better than just regular distanse as regular distanse checker uses Math.sqrt
                        //Which is not recomended to repetedly call for performance, and can be unrelyable and return NAN if it overflows.
                        //But alternatively can be ".distance(loc) <= radius (which is 5)
                        if (player.getLocation().distanceSquared(loc) <= radius * radius) {
                            //Gets the current effect
                            PotionEffect currentEffect = player.getPotionEffect(effect.ef);
                            //sets intial amplifier to zero
                            int amplifier = 0;
                            //If player has the effect increases the amplifier withtin the given limit
                            if (currentEffect != null) {
                                //Math mini selects the smaller number, meaning that if amplifer exides the ampLimit
                                //It will set the amplifier to the limit
                                amplifier = Math.min(currentEffect.getAmplifier() + 1, effect.ampLimit);
                            }
                            //adds the potion effect with the calculated amplifier
                            player.addPotionEffect(new PotionEffect(effect.ef, effect.ticks, amplifier, false, true, true));
                        }
                    }

                    /* Old logic, with crazy with big height range
                    for (Entity e : text.getNearbyEntities(5, 80, 5)) {
                        //Replaced living entity with a player as it should happen for player only
                        if (e instanceof Player player) {
                            int amp;
                            try {
                                amp = player.getPotionEffect(effect.ef).getAmplifier();
                            } catch (NullPointerException ex) {
                                amp = -1;
                            }

                            player.addPotionEffect(new PotionEffect(effect.ef, effect.ticks, amp + 1, false, true, true));
                        }
                    }*/
                    text.remove();
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 2, 1);
    }
}
