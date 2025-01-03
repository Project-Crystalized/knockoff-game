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
                    ItemStack BoostOrb = getBoostOrb();
                    player.getInventory().addItem(BoostOrb);
                    break;
                case "BridgeOrb":
                    ItemStack BridgeOrb = getBridgeOrb();
                    player.getInventory().addItem(BridgeOrb);
                    break;
                case "ExplosiveOrb":
                    ItemStack ExplosiveOrb = getExplosiveOrb();
                    player.getInventory().addItem(ExplosiveOrb);
                    break;
                case "GrapplingOrb":
                    ItemStack GrapplingOrb = getGrapplingOrb();
                    player.getInventory().addItem(GrapplingOrb);
                    break;
                case "KnockoutOrb":
                    ItemStack KnockoutOrb = getKnockoutOrb();
                    player.getInventory().addItem(KnockoutOrb);
                    break;
                case "CloudTotem":
                    ItemStack CloudTotem = getCloudTotem();
                    player.getInventory().addItem(CloudTotem);
                    break;
                case "WindCharge":
                    ItemStack WindCharge = getWindCharge();
                    player.getInventory().addItem(WindCharge);
                    break;
            }

        } else {
            Bukkit.getLogger().log(Level.WARNING, "[KNOCKOFFITEM] Unknown Item \"" + Item + "\".");
            player.sendMessage(Component.text("[!] An internal error occurred with custom items."));
        }
    }

    public static ItemStack getBoostOrb() {
        ItemStack boostitem = new ItemStack(Material.COAL);
        ItemMeta boostim = boostitem.getItemMeta();
        boostim.setCustomModelData(1);
        boostim.displayName(Component.translatable("crystalized.orb.boost.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> boostlore = new ArrayList<>();
        boostlore.add(Component.translatable("crystalized.orb.boost.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        boostim.lore(boostlore);
        boostitem.setItemMeta(boostim);
        return boostitem;
    }
    public static ItemStack getBridgeOrb() {
        ItemStack bridgeitem = new ItemStack(Material.COAL);
        ItemMeta bridgeim = bridgeitem.getItemMeta();
        bridgeim.setCustomModelData(2);
        bridgeim.displayName(Component.translatable("crystalized.orb.bridge.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> bridgelore = new ArrayList<>();
        bridgelore.add(Component.translatable("crystalized.orb.bridge.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        bridgeim.lore(bridgelore);
        bridgeitem.setItemMeta(bridgeim);
        return bridgeitem;
    }
    public static ItemStack getExplosiveOrb() {
        ItemStack explosiveitem = new ItemStack(Material.COAL);
        ItemMeta explosiveim = explosiveitem.getItemMeta();
        explosiveim.setCustomModelData(3);
        explosiveim.displayName(Component.translatable("crystalized.orb.explosive.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> explosivelore = new ArrayList<>();
        explosivelore.add(Component.translatable("crystalized.orb.explosive.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        explosiveim.lore(explosivelore);
        explosiveitem.setItemMeta(explosiveim);
        return explosiveitem;
    }
    public static ItemStack getGrapplingOrb() {
        ItemStack grapplingitem = new ItemStack(Material.COAL);
        ItemMeta grapplingim = grapplingitem.getItemMeta();
        grapplingim.setCustomModelData(4);
        grapplingim.displayName(Component.translatable("crystalized.orb.grappling.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> grapplinglore = new ArrayList<>();
        grapplinglore.add(Component.translatable("crystalized.orb.grappling.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        grapplingim.lore(grapplinglore);
        grapplingitem.setItemMeta(grapplingim);
        return grapplingitem;
    }
    public static ItemStack getKnockoutOrb() {
        ItemStack knockoutitem = new ItemStack(Material.COAL);
        ItemMeta knockoutim = knockoutitem.getItemMeta();
        knockoutim.setCustomModelData(6);
        knockoutim.displayName(Component.translatable("crystalized.orb.knockout.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> knockoutlore = new ArrayList<>();
        knockoutlore.add(Component.translatable("crystalized.orb.knockout.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        knockoutim.lore(knockoutlore);
        knockoutitem.setItemMeta(knockoutim);
        return knockoutitem;
    }
    public static ItemStack getCloudTotem() {
        ItemStack cloudtotemitem = new ItemStack(Material.COAL);
        ItemMeta cloudtotemim = cloudtotemitem.getItemMeta();
        cloudtotemim.setCustomModelData(10);
        cloudtotemim.displayName(Component.translatable("crystalized.totem.cloud.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> cloudtotemlore = new ArrayList<>();
        cloudtotemlore.add(Component.translatable("crystalized.totem.cloud.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        cloudtotemim.lore(cloudtotemlore);
        cloudtotemitem.setItemMeta(cloudtotemim);
        return cloudtotemitem;
    }
    public static ItemStack getWindCharge() {
        ItemStack windchargeitem = new ItemStack(Material.WIND_CHARGE);
        ItemMeta windchargeim = windchargeitem.getItemMeta();
        List<Component> windchargelore = new ArrayList<>();
        windchargelore.add(Component.text("TODO: Make a string for the wind charge's description").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        windchargeim.lore(windchargelore);
        windchargeitem.setItemMeta(windchargeim);
        windchargeitem.setAmount(3);
        return windchargeitem;
    }
}

class DropPowerup {
    public static void DropPowerup(Location loc, String powerup) {

        Item DroppedItem = loc.getWorld().spawn(loc, Item.class, entity -> {
            if (powerup.equals("BoostOrb")) {
                ItemStack BoostOrb = KnockoffItem.getBoostOrb();
                entity.setItemStack(BoostOrb);
                entity.customName(Component.text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.boost.name").color(NamedTextColor.AQUA)));
            } else if (powerup.equals("BridgeOrb")) {
                ItemStack BridgeOrb = KnockoffItem.getBridgeOrb();
                entity.setItemStack(BridgeOrb);
                entity.customName(Component.text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.bridge.name").color(NamedTextColor.GRAY)));
            } else if (powerup.equals("ExplosiveOrb")) {
                ItemStack ExplosiveOrb = KnockoffItem.getExplosiveOrb();
                entity.setItemStack(ExplosiveOrb);
                entity.customName(Component.text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.explosive.name").color(NamedTextColor.GOLD)));
            } else if (powerup.equals("GrapplingOrb")) {
                ItemStack GrapplingOrb = KnockoffItem.getGrapplingOrb();
                entity.setItemStack(GrapplingOrb);
                entity.customName(Component.text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.grappling.name").color(NamedTextColor.YELLOW)));
            } else if (powerup.equals("KnockoutOrb")) {
                ItemStack KnockoutOrb = KnockoffItem.getKnockoutOrb();
                entity.setItemStack(KnockoutOrb);
                entity.customName(Component.text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.knockout.name").color(NamedTextColor.LIGHT_PURPLE)));
            } else if (powerup.equals("CloudTotem")) {
                ItemStack CloudTotem = KnockoffItem.getCloudTotem();
                entity.setItemStack(CloudTotem);
                entity.customName(Component.text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.totem.cloud.name").color(NamedTextColor.WHITE)));
            } else if (powerup.equals("WindCharge")) {
                ItemStack WindCharge = KnockoffItem.getWindCharge();
                entity.setItemStack(WindCharge);
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
                timer --;
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 20);
    }
}
