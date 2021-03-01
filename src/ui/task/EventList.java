package ui.task;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.SchedaEvento;
import businesslogic.user.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;


public class EventList {

    @FXML
    Button apriButton;

    @FXML
    ListView<EventInfo> eventList;
    ObservableList<EventInfo> eventListItems;

    @FXML
    ListView<SchedaEvento> schedaList;
    ObservableList<SchedaEvento> schedaListItems;

    @FXML
    GridPane centralPane;
    Pane emptyPane;
    boolean paneVisible = true;

    public void initialize() {
        User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();
        if (eventListItems == null) {
            if(currentUser.isChef() && !currentUser.isService() && !currentUser.isCook()) {
                eventListItems = CatERing.getInstance().getEventManager().getAllEventsForU(currentUser.getId());
            }
            else if(!currentUser.isChef() && currentUser.isService() && currentUser.isCook()) {
                eventListItems = CatERing.getInstance().getEventManager().getAllEvents();
            }

            apriButton.setVisible(true);

            eventList.setItems(eventListItems);
            eventList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            eventList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EventInfo>() {
                @Override
                    public void changed(ObservableValue<? extends EventInfo> observableValue, EventInfo oldE, EventInfo newE) {
                        if (newE != null && newE != oldE) {
                            CatERing.getInstance().getEventManager().setCurrentEvent(eventList.getSelectionModel().getSelectedItem());
                            EventInfo currentEvent = CatERing.getInstance().getEventManager().getCurrentEvent();
                            boolean chef = false;

                            for(SchedaEvento s: currentEvent.getSchede()){
                                if(s.isEventChef(currentUser)){
                                    chef = true;
                                }
                            }

                            //schedaListItems = currentEvent.getSchede();
                            //schedaList.setItems(schedaListItems);
                        }

                        if (!paneVisible) {
                            centralPane.getChildren().remove(emptyPane);
                            centralPane.add(centralPane, 0, 1);
                            paneVisible = true;
                        }
                    }
                });
            System.out.println(eventList);
        } else {
            eventList.refresh();
        }
    }


    public void selectEvent(EventInfo e) {
        if (e != null) this.eventList.getSelectionModel().select(e);
        else this.eventList.getSelectionModel().select(-1);
    }

    public void selectScheda(SchedaEvento s) {
        if (s != null) this.schedaList.getSelectionModel().select(s);
        else this.schedaList.getSelectionModel().select(-1);
        try {
            SchedaEvento sc = schedaList.getSelectionModel().getSelectedItem();
            CatERing.getInstance().getEventManager().chooseSchedaEvento(sc);
            //taskManagementController.showSchede();
        } catch (UseCaseLogicException e) {
            e.printStackTrace();
        }

    }

    public void apriEventoButtonPressed(ActionEvent actionEvent) {
        System.out.println(CatERing.getInstance().getEventManager().getCurrentEvent());
    }
}
