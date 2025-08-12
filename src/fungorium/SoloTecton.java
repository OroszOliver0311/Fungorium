package fungorium;

/**
 * A SoloTecton egy olyan Tecton típus, amelyen legfeljebb egy MushThread lehet.
 */
public class SoloTecton extends Tecton {

    /**
     * Létrehoz egy új SoloTecton példányt a megadott crackTime értékkel.
     *
     * @param crackTime a törésig hátralévő idő
     */
    public SoloTecton(int crackTime) {
        super(crackTime);
    }

    /**
     * Csak akkor ad hozzá egy MushThread-et, ha még nincs rajta egy sem.
     *
     * @param thread a hozzáadandó MushThread példány
     */
    @Override
    public boolean addThread(MushThread thread) {
        if (getThreads().isEmpty()) {
            super.addThread(thread);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Létrehoz egy új SoloTecton példányt a jelenlegi crackTime alapján.
     *
     * @return új SoloTecton példány
     */
    @Override
    protected Tecton createNewTecton() {
        return new SoloTecton(-1);
    }

    /**
     * Visszaadja a SoloTecton típusát.
     */
    public String getType() {
        return "SOLOTECTON";
    }

}
