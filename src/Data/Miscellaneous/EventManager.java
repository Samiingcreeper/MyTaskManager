package Data.Miscellaneous;

import java.io.Serializable;
import java.util.ArrayList;

public class EventManager<T> implements Serializable {

    private ArrayList<EventListener<T>> subscribers;

    public EventManager(){
        subscribers = new ArrayList<>();
    }

    public void registerListener(EventListener<T> subscriber){
        subscribers.add(subscriber);
    }

    public void unregisterListener(EventListener<T> subscriber){
        if(subscribers.contains(subscriber))
            subscribers.remove(subscriber);
    }

    public void notifyListener(T source){
        for(EventListener<T> subscriber : subscribers){
            subscriber.handleUpdate(source);
        }
    }
}
