package controller;

import fungorium.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import view.*;

/**
 * A játék vezérlését ellátó osztály, amely kezeli a játékállapotokat,
 * eseményeket, valamint a felhasználói interakciókat.
 */
public class GameController {
    
    /**
     * A játék különböző állapotait reprezentáló enumeráció.
     */
    enum GameState {
        DEFAULT,
        PLAYERSTEP,
        INSECT_MOVE,
        INSECT_EAT,

    }

    private GameState currentState = GameState.DEFAULT;
    private GameEngine engine;
    private GameBoardView gameBoard;
    private InfoBoardView infoBoard;

    private TectonView selectedTectonView;
    private MushThreadView selectedMushThreadView;

    private String name;

    /**
     * Konstruktor, amely beállítja a szükséges komponenseket és eseménykezelőket.
     *
     * @param engine a játék motorja, amely a játékmenetet kezeli
     * @param infoBoard az információs panel nézet
     * @param gameBoard a játékmező nézet
     */
    public GameController(GameEngine engine, InfoBoardView infoBoard, GameBoardView gameBoard) {
        this.engine = engine;
        this.infoBoard = infoBoard;
        this.gameBoard = gameBoard;

        infoBoard.getMushroomGrowButton().addActionListener(e -> this.onGrowMushroom());
        infoBoard.getMushroomExtendButton().addActionListener(e -> this.onExtendMycelium());
        infoBoard.getMushroomFireButton().addActionListener(e -> this.onFireSpores());
        infoBoard.getMushroomEatInsectButton().addActionListener(e -> this.onEatInsect());

        infoBoard.getInsectMoveButton().addActionListener(e -> this.onInsectMove());
        infoBoard.getInsectEatButton().addActionListener(e -> this.onEatSpore());
        infoBoard.getInsectCutButton().addActionListener(e -> this.onCut());

        name = infoBoard.getPlayerTextField().getText();
        infoBoard.updateButtonStates(name);
        registerMouseClickHandler();
    }

    /**
     * Mushroom - Grow gomb művelete
     */
    public void onGrowMushroom() {
        if (selectedTectonView == null) {
            infoBoard.setLog("No selected Tecton");
            return;
        }

        String playerName = infoBoard.getPlayerTextField().getText();

        Mushroom currentMushroom = engine.getMushrooms().stream()
                .filter(m -> m.getName().equals(playerName))
                .findFirst()
                .orElse(null);

        if (currentMushroom == null) {
            infoBoard.setLog("No Mushroom found for player: " + playerName);
            return;
        }

        Tecton selectedTecton = selectedTectonView.getTecton();

        try {
            currentMushroom.growMushBody(selectedTecton);
            infoBoard.setLog("Attempted to grow mushroom on tecton: " + selectedTecton.getId());
        } catch (Exception e) {
            infoBoard.setLog("Error during growing mushroom: " + e.getMessage());
            e.printStackTrace();
        }

        gameBoard.update();
        // gameBoard.repaint();
        infoBoard.repaint();
    }

    /**
     * Mushroom - Extend gomb művelete
     */
    public void onExtendMycelium() {
        if (selectedTectonView == null) {
            infoBoard.setLog("No selected Tecton");
            return;
        }

        String playerName = infoBoard.getPlayerTextField().getText();

        Mushroom currentMushroom = engine.getMushrooms().stream()
                .filter(m -> m.getName().equals(playerName))
                .findFirst()
                .orElse(null);

        if (currentMushroom == null) {
            infoBoard.setLog("No Mushroom found for player: " + playerName);
            return;
        }

        Tecton targetTecton = selectedTectonView.getTecton();
        boolean threadGrown = false;

        for (MushBody body : currentMushroom.getMushBodies()) {
            // Check if thread can be grown to the selected tecton
            for (Tecton neighbor : targetTecton.getNeighbors()) {
                if (neighbor.checkIfConnectedByThread(body.getLocation()) != -1) {
                    // Found a mushbody that can grow a thread to selected tecton
                    currentMushroom.growThread(targetTecton, body);
                    infoBoard.setLog("Thread grown from " + body.getName() + " to tecton " + targetTecton.getId());
                    threadGrown = true;
                    break;
                }
            }
            if (threadGrown)
                break;
        }

        if (!threadGrown) {
            infoBoard.setLog("No MushBody found that can grow a thread to tecton " + targetTecton.getId());
        }

        gameBoard.update();
        // gameBoard.repaint();
        infoBoard.repaint();
    }

