package k23b.ac.services.observer;

public interface Observer<T> {

    void update(Observable<T> observable, T data);
}
