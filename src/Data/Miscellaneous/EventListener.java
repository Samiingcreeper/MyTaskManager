package Data.Miscellaneous;

@FunctionalInterface
public interface EventListener<T> {

    void handleUpdate(T source);
}
