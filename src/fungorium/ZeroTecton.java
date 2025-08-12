package fungorium;

/**
 * A ZeroTecton speciális Tecton típus, amely nem engedélyezi MushBody
 * hozzáadását.
 */
public class ZeroTecton extends Tecton {

    /**
     * Létrehoz egy új ZeroTecton példányt a megadott crackTime értékkel.
     * 
     * @param crackTime a törésig hátralévő idő
     */
    public ZeroTecton(int crackTime) {
        super(crackTime);
    }

    /**
     * Az AddMushBody a leszármazottban nem engedélyezi gombatest hozzáadását.
     * 
     * @param mushBodyName a hozzáadni kívánt MushBody objektum
     * @throws UnsupportedOperationException kivétel ha hozzáadást próbálnak
     */
    @Override
    public MushBody addMushBody(String mushBodyName) {
        return null;
    }

    /**
     * Létrehoz egy új ZeroTecton példányt a jelenlegi crackTime alapján.
     * 
     * @return új ZeroTecton példány
     */
    @Override
    protected Tecton createNewTecton() {
        return new ZeroTecton(-1);
    }

    /**
     * Visszaadja a ZeroTecton típusát.
     */
    public String getType() {
        return "ZEROTECTON";
    }
}