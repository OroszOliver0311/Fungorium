package fungorium;

/**
 * A CrampSpore osztály a MushSpore leszármazottja, amely görcsöt okoz a
 * rovaroknak.
 */
public class CrampSpore extends MushSpore {

    /**
     * Létrehoz egy új CrampSpore objektumot a megadott értékkel.
     *
     * @param value A spóra értéke.
     */
    public CrampSpore(int value) {
        super(value);
    }

    @Override
    /**
     * Rovar vágási képességének megszüntetése.
     *
     * @param insect Az a rovar, amelyre a hatás érvényesül.
     */
    public void giveEffect(Insect insect) {
        System.out.println(insect.getName() + " eating a CRAMPSPORE on the current tekton");
        insect.setCutSkill(false);
    }

    @Override
    /**
     * Visszaadja a spóra típusát.
     *
     * @return A spóra típusa.
     */
    public String getType() {
        return "CrampSpore";
    }
}
