package gg.knockoff.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.text;

public class Teams {

	public static List<String> spectator = new ArrayList<>();
	public static List<String> blue = new ArrayList<>();
	public static List<String> cyan = new ArrayList<>();
	public static List<String> green = new ArrayList<>();
	public static List<String> lemon = new ArrayList<>();
	public static List<String> lime = new ArrayList<>();
	public static List<String> magenta = new ArrayList<>();
	public static List<String> orange = new ArrayList<>();
	public static List<String> peach = new ArrayList<>();
	public static List<String> purple = new ArrayList<>();
	public static List<String> red = new ArrayList<>();
	public static List<String> white = new ArrayList<>();
	public static List<String> yellow = new ArrayList<>();

	public static final List<TeamData> team_datas = TeamData.create_teams();

	public Teams(GameManager.GameTypes type) {
		List<String> playerlist = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			playerlist.add(p.getName());
		}
		Collections.shuffle(playerlist);
		spectator.clear();
		blue.clear();
		cyan.clear();
		green.clear();
		lemon.clear();
		lime.clear();
		magenta.clear();
		orange.clear();
		peach.clear();
		purple.clear();
		red.clear();
		yellow.clear();
		white.clear();

		//We need to revisit this at some point
		if (type.equals(GameManager.GameTypes.Custom)) {

			FileConfiguration config = knockoff.getInstance().getConfig();
			Object[] config_spectator = config.getList("teams.spectator").toArray();
			Object[] config_blue = config.getList("teams.blue").toArray();
			Object[] config_cyan = config.getList("teams.cyan").toArray();
			Object[] config_green = config.getList("teams.green").toArray();
			Object[] config_lemon = config.getList("teams.lemon").toArray();
			Object[] config_lime = config.getList("teams.lime").toArray();
			Object[] config_magenta = config.getList("teams.magenta").toArray();
			Object[] config_orange = config.getList("teams.orange").toArray();
			Object[] config_peach = config.getList("teams.peach").toArray();
			Object[] config_purple = config.getList("teams.purple").toArray();
			Object[] config_red = config.getList("teams.red").toArray();
			Object[] config_yellow = config.getList("teams.yellow").toArray();
			Object[] config_white = config.getList("teams.white").toArray();
			for (Object o : config_spectator) {
				String s = (String) o;
				Player p = Bukkit.getPlayer(s);
				if (p == null) {
					knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
				} else {
					spectator.add(Bukkit.getPlayer(s).getName());
				}
			}
			for (Object o : config_blue) {
				String s = (String) o;
				Player p = Bukkit.getPlayer(s);
				if (p == null) {
					knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
				} else {
					blue.add(Bukkit.getPlayer(s).getName());
				}
			}
			for (Object o : config_cyan) {
				String s = (String) o;
				Player p = Bukkit.getPlayer(s);
				if (p == null) {
					knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
				} else {
					cyan.add(Bukkit.getPlayer(s).getName());
				}
			}
			for (Object o : config_green) {
				String s = (String) o;
				Player p = Bukkit.getPlayer(s);
				if (p == null) {
					knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
				} else {
					green.add(Bukkit.getPlayer(s).getName());
				}
			}
			for (Object o : config_lemon) {
				String s = (String) o;
				Player p = Bukkit.getPlayer(s);
				if (p == null) {
					knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
				} else {
					lemon.add(Bukkit.getPlayer(s).getName());
				}
			}
			for (Object o : config_lime) {
				String s = (String) o;
				Player p = Bukkit.getPlayer(s);
				if (p == null) {
					knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
				} else {
					lime.add(Bukkit.getPlayer(s).getName());
				}
			}
			for (Object o : config_magenta) {
				String s = (String) o;
				Player p = Bukkit.getPlayer(s);
				if (p == null) {
					knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
				} else {
					magenta.add(Bukkit.getPlayer(s).getName());
				}
			}
			for (Object o : config_orange) {
				String s = (String) o;
				Player p = Bukkit.getPlayer(s);
				if (p == null) {
					knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
				} else {
					orange.add(Bukkit.getPlayer(s).getName());
				}
			}
			for (Object o : config_peach) {
				String s = (String) o;
				Player p = Bukkit.getPlayer(s);
				if (p == null) {
					knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
				} else {
					peach.add(Bukkit.getPlayer(s).getName());
				}
			}
			for (Object o : config_purple) {
				String s = (String) o;
				Player p = Bukkit.getPlayer(s);
				if (p == null) {
					knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
				} else {
					purple.add(Bukkit.getPlayer(s).getName());
				}
			}
			for (Object o : config_red) {
				String s = (String) o;
				Player p = Bukkit.getPlayer(s);
				if (p == null) {
					knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
				} else {
					red.add(Bukkit.getPlayer(s).getName());
				}
			}
			for (Object o : config_yellow) {
				String s = (String) o;
				Player p = Bukkit.getPlayer(s);
				if (p == null) {
					knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
				} else {
					yellow.add(Bukkit.getPlayer(s).getName());
				}
			}
			for (Object o : config_white) {
				String s = (String) o;
				Player p = Bukkit.getPlayer(s);
				if (p == null) {
					knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
				} else {
					white.add(Bukkit.getPlayer(s).getName());
				}
			}

			//sanity check
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (GetPlayerTeam(p) == null) {
					spectator.add(p.getName());
					p.sendMessage(text("[!] Tourneys mode is enabled but you weren't assigned to a custom team, we've put you in Spectator Team."));
				}
			}

		}
        else {
			switch (type) {
				case GameManager.GameTypes.StanderedSolos -> {
					if (playerlist.size() > 0) {
						if (blue.isEmpty()) {
							blue.add(playerlist.get(0));
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + blue + " in Team Blue");
						}
					} else {
						Bukkit.getLogger().log(Level.SEVERE,
								"Tried to add a player to team Blue but the player list is 0. Please report this as you shouldn't be able to get this error");
					}

					if (playerlist.size() > 1) { // If the player list is 2 or greater
						if (cyan.isEmpty()) {
							cyan.add(playerlist.get(1));
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + cyan + " in Team Cyan");
						}
					} else {
						Bukkit.getLogger().log(Level.WARNING,
								"No player(s) available for Cyan team (FYI: Recommend getting an alt account or someone else to join. 2 or more players is recommended)");
					}

					if (playerlist.size() > 2) { // If the player list is 3 or greater
						if (green.isEmpty()) {
							green.add(playerlist.get(2));
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + green + " in Team Green");
						}
					} else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Green team");
					}

					if (playerlist.size() > 3) { // If the player list is 4 or greater
						if (lemon.isEmpty()) {
							lemon.add(playerlist.get(3));
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + lemon + " in Team Lemon");
						}
					} else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
					}

					if (playerlist.size() > 4) { // If the player list is 5 or greater
						if (lime.isEmpty()) {
							lime.add(playerlist.get(4));
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + lime + " in Team Lime");
						}
					} else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lime team");
					}

					if (playerlist.size() > 5) { // If the player list is 6 or greater
						if (magenta.isEmpty()) {
							magenta.add(playerlist.get(5));
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + magenta + " in Team Magenta");
						}
					} else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Magenta team");
					}

					if (playerlist.size() > 6) { // If the player list is 7 or greater
						if (orange.isEmpty()) {
							orange.add(playerlist.get(6));
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + orange + " in Team Orange");
						}
					} else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Orange team");
					}

					if (playerlist.size() > 7) { // If the player list is 8 or greater
						if (peach.isEmpty()) {
							peach.add(playerlist.get(7));
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + peach + " in Team Peach");
						}
					} else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Peach team");
					}

					if (playerlist.size() > 8) { // If the player list is 9 or greater
						if (purple.isEmpty()) {
							purple.add(playerlist.get(8));
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + purple + " in Team Purple");
						}
					} else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Purple team");
					}

					if (playerlist.size() > 9) { // If the player list is 10 or greater
						if (red.isEmpty()) {
							red.add(playerlist.get(9));
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + red + " in Team Red");
						}
					} else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Red team");
					}

					if (playerlist.size() > 10) { // If the player list is 11 or greater
						if (white.isEmpty()) {
							white.add(playerlist.get(10));
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + white + " in Team White");
						}
					} else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for White team");
					}

					if (playerlist.size() > 11) { // If the player list is 12
						if (yellow.isEmpty()) {
							yellow.add(playerlist.get(11));
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + yellow + " in Team Yellow");
						}
					} else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Yellow team");
					}
				}
				case GameManager.GameTypes.StanderedDuos -> {
					if (playerlist.size() > 0) {
						if (blue.isEmpty()) {
							blue.add(playerlist.get(0));
							if (playerlist.size() > 1) {
								blue.add(playerlist.get(1));
							}
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + blue + " in Team Blue");
						}
					}else {
						Bukkit.getLogger().log(Level.SEVERE, "Tried to add a player to team Blue but the player list is 0. Please report this as you shouldn't be able to get this error");
						throw new RuntimeException();
					}

					if (playerlist.size() > 2) {
						if (cyan.isEmpty()) {
							cyan.add(playerlist.get(2));
							if (playerlist.size() > 3) {
								cyan.add(playerlist.get(3));
							}
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + cyan + " in Team Cyan");
						}
					}else {
						Bukkit.getLogger().log(Level.WARNING, "No player(s) available for Cyan team (FYI: Recommend getting an alt account or someone else to join. 2 or more players is recommended)");
					}

					if (playerlist.size() > 4) {
						if (green.isEmpty()) {
							green.add(playerlist.get(4));
							if (playerlist.size() > 5) {
								green.add(playerlist.get(5));
							}
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + green + " in Team Green");
						}
					}else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Green team");
					}

					if (playerlist.size() > 6) {
						if (lemon.isEmpty()) {
							lemon.add(playerlist.get(6));
							if (playerlist.size() > 7) {
								lemon.add(playerlist.get(7));
							}
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + lemon + " in Team Lemon");
						}
					}else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
					}

					if (playerlist.size() > 8) {
						if (lime.isEmpty()) {
							lime.add(playerlist.get(8));
							if (playerlist.size() > 9) {
								lime.add(playerlist.get(9));
							}
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + lime + " in Team Lime");
						}
					}else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lime team");
					}

					if (playerlist.size() > 10) {
						if (magenta.isEmpty()) {
							magenta.add(playerlist.get(10));
							if (playerlist.size() > 11) {
								magenta.add(playerlist.get(11));
							}
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + magenta + " in Team Magenta");
						}
					}else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Magenta team");
					}

					if (playerlist.size() > 12) {
						if (orange.isEmpty()) {
							orange.add(playerlist.get(12));
							if (playerlist.size() > 13) {
								orange.add(playerlist.get(13));
							}
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + orange + " in Team Orange");
						}
					}else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Orange team");
					}

					if (playerlist.size() > 14) {
						if (peach.isEmpty()) {
							peach.add(playerlist.get(14));
							if (playerlist.size() > 15) {
								peach.add(playerlist.get(15));
							}
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + peach + " in Team Peach");
						}
					}else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Peach team");
					}

					if (playerlist.size() > 16) {
						if (purple.isEmpty()) {
							purple.add(playerlist.get(16));
							if (playerlist.size() > 17) {
								purple.add(playerlist.get(17));
							}
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + purple + " in Team Purple");
						}
					}else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Purple team");
					}

					if (playerlist.size() > 18) {
						if (red.isEmpty()) {
							red.add(playerlist.get(18));
							if (playerlist.size() > 19) {
								red.add(playerlist.get(19));
							}
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + red + " in Team Red");
						}
					}else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Red team");
					}

					if (playerlist.size() > 20) {
						if (white.isEmpty()) {
							white.add(playerlist.get(20));
							if (playerlist.size() > 21) {
								white.add(playerlist.get(21));
							}
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + white + " in Team White");
						}
					}else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for White team");
					}

					if (playerlist.size() > 22) {
						if (yellow.isEmpty()) {
							yellow.add(playerlist.get(22));
							if (playerlist.size() > 23) {
								yellow.add(playerlist.get(23));
							}
							Bukkit.getLogger().log(Level.INFO, "Player(s) " + yellow + " in Team Yellow");
						}
					}else {
						Bukkit.getLogger().log(Level.INFO, "No player(s) available for Yellow team");
					}
				}
			}


			if (playerlist.size() > 13) {
				Bukkit.getLogger().log(Level.INFO, "Sorting Players into teams (duos)...");
			} else {
				Bukkit.getLogger().log(Level.INFO, "Sorting Players into teams (solo)...");
			}
			if (Bukkit.getOnlinePlayers().isEmpty()) {
				Bukkit.getServer().sendMessage(
						text("\nStarting the game requires a player to be online. Please login to the server and try again.\n"));
				return;
			} else {

			}
		}
	}

	public static List<String> get_team_from_string(String s) {
		if (s.equals("spectator")) {
			return spectator;
		} else if (s.equals("blue")) {
			return blue;
		} else if (s.equals("cyan")) {
			return cyan;
		} else if (s.equals("green")) {
			return green;
		} else if (s.equals("lemon")) {
			return lemon;
		} else if (s.equals("lime")) {
			return lime;
		} else if (s.equals("magenta")) {
			return magenta;
		} else if (s.equals("orange")) {
			return orange;
		} else if (s.equals("peach")) {
			return peach;
		} else if (s.equals("purple")) {
			return purple;
		} else if (s.equals("red")) {
			return red;
		} else if (s.equals("white")) {
			return white;
		} else if (s.equals("yellow")) {
			return yellow;
		} else {
			return null;
		}
	}

	public static String GetPlayerTeam(Player player) {
		// Bukkit.getLogger().log(Level.INFO, "Figuring out " + player.getName() + "'s
		// Team...");
		if (spectator.contains(player.getName())) {
			return "spectator";
		} else if (blue.contains(player.getName())) {
			return "blue";
		} else if (cyan.contains(player.getName())) {
			return "cyan";
		} else if (green.contains(player.getName())) {
			return "green";
		} else if (lemon.contains(player.getName())) {
			return "lemon";
		} else if (lime.contains(player.getName())) {
			return "lime";
		} else if (magenta.contains(player.getName())) {
			return "magenta";
		} else if (orange.contains(player.getName())) {
			return "orange";
		} else if (peach.contains(player.getName())) {
			return "peach";
		} else if (purple.contains(player.getName())) {
			return "purple";
		} else if (red.contains(player.getName())) {
			return "red";
		} else if (white.contains(player.getName())) {
			return "white";
		} else if (yellow.contains(player.getName())) {
			return "yellow";
		} else {
			return null;
		}
	}

	public static void DisconnectPlayer(String Player) {
		if (spectator.contains(Player)) {
			spectator.remove(spectator.indexOf(Player));
		} else if (blue.contains(Player)) {
			blue.remove(blue.indexOf(Player));
		} else if (cyan.contains(Player)) {
			cyan.remove(cyan.indexOf(Player));
		} else if (green.contains(Player)) {
			green.remove(green.indexOf(Player));
		} else if (lemon.contains(Player)) {
			lemon.remove(lemon.indexOf(Player));
		} else if (lime.contains(Player)) {
			lime.remove(lime.indexOf(Player));
		} else if (magenta.contains(Player)) {
			magenta.remove(magenta.indexOf(Player));
		} else if (orange.contains(Player)) {
			orange.remove(orange.indexOf(Player));
		} else if (peach.contains(Player)) {
			peach.remove(peach.indexOf(Player));
		} else if (purple.contains(Player)) {
			purple.remove(purple.indexOf(Player));
		} else if (red.contains(Player)) {
			red.remove(red.indexOf(Player));
		} else if (white.contains(Player)) {
			white.remove(white.indexOf(Player));
		} else if (yellow.contains(Player)) {
			yellow.remove(yellow.indexOf(Player));
		}
	}

	public static void SetPlayerDisplayNames(Player player) {
		if (Teams.GetPlayerTeam(player).equals("spectator")) {
			player.displayName(text("[Spectator] ").append(text(player.getName())));
		} else {
			TeamData td = TeamData.get_team_data(Teams.GetPlayerTeam(player));
			player.displayName(text(td.symbol).append(text(player.getName()).color(TextColor.color(td.color.asRGB()))));
		}
	}
}

