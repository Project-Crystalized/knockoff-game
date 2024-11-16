package gg.knockoff.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.logging.Level;

public class CrystalBlocks implements Listener {

    @EventHandler
    public void WhenCrystalBlockPlaced(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getEquipment().getItemInMainHand().getItemMeta().hasCustomModelData() || player.getEquipment().getItemInOffHand().getItemMeta().hasCustomModelData()) {

            Location blockloc = new Location(Bukkit.getWorld("world"), player.getEyeLocation().getBlockX(), player.getEyeLocation().getBlockY(), player.getEyeLocation().getBlockZ());
            blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
            blockloc.getBlock().getState().update();
            /*if(blockloc.getBlock().getBlockData() instanceof Directional) {
                Directional dir = (Directional)blockloc.getBlock().getBlockData();
                dir.setFacing(BlockFace.NORTH);
                blockloc.getBlock().setBlockData(dir);
                blockloc.getBlock().getState().update();
            }
            */

            //Bukkit.getWorld("world").setBlockData(player.getEyeLocation().getBlockX(), player.getEyeLocation().getBlockY(), player.getEyeLocation().getBlockZ(), );
            Bukkit.getLogger().log(Level.INFO, "Player placed a Crystal Block");
        }

    }

    @EventHandler
    public void PlayerPunchBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // prevent blocks from getting broken
        event.setCancelled(true);
    }
}