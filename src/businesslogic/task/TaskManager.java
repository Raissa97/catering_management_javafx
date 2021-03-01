package businesslogic.task;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.SchedaEvento;
import businesslogic.recipe.Recipe;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final List<TaskEventReceiver> taskReceivers = FXCollections.observableArrayList();
    //private static Map<Integer, Task> loadedTasks = FXCollections.observableHashMap();
    //private ObservableList<Task> tasksFromRecipe;
    //private final ObservableMap<String,Task> descTask = FXCollections.observableHashMap();

    private SummarySheet currentSummaryS;
    private Task currentTask;

    public TaskManager() {}

    /*public List<Workshift> getWorkshifts() {
        return CatERing.getInstance().getTaskManager().getWorkshifts();
    }

    public List<SummarySheet> getSummarysSheets() {
        return CatERing.getInstance().getTaskManager().getSummarysSheets();
    }

    public List<SchedaEvento> getEvents() {
        User chef = CatERing.getInstance().getUserManager().getCurrentUser();
        return CatERing.getInstance().getEventManager().getSchedeEvento(chef);
    }

    public Workshift getCurrentWorkshift() {
        return currentWorkshift;
    }

    public Task getCurrentTask(){
        return currentTask;
    }*/

    public void setCurrentSummary(SummarySheet selectedItem) {
        currentSummaryS = selectedItem;
    }

    public SummarySheet getCurrentSummaryS() {
        return currentSummaryS;
    }


    /*public TaskAssignment getCurrentAssignment() {
        return currentAssignment;
    }

    public ObservableList<Task> getAllTask(){
        return Task.loadAllTasks();
    }*/

    public ObservableList<Task> getAllTasks(SummarySheet s) {
        return Task.loadAllTasksForEvent(s);
    }

    public List<Task> getAllTaskForSummary(int id) {
        return Task.loadAllTasksForSummary(id);
    }

    /*public Map<Task,Recipe> getAllTaskRecipeForE(int idEvent) {
        return Task.loadAllTasksRecipeForEvent(idEvent);
    }*/

    public Map<Recipe,Task> getTaskFromRecipe(int idEvent, int idRecipe){
        return Task.loadTaskRecipeForRecipe(idEvent,idRecipe);
    }

    public void setCurrentTask(Task taskSelected) {
        currentTask = taskSelected;
    }

    public Map<TaskAssignment,User> getAllAssignedUsers(Task t){
        return TaskAssignment.loadAllAssignedUser(t);
    }

    public void chooseTask(Task t) {
        this.setCurrentTask(t);
    }

    /*public void chooseWorkshift(Workshift w) {
        currentWorkshift = w;
    }

    public void chooseSummaryS(SummarySheet s) {
        currentSummaryS = s;
    }

    public void chooseAssignment(TaskAssignment a) {
        currentAssignment = a;
    }*/

    //2
    public Task newTask(SummarySheet s,Task t) throws UseCaseLogicException {
        if(t.summary==null) throw new UseCaseLogicException();
        if(s==null) throw new UseCaseLogicException();
        s.getTasks().add(t);

        notifyTaskCreated(s,t);
        return t;
    }
    public void notifyTaskCreated(SummarySheet s, Task t){
        for (TaskEventReceiver r : taskReceivers) {
            r.updateTaskCreated(s,t);
        }
    }

    public void editProprTask(Task t, int time, int porz) {
        t.time = time;
        t.portions = porz;
        notifyTaskPropEdited(t);
    }
    private void notifyTaskPropEdited(Task t) {
        for (TaskEventReceiver er : this.taskReceivers) {
            er.updateTaskPropEdited(t);
        }
    }

    //2a
    public void editTask(Task t, String desc) {
        t.description = desc;
        notifyTaskEdited(t);
    }
    private void notifyTaskEdited(Task t) {
        for (TaskEventReceiver er : this.taskReceivers) {
            er.updateTaskEdited(t);
        }
    }

    //2b
    public void deleteTask(Task t) throws UseCaseLogicException {
        if(isTaskAssigned(t) || t.isDone()){ //già assegnata o già conclusa
            throw new UseCaseLogicException();
        }
        notifyTaskDeleted(t);
    }
    public void notifyTaskDeleted(Task t){
        for (TaskEventReceiver r : taskReceivers) {
            r.updateTaskDeleted(t);
        }
    }

    public int setPosition(Task t, int pos) {
        t.setPos(pos);
        notifyTaskPositionSetted(t);
        return pos;
    }
    public void notifyTaskPositionSetted(Task t){
        for(TaskEventReceiver er: taskReceivers){
            er.updateTaskPositionChanged(t);
        }
    }

    public void setDone(Task t) {
        t.setDone(true);
        t.toPrepare = false;
        notifyTaskDone(t);
    }
    public void notifyTaskDone(Task t){
        for (TaskEventReceiver r : taskReceivers) {
            r.updateTaskDone(t);
        }
    }

    //5
    public void assignTask(TaskAssignment ta) throws UseCaseLogicException {
        LocalDate now = LocalDate.now();
        boolean ret=false;
        User u = User.getUserById(ta.cook);
        Workshift w = Workshift.getTurnoFromId(ta.workshift);
        Map<Integer,List<User>> map = Workshift.getCuochiDisponibiliInTurno(w.id);
        for(int i: map.keySet()){
            for(User user: map.get(i)){
                if(user==u){
                    ret = true;
                }
            }
        }
        if(ret = false) throw new UseCaseLogicException(); //cuoco non disponibile o occupato

        /* commento questa condizione perché nel database ho solo eventi con data nel passato
        if(w.date.compareTo(Date.valueOf(now))<0) throw new UseCaseLogicException(); /ù/turno nel passato
        */
        if(w.isAssignedYet(u)) throw new UseCaseLogicException(); //il cuoco è già impegnato per un'altra task in quel turno
        //TO DO: if cucina satura throw new UseCaseLogicException();
        notifyTaskAssigned(ta);
    }
    public void notifyTaskAssigned(TaskAssignment ta){
        for (TaskEventReceiver r : taskReceivers) {
            r.updateTaskAssigned(ta);
        }
    }

    //5a
    public void deleteAssignment(TaskAssignment ta) throws UseCaseLogicException{
        Workshift w = Workshift.getTurnoFromId(ta.workshift);
        LocalDate now = LocalDate.now();
        //if(w.date.compareTo(Date.valueOf(now))<0) throw new UseCaseLogicException();// il turno è nel passato
        notifyTaskAssignmentDeleted(ta);
    }
    public void notifyTaskAssignmentDeleted(TaskAssignment ta){
        for (TaskEventReceiver r : taskReceivers) {
            r.updateAssignmentDeleted(ta);
        }
    }

    public void setAvailability(Workshift w, User u) throws UseCaseLogicException {
        if(w==null) throw new UseCaseLogicException();
        w.cooksDisponibili.add(u);
        notifyAvailabilitySet(w,u);
    }
    private void notifyAvailabilitySet(Workshift w,User u) {
        for (TaskEventReceiver r : taskReceivers) {
            r.updateAvailabilitySet(w,u);
        }
    }

    public void deleteAvailability(Workshift w, User u) throws UseCaseLogicException {
        if(w==null) throw new UseCaseLogicException();
        w.cooksDisponibili.remove(u);
        notifyAvailabilityDeleted(w,u);
    }
    private void notifyAvailabilityDeleted(Workshift w,User u) {
        for (TaskEventReceiver r : taskReceivers) {
            r.updateAvailabilityDeleted(w,u);
        }
    }

    public void updateWorkshift(Workshift turno, SchedaEvento e, User u, boolean val) throws UseCaseLogicException {
        if(!e.isEventChef(u)) throw new UseCaseLogicException();
        turno.setInSede(val);
        notifyWorkshiftUpdated(turno);
    }
    public void notifyWorkshiftUpdated(Workshift w){
        for(TaskEventReceiver r: taskReceivers){
            r.updateWorkshift(w);
        }
    }

    /*public void deleteAssignment() throws UseCaseLogicException {
        if (currentAssignment == null) {
            throw new UseCaseLogicException();//"Choose assignment before deleting it.");
        }
        currentWorkshift.assignments.remove(currentAssignment);
        notifyAssignmentDeleted();
        currentAssignment = null;
    }
    public void notifyAssignmentDeleted(){
        for (TaskEventReceiver r : taskReceivers) {
            r.updateAssignmentDeleted(currentAssignment);
        }
    }*/

    public void initialize() {
    }

    public void addEventReceiver(TaskEventReceiver rec) {
        this.taskReceivers.add(rec);
    }

    //3
    public void moveTask(Task t, int position) throws UseCaseLogicException {
        if (t == null || t.getPos() < 0 )
            throw new UseCaseLogicException();
        if (position < 0) throw new IllegalArgumentException();
        t.setPos(position);
        this.notifyTaskRearranged(t);
    }
    private void notifyTaskRearranged(Task t) {
        for(TaskEventReceiver er: taskReceivers){
            er.updateTaskRearranged(t);
        }
    }


    // STATIC METHODS FOR PERSISTENCE
    private boolean isTaskAssigned(Task t) {
        final boolean[] ret = {false};
        String query = "SELECT id FROM Task_assignment WHERE task_id="+t.getId();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {

                int id = rs.getInt("id");
                if(id==0){
                    ret[0] = false;
                }else{
                    ret[0] = true;
                }
            }
        });
        return ret[0];
    }



}
