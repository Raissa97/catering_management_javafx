package persistence;

import businesslogic.event.SchedaEvento;
import businesslogic.menu.MenuEventReceiver;
import businesslogic.menu.Section;
import businesslogic.task.*;
import businesslogic.user.User;

import java.sql.PreparedStatement;
import java.util.Scanner;

public class TaskPersistence implements TaskEventReceiver {
    @Override
    public void updateTaskCreated(SummarySheet s, Task t){
        Task.createTask(s,t);
    }

    @Override
    public void updateTaskPropEdited(Task t){
        Task.editPropTask(t);
    }

    @Override
    public void updateTaskEdited(Task t){
        Task.editTask(t);
    }

    @Override
    public void updateTaskDeleted(Task t){
        Task.removeTask(t);
    }

    @Override
    public void updateTaskPositionChanged(Task t){
        Task.setPosition(t);
    }

    @Override
    public void updateTaskDone(Task t){
        Task.setIsDone(t);
    }

    @Override
    public void updateTaskAssigned(TaskAssignment a){
        TaskAssignment.assignTask(a);
    }

    @Override
    public void updateAssignmentDeleted(TaskAssignment a){
        TaskAssignment.deleteAssignment(a);
    }

    @Override
    public void updateTaskRearranged(Task t){
        Task.setPosition(t);
    }

    public void updateAvailabilitySet(Workshift w,User u){
        Workshift.addAvailableWorker(w,u);
    }

    public void updateAvailabilityDeleted(Workshift w, User u){
        Workshift.deleteAvailableWorker(w, u);
    }

    public void updateSummaryCreated(SummarySheet s){
        SummarySheet.loadNewSummary(s);
    }

    public void updateWorkshift(Workshift w){
        Workshift.updateWorkshift(w);
    }
}
