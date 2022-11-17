package Filter;

import Data.Task;

import java.time.LocalDate;

@FunctionalInterface
public interface EntityDataGetter<T, U>{

    EntityDataGetter<Task, Boolean> TASK_IS_COMPLETED = task ->{
        return task.isCompleted();
    };
    EntityDataGetter<Task, LocalDate> TASK_DATE = task ->{
        return task.getDate();
    };

    U get(T entity);
}
