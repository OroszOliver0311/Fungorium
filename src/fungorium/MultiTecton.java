package fungorium;

/**
 * A MultiTecton egy Tecton típus, amely jelenleg nem rendelkezik különleges
 * viselkedéssel,
 * olyan tekton ami az absztrakt Tecton osztály tulajdonságait viseli.
 */
public class MultiTecton extends Tecton {

    /**
     * Létrehoz egy új MultiTecton példányt a megadott crackTime értékkel.
     *
     * @param crackTime a törésig hátralévő idő
     */
    public MultiTecton(int crackTime) {
        super(crackTime);
    }

    /**
     * Létrehoz egy új MultiTecton példányt a jelenlegi crackTime alapján.
     *
     * @return új MultiTecton példány
     */
    @Override
    protected Tecton createNewTecton() {
        return new MultiTecton(-1);
    }

    /**
     * Visszaadja a MultiTecton típusát.
     */
    public String getType() {
        return "MULTITECTON";
    }
}
