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
import java.util.logging.Logger;

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
	public static List<String> weak = new ArrayList<>();
	public static List<String> strong = new ArrayList<>();

	public static final List<TeamData> team_datas = TeamData.create_teams();
    public static List<TeamData> team_datas_without_spectator = null; //for team sorting so that players cant get into spectator normally

	public Teams(GameManager.GameTypes type) {
        team_datas_without_spectator = TeamData.create_teams();
        team_datas_without_spectator.remove(team_datas_without_spectator.getFirst());

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
		weak.clear();
		strong.clear();

		//idk if this is good, but better than what I had before this ig - Callum
		try {
			switch (type) {
				case GameManager.GameTypes.Custom -> {
					FileConfiguration config = knockoff.getInstance().getConfig();
					for (Object o : config.getList("teams.spectator").toArray()) {
						String s = (String) o;
						Player p = Bukkit.getPlayer(s);
						if (p == null) {
							knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
						} else {
							spectator.add(Bukkit.getPlayer(s).getName());
						}
					}
					for (Object o : config.getList("teams.blue").toArray()) {
						String s = (String) o;
						Player p = Bukkit.getPlayer(s);
						if (p == null) {
							knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
						} else {
							blue.add(Bukkit.getPlayer(s).getName());
						}
					}
					for (Object o : config.getList("teams.cyan").toArray()) {
						String s = (String) o;
						Player p = Bukkit.getPlayer(s);
						if (p == null) {
							knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
						} else {
							cyan.add(Bukkit.getPlayer(s).getName());
						}
					}
					for (Object o : config.getList("teams.green").toArray()) {
						String s = (String) o;
						Player p = Bukkit.getPlayer(s);
						if (p == null) {
							knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
						} else {
							green.add(Bukkit.getPlayer(s).getName());
						}
					}
					for (Object o : config.getList("teams.lemon").toArray()) {
						String s = (String) o;
						Player p = Bukkit.getPlayer(s);
						if (p == null) {
							knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
						} else {
							lemon.add(Bukkit.getPlayer(s).getName());
						}
					}
					for (Object o : config.getList("teams.lime").toArray()) {
						String s = (String) o;
						Player p = Bukkit.getPlayer(s);
						if (p == null) {
							knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
						} else {
							lime.add(Bukkit.getPlayer(s).getName());
						}
					}
					for (Object o : config.getList("teams.magenta").toArray()) {
						String s = (String) o;
						Player p = Bukkit.getPlayer(s);
						if (p == null) {
							knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
						} else {
							magenta.add(Bukkit.getPlayer(s).getName());
						}
					}
					for (Object o : config.getList("teams.orange").toArray()) {
						String s = (String) o;
						Player p = Bukkit.getPlayer(s);
						if (p == null) {
							knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
						} else {
							orange.add(Bukkit.getPlayer(s).getName());
						}
					}
					for (Object o : config.getList("teams.peach").toArray()) {
						String s = (String) o;
						Player p = Bukkit.getPlayer(s);
						if (p == null) {
							knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
						} else {
							peach.add(Bukkit.getPlayer(s).getName());
						}
					}
					for (Object o : config.getList("teams.purple").toArray()) {
						String s = (String) o;
						Player p = Bukkit.getPlayer(s);
						if (p == null) {
							knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
						} else {
							purple.add(Bukkit.getPlayer(s).getName());
						}
					}
					for (Object o : config.getList("teams.red").toArray()) {
						String s = (String) o;
						Player p = Bukkit.getPlayer(s);
						if (p == null) {
							knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
						} else {
							red.add(Bukkit.getPlayer(s).getName());
						}
					}
					for (Object o : config.getList("teams.yellow").toArray()) {
						String s = (String) o;
						Player p = Bukkit.getPlayer(s);
						if (p == null) {
							knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
						} else {
							yellow.add(Bukkit.getPlayer(s).getName());
						}
					}
					for (Object o : config.getList("teams.white").toArray()) {
						String s = (String) o;
						Player p = Bukkit.getPlayer(s);
						if (p == null) {
							knockoff.getInstance().getLogger().log(Level.WARNING, "Player \"" + s + "\" is not online. cannot add them to a team.");
						} else {
							white.add(Bukkit.getPlayer(s).getName());
						}
					}
				}
				case GameManager.GameTypes.StanderedSolos -> {
					randomizeTeams(1, playerlist);
				}
				case GameManager.GameTypes.StanderedDuos -> {
					randomizeTeams(2, playerlist);
				}
				case GameManager.GameTypes.StanderedTrios -> {
                    randomizeTeams(3, playerlist);
				}
				case GameManager.GameTypes.StanderedSquads -> {
                    randomizeTeams(4, playerlist);
				}
			}
		} catch (Exception e) {

		}

		//sanity check
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (GetPlayerTeam(p) == null) {
				spectator.add(p.getName());
				p.sendMessage(text("[!] You weren't assigned a team, we've put you in Spectator Team."));
			}
		}

		Logger logger = knockoff.getInstance().getLogger();
		logger.log(Level.INFO, "Player(s) " + spectator + " in Team Spectator");
		logger.log(Level.INFO, "Player(s) " + blue + " in Team Blue");
		logger.log(Level.INFO, "Player(s) " + cyan + " in Team Cyan");
		logger.log(Level.INFO, "Player(s) " + green + " in Team Green");
		logger.log(Level.INFO, "Player(s) " + lemon + " in Team Lemon");
		logger.log(Level.INFO, "Player(s) " + lime + " in Team Lime");
		logger.log(Level.INFO, "Player(s) " + magenta + " in Team Magenta");
		logger.log(Level.INFO, "Player(s) " + orange + " in Team Orange");
		logger.log(Level.INFO, "Player(s) " + peach + " in Team Peach");
		logger.log(Level.INFO, "Player(s) " + purple + " in Team Purple");
		logger.log(Level.INFO, "Player(s) " + red + " in Team Red");
		logger.log(Level.INFO, "Player(s) " + yellow + " in Team Yellow");
		logger.log(Level.INFO, "Player(s) " + white + " in Team White");
		logger.log(Level.INFO, "Player(s) " + weak + " in Team Gray (Weak Shard) ");
		logger.log(Level.INFO, "Player(s) " + strong + " in Team Pink (Strong Shard)");

	}

	private void randomizeTeams(int TeamSize, List<String> playerlist) {
		try {
			int j = 0;
			Collections.shuffle(team_datas_without_spectator);
			for (TeamData td : team_datas_without_spectator) {
				int i = 0;
				boolean b = true;
				while (b  /*i != TeamSize + 1*/) {
					addPlayerToTeamIfPossible(get_team_from_string(td.name), playerlist.get(j));
					i++;
					j++;
					//Bukkit.getLogger().log(Level.INFO, "j: " + j + " i:" + i + " ts:" + TeamSize);
					if (i == TeamSize) {
						b = false;
					}
				}
			}
		} catch (Exception e) {

		}
	}

	private void addPlayerToTeamIfPossible(List<String> team, String p) {
		try {
			if (p != null) {
				team.add(p);
			}
		} catch (Exception e) {

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
		} else if (s.equals("weak")) {
			return weak;
		} else if (s.equals("strong")) {
			return strong;
		}

		else {
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
		} else if (weak.contains(player.getName())) {
			return "weak";
		} else if (strong.contains(player.getName())) {
			return "strong";
		}


		else {
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
		} else if (weak.contains(Player)) {
			weak.remove(weak.indexOf(Player));
		} else if (strong.contains(Player)) {
			strong.remove(strong.indexOf(Player));
		}
	}

	public static void SetPlayerDisplayNames(Player player) {
		TeamData td = TeamData.get_team_data(Teams.GetPlayerTeam(player));
		player.displayName(text(td.symbol).append(text(player.getName()).color(TextColor.color(td.color.asRGB()))));
	}
}

class TeamStatus {
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
			Bukkit.getServer().sendMessage(text("1 player detected, To end the game, run \"/knockoff end\" as a player with op. The game will not end automatically due to player size"));
			return;
		}

		new BukkitRunnable() {
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
						GameManager.StartEndGame(td.name, td);
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
		//list.add(new TeamData("weak", Color.fromRGB(0xFFFFFF), "? "));
		//list.add(new TeamData("strong", Color.fromRGB(0xFFFFFF), "? "));
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
