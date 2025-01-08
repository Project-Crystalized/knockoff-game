package gg.knockoff.game;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

import static net.kyori.adventure.text.Component.text;

public class Teams {

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

	public Teams() {
		List<String> playerlist = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			playerlist.add(p.getName());
		}
		Collections.shuffle(playerlist);

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
			new BukkitRunnable() {
				@Override
				public void run() { // Clear every team when the game ends and everyone is kicked
					if (knockoff.getInstance().GameManager == null) {
						for (TeamData td : team_datas) {
							if (!get_team_from_string(td.name).isEmpty()) {
								get_team_from_string(td.name).clear();
							}
						}
						cancel();
					}
				}
			}.runTaskTimer(knockoff.getInstance(), 20, 1);

			if (playerlist.size() > 0) {
				if (blue.isEmpty()) {
					blue.add(playerlist.get(0));
					if (playerlist.size() > 12) {
						blue.add(playerlist.get(12));
					}
					Bukkit.getLogger().log(Level.INFO, "Player(s) " + blue + " in Team Blue");
				}
			} else {
				Bukkit.getLogger().log(Level.SEVERE,
						"Tried to add a player to team Blue but the player list is 0. Please report this as you shouldn't be able to get this error");
			}

			if (playerlist.size() > 1) { // If the player list is 2 or greater
				if (cyan.isEmpty()) {
					cyan.add(playerlist.get(1));
					if (playerlist.size() > 13) {
						cyan.add(playerlist.get(13));
					}
					Bukkit.getLogger().log(Level.INFO, "Player(s) " + cyan + " in Team Cyan");
				}
			} else {
				Bukkit.getLogger().log(Level.WARNING,
						"No player(s) available for Cyan team (FYI: Recommend getting an alt account or someone else to join. 2 or more players is recommended)");
			}

			if (playerlist.size() > 2) { // If the player list is 3 or greater
				if (green.isEmpty()) {
					green.add(playerlist.get(2));
					if (playerlist.size() > 14) {
						green.add(playerlist.get(14));
					}
					Bukkit.getLogger().log(Level.INFO, "Player(s) " + green + " in Team Green");
				}
			} else {
				Bukkit.getLogger().log(Level.INFO, "No player(s) available for Green team");
			}

			if (playerlist.size() > 3) { // If the player list is 4 or greater
				if (lemon.isEmpty()) {
					lemon.add(playerlist.get(3));
					if (playerlist.size() > 15) {
						lemon.add(playerlist.get(15));
					}
					Bukkit.getLogger().log(Level.INFO, "Player(s) " + lemon + " in Team Lemon");
				}
			} else {
				Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lemon team");
			}

			if (playerlist.size() > 4) { // If the player list is 5 or greater
				if (lime.isEmpty()) {
					lime.add(playerlist.get(4));
					if (playerlist.size() > 16) {
						lime.add(playerlist.get(16));
					}
					Bukkit.getLogger().log(Level.INFO, "Player(s) " + lime + " in Team Lime");
				}
			} else {
				Bukkit.getLogger().log(Level.INFO, "No player(s) available for Lime team");
			}

			if (playerlist.size() > 5) { // If the player list is 6 or greater
				if (magenta.isEmpty()) {
					magenta.add(playerlist.get(5));
					if (playerlist.size() > 17) {
						magenta.add(playerlist.get(17));
					}
					Bukkit.getLogger().log(Level.INFO, "Player(s) " + magenta + " in Team Magenta");
				}
			} else {
				Bukkit.getLogger().log(Level.INFO, "No player(s) available for Magenta team");
			}

			if (playerlist.size() > 6) { // If the player list is 7 or greater
				if (orange.isEmpty()) {
					orange.add(playerlist.get(6));
					if (playerlist.size() > 18) {
						orange.add(playerlist.get(18));
					}
					Bukkit.getLogger().log(Level.INFO, "Player(s) " + orange + " in Team Orange");
				}
			} else {
				Bukkit.getLogger().log(Level.INFO, "No player(s) available for Orange team");
			}

			if (playerlist.size() > 7) { // If the player list is 8 or greater
				if (peach.isEmpty()) {
					peach.add(playerlist.get(7));
					if (playerlist.size() > 19) {
						peach.add(playerlist.get(19));
					}
					Bukkit.getLogger().log(Level.INFO, "Player(s) " + peach + " in Team Peach");
				}
			} else {
				Bukkit.getLogger().log(Level.INFO, "No player(s) available for Peach team");
			}

