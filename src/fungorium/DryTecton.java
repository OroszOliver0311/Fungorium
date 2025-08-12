package fungorium;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A DryTecton osztály a Tecton egy speciális típusa, amelyen egy idő elteltével
 * automatikusan megszűnnek a fonalak. A fonalak felszívódását egy belső időzítő
 * vezérli, amely minden kör végén csökken. Amikor ez az időzítő lejár,
 * a fonalak eltávolításra kerülnek a tektonról.
 */
public class DryTecton extends Tecton {
    /**
     * A felszívódási idő, amely megszabja hány kör múlva szűnnek meg a fonalak.
     */
    private int time = 0;

    /**
     * Konstruktor a DryTecton példányosításához.
     * 
     * @param crackTime a tekton töréséhez szükséges idő
     */
    public DryTecton(int crackTime) {
        super(crackTime);
        Random random = new Random();
        time = random.nextInt(3) + 1;
    }

    /**
     * Egy kör végrehajtása a tektonon.
     * Csökkenti a felszívódási időt, vagy ha lejárt az idő,
     * megszünteti a fonalakat és végigmegy azokon meghívva a fonál elszakítási
     * metódust.
     */
    @Override
    public void round() {
        if (time > 0) {
            time--;
        } else {
            List<MushThread> toCut = getThreads();
            for (MushThread thread : toCut) {
                thread.takeThreadApart();
            }
            setThreads(new ArrayList<>());
        }

        int crackTime = this.getCrackTime();
        if (crackTime > 0) {
            this.setCrackTime(crackTime - 1);
        }

        if (crackTime == 0) {
            this.setCrackTime(crackTime - 1);
            crack();
        }

        for (MushThread thread : this.getThreads()) {
            thread.round();
        }
    }

    /**
     * Új példány létrehozása ugyanilyen típusú tektonból.
     * 
     * @return egy új DryTecton példány a jelenlegi repedési idővel
     */
    @Override
    protected Tecton createNewTecton() {
        return new DryTecton(-1);
    }

    // Getterek/setterek és metódusok később kerülnek kialakításra

    /**
     * A felszívódási idő lekérdezése.
     * 
     * @return a jelenlegi időérték
     */
    public int getTime() {
        return time;
    }

    /**
     * A felszívódási idő beállítása.
     * 
     * @param time az új időérték
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * Visszaadja a DryTecton típusát.
     */
    public String getType() {
        return "DRYTECTON";
    }
}
