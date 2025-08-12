package view;

import controller.GameController;
import controller.GameWindow;
import fungorium.GameEngine;
import java.awt.*;
import java.net.URL;
import javax.swing.*;

/**
 * A GameSetupWindow osztály egy JFrame alapú ablak, amely lehetővé teszi a játék
 * beállításainak megadását: játékosok számát és a tektónok számát.
 * 
 * Ellenőrzi a bevitt értékeket, és ha helyesek, elindítja a játékot.
 */
public class GameSetupWindow extends JFrame {

    private JTextField playersField;
    private JTextField tectonsField;
    private JLabel errorLabel;
    private JPanel contentPanel;

    /**
     * Konstruktor, amely inicializálja a beállító ablakot,
     * beállítja a felület elemeit és a háttérképet.
     */
    public GameSetupWindow() {
        setTitle("Game Setup");
        setSize(1300, 780);
        setResizable(false);
        setLocationRelativeTo(null); // középre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPanel = new JPanel() {
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
        };
        contentPanel.setLayout(null);
        setContentPane(contentPanel);

        // Címkék és mezők
        JLabel playersLabel = new JLabel("Number of Players (1-3):");
        playersLabel.setOpaque(true);
        playersLabel.setBackground(Color.WHITE);
        playersLabel.setForeground(Color.BLACK);
        playersLabel.setBounds(450, 200, 250, 30);
        contentPanel.add(playersLabel);

        playersField = new JTextField();
        playersField.setBounds(720, 200, 100, 30);
        playersField.setBackground(Color.WHITE);
        playersField.setForeground(Color.BLACK);
        contentPanel.add(playersField);

        JLabel tectonsLabel = new JLabel("Number of Tectons (20-49):");
        tectonsLabel.setOpaque(true);
        tectonsLabel.setBackground(Color.WHITE);
        tectonsLabel.setForeground(Color.BLACK);
        tectonsLabel.setBounds(450, 250, 250, 30);
        contentPanel.add(tectonsLabel);

        tectonsField = new JTextField();
        tectonsField.setBounds(720, 250, 100, 30);
        tectonsField.setBackground(Color.WHITE);
        tectonsField.setForeground(Color.BLACK);
        contentPanel.add(tectonsField);

        // Finish gomb
        JButton finishButton = new JButton("Finish");
        finishButton.setBounds(680, 320, 120, 40);
        finishButton.addActionListener(e -> validateAndStart(true));
        contentPanel.add(finishButton);

        // Hibaüzenet
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setBounds(500, 380, 400, 30);
        contentPanel.add(errorLabel);
    }

    /**
     * Ellenőrzi a felhasználó által megadott értékeket, és ha helyesek,
     * inicializálja és elindítja a játékot.
     *
     * @param startEngine ha true, akkor a játékot elindítja, különben csak validál
     */
    private void validateAndStart(boolean startEngine) {
        try {
            int players = Integer.parseInt(playersField.getText().trim());
            int tectons = Integer.parseInt(tectonsField.getText().trim());

            if (players < 1 || players > 3) {
                errorLabel.setText("Hiba: Játékosok száma 1 és 3 között kell legyen.");
                return;
            }

            if (tectons < 20 || tectons > 49) {
                errorLabel.setText("Hiba: Tektónok száma 20 és 49 között kell legyen.");
                return;
            }

            if (startEngine) {
                // Játék indítása
                GameEngine engine = GameEngine.getInstance();
                GameEngine.setPlayerNumber(players);
                GameEngine.setTectonNumber(tectons);
                engine.createMap();
                engine.generatePlayers();
                //engine.startGame();
                GameBoardView panel = new GameBoardView(engine);
                InfoBoardView info = new InfoBoardView(panel);

                GameController controller = new GameController(engine, info, panel);
                GameWindow window = new GameWindow(1300, 780, panel, info);

                window.pack();
                window.setVisible(true);
            } else {
                System.out.println("Setup OK: " + players + " játékos, " + tectons + " tektón");
            }

            dispose(); // bezárjuk az ablakot

        } catch (NumberFormatException ex) {
            errorLabel.setText("Hiba: Csak számot írj be!");
        }
    }
}
