package businesslogic.task;

import businesslogic.event.SchedaEvento;
import businesslogic.recipe.Recipe;
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

public class Task {
    public int id;
    SummarySheet summary;
    public String description;
    public Recipe recipe;
    public int time;
    boolean done;
    boolean toPrepare;
    public int pos;
    public int portions;

    static Map<Integer,String> allTaskLoaded = FXCollections.observableHashMap();
    static List<String> descTasks = FXCollections.observableArrayList();
    static Map<String,Task> taskFromDesc = FXCollections.observableHashMap();
    static Map<SchedaEvento,Task> allLoadedTasksForR = FXCollections.observableHashMap();
    static List<Integer> allIdLoaded = FXCollections.observableArrayList();
    static Map<Integer,Task> allTasksIdLoaded = FXCollections.observableHashMap();

    public Task() {}

    public Task(SummarySheet s, String d, Boolean p, Recipe r, Integer t, Integer porz, Integer posizione) {
            summary = s;
            description = d;
            done = false;
            toPrepare = p;
            time = t;
            recipe = r;
            pos = posizione;
            portions = porz;
    }

    public Task(SummarySheet s, String d, Boolean p, Integer porz, Integer posizione) {
        summary = s;
        description = d;
        done = false;
        toPrepare = p;
        recipe = new Recipe(d);
        portions=porz;
        pos = posizione;
    }

    public int getId(){
        return this.id;
    }

    public Boolean isDone() {
        return done;
    }

