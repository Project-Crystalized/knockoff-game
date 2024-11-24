package gg.knockoff.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigData {

    public boolean enable_tourney_mode = false;
    public boolean enable_manual_hazard_control = false;
    public boolean enable_manual_map_control = false;

    public ConfigData() {
        File config = new File("./plugins/Knockoff", "config.json");
        if (!config.exists()) {
            URL url = getClass().getResource("config.json");
            try {
                FileUtils.copyURLToFile(url, config);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                String file_content = Files.readString(Paths.get("./plugins/Knockoff/config.json"));
                JsonObject json = JsonParser.parseString(file_content).getAsJsonObject();

                this.enable_tourney_mode = json.get("emable_tourney_mode").getAsBoolean();
                this.enable_manual_hazard_control = json.get("emable_manual_hazard_control").getAsBoolean();
                this.enable_manual_map_control = json.get("emable_manual_hazard_control").getAsBoolean();

                JsonArray team_blue = json.get("team_blue").getAsJsonArray();
                JsonArray team_cyan = json.get("team_cyan").getAsJsonArray();
                JsonArray team_green = json.get("team_green").getAsJsonArray();
                JsonArray team_lemon = json.get("team_lemon").getAsJsonArray();
                JsonArray team_lime = json.get("team_lime").getAsJsonArray();
                JsonArray team_magenta = json.get("team_magenta").getAsJsonArray();
                JsonArray team_orange = json.get("team_orange").getAsJsonArray();
                JsonArray team_peach = json.get("team_peach").getAsJsonArray();
                JsonArray team_purple = json.get("team_purple").getAsJsonArray();
                JsonArray team_red = json.get("team_red").getAsJsonArray();
                JsonArray team_white = json.get("team_white").getAsJsonArray();
                JsonArray team_yellow = json.get("team_yellow").getAsJsonArray();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