    /**
     * Mushroom - Fire gomb akciója
     */
    public void onFireSpores() {
        if (selectedTectonView == null) {
            infoBoard.setLog("No selected Tecton");
            return;
        }

        // Név lekérdezése az aktuális játékos mezőből
        String playerName = infoBoard.getPlayerTextField().getText();

        // Gomba keresése név alapján
        Mushroom currentMushroom = engine.getMushrooms().stream()
                .filter(m -> m.getName().equals(playerName))
                .findFirst()
                .orElse(null);

        if (currentMushroom == null) {
            infoBoard.setLog("Only Mushroom players can fire spores.");
            return;
        }

        // Bekérés: mennyi spórát szeretne kilőni
        String amountStr = JOptionPane.showInputDialog(null,
                "Enter amount of spores to fire:",
                "Fire Spores",
                JOptionPane.PLAIN_MESSAGE);

        if (amountStr == null) {
            infoBoard.setLog("Fire spores cancelled.");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            infoBoard.setLog("Invalid amount of spores.");
            return;
        }

        try {
            // Fire spores a kiválasztott tecton-ra
            Tecton targetTecton = selectedTectonView.getTecton();
            currentMushroom.fireSpore(currentMushroom.getMushBodies().get(0), targetTecton, amount);

            infoBoard.setLog("Fired " + amount + " spores on tecton: " + targetTecton.getId());
        } catch (Exception e) {
            infoBoard.setLog("Error while firing spores: " + e.getMessage());
        }

        // Frissítés
        // gameBoard.repaint();
        gameBoard.update();
        infoBoard.repaint();
    }

    /**
     * Mushroom - Eat Insect gomb akciója
     */

    public void onEatInsect() {
        if (selectedTectonView == null) {
            infoBoard.setLog("No selected Tecton");
            return;
        }

        // Játékos nevének lekérdezése
        String playerName = infoBoard.getPlayerTextField().getText();

        // Gomba keresése név alapján
        Mushroom currentMushroom = engine.getMushrooms().stream()
                .filter(m -> m.getName().equals(playerName))
                .findFirst()
                .orElse(null);

        if (currentMushroom == null) {
            infoBoard.setLog("No Mushroom found for player: " + playerName);
            return;
        }

        Tecton selectedTecton = selectedTectonView.getTecton();

        boolean actionPerformed = false;

        for (MushBody musbody : currentMushroom.getMushBodies()) {
            // Ellenőrizzük, hogy a fonalak valamelyike ehhez a MushBody-hoz tartozik-e
            boolean hasThread = selectedTecton.getThreads().stream()
                    .anyMatch(thread -> thread.getMushBody().equals(musbody));

            if (hasThread) {
                currentMushroom.eatInsectWithThread(musbody, selectedTecton);
                infoBoard.setLog("Attempted to eat insect at tecton: " + selectedTecton.getId());
                actionPerformed = true;
                break; // feltételezve, hogy elég egy fonál
            }
        }

        if (!actionPerformed) {
            infoBoard.setLog("No valid MushThread found to eat insect at tecton: " + selectedTecton.getId());
        }

        // gameBoard.repaint();
        gameBoard.update();
        infoBoard.repaint();
    }

