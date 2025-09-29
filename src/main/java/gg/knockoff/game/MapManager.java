package gg.knockoff.game;

import com.fastasyncworldedit.core.Fawe;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockState;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

//Used to be part of GameManager, separated into its own class for hazards rework needing to make this class public
public class MapManager {

    public static void CloneNewMapSection() {
        GameManager GameManager = knockoff.getInstance().GameManager;
        MapData md = knockoff.getInstance().mapdata;
        GameManager.LastSectionPlaceLocationX = GameManager.SectionPlaceLocationX;
        GameManager.LastSectionPlaceLocationY = GameManager.SectionPlaceLocationY;
        GameManager.LastSectionPlaceLocationZ = GameManager.SectionPlaceLocationZ;
        md.LastXLength = md.CurrentXLength;
        md.LastYLength = md.CurrentYLength;
        md.LastZLength = md.CurrentZLength;
        Bukkit.getServer().sendMessage(translatable("crystalized.game.knockoff.chat.movetosafety1").color(GOLD)
                .append(translatable("crystalized.game.knockoff.chat.movetosafety2").color(RED).decoration(TextDecoration.BOLD, true))
        );
        //CopyRandomMapSection();
        knockoff.getInstance().GameManager.mapMoving = true;

        //In the case the command is used instead of this being called naturally
        if (GameManager.plannedDirection.equals(gg.knockoff.game.GameManager.mapDirections.undecided)) {
            GameManager.decideMapDirection();
        }

        switch (GameManager.plannedDirection) {
            case gg.knockoff.game.GameManager.mapDirections.EAST:
                GameManager.SectionPlaceLocationX = GameManager.LastSectionPlaceLocationX + md.LastXLength;
                GameManager.SectionPlaceLocationZ = GameManager.LastSectionPlaceLocationZ;
                break;
            case gg.knockoff.game.GameManager.mapDirections.SOUTH:
                GameManager.SectionPlaceLocationX = GameManager.LastSectionPlaceLocationX;
                GameManager.SectionPlaceLocationZ = GameManager.LastSectionPlaceLocationZ + md.LastZLength;
                break;
            case gg.knockoff.game.GameManager.mapDirections.WEST:
                GameManager.SectionPlaceLocationX = GameManager.LastSectionPlaceLocationX - knockoff.getInstance().mapdata.CurrentXLength;
                GameManager.SectionPlaceLocationZ = GameManager.LastSectionPlaceLocationZ;
                break;
        }

        placeNewSection();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(Title.title(text(""), translatable("crystalized.game.knockoff.chat.movetosafety2").color(RED), Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(4), Duration.ofMillis(500))));
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, "minecraft:entity.illusioner.mirror_move", 50, 2);
            p.playSound(p, "minecraft:entity.illusioner.prepare_blindness", 50, 0.5F);
        }
        turnMapIntoCrystals();
        DecayMapSection();
    }

    public static void turnMapIntoCrystals() {
        List<Block> blockList = new ArrayList<>();
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        MapData md = knockoff.getInstance().mapdata;
        try (EditSession editSession = Fawe.instance().getWorldEdit().newEditSession(world)) {
            Region region = new CuboidRegion(
                    BlockVector3.at(
                            knockoff.getInstance().GameManager.LastSectionPlaceLocationX,
                            knockoff.getInstance().GameManager.LastSectionPlaceLocationY,
                            knockoff.getInstance().GameManager.LastSectionPlaceLocationZ
                    ),
                    BlockVector3.at(
                            knockoff.getInstance().GameManager.LastSectionPlaceLocationX + md.LastXLength -1,
                            knockoff.getInstance().GameManager.LastSectionPlaceLocationY + md.LastYLength -1, //Subtracting 1 to prevent a bug where section borders are caught within this
                            knockoff.getInstance().GameManager.LastSectionPlaceLocationZ + md.LastZLength -1
                    )
            );
            for (BlockVector3 bV3 : region) {
                Block b = new Location(Bukkit.getWorld("world"), bV3.x(), bV3.y(), bV3.z()).getBlock();
                if (!b.isEmpty()) {
                    blockList.add(b);
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }

        GameManager gm = knockoff.getInstance().GameManager;

        for (Block b : blockList) {
            gm.startBreakingCrystal(b, knockoff.getInstance().getRandomNumber(2 * 20, 8 * 20), knockoff.getInstance().getRandomNumber(3 * 20, 5 * 20), true);
        }
    }

    public static void DecayMapSection() {
        //WorldEdit/FAWE API documentation is ass, gl understanding this

        //TODO this code is shit but idk how to improve it well
        //Filling crystals with air, this has a delay compared to the previous BukkitRunnable
        //This is literally copy pasted code but with the material changed to AIR
        MapData md = knockoff.getInstance().mapdata;
        new BukkitRunnable() {
            int XPos = 0;

            @Override
            public void run() {
                if (knockoff.getInstance().GameManager == null) {cancel();}
                switch (GameManager.plannedDirection) {
                    case GameManager.mapDirections.EAST -> {
                        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
                        if ((GameManager.LastSectionPlaceLocationX + XPos) == (GameManager.LastSectionPlaceLocationX + md.LastXLength + 1)) {
                            finishDecay();
                            cancel();
                        } else {
                            try (EditSession editSession = Fawe.instance().getWorldEdit().newEditSession((com.sk89q.worldedit.world.World) world)) {
                                Region region = new CuboidRegion(
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + XPos,
                                                GameManager.LastSectionPlaceLocationY - 20,
                                                GameManager.LastSectionPlaceLocationZ
                                        ),
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + XPos - 5,
                                                GameManager.LastSectionPlaceLocationY + md.LastYLength,
                                                GameManager.LastSectionPlaceLocationZ + md.LastZLength
                                        )
                                );
                                //Mask mask = new BlockMask(editSession.getExtent(), new BaseBlock(BlockTypes.AIR));
                                ExistingBlockMask mask = new ExistingBlockMask(editSession.getExtent());
                                RandomPattern pat = new RandomPattern();
                                BlockState a = BukkitAdapter.adapt(Material.AIR.createBlockData());
                                pat.add(a, 1);
                                editSession.replaceBlocks(region, mask, pat);
                                editSession.flushQueue();
                            } catch (Exception e) {
                                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                                e.printStackTrace();
                            }
                            XPos++;
                        }
                    }
                    case GameManager.mapDirections.SOUTH -> {
                        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
                        if ((GameManager.LastSectionPlaceLocationZ + XPos) == (GameManager.LastSectionPlaceLocationZ + md.LastZLength + 1)) {
                            finishDecay();
                            cancel();
                        } else {
                            try (EditSession editSession = Fawe.instance().getWorldEdit().newEditSession((com.sk89q.worldedit.world.World) world)) {
                                Region region = new CuboidRegion(
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX,
                                                GameManager.LastSectionPlaceLocationY - 20,
                                                GameManager.LastSectionPlaceLocationZ + XPos
                                        ),
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + md.LastXLength,
                                                GameManager.LastSectionPlaceLocationY + md.LastYLength,
                                                GameManager.LastSectionPlaceLocationZ + XPos
                                        )
                                );
                                //Mask mask = new BlockMask(editSession.getExtent(), new BaseBlock(BlockTypes.AIR));
                                ExistingBlockMask mask = new ExistingBlockMask(editSession.getExtent());
                                RandomPattern pat = new RandomPattern();
                                BlockState a = BukkitAdapter.adapt(Material.AIR.createBlockData());
                                pat.add(a, 1);
                                editSession.replaceBlocks(region, mask, pat);
                                editSession.flushQueue();
                            } catch (Exception e) {
                                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                                e.printStackTrace();
                            }
                            XPos++; //cba renaming
                        }
                    }
                    case GameManager.mapDirections.WEST -> {
                        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
                        if ((GameManager.LastSectionPlaceLocationX + XPos) == (GameManager.LastSectionPlaceLocationX + md.LastXLength + 1)) {
                            finishDecay();
                            cancel();
                        } else {
                            try (EditSession editSession = Fawe.instance().getWorldEdit().newEditSession((com.sk89q.worldedit.world.World) world)) {
                                Region region = new CuboidRegion(
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + md.LastXLength - XPos,
                                                GameManager.LastSectionPlaceLocationY - 20,
                                                GameManager.LastSectionPlaceLocationZ
                                        ),
                                        BlockVector3.at(
                                                GameManager.LastSectionPlaceLocationX + md.LastXLength - XPos + 5,
                                                GameManager.LastSectionPlaceLocationY + md.LastYLength,
                                                GameManager.LastSectionPlaceLocationZ + md.LastZLength
                                        )
                                );
                                //Mask mask = new BlockMask(editSession.getExtent(), new BaseBlock(BlockTypes.AIR));
                                ExistingBlockMask mask = new ExistingBlockMask(editSession.getExtent());
                                RandomPattern pat = new RandomPattern();
                                BlockState a = BukkitAdapter.adapt(Material.AIR.createBlockData());
                                pat.add(a, 1);
                                editSession.replaceBlocks(region, mask, pat);
                                editSession.flushQueue();
                            } catch (Exception e) {
                                Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
                                e.printStackTrace();
                            }
                            XPos++;
                        }
                    }
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 8 * 20, 7);
    }

    private static void finishDecay() {
        GameManager.LastSectionPlaceLocationX = -1000;
        GameManager.LastSectionPlaceLocationY = 0;
        GameManager.LastSectionPlaceLocationZ = -1000;
        knockoff.getInstance().GameManager.mapMoving = false;
        GameManager.plannedDirection = GameManager.mapDirections.undecided;
    }

    //DEPRECATED
    //public static void CopyRandomMapSection() {
    //    knockoff.getInstance().mapdata.getrandommapsection();
    //}

    public static void placeNewSection() {
        //JsonArray data = knockoff.getInstance().mapdata.getCurrentsection();
        JsonElement sectionData = knockoff.getInstance().mapdata.getNewRandomSection();
        JsonObject sectionJson = sectionData.getAsJsonObject();
        JsonArray from = sectionJson.get("from").getAsJsonArray();
        JsonArray to = sectionJson.get("to").getAsJsonArray();
        World world = Bukkit.getWorld("world");

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            CuboidRegion region = new CuboidRegion(
                    BukkitAdapter.adapt(world),
                    BlockVector3.at(from.get(0).getAsInt(), from.get(1).getAsInt(), from.get(2).getAsInt()),
                    BlockVector3.at(to.get(0).getAsInt(), to.get(1).getAsInt(), to.get(2).getAsInt())
            );
            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                    BukkitAdapter.adapt(world), region, clipboard, region.getMinimumPoint()
            );
            Operations.complete(forwardExtentCopy);

            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(knockoff.getInstance().GameManager.SectionPlaceLocationX, knockoff.getInstance().GameManager.SectionPlaceLocationY, knockoff.getInstance().GameManager.SectionPlaceLocationZ))
                    .build();
            Operations.complete(operation);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }
        if (!knockoff.getInstance().DevMode) {
            //Could be optimised, this needs to use FAWE's API, but we're using commands instead since idk how the api works for this
            Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> {
                String a = sectionJson.get("remove_block").getAsString();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/world \"world\"");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos1 " + knockoff.getInstance().GameManager.SectionPlaceLocationX + "," + knockoff.getInstance().GameManager.SectionPlaceLocationY + "," + knockoff.getInstance().GameManager.SectionPlaceLocationZ);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/pos2 " + knockoff.getInstance().mapdata.getCurrentXLength() + "," + knockoff.getInstance().mapdata.getCurrentYLength() + "," + knockoff.getInstance().mapdata.getCurrentZLength());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/replace " + a + " air");
            }, 2);
        }
    }


    //For these 2 booleans, I dont think theres a better way of doing these
    //We could make a worldedit region and do shit with that, but I feel like making worldedit actions everytime this is called is stupid - Callum
    public static boolean isInsideCurrentSection(Location loc) {
        if (!(
                loc.getBlockY() > knockoff.getInstance().mapdata.getCurrentYLength() || loc.getBlockY() < (knockoff.getInstance().GameManager.SectionPlaceLocationY - 20)
                || loc.getBlockX() > knockoff.getInstance().mapdata.getCurrentXLength() || loc.getBlockX() < knockoff.getInstance().GameManager.SectionPlaceLocationX
                || loc.getBlockZ() > knockoff.getInstance().mapdata.getCurrentZLength() || loc.getBlockZ() < knockoff.getInstance().GameManager.SectionPlaceLocationZ
        )) {
            return true;
        }
        return false;
    }

    public static boolean isInsideDecayingSection(Location loc) {
        MapData md = knockoff.getInstance().mapdata;
        if (!(
                loc.getBlockY() > knockoff.getInstance().GameManager.LastSectionPlaceLocationY + md.LastYLength || loc.getBlockY() < (knockoff.getInstance().GameManager.LastSectionPlaceLocationY - 20)
                || loc.getBlockX() > knockoff.getInstance().GameManager.LastSectionPlaceLocationX + md.LastXLength || loc.getBlockX() < knockoff.getInstance().GameManager.LastSectionPlaceLocationX
                || loc.getBlockZ() > knockoff.getInstance().GameManager.LastSectionPlaceLocationZ + md.LastZLength || loc.getBlockZ() < knockoff.getInstance().GameManager.LastSectionPlaceLocationZ
        )) {
            return true;
        }
        return false;
    }
}
