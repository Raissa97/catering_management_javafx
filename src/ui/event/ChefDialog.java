package ui.event;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.SchedaEvento;
import businesslogic.user.User;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ChefDialog {

    public static class ChefRow {
        public StringProperty chefName;
        public BooleanProperty chefValue;

        public ChefRow() {
            chefName = new SimpleStringProperty("");
            chefValue = new SimpleBooleanProperty(false);
        }

        public void setChefName(String n) {
            chefName.setValue(n);
        }

        public String getChefName() {
            return chefName.getValue();
        }

        public void setChefValue(boolean b) {
            chefValue.setValue(b);
        }

        public boolean getChefValue() {
            return chefValue.getValue();
        }

        public BooleanProperty chefValueProperty() {
            return chefValue;
        }
    }

    @FXML
    TableView<ChefRow> chefsTable;
    ObservableList<ChefRow> rows;

    Stage myStage;

    SchedaEvento currentScheda = CatERing.getInstance().getEventManager().getCurrentScheda();

    public void initialize() {
        Map<Integer,User> users = CatERing.getInstance().getUserManager().getAllChefs();
        ArrayList<Integer> users_id = new ArrayList<>(users.keySet());
        rows = FXCollections.observableArrayList();

        chefsTable.getColumns().clear();
        Collections.sort(users_id);

        for (Integer id: users_id) {
            User u = User.getUserById(id);
            if(u!=null) {
                ChefRow row = new ChefRow();
                row.chefName = new SimpleStringProperty(u.getUserName());
                row.chefValue = new SimpleBooleanProperty(u == currentScheda.getChef());
                /*if (currentEvent.getEventChef() != null) {
                    System.out.println("NI");
                    if (currentEvent.getEventChef() == u) {
                        System.out.println("OOOK");
                        chefsTable.setEditable(false);
                    } else {
                        System.out.println("OOOOOOOOO");
                        row.setChefValue(true);
                        chefsTable.setEditable(false);
                    }
                }*/
                rows.add(row);
            }
        }
        chefsTable.setItems(rows);

        TableColumn<ChefRow, String> chefsNameCol = new TableColumn<>("Chef");
        chefsNameCol.setCellValueFactory(new PropertyValueFactory<>("chefName"));

        //chefsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn<ChefRow, Boolean> chefsValCol = new TableColumn<>("Select");
        chefsValCol.setCellValueFactory(new PropertyValueFactory<>("chefValue"));
        chefsValCol.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Integer param) {
                //System.out.println("chef "+rows.get(param).getChefName()+" changed value to " +rows.get(param).getChefValue());
                for (Integer i = 0; i < rows.size(); i++) {
                    if (i != param && rows.get(param).getChefValue()) {
                        rows.get(i).setChefValue(false);
                    }
                }
                return rows.get(param).chefValueProperty();
            }
        }));

        chefsTable.getColumns().add(chefsNameCol);
        chefsTable.getColumns().add(chefsValCol);

        chefsTable.setEditable(true);
        chefsNameCol.setEditable(false);
        chefsValCol.setEditable(true);
    }

    public void setOwnStage(Stage stage) {
        myStage = stage;
    }

    @FXML
    public void okButtonPressed() {
        String[] allChefsName = new String[rows.size()];
        boolean[] vals = new boolean[rows.size()];
        for (int i = 1; i < rows.size(); i++) {
            ChefRow cr = rows.get(i);
            allChefsName[i] = cr.chefName.getValue();
            vals[i] = cr.chefValue.getValue();
            if (vals[i]) {
                try {
                    CatERing.getInstance().getEventManager().setEventChef(CatERing.getInstance().getEventManager().getCurrentScheda(), allChefsName, vals);
                } catch (UseCaseLogicException ex) {
                    ex.printStackTrace();
                }
            }
        }
        this.myStage.close();
    }

    @FXML
    public void annullaButtonPressed() {
        this.myStage.close();
    }
}
