package fungorium;

import java.util.ArrayList;
import java.util.List;
import view.Observer;

public abstract class MushSpore implements Observable {
    protected int value;
    private String type = "Spore";
    protected List<Observer> observers = new ArrayList<>();

    /**
     * konstruktor
     * 
     * @param value - spora értéke
     * 
     */
    public MushSpore(int value) {
        this.value = value;
    }

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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    // Absztrakt metódus, amelyet az alosztályoknak kell implementálniuk
    public abstract void giveEffect(Insect insect);

    public abstract String getType();
}
