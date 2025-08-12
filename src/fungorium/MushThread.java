package fungorium;

import java.util.ArrayList;
import java.util.List;
import view.Observer;

/**
 * Megvalósítja a gombafonal osztályt ami feladata hogy tektonok között
 * növekedjen és rajtuk tudjanak mozogni a rovarok
 */
public class MushThread implements Round, Observable {
    /**
     * A fonal eleje
     */
    private Tecton start;
    /**
     * A fonal vége
     */
    private Tecton end;
    /**
     * A MushBody amihez a fonal tartozik
     */
    private MushBody mushBody;
    /**
     * Rovar által el lett-e vágva a fonal
     */
    private boolean cutByInsect;
    /**
     * A gomabtestel való kapcsolat megszünése után menyi ideje van mielőtt meghal
     */
    private int timeToDie;

    private List<Observer> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    /**
     * Konstruktor
     * 
     * @param s - A fonal eleje
     * @param e - A fonal vége
     * @param b - A MushBody amihez a fonal tartozik
     */
    public MushThread(Tecton s, Tecton e, MushBody b) {
        start = s;
        end = e;
        mushBody = b;
        cutByInsect = false;
        timeToDie = -1;
        // System.out.println("Adding thread between tekton " + start.getId() + " and
        // tekton " + end.getId() + " from "+ mushBody.getName());
    }

    /**
     * Megszünteti a fonalat, a start és end attribútumot NULL ra állítja és ezáltal
     * megszünteti a fonál létezését
     */
    public void takeThreadApart() {
        this.setStart(null);
        this.setEnd(null);
    }

    // Ez szerintem már nem kell
    public void connect(MushBody mushBody) {
    }

    /**
     * Round interface metódusának felülírása, körönként egyszerhívódik meg.
     * A MushThread állapotát frissíti.
     * Ha cutByInsect true, akkor a timeToDie értéket körönként csökkenti eggyel.
     * Ha eléri a 0-t, akkor a MushThread végleg elpusztul.
     */
    public void round() {
        if (cutByInsect) {
            timeToDie -= 1;
        }
        if (timeToDie == 0) {
            end.removeThread(this);
            takeThreadApart();
        }
    }

    /**
     * Visszatér a fonal elejével
     * 
     * @return - a fonal eleje
     */
    public Tecton getStart() {
        return start;
    }

    /**
     * Beállítja a fonal elejét
     * 
     * @param start - a fonal eleje
     */
    public void setStart(Tecton start) {
        this.start = start;
    }

    /**
     * Visszatér a fonal végével
     * 
     * @return - a fonal vége
     */
    public Tecton getEnd() {
        return end;
    }

    /**
     * Beállítja a fonal végét
     * 
     * @param end - a fonal vége
     */
    public void setEnd(Tecton end) {
        this.end = end;
    }

    /**
     * Viszatér a MushBody-val amihez a fonal tartozik
     * 
     * @return - A MushBody amihez a fonal tartozik
     */
    public MushBody getMushBody() {
        return mushBody;
    }

    /**
     * Beállítja a MushBody-t amihez a fonal tartozik
     * 
     * @return - A MushBody amihez a fonal tartozik
     */
    public void setMushBody(MushBody mushBody) {
        this.mushBody = mushBody;
    }

    /**
     * Visszatér azzal hogy, Rovar által el lett-e vágva a fonal
     * 
     * @return - Rovar által el lett-e vágva a fonal
     */
    public boolean isCutByInsect() {
        return cutByInsect;
    }

    /**
     * Beállítja azt hogy, Rovar által el lett-e vágva a fonal
     * 
     * @return - Rovar által el lett-e vágva a fonal
     */
    public void setCutByInsect(boolean cutByInsect) {
        this.cutByInsect = cutByInsect;
    }

    /**
     * Visszatér azzal hogy, a gomabtestel való kapcsolat megszünése után menyi
     * ideje van mielőtt meghal
     * 
     * @return a gomabtestel való kapcsolat megszünése után menyi ideje van mielőtt
     *         meghal
     */
    public int getTimeToDie() {
        return timeToDie;
    }

    /**
     * Beállítja azt hogy, a gomabtestel való kapcsolat megszünése után menyi ideje
     * van mielőtt meghal
     * 
     * @return a gomabtestel való kapcsolat megszünése után menyi ideje van mielőtt
     *         meghal
     */
    public void setTimeToDie(int timeToDie) {
        this.timeToDie = timeToDie;
    }

}