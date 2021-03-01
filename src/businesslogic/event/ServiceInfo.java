package businesslogic.event;

import businesslogic.menu.Menu;
import businesslogic.task.Workshift;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.BatchUpdateHandler;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.sql.*;

public class ServiceInfo implements EventItemInfo {
    private static Map<Integer, ServiceInfo> loadedServices = FXCollections.observableHashMap();

    public static ServiceInfo currentService;
    private int id;
    private int scheda_id;
    private String name;
    private java.sql.Date date;
    private Time timeStart;
    private Time timeEnd;
    private int participants;
    private Menu proposedMenu;
    private Menu approvedMenu;


    public ServiceInfo(String name) {
        this.name = name;
    }

    public ServiceInfo(SchedaEvento e, String name, LocalDate serviceDate, Time timeI, Time timeF, int people) {
        this.scheda_id = e.getId();
        this.name = name;
        this.date=java.sql.Date.valueOf(serviceDate);
        this.timeStart=timeI;
        this.timeEnd=timeF;
        this.participants=people;
    }

    public String getNameService(){
        return this.name;
    }

    public static Map<Integer, ServiceInfo> getLoadedServices() {
        return loadedServices;
    }

    public ServiceInfo getCurrentService() {
        return currentService;
    }

    public int getId() {
        return id;
    }

