package gg.knockoff.game;

import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class DamagePercentage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (knockoff.getInstance().GameManager == null) {
            event.setCancelled(true);
            return;
        }
        Entity e = event.getEntity();
        if (e instanceof Player) {
            PlayerData pd = knockoff.getInstance().GameManager.getPlayerData((Player) e);

            DamageSource ds = event.getDamageSource();
            if (ds.getDamageType().equals(DamageType.FALL)) {
                event.setCancelled(true);
                return;
            }
            if (ds.getDamageType().equals(DamageType.EXPLOSION)) {
                pd.percent = pd.percent + 5;
            } else if (ds.getDamageType().equals(DamageType.MAGIC)) {
                pd.percent = pd.percent + 2;
            }

            //pd.percent = pd.percent + 20; //testing
        }
    }

    @EventHandler
    public void onPlayerDamagePlayer(EntityDamageByEntityEvent e) {
        if (knockoff.getInstance().GameManager == null || (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player))) {
            e.setCancelled(true);
            return;
        }

        Player p = (Player) e.getEntity();
        PlayerData ppd = knockoff.getInstance().GameManager.getPlayerData(p);
        Player d = (Player) e.getDamager();
        GameManager gm = knockoff.getInstance().GameManager;

        if (gm.teams.GetPlayerTeam(p).equals(gm.teams.GetPlayerTeam(d))) {
            e.setCancelled(true);
            return;
        }

        p.setVelocity(new Vector(p.getVelocity().getX(), p.getVelocity().getY(), p.getVelocity().getZ()));
        p.setVelocity(d.getLocation().getDirection().multiply(ppd.percent / 12));


        if (e.isCritical()) {
            ppd.percent = ppd.percent + 7;
        } else {
            ppd.percent = ppd.percent + 3;
        }

    }
}
