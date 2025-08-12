package view;

import controller.GameStateDTO;
import controller.GameStateSaver;
import controller.GameStatusListener;
import fungorium.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * Az InfoBoardView a játék állapotát és vezérlő elemeit megjelenítő panel.
 * Figyeli a játék állapotának változásait, és frissíti a megjelenítést.
 * 
 * Megjeleníti az aktuális játékost, körszámot, eseményeket, valamint
 * lehetőséget ad a játékos számára különböző műveletek végrehajtására.
 */
public class InfoBoardView extends JPanel implements Observer, GameStatusListener {
    private GameEngine gameEngine = GameEngine.getInstance();

    private JTextField plaxerTextField;
    private JTextField logTextField;
    private JTextField roundTextField;

    private JComboBox<String> dynamicComboBox;
    private JButton insectMoveButton;
    private JButton insectEatButton;
    private JButton insectCutButton;

    private JButton mushroomGrowButton;
    private JButton mushroomExtendButton;
    private JButton mushroomFireButton;
    private JButton mushroomEatInsectButton;
    private JButton skipButton;

    private GameBoardView gameBoardView;
    public JTextArea infoField;

    /**
     * Eseménykezelő, amely értesül arról, ha az aktuális játékos változik.
     * Frissíti a megjelenített játékos nevét, a körszámot, a gombok állapotát,
     * a ComboBox tartalmát és ellenőrzi, hogy véget ért-e a játék.
     *
     * @param playerName Az aktuális játékos neve
     */
    @Override
    public void onCurrentPlayerChanged(String playerName) {
        SwingUtilities.invokeLater(() -> {
            plaxerTextField.setText(playerName);
            roundTextField.setText(Integer.toString(gameEngine.getRound()));
            updateButtonStates(playerName);
            showComboBoxOptions(); // <- itt frissítjük a ComboBoxot
            checkGameOver();
        });
    }

    /**
     * Konstruktor, amely inicializálja az InfoBoardView komponenseit és elrendezését.
     *
     * @param gbv A GameBoardView, amelyhez az InfoBoardView kapcsolódik
     */
    public InfoBoardView(GameBoardView gbv) {
        gameBoardView = gbv;
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(300, 780));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // térköz
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0; // sor számláló

        // Cím címke
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 3;
        add(new JLabel("Fungórium", SwingConstants.CENTER), gbc);

        y++; // Új sor #########################################

