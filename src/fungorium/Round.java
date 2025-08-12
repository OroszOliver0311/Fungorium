package fungorium;

public interface Round {

	/**
	 * Minden kör végén hívódik meg, az objektum állapotát frissíti.
	 */
    void round();
}