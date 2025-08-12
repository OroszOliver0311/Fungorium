package fungorium;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import view.Observer;

/**
 * Absztrakt osztály, amely egy tektonikus mezőt reprezentál a játék világában.
 * 
 * <p>
 * Az osztály tárolja a tektonhoz tartozó spórákat, gombatestet, fonalakat,
 * valamint a szomszédos tektonokat és azokat az információkat, amelyek alapján
 * a tektonok repedéseit nyomon lehet követni.
 * </p>
 * 
 * <p>
 * Minden tekton példány egyedi azonosítóval rendelkezik.
 * </p>
 */
public abstract class Tecton implements Round, Observable {

    protected List<Observer> observers = new ArrayList<>();

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

    private boolean cracked;

    /**
     * Egyedi azonosítóként szolgál, mely minden Tecton példányt egyértelműen
     * azonosít.
     */
    private int id;

    /**
     * Lista, mely tárolja a tekton szomszédos tektonjait.
     */
    private List<Tecton> neighbors;

    /**
     * Lista, mely tárolja a tekton felszínén található spórákat.
     */
    private List<MushSpore> spores;

    /**
     * Attribútum, mely tárolja, hogy van-e MushBody a Tecton felszínén.
     */
    private MushBody mushbody;

    /**
     * Attribútum, mely tárolja a tektonon található fonalakat.
     */
    private List<MushThread> threads;

    /**
     * Attribútum, mely azt foglalja magába, hogy egy tekton hány kör után törik
     * ketté.
     */
    private int crackTime;

    // Statikus változó, minden Tecton példány között közös.
    // Arra szolgál, hogy egyedi azonosítót rendeljen minden új Tectonhoz.
    public static int idCount = 1;

    /**
     * Tecton osztály konstruktora.
     * Beállítja az egyedi azonosítót, repedési időt és inicializálja a listákat.
     *
     * @param crackTime Az adott Tecton repedési ideje.
     */
    public Tecton(int crackTime) {
        this.id = idCount;
        this.crackTime = crackTime;
        this.spores = new ArrayList<>();
        this.threads = new ArrayList<>();
        this.neighbors = new ArrayList<>();
        this.mushbody = null;
        idCount++;
    }

    /**
     * Hozzáad egy MushBody-t a Tecton felszínéhez.
     * Csak akkor engedélyezett, ha még nincs rajta MushBody.
     * A ZeroTecton osztály felülírja ezt a metódust.
     *
     * @param mushBodyName a hozzáadandó MushBody objektum
     * @throws IllegalStateException ha már van MushBody a Tectonon
     */
    public MushBody addMushBody(String mushBodyName) {
        if (this.mushbody != null) {
            //throw new IllegalStateException("There is already a MushBody on the selected Tecton!");
            return null;
        }
        MushBody mushBody = new MushBody(this, mushBodyName);
        this.mushbody = mushBody;
        return mushBody;
    }

    /**
     * Eltávolítja a MushBody-t a Tecton felszínéről.
     * A ZeroTecton osztály felülírja ezt a metódust, mivel nem lehet MushBody
     * a ZeroTecton-on.
     */
    public void removeMushBody() {
        if (this.mushbody == null) {
            return;
        }
        this.mushbody = null;
    }

    /**
     * Hozzáad egy MushSpore példányt a Tecton felszínéhez.
     * A metódus polimorfizmus segítségével lehetővé teszi különböző típusú spórák
     * hozzáadását.
     *
     * @param mushSpore A hozzáadni kívánt MushSpore példány.
     * @throws IllegalArgumentException Ha a paraméter null értékű.
     */
    public void addSpore(MushSpore mushSpore) {
        if (mushSpore == null) {
            throw new IllegalArgumentException("A spóra nem lehet null.");
        }
        spores.add(mushSpore);
    }

    /**
     * Eltávolít egy MushSpore példányt a Tecton felszínéről.
     * A metódus polimorfizmus segítségével képes eltávolítani különböző típusú
     * spórákat.
     *
     * @param mushSpore A törölni kívánt MushSpore példány.
     * @throws IllegalArgumentException Ha a paraméter null értékű.
     * @throws NoSuchElementException   Ha a spóra nem található a listában.
     */
    public void removeSpore(MushSpore mushSpore) {
        if (mushSpore == null) {
            throw new IllegalArgumentException("A spóra nem lehet null.");
        }
        if (!spores.contains(mushSpore)) {
            throw new NoSuchElementException("A törölni kívánt spóra nem található.");
        }
        spores.remove(mushSpore);
    }

