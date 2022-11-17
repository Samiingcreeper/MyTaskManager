package Filter.Logical;

import Data.Task;
import Filter.TaskFilter;

public class Or extends LogicalTaskFilter {

    public Or(TaskFilter filter1, TaskFilter filter2){
        super(filter1, filter2);
    }

    @Override
    public boolean isMatch(Task task) {
        return filter1.isMatch(task) || filter2.isMatch(task);
    }
}
