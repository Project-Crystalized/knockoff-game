package gg.knockoff.game.hazards;

import gg.knockoff.game.GameManager;
import gg.knockoff.game.knockoff;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class hazard {
    //Hazards need to extend this class and @Override start() and call super(name) in its main class, See TNT.class or some other hazard for reference.

    public String name = "placeholder";

    public hazard(String name) {
        this.name = name;
    }

    public void start() throws Exception {
        throw new Exception("start() was called in hazards/hazard.java. You should not get this Exception");
    }

    public static Location getValidSpot(boolean get2loc) {
        boolean IsValidSpot = false;
        Location blockloc = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        Location blockloc2 = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        while (!IsValidSpot && knockoff.getInstance().GameManager != null) {
            blockloc = new Location(Bukkit.getWorld("world"),
                    knockoff.getInstance().getRandomNumber(GameManager.SectionPlaceLocationX, knockoff.getInstance().mapdata.getCurrentXLength()) + 0.5,
                    knockoff.getInstance().getRandomNumber(GameManager.SectionPlaceLocationY, knockoff.getInstance().mapdata.getCurrentYLength()),
                    knockoff.getInstance().getRandomNumber(GameManager.SectionPlaceLocationZ, knockoff.getInstance().mapdata.getCurrentZLength()) + 0.5
            );
            blockloc2 = new Location(Bukkit.getWorld("world"),
                    blockloc.getX(),
                    blockloc.getY() + 1,
                    blockloc.getZ()
            );
            if ((!blockloc.getBlock().isEmpty()) && blockloc2.getBlock().isEmpty()) {
                IsValidSpot = true;
            } else {
                IsValidSpot = false;
            }
        }
        if (get2loc) {
            return blockloc2;
        } else {
            return blockloc;
        }
    }

    public void displayHazard(Component title, Component subtitle, Title.Times times) {
        Bukkit.getServer().showTitle(Title.title(title, subtitle, times));
        Bukkit.getServer().sendMessage(title.append(subtitle));
    }
}
