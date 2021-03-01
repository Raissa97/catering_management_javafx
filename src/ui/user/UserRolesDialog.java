package ui.user;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class UserRolesDialog {
    public static class RoleRow {
        public StringProperty roleName;
        public BooleanProperty roleValue;

        public void roleRow() {
            roleName = new SimpleStringProperty("");
            roleValue = new SimpleBooleanProperty(false);
        }

        public void setRoleName(String n) {
            roleName.setValue(n);
        }

        public String getRoleName() {
            return roleName.getValue();
        }

        public void setRoleValue(boolean b) {
            roleValue.setValue(b);
        }

        public boolean getRoleValue() {
            return roleValue.getValue();
        }
    }

    @FXML
    TableView<RoleRow> featuresTable;
    ObservableList<RoleRow> rows;

    Stage myStage;



    /*public void initialize() {
        String roles[] = (String[]) CatERing.getInstance().getUserManager().getCurrentUser().getRoles().toArray();

        // Creo un table model a partire dal set di roles
        Set<String> rnames = null;
        for(int i=0; i< roles.length;i++){
            rnames.add(roles[i].toString());
        }
        //Collections.sort(rnames);
        rows = FXCollections.observableArrayList();
        for (String s: rnames) {
            RoleRow row = new RoleRow();
            row.roleName = new SimpleStringProperty(s);
            row.roleValue = new SimpleBooleanProperty(roles.get(s));
            rows.add(row);
        }

        featuresTable.setItems(rows);
        TableColumn<RoleRow, String> featureNameCol = new TableColumn<>("Feature");
        featureNameCol.setCellValueFactory(new PropertyValueFactory<>("featureName"));
        TableColumn<RoleRow, Boolean> featureValCol = new TableColumn<>("Value");
        featureValCol.setCellValueFactory(new PropertyValueFactory<>("featureValue"));
        featureValCol.setCellFactory(c -> new CheckBoxTableCell<>(new Callback<Integer, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Integer integer) {
                return rows.get(integer).roleValue;
            }
        }));

        rolesTable.getColumns().add(featureNameCol);
        rolessTable.getColumns().add(featureValCol);
        rolesTable.setEditable(true);
        roleNameCol.setEditable(false);
        roleValCol.setEditable(true);
    }

    public void setOwnStage(Stage stage) {
        myStage = stage;
    }

    /*@FXML
    public void okButtonPressed() {
        String[] features = new String[rows.size()];
        boolean[] vals = new boolean[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            FeatureRow fr = rows.get(i);
            features[i] = fr.featureName.getValue();
            vals[i] = fr.featureValue.getValue();
        }
        try {
            CatERing.getInstance().getMenuManager().setAdditionalFeatures(features, vals);
        } catch (UseCaseLogicException ex) {
            ex.printStackTrace();
        }
        this.myStage.close();
    }

    @FXML public void annullaButtonPressed() {
        this.myStage.close();
    }*/
}
