package k23b.ac.util.observerPattern;

public interface Observable {

    void registerObserver(Observer observer);

    void unregisterObserver(Observer observer);

    void notifyObservers();

    boolean getCurrentState();

}
