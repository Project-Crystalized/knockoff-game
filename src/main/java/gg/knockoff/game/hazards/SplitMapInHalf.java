package gg.knockoff.game.hazards;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import gg.knockoff.game.GameManager;
import gg.knockoff.game.MapData;
import gg.knockoff.game.knockoff;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class SplitMapInHalf extends hazard {

    public SplitMapInHalf(String name) {
        super(name);
    }

    @Override
    public void start() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                translatable("crystalized.game.knockoff.hazard.splitmapinhalf").color(NamedTextColor.LIGHT_PURPLE),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        List<Block> blockList = new ArrayList<>();
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
            MapData md = knockoff.getInstance().mapdata;
            int X1;
            int X2;
            int Z1;
            int Z2;
            int corruptionSize;
            int offset = knockoff.getInstance().getRandomNumber(-5, 5);

            switch (knockoff.getInstance().getRandomNumber(1, 10)) {
                case 2, 4, 6, 8, 10-> {
                    //X axis
                    corruptionSize = 4; //md.CurrentXLength / 8;
                    X1 = md.getCurrentMiddleXLength() - corruptionSize + offset;
                    X2 = md.getCurrentMiddleXLength() + corruptionSize + offset;
                    Z1 = GameManager.SectionPlaceLocationZ;
                    Z2 = md.getCurrentZLength();
                }
                default -> {
                    //Z axis
                    corruptionSize = 4; //md.CurrentZLength / 8;
                    X1 = GameManager.SectionPlaceLocationX;
                    X2 = md.getCurrentXLength();
                    Z1 = md.getCurrentMiddleZLength() - corruptionSize + offset;
                    Z2 = md.getCurrentMiddleZLength() + corruptionSize + offset;
                }
            }
            CuboidRegion region = new CuboidRegion(world,
                    BlockVector3.at(
                            X1,
                            GameManager.SectionPlaceLocationY,
                            Z1),
                    BlockVector3.at(
                            X2,
                            md.getCurrentYLength(),
                            Z2)
            );
            for (BlockVector3 bV3 : region) {
                Block b = new Location(Bukkit.getWorld("world"), bV3.x(), bV3.y(), bV3.z()).getBlock();
                if (!b.isEmpty()) {
                    blockList.add(b);
                }
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p, "minecraft:entity.wither.spawn", 1, 2);
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[HAZARDSMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }
        for (Block b : blockList) {
            GameManager.startBreakingCrystal(b, knockoff.getInstance().getRandomNumber(1, 18), knockoff.getInstance().getRandomNumber(13, 20), true);
        }
    }
}
