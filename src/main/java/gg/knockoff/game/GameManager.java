package gg.knockoff.game;

import com.google.gson.JsonArray;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockState;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.text;

public class GameManager {
    public List<PlayerData> playerDatas;
    public Teams teams = new Teams();

    public static int CurrentSectionX = 0;
    public static int CurrentSectionY = 0;
    public static int CurrentSectionZ = 0;
    public static int SectionPlaceLocationX = 1000000;
    public static int SectionPlaceLocationY = 0;
    public static int SectionPlaceLocationZ = 1000000;

    public GameManager() {//Start of the game
        Bukkit.getServer().sendMessage(text("Starting Game! \n(Note: the server might lag slightly)"));
        CopyRandomMapSection();

        for (Player p : Bukkit.getOnlinePlayers()) {
            GiveTeamItems(p);
            p.setGameMode(GameMode.SURVIVAL);
        }
        // Sets the target area to air to prevent previous game's sections to interfere with the current game
        // Could be optimised, Filling all this in 1 go and/or in larger spaces causes your server to most likely go out of memory or not respond for a good while
        // Plus this lags the server anyways
        JsonArray data = knockoff.getInstance().mapdata.getCurrentsection();
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        CuboidRegion selection = new CuboidRegion(world, BlockVector3.at(1000100, -30, 1000100), BlockVector3.at(1000000, 40, 1000000));
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
            RandomPattern pat = new RandomPattern();
            BlockState air = BukkitAdapter.adapt(Material.AIR.createBlockData());
            pat.add(air, 1);
            editSession.setBlocks(selection, pat);
        }  catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }


        PlaceCurrentlySelectedSection();

        new BukkitRunnable() {
            @Override
            public void run() {
                playerDatas = new ArrayList<PlayerData>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerData p = new PlayerData(player);
                    playerDatas.add(p);
                }
            }
        }.runTaskLater(knockoff.getInstance(), 1);

        new BukkitRunnable() { //Probably not great optimization
            @Override
            public void run() {
                //for map debugging
                Bukkit.getServer().sendActionBar(Component.text("[Debugging] Section data " + knockoff.getInstance().mapdata.currentsection +
                        ". X:" + knockoff.getInstance().mapdata.getCurrentXLength() + ". Y:" + knockoff.getInstance().mapdata.getCurrentYLength() + ". Z:" + knockoff.getInstance().mapdata.getCurrentZLength()
                        + ". MX:" + knockoff.getInstance().mapdata.getCurrentMiddleXLength() + ". MY:" + knockoff.getInstance().mapdata.getCurrentMiddleYLength() + ". MZ:" + knockoff.getInstance().mapdata.getCurrentMiddleZLength()));

                for (Player p : Bukkit.getOnlinePlayers()) {
                    PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
                    //p.getPlayer().sendActionBar(text("[Debugging] Your Stats. Lives: " + pd.getLives() + " Kills: " + pd.getKills() + " Deaths: " + pd.getDeaths()));
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 20 ,1);


    }

    private static void CopyRandomMapSection() {
        knockoff.getInstance().mapdata.getrandommapsection();
    }

    public static void GiveTeamItems(Player player) {
        ItemStack item = new ItemStack(Material.AMETHYST_BLOCK, 64);
        ItemMeta im = item.getItemMeta();
        PlayerInventory inv = player.getInventory();

        //for debugging
        //Bukkit.getLogger().log(Level.INFO, "[GAMEMANAGER] Player " + player.getName() + "Is in Team " + Teams.GetPlayerTeam(player));

        if (Teams.GetPlayerTeam(player).equals("blue")) {
            im.setCustomModelData(1);
            inv.setChestplate(colorArmor(Color.fromRGB(0x0A42BB), new ItemStack(Material.LEATHER_CHESTPLATE)));
            inv.setLeggings(colorArmor(Color.fromRGB(0x0A42BB), new ItemStack(Material.LEATHER_LEGGINGS)));
            inv.setBoots(colorArmor(Color.fromRGB(0x0A42BB), new ItemStack(Material.LEATHER_BOOTS)));

        } else if (Teams.GetPlayerTeam(player).equals("cyan")) {
            im.setCustomModelData(2);
            inv.setChestplate(colorArmor(Color.fromRGB(0x157D91), new ItemStack(Material.LEATHER_CHESTPLATE)));
            inv.setLeggings(colorArmor(Color.fromRGB(0x157D91), new ItemStack(Material.LEATHER_LEGGINGS)));
            inv.setBoots(colorArmor(Color.fromRGB(0x157D91), new ItemStack(Material.LEATHER_BOOTS)));

        } else
        if (Teams.GetPlayerTeam(player).equals("green")) {
            im.setCustomModelData(3);
            inv.setChestplate(colorArmor(Color.fromRGB(0x0A971E), new ItemStack(Material.LEATHER_CHESTPLATE)));
            inv.setLeggings(colorArmor(Color.fromRGB(0x0A971E), new ItemStack(Material.LEATHER_LEGGINGS)));
            inv.setBoots(colorArmor(Color.fromRGB(0x0A971E), new ItemStack(Material.LEATHER_BOOTS)));

        } else
        if (Teams.GetPlayerTeam(player).equals("lemon")) {
            im.setCustomModelData(4);
            inv.setChestplate(colorArmor(Color.fromRGB(0xFFC500), new ItemStack(Material.LEATHER_CHESTPLATE)));
            inv.setLeggings(colorArmor(Color.fromRGB(0xFFC500), new ItemStack(Material.LEATHER_LEGGINGS)));
            inv.setBoots(colorArmor(Color.fromRGB(0xFFC500), new ItemStack(Material.LEATHER_BOOTS)));

        } else
        if (Teams.GetPlayerTeam(player).equals("lime")) {
            im.setCustomModelData(5);
            inv.setChestplate(colorArmor(Color.fromRGB(0x67E555), new ItemStack(Material.LEATHER_CHESTPLATE)));
            inv.setLeggings(colorArmor(Color.fromRGB(0x67E555), new ItemStack(Material.LEATHER_LEGGINGS)));
            inv.setBoots(colorArmor(Color.fromRGB(0x67E555), new ItemStack(Material.LEATHER_BOOTS)));

        } else
        if (Teams.GetPlayerTeam(player).equals("magenta")) {
            im.setCustomModelData(6);
            inv.setChestplate(colorArmor(Color.fromRGB(0xDA50E0), new ItemStack(Material.LEATHER_CHESTPLATE)));
            inv.setLeggings(colorArmor(Color.fromRGB(0xDA50E0), new ItemStack(Material.LEATHER_LEGGINGS)));
            inv.setBoots(colorArmor(Color.fromRGB(0xDA50E0), new ItemStack(Material.LEATHER_BOOTS)));

        } else
        if (Teams.GetPlayerTeam(player).equals("orange")) {
            im.setCustomModelData(7);
            inv.setChestplate(colorArmor(Color.fromRGB(0xFF7900), new ItemStack(Material.LEATHER_CHESTPLATE)));
            inv.setLeggings(colorArmor(Color.fromRGB(0xFF7900), new ItemStack(Material.LEATHER_LEGGINGS)));
            inv.setBoots(colorArmor(Color.fromRGB(0xFF7900), new ItemStack(Material.LEATHER_BOOTS)));

        } else
        if (Teams.GetPlayerTeam(player).equals("peach")) {
            im.setCustomModelData(8);
            inv.setChestplate(colorArmor(Color.fromRGB(0xFF8775), new ItemStack(Material.LEATHER_CHESTPLATE)));
            inv.setLeggings(colorArmor(Color.fromRGB(0xFF8775), new ItemStack(Material.LEATHER_LEGGINGS)));
            inv.setBoots(colorArmor(Color.fromRGB(0xFF8775), new ItemStack(Material.LEATHER_BOOTS)));

        } else
        if (Teams.GetPlayerTeam(player).equals("purple")) {
            im.setCustomModelData(9);
            inv.setChestplate(colorArmor(Color.fromRGB(0x7525DC), new ItemStack(Material.LEATHER_CHESTPLATE)));
            inv.setLeggings(colorArmor(Color.fromRGB(0x7525DC), new ItemStack(Material.LEATHER_LEGGINGS)));
            inv.setBoots(colorArmor(Color.fromRGB(0x7525DC), new ItemStack(Material.LEATHER_BOOTS)));

        } else
        if (Teams.GetPlayerTeam(player).equals("red")) {
            im.setCustomModelData(10);
            inv.setChestplate(colorArmor(Color.fromRGB(0xF74036), new ItemStack(Material.LEATHER_CHESTPLATE)));
            inv.setLeggings(colorArmor(Color.fromRGB(0xF74036), new ItemStack(Material.LEATHER_LEGGINGS)));
            inv.setBoots(colorArmor(Color.fromRGB(0xF74036), new ItemStack(Material.LEATHER_BOOTS)));

        } else
        if (Teams.GetPlayerTeam(player).equals("white")) {
            im.setCustomModelData(11);
            inv.setChestplate(colorArmor(Color.fromRGB(0xFFFFFF), new ItemStack(Material.LEATHER_CHESTPLATE)));
            inv.setLeggings(colorArmor(Color.fromRGB(0xFFFFFF), new ItemStack(Material.LEATHER_LEGGINGS)));
            inv.setBoots(colorArmor(Color.fromRGB(0xFFFFFF), new ItemStack(Material.LEATHER_BOOTS)));

        } else
        if (Teams.GetPlayerTeam(player).equals("yellow")) {
            im.setCustomModelData(12);
            inv.setChestplate(colorArmor(Color.fromRGB(0xFBE059), new ItemStack(Material.LEATHER_CHESTPLATE)));
            inv.setLeggings(colorArmor(Color.fromRGB(0xFBE059), new ItemStack(Material.LEATHER_LEGGINGS)));
            inv.setBoots(colorArmor(Color.fromRGB(0xFBE059), new ItemStack(Material.LEATHER_BOOTS)));

        }

        im.itemName(Component.translatable("crystalized.item.nexusblock.name"));
        item.setItemMeta(im);
        player.getInventory().addItem(item);



    }

    private static ItemStack colorArmor(Color c, ItemStack i) {
        LeatherArmorMeta lam = (LeatherArmorMeta) i.getItemMeta();
        lam.setColor(c);
        lam.setUnbreakable(true);
        i.setItemMeta(lam);
        return i;
    }

    public PlayerData getPlayerData(Player p) {
        for (PlayerData pd : playerDatas) {
            if (pd.player.equals(p.getName())) {
                return pd;
            }
        }
        Bukkit.getServer().sendMessage(text("error occured, a player didnt have associated data"));
        Bukkit.getLogger().warning("player name: " + p.getName());

        for (PlayerData pd : playerDatas) {
            Bukkit.getLogger().warning(pd.player);
        }

        return null;
    }

    private static void PlaceCurrentlySelectedSection() {
        JsonArray data = knockoff.getInstance().mapdata.getCurrentsection();
        World world = Bukkit.getWorld("world");
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(world), BlockVector3.at(data.get(1).getAsInt(), data.get(2).getAsInt(), data.get(3).getAsInt()), BlockVector3.at(data.get(4).getAsInt(), data.get(5).getAsInt(), data.get(6).getAsInt()));
            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                    BukkitAdapter.adapt(world), region, clipboard, region.getMinimumPoint()
            );
            Operations.complete(forwardExtentCopy);

            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(SectionPlaceLocationX, SectionPlaceLocationY, SectionPlaceLocationZ))
                    .build();
            Operations.complete(operation);
        }  catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GAMEMANAGER] Exception occured within the worldedit API:");
            e.printStackTrace();
        }
    }
}
