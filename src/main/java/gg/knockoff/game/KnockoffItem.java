package gg.knockoff.game;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
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

import static net.kyori.adventure.text.Component.text;

public class KnockoffItem {
    public static final ArrayList ItemList = new ArrayList();
    public static ItemStack BoostOrb = new ItemStack(Material.COAL);
    public static ItemStack BridgeOrb = new ItemStack(Material.COAL);
    public static ItemStack ExplosiveOrb = new ItemStack(Material.COAL);
    public static ItemStack GrapplingOrb = new ItemStack(Material.COAL);
    public static ItemStack KnockoutOrb = new ItemStack(Material.COAL);
    public static ItemStack CloudTotem = new ItemStack(Material.COAL);
    public static ItemStack WindCharge = new ItemStack(Material.WIND_CHARGE);
    public static ItemStack BoxingGlove = new ItemStack(Material.GOLDEN_SWORD);

    public static void SetupKnockoffItems() {
        //Commented out powerups aren't functional yet and give a debug message. Commented out for a chance for players to always get usable powerups

        ItemList.clear();
        //ItemList.add("BoostOrb"); //
        //ItemList.add("BridgeOrb");
        //ItemList.add("ExplosiveOrb"); //
        //ItemList.add("GrapplingOrb");
        //ItemList.add("KnockoutOrb");
        //ItemList.add("CloudTotem");
        //ItemList.add("WindCharge");  //
        ItemList.add("BoxingGlove");


        ItemMeta boostim = BoostOrb.getItemMeta();
        boostim.setCustomModelData(1);
        boostim.displayName(Component.translatable("crystalized.orb.boost.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> boostlore = new ArrayList<>();
        boostlore.add(Component.translatable("crystalized.orb.boost.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        boostim.lore(boostlore);
        BoostOrb.setItemMeta(boostim);

        ItemMeta bridgeim = BridgeOrb.getItemMeta();
        bridgeim.setCustomModelData(2);
        bridgeim.displayName(Component.translatable("crystalized.orb.bridge.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> bridgelore = new ArrayList<>();
        bridgelore.add(Component.translatable("crystalized.orb.bridge.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        bridgeim.lore(bridgelore);
        BridgeOrb.setItemMeta(bridgeim);

        ItemMeta explosiveim = ExplosiveOrb.getItemMeta();
        explosiveim.setCustomModelData(3);
        explosiveim.displayName(Component.translatable("crystalized.orb.explosive.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> explosivelore = new ArrayList<>();
        explosivelore.add(Component.translatable("crystalized.orb.explosive.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        explosiveim.lore(explosivelore);
        ExplosiveOrb.setItemMeta(explosiveim);

        ItemMeta grapplingim = GrapplingOrb.getItemMeta();
        grapplingim.setCustomModelData(4);
        grapplingim.displayName(Component.translatable("crystalized.orb.grappling.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> grapplinglore = new ArrayList<>();
        grapplinglore.add(Component.translatable("crystalized.orb.grappling.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        grapplingim.lore(grapplinglore);
        GrapplingOrb.setItemMeta(grapplingim);

        ItemMeta knockoutim = KnockoutOrb.getItemMeta();
        knockoutim.setCustomModelData(6);
        knockoutim.displayName(Component.translatable("crystalized.orb.knockout.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> knockoutlore = new ArrayList<>();
        knockoutlore.add(Component.translatable("crystalized.orb.knockout.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        knockoutim.lore(knockoutlore);
        KnockoutOrb.setItemMeta(knockoutim);

        ItemMeta cloudtotemim = CloudTotem.getItemMeta();
        cloudtotemim.setCustomModelData(10);
        cloudtotemim.displayName(Component.translatable("crystalized.totem.cloud.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> cloudtotemlore = new ArrayList<>();
        cloudtotemlore.add(Component.translatable("crystalized.totem.cloud.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        cloudtotemim.lore(cloudtotemlore);
        CloudTotem.setItemMeta(cloudtotemim);

        ItemMeta windchargeim = WindCharge.getItemMeta();
        List<Component> windchargelore = new ArrayList<>();
        windchargelore.add(text("TODO: Make a string for the wind charge's description").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        windchargeim.lore(windchargelore);
        WindCharge.setAmount(3);
        WindCharge.setItemMeta(windchargeim);

        ItemMeta boxingglove_im = BoxingGlove.getItemMeta();
        boxingglove_im.customName(Component.translatable("crystalized.item.boxingglove.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> boxingglovelore = new ArrayList<>();
        boxingglovelore.add(Component.translatable("crystalized.item.boxingglove.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        boxingglove_im.lore(boxingglovelore);
        boxingglove_im.setItemModel(new NamespacedKey("crystalized", "knockoff_boxing_glove"));
        boxingglove_im.addEnchant(Enchantment.KNOCKBACK, 2, false);
        BoxingGlove.setItemMeta(boxingglove_im);
        BoxingGlove.setData(DataComponentTypes.DAMAGE, 0); //I dont trust these but we'll go with it
        BoxingGlove.setData(DataComponentTypes.MAX_DAMAGE, 5);
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
                case "BoxingGlove":
                    player.getInventory().addItem(BoxingGlove);
                    break;
            }

        } else {
            Bukkit.getLogger().log(Level.WARNING, "[KNOCKOFFITEM] Unknown Item \"" + Item + "\".");
            player.sendMessage(text("[!] An internal error occurred with custom items."));
        }
    }
}

class DropPowerup {
    public static void DropPowerup(Location loc, String powerup) {

        Item DroppedItem = loc.getWorld().spawn(loc, Item.class, entity -> {
            if (powerup.equals("BoostOrb")) {
                entity.setItemStack(KnockoffItem.BoostOrb);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.boost.name").color(NamedTextColor.AQUA)));
            } else if (powerup.equals("BridgeOrb")) {
                entity.setItemStack(KnockoffItem.BridgeOrb);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.bridge.name").color(NamedTextColor.GRAY)));
            } else if (powerup.equals("ExplosiveOrb")) {
                entity.setItemStack(KnockoffItem.ExplosiveOrb);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.explosive.name").color(NamedTextColor.GOLD)));
            } else if (powerup.equals("GrapplingOrb")) {
                entity.setItemStack(KnockoffItem.GrapplingOrb);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.grappling.name").color(NamedTextColor.YELLOW)));
            } else if (powerup.equals("KnockoutOrb")) {
                entity.setItemStack(KnockoffItem.KnockoutOrb);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.knockout.name").color(NamedTextColor.LIGHT_PURPLE)));
            } else if (powerup.equals("CloudTotem")) {
                entity.setItemStack(KnockoffItem.CloudTotem);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.totem.cloud.name").color(NamedTextColor.WHITE)));
            } else if (powerup.equals("WindCharge")) {
                entity.setItemStack(KnockoffItem.WindCharge);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("item.minecraft.wind_charge").color(NamedTextColor.WHITE)));
            } else if (powerup.equals("BoxingGlove")) {
                entity.setItemStack(KnockoffItem.BoxingGlove);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.item.boxingglove.name").color(NamedTextColor.GOLD)));
            }

            else {
                Bukkit.getServer().sendMessage(text("[!] An internal error occurred, check console."));
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
