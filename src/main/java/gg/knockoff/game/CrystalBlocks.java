package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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

    @EventHandler
    public void WhenCrystalBlockPlaced(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        //if (event.getHand() != EquipmentSlot.HAND) return;
        Block block = player.getTargetBlock(null, 5);
        Location blockloc = new Location(Bukkit.getWorld("world"), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
        if ((blockloc.getBlockY() > knockoff.getInstance().mapdata.getCurrentYLength()
                || blockloc.getBlockX() > knockoff.getInstance().mapdata.getCurrentXLength()
                || blockloc.getBlockX() < GameManager.SectionPlaceLocationX
                || blockloc.getBlockZ() > knockoff.getInstance().mapdata.getCurrentZLength()
                || blockloc.getBlockZ() < GameManager.SectionPlaceLocationZ) &&
                (blockloc.getBlockY() > GameManager.LastSectionPlaceLocationY + MapManager.LastYLength
                || blockloc.getBlockX() > GameManager.LastSectionPlaceLocationX + MapManager.LastXLength
                || blockloc.getBlockX() < GameManager.LastSectionPlaceLocationX
                || blockloc.getBlockZ() > GameManager.LastSectionPlaceLocationZ + MapManager.LastZLength
                || blockloc.getBlockZ() < GameManager.LastSectionPlaceLocationZ)) {
            event.setCancelled(true);
            return;
        }
        //I had to rewrite this because || statements are weird, fuck you - Callum
        //MainHand
        if (player.getEquipment().getItemInMainHand().getType().equals(Material.AMETHYST_BLOCK)) {
            if (player.getEquipment().getItemInMainHand().getItemMeta().hasCustomModelData()) {
                Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> {
                    if (!blockloc.getBlock().getType().equals(Material.AMETHYST_BLOCK)) {
                        return;
                    }
                    if (player.getEquipment().getItemInMainHand().getItemMeta().getCustomModelData() < 5) {
                        blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
                    } else if (player.getEquipment().getItemInMainHand().getItemMeta().getCustomModelData() < 9) {
                        blockloc.getBlock().setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                    } else {
                        blockloc.getBlock().setType(Material.GRAY_GLAZED_TERRACOTTA);
                    }
                    if (blockloc.getBlock().getBlockData() instanceof Directional) {
                        Directional dir = (Directional) blockloc.getBlock().getBlockData();

                        if (player.getEquipment().getItemInMainHand().getItemMeta().getCustomModelData() == 1 || player.getEquipment().getItemInMainHand().getItemMeta().getCustomModelData() == 5 ||
                                player.getEquipment().getItemInMainHand().getItemMeta().getCustomModelData() == 9) {
                            dir.setFacing(BlockFace.EAST);
                        } else if (player.getEquipment().getItemInMainHand().getItemMeta().getCustomModelData() == 2 || player.getEquipment().getItemInMainHand().getItemMeta().getCustomModelData() == 6 ||
                                player.getEquipment().getItemInMainHand().getItemMeta().getCustomModelData() == 10) {
                            dir.setFacing(BlockFace.NORTH);
                        } else if (player.getEquipment().getItemInMainHand().getItemMeta().getCustomModelData() == 3 || player.getEquipment().getItemInMainHand().getItemMeta().getCustomModelData() == 7 ||
                                player.getEquipment().getItemInMainHand().getItemMeta().getCustomModelData() == 11) {
                            dir.setFacing(BlockFace.SOUTH);
                        } else if (player.getEquipment().getItemInMainHand().getItemMeta().getCustomModelData() == 4 || player.getEquipment().getItemInMainHand().getItemMeta().getCustomModelData() == 8 ||
                                player.getEquipment().getItemInMainHand().getItemMeta().getCustomModelData() == 12) {
                            dir.setFacing(BlockFace.WEST);
                        }
                        blockloc.getBlock().setBlockData(dir);
                    }
                    blockloc.getBlock().getState().update();
                    PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                    pd.blocksplaced++;
                    if (knockoff.getInstance().DevMode) {
                        Bukkit.getServer().sendMessage(Component.text("[DEBUG] ")
                                .append(player.displayName())
                                .append(Component.text(" has placed a block (mainhand)"))
                        );
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
            if (player.getEquipment().getItemInOffHand().getItemMeta().hasCustomModelData()) {
                Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> {
                    if (!blockloc.getBlock().getType().equals(Material.AMETHYST_BLOCK)) {
                        return;
                    }
                    if (player.getEquipment().getItemInOffHand().getItemMeta().getCustomModelData() < 5) {
                        blockloc.getBlock().setType(Material.WHITE_GLAZED_TERRACOTTA);
                    } else if (player.getEquipment().getItemInOffHand().getItemMeta().getCustomModelData() < 9) {
                        blockloc.getBlock().setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                    } else {
                        blockloc.getBlock().setType(Material.GRAY_GLAZED_TERRACOTTA);
                    }
                    if (blockloc.getBlock().getBlockData() instanceof Directional) {
                        Directional dir = (Directional) blockloc.getBlock().getBlockData();

                        if (player.getEquipment().getItemInOffHand().getItemMeta().getCustomModelData() == 1 || player.getEquipment().getItemInOffHand().getItemMeta().getCustomModelData() == 5 ||
                                player.getEquipment().getItemInOffHand().getItemMeta().getCustomModelData() == 9) {
                            dir.setFacing(BlockFace.EAST);
                        } else if (player.getEquipment().getItemInOffHand().getItemMeta().getCustomModelData() == 2 || player.getEquipment().getItemInOffHand().getItemMeta().getCustomModelData() == 6 ||
                                player.getEquipment().getItemInOffHand().getItemMeta().getCustomModelData() == 10) {
                            dir.setFacing(BlockFace.NORTH);
                        } else if (player.getEquipment().getItemInOffHand().getItemMeta().getCustomModelData() == 3 || player.getEquipment().getItemInOffHand().getItemMeta().getCustomModelData() == 7 ||
                                player.getEquipment().getItemInOffHand().getItemMeta().getCustomModelData() == 11) {
                            dir.setFacing(BlockFace.SOUTH);
                        } else if (player.getEquipment().getItemInOffHand().getItemMeta().getCustomModelData() == 4 || player.getEquipment().getItemInOffHand().getItemMeta().getCustomModelData() == 8 ||
                                player.getEquipment().getItemInOffHand().getItemMeta().getCustomModelData() == 12) {
                            dir.setFacing(BlockFace.WEST);
                        }
                        blockloc.getBlock().setBlockData(dir);
                    }
                    blockloc.getBlock().getState().update();
                    PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                    pd.blocksplaced++;
                    if (knockoff.getInstance().DevMode) {
                        Bukkit.getServer().sendMessage(Component.text("[DEBUG] ")
                                .append(player.displayName())
                                .append(Component.text(" has placed a block (offhand)"))
                        );
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