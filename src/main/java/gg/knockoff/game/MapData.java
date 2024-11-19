package gg.knockoff.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

public class MapData {

    public final double[] que_spawn;
    public final JsonArray sections;
    public final JsonArray sectionslist = new JsonArray();
    public static JsonArray currentsection = new JsonArray();

    public int CurrentXLength = 0;
    public int CurrentYLength = 0;
    public int CurrentZLength = 0;

    public final String map_name;
    public final String game;


    public MapData() {
        try {
            String file_content = Files.readString(Paths.get("./world/map_config.json"));
            JsonObject json = JsonParser.parseString(file_content).getAsJsonObject();

            JsonArray q_spawn = json.get("que_spawn").getAsJsonArray();
            this.que_spawn = new double[] { q_spawn.get(0).getAsDouble(), q_spawn.get(1).getAsDouble(),
                    q_spawn.get(2).getAsDouble() };

            this.map_name = json.get("map_name").getAsString();
            this.game = json.get("game").getAsString();

            //this is a pain to figure out - Callum
            JsonArray sections = json.get("sections").getAsJsonArray();
            this.sections = json.get("sections").getAsJsonArray();
            int i = 0;
            for (int s = this.sections.size()/8;;) {
                if (i*8 == this.sections.size() == true) {
                    return;
                } else {
                    sectionslist.add("section" + i);
                    sectionslist.add(sections.get(0 + i*8));
                    sectionslist.add(sections.get(1 + i*8));
                    sectionslist.add(sections.get(2 + i*8));
                    sectionslist.add(sections.get(3 + i*8));
                    sectionslist.add(sections.get(4 + i*8));
                    sectionslist.add(sections.get(5 + i*8));
                    sectionslist.add(sections.get(6 + i*8));
                    sectionslist.add(sections.get(7 + i*8));
                }
                i++;
                Bukkit.getLogger().log(Level.WARNING, "" + sectionslist);
                Bukkit.getLogger().log(Level.INFO, "" + this.sections.size() + " " + i);
            }
            // for statement above should generate something similar to this
            // ["section1", -18, 19, 34, 46, -6, -29, "waxed_copper_block, 1"]
            // Format is: From X, From Y, From Z, To X, To Y, To Z, removeblock, middlecoord

        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load the maps configuration file!\n Error: " + e);
            e.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, "The Plugin will be disabled!");
            // disable plugin when failure
            Bukkit.getPluginManager().disablePlugin(knockoff.getInstance());
            throw new RuntimeException(new Exception());
        }
    }

    public Location get_que_spawn(World w) {
        return new Location(w, que_spawn[0], que_spawn[1], que_spawn[2]);
    }

    public String toString() {
        return "" + this.sections;
    }

    public JsonArray getrandommapsection() {
        Bukkit.getLogger().log(Level.INFO, "OLD: " + currentsection); //for debugging
        while(currentsection.size()>0)
        {
            currentsection.remove(0);
        }

        int n = (int)(Math.random() * sectionslist.size()/8 - 1);
        currentsection.add(sectionslist.get(0 + n * 9));
        currentsection.add(sectionslist.get(1 + n * 9));
        currentsection.add(sectionslist.get(2 + n * 9));
        currentsection.add(sectionslist.get(3 + n * 9));
        currentsection.add(sectionslist.get(4 + n * 9));
        currentsection.add(sectionslist.get(5 + n * 9));
        currentsection.add(sectionslist.get(6 + n * 9));
        currentsection.add(sectionslist.get(7 + n * 9));
        currentsection.add(sectionslist.get(8 + n * 9));

        Bukkit.getLogger().log(Level.INFO, "NEW: " + currentsection); //for debugging

        CurrentXLength = getNewCurrentXlength();
        CurrentYLength = getNewCurrentYlength();
        CurrentZLength = getNewCurrentZlength();

        return currentsection;
    }

    public JsonArray getCurrentsection() {
        return currentsection;
    }

    private int getNewCurrentXlength() {
        int a = currentsection.get(1).getAsInt();
        int b = currentsection.get(4).getAsInt();
        int i = a;
        int j = a;
        int c = 0;
        while (i != b || j != b) {
            if (i == b) {
                c = i;
                return c;
            }
            else if (j == b) {
                c = j;
                return c;
            } else {
                i++;
                j--;
            }
        }
        return c;
    }

    private int getNewCurrentYlength() {
        int a = currentsection.get(2).getAsInt();
        int b = currentsection.get(5).getAsInt();
        int i = a;
        int j = a;
        int c = 0;
        while (i != b || j != b) {
            if (i == b) {
                c = i;
                return c;
            }
            else if (j == b) {
                c = j;
                return c;
            } else {
                i++;
                j--;
            }
        }
        return c;
    }

    private int getNewCurrentZlength() {
        int a = currentsection.get(3).getAsInt();
        int b = currentsection.get(6).getAsInt();
        int i = a;
        int j = a;
        int c = 0;
        while (i != b || j != b) {
            if (i == b) {
                c = i;
                return c;
            }
            else if (j == b) {
                c = j;
                return c;
            } else {
                i++;
                j--;
            }
        }
        return c;
    }

    public int getCurrentXLength() {
        return CurrentXLength/2;
    }
    public int getCurrentYLength() {
        return CurrentYLength/2;
    }
    public int getCurrentZLength() {
        return CurrentZLength/2;
    }


    public int getCurrentMiddleXLength() {
        int i = CurrentXLength/2;
        return Math.round(i);
    }
    public int getCurrentMiddleYLength() {
        int i = CurrentYLength/2;
        return Math.round(i);
    }
    public int getCurrentMiddleZLength() {
        int i = CurrentZLength/2;
        return Math.round(i);
    }
}
