package Filter.Logical;

import Filter.TaskFilter;

public abstract class LogicalTaskFilter extends TaskFilter {

    protected TaskFilter filter1;
    protected TaskFilter filter2;

    protected LogicalTaskFilter(TaskFilter filter1, TaskFilter filter2){
        this.filter1 = filter1;
        this.filter2 = filter2;
    }
}
