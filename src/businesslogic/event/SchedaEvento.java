package businesslogic.event;

import businesslogic.menu.Menu;
import businesslogic.task.SummarySheet;
import businesslogic.task.Task;
import businesslogic.task.Workshift;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class SchedaEvento implements EventItemInfo {
    private static final Map<Integer, SchedaEvento> loadedSchede = FXCollections.observableHashMap();

    private int id;
    private int event;
    private String name;
    private Date dateStart;
    private Date dateEnd;
    private  String luogo;
    private int participants;
    private User chef;

    public List<Menu> approved_menu; //list of menus foreach scheda evento
    //public List<Task> tasks; //list of tasks for each event

    public List<Workshift> turni = FXCollections.observableArrayList();
    private final Map<User, String> employeeRoleMap;
    public ObservableList<ServiceInfo> services;

    public SchedaEvento() {
        employeeRoleMap = FXCollections.observableHashMap();
        approved_menu = FXCollections.observableArrayList();
    }

    public SchedaEvento(String name) {
        this.name = name;
        employeeRoleMap = FXCollections.observableHashMap();
        approved_menu = FXCollections.observableArrayList();
    }

    public SchedaEvento(EventInfo e, int chef_id, Date dataI, Date dataF, int numP,String luogo){
        id = 0;
        this.event = e.getId();
        this.name = e.getName();

        this.chef = User.getUserById(chef_id);

        this.dateStart = dataI;
        this.dateEnd = dataF;
        this.participants=numP;

        this.luogo = luogo;

        employeeRoleMap = FXCollections.observableHashMap();
        approved_menu = FXCollections.observableArrayList();
    }

    public static SchedaEvento getSchedaFromId(int id_scheda){
        SchedaEvento event = new SchedaEvento();
        if(loadedSchede.containsKey(id_scheda)){
            event = loadedSchede.get(id_scheda);
        }
        return event;
    }

    public static Map<Integer, SchedaEvento> getLoadedSchede() {
        return loadedSchede;
    }

    /*public static void setLoadedSchede(Map<Integer, SchedaEvento> loadedSchede) {
        SchedaEvento.loadedSchede = loadedSchede;
    }*/

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public User getChef() {
        return chef;
    }

    public void setEventChef(User user, boolean val) {
        if(val){
            this.chef = user;
        }
    }

    public int getEvent() {
        return this.event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public boolean isEventChef(User u){
        return u==this.chef;
    }

    public Map<User, String> getEmployeeRoleMap() {
        return employeeRoleMap;
    }

    /*public void setEmployeeRoleMap(Map<User, String> employeeRoleMap) {
        this.employeeRoleMap = employeeRoleMap;
    }*/

    public ObservableList<ServiceInfo> getServices() {
        return services;
    }

    public void setServices(ObservableList<ServiceInfo> services) {
        this.services = services;
    }

    public void setApprovedMenus(List<Integer> m_ids) {
        for (Integer m_id : m_ids) {
            approved_menu.add(Menu.getMenuFromId(m_id));
            //System.out.println(approved_menu.get(i).getId());
        }
    }

    public String toString() {
        return name + ": " + this.dateStart + "-" + this.dateEnd + ", " + this.participants + " pp. " + ", " + this.luogo;
    }

    /*public void addEmployeeRole(User employee, String role) {
        this.employeeRoleMap.put(employee, role);
    }*/

    // STATIC METHODS FOR PERSISTENCE
    public static ObservableList<SchedaEvento> loadAllSchedeEvento() {
        ObservableList<SchedaEvento> all = FXCollections.observableArrayList();
        String query = "SELECT * FROM SchedeEvento WHERE true"; //organizer_id ="+ currentUser.getId();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String n = rs.getString("name");
                SchedaEvento e = new SchedaEvento(n);
                e.id = rs.getInt("id");
                e.dateStart = rs.getDate("data_inizio");
                e.dateEnd = rs.getDate("data_fine");
                e.participants = rs.getInt("partecipanti");
                e.luogo = rs.getString("luogo");
                e.chef = User.loadUserById(rs.getInt("chef"));
                //e.tasks = Task.loadAllTasksForEvent(e.id);

                all.add(e);
            }
        });

        for (SchedaEvento e : all) {
            e.services = ServiceInfo.loadServiceInfoForSchedaEvento(e.id);
        }
        return all;
    }

    public static ObservableList<SchedaEvento> loadSchedeForEvent(int id) {
        ObservableList<SchedaEvento> all = FXCollections.observableArrayList();
        String query = "SELECT * FROM SchedeEvento WHERE id_event="+id; //organizer_id ="+ currentUser.getId();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String n = rs.getString("name");
                SchedaEvento e = new SchedaEvento(n);
                e.id = rs.getInt("id");
                e.dateStart = rs.getDate("data_inizio");
                e.dateEnd = rs.getDate("data_fine");
                e.participants = rs.getInt("partecipanti");
                e.luogo = rs.getString("luogo");
                e.chef = User.loadUserById(rs.getInt("chef"));
                //e.tasks = Task.loadAllTasksForEvent(e.id);
                all.add(e);
                loadedSchede.put(e.id,e);
            }
        });

        for (SchedaEvento e : all) {
            e.services = ServiceInfo.loadServiceInfoForSchedaEvento(e.id);
        }
        return all;
    }


    public static Map<Integer, SchedaEvento> loadAllSchedeMap() {
        Map<Integer, SchedaEvento> all = FXCollections.observableHashMap();
        String query = "SELECT * FROM SchedeEvento WHERE true"; //organizer_id ="+ currentUser.getId();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String n = rs.getString("name");
                SchedaEvento e = new SchedaEvento(n);
                e.id = rs.getInt("id");
                e.dateStart = rs.getDate("data_inizio");
                e.dateEnd = rs.getDate("data_fine");
                e.participants = rs.getInt("partecipanti");
                e.luogo = rs.getString("luogo");
                e.chef = User.loadUserById(rs.getInt("chef"));
                all.put(e.getId(),e);
                loadedSchede.put(e.id,e);
            }
        });

        for(int i: all.keySet()) {
            SchedaEvento e = all.get(i);
            e.services = ServiceInfo.loadServiceInfoForSchedaEvento(e.id);
        }
        return all;
    }

    public static void saveNewSchedaEvento(SchedaEvento sc) {
        String eventInsert = "INSERT INTO SchedeEvento (name, data_inizio, data_fine, partecipanti, luogo, id_event)" +
                " VALUES (?,?,?,?,?,?);";
        int[] result = PersistenceManager.executeBatchUpdate(eventInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setString(1, sc.getName());
                ps.setDate(2, sc.getDateStart());
                ps.setDate(3, sc.getDateEnd());
                ps.setInt(4, sc.getParticipants());
                ps.setString(5, sc.getLuogo());
                ps.setInt(6, sc.getEvent());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
            }
        });

        if (result[0] > 0) { // evento effettivamente inserito
            loadedSchede.put(sc.id, sc);
        }
    }

    /*public static ObservableList<SchedaEvento> loadAllSchedeOfChef(User u) {
        ObservableList<SchedaEvento> all = FXCollections.observableArrayList();
        String query = "SELECT * FROM SchedeEvento WHERE chef ="+ u.getId();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String n = rs.getString("name");
                SchedaEvento e = new SchedaEvento(n);
                e.id = rs.getInt("id");
                e.dateStart = rs.getDate("data_inizio");
                e.dateEnd = rs.getDate("data_fine");
                e.participants = rs.getInt("partecipanti");
                e.luogo = rs.getString("luogo");
                e.tasks = (List<Task>) Task.loadAllTasksForEvent(e.id);
                int org = rs.getInt("organizer_id");
                e.chef = User.loadUserById(rs.getInt("chef"));
                all.add(e);
            }
        });

        for (SchedaEvento e : all) {
            e.services = ServiceInfo.loadServiceInfoForSchedaEvento(e.id);
            e.summary_sheet = SummarySheet.loadSummaryForEvent(e.id);
        }

        return all;
    }*/

    public static ObservableList<SchedaEvento> loadAllSchedaEventoOfChef(User u) {
        ObservableList<SchedaEvento> all = FXCollections.observableArrayList();
        String query = "SELECT * FROM SchedeEvento WHERE chef ="+ u.getId();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String n = rs.getString("name");
                SchedaEvento e = new SchedaEvento(n);
                e.id = rs.getInt("id");
                e.dateStart = rs.getDate("date_start");
                e.dateEnd = rs.getDate("date_end");
                e.participants = rs.getInt("expected_participants");
                //e.tasks = Task.loadAllTasksForEvent(e.id);
                //int org = rs.getInt("organizer_id");
                e.chef = User.loadUserById(rs.getInt("chef"));
                all.add(e);
            }
        });

        for (SchedaEvento e : all) {
            e.services = ServiceInfo.loadServiceInfoForSchedaEvento(e.id);

        }

        return all;
    }

    public static ObservableList<SchedaEvento> loadAllSchedeEventoOfU(User u) {
        ObservableList<SchedaEvento> all = FXCollections.observableArrayList();
        String query = "SELECT * FROM SchedeEvento WHERE chef ="+ u.getId();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String n = rs.getString("name");
                SchedaEvento e = new SchedaEvento(n);
                e.id = rs.getInt("id");
                e.dateStart = rs.getDate("data_inizio");
                e.dateEnd = rs.getDate("data_fine");
                e.participants = rs.getInt("partecipanti");
                e.luogo = rs.getString("luogo");
                e.chef = User.loadUserById(rs.getInt("chef"));
                //e.tasks = Task.loadAllTasksForEvent(e.id);
                all.add(e);
            }
        });

        for (SchedaEvento e : all) {
            e.services = ServiceInfo.loadServiceInfoForSchedaEvento(e.id);

        }
        return all;
    }

    public static Map<Integer, SchedaEvento> loadAllSchedeMapOfChef(User u) {
        Map<Integer, SchedaEvento> all = FXCollections.observableHashMap();
        String query = "SELECT * FROM SchedeEvento WHERE chef ="+ u.getId();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String n = rs.getString("name");
                SchedaEvento e = new SchedaEvento(n);
                e.id = rs.getInt("id");
                e.event=rs.getInt("id_event");
                e.dateStart = rs.getDate("data_inizio");
                e.dateEnd = rs.getDate("data_fine");
                e.participants = rs.getInt("partecipanti");
                e.luogo = rs.getString("luogo");
                e.chef = User.loadUserById(rs.getInt("chef"));
                //e.tasks = Task.loadAllTasksForEvent(e.id);
                all.put(e.getId(),e);
                loadedSchede.put(e.id,e);
            }
        });

        for (int i: all.keySet()) {
            SchedaEvento e = all.get(i);
            e.services = ServiceInfo.loadServiceInfoForSchedaEvento(e.id);
        }

        return all;
    }

    public static void saveChef(SchedaEvento e) {
        String eventAssignChef = "UPDATE SchedeEvento SET chef= ? WHERE id= ?";
        int[] result = PersistenceManager.executeBatchUpdate(eventAssignChef, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setInt(1, (e.getChef().getId()));
                ps.setInt(2, e.getId());
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            System.out.println("Scheda evento IS ="+e.getId()+ " EVENTO IS = "+ e.event +" CHEF IS = "+e.chef);
            System.out.println("Chef assegnato correttamente \n");
        }
    }

    public static void deleteSchedaEvento(SchedaEvento s) {
        String query = "DELETE FROM SchedeEvento WHERE id = "+ s.getId();
        PersistenceManager.executeUpdate(query);
        loadedSchede.remove(s);

    }

    public static void saveDifferentName(SchedaEvento s) {
        String eventChangeName = "UPDATE SchedeEvento SET name= ? where id= ?";
        int[] result = PersistenceManager.executeBatchUpdate(eventChangeName, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setString(1, PersistenceManager.escapeString(s.getName()));
                ps.setInt(2, s.getId());
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            System.out.println("nome evento modificato correttamente \n");
        }
    }

    public static void saveInfosScheda(SchedaEvento s) {
        String eventChangeName = "UPDATE SchedeEvento SET name= ?, data_inizio=?,data_fine=?, luogo=?, partecipanti=? where id= ?";
        int[] result = PersistenceManager.executeBatchUpdate(eventChangeName, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setString(1,s.getName());
                ps.setDate(2, s.getDateStart());
                ps.setDate(3, s.getDateEnd());
                ps.setString(4, s.getLuogo());
                ps.setInt(5, s.getParticipants());
                ps.setInt(6, s.getId());
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count){
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            System.out.println("scheda evento modificata correttamente \n");
        }
    }


}
