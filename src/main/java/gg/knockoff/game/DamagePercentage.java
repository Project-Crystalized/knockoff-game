package gg.knockoff.game;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class DamagePercentage implements Listener {

    /*
    @EventHandler
    public void onDamageOld(EntityDamageEvent event) {
        GameManager gc = knockoff.getInstance().GameManager;
        if (gc == null) {
            event.setCancelled(true);
            return;
        } else {
            if (event.getEntity() instanceof Player) {
                Player player = ((Player) event.getEntity()).getPlayer();
                PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                pd.DamagePercentageStopTimer = true;
                player.setVelocity(new Vector(player.getVelocity().getX(), player.getVelocity().getY(), player.getVelocity().getZ()));
                player.setVelocity(player.getLocation().getDirection().multiply(-pd.getDamagepercentage()));
                pd.changepercentage(3);
            }
        }
    }
     */

    @EventHandler
    public void onDamageOld(EntityDamageByEntityEvent event) {
        GameManager gc = knockoff.getInstance().GameManager;
        if (gc == null) {
            event.setCancelled(true);
            return;
        } else {
            if (event.getEntity() instanceof Player) {
                Player player = (((Player) event.getEntity()).getPlayer());
                PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                pd.DamagePercentageStopTimer = true;
                player.setVelocity(new Vector(player.getVelocity().getX(), player.getVelocity().getY(), player.getVelocity().getZ()));
                player.setVelocity(player.getLocation().getDirection().multiply(-pd.getDamagepercentage() / 3));
                pd.changepercentage(3);
            }
        }
    }
}