class TeamStatus {
	enum Status {
		Alive,
		Dead,
	}

	public static HashMap<String, Integer> team_statuses = new HashMap<>();

	private static void update_team_status(String team) {
		int counter = 0;
		for (String p_name : Teams.get_team_from_string(team)) {
			Player p = Bukkit.getPlayer(p_name);
			PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
			if (pd == null) {
				return;
			}
			if (!pd.isEliminated) {
				counter++;
			}
		}
		List<String> td = Teams.get_team_from_string(team);
		team_statuses.put(team, counter);
		//if (td.size() == 0) {
		//
		//} else if (counter == td.size()) {
		//	team_statuses.put(team, Status.Alive);
		//}
		//else {
		//	team_statuses.put(team, Status.Dead);
		//}
	}

	private static boolean is_only_team_alive(String team) {
		if (team_statuses.get(team) == 0) {
			return false;
		}

		for (String loop_team : team_statuses.keySet()) {
			if (loop_team == team) {
				continue;
			}
			if (team_statuses.get(loop_team) > 0) {
				return false;
			}
		}
		return true;
	}

	public static List<String> getAliveTeams() {
		List<String> output = new ArrayList<>();
		for (String a : team_statuses.keySet()) {
			if (team_statuses.get(a) > 0) {
				output.add(a);
			}
		}
		return output;
	}

