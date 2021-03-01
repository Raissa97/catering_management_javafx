package businesslogic.user;

import businesslogic.CatERing;
import businesslogic.event.SchedaEvento;
import businesslogic.task.Workshift;
import javafx.collections.FXCollections;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class User {

    private static Map<Integer, User> loadedUsers = FXCollections.observableHashMap();
    private static Map<User,Boolean>  loadedChefs = FXCollections.observableHashMap();
    private static List<User>  loadedCOS = FXCollections.observableArrayList();
    private static Map<String,User>  chefsName = FXCollections.observableHashMap();
    private static Map<Integer,User> chefsId = FXCollections.observableHashMap();

    public enum Role {SERVIZIO, CUOCO, CHEF, ORGANIZZATORE};

    public static Map<Role,Character> roleAndId = FXCollections.observableHashMap();

    private int id;
    private String username;
    private Set<Role> roles;

    public void initializeRoleMap(){
        roleAndId.put(Role.SERVIZIO,'s');
        roleAndId.put(Role.CUOCO,'c');
        roleAndId.put(Role.CHEF,'h');
        roleAndId.put(Role.ORGANIZZATORE,'o');
    }

    private static Role chefRole(String role_id) {
        Role x = Role.CHEF;
        if(roleAndId.containsKey(role_id)) {
            if (role_id == "h") {
                x = Role.CHEF;
                System.out.println("Role string == "+getRoleString(x)+"\n");
            }
        }
        return x;
    }

    public static String getRoleString(Role r) {
        if(r==Role.CHEF) return "chef";
        if(r==Role.CUOCO) return  "cuoco";
        if(r==Role.SERVIZIO) return  "servizio";
        if(r==Role.ORGANIZZATORE) return  "organizzatore";
        else return null;
    }

    public User() {
        id = 0;
        username = "";
        this.roles = new HashSet<>();
    }
    public User(String user) {
        this.username = user;
        this.roles = new HashSet<>();
    }
    public User(int id) {
        this.id=id;
        this.roles = new HashSet<>();
    }
    public User(int id, String name){
        this.id=id;
        this.username = name;
        this.roles = new HashSet<>();
    }
    public User(String user,Set<Role> role) {
        this.username = user;
        this.roles = role;
    }


    public boolean isChef() {
        return roles.contains(Role.CHEF);
    }

    public boolean isOrganizer() {
        return roles.contains(Role.ORGANIZZATORE);
    }

    public boolean isCook() {
        return roles.contains(Role.CUOCO);
    }

    public boolean isService() {
        return roles.contains(Role.SERVIZIO);
    }

    public String getUserName() {
        return username;
    }

    private void setUsername(String name) {
        this.username = name;
    }

    public static User getUserById(int u_id){
        User u;
        u = loadedUsers.getOrDefault(u_id, null);
        return u;
    }

    public static User getUserByName(String s) {
        System.out.println("name = "+s);
        User u;
        u = chefsName.getOrDefault(s,null);
        System.out.println("user "+ u);
        return u;
    }

    public Set<User.Role> getRoles(){
        return this.roles;
    }

    public int getId() {
        return this.id;
    }

    public boolean isAssigned(Workshift w) {
        return w.cooks.contains(this);
    }

    public boolean isAvailable(Workshift w) {
        return w.cooksDisponibili.contains(this);
    }

    public String getAssignedName(Workshift w) {
        String s="";
        if(isAssigned(w)) {
            if (w.cooks.contains(this)) {
                s = this.getUserName();
            }
        }
        return s;
    }

    public String toString() {
        String result = username;
        if (roles.size() > 0) {
            result += ": ";

            for (User.Role r : roles) {
                result += r.toString() + " ";
            }
        }
        return result;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static User loadUserById(int uid) {
        if (loadedUsers.containsKey(uid)) return loadedUsers.get(uid);

        User load = new User();
        String userQuery = "SELECT * FROM Users WHERE id='"+uid+"'";
        PersistenceManager.executeQuery(userQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                load.id = rs.getInt("id");
                load.username = rs.getString("username");
            }
        });
        if (load.id > 0) {
            loadedUsers.put(load.id, load);
            String roleQuery = "SELECT * FROM UserRoles WHERE user_id=" + load.id;
            PersistenceManager.executeQuery(roleQuery, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    String role = rs.getString("role_id");
                    switch (role.charAt(0)) {
                        case 'c':
                            load.roles.add(User.Role.CUOCO);
                            break;
                        case 'h':
                            load.roles.add(User.Role.CHEF);
                            break;
                        case 'o':
                            load.roles.add(User.Role.ORGANIZZATORE);
                            break;
                        case 's':
                            load.roles.add(User.Role.SERVIZIO);
                    }
                }
            });
        }
        return load;
    }

    public static User loadUserByUsername(String username) {
        User u = new User(username);
        String userQuery = "SELECT * FROM users WHERE username='"+username+"'";
        PersistenceManager.executeQuery(userQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                u.id = rs.getInt("id");
                u.username = rs.getString("username");
            }
        });

        if (u.id > 0) {
            loadedUsers.put(u.id, u);
            String roleQuery = "SELECT * FROM UserRoles WHERE user_id=" + u.id;
            PersistenceManager.executeQuery(roleQuery, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    String role = rs.getString("role_id");
                    switch (role.charAt(0)) {
                        case 'c':
                            u.roles.add(User.Role.CUOCO);
                            break;
                        case 'h':
                            u.roles.add(User.Role.CHEF);
                            break;
                        case 'o':
                            u.roles.add(User.Role.ORGANIZZATORE);
                            break;
                        case 's':
                            u.roles.add(User.Role.SERVIZIO);
                    }
                }
            });
        }else {
            System.out.println("ERRORE LOGIN: l'utente inserito non esiste");
        }
        return u;

    }

    public static List<User> loadAllUsers() {
        String query = "SELECT * FROM users WHERE true";
        ArrayList<User> newUsers = new ArrayList<>();
        ArrayList<Integer> newUids = new ArrayList<>();
        ArrayList<User> oldUsers = new ArrayList<>();
        ArrayList<Integer> oldUids = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                if (loadedUsers.containsKey(id)) {
                    User u = loadedUsers.get(id);
                    u.username = rs.getString("username");
                    oldUids.add(rs.getInt("id"));
                    oldUsers.add(u);
                } else {
                    User u = new User();
                    u.id = id;
                    u.username = rs.getString("username");
                    newUids.add(rs.getInt("id"));
                    newUsers.add(u);
                }
            }
        });

        for (int i = 0; i < newUsers.size(); i++) {
            User u = newUsers.get(i);

            // load roles
            String featQ = "SELECT * FROM Roles JOIN UserRoles ON Roles.id=UserRoles.role_id WHERE user_id = " + u.id;
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    u.roles.add(Role.valueOf(rs.getString("role")));
                }
            });
        }

        for (int i = 0; i < oldUsers.size(); i++) {
            User u = oldUsers.get(i);

            // load roles
            u.roles.clear();
            String featQ = "SELECT * FROM Roles JOIN UserRoles ON Roles.id=UserRoles.role_id WHERE user_id = " + u.id;
            PersistenceManager.executeQuery(featQ, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    u.roles.add(Role.valueOf(rs.getString("role")));
                }
            });
        }
        for (User u: newUsers) {
            loadedUsers.put(u.id, u);
        }
        return FXCollections.observableArrayList(loadedUsers.values());
    }

    public static Map<Integer,User> loadAllChefs() {
        String query = "SELECT username, id, role_id FROM Users JOIN UserRoles ON Users.id=UserRoles.user_id WHERE role_id='h'";
        ArrayList<User> users = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                User u = new User(id);
                u.setUsername(rs.getString("username"));
                chefsId.put(u.id,u);
                chefsName.put(u.getUserName(),u);
                Role r = chefRole(rs.getString("role_id"));
                u.roles.add(r);
                users.add(u);
            }
        });

        SchedaEvento currentScheda = CatERing.getInstance().getEventManager().getCurrentScheda();
        for (User u : users) {
            if (currentScheda.getChef() != null)
                loadedChefs.put(u, currentScheda.getChef().getId() == u.getId());
            else loadedChefs.put(u, false);
        }

        return chefsId;
    }

    public static List<User> loadAllCookOrService() {
        String query = "SELECT username, id, role_id FROM Users JOIN UserRoles ON Users.id=UserRoles.user_id WHERE role_id='c' OR role_id='s'";

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                User u = new User(id);
                u.setUsername(rs.getString("username"));
                //chefsId.put(u.id,u);
                //chefsName.put(u.getUserName(),u);
                Role r = chefRole(rs.getString("role_id"));
                u.roles.add(r);
                loadedCOS.add(u);
            }
        });

        return loadedCOS;
    }

    /*public static Map<User,Boolean> loadAllCookOrSUsers() {
        String query = "SELECT * FROM Users JOIN UserRoles ON Users.id=UserRoles.user_id WHERE role_id='c' OR role_id='s'";
        ArrayList<User> users = new ArrayList<>();

        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                int id = rs.getInt("id");
                User user = new User(id);
                if(!loadedChefs.containsKey(user)) {
                    User u = new User();
                    u.id = id;
                    u.username = rs.getString("username");
                    cookOrServiceNameId.put(u.username,u.id);
                    Role r = chefRole(rs.getString("role_id"));
                    u.roles.add(r);
                    users.add(u);
                }
            }
        });
        Task currentTask = CatERing.getInstance().getTaskManager().getCurrentTask();
        if(loadedCooksOrService==null) {
            for (User u : users) {
                //System.out.println(u.getUserName() + "==" + currentEvent.getEventChef() + "?? " + currentEvent.getEventChef().getId()==u.getId() + "\n");
                if (currentTask.id != 0)
                    loadedCooksOrService.put(u,true);
                else loadedCooksOrService.put(u, false);
            }
        }
        return loadedCooksOrService;
    }*/

}