    /**
     * Visszaad egy véletlenszerűen kiválasztott spórát a tekton felszínéről.
     * A metódus akkor lép életbe, amikor egy Insect rálép a Tektonra,
     * és a szimulációs lépése egy spóra elfogyasztása.
     * 
     * @return A véletlenszerűen kiválasztott MushSpore példány.
     * @throws NoSuchElementException Ha nincsenek spórák a tekton felszínén.
     */
    public MushSpore getSpore() {
        if (spores.isEmpty()) {
            throw new NoSuchElementException("Nincs spóra a tekton felszínén.");
        }

        Random random = new Random();
        int index = random.nextInt(spores.size());

        return spores.get(index);
    }

    /**
     * Az adott tektonnal egy másik tekton példányt szomszédsági viszonyba állít.
     * Ha a szomszédság már létezik, nem adja hozzá újra.
     * 
     * @param tecton A másik tekton példány, melyet szomszédnak akarunk beállítani.
     * @throws IllegalArgumentException Ha a megadott tekton a már meglévő tekton.
     */
    public void addNeighbor(Tecton tecton) {
        if (neighbors.contains(tecton)) {
            return;    
        }

        neighbors.add(tecton);
        tecton.addNeighbor(this);
    }

    /**
     * Privát metódus, amely egy adott tekton szomszédait törli a paraméterként
     * kapott listában szereplő szomszédok közül.
     * A metódus felelős azért, hogy a két tekton közötti szomszédsági viszonyokat
     * frissítse.
     * 
     * @param neighbors A törlendő szomszédok listája, amely az aktuális tekton
     *                  szomszédai közül kerül kiválasztásra.
     */
    private void removeNeighbors(List<Tecton> neighbors) {
        for (Tecton neighbor : neighbors) {
            this.removeNeighbor(neighbor);
            neighbor.addNeighbor(this);
        }
    }

    /**
     * Eltávolít egy adott szomszédot az aktuális tekton szomszédainak listájából.
     * 
     * @param neighbor A törlendő szomszéd, amelyet el kell távolítani a listából.
     */
    public void removeNeighbor(Tecton neighbor) {
        if (neighbor != null) {
            this.neighbors.remove(neighbor);
        }
    }

    /**
     * Ellenőrzi, hogy a paraméterként kapott tekton szomszédos-e az aktuális
     * tektonnal.
     * 
     * @param tecton A vizsgálandó tekton.
     * @return true, ha a paraméterként kapott tekton szomszédos az aktuális
     *         tektonnal,
     *         false, ha nem.
     */
    public boolean isNeighbor(Tecton tecton) {
        if (tecton == null || tecton == this) {
            return false;
        }
        return this.neighbors.contains(tecton);
    }

    /**
     * Hozzáad egy MushThread típusú példányt a Tecton szálak listájához.
     * 
     * @param thread A hozzáadandó MushThread példány.
     * @throws IllegalArgumentException Ha a paraméter null értékű.
     * @return true, ha lehet új fonala
     */
    public boolean addThread(MushThread thread) {
        if (thread == null) {
            throw new IllegalArgumentException("A thread nem lehet null.");
        }
        if (!soloSecurity(thread.getStart()) || !soloSecurity(thread.getEnd())) return false;
        this.threads.add(thread);
        return true;
    }


    
    /**
     * //Eldönti hogy a SOLOTECTON kaphat-e még fonalat
     * 
     * @param solo A SoloTecton
     * @return true, ha lehet új fonala
     */
    public boolean soloSecurity(Tecton solo){
        if (solo.getType().equals("SOLOTECTON")){
            if (!solo.getThreads().isEmpty()) return false;
            for (Tecton t: solo.getNeighbors()){
                for (MushThread tr : t.getThreads()){
                    if (tr.getEnd().equals(solo) || tr.getStart().equals(solo)) return false;
                }
            }
        }
        return true;
    }
    /**
     * Eltávolít egy MushThread típusú példányt a Tecton szálak listájából.
     * Ha a fonal túl sokáig nem kapcsolódik MushBody-hoz, akkor megszüntethetjük a
     * példányt.
     * 
     * @param thread A törlendő MushThread példány.
     * @throws IllegalArgumentException Ha a paraméter null értékű vagy a thread nem
     *                                  található a listában.
     */
    public void removeThread(MushThread thread) {
        if (thread == null) {
            throw new IllegalArgumentException("A thread nem lehet null.");
        }

        if (!this.threads.contains(thread)) {
            throw new IllegalArgumentException("A megadott thread nem található a listában.");
        }

        this.threads.remove(thread);
    }

