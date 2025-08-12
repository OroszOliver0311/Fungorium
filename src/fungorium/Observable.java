package fungorium;

import view.Observer;

/**
 * Interfész az observer mintához, amely lehetővé teszi
 * megfigyelők (Observer-ek) regisztrálását, eltávolítását
 * és értesítését állapotváltozás esetén.
 */
public interface Observable {
    /**
     * Értesíti az összes regisztrált megfigyelőt az állapotváltozásról.
     */
    void notifyObservers();

    /**
     * Eltávolít egy megfigyelőt a megfigyelők listájából.
     *
     * @param observer a törlendő megfigyelő
     */
    void removeObserver(Observer observer);

    /**
     * Hozzáad egy megfigyelőt a megfigyelők listájához.
     *
     * @param observer a hozzáadandó megfigyelő
     */
    void addObserver(Observer observer);
}

