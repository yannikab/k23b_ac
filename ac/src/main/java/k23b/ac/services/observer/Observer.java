package k23b.ac.services.observer;


/**
 * 
 * Interface for an Observer of an Item
 *
 * @param <T> The type of the Item
 */
public interface Observer<T> {
    
    /**
     * This method is called by the Observable Item's notify.
     * 
     * @param observable The Observable Item which called the notify
     * @param data State of the Observable Item
     */
    void update(Observable<T> observable, T data);
}