    /**
     * Megvizsgálja, hogy a paraméterként átadott Tecton-nak és az aktuális
     * Tecton-nak van-e közös szomszédja.
     * 
     * @param tecton A másik Tecton objektum, amellyel közös szomszédokat keresünk.
     * @return true, ha közös szomszéd van, false, ha nincs.
     */
    public boolean hasSameNeighbor(Tecton tecton) {
        for (Tecton neighbor1 : this.neighbors) {
            for (Tecton neighbor2 : tecton.neighbors) {
                if (neighbor1.getId() == neighbor2.getId()) {
                    return true;
                }
            }
        }
        return false; // Ha nem találunk közös szomszédot, visszatérünk false-szal
    }

    /**
     * Véletlenszerűen kiválasztja a spórák felét, eltávolítja őket az eredeti
     * listából,
     * majd visszaadja egy új listában.
     *
     * @return Egy új lista a kiválasztott spórákkal.
     */
    private List<MushSpore> splitSpores() {
        return splitListRandomly(spores);
    }

    /**
     * Véletlenszerűen kiválasztja a szomszédok felét, eltávolítja őket az eredeti
     * listából,
     * majd visszaadja egy új listában.
     *
     * @return Egy új lista a kiválasztott szomszédokkal.
     */
    private List<Tecton> splitNeighbors() {
        return splitListRandomly(neighbors);
    }

    /**
     * Segédfüggvény, amely egy lista felét véletlenszerűen kiválasztja és
     * eltávolítja az eredeti listából.
     * A kiválasztott elemeket új listában adja vissza.
     * 
     * Ez a metódus a splitSpores és splitNeighbors közötti kódduplikáció
     * elkerülésére szolgál.
     * 
     * @param <T>  A lista elemeinek típusa.
     * @param list A bemeneti lista, amelyből a kiválasztás történik.
     * @return Egy új lista, amely tartalmazza a véletlenszerűen kiválasztott
     *         elemeket.
     */
    private <T> List<T> splitListRandomly(List<T> list) {
        List<T> split = new ArrayList<>();
        int halfSize = list.size() / 2;

        Collections.shuffle(list); // Véletlenszerű sorrend
        for (int i = 0; i < halfSize; i++) {
            split.add(list.remove(0));
        }

        return split;
    }

    /**
     * Meghatározza a legrövidebb fonál alapú útvonal hosszát az aktuális Tecton
     * és a megadott Tecton között BFS algoritmus segítségével.
     * Ha nincs fonál alapú útvonal a két Tecton között, akkor -1-et ad vissza.
     *
     * @param target A cél Tecton, amelyhez az utat keressük.
     * @return A legrövidebb út hossza fonál mentén, vagy -1, ha nincs kapcsolat.
     */
    public int checkIfConnectedByThread(Tecton target) {
        if (this == target) {
            return 0;
        }

        Queue<Tecton> toVisit = new LinkedList<>();
        Queue<Integer> distances = new LinkedList<>();
        Set<Tecton> visited = new HashSet<>();

        toVisit.add(this);
        distances.add(0);
        visited.add(this);

        while (!toVisit.isEmpty()) {
            Tecton current = toVisit.poll();
            int distance = distances.poll();
            List<MushThread> allThreads=new ArrayList(current.threads);
            for (Tecton t: current.neighbors){
                for (MushThread thread: t.threads){
                    if (thread.getEnd().equals(current) || thread.getStart().equals(current)) allThreads.add(thread); //szomszédok fonalai is (hiszen egy fonál csak egy tektonon)
                }
            }
            for (MushThread thread : allThreads) {
                Tecton neighbor;
                if (current.equals(thread.getStart()) ) neighbor = thread.getEnd();
                else neighbor = thread.getStart();
                if (neighbor == null || visited.contains(neighbor)) {
                    continue;
                }

                if (neighbor.equals(target)) {
                    return distance + 1;
                }

                toVisit.add(neighbor);
                distances.add(distance + 1);
            }
            visited.add(current);
        }

        return -1;
    }

    /*
     * A round() metódus minden kör végén meghívásra kerül.
     * Csökkenti a crackTime értékét, és ha elérte a nullát,
     * akkor meghívja a crack() metódust.
     * A fonalak is végrehajtják a saját round() metódusukat.
     */
    public void round() {
        if (crackTime > 0) {
            crackTime--;
        }

        if (crackTime == 0) {
            crackTime--;
            crack();
        }

        for (MushThread thread : threads) {
            thread.round();
        }
    }

