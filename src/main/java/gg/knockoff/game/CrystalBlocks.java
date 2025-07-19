package gg.knockoff.game;

import net.kyori.adventure.text.Component;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CrystalBlocks implements Listener {

    NamespacedKey blue = new NamespacedKey("crystalized", "block/nexus/blue");
    NamespacedKey cyan = new NamespacedKey("crystalized", "block/nexus/cyan");
    NamespacedKey green = new NamespacedKey("crystalized", "block/nexus/green");
    NamespacedKey lemon = new NamespacedKey("crystalized", "block/nexus/lemon");
    NamespacedKey lime = new NamespacedKey("crystalized", "block/nexus/lime");
    NamespacedKey magenta = new NamespacedKey("crystalized", "block/nexus/magenta");
    NamespacedKey orange = new NamespacedKey("crystalized", "block/nexus/orange");
    NamespacedKey peach = new NamespacedKey("crystalized", "block/nexus/peach");
    NamespacedKey purple = new NamespacedKey("crystalized", "block/nexus/purple");
    NamespacedKey white = new NamespacedKey("crystalized", "block/nexus/white");
    NamespacedKey yellow = new NamespacedKey("crystalized", "block/nexus/yellow");
    NamespacedKey red = new NamespacedKey("crystalized", "block/nexus/red");

    @EventHandler
    public void WhenCrystalBlockPlaced(BlockPlaceEvent event) {
        //TODO clean up this

        Player player = event.getPlayer();
        //if (event.getHand() != EquipmentSlot.HAND) return;
        //Block block = player.getTargetBlock(null, 5);
        Block block = event.getBlock();
        Location blockloc = new Location(Bukkit.getWorld("world"), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
        if (
                (blockloc.getBlockY() > knockoff.getInstance().mapdata.getCurrentYLength()
                || blockloc.getBlockX() > knockoff.getInstance().mapdata.getCurrentXLength()
                || blockloc.getBlockX() < GameManager.SectionPlaceLocationX
                || blockloc.getBlockZ() > knockoff.getInstance().mapdata.getCurrentZLength()
                || blockloc.getBlockZ() < GameManager.SectionPlaceLocationZ
                || blockloc.getBlockY() < (GameManager.SectionPlaceLocationY - 20)) &&
                (
                        blockloc.getBlockY() > GameManager.LastSectionPlaceLocationY + MapManager.LastYLength
                        || blockloc.getBlockX() > GameManager.LastSectionPlaceLocationX + MapManager.LastXLength
                        || blockloc.getBlockX() < GameManager.LastSectionPlaceLocationX
                        || blockloc.getBlockZ() > GameManager.LastSectionPlaceLocationZ + MapManager.LastZLength
                        || blockloc.getBlockZ() < GameManager.LastSectionPlaceLocationZ
                        || blockloc.getBlockY() < (GameManager.LastSectionPlaceLocationY - 20))
        ) {
            event.setCancelled(true);
            return;
        }
        //I had to rewrite this because || statements are weird
        GameManager gm = knockoff.getInstance().GameManager;
        if (gm.showdownModeStarted) {
            gm.startBreakingCrystal(blockloc.getBlock(), knockoff.getInstance().getRandomNumber(3 * 20, 15 * 20), knockoff.getInstance().getRandomNumber(20, 8 * 20), true);
        }
        //MainHand
        if (player.getEquipment().getItemInMainHand().getType().equals(Material.AMETHYST_BLOCK)) {
            if (player.getEquipment().getItemInMainHand().getItemMeta().hasItemModel()) {
                Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> {
                    NamespacedKey item_model = player.getEquipment().getItemInMainHand().getItemMeta().getItemModel();
                    String key = item_model.getKey();

                    switch (key) {
                        case "block/nexus/blue" -> {
                            blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.EAST);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/cyan" -> {
                            blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.NORTH);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/green" -> {
                            blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.SOUTH);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/lemon" -> {
                            blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.WEST);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/lime" -> {
                            blockloc.getBlock().setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.EAST);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/magenta" -> {
                            blockloc.getBlock().setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.NORTH);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/orange" -> {
                            blockloc.getBlock().setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.SOUTH);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/peach" -> {
                            blockloc.getBlock().setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.WEST);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/purple" -> {
                            blockloc.getBlock().setType(Material.GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.EAST);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/white" -> {
                            blockloc.getBlock().setType(Material.GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.SOUTH);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/yellow" -> {
                            blockloc.getBlock().setType(Material.GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.WEST);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/red" -> {
                            blockloc.getBlock().setType(Material.GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.NORTH);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                    }
                }, 1);
                Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> {
                    if (player.getEquipment().getItemInMainHand().getType().equals(Material.AMETHYST_BLOCK)) {
                        player.getInventory().getItemInMainHand().setAmount(64);
                    }
                }, 2);
            }
        }
        //OffHand
        if (player.getEquipment().getItemInOffHand().getType().equals(Material.AMETHYST_BLOCK)) {
            if (player.getEquipment().getItemInOffHand().getItemMeta().hasItemModel()) {
                Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> {
                    if (!blockloc.getBlock().getType().equals(Material.AMETHYST_BLOCK)) {
                        return;
                    }
                    NamespacedKey item_model = player.getEquipment().getItemInOffHand().getItemMeta().getItemModel();
                    String key = item_model.getKey();

                    switch (key) {
                        case "block/nexus/blue" -> {
                            blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.EAST);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/cyan" -> {
                            blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.NORTH);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/green" -> {
                            blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.SOUTH);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/lemon" -> {
                            blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.WEST);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/lime" -> {
                            blockloc.getBlock().setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.EAST);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/magenta" -> {
                            blockloc.getBlock().setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.NORTH);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/orange" -> {
                            blockloc.getBlock().setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.SOUTH);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/peach" -> {
                            blockloc.getBlock().setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.WEST);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/purple" -> {
                            blockloc.getBlock().setType(Material.GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.EAST);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/white" -> {
                            blockloc.getBlock().setType(Material.GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.SOUTH);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/yellow" -> {
                            blockloc.getBlock().setType(Material.GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.WEST);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                        case "block/nexus/red" -> {
                            blockloc.getBlock().setType(Material.GRAY_GLAZED_TERRACOTTA);
                            Directional dir = (Directional) blockloc.getBlock().getBlockData();
                            dir.setFacing(BlockFace.NORTH);
                            blockloc.getBlock().setBlockData(dir);
                            blockloc.getBlock().getState().update();
                        }
                    }
                }, 1);
                Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> {
                    if (player.getEquipment().getItemInOffHand().getType().equals(Material.AMETHYST_BLOCK)) {
                        player.getInventory().getItemInOffHand().setAmount(64);
                    }
                }, 2);
            }
        }
    }

    @EventHandler
    public void PlayerPunchBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = player.getTargetBlock(null ,5);
        if (knockoff.getInstance().GameManager != null) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                //could be optimised
                if (block.getType().equals(Material.WHITE_GLAZED_TERRACOTTA) || block.getType().equals(Material.GRAY_GLAZED_TERRACOTTA) || block.getType().equals(Material.LIGHT_GRAY_GLAZED_TERRACOTTA)
                    || block.getType().equals(Material.AMETHYST_BLOCK) || block.getType().equals(Material.PURPUR_BLOCK) || block.getType().equals(Material.PURPUR_SLAB) || block.getType().equals(Material.PURPUR_STAIRS)
                    || block.getType().equals(Material.PINK_STAINED_GLASS) || block.getType().equals(Material.PINK_STAINED_GLASS)) {
                    Location blockloc = new Location(Bukkit.getWorld("world"), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
                    blockloc.getBlock().breakNaturally(true);
                    PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                    pd.blocksbroken++;
                    if (knockoff.getInstance().DevMode) {
                        Bukkit.getServer().sendMessage(Component.text("[DEBUG] ")
                                .append(player.displayName())
                                .append(Component.text(" has broken a block"))
                        );
                    }
                }
            }
        } else {
            return;
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // prevent blocks from getting broken
        event.setCancelled(true);
    }
}