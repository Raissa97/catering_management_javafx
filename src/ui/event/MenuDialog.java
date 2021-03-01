package ui.event;


import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.SchedaEvento;
import businesslogic.event.ServiceInfo;
import businesslogic.menu.Menu;
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

import java.util.List;
import java.util.Map;

public class MenuDialog {
    public static class MenuRow {
        public StringProperty menuTitle;
        public BooleanProperty proposedM;
        public BooleanProperty menuValue;

        public MenuRow() {
            menuTitle = new SimpleStringProperty("");
            menuValue = new SimpleBooleanProperty(false);
            proposedM = new SimpleBooleanProperty(false);
        }

        public void setMenuTitle(String menuTitle) {
            this.menuTitle.set(menuTitle);
        }

        public void setProposedM(boolean proposedM) { this.proposedM.set(proposedM); }

        public void setMenuValue(boolean menuValue) {
            this.menuValue.set(menuValue);
        }

        public String getMenuTitle() {
            return menuTitle.get();
        }

        public String getProposedM() {
            return proposedM.get() ? "Sì" : "No";
        }

        public boolean isProposedM() {
            return proposedM.get();
        }

        public BooleanProperty proposedMProperty() {
            return proposedM;
        }

        public boolean getMenuValue() {
            return menuValue.get();
        }

        public BooleanProperty menuValueProperty() {
            return menuValue;
        }
    }

    @FXML
    TableView<MenuRow> menusTable;
    ObservableList<MenuRow> rows;

    Stage myStage;

    List<Menu> allMenus = CatERing.getInstance().getMenuManager().getAllMenus();

    public void initialize() {
        EventInfo currentEvent = CatERing.getInstance().getEventManager().getCurrentEvent();
        SchedaEvento currentSheda = CatERing.getInstance().getEventManager().getCurrentScheda();
        ServiceInfo currentService = CatERing.getInstance().getEventManager().getCurrentService();
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        SchedaEvento e = CatERing.getInstance().getEventManager().getCurrentScheda();

        // Creo un table model a partire dalla mappa di proposedMenus
        Map<Menu, Boolean> proposedMenus = ServiceInfo.getProposedMenuMap(currentService);
        Map<Menu,Boolean> approvedMenus = ServiceInfo.getApprovedMenuMap(currentService);

        rows = FXCollections.observableArrayList();
        menusTable.getColumns().clear();
        for (Menu m : allMenus) {
            MenuRow row = new MenuRow();
            row.menuTitle = new SimpleStringProperty(m.getTitle());
            row.menuValue = new SimpleBooleanProperty(approvedMenus.containsKey(m));
            row.proposedM = new SimpleBooleanProperty(proposedMenus.containsKey(m));
            rows.add(row);
        }
        menusTable.setItems(rows);

        TableColumn<MenuRow, String> nameCol = new TableColumn<>("Menu");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("menuTitle"));

        menusTable.getColumns().add(nameCol);

        if(u.isOrganizer() && currentEvent.isOrganizer(u)) {
            TableColumn<MenuRow, String> propCol = new TableColumn<>("Menu proposto");
            propCol.setCellValueFactory(new PropertyValueFactory<>("proposedM"));

            final ToggleGroup approved = new ToggleGroup();
            TableColumn<MenuRow, Boolean> valCol = new TableColumn<>("Approva");
            valCol.setCellValueFactory(new PropertyValueFactory<>("menuValue"));
            valCol.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(Integer param) {
                    //posso selezionare una sola cella per volta
                    for (int i = 0; i < rows.size(); i++) {
                        if (i != param && rows.get(param).getMenuValue()) {
                            rows.get(i).setMenuValue(false);
                        }
                    }
                    return rows.get(param).menuValueProperty();
                }
            }));
            menusTable.getColumns().add(propCol);
            menusTable.getColumns().add(valCol);
            valCol.setEditable(true);

        } else if(u.isChef() && currentSheda.isEventChef(u)) {
            TableColumn<MenuRow, String> propCol = new TableColumn<>("Menu proposto");
            propCol.setCellValueFactory(new PropertyValueFactory<>("proposedM"));
            propCol.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(Integer param) {
                    //posso selezionare una sola cella per volta
                    for (int i = 0; i < rows.size(); i++) {
                        if (i != param && rows.get(param).isProposedM()) {
                            rows.get(i).setProposedM(false);
                        }
                    }
                    return rows.get(param).proposedMProperty();
                }
            }));

            TableColumn<MenuRow, Boolean> valCol = new TableColumn<>("Approva");
            valCol.setCellValueFactory(new PropertyValueFactory<>("menuValue"));
            valCol.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(Integer param) {

                        valCol.setEditable(false);
                        propCol.setEditable(true);

                    return rows.get(param).menuValueProperty();
                }
            }));

            menusTable.getColumns().add(propCol);
            menusTable.getColumns().add(valCol);

        }

        menusTable.setEditable(true);
        nameCol.setEditable(false);

    }

    public void setOwnStage(Stage stage) {
        myStage = stage;
    }

    @FXML
    public void okButtonPressed() {
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        SchedaEvento e = CatERing.getInstance().getEventManager().getCurrentScheda();
        EventInfo currentEvent = CatERing.getInstance().getEventManager().getCurrentEvent();

        if(u.isOrganizer() && currentEvent.isOrganizer(u)) {
            String[] all_menus = new String[rows.size()];
            boolean[] vals = new boolean[rows.size()];
            for (int i = 0; i < rows.size(); i++) {
                MenuRow mr = rows.get(i);
                all_menus[i] = mr.getMenuTitle();
                vals[i] = mr.menuValue.getValue();
                if (vals[i]) {
                    try {
                        CatERing.getInstance().getEventManager().changeApprovedMenu(CatERing.getInstance().getEventManager().getCurrentService(), all_menus, vals);
                    } catch (UseCaseLogicException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            this.myStage.close();
        }
        else{
            String[] all_prop_menus = new String[rows.size()];
            boolean[] vals = new boolean[rows.size()];
            for (int i = 0; i < rows.size(); i++) {
                MenuRow mr = rows.get(i);
                all_prop_menus[i] = mr.getMenuTitle();
                vals[i] = mr.proposedM.getValue();
                if (vals[i]) {
                    try {
                        CatERing.getInstance().getEventManager().changeProposedMenu(CatERing.getInstance().getEventManager().getCurrentService(), all_prop_menus, vals);
                    } catch (UseCaseLogicException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            this.myStage.close();
        }
    }

    @FXML
    public void annullaButtonPressed() {
        this.myStage.close();
    }
}
