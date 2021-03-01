package businesslogic.task;

import businesslogic.CatERing;
import businesslogic.event.SchedaEvento;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Workshift {

    public static ObservableMap<Integer,Workshift> loadedWorkshifts = FXCollections.observableHashMap();

    public int id;
    public int event_id;
    public Time from;
    public Time to;
    public Date date;
    public boolean inSede;
    public int number;

    public List<User> cooks;
    public List<TaskAssignment> assignments;
    public List<User> cooksDisponibili;

    public Workshift() {
        cooks = new ArrayList<>();
        assignments = new ArrayList<>();
        cooksDisponibili = new ArrayList<>();
    }

    public Workshift(Time from, Time to, List<User> cooks) {
        this.from = from;
        this.to = to;
        this.cooks = cooks;
        cooks = new ArrayList<>();
        assignments = new ArrayList<>();
        cooksDisponibili = new ArrayList<>();
    }

    public static Workshift getTurnoFromId(int turno_id) {
        return loadedWorkshifts.getOrDefault(turno_id, null);
    }

    public static ObservableMap<Integer,Workshift> getAllWorkshifts() {
        return loadWorkshifts();
    }

    public static ObservableMap<Integer,Workshift> getAllWorkshiftsForS(SummarySheet s) {
        return loadWorkshiftsForS(s);
    }

    public static Map<Integer,List<User>> getCuochiDisponibiliInTurno(int turno_id){
        return loadCooksDisponibili(turno_id);
    }

    private List<User> addCook(User cuoco) {
        this.cooks.add(cuoco);
        return cooks;
    }

    public boolean isCookAvailable(User currentUser) {
        if(cooksDisponibili.contains(currentUser)){
            return true;
        }else{
            return false;
        }
    }

    public boolean isAssignedYet(User u) {
        return this.cooks.contains(u);
    }

    public void setInSede(boolean bool) {
        this.inSede = bool;
    }

    public String toString() {
        return  date + " da: " + from + " a: " + to + " Cuochi: " + cooks.toString();
    }

    // PERSISTENCE METHODS
    public static ObservableMap<Integer, Workshift> loadWorkshifts() {
        //List<User> users = CatERing.getInstance().getUserManager().getAllUsers();
        /* carichiamo tutti i turni di lavoro */
        String query = "SELECT * FROM Workshifts WHERE 1";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Workshift ws = new Workshift();
                ws.id = rs.getInt("id");
                ws.from = rs.getTime("inizio_turno");
                ws.to = rs.getTime("fine_turno");
                ws.date = rs.getDate("data");
                ws.inSede = rs.getBoolean("in_sede");
                ws.number = rs.getInt("number");
                loadedWorkshifts.put(ws.id,ws);
            }
        });

        /* per ogni turno di lavoro caricato già assegnato settiamo il/i cuoco/i */
        String query2 = "SELECT * FROM Workshifts JOIN Task_Assignment ON Task_Assignment.turno_id=Workshifts.id WHERE 1";
        PersistenceManager.executeQuery(query2, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Workshift ws = new Workshift();
                /* carichiamo il cuoco/cameriere assegnato al turno */
                User u = CatERing.getInstance().getUserManager().getUser(rs.getInt("cuoco_id"));
                ws.id = rs.getInt("id");
                ws.from = rs.getTime("inizio_turno");
                ws.to = rs.getTime("fine_turno");
                ws.date = rs.getDate("data");
                ws.inSede = rs.getBoolean("in_sede");
                ws.number = rs.getInt("number");
                /* aggiorniamo la lista di cuochi inserendo il cuoco/servizio assegnato */
                ws.cooks = ws.addCook(u);
                loadedWorkshifts.replace(ws.id,ws);
            }
        });

        return loadedWorkshifts;
    }

    public static ObservableMap<Integer, Workshift> loadWorkshiftsForS(SummarySheet s) {
        ObservableMap<Integer, Workshift> map = FXCollections.observableHashMap();
        /* carichiamo tutti i turni di lavoro per il foglio riepilogativo selezionato */
        String query = "SELECT * FROM Workshifts WHERE summary_id="+s.getId()+" order by number asc";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Workshift ws = new Workshift();
                ws.id = rs.getInt("id");
                ws.from = rs.getTime("inizio_turno");
                ws.to = rs.getTime("fine_turno");
                ws.date = rs.getDate("data");
                ws.inSede = rs.getBoolean("in_sede");
                ws.number = rs.getInt("number");
                loadedWorkshifts.put(ws.id,ws);
                map.put(ws.id,ws);
            }
        });

        /* per ogni turno di lavoro caricato già assegnato settiamo il/i cuoco/i */
        String query2 = "SELECT * FROM Workshifts JOIN Task_Assignment ON Task_Assignment.turno_id=Workshifts.id WHERE summary_id="+s.getId()+" order by number asc";
        PersistenceManager.executeQuery(query2, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Workshift ws = new Workshift();
                /* carichiamo il cuoco/cameriere assegnato al turno */
                User u = CatERing.getInstance().getUserManager().getUser(rs.getInt("cuoco_id"));
                ws.id = rs.getInt("id");
                ws.from = rs.getTime("inizio_turno");
                ws.to = rs.getTime("fine_turno");
                ws.date = rs.getDate("data");
                ws.inSede = rs.getBoolean("in_sede");
                ws.number = rs.getInt("number");
                /* aggiorniamo la lista di cuochi inserendo il cuoco/servizio assegnato */
                ws.cooks = ws.addCook(u);
                loadedWorkshifts.replace(ws.id,ws);
                map.replace(ws.id,ws);
            }
        });

        return map;
    }

    public static ObservableMap<Integer, Workshift> loadWorkshiftsForSummary(SummarySheet s) {
        //List<User> users = CatERing.getInstance().getUserManager().getAllUsers();
        /* carichiamo tutti i turni di lavoro */
        String query = "SELECT * FROM Workshifts WHERE summary_id="+s.getId();
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Workshift ws = new Workshift();
                ws.id = rs.getInt("id");
                ws.from = rs.getTime("inizio_turno");
                ws.to = rs.getTime("fine_turno");
                ws.date = rs.getDate("data");
                ws.inSede = rs.getBoolean("in_sede");
                loadedWorkshifts.put(ws.id,ws);
            }
        });

        /* per ogni turno di lavoro caricato già assegnato settiamo il/i cuoco/i */
        String query2 = "SELECT * FROM Workshifts JOIN Task_Assignment ON Task_Assignment.turno_id=Workshifts.id WHERE 1";
        PersistenceManager.executeQuery(query2, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                Workshift ws = new Workshift();
                /* carichiamo il cuoco/cameriere assegnato al turno */
                User u = CatERing.getInstance().getUserManager().getUser(rs.getInt("cuoco_id"));
                ws.id = rs.getInt("id");
                ws.from = rs.getTime("inizio_turno");
                ws.to = rs.getTime("fine_turno");
                ws.date = rs.getDate("data");
                ws.inSede = rs.getBoolean("in_sede");
                /* aggiorniamo la lista di cuochi inserendo il cuoco/servizio assegnato */
                ws.cooks = ws.addCook(u);
                loadedWorkshifts.replace(ws.id,ws);
            }
        });

        return loadedWorkshifts;
    }

    public static void insertNewWorkshifts(Workshift w, int number,int event_id) {
        /* inseriamo tutti i turni di lavoro per l'evento e */
        String query = "INSERT INTO Workshifts (event_id, in_sede, data, inizio_turno, fine_turno,number) VALUES (?,?,?,?,?,?);";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, w.event_id);
                ps.setBoolean(2, w.inSede);
                ps.setDate(3, w.date);
                ps.setTime(4, w.from);
                ps.setTime(5, w.to);
                ps.setInt(6, number);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
            }
        });

        if (result[0] > 0) { // turno effettivamente inserito
            //allWorkshiftsEmpty.put();
        }
    }

    public static Map<Integer,List<User>> loadCooksDisponibili(int id) {
        Workshift ws = getTurnoFromId(id);
        Map<Integer,List<User>> users = FXCollections.observableHashMap();
        /* carichiamo tutti i turni di lavoro */
        String query = "SELECT * FROM Availability WHERE turno_id="+id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int availability_id = rs.getInt("id");
                User u = User.getUserById(rs.getInt("cuoco_id"));
                ws.cooksDisponibili.add(u);
                users.put(ws.id,ws.cooksDisponibili);
            }
        });
        return users;
    }

    public static void addAvailableWorker(Workshift w,User u) {
        Map<Integer,List<User>> users = FXCollections.observableHashMap();
        /* carichiamo tutti i turni di lavoro */
        String query = "INSERT INTO Availability (turno_id, cuoco_id) VALUES (?,?)";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setInt(1, w.id);
                ps.setInt(2, u.getId());
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                //
            }
        });
        if (result[0] > 0) {
            System.out.println("availability aggiornata correttamente inserendo la disponibilità nel turno selezionato \n");
        }
    }

    public static void deleteAvailableWorker(Workshift w, User u) {
        Map<Integer,List<User>> users = FXCollections.observableHashMap();
        /* carichiamo tutti i turni di lavoro */
        String query = "DELETE FROM Availability WHERE turno_id=? AND cuoco_id=?";
        int[] result = PersistenceManager.executeBatchUpdate(query, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setInt(1, w.id);
                ps.setInt(2, u.getId());
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                //
            }
        });
        if (result[0] > 0) {
            System.out.println("availability aggiornata correttamente eliminando l'utente dal turno selezionato \n");
        }
    }

    public static void updateWorkshift(Workshift w) {
        String eventChangeName = "UPDATE Workshifts SET in_sede=? WHERE id=?";
        int[] result = PersistenceManager.executeBatchUpdate(eventChangeName, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setBoolean(1,w.inSede);
                ps.setInt(2, w.id);
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            //System.out.println("workshift in sede modificato correttamente \n");
        }
    }
}