    public int getScheda_id() {
        return scheda_id;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public Time getTimeStart() {
        return timeStart;
    }

    public Time getTimeEnd() {
        return timeEnd;
    }

    public int getParticipants() {
        return participants;
    }

    public Integer getApprovedMenuId() {
        return approvedMenu.getId();
    }
    public Menu getApprovedMenu() {
        return approvedMenu;
    }

    public Menu getProposedMenu() {
        return proposedMenu;
    }
    public static void setLoadedServices(Map<Integer, ServiceInfo> loadedServices) {
        ServiceInfo.loadedServices = loadedServices;
    }

    public static void setCurrentService(ServiceInfo s) {
        currentService = s;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setScheda_id(int scheda_id) {
        this.scheda_id = scheda_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTimeStart(Time timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(Time timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public void setProposedMenu(Menu m, boolean value) {
        if(value){
            this.proposedMenu = m;
        }
    }

    public void setApprovedMenu(Menu m, boolean value) {
        if(value){
            this.approvedMenu = m;
            SchedaEvento e = SchedaEvento.getSchedaFromId(this.scheda_id);
            //e.getEvent().setActive(true);
        }
    }

    public String printInfos(){
        return date + " (" + timeStart + "-" + timeEnd + "), " + participants + " pp.";
    }

    public boolean hasMenuApproved() {
        return approvedMenu!=null;
    }

    public static Map<Menu, Boolean> getProposedMenuMap(ServiceInfo currentService) {
        return ServiceInfo.loadAllMenuProposedMap(currentService);
    }

    public static Map<Menu, Boolean> getApprovedMenuMap(ServiceInfo currentService) {
        return ServiceInfo.loadAllMenuApprovedMap(currentService);
    }

    public String toString() {
        return name + ": " + date + " (" + timeStart + "-" + timeEnd + "), " + participants + " pp.";
    }

    // STATIC METHODS FOR PERSISTENCE
    public static void saveNewService(ServiceInfo s) {
        String serviceInsert = "INSERT INTO Services (event_id, name, service_date, time_start, time_end, " +
                "expected_participants, proposed_menu, approved_menu) " +
                "VALUES(?,?,?,?,?,?,?,?)";
        int[] result = PersistenceManager.executeBatchUpdate(serviceInsert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, s.scheda_id);
                ps.setString(2, s.getName());
                ps.setDate(3, s.getDate());
                ps.setTime(4, s.getTimeStart());
                ps.setTime(5, s.getTimeEnd());
                ps.setInt(6, s.getParticipants());
                ps.setInt(7, 0);
                ps.setInt(8, 0);
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
            }
        });

        if (result[0] > 0) { // evento effettivamente inserito
            loadedServices.put(s.id, s);
        }
    }

    public static void editService(ServiceInfo s) {
        String serviceEdit = "UPDATE Services SET name= ?, service_date=?, time_start=?, time_end=?, expected_participants=? WHERE id = ?;";
        int[] result = PersistenceManager.executeBatchUpdate(serviceEdit, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setString(1, s.getName());
                ps.setDate(2, (s.date));
                ps.setTime(3, s.timeStart);
                ps.setTime(4, s.timeEnd);
                ps.setInt(5, s.participants);
                ps.setInt(6, s.getId());
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            //System.out.println("SERVICE IS ="+s.getNameService());
            System.out.println("service modificato correttamente \n");
        }
    }

    public static void deleteService(ServiceInfo s) {
        String delSec = "DELETE FROM Services WHERE id = " + s.id;
        PersistenceManager.executeUpdate(delSec);
        loadedServices.remove(s);
    }

    public static ObservableList<ServiceInfo> loadServiceInfoForSchedaEvento(int id) {
        ObservableList<ServiceInfo> result = FXCollections.observableArrayList();
        String query = "SELECT * FROM Services WHERE scheda_id = " + id;
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String s = rs.getString("name");
                ServiceInfo service = new ServiceInfo(s);
                service.id = rs.getInt("id");
                service.date = rs.getDate("service_date");
                service.timeStart = rs.getTime("time_start");
                service.timeEnd = rs.getTime("time_end");
                service.participants = rs.getInt("expected_participants");
                service.proposedMenu = Menu.getMenuFromId(rs.getInt("proposed_menu"));
                service.approvedMenu = Menu.getMenuFromId(rs.getInt("approved_menu"));
                result.add(service);
            }
        });
        return result;
    }

    public static Map<Menu,Boolean> loadAllMenuProposedMap(ServiceInfo s) {
        String query = "SELECT menus.id, menus.title, services.id FROM Services JOIN Menus on menus.id=services.proposed_menu WHERE Services.id="+s.getId();
        Map<Menu,Boolean> proposed_menu_of_s = FXCollections.observableHashMap();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                Menu m = Menu.getMenuFromId(id);
                m.setTitle(rs.getString("title"));
                proposed_menu_of_s.put(m,s.getApprovedMenu()==m);
            }
        });

        return proposed_menu_of_s;
    }

    public static Map<Menu,Boolean> loadAllMenuApprovedMap(ServiceInfo s) {
        String query = "SELECT menus.id, menus.title, services.id FROM Services JOIN Menus on menus.id=services.approved_menu WHERE Services.id="+s.getId();
        Map<Menu,Boolean> app_menu_of_s = FXCollections.observableHashMap();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                Menu m = Menu.getMenuFromId(id);
                m.setTitle(rs.getString("title"));
                app_menu_of_s.put(m,s.getApprovedMenu()==m);
            }
        });

        return app_menu_of_s;
    }

    public static void editApprovedMenu(ServiceInfo s) {
        String serviceEdit = "UPDATE Services SET services.approved_menu=? WHERE id = ?;";
        int[] result = PersistenceManager.executeBatchUpdate(serviceEdit, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setInt(1, s.getApprovedMenu().getId());
                ps.setInt(2, s.getId());
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            System.out.println("SERVICE IS ="+s.getNameService());
            System.out.println("approved menu modificato correttamente "+ s.approvedMenu);
        }
    }

    public static void editProposedMenu(ServiceInfo s) {
        String serviceEdit = "UPDATE Services SET proposed_menu=? WHERE id = ?;";
        int[] result = PersistenceManager.executeBatchUpdate(serviceEdit, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int x) throws SQLException {
                ps.setInt(1, s.getProposedMenu().getId());
                ps.setInt(2, s.getId());
            }
            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                // no generated ids to handle
            }
        });
        if (result[0] > 0) {
            System.out.println("proposed menu modificato correttamente "+ s.proposedMenu);
        }
    }

}
