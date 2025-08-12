package fungorium;

/**
 * A StunSpore osztály a MushSpore leszármazottja, amely elkábítja a rovarokat.
 */
public class StunSpore extends MushSpore {

    /**
     * Létrehoz egy új StunSpore objektumot a megadott értékkel.
     *
     * @param value A spóra értéke.
     */
    public StunSpore(int value) {
        super(value);
    }

    @Override
    /**
     * Elkábítja a megadott rovart, csökkentve annak sebességét nullára.
     *
     * @param insect Az a rovar, amelyre a hatás érvényesül.
     */
    public void giveEffect(Insect insect) {
        System.out.println(insect.getName() + " eating a STUNSPORE on the current tekton");
        insect.setSpeed(0);
    }

    @Override
    /**
     * Visszaadja a spóra típusát.
     *
     * @return A spóra típusa.
     */
    public String getType() {
        return "StunSpore";
    }
}
