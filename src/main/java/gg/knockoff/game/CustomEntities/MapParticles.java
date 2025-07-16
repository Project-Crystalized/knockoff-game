package gg.knockoff.game.CustomEntities;

import gg.knockoff.game.GameManager;
import gg.knockoff.game.knockoff;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class MapParticles {
    Location location;

    public MapParticles(Location loc) {
        location = loc;

        new BukkitRunnable() {
            public void run() {
                int roundCounter = GameManager.RoundCounter;

                //TODO spawn purple particles at location

                if (roundCounter == 0) {
                    moveTo(
                            new Location(
                                    location.getWorld(),
                                    knockoff.getInstance().mapdata.getCurrentMiddleXLength(),
                                    knockoff.getInstance().mapdata.getCurrentMiddleYLength(),
                                    knockoff.getInstance().mapdata.getCurrentMiddleZLength()),
                            20
                    );
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 1);
    }

    public void moveTo(Location destination, int timeInTicks) {
        //TODO yea im lost on how to do this shit - callum
    }
}
