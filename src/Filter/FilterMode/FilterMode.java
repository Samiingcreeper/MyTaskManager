package Filter.FilterMode;

import java.util.function.BiPredicate;

public class FilterMode<T>{

    protected BiPredicate<T, T> predicate;

    FilterMode(BiPredicate<T, T> predicate){
        this.predicate = predicate;
    }

    public BiPredicate<T, T> getPredicate(){
        return predicate;
    }
}
