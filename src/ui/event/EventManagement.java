package ui.event;

import businesslogic.CatERing;
import businesslogic.event.EventInfo;
import businesslogic.event.SchedaEvento;

import businesslogic.event.ServiceInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import ui.Main;
import ui.task.TaskManagement;

import java.io.IOException;

public class EventManagement {

    @FXML
    Label userLabel;

    @FXML
    BorderPane containerPane;
    @FXML
    BorderPane eventListPane;
    @FXML
    EventList eventListPaneController;

    BorderPane eventContentPane;
    EventContent eventContentPaneController;

    Main mainPaneController;

    TaskManagement taskManagementController;

    public void initialize() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("event-content.fxml"));

        try {
            eventContentPane = loader.load();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        eventContentPaneController = loader.getController();
        eventContentPaneController.setEventManagementController(this);

        if (CatERing.getInstance().getUserManager().getCurrentUser() != null) {
            String uname = CatERing.getInstance().getUserManager().getCurrentUser().getUserName();
            userLabel.setText(uname);
        }

        eventListPaneController.setParent(this);
    }

    /*public void showCurrentEvent() {
        EventInfo e = CatERing.getInstance().getEventManager().getCurrentEvent();
        //eventContentPaneController.initialize(e);
        containerPane.setCenter(eventContentPane);
    }*/

    public void showCurrentScheda() {
        SchedaEvento sc = CatERing.getInstance().getEventManager().getCurrentScheda();
        eventContentPaneController.initialize(sc);
        containerPane.setCenter(eventContentPane);
    }

    /*public void showCurrentService() {
        ServiceInfo s = CatERing.getInstance().getEventManager().getCurrentService();
        //.initialize(s);
        containerPane.setCenter(eventContentPane);
    }*/

    public void showEventList(EventInfo e) {
        eventListPaneController.initialize();
        eventListPaneController.selectEvent(e);
        containerPane.setCenter(eventListPane);
    }

    public void endEventManagement() {
        mainPaneController.showStartPane();
    }

    public void setMainPaneController(Main main) {
        mainPaneController = main;
    }

    public void setParent(TaskManagement taskManagement) {
        taskManagementController = taskManagement;
    }
}