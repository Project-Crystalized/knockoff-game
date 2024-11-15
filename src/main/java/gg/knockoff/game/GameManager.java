package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class GameManager {
    public Teams teams = new Teams();

    public GameManager() {//Start of the game
        Bukkit.getServer().sendMessage(Component.text("Starting Game!"));
        CopyRandomMapSection();
        Bukkit.getServer().sendMessage(Component.text("[Debugging] Current Section loaded in memory: " + knockoff.getInstance().mapdata.currentsection));
        Location loc = new Location(Bukkit.getWorld("world"), 1000000, 5, 1000000);
        for(Player p : Bukkit.getOnlinePlayers()) {p.teleport(loc);} // teleport players to load chunks

        for (Player p : Bukkit.getOnlinePlayers()) {
            GiveTeamItems(p);
            p.setGameMode(GameMode.SURVIVAL);
        }

        //TODO This is where I want the map sections to generate. Theres this placeholder code here using vanilla commands for now until we make this
        //Clears everything near Z/X 10000000. This is to prevent old sections from previous games overlapping with current ones
        //TODO even this placeholder shit is broken lmao. Spams consoles and doesn't work because /fill limit
        int i = -64;
        int j = -64;
        while (i < 320) {
            i++;
            j++;
            int finalI = i;
            int finalJ = j;
            Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fill 999990 " + finalI +" 999990 1000010 " + finalJ +" 1000010 air"), 3*20);
        }
        Bukkit.getLogger().log(Level.SEVERE, "Apologies for the console spawn - Callum");


        Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fill 999990 100 999990 1000010 274 1000010 air"), 3*20);
        Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fill 999990 -64 999990 1000010 99 1000010 air"), 3*20);
        Bukkit.getScheduler().runTaskLater(knockoff.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fill 999990 0 999990 1000010 0 1000010 bedrock"), 3*20); //temporary platform until we figure out section cloning
        for(Player p : Bukkit.getOnlinePlayers()) {p.teleport(loc);} // teleport players again so they dont get stuck in the void
        Bukkit.getServer().sendMessage(Component.text("NOTICE: I cant figure out how to clone the actual sections of the map. You've been placed on a bedrock platform as a placeholder for now, have fun!"));

        // broken because /clone doesn't run in unloaded chunks, near location or at z/x = 1000000
        //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "clone " + knockoff.getInstance().mapdata.currentsection.get(1) + " " + knockoff.getInstance().mapdata.currentsection.get(2) + " " + knockoff.getInstance().mapdata.currentsection.get(3)
         //       + " " + knockoff.getInstance().mapdata.currentsection.get(4) + " " + knockoff.getInstance().mapdata.currentsection.get(5) + " " + knockoff.getInstance().mapdata.currentsection.get(6) + " 10000000 0 1000000 replace");


        new BukkitRunnable() { //Probably not great optimization
            @Override
            public void run() {
                Bukkit.getServer().sendActionBar(Component.text("[Debugging] Current Section: " + knockoff.getInstance().mapdata.currentsection));
            }
        }.runTaskTimer(knockoff.getInstance(), 20 ,1);
    }

    private static void CopyRandomMapSection() {
        knockoff.getInstance().mapdata.getrandommapsection();
    }

    public static void GiveTeamItems(Player player) {
        ItemStack item = new ItemStack(Material.AMETHYST_BLOCK, 64);
        ItemMeta im = item.getItemMeta();
        im.setCustomModelData(1);
        item.setItemMeta(im);
        player.getInventory().addItem(item);

        //if (Teams.GetPlayerTeam(player).equals("blue")) {}


        //player.getInventory().addItem(new ItemStack(Material.AMETHYST_BLOCK, 64));
    }
}