	// I hate this class

	// This could also cause performance issues since this class runs every tick
	// thanks to it being in a BukkitRunnable
	// Works fine on my machines ig, if you have lag problems blame this ig
	public static void Init() {
		team_statuses.clear();
		for (TeamData td : Teams.team_datas) {
			if (Teams.get_team_from_string(td.name).isEmpty()) {
				team_statuses.put(td.name, 0);
			} else {
				team_statuses.put(td.name, Teams.get_team_from_string(td.name).size());
			}
		}

		if (Bukkit.getOnlinePlayers().size() == 1) {
			Bukkit.getServer().sendMessage(text(
					"1 player detected, To end the game, run \"/knockoff end\" as a player with op. The game will not end automatically due to player size"));
			return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {

				for (TeamData td : Teams.team_datas) {
					if (knockoff.getInstance().GameManager == null) {
						cancel();
					} 
					// Check if all players in the team are alive. If not set them to dead
					update_team_status(td.name);
				}


				for (TeamData td : Teams.team_datas) {
					if (is_only_team_alive(td.name)) {
						GameManager.StartEndGame(td.name);
						cancel();
						return;
					}
				}
			}
		}.runTaskTimer(knockoff.getInstance(), 20, 1);
	}
}

class CustomPlayerNametags {
	public static void CustomPlayerNametags(Player player) {

		Location ploc = new Location(player.getWorld(), player.getX(), player.getY(), player.getZ(), player.getYaw(),
				player.getPitch());
		TextDisplay displayfront = ploc.getWorld().spawn(ploc, TextDisplay.class, entity -> {
			entity.setBillboard(Display.Billboard.CENTER);
		});
		player.addPassenger(displayfront);
		player.hideEntity(knockoff.getInstance(), displayfront);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (knockoff.getInstance().GameManager == null || !player.isOnline()
						|| knockoff.getInstance().GameManager.getPlayerData(player).isPlayerDead) {
					displayfront.remove();
					cancel();
				} else {
					PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(player);
					if (pd.isPlayerDead) {
						displayfront.text(text(""));
					} else {
						Component rankDisplay;
						if (pd.cachedRankIcon_full.equals(text(""))) {
							rankDisplay = text("");
						} else {
							rankDisplay = pd.cachedRankIcon_full.append(text("\n"));
						}

						displayfront.text(rankDisplay
								.append(player.displayName())
								.append(text("\nKB: "))
								.append(text(pd.percent))
								.append(text("% | "))
								.append(text(pd.getLives()))
								.append(text("x \uE12C"))
						);
					}
				}
			}
		}.runTaskTimer(knockoff.getInstance(), 1, 1);
	}
}

