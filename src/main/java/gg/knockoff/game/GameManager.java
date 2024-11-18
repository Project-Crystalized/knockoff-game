package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
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

    public GameManager() {//Start of the game
        Bukkit.getServer().sendMessage(text("Starting Game!"));
        CopyRandomMapSection();

        for (Player p : Bukkit.getOnlinePlayers()) {
            GiveTeamItems(p);
            p.setGameMode(GameMode.SURVIVAL);
        }

        //TODO section generation should start here

        // broken because /clone doesn't run in unloaded chunks, near location or at z/x = 1000000
        //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "clone " + knockoff.getInstance().mapdata.currentsection.get(1) + " " + knockoff.getInstance().mapdata.currentsection.get(2) + " " + knockoff.getInstance().mapdata.currentsection.get(3)
        //       + " " + knockoff.getInstance().mapdata.currentsection.get(4) + " " + knockoff.getInstance().mapdata.currentsection.get(5) + " " + knockoff.getInstance().mapdata.currentsection.get(6) + " 10000000 0 1000000 replace");

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
                //uncomment the line below for debugging
                //Bukkit.getServer().sendActionBar(Component.text("[Debugging] Current Section loaded in memory: " + knockoff.getInstance().mapdata.currentsection));

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
}
