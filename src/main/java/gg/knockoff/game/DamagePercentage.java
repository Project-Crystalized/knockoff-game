package gg.knockoff.game;

import org.bukkit.*;
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
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class DamagePercentage implements Listener {
    //The same key that knockout orb is using in crystalizied essentials
    private static final NamespacedKey KNOCKOUT_ORB_KEY = new NamespacedKey("crystalized_essentials", "knockout_orb");

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (knockoff.getInstance().gameManager == null) {
            return;
        }
        Entity e = event.getEntity();
        if (e instanceof Player) {
            Player p = (Player) e;
            if(p.getGameMode() == GameMode.ADVENTURE){
                event.setCancelled(true);
                return;
            }

            PlayerData pd = knockoff.getInstance().gameManager.getPlayerData(p);
            if (pd == null) return;

            DamageSource ds = event.getDamageSource();
            if (ds.getDamageType().equals(DamageType.FALL)) {
                event.setCancelled(true);
                return;
            }
            if (ds.getDamageType().equals(DamageType.EXPLOSION) || ds.getDamageType().equals(DamageType.PLAYER_EXPLOSION)) {
                //strenght being calculated as beofre
                int strength = (int) event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
                pd.percent = pd.percent + strength;
                Location source = ds.getSourceLocation();
                if (source != null) {
                    Vector dir = p.getLocation().toVector().subtract(source.toVector()).setY(0);
                    //Prevents normaliziation of the vector with no direction, and fall backs to upward vector.
                    if (dir.lengthSquared() > 0) {
                        dir.normalize();
                    } else {
                        dir = new Vector(0, 1, 0);
                    }
                    //This is specific checks to see if the explosion was caused by knockout orb
                    Entity causingEntity = ds.getCausingEntity();
                    boolean knockoutOrb = causingEntity != null && causingEntity.getPersistentDataContainer().has(KNOCKOUT_ORB_KEY,
                            PersistentDataType.BYTE);
                    if (knockoutOrb) {
                        //The knockout orb gets progresively stronger based on players precnetage
                        double percentKnockback = pd.percent / 50.0;
                        //This is the base explosion force and the percentage knockbak added to it
                        double knockbackStrength = 1.5 + percentKnockback;
                        //multiples the knockback by strenght
                        Vector kb = dir.multiply(knockbackStrength);
                        //sets the new player velocity.
                        Bukkit.getScheduler().runTask(knockoff.getInstance(), () -> p.setVelocity(p.getVelocity().add(kb)));
                        //every other explosion
                    } else {
                        //nerfed the car knockback as I discovered during testing it was sending me so far on hit like
                        // I wasn't even able to see the map anymore
                        //The strenght is being diveded by 5 to make the initial value smaler, and the maximimum extra knockback value
                        // is 2.0, as min will select the lower one
                        //Creating a more managebel knockback rather than just being hit by a car ending up in flinged death.
                        Vector kb = dir.multiply(Math.min(strength / 5.0, 2.0));
                        Bukkit.getScheduler().runTask(knockoff.getInstance(), () -> p.setVelocity(p.getVelocity().add(kb)));
                    }
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
                //otherwise while standing on the magma knockback would not go through so that could have been abused same for chactos I assume
                event.setCancelled(true);
                if (pd.magmaDamageCooldown <= 0) {
                    pd.percent = pd.percent + knockoff.getInstance().getRandomNumber(2, 4);
                    //makes it be 1 second before taking damage again
                    pd.magmaDamageCooldown = 20;
                    //fakes the hurt animation and soun
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
                    p.playHurtAnimation(0);
                }
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
            if(d.getGameMode() == GameMode.ADVENTURE){
                e.setCancelled(true);
                return;
            }
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
            //Checks if the player is using the mace to attack
            boolean usingMace = d.getInventory().getItemInMainHand().getType() == Material.MACE;

            //maybe will prevent spam clicking as we needed to get the damager player.
            //didn't feel good with 0.5 might be better at 0.2, but need to test with players
            //added the using mace to the rule as when you were using the mace it wasn't passing past this check
            //due to the adjsting tick
            //works fine for preventing crazy double knockback spam click with hands though, so keept is as it was.
            if (!usingMace && d.getCooledAttackStrength(5) < 0.2) {
                return;
            }

            float addedVelocity = (float) ppd.percent / 24;
            p.setVelocity(d.getLocation().getDirection().multiply(new Vector(addedVelocity, 0.2, addedVelocity)));
            if (ppd.percent > 200) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(p.getLocation(), "minecraft:entity.generic.explode", 1, 1);
                }
            }

            if (usingMace) {
                //adds the damage as it was before, so it scales with height.
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
