package gg.knockoff.game;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
import static net.kyori.adventure.text.Component.translatable;

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
    public static ItemStack WingedOrb = new ItemStack(Material.COAL);
    public static ItemStack PoisonOrb = new ItemStack(Material.COAL);

    public static void SetupKnockoffItems() {
        //Commented out powerups aren't functional yet and give a debug message. Commented out for a chance for players to always get usable powerups

        ItemList.clear();
        ItemList.add("BoostOrb");
        ItemList.add("BridgeOrb");
        ItemList.add("ExplosiveOrb");
        ItemList.add("GrapplingOrb");
        //ItemList.add("KnockoutOrb");
        ItemList.add("CloudTotem");
        ItemList.add("WindCharge");
        ItemList.add("BoxingGlove");
        ItemList.add("PoisonOrb");


        ItemMeta boostim = BoostOrb.getItemMeta();
        boostim.setItemModel(new NamespacedKey("crystalized", "boost_orb"));
        boostim.displayName(Component.translatable("crystalized.orb.boost.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> boostlore = new ArrayList<>();
        boostlore.add(Component.translatable("crystalized.orb.boost.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        boostim.lore(boostlore);
        BoostOrb.setItemMeta(boostim);

        ItemMeta bridgeim = BridgeOrb.getItemMeta();
        bridgeim.setItemModel(new NamespacedKey("crystalized", "bridge_orb"));
        bridgeim.displayName(Component.translatable("crystalized.orb.bridge.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> bridgelore = new ArrayList<>();
        bridgelore.add(Component.translatable("crystalized.orb.bridge.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        bridgeim.lore(bridgelore);
        BridgeOrb.setItemMeta(bridgeim);

        ItemMeta explosiveim = ExplosiveOrb.getItemMeta();
        explosiveim.setItemModel(new NamespacedKey("crystalized", "explosive_orb"));
        explosiveim.displayName(Component.translatable("crystalized.orb.explosive.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> explosivelore = new ArrayList<>();
        explosivelore.add(Component.translatable("crystalized.orb.explosive.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        explosiveim.lore(explosivelore);
        ExplosiveOrb.setItemMeta(explosiveim);

        ItemMeta grapplingim = GrapplingOrb.getItemMeta();
        grapplingim.setItemModel(new NamespacedKey("crystalized", "grappling_orb"));
        grapplingim.displayName(Component.translatable("crystalized.orb.grappling.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> grapplinglore = new ArrayList<>();
        grapplinglore.add(Component.translatable("crystalized.orb.grappling.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        grapplingim.lore(grapplinglore);
        GrapplingOrb.setItemMeta(grapplingim);

        ItemMeta knockoutim = KnockoutOrb.getItemMeta();
        knockoutim.setItemModel(new NamespacedKey("crystalized", "knockout_orb"));
        knockoutim.displayName(Component.translatable("crystalized.orb.knockout.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> knockoutlore = new ArrayList<>();
        knockoutlore.add(Component.translatable("crystalized.orb.knockout.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        knockoutim.lore(knockoutlore);
        KnockoutOrb.setItemMeta(knockoutim);

        ItemMeta cloudtotemim = CloudTotem.getItemMeta();
        cloudtotemim.setItemModel(new NamespacedKey("crystalized", "cloud_totem"));
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
        BoxingGlove.setData(DataComponentTypes.MAX_DAMAGE, 3);

        ItemMeta wingedorb_im = WingedOrb.getItemMeta();
        wingedorb_im.customName(translatable("crystalized.orb.winged.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> wingedorblore = new ArrayList<>();
        wingedorblore.add(Component.translatable("crystalized.orb.winged.desc").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        wingedorb_im.lore(wingedorblore);
        wingedorb_im.setItemModel(new NamespacedKey("crystalized", "winged_orb"));
        WingedOrb.setItemMeta(wingedorb_im);

        ItemMeta poison_im = PoisonOrb.getItemMeta();
        poison_im.customName(translatable("crystalized.orb.poison.name").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE));
        List<Component> poisonlore = new ArrayList<>();
        List<Component> poisondesc = new ArrayList<>();
        poisondesc.add(text("3"));
        poisondesc.add(text("4"));
        poisonlore.add(Component.translatable("crystalized.orb.poison.desc", poisondesc).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GRAY));
        poison_im.lore(poisonlore);
        poison_im.setItemModel(new NamespacedKey("crystalized", "poison_orb"));
        PoisonOrb.setItemMeta(poison_im);
    }
}

class DropPowerup {
    public static void DropPowerup(Location loc, String powerup) {

        //Messy
        Item DroppedItem = loc.getWorld().spawn(loc, Item.class, entity -> {
            if (powerup.equals("BoostOrb")) {
                entity.setItemStack(KnockoffItem.BoostOrb);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.boost.name").color(TextColor.color(0x9215DE))));
            } else if (powerup.equals("BridgeOrb")) {
                entity.setItemStack(KnockoffItem.BridgeOrb);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.bridge.name").color(NamedTextColor.GRAY)));
            } else if (powerup.equals("ExplosiveOrb")) {
                entity.setItemStack(KnockoffItem.ExplosiveOrb);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.explosive.name").color(TextColor.color(0xFFB44D))));
            } else if (powerup.equals("GrapplingOrb")) {
                entity.setItemStack(KnockoffItem.GrapplingOrb);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.grappling.name").color(TextColor.color(0xDA662C))));
            } else if (powerup.equals("KnockoutOrb")) {
                entity.setItemStack(KnockoffItem.KnockoutOrb);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.knockout.name").color(TextColor.color(0xFFDB00))));
            } else if (powerup.equals("CloudTotem")) {
                entity.setItemStack(KnockoffItem.CloudTotem);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.totem.cloud.name").color(NamedTextColor.WHITE)));
            } else if (powerup.equals("WindCharge")) {
                entity.setItemStack(KnockoffItem.WindCharge);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("item.minecraft.wind_charge").color(NamedTextColor.WHITE)));
            } else if (powerup.equals("BoxingGlove")) {
                entity.setItemStack(KnockoffItem.BoxingGlove);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.item.boxingglove.name").color(NamedTextColor.GOLD)));
            } else if (powerup.equals("WingedOrb")) {
                entity.setItemStack(KnockoffItem.WingedOrb);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.winged.name").color(TextColor.color(0x4177ED))));
            } else if (powerup.equals("PoisonOrb")) {
                entity.setItemStack(KnockoffItem.PoisonOrb);
                entity.customName(text("Powerup! ").color(NamedTextColor.GOLD).append(Component.translatable("crystalized.orb.poison.name").color(TextColor.color(0x084C00))));
            }

            else {
                Bukkit.getServer().sendMessage(text("[!] An internal error occurred, check console."));
                Bukkit.getLogger().log(Level.WARNING, "[KNOCKOFFITEM] Unknown Item \"" + powerup + "\".");
            }
            entity.setGravity(true);
            entity.setGlowing(true);
            entity.setCanPlayerPickup(true);
            entity.setCustomNameVisible(false);
            entity.setInvisible(true);
        });
        TextDisplay DroppedItemName = loc.getWorld().spawn(DroppedItem.getLocation(), TextDisplay.class, entity -> {
            entity.setBillboard(Display.Billboard.CENTER);
            entity.text(DroppedItem.customName());
            entity.setSeeThrough(true);
        });
        TextColor yellow = NamedTextColor.YELLOW;
        TextDisplay DroppedItemDesc = loc.getWorld().spawn(DroppedItem.getLocation(), TextDisplay.class, entity -> {
            entity.setBillboard(Display.Billboard.CENTER);
            entity.setSeeThrough(true);
            switch (powerup) {
                case "BoostOrb" -> {
                    entity.text(translatable("crystalized.game.knockoff.minidescs.boostorb").color(yellow));
                }
                case "BridgeOrb" -> {
                    entity.text(translatable("crystalized.game.knockoff.minidescs.bridgeorb").color(yellow));
                }
                case "ExplosiveOrb" -> {
                    entity.text(translatable("crystalized.game.knockoff.minidescs.explosiveorb").color(yellow));
                }
                case "GrapplingOrb" -> {
                    entity.text(translatable("crystalized.game.knockoff.minidescs.grappleorb").color(yellow));
                }
                case "KnockoutOrb" -> {
                    entity.text(translatable("crystalized.game.knockoff.minidescs.knockoutorb").color(yellow));
                }
                case "CloudTotem" -> {
                    entity.text(translatable("crystalized.game.knockoff.minidescs.cloudtotem").color(yellow));
                }
                case "WindCharge" -> {
                    entity.text(translatable("crystalized.game.knockoff.minidescs.windcharge").color(yellow));
                }
                case "BoxingGlove" -> {
                    entity.text(translatable("crystalized.game.knockoff.minidescs.boxingglove").color(yellow));
                }
                case "WingedOrb" -> {
                    entity.text(translatable("crystalized.game.knockoff.minidescs.wingedorb").color(yellow));
                }
                case "PoisonOrb" -> {
                    entity.text(translatable("crystalized.game.knockoff.minidescs.poisonorb").color(yellow));
                }
                default -> {
                    entity.text(text("This item has no mini description! Report this pls"));
                }
            }
        });

        DroppedItem.teleport(loc);
        new BukkitRunnable() {
            int timer = knockoff.getInstance().getRandomNumber(31, 40) * 20;
            public void run() {
                DroppedItem.setGravity(false);
                if (DroppedItem.isDead() || knockoff.getInstance().GameManager == null || timer == 0) {
                    DroppedItem.remove();
                    DroppedItemName.remove();
                    DroppedItemDesc.remove();
                    cancel();
                }

                //Prob not the best way of doing this but adding it as a passenger doesn't seem to work
                Location Nameloc = new Location(
                        DroppedItem.getLocation().getWorld(),
                        DroppedItem.getLocation().getX(),
                        DroppedItem.getLocation().getY() + 1,
                        DroppedItem.getLocation().getZ()
                        );
                Location Descloc = new Location(
                        DroppedItem.getLocation().getWorld(),
                        DroppedItem.getLocation().getX(),
                        DroppedItem.getLocation().getY() + 0.75,
                        DroppedItem.getLocation().getZ()
                );
                DroppedItemName.teleportAsync(Nameloc);
                DroppedItemDesc.teleportAsync(Descloc);
                DroppedItem.teleportAsync(loc);
                timer--;
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 1);


        new BukkitRunnable() {
            int timer = 1;
            public void run() {
                if (DroppedItem.isDead()) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.stopSound("minecraft:block.beacon.ambient");
                    }
                    cancel();
                }
                if (timer == 0) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(DroppedItem.getLocation(), "minecraft:block.beacon.ambient", 2.5F, 1);
                    }
                    timer = 5 * 20;
                }
                timer--;
            }
        }.runTaskTimer(knockoff.getInstance(), 0, 1);
    }
}
