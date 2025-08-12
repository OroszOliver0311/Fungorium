package fungorium;

/**
 * A SpeedSpore osztály a MushSpore leszármazottja, amely gyorsító hatást
 * biztosít a rovarok számára.
 */
public class SpeedSpore extends MushSpore {

    /**
     * Létrehoz egy új SpeedSpore objektumot a megadott értékkel.
     *
     * @param value A spóra értéke.
     */
    public SpeedSpore(int value) {
        super(value);
    }

    @Override
    /**
     * Gyorsító hatás alkalmazása egy rovarra.
     *
     * @param insect Az a rovar, amelyre a hatás érvényesül.
     */
    public void giveEffect(Insect insect) {
        System.out.println(insect.getName() + " eating a SPEEDSPORE on the current tekton");
        insect.setSpeed(3);
    }

    @Override
    /**
     * Visszaadja a spóra típusát.
     *
     * @return A spóra típusa.
     */
    public String getType() {
        return "SpeedSpore";
    }
}