    /**
     * Publikus metódus, amely a tekton kettétörését végzi el.
     * Létrehoz egy új Tectont, átadja a spórák felét és szomszédok felét az új
     * tektonnak,
     * elszakítja a meglévő fonalakat, és összeköti az eredetit az új tektonnal.
     * Ha az eredeti tektonon volt MushBody, akkor 50% eséllyel áthelyeződik az új
     * tektonra.
     */
    public void crack() {
        Tecton newTecton = createNewTecton();
        GameEngine.getInstance().addTecton(newTecton);

        List<MushSpore> splitSpores = splitSpores();
        for (MushSpore spore : splitSpores) {
            newTecton.addSpore(spore);
            this.removeSpore(spore);
        }

        for (MushThread thread : new ArrayList<>(threads)) {
            thread.takeThreadApart();
        }

        transferMushBodyIfNeeded(newTecton);
        this.addNeighbor(newTecton);
        newTecton.addNeighbor(this);

        GameEngine.getInstance().addNewTecton(this, newTecton);
    }

    /*
     * Absztrakt metódus, amelyet a származtatott osztályoknak
     * implementálniuk kell.
     * Ez a metódus felelős az új Tecton példány létrehozásáért.
     */
    protected abstract Tecton createNewTecton();

    /**
     * Ha az aktuális Tecton tartalmaz MushBody-t, akkor 50% eséllyel áthelyezi azt
     * az új Tecton-ra.
     * 
     * @param newTecton az újonnan létrehozott tekton, amely a lehetséges új gazda
     *                  lehet
     */
    private void transferMushBodyIfNeeded(Tecton newTecton) {
        if (this.mushbody == null)
            return;

        Random random = new Random();
        boolean transfer = random.nextBoolean();

        if (transfer) {
            newTecton.addMushBody(this.mushbody.getName());
            this.removeMushBody();
        }
    }

    // Getterek/setterek és metódusok később kerülnek kialakításra

    /**
     * Visszaadja a Tecton egyedi azonosítóját.
     * 
     * @return az egyedi azonosító
     */
    public int getId() {
        return id;
    }

    /**
     * Beállítja a Tecton egyedi azonosítóját.
     * 
     * @param id az egyedi azonosító
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Visszaadja a Tecton szomszédos tektonjainak listáját.
     * 
     * @return a szomszédos tektonok listája
     */
    public List<Tecton> getNeighbors() {
        return neighbors;
    }

    /**
     * Beállítja a Tecton szomszédos tektonjainak listáját.
     * 
     * @param neighbors a szomszédos tektonok listája
     */
    public void setNeighbors(List<Tecton> neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Visszaadja a Tecton felszínén található spórák listáját.
     * 
     * @return a spórák listája
     */
    public List<MushSpore> getSpores() {
        return spores;
    }

    /**
     * Beállítja a Tecton felszínén található spórák listáját.
     * 
     * @param spores a spórák listája
     */
    public void setSpores(List<MushSpore> spores) {
        this.spores = spores;
    }

    /**
     * Visszaadja a Tecton felszínén található MushBody-t.
     * 
     * @return a MushBody objektum, vagy null, ha nincs
     */
    public MushBody getMushbody() {
        return mushbody;
    }

    /**
     * Beállítja a Tecton felszínén található MushBody-t.
     * 
     * @param mushbody a MushBody objektum
     */
    public void setMushbody(MushBody mushbody) {
        this.mushbody = mushbody;
    }

    /**
     * Visszaadja a Tecton felszínén található fonalak listáját.
     * 
     * @return a MushThread objektumokat tartalmazó lista
     */
    public List<MushThread> getThreads() {
        return threads;
    }

    /**
     * Beállítja a Tecton felszínén található fonalak listáját.
     * 
     * @param threads a MushThread objektumokat tartalmazó lista
     */
    public void setThreads(List<MushThread> threads) {
        this.threads = threads;
    }

    /**
     * Visszaadja, hogy a Tecton hány kör után törik ketté.
     * 
     * @return a crackTime értéke
     */
    public int getCrackTime() {
        return crackTime;
    }

    /**
     * Beállítja, hogy a Tecton hány kör után törjön ketté.
     * 
     * @param crackTime a körök száma a kettétörésig
     */
    public void setCrackTime(int crackTime) {
        this.crackTime = crackTime;
    }

    /**
     * Kiírja a Tecton típusát.
     * A metódus a származtatott osztályokban kerül implementálásra.
     */
    public abstract String getType();

    public boolean isCracked() {
        return cracked;
    }

    public void setCracked(boolean cracked) {
        this.cracked = cracked;
    }

}
