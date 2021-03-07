package socialnetwork.ui.gui.utils.observer;

import socialnetwork.ui.gui.utils.enums.Tables;

import java.util.HashSet;

public abstract class Observable {

    HashSet<Observer> observers = new HashSet<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObserver(Tables table) {
        observers.forEach(observer -> observer.update(table));
    }
}
