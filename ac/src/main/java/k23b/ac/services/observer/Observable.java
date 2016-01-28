package k23b.ac.services.observer;

public interface Observable<T> {

    void registerObserver(Observer<T> observer);

    void unregisterObserver(Observer<T> observer);

    void notifyObservers();

    T getCurrentState();
}
