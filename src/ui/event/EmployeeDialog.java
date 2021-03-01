package ui.event;

import businesslogic.CatERing;
import businesslogic.event.SchedaEvento;
import businesslogic.user.User;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.*;

public class EmployeeDialog {

    public static class EmployeeRow {
        public User user;
        public ObservableList<User.Role> userRoles;
        public StringProperty employeeName;
        public StringProperty roles;
        public StringProperty employeeRole;
        public BooleanProperty employeeValue;

        public EmployeeRow() {
            employeeName = new SimpleStringProperty("");
            employeeRole = new SimpleStringProperty("");
            roles = new SimpleStringProperty("");
            employeeValue = new SimpleBooleanProperty(false);
            userRoles = FXCollections.observableArrayList();
        }

        public ObservableList<User.Role> getUserRoles() {
            return userRoles;
        }

        public void setUserRoles(ObservableList<User.Role> userRoles) {
            this.userRoles = userRoles;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public String getRoles() {
            return roles.get();
        }

        public void setRoles(String roles) {
            this.roles.set(roles);
        }

        public String getEmployeeName() {
            return employeeName.get();
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName.set(employeeName);
        }

        public String getEmployeeRole() {
            return employeeRole.get();
        }

        public StringProperty employeeRoleProperty() {
            return employeeRole;
        }

        public void setEmployeeRole(String employeeRole) {
            this.employeeRole.set(employeeRole);
        }

        public boolean isEmployeeValue() {
            return employeeValue.get();
        }

        public BooleanProperty employeeValueProperty() {
            return employeeValue;
        }

        public void setEmployeeValue(boolean employeeValue) {
            this.employeeValue.set(employeeValue);
        }
    }

    @FXML
    TableView<EmployeeRow> employeeTable;
    ObservableList<EmployeeRow> rows;

    Stage myStage;

    SchedaEvento currentScheda = CatERing.getInstance().getEventManager().getCurrentScheda();

    public void initialize() {
        List<User> allUsers = CatERing.getInstance().getUserManager().getAllCookOrService();
        List<Integer> ids = new ArrayList<>();
        ObservableMap<EmployeeRow,ObservableList<String>> options = FXCollections.observableHashMap();
        ObservableList<String> roleString = FXCollections.observableArrayList();
        rows = FXCollections.observableArrayList();

        for(User u: allUsers){
            int id = u.getId();
            if(u.getRoles().contains(User.Role.CUOCO)||u.getRoles().contains(User.Role.SERVIZIO)) {
                if (!ids.contains(id)) {
                    ids.add(id);
                }
            }
        }
        Collections.sort(ids);

        employeeTable.getColumns().clear(); // per problema doppie celle
        for (int id: ids) {
            User u = User.getUserById(id);
            if(u!=null) {
                EmployeeRow row = new EmployeeRow();
                row.employeeName = new SimpleStringProperty(u.getUserName());
                row.roles = new SimpleStringProperty(u.getRoles().toString());
                row.employeeValue = new SimpleBooleanProperty(currentScheda.getEmployeeRoleMap().containsKey(u));
                row.employeeRole = new SimpleStringProperty("");
                row.userRoles.addAll(u.getRoles());

                rows.add(row);
            }
        }
        employeeTable.setItems(rows);

        TableColumn<EmployeeRow, String> employeeNameCol = new TableColumn<>("Personale");
        employeeNameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        TableColumn<EmployeeRow, String> roleCol = new TableColumn<>("Ruolo");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("roles"));

        TableColumn<EmployeeRow, StringProperty> employeeRoleCol = new TableColumn<>("Scegli ruolo");


        TableColumn<EmployeeRow, Boolean> employeeValCol = new TableColumn<>("Scegli");
        employeeValCol.setCellValueFactory(new PropertyValueFactory<>("employeeValue"));
        employeeValCol.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Integer param) {
                for (int i = 0; i < rows.size(); i++) {
                    if (i != param && rows.get(param).isEmployeeValue()) {
                        rows.get(i).setEmployeeValue(false);
                        employeeRoleCol.setEditable(false);
                    }
                }
                employeeRoleCol.setEditable(true);
                return rows.get(param).employeeValueProperty();
            }
        }));

        employeeTable.getColumns().add(employeeNameCol);
        employeeTable.getColumns().add(roleCol);
        employeeTable.getColumns().add(employeeValCol);
        employeeTable.getColumns().add(employeeRoleCol);

        employeeTable.setEditable(true);
        employeeNameCol.setEditable(false);
        roleCol.setEditable(false);
        employeeRoleCol.setEditable(false);
        employeeValCol.setEditable(true);
    }

    public void setOwnStage(Stage stage) {
        myStage = stage;
    }

    @FXML
    public void okButtonPressed() {
        /*String[] allChefsName = new String[rows.size()];
        boolean[] vals = new boolean[rows.size()];
        for (int i = 1; i < rows.size(); i++) {
            ChefRow cr = rows.get(i);
            allChefsName[i] = cr.chefName.getValue();
            vals[i] = cr.chefValue.getValue();
            if (vals[i]) {
                try {
                    CatERing.getInstance().getEventManager().setEventChef(CatERing.getInstance().getEventManager().getCurrentEvent(), allChefsName, vals);
                } catch (UseCaseLogicException ex) {
                    ex.printStackTrace();
                }
            }
        }*/
        this.myStage.close();
    }

    @FXML
    public void annullaButtonPressed() {
        this.myStage.close();
    }
}
