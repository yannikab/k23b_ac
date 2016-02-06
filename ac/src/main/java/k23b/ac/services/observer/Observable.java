package k23b.ac.services.observer;

/**
 * Interface for an Observable Item
 * 
 * @param <T> The type of the Observable Item
 */
public interface Observable<T> {
    
    /**
     * Registers an Observer into the Observer list in order to notify it when changes occur.
     * @param observer 
     */
    void registerObserver(Observer<T> observer);

    /**
     * Unregisters an Observer removing it from the Observer list.
     * @param observer
     */
    void unregisterObserver(Observer<T> observer);

    /**
     * Notifies registered Observers for changes in the Observable's State.
     */
    void notifyObservers();
    
    /**
     * 
     * @return The current State of the Observable Item.
     */
    T getCurrentState();
}
