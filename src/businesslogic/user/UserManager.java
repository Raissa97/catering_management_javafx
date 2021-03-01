package businesslogic.user;

import businesslogic.menu.Menu;
import businesslogic.task.Task;
import businesslogic.task.TaskAssignment;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserManager {
    private User currentUser;

    public void login(String username){
        this.currentUser = User.loadUserByUsername(username);
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public List<User> getAllUsers(){
        return User.loadAllUsers();
    }

    public Map<Integer,User> getAllChefs() {
        return  User.loadAllChefs();
    }

    public List<User> getAllCookOrService() {
        return  User.loadAllCookOrService();
    }

    public User getUser(int id){
        return User.loadUserById(id);
    }
}
