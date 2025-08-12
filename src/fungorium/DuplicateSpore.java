package fungorium;

/**
 * A SlowSpore osztály a MushSpore leszármazottja, amely lassító hatást fejt ki
 * a rovarokra.
 */
public class DuplicateSpore extends MushSpore {

    /**
     * Létrehoz egy új SlowSpore objektumot a megadott értékkel.
     *
     * @param value A spóra értéke.
     */
    public DuplicateSpore(int value) {
        super(value);
    }

    @Override
    /**
     * Lassító hatás alkalmazása egy rovarra.
     *
     * @param insect Az a rovar, amelyre a hatás érvényesül.
     */
    public void giveEffect(Insect insect) {
        System.out.println(insect.getName() + " eating a DUPLICATESPORE on the current tekton");
        insect.duplicate();
    }

    @Override
    /**
     * Visszaadja a spóra típusát.
     *
     * @return A spóra típusa.
     */
    public String getType() {
        return "DuplicateSpore";
    }
}