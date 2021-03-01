package businesslogic.task;

import businesslogic.event.SchedaEvento;
import businesslogic.menu.Menu;
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

public class SummarySheet {

    private int id;
    private int schedaEvento;
    private String title;
    private List<Task> tasks; //list of tasks for each event
    private List<Menu> approved_menu; //list of menus foreach event
    private ObservableList<Recipe> recipes_of_m; //map menu id and recipes


    public static Map<Integer, SummarySheet> allLoadedSummariesMap = FXCollections.observableHashMap(); //map event id summary
    public static Map<Integer, SummarySheet> allLoadedSummaries = FXCollections.observableHashMap(); //map summaries

    // crea e precompila il sommario
    public SummarySheet() {
        this.id=0;
        this.approved_menu = FXCollections.observableArrayList();
        this.recipes_of_m = FXCollections.observableArrayList();
        this.tasks = FXCollections.observableArrayList();
    }

    public SummarySheet(SchedaEvento s) {
        this.schedaEvento = s.getId();
        this.title = s.getName();
        this.approved_menu = FXCollections.observableArrayList();
        this.recipes_of_m = FXCollections.observableArrayList();
        this.tasks = FXCollections.observableArrayList();
    }

    public SummarySheet(int id, int event, String title) {
        this.schedaEvento = event;
        this.title = title;
        this.id=id;
        this.approved_menu = FXCollections.observableArrayList();
        this.recipes_of_m = FXCollections.observableArrayList();
        this.tasks = FXCollections.observableArrayList();
    }

    public static SummarySheet getSummaryFromId(int summary_id) {
        return allLoadedSummaries.getOrDefault(summary_id, null);
    }

    public int getId(){
        return this.id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public ObservableList<Recipe> getRecipes() {
        return recipes_of_m;
    }

    public void setSchedaEvento(int schedaEvento) {
        this.schedaEvento = schedaEvento;
    }

    public int getSchedaEvento() {
        return schedaEvento;
    }

    public static SummarySheet getSummaryFromScheda(SchedaEvento s) {
        return loadSummaryForScheda(s.getId());
    }

    /*public static SummarySheet getSummaryFromScheda(int id) {
        System.out.println("summary sheet = "+allLoadedSummariesMap.getOrDefault(id, null));
        return allLoadedSummariesMap.getOrDefault(id, null);
    }*/

    public void setApprovedMenus(List<Integer> m_ids) {
        for (Integer m_id : m_ids) {
            this.approved_menu.add(Menu.getMenuFromId(m_id));
            //System.out.println(approved_menu.get(i).getId());
        }
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTasksInSummary(List<Recipe> recipes, SchedaEvento current_event, int time, int porz, int pos){
        for (Recipe r : recipes){
            //System.out.println("i = " +  "  " + r + "  [" + r.getId() + "] ");
            if (!recipes_of_m.contains(r)) {
                this.recipes_of_m.add(r);
            }
           /* try{
                CatERing.getInstance().getTaskManager().newTask(current_event,r.getName(),r,time,porz,pos);
            }catch (UseCaseLogicException e){
                e.printStackTrace();
            }*/

        }
    }

    public String toString() {
        int id = this.schedaEvento;
        return SchedaEvento.getSchedaFromId(id).toString();
    }

    // PERSISTENCE METHODS
    public static SummarySheet loadSummaryForScheda(int id) {
        SummarySheet s = new SummarySheet();
        String query = "SELECT * FROM Summary_Sheets WHERE scheda_id="+id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                s.id = rs.getInt("id");
                s.schedaEvento = id;
                s.title = SchedaEvento.getSchedaFromId(id).getName();
                s.tasks = Task.loadAllTasksForEvent(s);
                allLoadedSummariesMap.put(id, s);
                allLoadedSummaries.put(s.getId(),s);
            }
        });

        return s;
    }

    public static void loadNewSummary(SummarySheet s) {
        String eventInsert = "INSERT INTO Summary_Sheets (id,scheda_id)" +
                " VALUES (?,?);";
        int[] result = PersistenceManager.executeBatchUpdate(eventInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, s.id);
                ps.setInt(2, s.schedaEvento);

                s.tasks = (List<Task>) Task.loadAllTasksForEvent(s);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
            }
        });

        if (result[0] > 0) { // summary effettivamente inserito
            allLoadedSummariesMap.put(s.getSchedaEvento(), s);
            allLoadedSummaries.put(s.getId(),s);
        }

    }

    public static Map<Integer,SummarySheet> getAllSummaries(Map<Integer, SchedaEvento> schede) {
        Map<Integer,SummarySheet> sheets = FXCollections.observableHashMap();
        String query = "SELECT * FROM Summary_Sheets WHERE 1";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {

                int scheda = rs.getInt("scheda_id");
                int id = rs.getInt("id");
                String title ="";
                if(schede.containsKey(scheda)) {
                    title = schede.get(scheda).getName();
                }
                SummarySheet s = new SummarySheet(scheda,id,title);
                allLoadedSummariesMap.put(s.schedaEvento, s);
                allLoadedSummaries.put(s.getId(),s);
                sheets.put(s.getSchedaEvento(),s);
            }
        });

        return sheets;
    }
}
