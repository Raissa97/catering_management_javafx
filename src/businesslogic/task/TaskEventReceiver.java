package businesslogic.task;

import businesslogic.event.SchedaEvento;
import businesslogic.recipe.Recipe;
import businesslogic.user.User;

public interface TaskEventReceiver {
    void updateTaskCreated(SummarySheet s, Task t); //creation of assignment included in task creation
    void updateTaskPropEdited(Task t);
    void updateTaskEdited(Task t);
    void updateTaskDeleted(Task t);
    void updateTaskPositionChanged(Task t);
    void updateTaskDone(Task t);
    void updateTaskAssigned(TaskAssignment a);
    void updateAssignmentDeleted(TaskAssignment a);
    void updateTaskRearranged(Task t);

    void updateAvailabilitySet(Workshift w, User u);
    void updateAvailabilityDeleted(Workshift w, User u);


    void updateSummaryCreated(SummarySheet s);

    void updateWorkshift(Workshift w);
}
