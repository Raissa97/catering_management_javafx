package ui;

import businesslogic.CatERing;
import businesslogic.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.text.ParseException;

public class Start {

    private Main mainPaneController;

    public void initialize() {
    }

    @FXML
    void startMenus() {
        mainPaneController.startMenuManagement();
    }
    @FXML
    void startEvents() {
        mainPaneController.startEventManagement();
    }
    @FXML
    void startTasks(){ mainPaneController.startTaskManagement(); }

    public void setParent(Main main) {
        this.mainPaneController = main;
    }

}
