package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class GameManager {
    public Teams teams = new Teams();

    public GameManager() {
        Bukkit.getServer().sendMessage(Component.text("Starting Game!"));
        CopyRandomMapSection();
        Bukkit.getServer().sendMessage(Component.text("[Debugging] Current Section: " + knockoff.getInstance().mapdata.currentsection));

    }

    private static void CopyRandomMapSection() {
        //for debugging
        knockoff.getInstance().mapdata.getrandommapsection();
    }
}
