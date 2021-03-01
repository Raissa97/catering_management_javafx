package ui.event;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.SchedaEvento;
import businesslogic.event.ServiceInfo;
import businesslogic.user.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.general.Results;

import javax.swing.text.AttributeSet;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

public class EventContent {
    public BorderPane schedeListPane;
    public Label schedaName;

    public Button addSchedaButton;
    public Button deleteScheda;
    public Button modificaScheda;
    @FXML
    Button menuServiceButton;
    @FXML
    Button addServiceButton;
    @FXML
    Button deleteService;
    @FXML
    Button modificaServiceButton;

    @FXML
    Label nameLabel;

    @FXML
    ListView<ServiceInfo> serviceList;
    ObservableList<ServiceInfo> serviceListItems;

    @FXML
    ListView<SchedaEvento> schedaList;
    ObservableList<SchedaEvento> schedaListItems;

    @FXML
    Label serviceTitle;

    @FXML
    BorderPane serviceListPane;

    @FXML
    Button editNameButton;
    @FXML
    Button assegnaChef;
    @FXML
    Button scegliPersonale;

    @FXML
    GridPane centralPane;
    Pane emptyPane;
    boolean paneVisible = true;

    EventManagement eventManagementController;

    public void initialize(SchedaEvento currentScheda) {
        CatERing.getInstance().getEventManager().setCurrentSchedaEvento(currentScheda);
        EventInfo e = CatERing.getInstance().getEventManager().getCurrentEvent();
        nameLabel.setText(currentScheda.getName()+ "\n\nData: "+currentScheda.getDateStart()+" - "+currentScheda.getDateEnd()
                + "\nLuogo: "+currentScheda.getLuogo()+"\nPartecipanti: "+currentScheda.getParticipants());

        schedaListItems = e.getSchede();
        schedaList.setItems(schedaListItems);

        schedaList.refresh();

        schedaList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        schedaList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SchedaEvento>() {
            @Override
            public void changed(ObservableValue<? extends SchedaEvento> observableValue, SchedaEvento oldS, SchedaEvento newS) {
                if (newS != null && newS != oldS) {
                    if (!paneVisible) {
                        centralPane.getChildren().remove(emptyPane);
                        centralPane.add(schedeListPane,0,0);
                        centralPane.add(serviceListPane, 1, 0);
                        paneVisible = true;
                    }
                    schedaName.setText(newS.getName());
                }
            }
        });

