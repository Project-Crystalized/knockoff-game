package gg.knockoff.game;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class DamagePercentage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        GameManager gc = knockoff.getInstance().GameManager;
        if (gc == null) {
            event.setCancelled(true);
            return;
        } else {
            if (event.getEntity() instanceof Player) {
                Player player = (((Player) event.getEntity()).getPlayer());
                Player damager = (((Player)event.getDamager()).getPlayer());
                PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                pd.DamagePercentageStopTimer = true;
                player.setVelocity(new Vector(player.getVelocity().getX(), player.getVelocity().getY(), player.getVelocity().getZ()));
                player.setVelocity(damager.getLocation().getDirection().multiply(pd.getDamagepercentage() / 6));
                pd.changepercentage(3);
            }
        }
    }
}
