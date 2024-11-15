package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class GameManager {
    public Teams teams = new Teams();

    public GameManager() { //Start of the game
        Bukkit.getServer().sendMessage(Component.text("Starting Game!"));
        CopyRandomMapSection();
        Bukkit.getServer().sendMessage(Component.text("[Debugging] Current Section: " + knockoff.getInstance().mapdata.currentsection));

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

    //TODO
    //@EventHandler
    //public static void GiveTeamItems() {
    //    Player player = Bukkit.getPlayer();
    //    player.getInventory()
    //}
}
