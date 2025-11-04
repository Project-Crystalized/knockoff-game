package gg.knockoff.game;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import gg.crystalized.lobby.Lobby_plugin;
import gg.crystalized.lobby.Ranks;
import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.event.block.VaultChangeStateEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Vault;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.geysermc.floodgate.api.FloodgateApi;

import java.time.Duration;
import java.util.*;
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
		Player p = event.getPlayer();
		FloodgateApi floodgateapi = FloodgateApi.getInstance();
		event.joinMessage(Component.text(""));

		if (knockoff.getInstance().GameManager == null) {
			p.teleport(knockoff.getInstance().mapdata.get_que_spawn(p.getWorld()));
			p.getInventory().clear();
			p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
			p.setHealth(20);
			p.setFoodLevel(20);
			p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			p.setGameMode(GameMode.ADVENTURE);
			p.setExp(0);
			p.setLevel(0);
			p.removePotionEffect(PotionEffectType.REGENERATION);
			p.removePotionEffect(PotionEffectType.HUNGER);
			p.removePotionEffect(PotionEffectType.RESISTANCE);
			p.removePotionEffect(PotionEffectType.JUMP_BOOST); // Should remove slime time if you somehow still have it
			p.removePotionEffect(PotionEffectType.POISON); // Removes Poisonous Bushes' effect
			p.removePotionEffect(PotionEffectType.SLOWNESS);
			p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 1, false, false, true));
			p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 255, false, false, false));
			p.sendPlayerListHeaderAndFooter(
					// Header
					text("\n")
							.append(text("Crystalized: ").color(NamedTextColor.LIGHT_PURPLE)
									.append(text("Knockoff").color(NamedTextColor.GOLD)))
							.append(text("\n")),

					// Footer
					text(
							"\nIf you find any bugs please report to TotallyNoCallum on the Crystalized Discord")
							.append(text("\n https://github.com/Project-Crystalized ").color(NamedTextColor.GRAY)));
			new QueueScoreBoard(p);

			ItemStack leavebutton = new ItemStack(Material.COAL, 1);
			ItemMeta leavebuttonim = leavebutton.getItemMeta();
			leavebuttonim.setItemModel(new NamespacedKey("crystalized", "ui/leave"));
			leavebuttonim.displayName(Component.text("Return to lobby").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
			leavebutton.setItemMeta(leavebuttonim);
			p.getInventory().setItem(8, leavebutton);

			if (floodgateapi.isFloodgatePlayer(p.getUniqueId())) {
				p.sendMessage(text("-".repeat(40)));
			} else {
				p.sendMessage(text(" ".repeat(55)).decoration(TextDecoration.STRIKETHROUGH,  true));
			}
			p.sendMessage(
					text("\n")
							.append(translatable("crystalized.game.knockoff.name").color(NamedTextColor.GOLD).append(text(" \uE12E").color(NamedTextColor.WHITE)))
							.append(text("\n").append(translatable("crystalized.game.knockoff.chat.tutorial").color(NamedTextColor.GRAY)))
							.append(text("\n"))
			);
			if (floodgateapi.isFloodgatePlayer(p.getUniqueId())) {
				p.sendMessage(text("-".repeat(40)));
			} else {
				p.sendMessage(text(" ".repeat(55)).decoration(TextDecoration.STRIKETHROUGH,  true));
			}

            new BukkitRunnable() {
                int padCooldown = 0; //in ticks
                public void run() {
                    if (!p.isOnline()) {
                        cancel();
                    }

                    //launch/effect pads
                    if (padCooldown == 0) {
                        if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
                            Block block_under = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
                            switch (block_under.getType()) {
                                case Material.COPPER_BLOCK -> {
                                    p.playSound(p, "crystalized:effect.hazard_positive", 1, 1);
                                    p.setVelocity(p.getLocation().getDirection().multiply(1.5));
                                    padCooldown = 20;
                                }
                                case Material.CHISELED_COPPER -> {
                                    p.playSound(p, "crystalized:effect.hazard_positive", 1, 1);
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, (20), 7));
                                    padCooldown = 20;
                                }
                                case Material.CUT_COPPER -> {
                                    p.playSound(p, "crystalized:effect.hazard_positive", 1, 1);
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 6));
                                    padCooldown = 35;
                                }
                            }
                        }
                    } else {
                        padCooldown--;
                    }
                }
            }.runTaskTimer(knockoff.getInstance(), 1, 1);
		} else {
			p.kick(Component.text("A game is currently is progress, try joining again later.").color(NamedTextColor.RED));
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
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (Teams.GetPlayerTeam(p).equals(Teams.GetPlayerTeam(player)) && !player.equals(p)) {
				p.playSound(p, "crystalized:effect.ally_kill", 50, 1);
			}
		}
		player.setGameMode(GameMode.SPECTATOR);
		if (player.getKiller() == null) {
			Bukkit.getServer().sendMessage(text("[\uE103] ")
					.append(pd.cachedRankIcon_small)
					.append(text(" "))
					.append(player.displayName())
					.append(translatable("crystalized.game.knockoff.chat.deathgeneric")));
		} else {
			Bukkit.getServer().sendMessage(text("[\uE103] ")
					.append(pd.cachedRankIcon_small)
					.append(text(" "))
					.append(player.displayName())
					.append(translatable("crystalized.game.knockoff.chat.deathknockoff"))
					.append(player.getKiller().displayName()));
			Player attacker = player.getKiller();
			PlayerData pda = knockoff.getInstance().GameManager.getPlayerData(attacker);
			pda.addKill(1);
			attacker.showTitle(Title.title(text(" "), text("[\uE103] ").append(player.displayName()),
					Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(1), Duration.ofMillis(250))));
			attacker.playSound(attacker, "crystalized:effect.enemy_kill", 50, 1);
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
					.append(pd.cachedRankIcon_small)
					.append(text(" "))
					.append(player.displayName())
					.append(translatable("crystalized.game.knockoff.chat.eliminated")));
			pd.isPlayerDead = true;
			pd.isEliminated = true;
		}
	}

	@EventHandler
	public void onChat(AsyncChatEvent event) {
		Player p = event.getPlayer();
		event.setCancelled(true);
		//this is dumb
		if (knockoff.getInstance().GameManager == null) {
			Bukkit.getServer().sendMessage(Ranks.getName(Bukkit.getOfflinePlayer(p.getName()))
					.append(Component.text(": "))
					.append(event.message()));
		} else {
			PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
			Bukkit.getServer().sendMessage(pd.cachedRankIcon_small
					.append(text(" "))
					.append(p.displayName())
					.append(Component.text(": "))
					.append(event.message()));
		}
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

        for (Block b : tempBlockList) {
            switch (Teams.GetPlayerTeam(p)) {
                case "blue", "cyan", "green", "lemon" -> {
                    b.setType(Material.WHITE_GLAZED_TERRACOTTA);
                }
                case "lime", "magenta", "orange", "peach" -> {
                    b.setType(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
                }
                case "purple", "white", "yellow", "red" -> {
                    b.setType(Material.GRAY_GLAZED_TERRACOTTA);
                }
                case "weak", "strong" -> {
                    b.setType(Material.BLACK_GLAZED_TERRACOTTA);
                }
            }
            Directional dir = (Directional) b.getBlockData();

            //set direction to match the item model's model
            switch (Teams.GetPlayerTeam(p)) {
                case "blue", "lime", "purple", "weak" -> {
                    dir.setFacing(BlockFace.EAST);
                }
                case "cyan", "magenta", "red", "strong" -> {
                    dir.setFacing(BlockFace.NORTH);
                }
                case "green", "orange", "white" -> {
                    dir.setFacing(BlockFace.SOUTH);
                }
                case "lemon", "peach", "yellow" -> {
                    dir.setFacing(BlockFace.WEST);
                }
            }

            b.setBlockData(dir);
            b.getState().update();
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
				knockoff.getInstance().mapdata.getCurrentMiddleZLength(), LookAnchor.EYES
        );
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
            if (knockoff.getInstance().GameManager.GameState != "end") {
                knockoff.getInstance().GameManager.ForceEndGame();
            }
		}
	}

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        LivingEntity en = e.getEntity();
        if (en instanceof Player) {return;}
        if (en instanceof Breeze b) {
            e.getDrops().clear();
            if (b.getAttribute(Attribute.MAX_HEALTH).getBaseValue() == 4.0) { //dumb workaround, but this is for the trial key drop for the trial chamber hazard
                if (!(b.getLocation().getY() < -20.0)) {
                    //DropPowerup.DropPowerup(b.getLocation(), "TrialChamberHazardKey");
                    Entity entity = e.getDamageSource().getCausingEntity();
                    if (entity != null) {
                        entity.sendMessage(text("[!] You killed the Big Breeze, A trial key has been dropped"));
                    }
                }
            } else {
                ItemStack item = KnockoffItem.WindCharge.clone();
                item.setAmount(1);
                e.getDrops().add(item);
            }
        }

    }

	@EventHandler
	public void OnPlayerPickupItem(EntityPickupItemEvent event) {
		Player player = (Player) event.getEntity();
		PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
		pd.powerupscollected++;
		List<Component> component = new ArrayList<>();
		if (pd.cachedRankIcon_full.equals(text(""))) {
			component.add(player.displayName());
		} else {
			component.add(pd.cachedRankIcon_small.append(text(" ")).append(player.displayName()));
		}
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
        Component text = e.getEntity().customName();

        if (text == null) {
            for (Block b : e.blockList()) {
                knockoff.getInstance().GameManager.startBreakingCrystal(b, knockoff.getInstance().getRandomNumber(0, 4), knockoff.getInstance().getRandomNumber(11, 16), true);
            }
        } else if (text.equals(text("magma"))) {
            for (Block b : e.blockList()) {
                if (!b.getType().equals(Material.RESIN_BLOCK)) {
                    b.setType(Material.MAGMA_BLOCK);
                }
            }
            //randomly spawn explosive orb for elementals eurption
            switch (knockoff.getInstance().getRandomNumber(1, 8)) {
                case 5 -> {
                    KnockoffItem.DropPowerup(e.getLocation(), "ExplosiveOrb");
                }
            }
        }
	}

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent e) {
        if (knockoff.getInstance().GameManager == null) {return;}
        Entity entity = e.getEntity();
        if (entity instanceof Snowball s) {
            Component text = s.customName();
            if (text == null) {return;}
            if (text.equals(text("magma"))) {
                //create fireball for big explosion turning into magma blocks
                Bukkit.getWorld("world").spawn(s.getLocation(), Fireball.class, fireball -> {
                    fireball.customName(text);
                    fireball.setYield(6);
                    fireball.setVelocity(new Vector(0, -10, 0));
                    fireball.setCustomNameVisible(false);
                });
            }
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

    @EventHandler
    public void onVaultUnlock(VaultChangeStateEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock().getLocation().getBlock();
		Vault data = (Vault) b.getBlockData();
        if (e.getNewState().equals(org.bukkit.block.data.type.Vault.State.EJECTING)) {
			List<String> powerups;
			e.setCancelled(true);
			b.setType(Material.AMETHYST_BLOCK);
			if (data.isOminous()) {
				powerups = Arrays.asList("KnockoutOrb", "ExplosiveOrb", "PoisonOrb", "TrialChamberMace");
			} else {
				powerups = Arrays.asList("WindCharge", "BoostOrb", "WingedOrb");
			}
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(b.getLocation(), "crystalized:effect.nexus_crystal_destroyed", 1, 1.5F);
            }
            Collections.shuffle(powerups);
            KnockoffItem.DropPowerup(b.getLocation().clone().add(0.5, 1, 0.5), powerups.get(knockoff.getInstance().getRandomNumber(0, powerups.size())));
        }
    }
}
