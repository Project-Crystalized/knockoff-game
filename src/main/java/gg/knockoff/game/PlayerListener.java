package gg.knockoff.game;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.BreezeWindCharge;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.geysermc.floodgate.api.FloodgateApi;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class PlayerListener implements Listener {

	@EventHandler
	public void PlayerDisconnectMessage(PlayerQuitEvent event) {
		event.quitMessage(Component.text(""));
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		FloodgateApi floodgateapi = FloodgateApi.getInstance();
		event.joinMessage(Component.text(""));

		if (knockoff.getInstance().GameManager == null) {
			player.teleport(knockoff.getInstance().mapdata.get_que_spawn(player.getWorld()));
			player.getInventory().clear();
			player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
			player.setHealth(20);
			player.setFoodLevel(20);
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			player.setGameMode(GameMode.ADVENTURE);
			player.setExp(0);
			player.setLevel(0);
			player.removePotionEffect(PotionEffectType.REGENERATION);
			player.removePotionEffect(PotionEffectType.HUNGER);
			player.removePotionEffect(PotionEffectType.RESISTANCE);
			player.removePotionEffect(PotionEffectType.JUMP_BOOST); // Should remove slime time if you somehow still have it
			player.removePotionEffect(PotionEffectType.POISON); // Removes Poisonous Bushes' effect
			player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 1, false, false, true));
			player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 255, false, false, false));
			player.sendPlayerListHeaderAndFooter(
					// Header
					text("\n")
							.append(text("Crystalized: ").color(NamedTextColor.LIGHT_PURPLE)
									.append(text("Knockoff").color(NamedTextColor.GOLD)))
							.append(text("\n")),

					// Footer
					text(
							"\nIf you find any bugs please report to TotallyNoCallum on the Crystalized Discord")
							.append(text("\n https://github.com/Project-Crystalized ").color(NamedTextColor.GRAY)));
			new QueueScoreBoard(player);

			ItemStack leavebutton = new ItemStack(Material.COAL, 1);
			ItemMeta leavebuttonim = leavebutton.getItemMeta();
			leavebuttonim.setItemModel(new NamespacedKey("crystalized", "ui/leave"));
			leavebuttonim.displayName(Component.text("Return to lobby").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
			leavebutton.setItemMeta(leavebuttonim);
			player.getInventory().setItem(8, leavebutton);

			if (floodgateapi.isFloodgatePlayer(player.getUniqueId())) {
				player.sendMessage(text("-".repeat(40)));
			} else {
				player.sendMessage(text(" ".repeat(55)).decoration(TextDecoration.STRIKETHROUGH,  true));
			}
			player.sendMessage(
					text("\n")
							.append(translatable("crystalized.game.knockoff.name").color(NamedTextColor.GOLD).append(text(" \uE12E").color(NamedTextColor.WHITE)))
							.append(text("\nKnock enemies into the void until they run out of lives, but be careful, you must avoid hazards along the way.").color(NamedTextColor.GRAY)) //TODO make this translatable
							.append(text("\n"))
			);
			if (floodgateapi.isFloodgatePlayer(player.getUniqueId())) {
				player.sendMessage(text("-".repeat(40)));
			} else {
				player.sendMessage(text(" ".repeat(55)).decoration(TextDecoration.STRIKETHROUGH,  true));
			}

		} else {
			player.kick(Component.text("A game is currently is progress, try joining again later.").color(NamedTextColor.RED));
		}
	}

	@EventHandler
	public void onHunger(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getPlayer();
		PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
		event.setCancelled(true);
		if (player.getGameMode().equals(GameMode.SPECTATOR)) {
			return;
		}
		player.setGameMode(GameMode.SPECTATOR);
		if (player.getKiller() == null) {
			Bukkit.getServer().sendMessage(text("[\uE103] ")
					.append(player.displayName())
					.append(translatable("crystalized.game.knockoff.chat.deathgeneric")));
		} else {
			Bukkit.getServer().sendMessage(text("[\uE103] ")
					.append(player.displayName())
					.append(translatable("crystalized.game.knockoff.chat.deathknockoff"))
					.append(player.getKiller().displayName()));
			Player attacker = player.getKiller();
			PlayerData pda = knockoff.getInstance().GameManager.getPlayerData(attacker);
			pda.addKill(1);
			attacker.showTitle(Title.title(text(" "), text("[\uE103] ").append(player.displayName()),
					Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(1), Duration.ofMillis(250))));
			attacker.playSound(attacker, "crystalized:effect.ally_kill", 50, 1);
			for (Player p : Bukkit.getOnlinePlayers()) {
				//p.playSound(p, "minecraft:block.anvil.place", 0.5F, 0.5f); //commented out since this is kinda annoying - Callum
				p.playSound(player.getLocation(), "minecraft:entity.firework_rocket.blast_far", 4, 1); //TODO make actual firework
			}
		}
		pd.addDeath(1);
		pd.isPlayerDead = true;

		// Next best thing to delaying a task ig
		new BukkitRunnable() {
			@Override
			public void run() {
				Location loc = new Location(Bukkit.getWorld("world"), knockoff.getInstance().mapdata.getCurrentMiddleXLength(),
						knockoff.getInstance().mapdata.getCurrentMiddleYLength() + 10,
						knockoff.getInstance().mapdata.getCurrentMiddleZLength());
				player.teleportAsync(loc);
				cancel();
			}
		}.runTaskTimer(knockoff.getInstance(), 2, 20);

		pd.takeawayLife(1);
		if (pd.getLives() > 0) {
			player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(pd.getLives() * 2);

			switch (pd.getLives()) {
				case 4 -> {pd.setDeathtimer(4);} //4
				case 3 -> {pd.setDeathtimer(8);} //8
				case 2 -> {pd.setDeathtimer(10);} //10
				case 1 -> {pd.setDeathtimer(12);} //12
				default -> {pd.setDeathtimer(4);} //4
			}

			new BukkitRunnable() {
				public void run() {
					player.sendActionBar(translatable("crystalized.game.knockoff.respawn1")
							.append(Component.text(pd.getDeathtimer()))
							.append(translatable("crystalized.game.knockoff.respawn2")));
					switch (pd.getDeathtimer()) {
						case 3 -> {
							player.playSound(player, "crystalized:effect.knockoff_countdown", 50, 1);
						}
						case 2 -> {
							player.playSound(player, "crystalized:effect.knockoff_countdown", 50, 1.25F);
						}
						case 1 -> {
							player.playSound(player, "crystalized:effect.knockoff_countdown", 50, 1.5F);
						}
						case 0 -> {
							player.playSound(player, "crystalized:effect.knockoff_countdown", 50, 2);
							if (GameManager.GameState.equals("game")) {
								tpPlayersBack(player);
								player.setGameMode(GameMode.SURVIVAL);
								pd.setDeathtimer(0);
								pd.isPlayerDead = false;
								CustomPlayerNametags.CustomPlayerNametags(player);
							}
							cancel();
						}
					}
					if (!pd.isPlayerDead) {
						cancel();
					}
					pd.setDeathtimer(pd.getDeathtimer() - 1);
				}
			}.runTaskTimer(knockoff.getInstance(), 1, 20);
		} else {
			if (pd.getLives() < 0) {
				// we kick the player if their lives is less than 0. To prevent cheating and to
				// possibly catch bugs where players may die twice
				player.kick(Component.text(
						"You're eliminated from the game but you have somehow died again. and/or your lives is measured in negative numbers! Please report this bug to the Crystalized devs.")
						.color(NamedTextColor.RED));
			}
			player.playSound(player, "crystalized:effect.kill_streak_5", 1, 1);
			Bukkit.getServer().sendMessage(text("[")
					.append(Component.text("\uE103").color(NamedTextColor.RED))
					.append(Component.text("] "))
					.append(player.displayName())
					.append(translatable("crystalized.game.knockoff.chat.eliminated")));
			pd.isPlayerDead = true;
			pd.isEliminated = true;
		}
	}

	@EventHandler
	public void onChat(AsyncChatEvent event) {
		Player player = event.getPlayer();
		event.setCancelled(true);
		Bukkit.getServer().sendMessage(Component.text("")
				.append(player.displayName())
				.append(Component.text(": "))
				.append(event.message()));
	}

	private static void tpPlayersBack(Player p) {

		Location middleLoc = new Location(Bukkit.getWorld("world"),
				knockoff.getInstance().getRandomNumber(GameManager.SectionPlaceLocationX, knockoff.getInstance().mapdata.getCurrentXLength()) + 0.5,
				knockoff.getInstance().mapdata.getCurrentMiddleYLength() + knockoff.getInstance().getRandomNumber(5, 8), // TODO temp
				knockoff.getInstance().getRandomNumber(GameManager.SectionPlaceLocationZ, knockoff.getInstance().mapdata.getCurrentZLength()) + 0.5);
		Location ploc = new Location(Bukkit.getWorld("world"), middleLoc.getX(), middleLoc.getY() + 2, middleLoc.getZ());
		if (knockoff.getInstance().GameManager == null || p == null) {
			return;
		}
		List<Block> tempBlockList = new ArrayList<>();
		tempBlockList.add(middleLoc.getBlock());
		tempBlockList.add(middleLoc.clone().add(1, 0, 0).getBlock());
		tempBlockList.add(middleLoc.clone().add(-1, 0, 0).getBlock());
		tempBlockList.add(middleLoc.clone().add(0, 0, 1).getBlock());
		tempBlockList.add(middleLoc.clone().add(1, 0, 1).getBlock());
		tempBlockList.add(middleLoc.clone().add(-1, 0, 1).getBlock());
		tempBlockList.add(middleLoc.clone().add(0, 0, -1).getBlock());
		tempBlockList.add(middleLoc.clone().add(1, 0, -1).getBlock());
		tempBlockList.add(middleLoc.clone().add(-1, 0, -1).getBlock());
		//TODO how do we make this better
		switch (Teams.GetPlayerTeam(p)) {
			case "blue" -> {
				for (Block b : tempBlockList) {
					b.setType(Material.WHITE_GLAZED_TERRACOTTA);
					Directional dir = (Directional) b.getBlockData();
					dir.setFacing(BlockFace.EAST);
					b.setBlockData(dir);
				}
			}
			case "cyan" -> {
				for (Block b : tempBlockList) {
					b.setType(Material.WHITE_GLAZED_TERRACOTTA);
					Directional dir = (Directional) b.getBlockData();
					dir.setFacing(BlockFace.NORTH);
					b.setBlockData(dir);
				}
			}
			case "green" -> {
				for (Block b : tempBlockList) {
					b.setType(Material.WHITE_GLAZED_TERRACOTTA);
					Directional dir = (Directional) b.getBlockData();
					dir.setFacing(BlockFace.SOUTH);
					b.setBlockData(dir);
				}
			}
			case "lemon" -> {
				for (Block b : tempBlockList) {
					b.setType(Material.WHITE_GLAZED_TERRACOTTA);
					Directional dir = (Directional) b.getBlockData();
					dir.setFacing(BlockFace.WEST);
					b.setBlockData(dir);
				}
			}
			case "lime" -> {
				for (Block b : tempBlockList) {
					b.setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
					Directional dir = (Directional) b.getBlockData();
					dir.setFacing(BlockFace.EAST);
					b.setBlockData(dir);
				}
			}
			case "magenta" -> {
				for (Block b : tempBlockList) {
					b.setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
					Directional dir = (Directional) b.getBlockData();
					dir.setFacing(BlockFace.NORTH);
					b.setBlockData(dir);
				}
			}
			case "orange" -> {
				for (Block b : tempBlockList) {
					b.setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
					Directional dir = (Directional) b.getBlockData();
					dir.setFacing(BlockFace.SOUTH);
					b.setBlockData(dir);
				}
			}
			case "peach" -> {
				for (Block b : tempBlockList) {
					b.setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
					Directional dir = (Directional) b.getBlockData();
					dir.setFacing(BlockFace.WEST);
					b.setBlockData(dir);
				}
			}
			case "purple" -> {
				for (Block b : tempBlockList) {
					b.setType(Material.GRAY_GLAZED_TERRACOTTA);
					Directional dir = (Directional) b.getBlockData();
					dir.setFacing(BlockFace.EAST);
					b.setBlockData(dir);
				}
			}
			case "red" -> {
				for (Block b : tempBlockList) {
					b.setType(Material.GRAY_GLAZED_TERRACOTTA);
					Directional dir = (Directional) b.getBlockData();
					dir.setFacing(BlockFace.NORTH);
					b.setBlockData(dir);
				}
			}
			case "white" -> {
				for (Block b : tempBlockList) {
					b.setType(Material.GRAY_GLAZED_TERRACOTTA);
					Directional dir = (Directional) b.getBlockData();
					dir.setFacing(BlockFace.SOUTH);
					b.setBlockData(dir);
				}
			}
			case "yellow" -> {
				for (Block b : tempBlockList) {
					b.setType(Material.GRAY_GLAZED_TERRACOTTA);
					Directional dir = (Directional) b.getBlockData();
					dir.setFacing(BlockFace.WEST);
					b.setBlockData(dir);
				}
			}
			default -> {
				for (Block b : tempBlockList) {
					b.setType(Material.AMETHYST_BLOCK);
				}
			}
		}
		for (Block b : tempBlockList) {
			GameManager.startBreakingCrystal(b, 4 * 20, knockoff.getInstance().getRandomNumber(20, 30), false);
		}

		PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
		if (pd.lives == 1) {
			p.sendMessage(text("You have one life remaining and will not respawn when you die!").color(NamedTextColor.RED)); //TODO translatable
			p.playSound(p, "minecraft:block.note_block.pling", 1, 0.5f);
		}

		middleLoc.getBlock().getState().update();
		p.teleport(ploc);
		p.lookAt(knockoff.getInstance().mapdata.getCurrentMiddleXLength(),
				knockoff.getInstance().mapdata.getCurrentMiddleYLength(),
				knockoff.getInstance().mapdata.getCurrentMiddleZLength(), LookAnchor.EYES);
	}

	@EventHandler
	public void PlayerDropItem(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void OnPlayerDisconnect(PlayerConnectionCloseEvent event) {
		if (knockoff.getInstance().GameManager != null) {
			Teams.DisconnectPlayer(event.getPlayerName());
		}
		if (knockoff.getInstance().GameManager != null && Bukkit.getOnlinePlayers().isEmpty()) {
			Bukkit.getLogger().log(Level.WARNING, "[!] All players have disconnected. The Game will now end.");
			knockoff.getInstance().GameManager.ForceEndGame();
		}
	}

	@EventHandler
	public void OnPlayerPickupItem(EntityPickupItemEvent event) {
		Player player = (Player) event.getEntity();
		PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
		pd.powerupscollected++;
		List<Component> component = new ArrayList<>();
		component.add(player.displayName());
		Bukkit.getServer().sendMessage(Component.text("[!] ")
				.append(translatable("crystalized.game.knockoff.chat.pickedup", component))
				.append(event.getItem().getItemStack().effectiveName()));
	}

	@EventHandler
	public void OnPlayerItemInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (knockoff.getInstance().GameManager != null) {
			PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
			if (event.getHand() != EquipmentSlot.HAND || event.getItem() == null) {
				return;
			}
			if (!event.getAction().isLeftClick()) {
				ItemMeta im = event.getItem().getItemMeta();
				if (im == null) {return;} //temporary fix to this not counting anything based on Material.COAL
				NamespacedKey item_model = im.getItemModel();
				if ((event.getItem().getType() == Material.COAL && im.hasItemModel() ||
						(event.getItem().getType() == Material.WIND_CHARGE))) {
					if (event.getItem().getType() == Material.COAL && !(item_model.getKey().toLowerCase().contains("orb") || (item_model.getKey().toLowerCase().contains("totem")))) {
						return;
					}
					new BukkitRunnable() {
						@Override
						public void run() {
							if (player.hasCooldown(Material.COAL) || player.hasCooldown(Material.WIND_CHARGE)) {
								pd.powerupsused++;
								if (knockoff.getInstance().DevMode) {
									Bukkit.getServer().sendMessage(Component.text("[DEBUG] ")
											.append(player.displayName())
											.append(Component.text(" has used a powerup")));
								}

							}
							cancel();
						}
					}.runTaskTimer(knockoff.getInstance(), 2, 1);
				}
			}
		} else {
			if (event.getHand() != EquipmentSlot.HAND || event.getItem() == null)
				return;
			if (event.getAction().isRightClick()) {
				if (event.getItem().getType().equals(Material.COAL)) {
					ItemMeta im = event.getItem().getItemMeta();
					if (im.hasItemModel() && im.getItemModel().getKey().equals("ui/leave")) {
						player.kick();
					}
				}
			}
		}
	}

	@EventHandler
	public void OnInventoryMoveItem(InventoryClickEvent event) {
		if (knockoff.getInstance().GameManager == null) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityExplosion(EntityExplodeEvent e) {
		if ((e.getEntity() instanceof WindCharge || e.getEntity() instanceof BreezeWindCharge) && !knockoff.getInstance().getConfig().getBoolean("other.explosive_wind_charges")) {
			return;
		}
		e.setCancelled(true);
		if (knockoff.getInstance().GameManager == null) {return;}
		e.getLocation().createExplosion(null, 1.5F, false, false);
		for (Block b : e.blockList()) {
			knockoff.getInstance().GameManager.startBreakingCrystal(b, knockoff.getInstance().getRandomNumber(0, 4), knockoff.getInstance().getRandomNumber(11, 16), true);
		}
	}

    @EventHandler
    public void onWaterFlow(FluidLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockUpdate(BlockFromToEvent e) {
        e.setCancelled(true);
    }
}
