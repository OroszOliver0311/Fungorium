package fungorium;

import controller.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import view.Observer;

/**
 * A játék motorját (GameEngine) megvalósító Singleton osztály.
 * Ez az osztály kezeli a játék állapotát, a térképet, a játékosokat (gombák és
 * rovarok),
 * valamint a játékmenetet.
 */
public final class GameEngine {
    private static volatile GameEngine instance;
    private static int roundNumber = 10;
    private static int tectonNumber = 20;
    private static int playerNumber = 1;
    private List<Tecton> tectons;
    private List<Mushroom> mushrooms;
    private List<Insect> insects;
    private final Random random;

    private final List<GameStatusListener> listeners = new ArrayList<>();
    private final List<NewTectonListener> newTectonListeners = new ArrayList<>();

    private String currentPlayer;

    public void addGameStatusListener(GameStatusListener listener) {
        listeners.add(listener);
    }

    private void notifyCurrentPlayerChanged(String name) {
        for (GameStatusListener listener : listeners) {
            listener.onCurrentPlayerChanged(name);
        }
    }

    public void addNewTectonListener(NewTectonListener tectonListener) {         //Ezeket kell a GameEngine-be átmozgatni
        newTectonListeners.add(tectonListener);
    }

    private void notifyNewTectonListener(Tecton original, Tecton newTecton) {
        for (NewTectonListener tectonListener : newTectonListeners) {
            tectonListener.onNewTectonAdded(original, newTecton);
        }
    }

    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    /**
     * Privát konstruktor a Singleton minta miatt.
     * Inicializálja a játék környezetét és elindítja a játékot.
     * 
     * @param round        A kezdő kör száma
     * @param tectonNumber A tecton objektumok száma
     */
    private GameEngine(int r, int t, int p) {
        random = new Random();
        roundNumber = r;
        tectonNumber = t;
        playerNumber = p;
        this.tectons = new ArrayList<>();
        this.mushrooms = new ArrayList<>();
        this.insects = new ArrayList<>();

    }

    /**
     * Singleton példány lekérése.
     * Ha még nincs példány létrehozva, létrehozza azt biztonságosan, dupla
     * ellenőrzéssel.
     * 
     * @return A GameEngine egyetlen példánya
     */
    @SuppressWarnings("DoubleCheckedLocking")
    public static GameEngine getInstance() {
        if (instance == null) {
            synchronized (GameEngine.class) {
                if (instance == null) {
                    instance = new GameEngine(roundNumber, tectonNumber, playerNumber);
                }
            }
        }
        return instance;
    }

    public Tecton getTectonById(int id) {
        for (Tecton tecton : tectons) {
            if (tecton.getId() == id) {
                return tecton;
            }
        }
        return null;
    }

    public void loadGameState(GameStateDTO gameState) {
        System.out.println("GameEngine: loadGameState");
    }

    /**
     * Új tecton objektum hozzáadása a térképhez.
     * 
     * @param tecton A hozzáadandó tecton objektum
     */
    public void addTecton(Tecton tecton) {
        this.tectons.add(tecton);
    }

    /**
     * Létrehozza a játék térképét tectonokkal. Szomszédok nélkül.
     * 20 és 49 között kell legyen a tekton szám
     */
    public void createMap() {
        for (int i = 0; i < tectonNumber; i++) {
            int rand = random.nextInt(5); // Random szám a random Tekton generálásához
            int crackRand = random.nextInt(55);   //Ritkán legyen crack, mert annyira nem szép
            switch (rand) {
                case 0 -> tectons.add(new SoloTecton(crackRand));
                case 1 -> tectons.add(new ZeroTecton(crackRand));
                case 2 -> tectons.add(new MultiTecton(crackRand));
                case 3 -> tectons.add(new DryTecton(crackRand));
                case 4 -> tectons.add(new NutriTecton(crackRand));
                default -> {
                }
            }
        }
    }

    /**
     * a playerNumber értékének megfelelő mennyiségű Mushroom objektumot és Insect
     * objektumot generál.
     * Hozzáadja őket a mushrooms és insects listákhoz
     */
    public void generatePlayers() {
        for (int i = 0; i < playerNumber; i++) {
            Mushroom mushroom = new Mushroom("m" + i, 0);

            Tecton mushBodyLocation;
            boolean validTecton = false;
            while (!validTecton) {
                int rand = random.nextInt(tectonNumber);
                mushBodyLocation = tectons.get(rand);
                MushBody mBody = mushBodyLocation.addMushBody(mushroom.getName() + "_1");
                if (mBody != null) {
                    mushroom.addMushBody(mBody);
                    validTecton = true;
                }
            }

            mushrooms.add(mushroom);

            for (int k = 0; k < 3; k++) {
                int rand = random.nextInt(tectonNumber);
                Tecton insectPosition = tectons.get(rand);
                insects.add(new Insect("i" + i + "_" + k, 0, 2, true, insectPosition));
            }
        }
    }

