package businesslogic.event;

import businesslogic.task.SummarySheet;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class EventInfo {

    private String name;
    private int id;
    private User organizer;
    private boolean isActive;
    private boolean isRicorrente;

    private static Map<Integer, EventInfo> loadedEvents = FXCollections.observableHashMap();
    public ObservableList<SchedaEvento> schedeEvento;

    public EventInfo() {
    }

    public EventInfo(String name) {
        this.name = name;
    }

    public EventInfo(String name, boolean isRic, User user) {
        this.name = name;
        this.isRicorrente = isRic;
        this.organizer = user;
        this.schedeEvento = FXCollections.observableArrayList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public String yesOrNo(){
        return isActive ? "yes": "no";
    }

    public void setActive(boolean active){
        isActive = active;
    }

    public boolean isRicorrente() {
        return isRicorrente;
    }

    public void setRicorrente(boolean ricorrente) {
        isRicorrente = ricorrente;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }


    public boolean isOrganizer(User u) {
        return u.getId() == this.organizer.getId();
    }

    public static EventInfo getEventFromId(int id_event){
        EventInfo event = new EventInfo();
        if(loadedEvents.containsKey(id_event)){
            event = loadedEvents.get(id_event);
        }
        return event;
    }

    public ObservableList<SchedaEvento> getSchede() {
        return schedeEvento;
    }

    public ObservableList<SchedaEvento> getSchedeOfU(User u) {
        ObservableList<SchedaEvento> ret = FXCollections.observableArrayList();
        for(SchedaEvento s: schedeEvento){
            if(s.getChef()==u){
                ret.add(s);
            }
        }
        return ret;
    }

    public void setServices(ObservableList<SchedaEvento> schede) {
        this.schedeEvento = schede;
    }

    @Override
    public String toString() {
        return  name + "(attivo: " + yesOrNo() + ", organizzatore:"+organizer.getUserName()+")";
    }

    // PERSISTENCE METHODS
    public static ObservableList<EventInfo> loadAllEventInfo() {
        ObservableList<EventInfo> all = FXCollections.observableArrayList();
        String query = "SELECT * FROM Events WHERE 1";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String name = rs.getString("name");
                EventInfo e = new EventInfo(name);
                e.id = rs.getInt("id");
                e.setActive(rs.getBoolean("attivo"));
                int org = rs.getInt("organizer_id");
                e.organizer = User.loadUserById(org);
                all.add(e);
            }
        });

        for (EventInfo e : all) {
            e.schedeEvento = SchedaEvento.loadSchedeForEvent(e.id);
        }

        return all;
    }

    public static void saveDifferentName(EventInfo e) {
        String eventChangeName = "UPDATE Events SET name= ? where id= ?";
        int[] result = PersistenceManager.executeBatchUpdate(eventChangeName, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setString(1, PersistenceManager.escapeString(e.getName()));
                ps.setInt(2, e.getId());
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            System.out.println("nome evento modificato correttamente \n");
        }
    }

    public static void saveNewEvent(EventInfo e) {
        String eventInsert = "INSERT INTO Events(name, organizer_id, attivo)" +
                " VALUES (?,?,?);";
        int[] result = PersistenceManager.executeBatchUpdate(eventInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setString(1, e.getName());
                ps.setInt(2, e.getOrganizer().getId());
                ps.setBoolean(3, e.isActive());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
            }
        });

        System.out.println("evento "+e.getId() + "inserito correttamente");
        if (result[0] > 0) { // evento effettivamente inserito
            loadedEvents.put(e.id, e);
        }
    }

    public static ObservableList<EventInfo> loadAllEventInfoForU(User u) {
        ObservableList<EventInfo> all = FXCollections.observableArrayList();
        String query = "SELECT * FROM Events WHERE organizer_id ="+ u.getId();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String n = rs.getString("name");
                EventInfo e = new EventInfo(n);
                e.setId(rs.getInt("id"));
                int org = rs.getInt("organizer_id");
                e.organizer = User.loadUserById(org);
                all.add(e);
            }
        });

        for (EventInfo e : all) {
            e.schedeEvento = SchedaEvento.loadSchedeForEvent(e.id);
        }
        return all;
    }


}