			if (playerlist.size() > 8) { // If the player list is 9 or greater
				if (purple.isEmpty()) {
					purple.add(playerlist.get(8));
					if (playerlist.size() > 20) {
						purple.add(playerlist.get(20));
					}
					Bukkit.getLogger().log(Level.INFO, "Player(s) " + purple + " in Team Purple");
				}
			} else {
				Bukkit.getLogger().log(Level.INFO, "No player(s) available for Purple team");
			}

			if (playerlist.size() > 9) { // If the player list is 10 or greater
				if (red.isEmpty()) {
					red.add(playerlist.get(9));
					if (playerlist.size() > 21) {
						red.add(playerlist.get(21));
					}
					Bukkit.getLogger().log(Level.INFO, "Player(s) " + red + " in Team Red");
				}
			} else {
				Bukkit.getLogger().log(Level.INFO, "No player(s) available for Red team");
			}

			if (playerlist.size() > 10) { // If the player list is 11 or greater
				if (white.isEmpty()) {
					white.add(playerlist.get(10));
					if (playerlist.size() > 22) {
						white.add(playerlist.get(22));
					}
					Bukkit.getLogger().log(Level.INFO, "Player(s) " + white + " in Team White");
				}
			} else {
				Bukkit.getLogger().log(Level.INFO, "No player(s) available for White team");
			}

