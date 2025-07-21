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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrystalBlocks implements Listener {

    @EventHandler
    public void WhenCrystalBlockPlaced(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        Block b = event.getBlock();
        ItemStack itemUsed;

        if (!(MapManager.isInsideCurrentSection(b.getLocation()) || MapManager.isInsideDecayingSection(b.getLocation()))) {
            event.setCancelled(true);
            return;
        }
        if (p.getInventory().getItemInMainHand().getType().equals(Material.AMETHYST_BLOCK)) {
            itemUsed = p.getInventory().getItemInMainHand();
            Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> {
                p.getInventory().getItemInMainHand().setAmount(64);
            }, 2);
        } else if (p.getInventory().getItemInOffHand().getType().equals(Material.AMETHYST_BLOCK)) {
            itemUsed = p.getInventory().getItemInOffHand();
            Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> {
                p.getInventory().getItemInOffHand().setAmount(64);
            }, 2);
        } else {
            return;
        }

        //may cause errors with the 2nd check if you place a vanilla amethyst block with nothing, not my problem as that wont happen without the player being in creative
        if (itemUsed.hasItemMeta() && itemUsed.getItemMeta().hasItemModel()) {
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
                case "block/nexus/cyan", "block/nexus/magenta", "block/nexus/white", "block/nexus/strong" -> {
                    dir.setFacing(BlockFace.NORTH);
                }
                case "block/nexus/green", "block/nexus/orange", "block/nexus/yellow" -> {
                    dir.setFacing(BlockFace.SOUTH);
                }
                case "block/nexus/lemon", "block/nexus/peach", "block/nexus/red" -> {
                    dir.setFacing(BlockFace.WEST);
                }
            }

            b.setBlockData(dir);
            b.getState().update();
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
        if (knockoff.getInstance().GameManager != null) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                //idk how to make this better
                if (
                        block.getType().equals(Material.WHITE_GLAZED_TERRACOTTA)
                                || block.getType().equals(Material.GRAY_GLAZED_TERRACOTTA)
                                || block.getType().equals(Material.LIGHT_GRAY_GLAZED_TERRACOTTA)
                                || block.getType().equals(Material.BLACK_GLAZED_TERRACOTTA)
                                || block.getType().equals(Material.AMETHYST_BLOCK)
                                || block.getType().equals(Material.PURPUR_BLOCK)
                                || block.getType().equals(Material.PURPUR_SLAB)
                                || block.getType().equals(Material.PURPUR_STAIRS)
                                || block.getType().equals(Material.CUT_COPPER_SLAB)
                                || block.getType().equals(Material.CUT_COPPER_STAIRS)
                                || block.getType().equals(Material.PINK_STAINED_GLASS)
                                || block.getType().equals(Material.PINK_STAINED_GLASS_PANE)
                ){
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
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }
}