    /**
     * A játék fő ciklusát végzi, ellenőrzi a játékelemeket, majd kezeli a köröket.
     * A gombák és rovarok felváltva lépnek.
     */
    public void startGame() {
        while (roundNumber > 0) {
            for (Mushroom mushroom : mushrooms) {
                notifyCurrentPlayerChanged(mushroom.getName());
                mushroom.step();
            }

            for (int i = 0; i < playerNumber; i++) {
                Insect currentInsect = insects.get(i);
                notifyCurrentPlayerChanged(currentInsect.getName());
                selectInsect(i);
            }

            roundNumber--;

            for (Mushroom mushroom : mushrooms)
                mushroom.round();
            for (Insect insect : insects)
                insect.round();
            for (Tecton tecton : tectons)
                tecton.round();
        }

        endGame();
    }

    /**
     * A konzolról bekér egy insect nevet, melynek meghívja a step() függvényét
     * 
     * @param player
     */
    private void selectInsect(int player) {
        Scanner scanner = new Scanner(System.in);
        boolean rightcommand = false;
        while (!rightcommand) {
            System.out.println("A soron lévő rovarász: i" + player);
            System.out.println("Add meg melyik rovarral lépsz:");
            for (int j = 0; j < insects.size(); j++) { // Kiírja a játékos összes rovarának nevét
                if (insects.get(j).getName().charAt(1) == (char) player)
                    System.out.println(insects.get(j).getName());
            }

            String input = scanner.next(); // Bekéri annak a rovarnak a nevét, amelyikkel lépni akar

            for (int j = 0; j < insects.size(); j++) { // Kiírja a játékos összes rovarának nevét
                String iName = insects.get(j).getName();
                if (iName.charAt(1) == player && iName.equals(input)) {
                    insects.get(j).step();
                    rightcommand = true;
                }
            }
            System.out.println("Rossz rovar nevet adtál meg!\n");
        }
    }

    /**
     * A következő játékos lépésének kezelése.
     */
    public void nextPlayer() {
        // Ezt nem tudom hogy rakjam bele a fő ciklusba
    }

    /**
     * A játék befejezése, eredmények összesítése és a győztes kiírása.
     */
    public void endGame() {
        Map<Integer, Integer> insectScores = new HashMap<>();
        Map<Integer, Integer> mushroomScores = new HashMap<>();

        for (Insect insect : insects) {
            char insectNumberChar = insect.getName().charAt(1);
            int insectNumber = insectNumberChar;
            insectScores.put(insectNumber, insectScores.getOrDefault(insectNumber, 0) + insect.getScore());
        }

        for (Mushroom mushroom : mushrooms) {
            char mushroomNumberChar = mushroom.getName().charAt(1);
            int mushroomNumber = mushroomNumberChar;
            mushroomScores.put(mushroomNumber, mushroomScores.getOrDefault(mushroomNumber, 0) + mushroom.getScore());
        }

        int insectWinner = -1;
        int insectMaxScore = -1;
        for (Map.Entry<Integer, Integer> entry : insectScores.entrySet()) {
            if (entry.getValue() > insectMaxScore) {
                insectMaxScore = entry.getValue();
                insectWinner = entry.getKey();
            }
        }

        int mushroomWinner = -1;
        int mushroomMaxScore = -1;
        for (Map.Entry<Integer, Integer> entry : mushroomScores.entrySet()) {
            if (entry.getValue() > insectMaxScore) {
                mushroomMaxScore = entry.getValue();
                mushroomWinner = entry.getKey();
            }
        }

        System.out.println("A legjobb rovarász: i" + insectWinner + ", összpontszáma: " + insectMaxScore);
        System.out.println("A legjobb gombász: m" + mushroomWinner + ", összpontszáma: " + mushroomMaxScore);
    }

    // ---------------- GETTEREK ÉS SETTEREK ----------------

    /**
     * @return Az aktuális kör száma
     */
    public int getRound() {
        return roundNumber;
    }