    /**
     * Insect - Move gomb akciója
     */
    public void onInsectMove() {
        Insect selected=null;
        if (selectedTectonView == null) {
            infoBoard.setLog("No selected Tecton.");
            return;
        }
        String insectName=infoBoard.getSelectedComboBoxItem();
        for (Insect i: engine.getPlayerInsects(infoBoard.getPlayerTextField().getText())){
            if (i.getName().equals(insectName)) selected=i;
        }
        if (selected!=null){
            selected.moveInsect(selectedTectonView.getTecton());
        }

        /*for (Insect i : engine.getInsects()) {
            if (i.getPosition().equals(selectedTectonView.getTecton())) {
                currentState = GameState.INSECT_MOVE; // Új állapotba lépünk
                infoBoard.setLog("Where to move?");
                selectedTectonView.setSelected(false);
                selectedTectonView = null;
                return;
            }
        }*/
        gameBoard.update();
        infoBoard.repaint();
        selectedTectonView.setSelected(false);
        selectedTectonView = null;
    }

    /**
     * Insect - Eat gomb akciója
     */
    public void onEatSpore() {
        if (selectedTectonView == null) {
            infoBoard.setLog("No selected Tecton");
            return;
        }

        Tecton selectedTecton = selectedTectonView.getTecton();
        /*List<Insect> insectsOnTecton = engine.getInsects().stream()
                .filter(insect -> insect.getPosition().equals(selectedTecton))
                .collect(Collectors.toList());

        if (insectsOnTecton.isEmpty()) {
            infoBoard.setLog("No insect on the selected Tecton to eat spores.");
            return;
        }

        // Feltételezzük, hogy csak az aktuális játékos irányíthat rovart
        String playerName = infoBoard.getPlayerTextField().getText();*/
        Insect currentInsect = engine.getInsects().stream()
                .filter(i -> i.getName().equals(infoBoard.getSelectedComboBoxItem()))
                .findFirst()
                .orElse(null);

        if (!currentInsect.getPosition().equals(selectedTecton)) {
            infoBoard.setLog("No matching insect for player " + infoBoard.getSelectedComboBoxItem().substring(0,2) + " on the selected Tecton.");
            return;
        }

        int gainedScore = currentInsect.eatSpore();
        if (gainedScore > 0) {
            infoBoard.setLog(infoBoard.getSelectedComboBoxItem().substring(0,2) + " ate a spore and gained " + gainedScore + " points!");
        } else {
            infoBoard.setLog("No spores to eat on the selected Tecton.");
        }

        // gameBoard.repaint();
        gameBoard.update();
        infoBoard.repaint();
    }

    /**
     * Insect - Cut gomb akciója
     */
    public void onCut() {
        if (selectedTectonView == null) {
            infoBoard.setLog("No selected Tecton");
            return;
        }

        Tecton selectedTecton = selectedTectonView.getTecton();

        // --- ÚJDONSÁG: lekérjük a kiválasztott nevet a ComboBoxból ---
        String selectedInsectName = infoBoard.getSelectedComboBoxItem();

        if (selectedInsectName == null || selectedInsectName.isEmpty()) {
            infoBoard.setLog("No insect selected from dropdown.");
            return;
        }

        // --- A név alapján megkeressük az Insect objektumot ---
        Insect currentInsect = engine.getInsects().stream()
                .filter(i -> i.getName().equals(selectedInsectName))
                .findFirst()
                .orElse(null);

        if (currentInsect == null) {
            infoBoard.setLog("Selected insect not found.");
            return;
        }

        Tecton insectTecton = currentInsect.getPosition();

        boolean found = false;
        List<MushThread> allThreads=new ArrayList<>();
        allThreads.addAll(insectTecton.getThreads());
        allThreads.addAll(selectedTecton.getThreads());
        for (MushThread thread : allThreads) {
            if (thread.getEnd().equals(selectedTecton) || thread.getStart().equals(selectedTecton)) {
                currentInsect.cutThread(thread);
                found = true;
                break;
            }
        }

        if (!found) {
            infoBoard.setLog("There's no thread between your insect and the selected tecton.");
        }

        gameBoard.update();
        infoBoard.repaint();
    }

    /**
     * Tekton kiválasztása kattintással a GameBoardView-n
     * 
     * @param selected
     */
    public void onTectonClicked(TectonView selected) {
        selectedTectonView = selected;
        // TODO: állapot alapján váltás, infoBoard frissítése
    }