        // Játékos kiírás sor
        gbc.gridy = y;
        gbc.gridwidth = 1;
        add(new JLabel("Player:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        plaxerTextField = new JTextField();
        plaxerTextField.setEditable(false);
        plaxerTextField.setText(GameEngine.getInstance().getMushrooms().get(0).getName());

        add(plaxerTextField, gbc);
        GameEngine.getInstance().addGameStatusListener(this);

        y++; // Új sor #########################################

        // Kör számláló
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        add(new JLabel("Hátralévő körök száma:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        roundTextField = new JTextField();
        roundTextField.setEditable(false);
        int round = gameEngine.getRound();
        roundTextField.setText(Integer.toString(round));
        add(roundTextField, gbc);

        y++; // Új sor #########################################

        // Nagy szövegdoboz – Tekton infó
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1; // Nyújtózkodhat
        String defaultText = "";
        int playerNumber = GameEngine.getInstance().getPlayerNumber();
        defaultText += "   i0: green     m0: blue\n";
        if(playerNumber >= 2)
            defaultText +=  "   i1: blue        m1: red\n";
        if(playerNumber == 3)
            defaultText += "   i2: red         m2: green\n";
        defaultText += "\n\n\n\n Jó játékot!";
        infoField = new JTextArea(defaultText);
        infoField.setEditable(false);
        add(infoField, gbc);
        gbc.weighty = 0; // Most már ne nyújtózkodjon

        y++; // Új sor #########################################

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dynamicComboBox = new JComboBox<>();
        dynamicComboBox.setVisible(true); // Alapból nem látszik
        add(dynamicComboBox, gbc);
        showComboBoxOptions();

        y++; // Új sor #########################################

        // Insect / Mushroom gomb fejlécek
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(new JLabel("Insect", SwingConstants.CENTER), gbc);
        gbc.gridx = 1;
        add(new JLabel("Mushroom", SwingConstants.CENTER), gbc);

        y++; // Új sor #########################################

        // Insect Move gomb
        gbc.gridy = y;
        gbc.gridx = 0;
        insectMoveButton = new JButton("Move");
        add(insectMoveButton, gbc);

        // Mushroom Grow gomb
        gbc.gridx = 1;
        mushroomGrowButton = new JButton("Grow");
        add(mushroomGrowButton, gbc);

        y++; // Új sor #########################################

        // Insect Eat gomb
        gbc.gridy = y;
        gbc.gridx = 0;
        insectEatButton = new JButton("Eat");
        add(insectEatButton, gbc);

        // Mushroom Extend gomb
        gbc.gridx = 1;
        mushroomExtendButton = new JButton("Extend");
        add(mushroomExtendButton, gbc);

        y++; // Új sor #########################################

        // Insect Cut gomb
        gbc.gridy = y;
        gbc.gridx = 0;
        insectCutButton = new JButton("Cut");
        add(insectCutButton, gbc);

        // Mushroom Fire gomb
        gbc.gridx = 1;
        mushroomFireButton = new JButton("Fire");
        add(mushroomFireButton, gbc);

        y++; // Új sor #########################################

        // Mushroom Eat Insect gomb
        gbc.gridy = y;
        gbc.gridx = 1;
        mushroomEatInsectButton = new JButton("Eat Insect");
        add(mushroomEatInsectButton, gbc);

        y++; // Új sor #########################################

        // Skip gomb, ha a játékos nem tud/akar lépni
        gbc.gridy = y;
        gbc.gridx = 0;
        gbc.gridwidth = 0;
        skipButton = new JButton("Skip step");
        add(skipButton, gbc);
        skipButton.addActionListener(e -> this.skipTurn());

        y++; // Új sor #########################################

        // Log mező - itt írja ki ha valami rossz
        gbc.gridy = y;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        logTextField = new JTextField("Log");
        logTextField.setEditable(false);
        add(logTextField, gbc);

        y++; // Új sor #########################################

        // SAVE GOMB
        gbc.gridy = y;
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> save());
        add(saveButton, gbc);

        updateButtonStates(GameEngine.getInstance().getMushrooms().get(0).getName());

    }

    /**
     * Visszaadja a ComboBox-ban kiválasztott elemet szövegként.
     *
     * @return A kiválasztott elem neve vagy null, ha nincs kiválasztva semmi
     */
    public String getSelectedComboBoxItem() {
        Object selected = dynamicComboBox.getSelectedItem();
        return selected != null ? selected.toString() : null;
    }

    /**
     * Mentést végez a jelenlegi játékállásról egy JSON fájlba.
     * Megjelenít egy értesítést a sikeres mentésről.
     */
    private void save() {
        GameEngine engine = GameEngine.getInstance();
        GameStateDTO state = new GameStateDTO();
        state.tectons = gameBoardView.getTectonViews();
        state.insects = engine.getInsects();
        state.mushrooms = engine.getMushrooms();
        state.round = engine.getRound();

        String fileName = "saved_state.json";
        GameStateSaver.saveToFile(state, fileName);

        JOptionPane.showMessageDialog(this, "Játékállás elmentve: " + fileName, "Sikeres mentés",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void draw() {
    }

    /**
     * Az Observer interfész frissítési metódusa, amely frissíti a körszámot,
     * ellenőrzi a játék végét, és újrarajzolja a panelt.
     */
    @Override
    public void update() {
        // Minden update hatására frissíti a kör kiírását
        int roundNumber = GameEngine.getInstance().getRound();
        roundTextField.setText(Integer.toString(roundNumber));

        checkGameOver();

        // Újra rajzol
        repaint();
    }

    /**
     * Ellenőrzi, hogy a játék véget ért-e (körszám 0), és ha igen,
     * megjeleníti a győzteseket és letiltja a vezérlő gombokat.
     */
    private void checkGameOver() {
        if (gameEngine.getRound() == 0) {
            String adatok = "Game Over! \n\n Winners are:\n\n";
            int iscore = gameEngine.getInsectPlayeScore("i0");
            String iw = "i0";
            for(int i = 0; i < gameEngine.getPlayerNumber(); i++){
                if(gameEngine.getInsectPlayeScore("i" + i) > iscore){
                    iscore = gameEngine.getInsectPlayeScore("i" + i);
                    iw = "i" + i;
                }
            }
            adatok += " " + iw +": " + iscore;
            adatok += "\n";

            int mscore = gameEngine.getMushrooms().get(0).getScore();
            String mw = "m0";
            for(int i = 0; i < gameEngine.getPlayerNumber(); i++){
                if( gameEngine.getMushrooms().get(i).getScore() > mscore){
                    mscore = gameEngine.getMushrooms().get(i).getScore();
                    mw = "m" + i;
                }
            }
            adatok += " " + mw +": " + mscore;


            infoField.setText(adatok);
            // Disable all game control buttons when game is over
            insectMoveButton.setEnabled(false);
            insectEatButton.setEnabled(false);
            insectCutButton.setEnabled(false);
            mushroomGrowButton.setEnabled(false);
            mushroomExtendButton.setEnabled(false);
            mushroomFireButton.setEnabled(false);
            mushroomEatInsectButton.setEnabled(false);
            skipButton.setEnabled(false);
        }
    }
        

    // Getterek, Setterek

    /** @return Az "Insect Move" gomb */
    public JButton getInsectMoveButton() {
        return insectMoveButton;
    }

    /** @return Az "Insect Eat" gomb */
    public JButton getInsectEatButton() {
        return insectEatButton;
    }

    /** @return Az "Insect Cut" gomb */
    public JButton getInsectCutButton() {
        return insectCutButton;
    }

    /** @return A "Mushroom Grow" gomb */
    public JButton getMushroomGrowButton() {
        return mushroomGrowButton;
    }

    /** @return A "Mushroom Extend" gomb */
    public JButton getMushroomExtendButton() {
        return mushroomExtendButton;
    }

    /** @return A "Mushroom Fire" gomb */
    public JButton getMushroomFireButton() {
        return mushroomFireButton;
    }

    /** @return A "Mushroom Eat Insect" gomb */
    public JButton getMushroomEatInsectButton() {
        return mushroomEatInsectButton;
    }

    /**
     * Beállítja a log szövegét a megjelenítéshez, majd frissíti a nézetet.
     * 
     * @param log A megjelenítendő log üzenet
     */
    public void setLog(String log) {
        logTextField.setText(log);
        update();
    }

    /**
     * Visszaadja a játékost megjelenítő szövegmezőt.
     *
     * @return A játékos nevét megjelenítő JTextField
     */
    public JTextField getPlayerTextField() {
        return plaxerTextField;
    }

    /**
     * Beállítja a játékos nevét a szövegmezőben és frissíti a nézetet.
     *
     * @param player A játékos neve
     */
    public void setPlayerTextField(String player) {
        plaxerTextField.setText(player);
        update();
    }

    /**
     * A gombok engedélyezési állapotát állítja be az aktuális játékos típusa alapján.
     * Ha a játékos neve "i"-vel kezdődik, az rovar gombokat engedélyezi,
     * ha "m"-mel, akkor gomba gombokat.
     *
     * @param playerName Az aktuális játékos neve
     */
    public void updateButtonStates(String playerName) {
        boolean isInsect = playerName.toLowerCase().startsWith("i");
        boolean isMushroom = playerName.toLowerCase().startsWith("m");

        // Insect gombok
        insectMoveButton.setEnabled(isInsect);
        insectEatButton.setEnabled(isInsect);
        insectCutButton.setEnabled(isInsect);

        // Mushroom gombok
        mushroomGrowButton.setEnabled(isMushroom);
        mushroomExtendButton.setEnabled(isMushroom);
        mushroomFireButton.setEnabled(isMushroom);
        mushroomEatInsectButton.setEnabled(isMushroom);
    }

    /**
     * Továbblépteti a játékot a következő körre és frissíti a ComboBox tartalmát.
     */
    public void skipTurn() {
        gameEngine.next();
        showComboBoxOptions();
    }

    /**
     * Frissíti a ComboBox tartalmát az aktuális játékoshoz tartozó entitásokkal (rovarok vagy gombák).
     */
    private void showComboBoxOptions() {
        String currentPlayer = plaxerTextField.getText().trim(); // vagy amit használsz
        GameEngine engine = GameEngine.getInstance();
        if (currentPlayer.isEmpty())
            return;

        dynamicComboBox.removeAllItems(); // régi elemek törlése

        if (currentPlayer.startsWith("i")) {
            List<Insect> insects = engine.getPlayerInsects(currentPlayer);
            for (Insect insect : insects) {
                dynamicComboBox.addItem(insect.getName());
            }
        } else if (currentPlayer.startsWith("m")) {
            for (Mushroom mushroom : engine.getMushrooms()) {
                if (mushroom.getName().equals(currentPlayer)) {
                    for (MushBody body : mushroom.getMushBodies()) {
                        dynamicComboBox.addItem(body.getName());
                    }
                    break;
                }
            }
        }

        dynamicComboBox.setVisible(true);
    }

    /**
     * Elrejti a ComboBox-ot.
     */
    public void hideComboBox() {
        dynamicComboBox.setVisible(false);
    }

    /**
     * Visszaadja a dinamikusan frissülő ComboBox-ot.
     *
     * @return A ComboBox komponens
     */
    public JComboBox<String> getDynamicComboBox() {
        return dynamicComboBox;
    }
}
