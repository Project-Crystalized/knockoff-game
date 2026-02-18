package gg.knockoff.game;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static net.kyori.adventure.text.Component.text;

public class CrystalBlocks implements Listener {

    @EventHandler
    public void WhenCrystalBlockPlaced(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        Block b = event.getBlock();
        PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
        ItemStack itemUsed;

        if (!(MapManager.isInsideCurrentSection(b.getLocation()) || MapManager.isInsideDecayingSection(b.getLocation()))) {
            event.setCancelled(true);
            p.sendMessage(text("[!] You cannot place blocks outside the map's borders!").color(NamedTextColor.RED));
            return;
        }

        itemUsed = event.getItemInHand();
        Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> {
            event.getItemInHand().setAmount(64);
        }, 2);

        //may cause errors with the 2nd check if you place a vanilla amethyst block with nothing, not my problem as that wont happen without the player being in creative
        if (itemUsed.hasItemMeta() && itemUsed.getItemMeta().hasItemModel()) {
            if (!itemUsed.getPersistentDataContainer().has(new NamespacedKey("knockoff", "iscrystal"))) {
                return;
            }
            ItemMeta meta = itemUsed.getItemMeta();
            NamespacedKey itemModel = meta.getItemModel();

            //set the material
            switch (itemModel.getKey()) {
                case "block/nexus/blue", "block/nexus/cyan", "block/nexus/green", "block/nexus/lemon" -> {
                    b.setType(Material.WHITE_GLAZED_TERRACOTTA);
                }
                case "block/nexus/lime", "block/nexus/magenta", "block/nexus/orange", "block/nexus/peach" -> {
                    b.setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                }
                case "block/nexus/purple", "block/nexus/white", "block/nexus/yellow", "block/nexus/red" -> {
                    b.setType(Material.GRAY_GLAZED_TERRACOTTA);
                }
                case "block/nexus/weak", "block/nexus/strong" -> {
                    b.setType(Material.BLACK_GLAZED_TERRACOTTA);
                }
            }
            Directional dir = (Directional) b.getBlockData();

            //set direction to match the item model's model
            switch (itemModel.getKey()) {
                case "block/nexus/blue", "block/nexus/lime", "block/nexus/purple", "block/nexus/weak" -> {
                    dir.setFacing(BlockFace.EAST);
                }
                case "block/nexus/cyan", "block/nexus/magenta", "block/nexus/red", "block/nexus/strong" -> {
                    dir.setFacing(BlockFace.NORTH);
                }
                case "block/nexus/green", "block/nexus/orange", "block/nexus/white" -> {
                    dir.setFacing(BlockFace.SOUTH);
                }
                case "block/nexus/lemon", "block/nexus/peach", "block/nexus/yellow" -> {
                    dir.setFacing(BlockFace.WEST);
                }
            }

            b.setBlockData(dir);
            b.getState().update();
            pd.blocksplaced++;
        }

        GameManager gm = knockoff.getInstance().GameManager;
        if (gm.showdownModeStarted) {
            gm.startBreakingCrystal(b, knockoff.getInstance().getRandomNumber(3 * 20, 15 * 20), knockoff.getInstance().getRandomNumber(20, 8 * 20), false);
        }
    }

    @EventHandler
    public void PlayerPunchBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = player.getTargetBlock(null ,5);
        if (knockoff.getInstance().GameManager != null && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            switch (block.getType()) {
                case WHITE_GLAZED_TERRACOTTA, GRAY_GLAZED_TERRACOTTA, LIGHT_GRAY_GLAZED_TERRACOTTA, BLACK_GLAZED_TERRACOTTA,
                     AMETHYST_BLOCK, CUT_COPPER_SLAB, CUT_COPPER_STAIRS, PINK_STAINED_GLASS, PINK_STAINED_GLASS_PANE,
                     PINK_CARPET, FROSTED_ICE
                        -> {
                    if (block.getType().equals(Material.FROSTED_ICE)) {
                        block.setType(Material.AIR);
                    } else {
                        block.breakNaturally(true);
                    }
                    PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                    pd.blocksbroken++;
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e) {
        e.setCancelled(true);
    }
}