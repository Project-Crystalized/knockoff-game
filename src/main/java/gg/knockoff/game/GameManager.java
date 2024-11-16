package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class GameManager {
    public Teams teams = new Teams();

    public GameManager() {//Start of the game
        Bukkit.getServer().sendMessage(Component.text("Starting Game!"));
        CopyRandomMapSection();

        for (Player p : Bukkit.getOnlinePlayers()) {
            GiveTeamItems(p);
            p.setGameMode(GameMode.SURVIVAL);
        }

        //TODO section generation should start here

        // broken because /clone doesn't run in unloaded chunks, near location or at z/x = 1000000
        //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "clone " + knockoff.getInstance().mapdata.currentsection.get(1) + " " + knockoff.getInstance().mapdata.currentsection.get(2) + " " + knockoff.getInstance().mapdata.currentsection.get(3)
        //       + " " + knockoff.getInstance().mapdata.currentsection.get(4) + " " + knockoff.getInstance().mapdata.currentsection.get(5) + " " + knockoff.getInstance().mapdata.currentsection.get(6) + " 10000000 0 1000000 replace");

        new BukkitRunnable() { //Probably not great optimization
            @Override
            public void run() {
                Bukkit.getServer().sendActionBar(Component.text("[Debugging] Current Section loaded in memory: " + knockoff.getInstance().mapdata.currentsection));
            }
        }.runTaskTimer(knockoff.getInstance(), 20 ,1);
    }

    private static void CopyRandomMapSection() {
        knockoff.getInstance().mapdata.getrandommapsection();
    }

    public static void GiveTeamItems(Player player) {
        ItemStack item = new ItemStack(Material.AMETHYST_BLOCK, 64);
        ItemMeta im = item.getItemMeta();

        Bukkit.getLogger().log(Level.INFO, "[GAMEMANAGER] Player " + player.getName() + "Is in Team " + Teams.GetPlayerTeam(player));

        if (Teams.GetPlayerTeam(player).equals("blue")) {
            im.setCustomModelData(1);
        } else if (Teams.GetPlayerTeam(player).equals("cyan")) {
            im.setCustomModelData(2);
        } else
        if (Teams.GetPlayerTeam(player).equals("green")) {
            im.setCustomModelData(3);
        } else
        if (Teams.GetPlayerTeam(player).equals("lemon")) {
            im.setCustomModelData(4);
        } else
        if (Teams.GetPlayerTeam(player).equals("lime")) {
            im.setCustomModelData(5);
        } else
        if (Teams.GetPlayerTeam(player).equals("magenta")) {
            im.setCustomModelData(6);
        } else
        if (Teams.GetPlayerTeam(player).equals("orange")) {
            im.setCustomModelData(7);
        } else
        if (Teams.GetPlayerTeam(player).equals("peach")) {
            im.setCustomModelData(8);
        }

        item.setItemMeta(im);

        player.getInventory().addItem(item);

        //player.getInventory().addItem(new ItemStack(Material.AMETHYST_BLOCK, 64));
    }
}
