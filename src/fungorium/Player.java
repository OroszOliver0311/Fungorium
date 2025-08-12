package fungorium;

/**
 * A felhasznalokat ez az osztaly reprezentalja, kezeli a pontszamot.
 */
public interface Player {
	/**
	 * A jatekos pontszamat noveli s-sel.
	 * @param score
	 */
    void addScore(int score);
    
    /**
     * A jatekos lehetosegei a koreben.
     */
    void step();
}