			if (playerlist.size() > 11) { // If the player list is 12
				if (yellow.isEmpty()) {
					yellow.add(playerlist.get(11));
					if (playerlist.size() > 23) {
						yellow.add(playerlist.get(23));
					}
					Bukkit.getLogger().log(Level.INFO, "Player(s) " + yellow + " in Team Yellow");
				}
			} else {
				Bukkit.getLogger().log(Level.INFO, "No player(s) available for Yellow team");
			}
		}
	}

	public static List<String> get_team_from_string(String s) {
		if (s.equals("blue")) {
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
		if (blue.contains(player.getName())) {
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
		if (blue.contains(Player)) {
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
		TeamData td = TeamData.get_team_data(Teams.GetPlayerTeam(player));
		player.displayName(text(td.symbol).append(text(player.getName()).color(TextColor.color(td.color.asRGB()))));
	}
}

class TeamStatus {

	public static String BlueStatus = "";
	public static String CyanStatus = "";
	public static String GreenStatus = "";
	public static String LemonStatus = "";
	public static String LimeStatus = "";
	public static String MagentaStatus = "";
	public static String OrangeStatus = "";
	public static String PeachStatus = "";
	public static String PurpleStatus = "";
	public static String RedStatus = "";
	public static String WhiteStatus = "";
	public static String YellowStatus = "";
	private static ArrayList<String> TeamsList = new ArrayList<String>();

	// I hate this class

	// This could also cause performance issues since this class runs every tick
	// thanks to it being in a BukkitRunnable
	// Works fine on my machines ig, if you have lag problems blame this ig
	public static void Init() {

		// If blue is empty, set it to "dead", otherwise set it to "alive"
		// Also this specific case should never trigger, Blue is the first team player 1
		// is always in, You need at least 1 player to start a game
		if (Teams.blue.isEmpty()) {
			BlueStatus = "dead";
		} else {
			BlueStatus = "alive";
		}
		if (Teams.cyan.isEmpty()) {
			CyanStatus = "dead";
		} else {
			CyanStatus = "alive";
		}
		if (Teams.green.isEmpty()) {
			GreenStatus = "dead";
		} else {
			GreenStatus = "alive";
		}
		if (Teams.lemon.isEmpty()) {
			LemonStatus = "dead";
		} else {
			LemonStatus = "alive";
		}
		if (Teams.lime.isEmpty()) {
			LimeStatus = "dead";
		} else {
			LimeStatus = "alive";
		}
		if (Teams.magenta.isEmpty()) {
			MagentaStatus = "dead";
		} else {
			MagentaStatus = "alive";
		}
		if (Teams.orange.isEmpty()) {
			OrangeStatus = "dead";
		} else {
			OrangeStatus = "alive";
		}
		if (Teams.peach.isEmpty()) {
			PeachStatus = "dead";
		} else {
			PeachStatus = "alive";
		}
		if (Teams.purple.isEmpty()) {
			PurpleStatus = "dead";
		} else {
			PurpleStatus = "alive";
		}
		if (Teams.red.isEmpty()) {
			RedStatus = "dead";
		} else {
			RedStatus = "alive";
		}
		if (Teams.white.isEmpty()) {
			WhiteStatus = "dead";
		} else {
			WhiteStatus = "alive";
		}
		if (Teams.yellow.isEmpty()) {
			YellowStatus = "dead";
		} else {
			YellowStatus = "alive";
		}

		if (Bukkit.getOnlinePlayers().size() == 1) {
			Bukkit.getServer().sendMessage(text(
					"1 player detected, To end the game, run \"/knockoff end\" as a player with op. The game will not end automatically due to player size"));
			return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {

				// Check if all players in the team are alive. If not set them to dead
				if (knockoff.getInstance().GameManager == null) {
					cancel();
				} // Putting this in between these blocks of code to prevent errors in console.
					// This stops the BukkitRunnable when the game ends

				// This is going get annoying to copy-paste for all 12 teams :cry:
				// Could be optimised, marking all the ugly code in this project with this
				// phrase ig
				int blue = 0;
				int bi = 0;
				while (bi != Teams.blue.size() && knockoff.getInstance().GameManager != null) {
					Player p = Bukkit.getPlayer(Teams.blue.get(bi));
					PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
					if (!pd.isEliminated) {
						blue++;
					}
					bi++;
				}
				if (bi == 0 && Teams.blue.size() == 0) {
					BlueStatus = "dead";
				} else if (blue == Teams.blue.size()) {
					BlueStatus = "alive";
				} else {
					BlueStatus = "dead";
				}

				if (knockoff.getInstance().GameManager == null) {
					cancel();
				}
				int cyan = 0;
				int ci = 0;
				while (ci != Teams.cyan.size() && knockoff.getInstance().GameManager != null) {
					Player p = Bukkit.getPlayer(Teams.cyan.get(ci));
					PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
					if (pd == null) {
						return;
					}
					if (!pd.isEliminated) {
						cyan++;
					}
					ci++;
				}
				if (ci == 0 && Teams.cyan.size() == 0) {
					CyanStatus = "dead";
				} else if (cyan == Teams.cyan.size()) {
					CyanStatus = "alive";
				} else {
					CyanStatus = "dead";
				}

				if (knockoff.getInstance().GameManager == null) {
					cancel();
				}
				int green = 0;
				int gi = 0;
				while (gi != Teams.green.size() && knockoff.getInstance().GameManager != null) {
					Player p = Bukkit.getPlayer(Teams.green.get(gi));
					PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
					if (pd == null) {
						return;
					}
					if (!pd.isEliminated) {
						green++;
					}
					gi++;
				}
				if (gi == 0 && Teams.green.size() == 0) {
					GreenStatus = "dead";
				} else if (green == Teams.green.size()) {
					GreenStatus = "alive";
				} else {
					GreenStatus = "dead";
				}

				if (knockoff.getInstance().GameManager == null) {
					cancel();
				}
				int lemon = 0;
				int li = 0;
				while (li != Teams.lemon.size() && knockoff.getInstance().GameManager != null) {
					Player p = Bukkit.getPlayer(Teams.lemon.get(li));
					PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
					if (pd == null) {
						return;
					}
					if (!pd.isEliminated) {
						lemon++;
					}
					li++;
				}
				if (li == 0 && Teams.lemon.size() == 0) {
					LemonStatus = "dead";
				} else if (lemon == Teams.lemon.size()) {
					LemonStatus = "alive";
				} else {
					LemonStatus = "dead";
				}

				if (knockoff.getInstance().GameManager == null) {
					cancel();
				}
				int lime = 0;
				int l2i = 0;
				while (l2i != Teams.lime.size() && knockoff.getInstance().GameManager != null) {
					Player p = Bukkit.getPlayer(Teams.lime.get(l2i));
					PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
					if (pd == null) {
						return;
					}
					if (!pd.isEliminated) {
						lime++;
					}
					l2i++;
				}
				if (l2i == 0 && Teams.lime.size() == 0) {
					LimeStatus = "dead";
				} else if (lime == Teams.lime.size()) {
					LimeStatus = "alive";
				} else {
					LimeStatus = "dead";
				}

				if (knockoff.getInstance().GameManager == null) {
					cancel();
				}
				int magenta = 0;
				int mi = 0;
				while (mi != Teams.magenta.size() && knockoff.getInstance().GameManager != null) {
					Player p = Bukkit.getPlayer(Teams.magenta.get(mi));
					PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
					if (pd == null) {
						return;
					}
					if (!pd.isEliminated) {
						magenta++;
					}
					mi++;
				}
				if (mi == 0 && Teams.magenta.size() == 0) {
					MagentaStatus = "dead";
				} else if (magenta == Teams.magenta.size()) {
					MagentaStatus = "alive";
				} else {
					MagentaStatus = "dead";
				}

				if (knockoff.getInstance().GameManager == null) {
					cancel();
				}
				int orange = 0;
				int oi = 0;
				while (oi != Teams.orange.size() && knockoff.getInstance().GameManager != null) {
					Player p = Bukkit.getPlayer(Teams.orange.get(oi));
					PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
					if (pd == null) {
						return;
					}
					if (!pd.isEliminated) {
						orange++;
					}
					oi++;
				}
				if (oi == 0 && Teams.orange.size() == 0) {
					OrangeStatus = "dead";
				} else if (orange == Teams.orange.size()) {
					OrangeStatus = "alive";
				} else {
					OrangeStatus = "dead";
				}

				if (knockoff.getInstance().GameManager == null) {
					cancel();
				}
				int peach = 0;
				int pi = 0;
				while (pi != Teams.peach.size() && knockoff.getInstance().GameManager != null) {
					Player p = Bukkit.getPlayer(Teams.peach.get(pi));
					PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
					if (pd == null) {
						return;
					}
					if (!pd.isEliminated) {
						peach++;
					}
					pi++;
				}
				if (pi == 0 && Teams.peach.size() == 0) {
					PeachStatus = "dead";
				} else if (peach == Teams.peach.size()) {
					PeachStatus = "alive";
				} else {
					PeachStatus = "dead";
				}

				if (knockoff.getInstance().GameManager == null) {
					cancel();
				}
				int purple = 0;
				int p1i = 0;
				while (p1i != Teams.purple.size() && knockoff.getInstance().GameManager != null) {
					Player p = Bukkit.getPlayer(Teams.purple.get(p1i));
					PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
					if (pd == null) {
						return;
					}
					if (!pd.isEliminated) {
						purple++;
					}
					p1i++;
				}
				if (purple == Teams.purple.size()) {
					PurpleStatus = "dead";
				} else if (p1i == 0 && Teams.purple.size() == 0) {
					PurpleStatus = "alive";
				} else {
					PurpleStatus = "dead";
				}

				if (knockoff.getInstance().GameManager == null) {
					cancel();
				}
				int red = 0;
				int ri = 0;
				while (ri != Teams.red.size() && knockoff.getInstance().GameManager != null) {
					Player p = Bukkit.getPlayer(Teams.red.get(ri));
					PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
					if (pd == null) {
						return;
					}
					if (!pd.isEliminated) {
						red++;
					}
					ri++;
				}
				if (ri == 0 && Teams.red.size() == 0) {
					RedStatus = "dead";
				} else if (red == Teams.red.size()) {
					RedStatus = "alive";
				} else {
					RedStatus = "dead";
				}

				if (knockoff.getInstance().GameManager == null) {
					cancel();
				}
				int white = 0;
				int wi = 0;
				while (wi != Teams.white.size() && knockoff.getInstance().GameManager != null) {
					Player p = Bukkit.getPlayer(Teams.white.get(wi));
					PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
					if (pd == null) {
						return;
					}
					if (!pd.isEliminated) {
						white++;
					}
					wi++;
				}
				if (wi == 0 && Teams.white.size() == 0) {
					WhiteStatus = "dead";
				} else if (white == Teams.white.size()) {
					WhiteStatus = "alive";
				} else {
					WhiteStatus = "dead";
				}

				if (knockoff.getInstance().GameManager == null) {
					cancel();
				}
				int yellow = 0;
				int yi = 0;
				while (yi != Teams.yellow.size() && knockoff.getInstance().GameManager != null) {
					Player p = Bukkit.getPlayer(Teams.yellow.get(yi));
					PlayerData pd = knockoff.getInstance().GameManager.getPlayerData(p);
					if (pd == null) {
						return;
					}
					if (!pd.isEliminated) {
						yellow++;
					}
					yi++;
				}
				if (yi == 0 && Teams.yellow.size() == 0) {
					YellowStatus = "dead";
				} else if (yellow == Teams.yellow.size()) {
					YellowStatus = "alive";
				} else {
					YellowStatus = "dead";
				}

				TeamsList.clear();
				TeamsList.add(BlueStatus);
				TeamsList.add(CyanStatus);
				TeamsList.add(GreenStatus);
				TeamsList.add(LemonStatus);
				TeamsList.add(LimeStatus);
				TeamsList.add(MagentaStatus);
				TeamsList.add(OrangeStatus);
				TeamsList.add(PeachStatus);
				TeamsList.add(PurpleStatus);
				TeamsList.add(RedStatus);
				TeamsList.add(WhiteStatus);
				TeamsList.add(YellowStatus);

				if (TeamsList.contains("alive")) {

				}

				// TODO i fucking hate this but idk how else to format this well but it works
				// Could be optimised especially
				if (BlueStatus.equals("alive") && CyanStatus.equals("dead") && GreenStatus.equals("dead")
						&& LemonStatus.equals("dead") && LimeStatus.equals("dead") && MagentaStatus.equals("dead")
						&& OrangeStatus.equals("dead")
						&& PeachStatus.equals("dead") && PurpleStatus.equals("dead") && RedStatus.equals("dead")
						&& WhiteStatus.equals("dead") && YellowStatus.equals("dead")) {
					GameManager.StartEndGame("blue");
					cancel();
				} else if (BlueStatus.equals("dead") && CyanStatus.equals("alive") && GreenStatus.equals("dead")
						&& LemonStatus.equals("dead") && LimeStatus.equals("dead") && MagentaStatus.equals("dead")
						&& OrangeStatus.equals("dead")
						&& PeachStatus.equals("dead") && PurpleStatus.equals("dead") && RedStatus.equals("dead")
						&& WhiteStatus.equals("dead") && YellowStatus.equals("dead")) {
					GameManager.StartEndGame("cyan");
					cancel();
				} else if (BlueStatus.equals("dead") && CyanStatus.equals("dead") && GreenStatus.equals("alive")
						&& LemonStatus.equals("dead") && LimeStatus.equals("dead") && MagentaStatus.equals("dead")
						&& OrangeStatus.equals("dead")
						&& PeachStatus.equals("dead") && PurpleStatus.equals("dead") && RedStatus.equals("dead")
						&& WhiteStatus.equals("dead") && YellowStatus.equals("dead")) {
					GameManager.StartEndGame("green");
					cancel();
				} else if (BlueStatus.equals("dead") && CyanStatus.equals("dead") && GreenStatus.equals("dead")
						&& LemonStatus.equals("alive") && LimeStatus.equals("dead") && MagentaStatus.equals("dead")
						&& OrangeStatus.equals("dead")
						&& PeachStatus.equals("dead") && PurpleStatus.equals("dead") && RedStatus.equals("dead")
						&& WhiteStatus.equals("dead") && YellowStatus.equals("dead")) {
					GameManager.StartEndGame("lemon");
					cancel();
				} else if (BlueStatus.equals("dead") && CyanStatus.equals("dead") && GreenStatus.equals("dead")
						&& LemonStatus.equals("dead") && LimeStatus.equals("alive") && MagentaStatus.equals("dead")
						&& OrangeStatus.equals("dead")
						&& PeachStatus.equals("dead") && PurpleStatus.equals("dead") && RedStatus.equals("dead")
						&& WhiteStatus.equals("dead") && YellowStatus.equals("dead")) {
					GameManager.StartEndGame("lime");
					cancel();
				} else if (BlueStatus.equals("dead") && CyanStatus.equals("dead") && GreenStatus.equals("dead")
						&& LemonStatus.equals("dead") && LimeStatus.equals("dead") && MagentaStatus.equals("alive")
						&& OrangeStatus.equals("dead")
						&& PeachStatus.equals("dead") && PurpleStatus.equals("dead") && RedStatus.equals("dead")
						&& WhiteStatus.equals("dead") && YellowStatus.equals("dead")) {
					GameManager.StartEndGame("magenta");
					cancel();
				} else if (BlueStatus.equals("dead") && CyanStatus.equals("dead") && GreenStatus.equals("dead")
						&& LemonStatus.equals("dead") && LimeStatus.equals("dead") && MagentaStatus.equals("dead")
						&& OrangeStatus.equals("alive")
						&& PeachStatus.equals("dead") && PurpleStatus.equals("dead") && RedStatus.equals("dead")
						&& WhiteStatus.equals("dead") && YellowStatus.equals("dead")) {
					GameManager.StartEndGame("orange");
					cancel();
				} else if (BlueStatus.equals("dead") && CyanStatus.equals("dead") && GreenStatus.equals("dead")
						&& LemonStatus.equals("dead") && LimeStatus.equals("dead") && MagentaStatus.equals("dead")
						&& OrangeStatus.equals("dead")
						&& PeachStatus.equals("alive") && PurpleStatus.equals("dead") && RedStatus.equals("dead")
						&& WhiteStatus.equals("dead") && YellowStatus.equals("dead")) {
					GameManager.StartEndGame("peach");
					cancel();
				} else if (BlueStatus.equals("dead") && CyanStatus.equals("dead") && GreenStatus.equals("dead")
						&& LemonStatus.equals("dead") && LimeStatus.equals("dead") && MagentaStatus.equals("dead")
						&& OrangeStatus.equals("dead")
						&& PeachStatus.equals("dead") && PurpleStatus.equals("alive") && RedStatus.equals("dead")
						&& WhiteStatus.equals("dead") && YellowStatus.equals("dead")) {
					GameManager.StartEndGame("purple");
					cancel();
				} else if (BlueStatus.equals("dead") && CyanStatus.equals("dead") && GreenStatus.equals("dead")
						&& LemonStatus.equals("dead") && LimeStatus.equals("dead") && MagentaStatus.equals("dead")
						&& OrangeStatus.equals("dead")
						&& PeachStatus.equals("dead") && PurpleStatus.equals("dead") && RedStatus.equals("alive")
						&& WhiteStatus.equals("dead") && YellowStatus.equals("dead")) {
					GameManager.StartEndGame("red");
					cancel();
				} else if (BlueStatus.equals("dead") && CyanStatus.equals("dead") && GreenStatus.equals("dead")
						&& LemonStatus.equals("dead") && LimeStatus.equals("dead") && MagentaStatus.equals("dead")
						&& OrangeStatus.equals("dead")
						&& PeachStatus.equals("dead") && PurpleStatus.equals("dead") && RedStatus.equals("dead")
						&& WhiteStatus.equals("alive") && YellowStatus.equals("dead")) {
					GameManager.StartEndGame("white");
					cancel();
				} else if (BlueStatus.equals("dead") && CyanStatus.equals("dead") && GreenStatus.equals("dead")
						&& LemonStatus.equals("dead") && LimeStatus.equals("dead") && MagentaStatus.equals("dead")
						&& OrangeStatus.equals("dead")
						&& PeachStatus.equals("dead") && PurpleStatus.equals("dead") && RedStatus.equals("dead")
						&& WhiteStatus.equals("dead") && YellowStatus.equals("alive")) {
					GameManager.StartEndGame("yellow");
					cancel();
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
						displayfront.text(text("")
								.append(player.displayName())
								.append(text("\nKB: "))
								.append(text(pd.getDamagepercentage()))
								.append(text("% | L: "))
								.append(text(pd.getLives())));
					}
				}
			}
		}.runTaskTimer(knockoff.getInstance(), 1, 1);
	}
}

class TeamData {
	public final String name;
	public final Color color;
	public final int custom_model_data;
	public final String symbol;

	public static List<TeamData> create_teams() {
		List<TeamData> list = new ArrayList<>();
		list.add(new TeamData("blue", Color.fromRGB(0x0A42BB), 1, "\uE120 "));
		list.add(new TeamData("cyan", Color.fromRGB(0x157D91), 2, "\uE121 "));
		list.add(new TeamData("green", Color.fromRGB(0x0A971E), 3, "\uE122 "));
		list.add(new TeamData("lemon", Color.fromRGB(0xFFC500), 4, "\uE128 "));
		list.add(new TeamData("lime", Color.fromRGB(0x67E555), 5, "\uE123 "));
		list.add(new TeamData("magenta", Color.fromRGB(0xDA50E0), 6, "\uE124 "));
		list.add(new TeamData("orange", Color.fromRGB(0xFF7900), 7, "\uE129 "));
		list.add(new TeamData("peach", Color.fromRGB(0xFF8775), 8, "\uE12A "));
		list.add(new TeamData("purple", Color.fromRGB(0x7525DC), 9, "\uE12B "));
		list.add(new TeamData("red", Color.fromRGB(0xF74036), 10, "\uE125 "));
		list.add(new TeamData("white", Color.fromRGB(0xFFFFFF), 11, "\uE126 "));
		list.add(new TeamData("yellow", Color.fromRGB(0xFBE059), 12, "\uE127 "));
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

	public TeamData(String name, Color color, int custom_model_data, String symbol) {
		this.name = name;
		this.color = color;
		this.custom_model_data = custom_model_data;
		this.symbol = symbol;
	}
}
