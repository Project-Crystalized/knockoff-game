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
    public int LastXLength = 0;
    public int LastYLength = 0;
    public int LastZLength = 0;

    public final Component map_name;
    public final String map_nameString;
    public final String game;
    private final int version;

    public MapExtraFeatures extras;

    public MapData() {
        JsonObject json;

        try {
            String file_content = Files.readString(Paths.get("./world/map_config.json"));
            json = JsonParser.parseString(file_content).getAsJsonObject();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot read the map_config.json file, is it in the correct place?");
            e.printStackTrace();
            // disable plugin when failure
            Bukkit.getPluginManager().disablePlugin(knockoff.getInstance());
            throw new RuntimeException(e);
        }

        JsonArray q_spawn = json.get("spawn").getAsJsonArray();
        this.queue_spawn = new double[] { q_spawn.get(0).getAsDouble(), q_spawn.get(1).getAsDouble(),
                q_spawn.get(2).getAsDouble() };

        this.map_name = MiniMessage.miniMessage().deserialize(json.get("name").getAsString());
        this.map_nameString = json.get("name").getAsString();
        this.game = json.get("game").getAsString();
        this.version = json.get("version").getAsInt();

        if (!this.game.toLowerCase().equals("knockoff")) {
            crash("You've inserted a game config for \"" + this.game + "\", Please update the world file to be compatible with knockoff");
        }
        JsonArray SectionData = json.get("section_data").getAsJsonArray();
        for (JsonElement j : SectionData) {
            newSectionsList.add(j);
        }
        if (this.version != 2) {
            crash("Invalid map_config Version! Expected 2 but found " + this.version);
        }
        extras = new MapExtraFeatures(json);
    }

    // use this as a substitute for throwing an exception on plugin startup
    private void crash(String reason) {
        knockoff.getInstance().getLogger().log(Level.SEVERE, "Error occurred in MapData:");
        Bukkit.getPluginManager().disablePlugin(knockoff.getInstance());
        throw new RuntimeException(reason);
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

class MapExtraFeatures {

    public boolean JumpBoostPads = false;
    public boolean LevitationPads = false;
    public boolean BoostPads = false;
    public boolean PassiveCopperGolems = false; //Will also spawn items in (Copper) Chests to get the copper golems to move

    public String exclusiveHazard = "";

    JsonObject data;

    public MapExtraFeatures(JsonObject json) {
        try {
            data = json.get("extras").getAsJsonObject();
        } catch (Exception ex) {
            return;
        }

        JumpBoostPads = getBool("JumpBoostPads");
        LevitationPads = getBool("LevitationPads");
        BoostPads = getBool("BoostPads");
        PassiveCopperGolems = getBool("PassiveCopperGolems");
        setupExclusiveHazard();

        Bukkit.getLogger().log(Level.INFO, "MapExtraFeatures initialized");
    }

    private boolean getBool(String bool) {
        try {
            return data.get(bool).getAsBoolean();
        } catch (Exception ex) {
            return false;
        }
    }

    private void setupExclusiveHazard() {
        try {
            String temp = data.get("exclusiveHazard").getAsString();
            switch (temp) {
                case "TrialChamber", "Elements" -> {
                    exclusiveHazard = temp;
                }
                default -> {
                    Bukkit.getLogger().log(Level.WARNING, "[Knockoff/MapData] Unknown/Unimplemented Exclusive Hazard \"" + temp + "\". Refer to the crystalized github wiki documentation for more info.");
                }
            }
        } catch (Exception ex) {
            exclusiveHazard = "";
        }
    }
}
