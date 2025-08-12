package fungorium;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import view.Observer;

public class MushBody implements Round, Observable {
    private String name;
    private boolean mature;
    private Tecton location;
    private final List<MushSpore> spores;
    private final Random random;
    private int fireCount;

    private List<Observer> observers = new ArrayList<>();

    /**
     * Konstruktor egy gombatest létrehozásához.
     * 
     * @param loc  - A gombatest helyét meghatározó Tekton
     * @param name - A gombatest neve, könyebb megkülönböztetés céljából
     */
    public MushBody(Tecton loc, String n) {
        mature = false;
        location = loc;
        fireCount = 0;
        spores = new ArrayList<>();
        random = new Random();
        name = n;
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
     * A spórák lista elejéről visszaad annyi darabot egy listában,
     * ahányra paraméterként parancsot kapott
     * 
     * @param amount - mennyiség
     */
    public List<MushSpore> removeSpores(int amount) {
        if (amount > spores.size())
            return null; // Többet nem lehet kivenni, mint ami van benne

        List<MushSpore> sporesToRemove = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            sporesToRemove.add(spores.get(i));
        }
        for (int i = 0; i < amount; i++) {
            spores.remove(0);
        }
        fireCount++;
        return sporesToRemove;
    }

    /**
     * Éretté teszi a gombatestet
     */
    public void growMature() {
        setMature(true);
    }

    /**
     * Egy random spórát ad a spróák listához
     */
    public void addSporesPerRound() {
        for (int i = 0; i < 5; i++) {
            int rand = random.nextInt(5); // 0 és 5 közötti random szám
            switch (rand) {
                case 0 -> spores.add(new SlowSpore(2));
                case 1 -> spores.add(new SpeedSpore(1));
                case 2 -> spores.add(new StunSpore(4));
                case 3 -> spores.add(new CrampSpore(3));
                case 4 -> spores.add(new DuplicateSpore(0));
                default -> throw new IllegalStateException("Wrong random number");
            }
        }
    }

    /**
     * Készít egy fonalat a paraméterül kapott tektonok között,
     * ha azok szomszédos tektonok
     * 
     * @param startTecton - ezen a Tektonon lesz tárolva a fonal
     * @param endTecton
     */
    public void extendThread(Tecton startTecton, Tecton endTecton) { // Lehet ez a függvény már nem is kell
        if (startTecton.isNeighbor(endTecton)) // mivel a Thread-ek átkerültek a Tektonokra
            startTecton.addThread(new MushThread(startTecton, endTecton, this));
        else
            throw new InvalidParameterException("Tectons are not neighbors");
    }

    /**
     * Meghal a test, a pozícióját null-ra állítja
     */
    public void die() {
        location = null;
    }

    @Override
    public void round() {
        addSporesPerRound();
        if (spores.size() >= 8)
            growMature();

        if (fireCount >= 3)
            die();
    }

    /**
     * Visszaadja gombatest nevét
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Hányszor lőtt már spórát
     * 
     * @return firecount
     */
    public int getFireCount() {
        return fireCount;
    }

    /**
     * Visszatér a gombatest érettségével
     * 
     * @return érettség
     */
    public boolean isMature() {
        return mature;
    }

    /**
     * Beállítja a paraméterként kapott i/h érettségekhez
     * 
     * @param mature
     */
    public void setMature(boolean mature) {
        this.mature = mature;
    }

    /**
     * Visszaadja a gombatest pozícióját
     * 
     * @return pozíció
     */
    public Tecton getLocation() {
        return location;
    }

    /**
     * Beállítja a paraméterként kapott pozíciót
     * 
     * @param location
     */
    public void setLocation(Tecton location) {
        this.location = location;
    }

    /**
     * Visszaadja a spórák listáját
     * 
     * @return A spórák listja
     */
    public List<MushSpore> getSpores() {
        return spores;
    }

    /**
     * Hozzáad egy spórát a listához
     * 
     * @param spore - A spóra amit hozzá akarunk adni a listához
     */
    public void addSpore(MushSpore spore) {
        if (spore == null)
            throw new NullPointerException("MushSpore is null, cannot add to the list of spores");
        else {
            spores.add(spore);
        }

    }
}
