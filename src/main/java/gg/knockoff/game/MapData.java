package gg.knockoff.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MapData {

    public final double[] queue_spawn;
    public static JsonElement currentSection;

    public static List<JsonElement> newSectionsList = new ArrayList<>();

    public int CurrentXLength = 0;
    public int CurrentYLength = 0;
    public int CurrentZLength = 0;

    public final Component map_name;
    public final String map_nameString;
    public final String game;
    private final int version;


    public MapData() {
        try {
            String file_content = Files.readString(Paths.get("./world/map_config.json"));
            JsonObject json = JsonParser.parseString(file_content).getAsJsonObject();

            JsonArray q_spawn = json.get("spawn").getAsJsonArray();
            this.queue_spawn = new double[] { q_spawn.get(0).getAsDouble(), q_spawn.get(1).getAsDouble(),
                    q_spawn.get(2).getAsDouble() };

            this.map_name = MiniMessage.miniMessage().deserialize(json.get("name").getAsString());
            this.map_nameString = json.get("name").getAsString();
            this.game = json.get("game").getAsString();
            this.version = json.get("version").getAsInt();

            if (!this.game.toLowerCase().equals("knockoff")) {
                Bukkit.getLogger().log(Level.SEVERE, "You've inserted a game config for \"" + this.game + "\", Please update the world file to be compatible with knockoff");
                throw new Exception();
            }
            JsonArray SectionData = json.get("section_data").getAsJsonArray();
            for (JsonElement j : SectionData) {
                newSectionsList.add(j);
            }
            Bukkit.getLogger().log(Level.INFO, "" + newSectionsList.toString());

            if (this.version != 2) {
                throw new Exception("Invalid map_config Version! Expected 2 but found " + this.version);
            }


        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load the maps configuration file!\n Error: " + e);
            e.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, "The Plugin will be disabled!");
            // disable plugin when failure
            Bukkit.getPluginManager().disablePlugin(knockoff.getInstance());
            throw new RuntimeException(e);
        }
    }

    public Location get_que_spawn(World w) {
        return new Location(w, queue_spawn[0], queue_spawn[1], queue_spawn[2]);
    }

    public JsonElement getNewRandomSection() {
        currentSection = newSectionsList.get(knockoff.getInstance().getRandomNumber(0, newSectionsList.size()));
        CurrentXLength = getNewCurrentXlength();
        CurrentYLength = getNewCurrentYlength();
        CurrentZLength = getNewCurrentZlength();
        return currentSection;
    }

    private int getNewCurrentXlength() {
        int a = currentSection.getAsJsonObject().get("from").getAsJsonArray().get(0).getAsInt();
        int b = currentSection.getAsJsonObject().get("to").getAsJsonArray().get(0).getAsInt();
        return Math.abs(a - b);
    }

    private int getNewCurrentYlength() {
        int a = currentSection.getAsJsonObject().get("from").getAsJsonArray().get(1).getAsInt();
        int b = currentSection.getAsJsonObject().get("to").getAsJsonArray().get(1).getAsInt();
        return Math.abs(a - b);
    }

    private int getNewCurrentZlength() {
        int a = currentSection.getAsJsonObject().get("from").getAsJsonArray().get(2).getAsInt();
        int b = currentSection.getAsJsonObject().get("to").getAsJsonArray().get(2).getAsInt();
        return Math.abs(a - b);
    }

    public int getCurrentXLength() {
        return GameManager.SectionPlaceLocationX + CurrentXLength;
    }
    public int getCurrentYLength() {
        return GameManager.SectionPlaceLocationY + CurrentYLength;
    }
    public int getCurrentZLength() {
        return GameManager.SectionPlaceLocationZ + CurrentZLength;
    }

    public int getCurrentMiddleXLength() {
        int i = CurrentXLength/2 + GameManager.SectionPlaceLocationX;
        return Math.round(i);
    }
    public int getCurrentMiddleYLength() {
        int i = CurrentYLength/2 + GameManager.SectionPlaceLocationY;
        return Math.round(i);
    }
    public int getCurrentMiddleZLength() {
        int i = CurrentZLength/2 + GameManager.SectionPlaceLocationZ;
        return Math.round(i);
    }
}
