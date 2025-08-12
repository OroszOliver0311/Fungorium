package view;

import controller.GameStateDTO;
import controller.GameStateLoader;
import controller.GameWindow;
import fungorium.GameEngine;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * A játék főmenüjét megjelenítő osztály.
 * Innen indítható új játék, mentett játék betöltése, illetve kilépés az alkalmazásból.
 */
public class Menu extends JPanel {

    private JFrame frame;
    private JComboBox<String> saveFileComboBox;
    private boolean comboBoxVisible = false;

    /**
     * Létrehozza és inicializálja a menü felhasználói felületét.
     */
    public Menu() {
        int width = 1300;
        int height = 780;

        frame = new JFrame("Game Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // középre helyezi az ablakot

        this.setLayout(null);
        this.setBackground(Color.BLACK);

        int buttonWidth = 300;
        int buttonHeight = 60;
        int centerX = (width - buttonWidth) / 2;

        JButton newGameButton = createButton("New Game", centerX, 250, buttonWidth, buttonHeight);
        JButton loadButton = createButton("Load", centerX, 350, buttonWidth, buttonHeight);
        JButton exitButton = createButton("Exit", centerX, 450, buttonWidth, buttonHeight);

        newGameButton.addActionListener(e -> startGame());
        loadButton.addActionListener(e -> loadGame());
        exitButton.addActionListener(e -> System.exit(0));

        this.add(newGameButton);
        this.add(loadButton);
        this.add(exitButton);

        frame.setContentPane(this);
        frame.setVisible(true);
    }

    /**
     * Segédfüggvény gomb létrehozására a megadott tulajdonságokkal.
     *
     * @param text   a gomb felirata
     * @param x      vízszintes pozíció
     * @param y      függőleges pozíció
     * @param width  gomb szélessége
     * @param height gomb magassága
     * @return az elkészített JButton
     */
    private JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        return button;
    }

    /**
     * Új játék indítását kezeli, bezárja a főmenüt és megnyitja a GameSetupWindow ablakot.
     */
    private void startGame() {
        frame.dispose(); // bezárja a menüt
        new GameSetupWindow().setVisible(true); // új ablak

    }

    /**
     * Kezeli a mentett játékok betöltésének elindítását.
     * Ha vannak elérhető mentések, megjelenít egy legördülő listát.
     */
    private void loadGame() {
        if (!comboBoxVisible) {
            List<String> savedFiles = getSavedGameFiles();
            if (savedFiles.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nincs elérhető mentett játék.", "Hiba",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            saveFileComboBox = new JComboBox<>(savedFiles.toArray(new String[0]));
            saveFileComboBox.setBounds(500, 420, 300, 30); // a Load gomb alá
            this.add(saveFileComboBox);
            this.repaint();
            this.revalidate();

            comboBoxVisible = true;

            // opcionális: eseménykezelő ha kiválasztanak valamit
            saveFileComboBox.addActionListener(e -> {
                String selectedFile = (String) saveFileComboBox.getSelectedItem();
                if (selectedFile != null) {
                    loadSelectedGame(selectedFile);
                }
            });
        }
    }

    /**
     * Visszaadja az elérhető mentett játékfájlokat (csak .json kiterjesztésűek).
     *
     * @return mentett játékok fájlneveinek listája
     */
    private List<String> getSavedGameFiles() {
        File folder = new File("savedgames");
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
            }
        }
        return fileNames;
    }

    /**
     * A kiválasztott mentett játék betöltése.
     *
     * @param selectedFile a kiválasztott mentés fájlneve
     */
    private void loadSelectedGame(String selectedFile) {
        String fullPath = "savedgames/" + selectedFile;
        GameStateDTO gameState = GameStateLoader.loadFromFile(fullPath);
        GameEngine engine = GameEngine.getInstance();
        engine.loadGameState(gameState); // Feltételezve, hogy van ilyen metódusod

        // Megjelenítjük a játéktáblát az állapot alapján
        GameBoardView panel = new GameBoardView(engine);
        InfoBoardView infoPanel = new InfoBoardView(panel);
        GameWindow window = new GameWindow(1300, 780, panel, infoPanel);
        window.pack();
        window.setVisible(true);
        this.frame.dispose();
    }

     /**
     * A menü háttérképének kirajzolása.
     *
     * @param g a grafikus objektum, amire rajzol
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            URL imageUrl = getClass().getResource("menu.png");
            if (imageUrl != null) {
                Image background = new ImageIcon(imageUrl).getImage();
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            } else {
                System.err.println("HIBA: Nem található a háttérkép (menu.png)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
