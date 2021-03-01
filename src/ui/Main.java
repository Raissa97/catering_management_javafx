package ui;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import ui.event.EventManagement;
import ui.menu.MenuManagement;
import ui.task.TaskManagement;
import ui.task.TaskManagement;
import ui.user.UserLoginManagement;

import java.io.IOException;
import java.text.ParseException;

public class Main {

    @FXML
    AnchorPane paneContainer;

    @FXML
    FlowPane startPane;

    @FXML
    Start startPaneController;

    BorderPane menuManagementPane;
    MenuManagement menuManagementPaneController;

    BorderPane eventManagementPane;
    EventManagement eventManagementPaneController;

    BorderPane taskManagementPane;
    TaskManagement taskManagementPaneController;

    BorderPane userLoginManagementPane;
    UserLoginManagement userLoginManagementController;

    public void initialize() {
        startPaneController.setParent(this);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("user/login.fxml"));
        try {
            userLoginManagementPane = loader.load();
            userLoginManagementController = loader.getController();
        } catch (IOException ex) {
            System.out.println("ERRORE LOGIN: l'utente inserito non esiste");
        }
        if(!CatERing.getInstance().getUserManager().getCurrentUser().getUserName().equals("")) {
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("event/event-management.fxml"));
            FXMLLoader loader3 = new FXMLLoader(getClass().getResource("menu/menu-management.fxml"));
            FXMLLoader loader4 = new FXMLLoader(getClass().getResource("task/task-management.fxml"));
            try {
                eventManagementPane = loader2.load();
                eventManagementPaneController = loader2.getController();
                eventManagementPaneController.setMainPaneController(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                menuManagementPane = loader3.load();
                menuManagementPaneController = loader3.getController();
                menuManagementPaneController.setMainPaneController(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                taskManagementPane = loader4.load();
                taskManagementPaneController = loader4.getController();
                taskManagementPaneController.setMainPaneController(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void startLoginManagement() {
        userLoginManagementController.initialize();
    }

    public void startMenuManagement() {
        menuManagementPaneController.initialize();
        paneContainer.getChildren().remove(startPane);
        paneContainer.getChildren().add(menuManagementPane);
        AnchorPane.setTopAnchor(menuManagementPane, 0.0);
        AnchorPane.setBottomAnchor(menuManagementPane, 0.0);
        AnchorPane.setLeftAnchor(menuManagementPane, 0.0);
        AnchorPane.setRightAnchor(menuManagementPane, 0.0);
    }
    public void startEventManagement() {
        eventManagementPaneController.initialize();
        paneContainer.getChildren().remove(startPane);
        paneContainer.getChildren().add(eventManagementPane);
        AnchorPane.setTopAnchor(eventManagementPane, 0.0);
        AnchorPane.setBottomAnchor(eventManagementPane, 0.0);
        AnchorPane.setLeftAnchor(eventManagementPane, 0.0);
        AnchorPane.setRightAnchor(eventManagementPane, 0.0);
    }

    public void startTaskManagement() {
        taskManagementPaneController.initialize();
        paneContainer.getChildren().remove(startPane);
        paneContainer.getChildren().add(taskManagementPane);
        AnchorPane.setTopAnchor(taskManagementPane, 0.0);
        AnchorPane.setBottomAnchor(taskManagementPane, 0.0);
        AnchorPane.setLeftAnchor(taskManagementPane, 0.0);
        AnchorPane.setRightAnchor(taskManagementPane, 0.0);
    }

    public void showStartPane() {
        startPaneController.initialize();
        paneContainer.getChildren().remove(menuManagementPane);
        paneContainer.getChildren().remove(eventManagementPane);
        paneContainer.getChildren().remove(taskManagementPane);
        paneContainer.getChildren().add(startPane);
    }

}
