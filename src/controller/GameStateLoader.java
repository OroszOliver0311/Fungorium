package controller;

import fungorium.*;
import java.io.*;
import java.util.*;
import view.TectonView;

/**
 * A játék állapotának betöltéséért felelős osztály,
 * amely JSON formátumú fájlból olvassa be a játék állapotát.
 */
public class GameStateLoader {

    /**
     * Betölti a játék állapotát egy JSON fájlból.
     * 
     * @param fileName a fájl neve vagy elérési útja, ahonnan be kell olvasni
     * @return a beolvasott játékállapot DTO objektumként
     */
    public static GameStateDTO loadFromFile(String fileName) {
        GameStateDTO gameState = new GameStateDTO();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }

            String json = jsonBuilder.toString();

            // Round érték kiolvasása
            gameState.round = parseIntValue(json, "\"round\":");

            // TectonView lista beolvasása
            gameState.tectons = parseTectons(json);

            // Insects lista beolvasása
            gameState.insects = parseInsects(json);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return gameState;
    }

    /**
     * Kiolvassa egy egész szám értékét egy JSON sztringből egy adott kulcs alapján.
     * 
     * @param json a JSON sztring
     * @param key a keresett kulcs, pl. "\"round\":"
     * @return a kulcshoz tartozó egész szám, vagy 0, ha nem található
     */
    private static int parseIntValue(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1)
            return 0;
        int start = idx + key.length();
        int end = json.indexOf(",", start);
        if (end == -1)
            end = json.indexOf("}", start);
        return Integer.parseInt(json.substring(start, end).trim());
    }

    /**
     * Beolvassa a TectonView objektumokat egy JSON sztringből.
     * 
     * @param json a JSON sztring
     * @return a TectonView objektumok listája
     */
    private static List<TectonView> parseTectons(String json) {
        List<TectonView> tectons = new ArrayList<>();

        String tectonArrayStart = "\"tectons\":[";
        int startIdx = json.indexOf(tectonArrayStart);
        if (startIdx == -1)
            return tectons;

        int endIdx = json.indexOf("]", startIdx);
        String tectonArray = json.substring(startIdx + tectonArrayStart.length(), endIdx);

        String[] tectonItems = tectonArray.split("\\},\\{");
        for (String rawTecton : tectonItems) {
            String tectonJson = rawTecton.replace("{", "").replace("}", "");

            int id = parseIntValue(tectonJson, "\"id\":");
            boolean cracked = tectonJson.contains("\"cracked\":true");
            int crackTime = parseIntValue(tectonJson, "\"crackTime\":");
            int x = parseIntValue(tectonJson, "\"x\":");
            int y = parseIntValue(tectonJson, "\"y\":");

            Tecton tecton = new SoloTecton(crackTime);
            tecton.setCracked(cracked);
            tecton.setCrackTime(crackTime);

            TectonView view = new TectonView(x, y, tecton);
            view.setTecton(tecton);

            tectons.add(view);
        }

        return tectons;
    }

    /**
     * Beolvassa az Insect objektumokat egy JSON sztringből.
     * 
     * @param json a JSON sztring
     * @return az Insect objektumok listája
     */
    private static List<Insect> parseInsects(String json) {
        List<Insect> insects = new ArrayList<>();

        String insectArrayStart = "\"insects\":[";
        int startIdx = json.indexOf(insectArrayStart);
        if (startIdx == -1)
            return insects;

        int endIdx = json.indexOf("]", startIdx);
        String insectArray = json.substring(startIdx + insectArrayStart.length(), endIdx);

        String[] insectItems = insectArray.split("\\},\\{");
        for (String rawInsect : insectItems) {
            String insectJson = rawInsect.replace("{", "").replace("}", "");

            String name = parseStringValue(insectJson, "\"name\":");
            int score = parseIntValue(insectJson, "\"score\":");
            int speed = parseIntValue(insectJson, "\"speed\":");
            boolean cutSkill = parseBooleanValue(insectJson, "\"cutSkill\":");

            int positionId = parseIntValue(insectJson, "\"id\":");
            GameEngine engine = GameEngine.getInstance();
            Tecton position = engine.getTectonById(positionId);
            Insect insect = new Insect(name, score, speed, cutSkill, position);
            insect.setScore(score);
            insects.add(insect);
        }

        return insects;
    }

    /**
     * Kiolvassa egy logikai értéket egy JSON sztringből egy adott kulcs alapján.
     * 
     * @param json a JSON sztring
     * @param key a keresett kulcs
     * @return a kulcshoz tartozó logikai érték, alapértelmezetten false
     */
    private static boolean parseBooleanValue(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1)
            return false;
        int start = idx + key.length();
        String value = json.substring(start).trim();
        return value.startsWith("true");
    }

    /**
     * Kiolvassa egy szöveges értéket egy JSON sztringből egy adott kulcs alapján.
     * 
     * @param json a JSON sztring
     * @param key a keresett kulcs
     * @return a kulcshoz tartozó sztring, vagy üres, ha nem található
     */
    private static String parseStringValue(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1)
            return "";
        int start = json.indexOf("\"", idx + key.length()) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
