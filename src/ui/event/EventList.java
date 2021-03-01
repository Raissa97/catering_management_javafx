package ui.event;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.SchedaEvento;
import businesslogic.task.Workshift;
import businesslogic.user.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ui.general.Results;

import java.sql.Date;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class EventList {

    public BorderPane eventListPane;
    public BorderPane schedeListPane;

    public Button nuovaSButton;
    public Button apriSButton;
    public Button eliminaSButton;
    private EventManagement eventManagementController;

    @FXML
    ListView<EventInfo> eventList;
    ObservableList<EventInfo> eventListItems;

    @FXML
    ListView<SchedaEvento> schedaList;
    ObservableList<SchedaEvento> schedaListItems;

    @FXML
    Button eliminaButton;
    @FXML
    Button nuovoButton;

    @FXML
    GridPane centralPane;
    Pane emptyPane;
    boolean paneVisible = true;

    @FXML
    public void fineButtonPressed() {
        eventManagementController.endEventManagement();
    }

    public void setParent(EventManagement eventManagement) {
        eventManagementController = eventManagement;
    }

    public void initialize() {
        User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();
        if (eventListItems == null) {
            if(currentUser.isOrganizer()) {
                eventListItems = CatERing.getInstance().getEventManager().getAllEventsForU(currentUser.getId());
                eliminaButton.setDisable(false);
                nuovoButton.setDisable(false);
            }
            if(currentUser.isChef()){
                eventListItems = CatERing.getInstance().getEventManager().getAllEvents();
            }

            eventList.setItems(eventListItems);
            eventList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            eventList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EventInfo>() {
                @Override
                public void changed(ObservableValue<? extends EventInfo> observableValue, EventInfo oldE, EventInfo newE) {
                    if (newE != null && newE != oldE) {
                        CatERing.getInstance().getEventManager().setCurrentEvent(eventList.getSelectionModel().getSelectedItem());
                        EventInfo currentEvent = CatERing.getInstance().getEventManager().getCurrentEvent();
                        boolean chef = false;

                        if(currentEvent.isOrganizer(currentUser)) {
                            eliminaSButton.setDisable(false);
                            apriSButton.setDisable(false);
                            nuovaSButton.setDisable(false);
                        }
                        for(SchedaEvento s: currentEvent.getSchede()){
                            if(s.isEventChef(currentUser)){
                                chef = true;
                            }
                        }
                        if(currentUser.isChef() && chef){
                            eliminaSButton.setVisible(false);
                            nuovaSButton.setVisible(false);
                            apriSButton.setDisable(false);
                        }else{
                            apriSButton.setVisible(true);
                            nuovaSButton.setVisible(true);
                            eliminaSButton.setVisible(true);
                        }
                        schedaListItems = currentEvent.getSchede();
                        schedaList.setItems(schedaListItems);
                    }

                    if (!paneVisible) {
                        centralPane.getChildren().remove(emptyPane);
                        centralPane.add(schedeListPane, 0, 1);
                        paneVisible = true;
                    }
                }
            });
        } else {
            eventList.refresh();
        }
    }

    @FXML
    public void nuovoEventoButtonPressed() {
        User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();

        ZoneId defaultZoneId = ZoneId.systemDefault();
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("Crea un nuovo evento");
        dialog.setHeaderText("Inserisci i dati del nuovo evento…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField title = new TextField("Name");
        DatePicker dataIn = new DatePicker(LocalDate.now());
        DatePicker dataFin = new DatePicker(LocalDate.now());
        Label people = new Label("numero atteso di partecipanti:");
        TextField pp = new TextField("0");
        Label l = new Label("luogo:");
        TextField luogo = new TextField("...");
        CheckBox ric = new CheckBox("L'evento è ricorrente?");

        // convert from instant to local date to sql date
        Instant i = dataIn.getValue().atStartOfDay(defaultZoneId).toInstant();
        ZonedDateTime zdt = ZonedDateTime.ofInstant(i , defaultZoneId);
        LocalDate localDate1 = zdt.toLocalDate();
        java.sql.Date dataI = java.sql.Date.valueOf(localDate1);

        Instant i2 = dataIn.getValue().atStartOfDay(defaultZoneId).toInstant();
        ZonedDateTime zdt2 = ZonedDateTime.ofInstant(i2 , defaultZoneId);
        LocalDate localDate2 = zdt2.toLocalDate();
        java.sql.Date dataF = java.sql.Date.valueOf(localDate2);

        dialogPane.setContent(new VBox(8, title, dataIn, dataFin, people, pp, l, luogo, ric));
        Platform.runLater(title::requestFocus);
        Platform.runLater(dataIn::requestFocus);
        Platform.runLater(dataFin::requestFocus);
        Platform.runLater(pp::requestFocus);
        Platform.runLater(ric::requestFocus);
        Platform.runLater(luogo::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(title.getText(),dataI, dataF,luogo.getText(),Integer.parseInt(pp.getText()),ric.selectedProperty().get());
            }return null;
        });

        Optional<Results> optionalResult = dialog.showAndWait();

        if(optionalResult.isPresent()){
            int inputPp;
            String name, posto;
            java.sql.Date dIn,dEnd;
            boolean isRic;

            try {
                inputPp=optionalResult.get().pp;
                name=optionalResult.get().text;
                posto = optionalResult.get().luogo;
                dIn= optionalResult.get().inDate;
                dEnd=optionalResult.get().endDate;
                isRic=optionalResult.get().isRic;

                EventInfo e = new EventInfo(name,isRic,currentUser);
                CatERing.getInstance().getEventManager().createEvent(name, isRic, currentUser);
                
                CatERing.getInstance().getEventManager().setCurrentEvent(e);
                System.out.println(e.getId());
                System.out.println(CatERing.getInstance().getEventManager().getCurrentEvent());

                if(isRic) {
                    Dialog<Results> dialog2 = new Dialog<>();
                    dialog2.setTitle("evento ricorrente");
                    dialog2.setHeaderText("Inserisci i dati dell'evento ricorrente");
                    DialogPane dialogPane2 = dialog.getDialogPane();
                    //dialogPane2.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                    Label text2 = new Label("ogni quanti giorni si ripete l'evento?");
                    TextField ogniQuanto = new TextField("0");
                    Label text3 = new Label("Per quante volte?");
                    TextField perQuanto = new TextField("0");
                    Label text = new Label("oppure... qual è la data dell'ultima ripetizione dell'evento?");
                    DatePicker fineRic = new DatePicker(LocalDate.now());

                    //converto fineRic in sql.Date
                    Instant i3 = fineRic.getValue().atStartOfDay(defaultZoneId).toInstant();
                    ZonedDateTime zdt3 = ZonedDateTime.ofInstant(i3, defaultZoneId);
                    LocalDate localDate3 = zdt3.toLocalDate();
                    java.sql.Date fineRip = java.sql.Date.valueOf(localDate3);

                    dialogPane2.setContent(new VBox(8, text2, ogniQuanto, text3, perQuanto, text, fineRic));
                    Platform.runLater(ogniQuanto::requestFocus);
                    Platform.runLater(perQuanto::requestFocus);
                    Platform.runLater(text::requestFocus);
                    Platform.runLater(fineRic::requestFocus);

                    dialog.setResultConverter((ButtonType button) -> {
                        if (button == ButtonType.OK) {
                            return new Results(Integer.parseInt(perQuanto.getText()), Integer.parseInt(ogniQuanto.getText()), fineRip);
                        }
                        return null;
                    });

                    Optional<Results> optionalResult2 = dialog.showAndWait();
                    if (optionalResult2.isPresent()) {

                        int ogniQ,perQ;
                        java.sql.Date dataFineRip;

                        try{
                            perQ = optionalResult2.get().perQuanto;
                            ogniQ = optionalResult2.get().ogniQuanto;
                            dataFineRip = optionalResult2.get().dataFineRip;

                            if(ogniQ != 0 && perQ>=1){
                                Date in = dIn;
                                Date fin = dEnd;
                                int j=0;
                                while(j<perQ){
                                    System.out.println("in while = "+e.getId());
                                    SchedaEvento sc = CatERing.getInstance().getEventManager().createSchedaEvento(e,in,fin,inputPp,posto);
                                    e.schedeEvento.add(sc);
                                    //System.out.println("data in "+ in +" data fin "+ fin +"  j = "+j);
                                    in = Date.valueOf(in.toLocalDate().plusDays(ogniQ));//+ogniQ;
                                    fin = Date.valueOf(in.toLocalDate().plusDays(ogniQ));
                                    j++;
                                }

                            }else if(!dataFineRip.equals(Date.valueOf(LocalDate.now()))){
                                Date d=dIn;
                                Date dF=dEnd;
                                while(d.compareTo(dataFineRip)<0){
                                    SchedaEvento sc = CatERing.getInstance().getEventManager().createSchedaEvento(e,d,dF,inputPp,posto);
                                    e.schedeEvento.add(sc);
                                    d = Date.valueOf(d.toLocalDate().plusDays(ogniQ));
                                    dF = Date.valueOf(dF.toLocalDate().plusDays(ogniQ));
                                }
                            }

                            eventListItems.add(e);
                            //CatERing.getInstance().getEventManager().chooseSchedaEvento(sc);
                            //eventManagementController.showCurrentEvent();
                        }catch (UseCaseLogicException ex){
                            ex.printStackTrace();
                        }

                    }
                }else{
                    System.out.println(e.getId());
                    System.out.println(CatERing.getInstance().getEventManager().getCurrentEvent());

                    SchedaEvento sc = CatERing.getInstance().getEventManager().createSchedaEvento(e,dIn,dEnd,inputPp,posto);
                    CatERing.getInstance().getEventManager().chooseSchedaEvento(sc);
                    eventManagementController.showCurrentScheda();
                }
            } catch (UseCaseLogicException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void eliminaEventoButtonPressed() {
        try {
            EventInfo e = eventList.getSelectionModel().getSelectedItem();
            CatERing.getInstance().getEventManager().deleteEvent(e);
            eventListItems.remove(e);
        } catch (UseCaseLogicException e) {
            e.printStackTrace();
        }
    }

    public void selectEvent(EventInfo e) {
        if (e != null) this.eventList.getSelectionModel().select(e);
        else this.eventList.getSelectionModel().select(-1);
    }

    public void setManagementController(EventManagement eventManagement) {
        eventManagementController = eventManagement;
    }

    public void nuovaSchedaButtonPressed(ActionEvent actionEvent) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        EventInfo currentEvent = CatERing.getInstance().getEventManager().getCurrentEvent();
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("Nuova scheda evento");
        dialog.setHeaderText("Inserisci i dati della nuova scheda per l'evento "+currentEvent.getName());
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        DatePicker dataIn = new DatePicker(LocalDate.now());
        DatePicker dataFin = new DatePicker(LocalDate.now());
        Label people = new Label("numero atteso di partecipanti:");
        TextField pp = new TextField("0");

        Label l = new Label("luogo dell'evento:");
        TextField luogo = new TextField("luogo");

        // convert from instant to local date to sql date
        Instant i = dataIn.getValue().atStartOfDay(defaultZoneId).toInstant();
        ZonedDateTime zdt = ZonedDateTime.ofInstant(i , defaultZoneId);
        LocalDate localDate1 = zdt.toLocalDate();
        java.sql.Date dataI = java.sql.Date.valueOf(localDate1);

        Instant i2 = dataIn.getValue().atStartOfDay(defaultZoneId).toInstant();
        ZonedDateTime zdt2 = ZonedDateTime.ofInstant(i2 , defaultZoneId);
        LocalDate localDate2 = zdt2.toLocalDate();
        java.sql.Date dataF = java.sql.Date.valueOf(localDate2);

        dialogPane.setContent(new VBox(8, dataIn, dataFin, people, pp, l, luogo));
        Platform.runLater(dataIn::requestFocus);
        Platform.runLater(dataFin::requestFocus);
        Platform.runLater(pp::requestFocus);
        Platform.runLater(luogo::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(dataI, dataF,Integer.parseInt(pp.getText()),luogo.getText());
            }return null;
        });

        Optional<Results> optionalResult = dialog.showAndWait();


        if(optionalResult.isPresent()) {
            int inputPp;
            java.sql.Date dIn, dEnd;
            String place;

            try {
                inputPp = optionalResult.get().pp;
                dIn = optionalResult.get().inDate;
                dEnd = optionalResult.get().endDate;
                place = optionalResult.get().luogo;

                SchedaEvento sc = CatERing.getInstance().getEventManager().createSchedaEvento(currentEvent, dIn, dEnd, inputPp,place);
                currentEvent.schedeEvento.add(sc);

                /* creo la tabella dei turni senza assegnamenti per il nuovo evento */
                Time[] times_in = new Time[]{Time.valueOf("9:00:00"), Time.valueOf("10:00:00"), Time.valueOf("11:00:00"),
                        Time.valueOf("12:00:00"), Time.valueOf("13:00:00"), Time.valueOf("14:00:00"), Time.valueOf("15:00:00"),
                        Time.valueOf("16:00:00"), Time.valueOf("17:00:00"), Time.valueOf("18:00:00"), Time.valueOf("19:00:00"),
                        Time.valueOf("20:00:00"), Time.valueOf("21:00:00"), Time.valueOf("22:00:00")};
                Time[] times_end = new Time[]{Time.valueOf("10:00:00"), Time.valueOf("11:00:00"),
                        Time.valueOf("12:00:00"), Time.valueOf("13:00:00"), Time.valueOf("14:00:00"), Time.valueOf("15:00:00"),
                        Time.valueOf("16:00:00"), Time.valueOf("17:00:00"), Time.valueOf("18:00:00"), Time.valueOf("19:00:00"),
                        Time.valueOf("20:00:00"), Time.valueOf("21:00:00"), Time.valueOf("22:00:00"), Time.valueOf("23:00:00")};

                for(int j=0; j< times_end.length;j++) {
                    Workshift w = new Workshift();
                    w.date=sc.getDateStart();
                    w.from=times_in[j];
                    w.to=times_end[j];
                    w.cooksDisponibili=new ArrayList<>();
                    w.cooks = new ArrayList<>();
                    w.assignments = new ArrayList<>();
                    Workshift.insertNewWorkshifts(w,j,sc.getId());
                    sc.turni.add(w);
                }
            } catch (UseCaseLogicException ex) {
                ex.printStackTrace();
            }
        }


    }

    @FXML
    public void apriSchedaButtonPressed() {
        try {
            SchedaEvento sc = schedaList.getSelectionModel().getSelectedItem();
            CatERing.getInstance().getEventManager().chooseSchedaEvento(sc);
            eventManagementController.showCurrentScheda();
        } catch (UseCaseLogicException e) {
            e.printStackTrace();
        }
    }

    public void eliminaSchedaButtonPressed(ActionEvent actionEvent) {

    }
}
