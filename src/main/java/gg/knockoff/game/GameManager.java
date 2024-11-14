package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class GameManager {
    public Teams teams = new Teams();

    public GameManager() {
        Bukkit.getServer().sendMessage(Component.text("Starting Game!"));
    }
}