    private void registerMouseClickHandler() {
        gameBoard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                //System.out.println("Mouse clicked at " + e.getPoint());

                // Először megnézzük a MushThreadView-kat
                List<MushThreadView> threadViews = gameBoard.getMushThreadViews(); // ezt a metódust biztosítani kell!
                MushThreadView closestThread = null;
                double minThreadDist = Double.MAX_VALUE;

                for (MushThreadView threadView : threadViews) {
                    double dist = threadView.distanceToPoint(e.getPoint()); // ezt is implementálni kell
                    if (dist < minThreadDist) {
                        minThreadDist = dist;
                        closestThread = threadView;
                    }
                }

                // Ha a fonál közelebb van mint egy határ (pl. 10 pixel), akkor azt választjuk
                if (closestThread != null && minThreadDist < 10) {
                    threadViews.forEach(tv -> tv.setSelected(false)); // előző kijelölések törlése
                    closestThread.setSelected(true);
                    gameBoard.repaint();
                    infoBoard.setLog("Selected a MushThread between Tectons: "
                            + closestThread.getThread().getEnd().getId()
                            + " and " + closestThread.getThread().getStart().getId());
                    //return;
                }

                List<TectonView> tectonViews = gameBoard.getTectonViews();

                TectonView closest = null;
                double minDist = Double.MAX_VALUE;

                for (TectonView tv : tectonViews) {
                    tv.update();
                    int dx = tv.getX() - e.getX();
                    int dy = tv.getY() - e.getY();
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist < minDist) {
                        minDist = dist;
                        closest = tv;
                    }
                }

                if (closest != null && minDist < 100) { // MAX_DISTANCE megfelelő értékkel
                    tectonViews.forEach(tv -> tv.setSelected(false));
                    closest.setSelected(true);
                    selectedTectonView = closest;
                    
                    infoBoard.infoField.setText(writeInfo());
                    gameBoard.repaint();
                }
            }
        });
    }

    /**
     * Összeállítja és visszaadja az aktuális állapothoz tartozó információkat szövegként,
     * amely megjeleníthető az információs panelen.
     *
     * @return az aktuális állapotot leíró információs szöveg
     */
    public String writeInfo(){
        String adatok = "";

        int playerNumber = engine.getPlayerNumber();
        adatok += "   i0: green     m0: blue\n";
        if(playerNumber >= 2)
            adatok +=  "   i1: blue        m1: red\n";
        if(playerNumber == 3)
            adatok += "   i2: red         m2: green\n";

        adatok += "\n";

        // Tecton info kiírása
        adatok += "Tecton: ";
        adatok += selectedTectonView.getTecton().getType();
        adatok += ", id: " + selectedTectonView.getTecton().getId();
        adatok += "\n\n";

        // Spóra infók kiírása 
        int numberOfSpores = selectedTectonView.getTecton().getSpores().size();
        adatok += "Number of spores in tecton: " + numberOfSpores;
        adatok += "\n\n";

        // Insect info kiírása
        adatok += "Insects: \n";
        for (Insect insect : GameEngine.getInstance().getInsects()) {
            if (insect.getPosition().getId() == selectedTectonView.getTecton().getId()) {
                adatok += "   " + insect.getName();
                if (insect.getCutSkill() == false) {
                    adatok += ", nem tud vágni";
                } else {
                    adatok += ", tud vágni";
                }
                adatok += ", sebesség:" + insect.getSpeed();
                adatok += "\n";
            }
        }

        // Mushbody info kiírása
        MushBody bodyOnSelectedTecton = selectedTectonView.getTecton().getMushbody();
        if(bodyOnSelectedTecton != null){
            adatok += "\nMushBody: \n";
            adatok += "   " + bodyOnSelectedTecton.getName();
            if(bodyOnSelectedTecton.isMature())
                adatok += ", mature";
            else    
                adatok += ", not mature";
            adatok += ", spores: " + bodyOnSelectedTecton.getSpores().size();
            adatok += "\n\n";
        }
        return adatok;
    }
}