    /**
     * Beállítja az aktuális kör számát.
     * 
     * @param round Az új kör száma
     */
    public static void setRound(int round) {
        GameEngine.roundNumber = round;
    }

    /**
     * @return A tecton objektumok száma
     */
    public int getTectonNumber() {
        return tectonNumber;
    }

    /**
     * Beállítja a tectonok számát.
     * 
     * @param tectonNumber Az új tecton szám
     */
    public static void setTectonNumber(int tectonNumber) {
        GameEngine.tectonNumber = tectonNumber;
    }

    /**
     * @return A játékban szereplő tecton objektumok listája
     */
    public List<Tecton> getTectons() {
        return tectons;
    }

    /**
     * Beállítja a tectonok listáját.
     * 
     * @param tectons A tectonok listája
     */
    public void setTectons(List<Tecton> tectons) {
        this.tectons = tectons;
    }

    /**
     * @return A játékban szereplő gombák listája
     */
    public List<Mushroom> getMushrooms() {
        return mushrooms;
    }

    /**
     * Beállítja a gombák listáját.
     * 
     * @param mushrooms A gombák listája
     */
    public void setMushrooms(List<Mushroom> mushrooms) {
        this.mushrooms = mushrooms;
    }

    /**
     * @return A játékban szereplő rovarok listája
     */
    public List<Insect> getInsects() {
        return insects;
    }

    /**
     * Beállítja a rovarok listáját.
     * 
     * @param insects A rovarok listája
     */
    public void setInsects(List<Insect> insects) {
        this.insects = insects;
    }

    /**
     * Egy új rovart ad az insects listához
     * 
     * @param insect
     */
    public void addInsect(Insect insect) {
        Boolean validName = false;
        Boolean nameSetted = false;
        while (!validName) {
            for (Insect i : insects) {
                if (i.getName().equals(insect.getName())) {
                    insect.setName(insect.getName() + "_1");
                    nameSetted = true;
                }
            }
            if (!nameSetted) // Ha ebben a ciklusban nem kellett változtatni a néven, akkor validdá tesszük a
                             // nevet
                validName = true;
            else
                nameSetted = false;
        }
        insects.add(insect);
    }

    /**
     * Visszaadja a játék aktuális állapotát DTO formátumban.
     * 
     * @return A játék aktuális állapota DTO formátumban
     *
     */
    public GameStateDTO getCurrentState() {
        return new GameStateDTO(/* adatokkal feltöltve */);
    }

    /**
     * @return A játékosok száma
     */
    public static int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * Beállítja a rovarok számát.
     * 
     * @param p Az új játékosszám
     */
    public static void setPlayerNumber(int p) {
        GameEngine.playerNumber = p;
    }

    public void setCurrentPlayer(String playerName) {
        this.currentPlayer = playerName;
        notifyCurrentPlayerChanged(playerName);
    }

    public void addNewTecton(Tecton original, Tecton newTecton){
        notifyNewTectonListener(original, newTecton);
        tectons.add(newTecton);
    }

    private int currentIndex = 0;
    private boolean isMushroomPhase = true;

    public void next() {

        if (isMushroomPhase) {
            currentIndex++;
            if (currentIndex < getMushrooms().size()) {
                String nextPlayer = getMushrooms().get(currentIndex).getName();
                setCurrentPlayer(nextPlayer);
                
            } else {
                isMushroomPhase = false;
                currentIndex = 0;
            }
        }

        if (!isMushroomPhase) {
            if (currentIndex < playerNumber) {
                setCurrentPlayer("i"+currentIndex);
                currentIndex++;
                
            } else {
                isMushroomPhase = true;
                currentIndex = -1;
                roundNumber--;
                for (Mushroom mushroom : mushrooms) mushroom.round();
                for (Insect insect : insects) insect.round();
                for (Tecton tecton : new ArrayList<>(tectons)) tecton.round();
                next();
            }
        }
    }

    /**
     * 
     * @param name - a rovarász 2 karakter hosszú neve pl: "i1"
     * @return Lista a rovarász rovarjairól
     */
    public List<Insect> getPlayerInsects(String name){
        List<Insect> playerInsects = new ArrayList<>();
        for(Insect i : insects){
            String insectName = null;
            if(i.getName().length() > 2)
                insectName = i.getName().substring(0,2);
            if(insectName.equals(name)){
                playerInsects.add(i);
            }
        }

        return playerInsects;
    }

    public int getInsectPlayeScore(String name){
        int score = 0;
        for(Insect i : getPlayerInsects(name)){
            score += i.getScore();
        }
        return score;
    }
}
