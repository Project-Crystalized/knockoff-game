package gg.knockoff.game.hazards;

import com.fastasyncworldedit.core.Fawe;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import gg.knockoff.game.GameManager;
import gg.knockoff.game.MapData;
import gg.knockoff.game.knockoff;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
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
        nature, //TODO
        sixthElementHazard, //TODO
    }

    @Override
    public void start() {
        miniHazards type = figureOutMiniHazard();
        if (type == null) {
            knockoff.getInstance().getLogger().log(Level.SEVERE, "Elementals Hazard triggered but miniHazards type is null. Either this is not the correct map and/or the required block was not found in the current section.");
            return;
        }
        switch (type) {
            case eruption -> {eruption();}
            case sheerCold -> {sheerCold();}
            case blockBreaker -> {blockBreaker();}
            case howlingWind -> {howlingWind();}
            default -> {
                knockoff.getInstance().getLogger().log(Level.SEVERE, "unknown/unimplemented type: " + type);
            }
        }
    }

    private miniHazards figureOutMiniHazard() {
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
                if (b.getType().equals(Material.MAGMA_BLOCK)) {return miniHazards.eruption;}
                else if (b.getType().equals(Material.BLUE_ICE)) {return miniHazards.sheerCold;}
                else if (b.getType().equals(Material.SOUL_SOIL)) {return miniHazards.blockBreaker;}
                else if (b.getType().equals(Material.WHITE_STAINED_GLASS)) {return miniHazards.howlingWind;}
                else if (b.getType().equals(Material.MOSS_BLOCK)) {return miniHazards.nature;}
                else if (b.getType().equals(Material.AMETHYST_BLOCK)) {return miniHazards.sixthElementHazard;}
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }

        return null;
    }


    // Methods for different types of this hazard

    private void eruption() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                text("Elementals (Eruption)").color(NamedTextColor.DARK_RED),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        //TODO
    }


    private void sheerCold() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                text("Elementals (Sheer Cold)").color(NamedTextColor.AQUA),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, "minecraft:block.beacon.activate", 1, 1); //TODO placeholder sound
        }
        crystalsToIce(false);
        new BukkitRunnable() {
            int timer = 10 * 20;
            public void run() {
                crystalsToIce(true);
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

    private void crystalsToIce(boolean sounds) {
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
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
                    b.setType(Material.ICE);
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
                text("Elementals (Block Breaker)").color(NamedTextColor.GOLD),
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
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
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
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                text("Elementals (Howling Wind)").color(NamedTextColor.WHITE),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        //TODO
    }
}
