package gg.knockoff.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class DamagePercentage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (knockoff.getInstance().gameManager == null) {
            return;
        }
        Entity e = event.getEntity();
        if (e instanceof Player) {
            Player p = (Player) e;
            PlayerData pd = knockoff.getInstance().gameManager.getPlayerData(p);
            if (pd == null) return;

            DamageSource ds = event.getDamageSource();
            if (ds.getDamageType().equals(DamageType.FALL)) {
                event.setCancelled(true);
                return;
            }
            if (ds.getDamageType().equals(DamageType.EXPLOSION) || ds.getDamageType().equals(DamageType.PLAYER_EXPLOSION)) {
                int strength = (int) event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
                pd.percent = pd.percent + strength;
                Location source = ds.getSourceLocation();
                if (source != null) {
                    Vector dir = p.getLocation().toVector().subtract(source.toVector()).setY(0);
                    dir.normalize();
                    Vector kb = dir.multiply(strength);
                    Bukkit.getScheduler().runTask(knockoff.getInstance(), () -> p.setVelocity(p.getVelocity().add(kb)));
                }
            } else if (ds.getDamageType().equals(DamageType.MAGIC) || ds.getDamageType().equals(DamageType.WITHER)) {
                pd.percent = pd.percent + knockoff.getInstance().getRandomNumber(5, 6);
            } else if (ds.getDamageType().equals(DamageType.MOB_ATTACK) || ds.getDamageType().equals(DamageType.MOB_ATTACK_NO_AGGRO)) {
                if (ds.getCausingEntity() instanceof Warden) {
                    pd.percent = pd.percent + knockoff.getInstance().getRandomNumber(10, 13);
                } else {
                    pd.percent = pd.percent + knockoff.getInstance().getRandomNumber(3, 6);
                }
            } else if (ds.getDamageType().equals(DamageType.LIGHTNING_BOLT)) {
                pd.percent = pd.percent + knockoff.getInstance().getRandomNumber(10, 13);
            } else if (ds.getDamageType().equals(DamageType.SONIC_BOOM)) {
                pd.percent = pd.percent + knockoff.getInstance().getRandomNumber(25, 50);
            } else if (ds.getDamageType().equals(DamageType.HOT_FLOOR) || ds.getDamageType().equals(DamageType.CACTUS)) {
                pd.percent = pd.percent + knockoff.getInstance().getRandomNumber(2, 4);
            } else if (ds.getDamageType().equals(DamageType.STALAGMITE)) {
                pd.percent = pd.percent + (int) (event.getDamage() * 6);
                //pd.percent = pd.percent + knockoff.getInstance().getRandomNumber(5, 10);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();
        if (knockoff.getInstance().gameManager == null) {
            e.setCancelled(true);
            return;
        }

        //player to player
        if (entity instanceof Player && damager instanceof Player) {
            Player p = (Player) e.getEntity();
            PlayerData ppd = knockoff.getInstance().gameManager.getPlayerData(p);
            if (ppd == null) return;
            Player d = (Player) e.getDamager();
            GameManager gm = knockoff.getInstance().gameManager;

            if (gm.teams.GetPlayerTeam(p).equals(gm.teams.GetPlayerTeam(d))) {
                e.setCancelled(true);
                return;
            }
            try {
                if (p.getWorldBorder().getSize() == 3.0) { //weird workaround but this is to stop players hitting through the border
                    e.setCancelled(true);
                    return;
                }
            } catch (Exception exception) {
            }

            //p.setVelocity(d.getLocation().getDirection().multiply(ppd.percent / 12).add(new Vector(0, 0.4, 0)));

            if (p.getCooledAttackStrength(5) < 0.5) {
                return; //should hopefully prevent spam clicking
            }

            float addedVelocity = (float) ppd.percent / 24;
            p.setVelocity(d.getLocation().getDirection().multiply(new Vector(addedVelocity, 0.2, addedVelocity)));
            if (ppd.percent > 200) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(p.getLocation(), "minecraft:entity.generic.explode", 1, 1);
                }
            }

            if (d.getInventory().getItemInMainHand().getType().equals(Material.MACE)) {
                ppd.percent = ppd.percent + (int) e.getDamage();
            } else {
                if (e.isCritical()) {
                    ppd.percent = ppd.percent + knockoff.getInstance().getRandomNumber(5, 7);
                } else {
                    ppd.percent = ppd.percent + knockoff.getInstance().getRandomNumber(3, 6);
                }
            }

        }
        //bee to player, to prevent the poison effect which is unbalanced
        if (entity instanceof Player && damager instanceof Bee) {
            e.setCancelled(true);
            ((Player) e.getEntity()).damage(2, DamageSource.builder(DamageType.MOB_ATTACK).build());
        }
    }
}
