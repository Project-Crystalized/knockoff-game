package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class Hazards {
    public static final List<hazards> HazardList = new ArrayList<>();
    private static final List<NamespacedKey> CarsList = new ArrayList<>();
    private static boolean IsHazardOver = false;

    enum hazards {
        tnt,
        slimetime,
        flyingcars,
    }

    public static void StartHazards() {
        IsHazardOver = true;
        HazardList.clear();
        HazardList.add(hazards.tnt);
        HazardList.add(hazards.slimetime);
        HazardList.add(hazards.flyingcars);

        CarsList.clear();
        CarsList.add(new NamespacedKey("crystalized", "models/car/military_bus"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/military_van"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/abby_car"));
        CarsList.add(new NamespacedKey("crystalized", "models/car/beat_up_truck"));

        new BukkitRunnable() {
            int timer = knockoff.getInstance().getRandomNumber(30, 60);
            @Override
            public void run() {
                if (knockoff.getInstance().GameManager == null || GameManager.GameState.equals("end")) {cancel();}
                if (knockoff.getInstance().DevMode) {
                    Bukkit.getServer().sendMessage(text("Hazards disabled due to developer mode."));
                    cancel();
                }

                if (timer == 0) {
                    timer = knockoff.getInstance().getRandomNumber(30, 60);
                    NewHazard();
                }
                if (IsHazardOver) {
                    timer--;
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0 ,20);
    }

    private static void NewHazard() {
        IsHazardOver = false;
        hazards Select = HazardList.get(knockoff.getInstance().getRandomNumber(0, HazardList.size())); //Will get a random hazard from the list

        Component HazardMessage = translatable("crystalized.game.knockoff.chat.hazard").color(NamedTextColor.GOLD);

        for (Player player : Bukkit.getOnlinePlayers()) {
            switch (Select) {
                case hazards.tnt:
                    player.sendMessage(HazardMessage.append(translatable("block.minecraft.tnt").color(NamedTextColor.RED)));
                    player.showTitle(
                            Title.title(
                                    HazardMessage, translatable("block.minecraft.tnt").color(NamedTextColor.RED),
                                    Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000)))
                    );
                    break;
                case hazards.slimetime:
                    player.sendMessage(HazardMessage.append(translatable("crystalized.game.knockoff.hazard.slimetime").color(NamedTextColor.GREEN)));
                    player.showTitle(
                            Title.title(
                                    HazardMessage, translatable("crystalized.game.knockoff.hazard.slimetime").color(NamedTextColor.GREEN),
                                    Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000)))
                    );
                    break;
                case hazards.flyingcars:
                    player.sendMessage(HazardMessage.append(translatable("crystalized.game.knockoff.hazard.flyingcars").color(NamedTextColor.BLUE)));
                    player.showTitle(
                            Title.title(
                                    HazardMessage, translatable("crystalized.game.knockoff.hazard.flyingcars").color(NamedTextColor.BLUE),
                                    Title.Times.times(Duration.ofMillis(0), Duration.ofSeconds(3), Duration.ofMillis(1000)))
                    );
                    break;
                default:
                    break;
            }
        }

        switch (Select) {
            case hazards.tnt:
                new BukkitRunnable() {
                    int timer = 0;
                    @Override
                    public void run() {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
                            if (!pd.isPlayerDead) {
                                Location loc = new Location(player.getWorld(), player.getX(), player.getY() + 10, player.getZ(), player.getYaw(), player.getPitch());
                                TNTPrimed TNT = player.getWorld().spawn(loc, TNTPrimed.class, entity -> {

                                });
                            }
                            player.playSound(player, "minecraft:entity.tnt.primed",  50, 1);
                        }
                        if (timer == 3) {
                            IsHazardOver = true;
                            cancel();
                        }
                        timer++;
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 40);
                break;

            case hazards.slimetime:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 12 * 20, 2, false, false, true));
                    player.playSound(player, "minecraft:block.conduit.activate", 50, 1);
                }
                new BukkitRunnable() {
                    int timer = 0;
                    @Override
                    public void run() {
                        if (timer == 12) { //This should last the jump boost's duration
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.playSound(player, "minecraft:block.beacon.deactivate", 50, 1);
                            }
                            IsHazardOver = true;
                            cancel();
                        }
                        timer++;
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 20);
                break;

            case hazards.flyingcars:
                new BukkitRunnable() {
                    int timer = 0;
                    @Override
                    public void run() {
                        if (timer == 12) { //This should last the jump boost's duration
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.playSound(player, "minecraft:block.beacon.deactivate", 50, 1);
                            }
                            IsHazardOver = true;
                            cancel();
                        }
                        spawnFlyingCar();
                        timer++;
                    }
                }.runTaskTimer(knockoff.getInstance(), 0, 20);

                break;
        }
    }

    public static void spawnFlyingCar() {
        boolean IsValidSpot = false;
        Location blockloc = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        Location blockloc2 = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        while (!IsValidSpot) {
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
            if ((!blockloc.getBlock().getType().equals(Material.AIR)) && blockloc2.getBlock().getType().equals(Material.AIR)) {
                IsValidSpot = true;
            } else {
                IsValidSpot = false;
            }
        }

        Location loc = new Location(Bukkit.getWorld("world"), blockloc.getX(), knockoff.getInstance().mapdata.getCurrentYLength() + 13, blockloc.getZ());
        ItemStack item = new ItemStack(Material.CHARCOAL);
        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(CarsList.get(knockoff.getInstance().getRandomNumber(0, CarsList.size())));
        item.setItemMeta(meta);

        //No idea how to launch a fireball from the server, this is the next best thing I guess
        ArmorStand tempentity = Bukkit.getWorld("world").spawn(loc, ArmorStand.class, entity -> {
            entity.setRotation(0, 90);
        });

        Fireball ball = tempentity.launchProjectile(Fireball.class, tempentity.getEyeLocation().getDirection());
        ball.getLocation().add(ball.getVelocity().normalize().multiply(1.05));
        ball.setYield(6);
        ball.setVisualFire(false);
        //ball.setVisibleByDefault(false); //Does weird ass visual bugs, dont uncomment this

        ArmorStand car = Bukkit.getWorld("world").spawn(loc, ArmorStand.class, entity -> {
            entity.getEquipment().setHelmet(item);
            entity.setVisible(false);
            ball.addPassenger(entity);
            entity.setGlowing(true);
        });
        tempentity.remove();

        new BukkitRunnable() {
            public void run() {
                if (ball.isDead()) {
                    car.remove();
                    cancel();
                }
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 1);

    }


}
