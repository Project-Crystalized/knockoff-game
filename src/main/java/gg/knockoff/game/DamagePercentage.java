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
        Player player = (((Player) event.getEntity()).getPlayer());
        Player damager = (((Player)event.getDamager()).getPlayer());
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (gc == null) {
            event.setCancelled(true);
            return;
        } else {
            if (event.getEntity() instanceof Player) { //Prevents friendly fire, this checks if the player you're attacking is in the same team as you and canceles the attack event
                if (knockoff.getInstance().GameManager.teams.GetPlayerTeam(player)
                        .equals(knockoff.getInstance().GameManager.teams.GetPlayerTeam(damager))) {
                    event.setCancelled(true);
                } else {
                    PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                    pd.DamagePercentageStopTimer = true;
                    player.setVelocity(new Vector(player.getVelocity().getX(), player.getVelocity().getY(), player.getVelocity().getZ()));
                    player.setVelocity(damager.getLocation().getDirection().multiply(pd.getDamagepercentage() / 10));
                    pd.changepercentage(3);
                }
            }
        }
    }
}
