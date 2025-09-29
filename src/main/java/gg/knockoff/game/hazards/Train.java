package gg.knockoff.game.hazards;

import gg.knockoff.game.GameManager;
import gg.knockoff.game.PlayerData;
import gg.knockoff.game.knockoff;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;

public class Train extends hazard {

    public Train(String name) {
        super(name);
    }

    @Override
    public void start() {
        displayHazard(
                translatable("crystalized.game.knockoff.chat.hazard").color(GOLD),
                translatable("crystalized.game.knockoff.hazard.train").color(NamedTextColor.GRAY),
                Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000))
        );
        boolean goZinsteadofX;
        switch (knockoff.getInstance().getRandomNumber(0, 10)) {
            case 0, 2, 4, 6, 8, 10 -> {
                goZinsteadofX = false;
            }
            default -> {
                goZinsteadofX = true;
            }
        }
        //If we want this hazard to be actually effective and not go some random direction nobody is at, we should try to target 1 player
        List<Player> playerList = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
                playerList.add(p);
            }
            p.playSound(p, "crystalized:effect.ambient.train_horn", 1, 1);
        }
        Player randomPlayer = playerList.get(knockoff.getInstance().getRandomNumber(0, playerList.size()));
        if (randomPlayer == null) {
            return; //90% of times this shouldn't happen, but this might be the case if everyone is dead and respawning.
        }

        new BukkitRunnable() {
            int timer = 3;
            double Z = randomPlayer.getLocation().clone().getZ();
            double Y = randomPlayer.getLocation().clone().getY();
            double X = randomPlayer.getLocation().clone().getX();
            String model = "";

            public void run() {
                if (timer == 3) {
                    model = "models/train/train_main";
                } else {
                    model = "models/train/train_carrage_passenger";
                }

                if (goZinsteadofX) {
                    //Z
                    Z = X;
                    spawnTrain(GameManager.SectionPlaceLocationZ - 5, knockoff.getInstance().mapdata.getCurrentZLength() + 5, Z, Y, model, true
                    );
                } else {
                    //X
                    spawnTrain(GameManager.SectionPlaceLocationX - 5, knockoff.getInstance().mapdata.getCurrentXLength() + 5, Z, Y, model, false
                    );
                }

                timer--;
                if (timer == 0) {
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 9);
    }

    private static void spawnTrain(int startX, int endX, double Z, double Y, String itemModel, boolean swapXandZ) {
        Location loc = new Location(Bukkit.getWorld("world"), startX, Y, Z, -90, 0);
        if (swapXandZ) {
            loc = new Location(loc.getWorld(), Z, Y, startX, 0, 0);
        }
        ArmorStand train = loc.getWorld().spawn(loc, ArmorStand.class, entity -> {
            ItemStack item = new ItemStack(Material.CHARCOAL);
            ItemMeta meta = item.getItemMeta();
            meta.setItemModel(new NamespacedKey("crystalized", itemModel));
            item.setItemMeta(meta);
            entity.setItem(EquipmentSlot.HEAD, item);
            entity.addDisabledSlots(EquipmentSlot.HEAD);
            entity.addDisabledSlots(EquipmentSlot.HAND);
            entity.addDisabledSlots(EquipmentSlot.OFF_HAND);
            entity.setInvisible(true);
            entity.setInvulnerable(true);
            entity.setGlowing(true);
        });
        BoundingBox hitbox = train.getBoundingBox();
        hitbox.resize(4, 4, 4, -4, -4, -4);
        new BukkitRunnable() {
            int soundTimer = 0;
            public void run() {
                if (knockoff.getInstance().GameManager == null ||
                        ( (!swapXandZ && train.getLocation().getX() > endX) || (swapXandZ && train.getLocation().getZ() > endX) )
                ) {
                    train.remove();
                    cancel();
                }
                if (swapXandZ) {
                    train.setVelocity(new Vector(0, 0.2, 0.8));
                } else {
                    train.setVelocity(new Vector(0.8, 0.2, 0));
                }

                //Minecart rail sound effect
                if (soundTimer < 0) {
                    soundTimer = 2 * 4;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(train, "minecraft:entity.minecart.riding", 2, 1);
                    }
                }
                soundTimer--;

                //player knockback
                for (Entity e : train.getNearbyEntities(4, 4, 4)) {
                    if (e instanceof Player p) {
                        PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
                        p.setVelocity(new Vector(0.5, 2, 0));
                        pd.percent = pd.percent + knockoff.getInstance().getRandomNumber(40, 60);
                    }
                }

                //block destrution
                for (Block b : getNearbyBlocks(train.getLocation(), 5, 5, 5)) {
                    if (!b.isEmpty()) {
                        b.breakNaturally(true);
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.playSound(b.getLocation(), "minecraft:entity.generic.explode", 1, 1);
                        }
                    }
                }
            }

            //this is dumb, dont have a better way of doing this. might be unsafe also
            Set<Block> getNearbyBlocks(Location center, int x, int y, int z) {
                Set<Block> list = new HashSet<>();
                Location loc = center.subtract(x/2, y/2, z/2);
                int X = x;
                while (X != 0) {
                    int Y = y;
                    while (Y != 0) {
                        int Z = z;
                        while (Z != 0) {
                            list.add(loc.clone().add(X, Y, Z).getBlock());
                            Z--;
                        }
                        Y--;
                    }
                    X--;
                }
                return list;
            }

        }.runTaskTimer(knockoff.getInstance(), 1, 5);
    }
}
