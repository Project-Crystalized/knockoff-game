package gg.knockoff.game;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class KnockoffDatabase {

    private static String dbDir() {
        String d = System.getenv("CRYSTALIZED_DB_DIR");
        if (d == null || d.isBlank()) d = System.getProperty("user.home") + "/databases/test_dbs";
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Path.of(d));
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Could not create database directory: " + d, e);
        }
        return d;
    }

    //old location
    //private static final String URL = "jdbc:sqlite:./databases/knockoff_db.sql";
    public static final String URL = "jdbc:sqlite:" + dbDir() + "/knockoff_db.sql";

    public static void setup_databases() {
        String create_ko_games = "CREATE TABLE IF NOT EXISTS KnockoffGames ("
                + "game_id INTEGER PRIMARY KEY,"
                + "map STRING,"
                + "winner_team STRING,"
                + "gametype STRING,"
                + "timestamp INTEGER"
                + ");";
        String create_ko_players = "CREATE TABLE IF NOT EXISTS KoGamesPlayers ("
                + "game INTEGER REFERENCES KnockoffGames(game_id),"
                + "player_uuid BYTES,"
                + "team STRING,"
                + "kills INTEGER,"
                + "deaths INTEGER,"
                + "blocks_placed INTEGER,"
                + "blocks_broken INTEGER,"
                + "items_collected INTEGER,"
                + "items_used INTEGER,"
                + "games_won INTEGER"
                + ");";
        addGameIdColumn();
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

    public static void addGameIdColumn(){
        String create_id_column = "ALTER TABLE KnockoffGames ADD COLUMN game_id INTEGER;";
        String check_id_column = "SELECT game_id FROM KnockoffGames LIMIT 1;";

        String create_game_column = "ALTER TABLE KoGamesPlayers ADD COLUMN game INTEGER REFERENCES KnockoffGames(game_id);";
        String check_game_column = "SELECT game FROM KoGamesPlayers LIMIT 1;";

        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.createStatement().execute(check_id_column);
        } catch (SQLException e) {
            // if we catch a sql error, it mean the column doesnt exist, so we add it
            try (Connection conn = DriverManager.getConnection(URL)) {
                conn.createStatement().execute(create_id_column);
            } catch (SQLException ex) {
                Bukkit.getLogger().severe(ex.getMessage());
                Bukkit.getLogger().severe("uh weird error, idk bro ;-; (id)");
            }
        }

        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.createStatement().execute(check_game_column);
        } catch (SQLException e) {
            try (Connection conn = DriverManager.getConnection(URL)) {
                conn.createStatement().execute(create_game_column);
            } catch (SQLException ex) {
                Bukkit.getLogger().severe("uh weird error, idk bro ;-; (game)");
            }
        }
    }

    public static void save_game(String WinningTeam) {
        String save_game = "INSERT INTO KnockoffGames(map, winner_team, gametype, timestamp) VALUES(?, ?, ?, unixepoch())";

        try (Connection conn = DriverManager.getConnection(URL)) {
            PreparedStatement game_stmt = conn.prepareStatement(save_game);
            game_stmt.setString(1, knockoff.getInstance().mapdata.map_nameString);
            game_stmt.setString(2, WinningTeam);
            game_stmt.setString(3, GameManager.GameType.toString());
            game_stmt.executeUpdate();

            int game_id = conn.prepareStatement("SELECT last_insert_rowid();").executeQuery().getInt("last_insert_rowid()");

            String save_player = "INSERT INTO KoGamesPlayers(game, player_uuid, team, kills, deaths, blocks_placed, blocks_broken, items_collected, items_used, games_won) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement player_stmt = conn.prepareStatement(save_player);
            for (PlayerData pd : GameManager.playerDatas) {
                if (pd == null || !pd.isParticipant) continue;
                String team = Teams.GetPlayerTeam(pd.player);
                if (team == null || team.equals("spectator")) {
									Bukkit.getLogger().severe("HUH a player was a participant but has no team or is spectator??? this cant happen surely :skull: :pray:");
									continue;
								};

                player_stmt.setInt(1, game_id);
                player_stmt.setBytes(2, uuid_to_bytes(uuid_for_name(pd.player)));
                player_stmt.setString(3, team);
                player_stmt.setInt(4, pd.kills);
                player_stmt.setInt(5, pd.getDeaths());
                player_stmt.setInt(6, pd.blocksplaced);
                player_stmt.setInt(7, pd.blocksbroken);
                player_stmt.setInt(8, pd.powerupscollected);
                player_stmt.setInt(9, pd.powerupsused);
                if (WinningTeam.equals(team)) {
                    player_stmt.setInt(10, 1);
                } else {
                    player_stmt.setInt(10, 0);
                }
                player_stmt.executeUpdate();
            }

        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.getMessage());
        }
    }

    // resolves a participant name to a UUID without requiring them to be online
    private static UUID uuid_for_name(String name) {
        Player online = Bukkit.getPlayerExact(name);
        if (online != null) {
            return online.getUniqueId();
        }
        UUID cached = Bukkit.getPlayerUniqueId(name);
        if (cached != null) {
            return cached;
        }
        OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
        return offline.getUniqueId();
    }

    private static byte[] uuid_to_bytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
