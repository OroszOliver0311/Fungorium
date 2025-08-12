package view;

/**
 * Megfigyelő interfész, amelyet olyan osztályok valósítanak meg,
 * amelyek értesítést szeretnének kapni valamilyen megfigyelt objektum állapotváltozásairól.
 */
public interface Observer {

    /**
     * Ezt a metódust hívja meg a megfigyelt objektum, amikor valamilyen változás történik.
     * A megfigyelő implementációja határozza meg, hogyan reagál a változásra.
     */
    void update();
}