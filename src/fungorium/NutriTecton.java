package fungorium;

import java.util.List;
import java.util.Random;

/**
 * A NutriTecton osztály a Tecton egy speciális típusa, amely képes
 * felismerni, ha a felszínén olyan fonalak találhatók, amelyek már nem
 * kapcsolódnak MushBody-hoz (pl. el lettek vágva), és ezek gyógyítását
 * (felszívódási idejének megnövelését) végrehajtja.
 */
public class NutriTecton extends Tecton {

    private final Random random = new Random();

    /**
     * Konstruktor a NutriTecton példányosításához.
     * 
     * @param crackTime a tekton töréséhez szükséges idő
     */
    public NutriTecton(int crackTime) {
        super(crackTime);
    }

    /**
     * Egy kör végrehajtása a NutriTectonon.
     * Minden körben ellenőrzi, hogy a felszínén lévő fonalak közül
     * melyek nem csatlakoznak MushBody-hoz, és ezekre meghívja a
     * gyógyítási metódust.
     */
    @Override
    public void round() {
        healNotConnectedThreads();

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
     * Gyógyítja azokat a fonalakat, amelyek már nem kapcsolódnak
     * MushBody-hoz. Ez úgy történik, hogy azok eltűnési idejét
     * megnöveli egy véletlenszerű értékkel (pl. 1–3 között).
     */
    private void healNotConnectedThreads() {
        List<MushThread> threads = getThreads();
        for (MushThread thread : threads) {
            if (thread.isCutByInsect()) {
                int extraTime = 1 + random.nextInt(3);
                thread.setTimeToDie(extraTime);
            }
        }
    }

    /**
     * Egy új NutriTecton példány létrehozása a repedési idővel.
     *
     * @return új példány
     */
    @Override
    protected Tecton createNewTecton() {
        return new NutriTecton(-1);
    }

    /**
     * Visszaadja a NutriTecton típusát.
     */
    public String getType() {
        return "NUTRITECTON";
    }
}
