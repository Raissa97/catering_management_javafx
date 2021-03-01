package businesslogic.task;

import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TaskAssignment {
    public static ObservableList<TaskAssignment> loadedAllAssignment = FXCollections.observableArrayList();
    public static Map<Task,TaskAssignment> mapTA = FXCollections.observableHashMap();
    private static Map<TaskAssignment,User>  loadedCooksOrService = FXCollections.observableHashMap();
    private static Map<String,Integer>  cookOrServiceNameId = FXCollections.observableHashMap();

    public int id;
    public int task_id;
    public int workshift;
    public int cook;

    public TaskAssignment() {
        this.task_id = 0;
        this.workshift=0;
        this.cook=0;
    }

    public TaskAssignment(int t_id) {
        this.task_id = t_id;
        this.workshift=0;
        this.cook=0;
    }

    public TaskAssignment(int t, int w, int c) {
        task_id = t;
        workshift = w;
        cook = c;
    }

    public static User getAssFromT(int task_id) {
        //System.out.println("task = "+ task_id);
        return getCookAssignmentForT(task_id);
    }

    public static Map<Task,TaskAssignment> getAssignmentsFromT(Task t) {
        //System.out.println("task = "+ t);
        return loadAllAssignment(t);
    }

    public TaskAssignment ass(Task t){
        TaskAssignment ta = new TaskAssignment(t.id);
        if(t.id == this.task_id){
            ta=this;
        }
        return ta;
    }

    private int getTaskId() {
        return task_id;
    }

    public int getCount() {
        return 0;
    }

    public int getPosition(Task t) {
        return 0;
    }

    public String toString() {
        String d = ", ";
        return
                //"Descrizione: "+ task.description + "Terminato: " + task.done + d +
                        "Cuoco: " + User.getUserById(cook);
    }

    //Persistence methods
    public static Map<Task,TaskAssignment> loadAllAssignment(Task currentTask) {
        String query = "SELECT * FROM Task_Assignment  WHERE task_id="+currentTask.id;

        TaskAssignment ta = new TaskAssignment(currentTask.id);

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                ta.cook = rs.getInt("cuoco_id");
                ta.workshift = rs.getInt("turno_id");
                ta.task_id = rs.getInt("task_id");
                ta.id = rs.getInt("id");

                loadedAllAssignment.add(ta);
            }
        });

        //System.out.println( ta.id +"   "+ ta.cook +"   "+User.getUserById(ta.cook)+"  " + currentTask.recipe+ "   " + currentTask.id+ "\n");

        mapTA.put(currentTask, ta);

        //System.out.println(loadedCooksOrService);

        return mapTA;
    }

    public static User getCookAssignmentForT(int task_id){
        User cookOrService = new User();
        TaskAssignment ta = new TaskAssignment(task_id);
        String query = "SELECT cuoco_id FROM Task_Assignment WHERE task_id = " + task_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                ta.cook = rs.getInt("cuoco_id");
            }
        });
        cookOrService = User.getUserById(ta.cook);
        System.out.println("cook of task assignment = " + User.getUserById(ta.cook));

        return cookOrService;
    }

    public static Map<TaskAssignment,User> getAssignmentsForW(Workshift w) {
        Map<TaskAssignment,User> assignments = FXCollections.observableHashMap();
        String query = "SELECT * FROM Task_Assignment WHERE turno_id = "+w.id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                TaskAssignment ta = new TaskAssignment();
                ta.id = rs.getInt("id");
                ta.cook = rs.getInt("cuoco_id");
                ta.workshift = w.id;
                ta.task_id = rs.getInt("task_id");
                assignments.put(ta,User.getUserById(ta.cook));
            }
        });
        return assignments;
    }

    public static List<User> getAssignmentsForW(int w_id) {
        List<User> assignments = FXCollections.observableArrayList();
        String query = "SELECT * FROM Task_Assignment WHERE turno_id = "+w_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                TaskAssignment ta = new TaskAssignment();
                ta.id = rs.getInt("id");
                ta.cook = rs.getInt("cuoco_id");
                ta.workshift = w_id;
                ta.task_id = rs.getInt("task_id");
                User.loadUserById(ta.cook);
                assignments.add(User.getUserById(ta.cook));
            }
        });
        return assignments;
    }

    public static Map<TaskAssignment,User> loadAllAssignedUser(Task currentTask) {
        String query = "SELECT * FROM Task_Assignment  WHERE task_id="+currentTask.id;
        TaskAssignment ta = new TaskAssignment(currentTask.id);

         PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                ta.cook = rs.getInt("cuoco_id");
                ta.workshift = rs.getInt("turno_id");
                ta.task_id = rs.getInt("task_id");
                ta.id = rs.getInt("id");

                User.loadUserById(ta.cook);
            }
        });
        //System.out.println( ta.id +"   "+ ta.cook +"   "+User.getUserById(ta.cook)+"  " + currentTask.recipe+ "   " + currentTask.id+ "\n");
        User u = User.getUserById(ta.cook);
        loadedCooksOrService.put(ta,u);
        //System.out.println(loadedCooksOrService);

        return loadedCooksOrService;
    }

    public static void assignTask(TaskAssignment a) {
        String query = "INSERT Task_Assignment (id,task_id,turno_id, cuoco_id) VALUES(?,?,?,?) ";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setInt(1, a.id);
                ps.setInt(2, a.task_id);
                ps.setInt(3, a.workshift);
                ps.setInt(4,a.cook);
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            System.out.println("task assegnata correttamente \n");
        }
    }

    public static void createTA(Task t){
        //TO DO: riconosce t ma non t.id e quindi non inserisce il giusto assignment
        TaskAssignment ta = new TaskAssignment(t.id);
        System.out.println(ta.task_id);

        String query = "INSERT INTO Task_Assignment (id, task_id) VALUES (?,?);";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setInt(1,ta.id);
                ps.setInt(2,t.id);

                loadedAllAssignment.add(ta);
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            System.out.println("task assignment inserito correttamente \n");
        }
    }

    public static void deleteAssignment(TaskAssignment a) {
        String query = "DELETE FROM Task_Assignment WHERE id = ?";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setInt(1, a.id);
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            System.out.println("task assignment eliminato correttamente \n");
        }
    }

}
