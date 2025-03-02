package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Display;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class KnockoffItem {
    public static final ArrayList ItemList = new ArrayList();
    public static ItemStack BoostOrb = new ItemStack(Material.COAL);
    public static ItemStack BridgeOrb = new ItemStack(Material.COAL);
    public static ItemStack ExplosiveOrb = new ItemStack(Material.COAL);
    public static ItemStack GrapplingOrb = new ItemStack(Material.COAL);
    public static ItemStack KnockoutOrb = new ItemStack(Material.COAL);
    public static ItemStack CloudTotem = new ItemStack(Material.COAL);
    public static ItemStack WindCharge = new ItemStack(Material.WIND_CHARGE);

    public static void SetupKnockoffItems() {
        //Commented out powerups aren't functional yet and give a debug message. Commented out for a chance for players to always get usable powerups

        ItemList.clear();
        ItemList.add("BoostOrb");
        //ItemList.add("BridgeOrb");
        ItemList.add("ExplosiveOrb");
        //ItemList.add("GrapplingOrb");
        //ItemList.add("KnockoutOrb");
        //ItemList.add("CloudTotem");
        ItemList.add("WindCharge");
    }

    public static void GiveCustomItem(Player player, String Item) {
        if (ItemList.contains(Item)) {
            PlayerInventory inv = player.getInventory();
            switch (Item) {
                case "BoostOrb":
                    player.getInventory().addItem(BoostOrb);
                    break;
                case "BridgeOrb":
                    player.getInventory().addItem(BridgeOrb);
                    break;
                case "ExplosiveOrb":
                    player.getInventory().addItem(ExplosiveOrb);
                    break;
                case "GrapplingOrb":
                    player.getInventory().addItem(GrapplingOrb);
                    break;
                case "KnockoutOrb":
                    player.getInventory().addItem(KnockoutOrb);
                    break;
                case "CloudTotem":
                    player.getInventory().addItem(CloudTotem);
                    break;
                case "WindCharge":
                    player.getInventory().addItem(WindCharge);
                    break;
            }

        } else {
            Bukkit.getLogger().log(Level.WARNING, "[KNOCKOFFITEM] Unknown Item \"" + Item + "\".");
            player.sendMessage(Component.text("[!] An internal error occurred with custom items."));
        }
    }
}

class DropPowerup {
    public static void DropPowerup(Location loc, String powerup) {

        Item DroppedItem = loc.getWorld().spawn(loc, Item.class, entity -> {
            if (powerup.equals("BoostOrb")) {
                entity.setItemStack(KnockoffItem.BoostOrb);
                entity.customName(Component.text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.boost.name").color(NamedTextColor.AQUA)));
            } else if (powerup.equals("BridgeOrb")) {
                entity.setItemStack(KnockoffItem.BridgeOrb);
                entity.customName(Component.text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.bridge.name").color(NamedTextColor.GRAY)));
            } else if (powerup.equals("ExplosiveOrb")) {
                entity.setItemStack(KnockoffItem.ExplosiveOrb);
                entity.customName(Component.text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.explosive.name").color(NamedTextColor.GOLD)));
            } else if (powerup.equals("GrapplingOrb")) {
                entity.setItemStack(KnockoffItem.GrapplingOrb);
                entity.customName(Component.text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.grappling.name").color(NamedTextColor.YELLOW)));
            } else if (powerup.equals("KnockoutOrb")) {
                entity.setItemStack(KnockoffItem.KnockoutOrb);
                entity.customName(Component.text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.knockout.name").color(NamedTextColor.LIGHT_PURPLE)));
            } else if (powerup.equals("CloudTotem")) {
                entity.setItemStack(KnockoffItem.CloudTotem);
                entity.customName(Component.text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.totem.cloud.name").color(NamedTextColor.WHITE)));
            } else if (powerup.equals("WindCharge")) {
                entity.setItemStack(KnockoffItem.WindCharge);
                entity.customName(Component.text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("item.minecraft.wind_charge").color(NamedTextColor.WHITE)));
            }

            else {
                Bukkit.getServer().sendMessage(Component.text("[!] An internal error occurred, check console."));
                Bukkit.getLogger().log(Level.WARNING, "[KNOCKOFFITEM] Unknown Item \"" + powerup + "\".");
            }
            entity.setGravity(true);
            entity.setGlowing(true);
            entity.setCanPlayerPickup(true);
            entity.setCustomNameVisible(false);
        });
        TextDisplay DroppedItemName = loc.getWorld().spawn(DroppedItem.getLocation(), TextDisplay.class, entity -> {
            entity.setBillboard(Display.Billboard.CENTER);
            entity.text(DroppedItem.customName());
            entity.setSeeThrough(true);
        });

        DroppedItem.teleport(loc);
        new BukkitRunnable() {
            @Override
            public void run() {
                DroppedItem.setGravity(false);
                if (DroppedItem.isDead()) {
                    DroppedItemName.remove();
                    cancel();
                }
                if (knockoff.getInstance().GameManager == null) {
                    DroppedItemName.remove();
                    DroppedItem.remove();
                    cancel();
                }

                //Prob not the best way of doing this but adding it as a passenger doesn't seem to work
                Location Nameloc = new Location(
                        DroppedItem.getLocation().getWorld(),
                        DroppedItem.getLocation().getX(),
                        DroppedItem.getLocation().getY() + 1,
                        DroppedItem.getLocation().getZ()
                        );
                DroppedItemName.teleport(Nameloc);
                DroppedItem.teleport(loc);
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 1);

        new BukkitRunnable() {
            int timer = 30;
            @Override
            public void run() {
                if (timer == 0) {
                    DroppedItemName.remove();
                    DroppedItem.remove();
                    cancel();
                }
                timer--;
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 20);
    }
}
