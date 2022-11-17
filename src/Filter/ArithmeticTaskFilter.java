package Filter;

import Data.Task;
import Filter.FilterMode.FilterMode;

public class ArithmeticTaskFilter<T> extends TaskFilter{

    protected T value;
    protected EntityDataGetter<Task, T> dataGetter;
    protected FilterMode<T> filterMode;

    public ArithmeticTaskFilter(T value, EntityDataGetter<Task, T> dataGetter, FilterMode<T> filterMode){
        this.value = value;
        this.dataGetter = dataGetter;
        this.filterMode = filterMode;
    }

    @Override
    public boolean isMatch(Task entity) {
        return filterMode.getPredicate().test(value, dataGetter.get(entity));
    }
}
