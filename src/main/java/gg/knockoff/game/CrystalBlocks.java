package gg.knockoff.game;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
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
        if (knockoff.getInstance().gameManager == null) return;
        PlayerData pd = knockoff.getInstance().gameManager.getPlayerData(p);
        if (pd == null) return;
        ItemStack itemUsed;
        //sets the current distanse at first just to just to get the distance outside of current section
        int currentDistance = MapManager.getDistanceOutsideCurrentSection(b.getLocation());
        int outsideDistance;
        //depending on if the decaying island fully decayed and you are still in it
        if (pd.onlyUseCurrentSectionForBuildDistance) {
            //so if you are still in the fully decayed island it will only calulate the distanse between the current section
            //fully decay is when all blocks has been set to air except player blocks
            outsideDistance = currentDistance;
        } else {
            //if the decaying is not fully decaed, will see what distanse it must choose based on proxmity to the section
            int decayingDistance = MapManager.getDistanceOutsideDecayingSection(b.getLocation());
            outsideDistance = Math.min(currentDistance, decayingDistance);
        }
        //checker to see if inside the map or not
        boolean insideMap = MapManager.isInsideCurrentSection(b.getLocation()) || MapManager.isInsideDecayingSection(b.getLocation());
        //This is the final build limit which prevents the building when outside distanse becomes more than a 100
        if (outsideDistance > 100) {
            event.setCancelled(true);
            p.sendMessage(text("[!] You are too far from the build map!").color(NamedTextColor.RED));
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            return;
        }
        //resets the warning when reenters the map
        if (outsideDistance <= 0) {
            pd.warnedOutsideBuildLimit = false;
            pd.warnedFarBuildLimit = false;
            pd.warnedVeryFarBuildLimit = false;
        }
        //The final warning before the build limit
        if (outsideDistance >= 90 && !pd.warnedVeryFarBuildLimit) {
            pd.warnedVeryFarBuildLimit = true;
            p.sendMessage(text("[!] Build limit is super close !").color(NamedTextColor.RED));
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
        }
        //The second warning when the player left too far.
        else if (outsideDistance >= 75 && !pd.warnedFarBuildLimit) {
            pd.warnedFarBuildLimit = true;

            p.sendMessage(text("[!] You are getting very far from the map! Build limit exists !").color(NamedTextColor.YELLOW));
        }
        //The first warning when the player left the map area, about block instability.
        else if (outsideDistance >= 1 && !pd.warnedOutsideBuildLimit) {
            pd.warnedOutsideBuildLimit = true;
            p.sendMessage(text("[!] You left the build limit, the blocks get progressively unstable!").color(NamedTextColor.RED));
        }
        //Here it callculates how much the blocks should be unstable depending on distanse for x and z, for height it is slightly more forgiving in detection logic
        if (!insideMap) {
            int delay;
            int period;
            //The decay becomes faster the further the player is
            if (outsideDistance <= 5) {
                delay = 5 * 20;
                period = 10;
            } else if (outsideDistance <= 12) {
                delay = 3 * 20;
                period = 6;
            } else if (outsideDistance <= 15) {
                delay = 1 * 20;
                period = 4;
            } else if (outsideDistance <= 50) {
                delay = 0;
                period = 2;
            } else {
                //This is super far where player should have a near imposible building.
                delay = 0;
                period = 1;
            }
            //In the end decided to it converts, to make it look visualy unstable as well.
            GameManager.startBreakingCrystal(b, delay, period, true);
        }
        else if (MapManager.isInsideDecayingSection(b.getLocation())) {
            //This is the decaying map fall back, to not have unefected blocks in the decaying area, so they start decaying as fast as first stage of outside
            //Made so they convert to the purple crystals to match the decaying island.
            GameManager.startBreakingCrystal(b,  5 * 20, 10, true);
        }
        else if (MapManager.isInsideCurrentSection(b.getLocation())) {
            //In the end decided to make all the blocks have some form of the decay, just in the current section it is a a lot slower. So no blocks will stay forever
            //doesn't convert in the default stage
            GameManager.startBreakingCrystal(b, 15 * 20, 20, false);

        }
        /*
        OLD LOGIC:
        if (!(MapManager.isInsideCurrentSection(b.getLocation()) || MapManager.isInsideDecayingSection(b.getLocation()))) {
            event.setCancelled(true);
            p.sendMessage(text("[!] You cannot place blocks outside the map's borders!").color(NamedTextColor.RED));
            return;
        }*/

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
            //adds it to the player placed blocks list
            GameManager.playerPlacdBlocks.add(b);
            pd.blocksplaced++;
        }

        GameManager gm = knockoff.getInstance().gameManager;
        if (gm.showdownModeStarted) {
            gm.startBreakingCrystal(b, knockoff.getInstance().getRandomNumber(3 * 20, 15 * 20), knockoff.getInstance().getRandomNumber(20, 8 * 20), false);
        }
    }

    @EventHandler
    public void PlayerPunchBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = player.getTargetBlock(null ,5);
        if (knockoff.getInstance().gameManager != null && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            //Gets the player's starter border
            WorldBorder startingBorder = player.getWorldBorder();

            //If border is not null and the size is smaller or equal to 3 which is the size of the starting border
            if (startingBorder != null && startingBorder.getSize() <= 3.0) {
                //Prevents the players breaking of the starter platform, while in the starter border.
                event.setCancelled(true);
                //returns so the other stuff won't run
                return;
            }
            switch (block.getType()) {

                case WHITE_GLAZED_TERRACOTTA, GRAY_GLAZED_TERRACOTTA, LIGHT_GRAY_GLAZED_TERRACOTTA, BLACK_GLAZED_TERRACOTTA,
                     AMETHYST_BLOCK, CUT_COPPER_SLAB, CUT_COPPER_STAIRS, PINK_STAINED_GLASS, PINK_STAINED_GLASS_PANE,
                     PINK_CARPET, FROSTED_ICE
                        -> {
                    //Plays the amethiste break sound, otherwise it just sounds like breaking a normal broke. 
                    block.getWorld().playSound(block.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 1.0f);
                    //removes it from the player placed blocks set if it is in it.
                    GameManager.playerPlacdBlocks.remove(block);
                    if (block.getType().equals(Material.FROSTED_ICE)) {
                        block.setType(Material.AIR);
                    } else {
                        block.breakNaturally(true);
                    }
                    PlayerData pd = knockoff.getInstance().gameManager.getPlayerData(player);
                    if (pd != null) pd.blocksbroken++;
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