package fungorium;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import view.Observer;

public class Insect implements Player, Round, Observable {
    private GameEngine engine=GameEngine.getInstance();
    private String name;
    private int score;
    private int speed;
    private boolean cutSkill;
    private Tecton position;
    private List<Observer> observers = new ArrayList<>();

    /**
     * Konstruktor a skeleton tesztekhez
     * 
     * @param n
     * @param s
     * @param sped
     * @param cut
     * @param pos
     */
    public Insect(String n, int s, int sped, boolean cut, Tecton pos) {
        this.name = n;
        this.score = s;
        this.speed = sped;
        this.cutSkill = cut;
        this.position = pos;
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    /**
     * A rovart elmozgatja a paraméterként kapott Tecton-ra, ha valid a távolság
     * vagy a fonal összeköttetés
     * 
     * @param tecton - Ahova átmozog
     * @return - true ha sikerült, false ha nem
     */
    public void duplicate() {
        String ujs = this.name + engine.getInsects().size();
        engine.addInsect(new Insect(ujs, 0, 2, cutSkill, position));
    }

    public boolean moveInsect(Tecton tecton) {

        if (speed <= 0) {
            System.out.println("Moving " + this.name + " to tekton " + tecton.getId() + " failed because " + this.name
                    + " is stunned.");
            return false;
        }
        int connected = tecton.checkIfConnectedByThread(position);
        System.out.println(connected);
        if (connected == -1) {
            System.out.println("Moving " + this.name + " to tekton " + tecton.getId() + " failed because tekton "
                    + tecton.getId() + " is not connected to your current tekton.");
            return false;
        }

        if (speed >= connected) {
            position = tecton;
            engine.next();
            System.out.println("Moving " + this.getName() + " to tekton " + tecton.getId());
            return true;
        } else {
            if (speed == 1)
                System.out.println("Moving " + this.name + " to tekton " + tecton.getId() + " failed because "
                        + this.name + " is slowed.");
            if (speed == 2)
                System.out.println("Moving " + this.name + " to tekton " + tecton.getId() + " failed because "
                        + this.name + " is not fast and tekton " + tecton.getId() + " is too far.");

        }
        return false;
    }

    /**
     * Paraméterként kapott fonal elvágása
     * 
     * @param thread - A fonal
     * @return - cutSkill
     */
    public boolean cutThread(MushThread thread) {
        if (cutSkill) {
            thread.setCutByInsect(true);
            thread.setTimeToDie(2);
            thread.getEnd().removeThread(thread);
            thread.takeThreadApart();
            engine.next();
        } else {
            System.out.println("->CutSkillIsFalseException");
        }

        return cutSkill;
    }

    /**
     * Eszik egy spórát a position tectonról
     * 
     * @return a megevett spóra értéke vagy 0, ha nem evett
     */
    public int eatSpore() {
        if (position.getSpores().isEmpty()) {
            System.out.println(this.name
                    + " eating a spore on the current tekton failed because there’s no spore on the current tekton.");
            return 0;
        }
        Random random = new Random();
        int eatedspore = random.nextInt(position.getSpores().size()); // eated xD

        MushSpore eatenSpore = position.getSpores().get(eatedspore);
        position.getSpores().remove(eatedspore);

        if (eatenSpore == null) {
            System.out.println(this.name
                    + " eating a spore on the current tekton failed because there’s no spore on the current tekton.");
            return 0;
        } else {
            addScore(eatenSpore.getValue());
            eatenSpore.giveEffect(this);
            engine.next();
            return eatenSpore.getValue();
        }
    }

    /**
     * Pontok novelese
     */
    @Override
    public void addScore(int s) {
        this.score += s;
    }

    /**
     * A jatekos egy korenek kezelese
     */
    @Override
    public void step() {
        /*
         * Scanner scanner = new Scanner(System.in);
         * boolean rightcommand = false;
         * while (!rightcommand) {
         * 
         * System.out.print("Adj meg egy utasítást: ");
         * String input = scanner.nextLine();
         * String[] command = input.split(" ");
         * Insect cInsect = null;
         * Tecton cTecton = null;
         * MushThread cThread = null;
         * switch (command[0]) {
         * case "MOVEINSECT": // PARANCS MELYIK TECTONID
         * if (command.length != 3) {
         * System.out.print("Hibás utasítást adtál meg");
         * break;
         * }
         * for (Insect i : engine.getInsects()) {
         * if (command[1].equals(i.getName())) {
         * cInsect = i;
         * for (Tecton t : engine.getTectons()) {
         * if (Integer.parseInt(command[2]) == t.getId()) {
         * cTecton = t;
         * if (cInsect == null || cTecton == null) {
         * System.out.print("Hibás utasítást adtál meg");
         * break;
         * }
         * i.moveInsect(t);
         * }
         * }
         * }
         * }
         * 
         * break;
         * case "EATSPORE": // PARANCS MELYIK SPORE/RANDOM
         * if (command.length != 3) {
         * System.out.print("Hibás utasítást adtál meg");
         * break;
         * }
         * for (Insect i : engine.getInsects()) { // Beszedi az
         * insecteket
         * if (command[1].equals(i.getName())) {
         * cInsect = i;// Megkeresi a megfelelő nevűt
         * if (cInsect == null) {
         * System.out.print("Hibás utasítást adtál meg");
         * break;
         * }
         * i.eatSpore();
         * }
         * }
         * 
         * break;
         * 
         * case "CUTTHREAD": // PARANCS TECTON1(ezen van) TECTON2(erre vág)
         * if (command.length != 3) {
         * System.out.print("Hibás utasítást adtál meg");
         * break;
         * }
         * for (Insect i : engine.getInsects()) { // Beszedi az
         * insecteket
         * if (command[1].equals(i.getName())) {
         * cInsect = i;// Megkeresi a megfelelő nevűt
         * for (Tecton t2 : i.getPosition().getNeighbors()) {// beszedi a szomszédokat
         * if (t2.getId() == Integer.parseInt(command[2])) {
         * cTecton = t2;// Megkeresi a megfelelőt
         * for (MushThread mt : i.getPosition().getThreads()) {// a position lévő
         * Threadeket
         * // besztedi
         * if (mt.getEnd().getId() == t2.getId()) {
         * cThread = mt;// Megkeresi a megfelelőt
         * if (cInsect == null || cTecton == null || cThread == null) {
         * System.out.print("Hibás utasítást adtál meg");
         * break;
         * }
         * i.cutThread(mt);
         * 
         * }
         * 
         * }
         * }
         * }
         * }
         * }
         * 
         * break;
         * default:
         * System.out.print("Hibás utasítást adtál meg");
         * break;
         * 
         * }
         * }
         * 
         * scanner.close();
         */
    }

    public void round() {
        this.cutSkill = true;
    }

    public void die() {
        name = null;
        score = -1;
        speed = -1;
        cutSkill = false;
        position = null;
        engine.getInsects().clear();
    }

    /**
     * Ha a paraméterként kapott érték valid, beállítja a sebességet rá
     * 
     * @param s
     */
    public void setSpeed(int s) {
        if (s >= 0 && s <= 3)
            speed = s;
        else
            throw new IllegalArgumentException("Insect speed is not valid");
    }

    /**
     * Visszatér a sebesség értékével
     * 
     * @return sebesség
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Beállítja a fonal vágó képességet a paraméterként kapott i/h értékre
     * 
     * @param c
     */
    public void setCutSkill(boolean c) {
        cutSkill = c;
    }

    /**
     * Visszatér a vágó képességgel
     * 
     * @return vágó képesség
     */
    public boolean getCutSkill() {
        return cutSkill;
    }

    /**
     * A pozíciót a paraméterként kapott Tectonra állítja
     * 
     * @param p
     */
    public void setPosition(Tecton p) {
        position = p;
    }

    /**
     * Visszaadja a pozíciót
     * 
     * @return pozíció
     */
    public Tecton getPosition() {
        return position;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int s) {
        score = s;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

}