class TeamData {
	public final String name;
	public final Color color;
    public final NamespacedKey item_model;
	public final String symbol;

	public static List<TeamData> create_teams() {
		List<TeamData> list = new ArrayList<>();
		list.add(new TeamData("spectator", Color.fromRGB(0xFFFFFF), " "));
		list.add(new TeamData("blue", Color.fromRGB(0x0A42BB), "\uE120 "));
		list.add(new TeamData("cyan", Color.fromRGB(0x157D91), "\uE121 "));
		list.add(new TeamData("green", Color.fromRGB(0x0A971E), "\uE122 "));
		list.add(new TeamData("lemon", Color.fromRGB(0xFFC500), "\uE128 "));
		list.add(new TeamData("lime", Color.fromRGB(0x67E555), "\uE123 "));
		list.add(new TeamData("magenta", Color.fromRGB(0xDA50E0), "\uE124 "));
		list.add(new TeamData("orange", Color.fromRGB(0xFF7900), "\uE129 "));
		list.add(new TeamData("peach", Color.fromRGB(0xFF8775), "\uE12A "));
		list.add(new TeamData("purple", Color.fromRGB(0x7525DC), "\uE12B "));
		list.add(new TeamData("red", Color.fromRGB(0xF74036), "\uE125 "));
		list.add(new TeamData("white", Color.fromRGB(0xFFFFFF), "\uE126 "));
		list.add(new TeamData("yellow", Color.fromRGB(0xFBE059), "\uE127 "));
		return list;
	}

	public static TeamData get_team_data(String s) {
		for (TeamData td : Teams.team_datas) {
			if (td.name.equals(s)) {
				return td;
			}
		}
		return null;
	}

	public TeamData(String name, Color color, String symbol) {
		this.name = name;
		this.color = color;
        this.item_model = new NamespacedKey("crystalized", "block/nexus/" + name);
		this.symbol = symbol;
	}
}
