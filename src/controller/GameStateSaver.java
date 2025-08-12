package controller;

import fungorium.Insect;
import fungorium.MushBody;
import fungorium.MushSpore;
import fungorium.MushThread;
import fungorium.Mushroom;
import fungorium.Tecton;
import java.io.*;
import java.util.List;
import view.TectonView;

/**
 * Osztály, amely felelős a játék állapotának mentéséért JSON formátumban fájlba.
 */
public class GameStateSaver {

    /**
     * Elmenti a játék állapotát JSON formátumban egy megadott fájlba.
     *
     * @param gameState az elmenteni kívánt játékállapot DTO objektum
     * @param fileName  a célfájl neve vagy elérési útja
     */
    public static void saveToFile(GameStateDTO gameState, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{\n");
            jsonBuilder.append("\"round\": ").append(gameState.round).append(",\n");
            // Tectonok kiírása
            jsonBuilder.append("\"tectons\": [\n");
            List<TectonView> tectonViews = gameState.tectons;
            for (int i = 0; i < tectonViews.size(); i++) {
                TectonView tectonView = tectonViews.get(i);
                Tecton tecton = tectonView.getTecton();

                jsonBuilder.append("  {\n");
                jsonBuilder.append("    \"id\": ").append(tecton.getId()).append(",\n");
                jsonBuilder.append("    \"cracked\": ").append(tecton.isCracked()).append(",\n");
                jsonBuilder.append("    \"crackTime\": ").append(tecton.getCrackTime()).append(",\n");
                jsonBuilder.append("    \"x\": ").append(tectonView.getX()).append(",\n");
                jsonBuilder.append("    \"y\": ").append(tectonView.getY()).append(",\n");

                // Neighbors
                jsonBuilder.append("    \"neighbors\": [");
                List<Tecton> neighbors = tecton.getNeighbors();
                for (int j = 0; j < neighbors.size(); j++) {
                    jsonBuilder.append(neighbors.get(j).getId());
                    if (j < neighbors.size() - 1)
                        jsonBuilder.append(", ");
                }
                jsonBuilder.append("],\n");

                // Spores
                jsonBuilder.append("    \"spores\": [\n");
                List<MushSpore> spores = tecton.getSpores();
                for (int j = 0; j < spores.size(); j++) {
                    MushSpore spore = spores.get(j);
                    jsonBuilder.append("      {\n");
                    jsonBuilder.append("        \"value\": ").append(spore.getValue()).append(",\n");
                    jsonBuilder.append("        \"type\": \"").append(spore.getType()).append("\",\n");
                    jsonBuilder.append("        \"x\": ").append(tectonView.getX()).append(",\n");
                    jsonBuilder.append("        \"y\": ").append(tectonView.getY()).append("\n");
                    jsonBuilder.append("      }");
                    if (j < spores.size() - 1)
                        jsonBuilder.append(",");
                    jsonBuilder.append("\n");
                }
                jsonBuilder.append("    ],\n");

                // MushBody
                MushBody body = tecton.getMushbody();
                if (body != null) {
                    jsonBuilder.append("    \"mushbody\": {\n");
                    jsonBuilder.append("      \"mature\": ").append(body.isMature()).append(",\n");
                    jsonBuilder.append("      \"x\": ").append(tectonView.getX()).append(",\n");
                    jsonBuilder.append("      \"y\": ").append(tectonView.getY()).append("\n");
                    jsonBuilder.append("    },\n");
                }

                // Threads
                jsonBuilder.append("    \"threads\": [\n");
                List<MushThread> threads = tecton.getThreads();
                for (int j = 0; j < threads.size(); j++) {
                    MushThread thread = threads.get(j);
                    jsonBuilder.append("      {\n");
                    jsonBuilder.append("        \"timeToDie\": ").append(thread.getTimeToDie()).append("\n");
                    jsonBuilder.append("      }");
                    if (j < threads.size() - 1)
                        jsonBuilder.append(",");
                    jsonBuilder.append("\n");
                }
                jsonBuilder.append("    ]\n");

                jsonBuilder.append("  }");
                if (i < tectonViews.size() - 1)
                    jsonBuilder.append(",");
                jsonBuilder.append("\n");
            }
            jsonBuilder.append("],\n");

            // 🐞 Insects hozzáadása
            jsonBuilder.append("\"insects\": [\n");
            List<Insect> insects = gameState.insects;
            for (int i = 0; i < insects.size(); i++) {
                Insect insect = insects.get(i);
                jsonBuilder.append("  {\n");
                jsonBuilder.append("    \"name\": \"").append(insect.getName()).append("\",\n");
                jsonBuilder.append("    \"score\": ").append(insect.getScore()).append(",\n");
                jsonBuilder.append("    \"speed\": ").append(insect.getSpeed()).append(",\n");
                jsonBuilder.append("    \"cutSkill\": ").append(insect.getCutSkill()).append(",\n");

                Tecton pos = insect.getPosition();
                jsonBuilder.append("    \"position\": {\n");
                jsonBuilder.append("      \"id\": ").append(pos.getId()).append("\n");
                jsonBuilder.append("    }\n");

                jsonBuilder.append("  }");
                if (i < insects.size() - 1)
                    jsonBuilder.append(",");
                jsonBuilder.append("\n");
            }
            jsonBuilder.append("],\n");

            // 🍄 Mushrooms hozzáadása
            jsonBuilder.append("\"mushrooms\": [\n");
            List<Mushroom> mushrooms = gameState.mushrooms;
            for (int i = 0; i < mushrooms.size(); i++) {
                Mushroom mushroom = mushrooms.get(i);
                jsonBuilder.append("  {\n");
                jsonBuilder.append("    \"name\": \"").append(mushroom.getName()).append("\",\n");
                jsonBuilder.append("    \"score\": ").append(mushroom.getScore()).append(",\n");

                List<MushBody> mushbodies = mushroom.getMushBodies();
                jsonBuilder.append("    \"mushbodies\": [\n");
                for (int j = 0; j < mushbodies.size(); j++) {
                    MushBody body = mushbodies.get(j);
                    jsonBuilder.append("      {\n");
                    jsonBuilder.append("        \"mature\": ").append(body.isMature()).append(",\n");
                    jsonBuilder.append("      }");
                    if (j < mushbodies.size() - 1)
                        jsonBuilder.append(",");
                    jsonBuilder.append("\n");
                }
                jsonBuilder.append("    ]\n");

                jsonBuilder.append("  }");
                if (i < mushrooms.size() - 1)
                    jsonBuilder.append(",");
                jsonBuilder.append("\n");
            }
            jsonBuilder.append("]\n");

            jsonBuilder.append("}\n");

            writer.write(jsonBuilder.toString());
            System.out.println("Game state saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
