package gg.knockoff.game;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KnockoffDatabase {

    private static final String URL = "jdbc:sqlite:./databases/knockoff_db.sql";

    public static void setup_databases() {
        String create_ko_games = "CREATE TABLE IF NOT EXISTS KnockoffGames ("
                + "map STRING,"
                + "winner_team STRING,"
                + "gametype STRING"
                + ");";
        String create_ko_players = "CREATE TABLE IF NOT EXISTS KoGamesPlayers ("
                + "player_uuid BYTES,"
                + "team STRING,"
                + "kills INTEGER,"
                + "deaths INTEGER,"
                + "blocks_placed INTEGER,"
                + "blocks_broken INTEGER,"
                + "items_collected INTEGER,"
                + "items_used INTEGER"
                + "games_won INTEGER"
                + ");";

        try (Connection conn = DriverManager.getConnection(URL)) {
            Statement stmt = conn.createStatement();
            stmt.execute(create_ko_games);
            stmt.execute(create_ko_players);
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.getMessage());
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                Bukkit.getLogger().severe(ste.toString());
            }
        }
    }

    public static void save_game() {
        String save_game = "INSERT INTO KnockoffGames(map, winner_team, gametype) VALUES(?, ?, ?)";
        GameManager gm = knockoff.getInstance().GameManager;

        try (Connection conn = DriverManager.getConnection(URL)) {
            PreparedStatement game_stmt = conn.prepareStatement(save_game);
            game_stmt.setString(1, knockoff.getInstance().mapdata.map_name);
            game_stmt.setString(2, "placeholder");
            game_stmt.setString(3, GameManager.GameType);
            game_stmt.executeUpdate();

            int game_id = conn.prepareStatement("SELECT last_insert_rowid();").executeQuery().getInt("last_insert_rowid()");

            String save_player = "INSERT INTO KoGamesPlayers(player_uuid, team, kills, deaths, blocks_placed, blocks_broken, items_collected, items_used, games_won)"
                    + " VALUES(?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement player_stmt = conn.prepareStatement(save_player);
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerData pd = gm.getPlayerData(p);

                player_stmt.setBytes(1, uuid_to_bytes(p));
                player_stmt.setString(2, Teams.GetPlayerTeam(p));
                player_stmt.setInt(3, pd.kills);
                player_stmt.setInt(4, pd.getDeaths());
                player_stmt.setInt(5, pd.blocksplaced);
                player_stmt.setInt(6, pd.blocksbroken);
                player_stmt.setInt(7, pd.powerupscollected);
                player_stmt.setInt(8, pd.powerupsused);
                player_stmt.setInt(9, 0); //TODO
                player_stmt.executeUpdate();
            }

        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.getMessage());
        }
    }

    private static byte[] uuid_to_bytes(Player p) {
        ByteBuffer bb = ByteBuffer.allocate(16);
        UUID uuid = p.getUniqueId();
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