    public void setDone(boolean val){
        done = val;
    }
    private static Task getTaskFromId(int task_id) {
        Task t = new Task();
        for(int i: allIdLoaded){
            if(i== task_id){
                t = allTasksIdLoaded.get(i);
            }else t= null;
        }
        return t;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public static Task getTaskFromDescription(String desc) {
        Task t = new Task();
        for(String d: descTasks){
            if(d.equals(desc)){
                t = taskFromDesc.get(d);
            }else t= null;
        }
        return t;
    }

    public static Task getTaskFromAssignment(TaskAssignment ta) {
        return loadTaskFromTA(ta);
    }

    public String toString() {
        String d = ", ";
        return
                "#: "+ pos +"  Descrizione: "+ description + d + "Terminato: " + (done ? "si" : "no") + d + "Da preparare: " + toPrepare + d
                        + "Tempo stimato: " + time + " min" + d + "Ricetta: " + (recipe != null ? recipe.getName() : "Nessuna");
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<Task> loadAllTasks() {
        ObservableList<Task> allTasks = FXCollections.observableArrayList();
        String query = "SELECT * FROM Tasks WHERE true";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Task t = new Task();
                t.id = rs.getInt("id");
                int summary_id = rs.getInt("id_summary");
                t.summary = SummarySheet.getSummaryFromId(summary_id);
                int recipe_id = rs.getInt("id_ricetta");
                t.recipe = Recipe.getRecipeFromId(recipe_id);
                t.description = rs.getString("descrizione");
                t.done = rs.getBoolean("done");
                t.toPrepare = rs.getBoolean("to_prepare");
                t.time = rs.getInt("tempo_previsto");
                t.portions=rs.getInt("porzioni");
                allTasks.add(t);
                allIdLoaded.add(t.id);
                allTasksIdLoaded.put(t.id,t);
                descTasks.add(t.description);
                taskFromDesc.put(t.description,t );
                allTaskLoaded.put(t.pos,t.description);
            }
        });
        return allTasks;
    }

    public static ObservableList<Task> loadAllTasksForEvent(SummarySheet s) {
        int summary_id = s.getId();
        ObservableList<Task> allTasksforE = FXCollections.observableArrayList();
        String query = "SELECT * FROM Tasks WHERE id_summary="+summary_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Task t = new Task();
                t.id = rs.getInt("id");
                int recipe_id = rs.getInt("id_ricetta");
                t.recipe = Recipe.getRecipeFromId(recipe_id);
                t.description = rs.getString("descrizione");
                t.done = rs.getBoolean("done");
                t.toPrepare = rs.getBoolean("to_prepare");
                t.time = rs.getInt("tempo_previsto");
                t.portions=rs.getInt("porzioni");
                t.pos=rs.getInt("pos");
                allTasksforE.add(t);
            }
        });
        return allTasksforE;
    }

    public static ObservableList<Task> loadAllTasksForSummary(int id) {
        ObservableList<Task> allTasksforE = FXCollections.observableArrayList();
        String query = "SELECT * FROM Tasks WHERE id_summary="+id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Task t = new Task();
                t.id = rs.getInt("id");
                t.summary = SummarySheet.getSummaryFromId(id);
                int recipe_id = rs.getInt("id_ricetta");
                t.recipe = Recipe.getRecipeFromId(recipe_id);
                t.description = rs.getString("descrizione");
                t.done = rs.getBoolean("done");
                t.toPrepare = rs.getBoolean("to_prepare");
                t.time = rs.getInt("tempo_previsto");
                t.portions=rs.getInt("porzioni");
                t.pos=rs.getInt("pos");
                allTasksforE.add(t);
            }
        });
        return allTasksforE;
    }

    public static Map<Task,Recipe> loadAllTasksRecipeForEvent(int event_id) {
        Map<Task,Recipe> allTasksforE = FXCollections.observableHashMap();
        String query = "SELECT * FROM Tasks WHERE id_scheda="+event_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Task t = new Task();
                t.id = rs.getInt("id");
                t.summary = SummarySheet.getSummaryFromId(event_id);
                int recipe_id = rs.getInt("id_ricetta");
                t.recipe = Recipe.getRecipeFromId(recipe_id);
                t.description = rs.getString("descrizione");
                t.done = rs.getBoolean("done");
                t.toPrepare = rs.getBoolean("to_prepare");
                t.time = rs.getInt("tempo_previsto");
                t.portions=rs.getInt("porzioni");
                allTasksforE.put(t,t.recipe);
            }
        });
        return allTasksforE;
    }

    public static Map<Recipe,Task> loadTaskRecipeForRecipe(int idEvent, int idRecipe) {
        Map<Recipe,Task> taskRecipesList = FXCollections.observableHashMap();
        ObservableList<Task> taskList = FXCollections.observableArrayList();
        final int[] count = {1};
        String query = "SELECT * FROM Tasks WHERE id_summary=" + idEvent +" AND id_ricetta=" + idRecipe;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Task t = new Task();
                t.id = rs.getInt("id");
                t.summary = SummarySheet.getSummaryFromId(idEvent);
                t.recipe = Recipe.getRecipeFromId(idRecipe);
                t.description = rs.getString("descrizione");
                t.done = rs.getBoolean("done");
                t.toPrepare = rs.getBoolean("to_prepare");
                t.time = rs.getInt("tempo_previsto");
                t.portions=rs.getInt("porzioni");
                t.pos= count[0];
                taskList.add(t);
                taskRecipesList.put(t.recipe,t);
                allTasksIdLoaded.put(t.id,t);
                allLoadedTasksForR.put(SchedaEvento.getSchedaFromId(idRecipe),t);
                //System.out.println("["+t.pos+"] task = "+t);
                count[0]++;
            }
        });
        return taskRecipesList;
    }

    public static ObservableList<Task> loadTaskForRecipe(int idEvent, int idRecipe) {
        ObservableList<Task> taskList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Tasks WHERE id_scheda=" + idEvent +" AND id_ricetta=" + idRecipe;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Task t = new Task();
                t.id = rs.getInt("id");
                t.summary = SummarySheet.getSummaryFromId(idEvent);
                t.recipe = Recipe.getRecipeFromId(idRecipe);
                t.description = rs.getString("descrizione");
                t.done = rs.getBoolean("done");
                t.toPrepare = rs.getBoolean("to_prepare");
                t.time = rs.getInt("tempo_previsto");
                t.pos= rs.getInt("pos");
                taskList.add(t);
                allTasksIdLoaded.put(t.id,t);
                allLoadedTasksForR.put(SchedaEvento.getSchedaFromId(idRecipe),t);
                //System.out.println("["+t.pos+"] task = "+t);
            }
        });
        return taskList;
    }

    public static void createTask(SummarySheet s, Task t) {
        String query = "INSERT INTO Tasks(id, id_summary, id_ricetta, descrizione, done, to_prepare, tempo_previsto,pos,porzioni) VALUES" +
                "(?,?,?,?,?,?,?,?,?)";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setInt(1,t.id);
                ps.setInt(2,t.summary.getId());
                ps.setInt(3, t.recipe.getId());
                ps.setString(4, t.description);
                ps.setBoolean(5,t.done);
                ps.setBoolean(6,t.toPrepare);
                ps.setInt(7, t.time);
                ps.setInt(8,t.pos);
                ps.setInt(9,t.portions);
                allLoadedTasksForR.put(SchedaEvento.getSchedaFromId(t.recipe.getId()),t);
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            s.getTasks().add(t);
            System.out.println("task inserita correttamente \n");
        }
    }

    public static void editTask(Task t) {
        String query = "UPDATE Tasks SET descrizione=? where id=?";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setString(1, t.description);
                ps.setInt(2, t.id);
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            System.out.println("desc task modificata correttamente \n");
        }
    }

    public static void editPropTask(Task t) {
        String query = "UPDATE Tasks SET tempo_previsto=?, porzioni=? where id=?";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setInt(1,t.time);
                ps.setInt(2,t.portions);
                ps.setInt(3, t.id);
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            System.out.println("proprietà della task modificata correttamente \n");
        }
    }

    public static void setPosition(Task t) {
        String query = "UPDATE Tasks SET pos=? where id=?";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setInt(1, t.getPos());
                ps.setInt(2,t.getId());
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            System.out.println("task position "+t.getPos()+"  modificata correttamente \n");
        }
    }

    private static Task loadTaskFromTA(TaskAssignment ta) {
        Task t = new Task();
        String query = "SELECT * FROM Tasks WHERE id="+ ta.task_id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                t.id = rs.getInt("id");
                t.summary = SummarySheet.getSummaryFromId(rs.getInt("id_summary"));
                t.recipe = Recipe.getRecipeFromId(rs.getInt("id_ricetta"));
                t.description = rs.getString("descrizione");
                t.done = rs.getBoolean("done");
                t.toPrepare = rs.getBoolean("to_prepare");
                t.time = rs.getInt("tempo_previsto");
                t.pos= rs.getInt("pos");
                t.portions=rs.getInt("porzioni");
            }
        });

        return t;
    }

    public static void removeTask(Task t) {
        String query = "DELETE from Tasks WHERE id=?";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setInt(1,t.id);
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            System.out.println("task eliminata correttamente \n");
        }
    }

    public static void setIsDone(Task t) {
        String query = "UPDATE Tasks SET done=?, to_prepare=? WHERE id=?";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setBoolean(1, t.done);
                ps.setBoolean(2,t.toPrepare);
                ps.setInt(3,t.id);
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            System.out.println("task is done updated \n");
        }
    }
}