        initializeServiceList(currentScheda);
    }

    public void initializeServiceList(SchedaEvento sc){
        SchedaEvento currentScheda = CatERing.getInstance().getEventManager().getCurrentScheda();
        EventInfo currentEvent = CatERing.getInstance().getEventManager().getCurrentEvent();
        User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();

        serviceListItems = sc.getServices();
        if(serviceListItems!=null) {
            serviceList.setItems(serviceListItems);

            serviceList.refresh();

            serviceList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            serviceList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ServiceInfo>() {
                @Override
                public void changed(ObservableValue<? extends ServiceInfo> observableValue, ServiceInfo oldService, ServiceInfo newService) {
                    if (newService != null && newService != oldService) {
                        if (!paneVisible) {
                            centralPane.getChildren().remove(emptyPane);
                            centralPane.add(schedeListPane, 0, 0);
                            centralPane.add(serviceListPane, 1, 0);
                            paneVisible = true;
                        }
                        serviceTitle.setText(newService.getNameService());
                        menuServiceButton.setDisable(false);
                    }else{
                        menuServiceButton.setDisable(true);
                    }
                }
            });

            if(serviceList.getItems().size()>0) {
                /* enable other section actions*/
                if (currentScheda.isEventChef(currentUser)) {
                    addServiceButton.setDisable(true);
                    deleteService.setDisable(true);
                    modificaServiceButton.setDisable(true);

                    editNameButton.setDisable(true);
                    assegnaChef.setDisable(true);
                    scegliPersonale.setDisable(true);
                }else if (currentEvent.isOrganizer(currentUser)) {
                    addServiceButton.setDisable(false);
                    deleteService.setDisable(false);
                    modificaServiceButton.setDisable(false);

                    editNameButton.setDisable(false);
                    assegnaChef.setDisable(false);
                    scegliPersonale.setDisable(false);
                }
            }else {
                if (currentScheda.isEventChef(currentUser)) {
                    addServiceButton.setDisable(true);
                    deleteService.setDisable(true);
                    modificaServiceButton.setDisable(true);
                    menuServiceButton.setDisable(true);

                    editNameButton.setDisable(true);
                    assegnaChef.setDisable(true);
                    scegliPersonale.setDisable(true);
                } else if (currentEvent.isOrganizer(currentUser)) {
                    addServiceButton.setDisable(false);
                    deleteService.setDisable(false);
                    modificaServiceButton.setDisable(false);
                    menuServiceButton.setDisable(true);

                    editNameButton.setDisable(false);
                    assegnaChef.setDisable(false);
                    scegliPersonale.setDisable(false);
                }
            }
        }

    }

    public void setEventManagementController(EventManagement eventManagement) {
        eventManagementController = eventManagement;
    }

    /* TOP RIGHT BUTTONS */
    @FXML
    public void editSchedaButtonPressed() {
        EventInfo currentEvent = CatERing.getInstance().getEventManager().getCurrentEvent();
        SchedaEvento currentScheda = CatERing.getInstance().getEventManager().getCurrentScheda();

        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("Modifica scheda evento");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setHeaderText("inserisci le informazioni");
        Label t1 = new Label("Cambia nome");
        TextField nome = new TextField(currentScheda.getName());
        Label t2 = new Label("Cambia data inizio");
        DatePicker data_in = new DatePicker(currentScheda.getDateStart().toLocalDate());
        Label t3 = new Label("Cambia data fine");
        DatePicker data_fine = new DatePicker(currentScheda.getDateEnd().toLocalDate());
        Label t4 = new Label("Cambia luogo");
        TextField luogo = new TextField(currentScheda.getLuogo());
        Label t5 = new Label("Cambia numero partecipanti");
        TextField part = new TextField(String.valueOf(currentScheda.getParticipants()));

        dialogPane.setContent(new VBox(8,t1,nome,t2,data_in,t3,data_fine,t4,luogo,t5,part));
        Platform.runLater(nome::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(nome.getText(), luogo.getText(), Integer.parseInt(part.getText()),
                        Date.valueOf(data_in.getValue()), Date.valueOf(data_fine.getValue()));
            }return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();

        if (optionalResult.isPresent()) {
            String name, place;
            Date dIn,dEnd;
            int pp;

            try {
                name=optionalResult.get().text;
                dIn = optionalResult.get().inDate;
                dEnd = optionalResult.get().endDate;
                place=optionalResult.get().luogo;
                pp=optionalResult.get().pp;

                //CatERing.getInstance().getEventManager().changeNameEvent(currentEvent,name);
                CatERing.getInstance().getEventManager().editScheda(currentScheda,name,place,dIn,dEnd,pp);
                this.nameLabel.setText(optionalResult.get().text+"\n\nData: "+optionalResult.get().inDate+" - "
                        +optionalResult.get().endDate+"\nLuogo: "+optionalResult.get().luogo+
                        "\nPartecipanti: "+optionalResult.get().pp);
            } catch (UseCaseLogicException ex) {
                ex.printStackTrace();
            }
        }
    }
    @FXML
    public void assegnaChefButtonPressed() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chefs-dialog.fxml"));
        try {
            BorderPane pane = loader.load();
            ChefDialog controller = loader.getController();

            Stage stage = new Stage();

            controller.setOwnStage(stage);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Scegli uno chef");
            stage.setScene(new Scene(pane));

            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @FXML
    public void chooseEmployee(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("employee-dialog.fxml"));
        try {
            BorderPane pane = loader.load();
            EmployeeDialog controller = loader.getController();

            Stage stage = new Stage();

            controller.setOwnStage(stage);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Scegli un cameriere o un cuoco:");
            stage.setScene(new Scene(pane));

            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /* BOTTOM BUTTONS FOR SERVICES*/
    @FXML
    public void addServicePressed(ActionEvent actionEvent) throws ParseException {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("Aggiungi un servizio");
        dialog.setHeaderText("Inserisci i dati del nuovo servizio…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField title = new TextField("Nome");
        DatePicker data = new DatePicker(LocalDate.now());
        TextField tempoIn = new TextField("00:00:00");
        TextField tempoFine = new TextField("00:00:00");
        TextField pp = new TextField("numero partecipanti");

        dialogPane.setContent(new VBox(8,title,data,tempoIn,tempoFine,pp));
        Platform.runLater(title::requestFocus);
        Platform.runLater(data::requestFocus);
        Platform.runLater(tempoIn::requestFocus);
        Platform.runLater(tempoFine::requestFocus);
        Platform.runLater(pp::requestFocus);

        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        Time in = new Time(formatter.parse(tempoIn.getText()).getTime());
        Time fin = new Time(formatter.parse(tempoFine.getText()).getTime());

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(title.getText(), Date.valueOf(data.getValue()), in ,
                    fin, pp.getText());
            }return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();

        if(optionalResult.isPresent()){
            int inputPp;
            String name;
            Time tIn,tEnd;
            java.sql.Date sDate;
            try {
                name=optionalResult.get().text;
                sDate = optionalResult.get().date;
                inputPp = Integer.parseInt(optionalResult.get().people);
                tIn=optionalResult.get().timeInizio;
                tEnd=optionalResult.get().timeFine;

                EventInfo currentEvent = CatERing.getInstance().getEventManager().getCurrentEvent();
                int event_id = currentEvent.getId();


                ServiceInfo s = CatERing.getInstance().getEventManager().createService(event_id,name,sDate,tIn,tEnd,inputPp);

                serviceListItems.add(s);
                CatERing.getInstance().getEventManager().chooseService(SchedaEvento.getSchedaFromId(s.getId()), s);
                //eventManagementController.showCurrentService();
                System.out.println(s + "   s= "+s.getId() +
                        "  service id " + CatERing.getInstance().getEventManager().getCurrentService().getId() +
                        "  evento " +CatERing.getInstance().getEventManager().getCurrentService().getId());



                //currentEvent.services.add(s);
                serviceList.refresh();
            } catch (UseCaseLogicException ex) {
                ex.printStackTrace();
            }
        }
    }
    @FXML
    public void deleteServicePressed(ActionEvent actionEvent) {
        try {
            ServiceInfo s = serviceList.getSelectionModel().getSelectedItem();
            CatERing.getInstance().getEventManager().deleteServizio(s);
            serviceListItems.remove(s);
        } catch (UseCaseLogicException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void modificaServicePressed(ActionEvent actionEvent) {
        SchedaEvento e = CatERing.getInstance().getEventManager().getCurrentScheda();
        try {
            ServiceInfo s = serviceList.getSelectionModel().getSelectedItem();
            CatERing.getInstance().getEventManager().chooseService(e,s);
            edit(s);
        } catch (ParseException | UseCaseLogicException ex) {
            ex.printStackTrace();
        }
    }
    private void edit(ServiceInfo s) throws ParseException {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("Modifica il servizio");
        dialog.setHeaderText("Modifica i dati del servizio…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField title = new TextField(s.getName());
        DatePicker data = new DatePicker(LocalDate.now());
        TextField tempoIn = new TextField(String.valueOf(s.getTimeStart()));
        TextField tempoFine = new TextField(String.valueOf(s.getTimeEnd()));
        TextField pp = new TextField(String.valueOf(s.getParticipants()));

        /* convert from DatePicker to Date
        LocalDate dataD=data.getValue();*/

        dialogPane.setContent(new VBox(8,title,data,tempoIn,tempoFine,pp));
        Platform.runLater(title::requestFocus);
        Platform.runLater(data::requestFocus);
        Platform.runLater(tempoIn::requestFocus);
        Platform.runLater(tempoFine::requestFocus);
        Platform.runLater(pp::requestFocus);

        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        Time in = new Time(formatter.parse(tempoIn.getText()).getTime());
        Time fin = new Time(formatter.parse(tempoFine.getText()).getTime());

        //System.out.println(tempoIn.getText());
        //System.out.println(tempoFine.getText());
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(title.getText(), java.sql.Date.valueOf(data.getValue()), java.sql.Time.valueOf(tempoIn.getText()),
                        java.sql.Time.valueOf(tempoFine.getText()), pp.getText());
            }return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();

        if(optionalResult.isPresent()){
            int inputPp;
            String name;
            Time tIn,tEnd;
            java.sql.Date sDate;
            try {
                name=optionalResult.get().text;
                s.setName(name);
                sDate = optionalResult.get().date;
                s.setDate(sDate);
                inputPp = Integer.parseInt(optionalResult.get().people);
                s.setParticipants(inputPp);
                tIn=optionalResult.get().timeInizio;
                s.setTimeStart(tIn);
                tEnd=optionalResult.get().timeFine;
                s.setTimeEnd(tEnd);

                //System.out.println(tIn.toString());
                //System.out.println(tEnd.toString());

                CatERing.getInstance().getEventManager().editService(s);

                serviceList.refresh();

            } catch (UseCaseLogicException ex) {
                ex.printStackTrace();
            }
        }
    }
    @FXML
    public void chooseMenuForService(ActionEvent actionEvent) {
        SchedaEvento e = CatERing.getInstance().getEventManager().getCurrentScheda();

        try {
            ServiceInfo s = serviceList.getSelectionModel().getSelectedItem();
            CatERing.getInstance().getEventManager().chooseService(e, s);
            menu(s);
        } catch (UseCaseLogicException ex) {
            ex.printStackTrace();
        }
    }
    private void menu(ServiceInfo s) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-dialog.fxml"));
        try {
            BorderPane pane = loader.load();
            MenuDialog controller = loader.getController();

            Stage stage = new Stage();

            controller.setOwnStage(stage);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Proponi un menù per il servizio selezionato");
            stage.setScene(new Scene(pane));

            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /* BOTTOM GENERAL BUTTONS */

    @FXML  /* termina evento e chiudi*/
    public void endButtonPressed() {
        SchedaEvento currentScheda = CatERing.getInstance().getEventManager().getCurrentScheda();
        try {
            CatERing.getInstance().getEventManager().endScheda(currentScheda);
            EventInfo currentEvent = CatERing.getInstance().getEventManager().getCurrentEvent();
            eventManagementController.showEventList(currentEvent);
        }catch(UseCaseLogicException e){
            e.printStackTrace();
        }

    }
    @FXML
    public void exitButtonPressed() {
        EventInfo currentEvent = CatERing.getInstance().getEventManager().getCurrentEvent();
        eventManagementController.showEventList(currentEvent);
    }

}