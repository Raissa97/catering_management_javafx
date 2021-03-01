package ui.task;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.SchedaEvento;
import businesslogic.task.SummarySheet;
import businesslogic.task.Task;
import businesslogic.task.TaskAssignment;
import businesslogic.task.Workshift;
import businesslogic.user.User;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkshiftDialog{

    // definition of row's table of workshifts
    public static class WorkRow {
        public Workshift workshift;
        public IntegerProperty wNumber;
        public StringProperty data;
        public StringProperty inizio;
        public StringProperty fine;
        public BooleanProperty inSede;
        public BooleanProperty disponibilita;
        public BooleanProperty assegnato;
        public StringProperty assegnatoName;
        public List<User> disponibiliName;

        public WorkRow() {
            workshift = new Workshift();
            wNumber = new SimpleIntegerProperty(0);
            data = new SimpleStringProperty("2019/12/31");
            inizio = new SimpleStringProperty("00:00:00");
            fine = new SimpleStringProperty("00:00:00");
            inSede = new SimpleBooleanProperty(false);
            disponibilita = new SimpleBooleanProperty(false);
            assegnato = new SimpleBooleanProperty(false);
            assegnatoName = new SimpleStringProperty("");
            disponibiliName = new ArrayList<>();
        }

        public Workshift getWorkshift() {
            return workshift;
        }

        public int getWNumber() {
            return wNumber.get();
        }

        public String getData() {
            return data.get();
        }

        public String getInizio() {
            return inizio.get();
        }

        public String getFine() {
            return fine.get();
        }

        public boolean isInSede() {
            return inSede.getValue();
        }

        public ObservableValue<Boolean> inSedeProperty() {
            return inSede;
        }

        public boolean isDisponibilita() {
            return disponibilita.get();
        }

        public BooleanProperty disponibilitaProperty() {
            return disponibilita;
        }

        public List<User> getDisponibiliName() {
            return disponibiliName;
        }

        public boolean isAssegnato() {
            return assegnato.get();
        }

        public BooleanProperty assegnatoProperty() {
            return assegnato;
        }

        public String getAssegnatoName() {
            return assegnatoName.get();
        }

        public void setWorkshift(Workshift workshift) {
            this.workshift = workshift;
        }

        public void setwNumber(int wNumber) {
            this.wNumber.set(wNumber);
        }

        public void setData(String data) {
            this.data.set(data);
        }

        public void setInizio(String inizio) {
            this.inizio.set(inizio);
        }

        public void setFine(String fine) {
            this.fine.set(fine);
        }

        public void setInSede(boolean inSede) {
            this.inSede.set(inSede);
        }

        public void setDisponibilita(boolean disponibilita) {
            this.disponibilita.set(disponibilita);
        }

        public void setDisponibiliName(List<User> disponibiliName) {
            this.disponibiliName = disponibiliName;
        }

        public void setAssegnato(boolean assegnato) {
            this.assegnato.set(assegnato);
        }

        public void setAssegnatoName(String assegnatoName) {
            this.assegnatoName.set(assegnatoName);
        }
    }

    public static class SingleTaskRow {
        public Task task;
        public StringProperty taskDescription;
        public BooleanProperty doneValue;
        public IntegerProperty timeValue;
        public IntegerProperty porzioni;

        public SingleTaskRow() {
            task = new Task();
            taskDescription = new SimpleStringProperty("");
            doneValue = new SimpleBooleanProperty(false);
            timeValue = new SimpleIntegerProperty(0);
        }

        public void setTaskDescription(String n) {
            taskDescription.setValue(n);
            task.description = taskDescription.get();
        }

        public String getTaskDescription() {
            return taskDescription.getValue();
        }

        public void setDoneValue(boolean b) {
            doneValue.setValue(b);
            task.setDone(b);
        }

        public boolean isDone() {
            return doneValue.getValue();
        }

        public BooleanProperty doneValueProperty() {
            return doneValue;
        }

        public void setTimeValue(int i) {
            timeValue.setValue(i);
            task.time = i;
        }

        public int getTimeValue() {
            return timeValue.getValue();
        }

        public int getPorzioni() {
            return porzioni.get();
        }

        public void setPorzioni(int porzioni) {
            this.porzioni.setValue(porzioni);
            task.portions = porzioni;
        }
    }

    Stage myStage;

    @FXML
    TableView<WorkRow> workShiftTable;
    ObservableList<WorkRow> wRows;
    @FXML
    TableView<SingleTaskRow> task; //single row table with task assigned
    ObservableList<SingleTaskRow> trows;

    @FXML
    Button visualizzaTaskTurno;
    @FXML
    Button salvaDispTurno;
    @FXML
    Button salva;
    @FXML
    Button salvaTaskDone;

    // tabella dei turni di lavoro modificabile solo dai cuochi/seervizio
    public void initialize(){
        User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();
        SummarySheet currentSummary = CatERing.getInstance().getTaskManager().getCurrentSummaryS();
        SchedaEvento currentScheda = CatERing.getInstance().getEventManager().getCurrentScheda();

        // Creo un table model a partire dalla mappa di turni per ogni foglio riepilogativo
        ObservableMap<Integer, Workshift> turniPerFoglioRiep = Workshift.getAllWorkshiftsForS(currentSummary);
        Map<Workshift,String> cooks_names = FXCollections.observableHashMap(); //per ogni w ho la lista di utenti assegnati

        for (int id : turniPerFoglioRiep.keySet()) {
            Workshift w = Workshift.getTurnoFromId(id);
            cooks_names.put(w,w.cooks.toString());
        }

        // mappa del personale disponibile in ogni turno
        Map<Integer, List<User>> user_disponibili;
        wRows = FXCollections.observableArrayList();

        workShiftTable.getColumns().clear(); //PER RISOLVERE PROBLEMA DOPPIE CELLE
        for (int id : turniPerFoglioRiep.keySet()) {
            Workshift w = Workshift.getTurnoFromId(id);
            // popolo le righe
            WorkRow row = new WorkRow();

            row.wNumber = new SimpleIntegerProperty(w.number);
            row.data = new SimpleStringProperty(String.valueOf(w.date));
            row.inizio = new SimpleStringProperty(String.valueOf(w.from));
            row.fine = new SimpleStringProperty(String.valueOf(w.to));
            row.inSede = new SimpleBooleanProperty(w.inSede);

            // setting workshifts'informations
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                java.util.Date date1 = format.parse(row.data.get());
                java.sql.Date sql = new java.sql.Date(date1.getTime());
                Time time1 = new Time(formatter.parse(row.inizio.get()).getTime());
                Time time2 = new Time(formatter.parse(row.fine.get()).getTime());
                w.from=time1;
                w.to = time2;
                w.date = sql;
            }catch (ParseException ex){
                ex.printStackTrace();
            }

            row.assegnato = new SimpleBooleanProperty(currentUser.isAssigned(w));
            row.assegnatoName = new SimpleStringProperty(cooks_names.get(w));

            user_disponibili = Workshift.getCuochiDisponibiliInTurno(id);
            //System.out.println(user_disponibili);
            if(user_disponibili.get(id)!=null){
                //il cuoco ha dato la sua disponibilità per quel turno
                row.disponibilita = new SimpleBooleanProperty(w.cooksDisponibili.contains(currentUser));
                //il cuoco che ha dato la disponililità
                row.disponibiliName =user_disponibili.get(id);
            }else{
                row.disponibilita = new SimpleBooleanProperty(false);
                row.disponibiliName = new ArrayList<>();
            }

            w.cooksDisponibili = row.disponibiliName;
            row.setWorkshift(w);

            wRows.add(row);
        }
        workShiftTable.setItems(wRows);

        TableColumn<WorkRow, String> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("data"));

        TableColumn<WorkRow, String> wNumberCol = new TableColumn<>("N° turno");
        wNumberCol.setCellValueFactory(new PropertyValueFactory<>("wNumber"));
        wNumberCol.setSortable(true);

        TableColumn<WorkRow, Boolean> sedeCol = new TableColumn<>("In sede?");
        sedeCol.setCellValueFactory(new PropertyValueFactory<>("inSede"));
        // lo chef dell'evento può modificare questa cella
        sedeCol.setCellFactory(c -> new CheckBoxTableCell<>(param -> wRows.get(param).inSedeProperty()));

        TableColumn<WorkRow,String> inTurnoCol = new TableColumn<>("Inizio");
        inTurnoCol.setCellValueFactory(new PropertyValueFactory<>("inizio"));
        inTurnoCol.setCellFactory(c -> new TextFieldTableCell<>());

        TableColumn<WorkRow, String> finTurnoCol = new TableColumn<>("Fine");
        finTurnoCol.setCellValueFactory(new PropertyValueFactory<>("fine"));
        finTurnoCol.setCellFactory(c -> new TextFieldTableCell<>());

        TableColumn<WorkRow, Boolean> assCol = new TableColumn<>("Assegnato");
        assCol.setCellValueFactory(new PropertyValueFactory<>("assegnato"));
        // un utente cuoco o servizio può essere assegnato in più turni diversi
        assCol.setCellFactory(c -> new CheckBoxTableCell<>(param -> wRows.get(param).assegnatoProperty()));
        assCol.setEditable(false);

        TableColumn<WorkRow, Boolean> assegnatoCol = new TableColumn<>("Assegnato a:");
        assegnatoCol.setCellValueFactory(new PropertyValueFactory<>("assegnatoName"));
        assegnatoCol.setCellFactory(c -> new TextFieldTableCell<>());

        TableColumn<WorkRow, Boolean> disponibilitaCol = new TableColumn<>("Disponibilità:");
        disponibilitaCol.setCellValueFactory(new PropertyValueFactory<>("disponibilita"));
        // un utente cuoco o servizio puo dare la disponibilità in piu turni diversi
        disponibilitaCol.setCellFactory(c -> new CheckBoxTableCell<>(param -> wRows.get(param).disponibilitaProperty()));

        TableColumn<WorkRow, String> cuochiDisponibiliCol = new TableColumn<>("Disponibilità:");
        cuochiDisponibiliCol.setCellValueFactory(new PropertyValueFactory<>("disponibiliName"));

        workShiftTable.getColumns().add(wNumberCol);
        workShiftTable.getColumns().add(dateCol);
        workShiftTable.getColumns().add(sedeCol);
        workShiftTable.getColumns().add(inTurnoCol);
        workShiftTable.getColumns().add(finTurnoCol);
        if(currentScheda.getChef()==currentUser){
            workShiftTable.getColumns().add(assegnatoCol);
            workShiftTable.getColumns().add(cuochiDisponibiliCol);
        } else{
            workShiftTable.getColumns().add(assCol);
            workShiftTable.getColumns().add(disponibilitaCol);
        }

        workShiftTable.setEditable(true);

        if(currentScheda.getChef()==currentUser) {
            System.out.println("chef of this scheda");
            salva.setVisible(true); //salva
            salvaTaskDone.setVisible(false);
            salvaDispTurno.setVisible(false);
            visualizzaTaskTurno.setVisible(false);
        } else {
            System.out.println("cook or service and not chef");
            salva.setVisible(false);
            salvaTaskDone.setVisible(true); //ok
            salvaDispTurno.setVisible(true); //salva
            visualizzaTaskTurno.setVisible(true); //visualizza
        }
    }

    public void visualizzaTaskButtonPressed() {
        User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();
        Workshift w = workShiftTable.getSelectionModel().getSelectedItem().getWorkshift();
        Map<TaskAssignment,User> assignmentsForW = TaskAssignment.getAssignmentsForW(w);
        //SchedaEvento currentScheda = CatERing.getInstance().getEventManager().getCurrentScheda();

        task.getColumns().clear();
        trows = FXCollections.observableArrayList();
        for(TaskAssignment ta: assignmentsForW.keySet()) {
            if(currentUser == assignmentsForW.get(ta)){
                SingleTaskRow tr = new SingleTaskRow();
                Task t = Task.getTaskFromAssignment(ta);

                tr.task = t;
                tr.taskDescription = new SimpleStringProperty(t.description);
                tr.doneValue = new SimpleBooleanProperty(t.isDone());
                tr.timeValue = new SimpleIntegerProperty(t.time);
                tr.porzioni = new SimpleIntegerProperty(t.portions);
                trows.add(tr);
            }
        }
        task.setItems(trows);

        TableColumn<SingleTaskRow, String> descriptionCol = new TableColumn<>("Descrizione");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("taskDescription"));

        TableColumn<SingleTaskRow, Boolean> doneCol = new TableColumn<>("Completato?");
        doneCol.setCellValueFactory(new PropertyValueFactory<>("doneValue"));
        /* il cuoco deve segnare se ha terminato la task o no */
        doneCol.setCellFactory(c -> new CheckBoxTableCell<>(param -> trows.get(param).doneValueProperty()));

        TableColumn<SingleTaskRow, Integer> timeCol = new TableColumn<>("Tempo");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeValue"));
        timeCol.setCellFactory(c -> new TextFieldTableCell<>(new IntegerStringConverter()));

        TableColumn<SingleTaskRow, Integer> porzCol = new TableColumn<>("# porzioni");
        porzCol.setCellValueFactory(new PropertyValueFactory<>("porzioni"));
        porzCol.setCellFactory(c -> new TextFieldTableCell<>(new IntegerStringConverter()));

        task.getColumns().add(descriptionCol);
        task.getColumns().add(doneCol);
        task.getColumns().add(timeCol);
        task.getColumns().add(porzCol);

        if(task.getSelectionModel().getSelectedItem()!=null) {
            Task currentTask = task.getSelectionModel().getSelectedItem().task;
            CatERing.getInstance().getTaskManager().setCurrentTask(currentTask);
            CatERing.getInstance().getTaskManager().chooseTask(currentTask);
            //System.out.println(currentTask);
        }

        task.setEditable(true);

    }

    // il cuoco o cameriere segna se ha svolto o no il compito assegnato
    public void saveTaskDone() {
        for (SingleTaskRow trow : trows) { //1 solo ciclo
            if (trow.isDone()) { // se è svolto [V]
                CatERing.getInstance().getTaskManager().setDone(trow.task);
                task.setEditable(false);
                salvaTaskDone.setDisable(true);
            }
        }
        this.myStage.close();
    }

    public void saveAvailabilityButtonPressed() {
        User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();

        for (WorkRow wRow : wRows) { //controlliamo ogni riga
            Workshift turno = wRow.getWorkshift();
            if (wRow.isDisponibilita()) { // se la disponibilità è true [V]
                if (!currentUser.isAvailable(turno)) { // se il cuoco non è disponibile nel turno
                    try {
                        CatERing.getInstance().getTaskManager().setAvailability(turno, currentUser);
                    } catch (UseCaseLogicException ex) {
                        ex.printStackTrace();
                    }
                }
            } else { // se la disponibilità non è selezionata [ ]
                if (currentUser.isAvailable(turno)) {// se il cuoco è disponibile nel turno
                    try {
                        CatERing.getInstance().getTaskManager().deleteAvailability(turno, currentUser);
                    } catch (UseCaseLogicException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        this.myStage.close();
    }

    public void saveInSedeButtonPressed() {
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        SchedaEvento e = CatERing.getInstance().getEventManager().getCurrentScheda();

        for (WorkRow wRow : wRows) { //controlliamo ogni riga
            Workshift turno = wRow.getWorkshift();
            try{
                CatERing.getInstance().getTaskManager().updateWorkshift(turno, e, u, wRow.isInSede());
            }catch (UseCaseLogicException ex){
                ex.printStackTrace();
            }
        }
        this.myStage.close();
    }

    public void setOwnStage(Stage stage) {
        myStage = stage;
    }
}
