package gg.knockoff.game;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import net.kyori.adventure.text.Component;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (knockoff.getInstance().GameManager == null) {
            GameManager gameManager = knockoff.getInstance().GameManager;

            player.teleport(knockoff.getInstance().mapdata.get_que_spawn(player.getWorld()));
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            player.setGameMode(GameMode.ADVENTURE);
        } else {
            player.kick(Component.text("A game is currently is progress, try joining again later.").color(NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        // if game isn't going, cancel event
        GameManager gc = knockoff.getInstance().GameManager;
        if (gc == null) {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        } //TODO make lives system, when a player dies they lose 1 live, when they lose all lives they're permanently put in spectator for the rest of the game until it resets
        player.playSound(player, "minecraft:item.armor.equip_elytra", 50, 1);
    }
}
