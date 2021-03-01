package ui.task;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.SchedaEvento;
import businesslogic.event.ServiceInfo;
import businesslogic.recipe.Recipe;
import businesslogic.task.SummarySheet;
import businesslogic.task.Task;
import businesslogic.task.TaskAssignment;
import businesslogic.task.Workshift;
import businesslogic.user.User;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import ui.Main;
import ui.general.Results;

import java.io.IOException;
import java.util.*;

public class TaskManagement {

    // definition of row's table of tasks
    public static class TaskRow {
        public Task task;
        public TaskAssignment taskAssignment;
        public StringProperty taskDescription;
        public BooleanProperty doneValue;
        public IntegerProperty timeValue;
        public IntegerProperty portionValue;
        public StringProperty assignmentName;
        public IntegerProperty workshift;
        public IntegerProperty position;

        public TaskRow() {
            task = new Task();
            taskDescription = new SimpleStringProperty("");
            doneValue = new SimpleBooleanProperty(false);
            timeValue = new SimpleIntegerProperty(0);
            portionValue = new SimpleIntegerProperty(0);
            assignmentName = new SimpleStringProperty("");
            workshift = new SimpleIntegerProperty(0);
            taskAssignment = null; //= new TaskAssignment(task.id,cook.getId(),workshift.get());
            position = new SimpleIntegerProperty(task.pos);
        }

        public TaskRow(Task t) {
            task = t;
            taskDescription = new SimpleStringProperty(t.description);
            doneValue = new SimpleBooleanProperty(t.isDone());
            timeValue = new SimpleIntegerProperty(t.time);
            portionValue = new SimpleIntegerProperty(t.portions);
            taskAssignment = null; //= new TaskAssignment(task.id,cook.getId(),workshift.get());
            assignmentName = new SimpleStringProperty();
            workshift = new SimpleIntegerProperty(0);

            position = new SimpleIntegerProperty(task.pos);
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

        public String getDoneValue() {
            return doneValue.getValue() ? "Yes" : "No";
        }

        public void setTimeValue(int i) {
            timeValue.setValue(i);
            task.time = i;
        }

        public int getTimeValue() {
            return timeValue.getValue();
        }

        public int getPortionValue() {
            return portionValue.get();
        }

        public void setPortionValue(int i) {
            portionValue.setValue(i);
            task.portions = i;
        }

        public void setAssignmentName(String n) {
            assignmentName.setValue(n);
            taskAssignment.cook = User.getUserByName(n).getId();
        }

        public String getAssignmentName() {
            return assignmentName.getValue();
        }

        public void setWorkshift(int workshift) {
            this.workshift.set(workshift);
            taskAssignment.workshift = workshift;
        }

        public int getWorkshift() {
            return workshift.get();
        }

        public Task getTask(){
            return task;
        }

        public TaskAssignment getTaskAssignment(){
            return taskAssignment;
        }

        public int getPosition() {
            return position.get();
        }

        public void setPosition(int position) {
            this.position.set(position);
        }
    }

    @FXML
    BorderPane taskListPane;
    @FXML
    BorderPane listPane;
    @FXML
    Label userLabel;

    @FXML
    ListView<EventInfo> eventList; //table of events
    ObservableList<EventInfo> eventListItems;
    @FXML
    ListView<SummarySheet> summaryList; //table of summarySheets
    ObservableList<SummarySheet> summarySheetItems;
    @FXML
    TableView<TaskRow> summarySheetTable; //table of tasks
    ObservableList<TaskRow> rows;

    @FXML
    Button workshiftButton;
    @FXML
    Button aggiungiTask;
    @FXML
    Button deleteTask;
    @FXML
    Button upButton;
    @FXML
    Button downButton;

    @FXML
    Button editButton;
    @FXML
    Button restore;

    @FXML
    BorderPane eventListPane;
    @FXML
    EventList eventListPaneController;

    @FXML
    GridPane centralPane;
    Pane emptyPane;
    boolean paneVisible = true;

    Main mainPaneController;

    Map<Task,TaskAssignment> tasksTa = FXCollections.observableHashMap();
    int lastPos=0;

    public void initialize(){
        User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();
        if(currentUser!=null) {
            userLabel.setText(currentUser.getUserName());
        }

        workshiftButton.setDisable(true);

        if (eventListItems == null) {
            //popolo la lista di fogli riepilogativi
            if(currentUser != null) {
                List<EventInfo> events = null;

                if(!(currentUser.isCook() || currentUser.isService()) && currentUser.isChef() ){
                    //se l'utente è chef ma non anche cameriere o servizio
                    events = CatERing.getInstance().getEventManager().getAllEvents();
                }else if((currentUser.isCook() || currentUser.isService()) && currentUser.isChef()){
                    //se l'utente sia cuoco o cameriere che chef
                    events = CatERing.getInstance().getEventManager().getAllEvents();
                }else if((currentUser.isCook() || currentUser.isService()) && !currentUser.isChef()) {
                    //se l'utente è cameriere o cuoco ma non chef
                    events = CatERing.getInstance().getEventManager().getAllEvents();
                }
                if(events!=null) {
                    Map<Integer,List<SchedaEvento>> sheets = FXCollections.observableHashMap();
                    Map<Integer,SchedaEvento> summaries = SchedaEvento.getLoadedSchede();
                    eventListItems=FXCollections.observableArrayList();
                    for(EventInfo e: events) {
                        int ev_id = e.getId();
                        if(summaries.containsKey(ev_id)){
                            eventListItems.add(e);
                            sheets.put(e.getId(), e.getSchede());
                        }
                    }

                    eventList.setItems(eventListItems);
                    eventList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                    eventList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EventInfo>() {
                        @Override
                        public void changed(ObservableValue<? extends EventInfo> observableValue, EventInfo oldE, EventInfo newE) {
                            if (newE != null && newE != oldE) {
                                int pos = eventList.getSelectionModel().getSelectedIndex();
                                eventList.getSelectionModel().isSelected(pos);
                                CatERing.getInstance().getEventManager().setCurrentEvent(eventList.getSelectionModel().getSelectedItem());
                                EventInfo currentEvent = CatERing.getInstance().getEventManager().getCurrentEvent();

                                ObservableList<SchedaEvento> schede=FXCollections.observableArrayList();
                                if(!(currentUser.isCook() || currentUser.isService()) && currentUser.isChef() ){
                                    //se l'utente è chef ma non anche cameriere o servizio
                                    schede.addAll(currentEvent.getSchedeOfU(currentUser));
                                }else if((currentUser.isCook() || currentUser.isService()) && currentUser.isChef()){
                                    //se l'utente sia cuoco o cameriere che chef
                                    schede.addAll(currentEvent.getSchede());
                                }else if((currentUser.isCook() || currentUser.isService()) && !currentUser.isChef()) {
                                    //se l'utente è cameriere o cuoco ma non chef
                                    schede.addAll(currentEvent.getSchede());
                                }


                                summarySheetItems=FXCollections.observableArrayList();
                                for (SchedaEvento sc : schede) {
                                    summarySheetItems.add(SummarySheet.getSummaryFromScheda(sc));
                                }
                                summaryList.setItems(summarySheetItems);

                                summaryList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                                summaryList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SummarySheet>() {
                                    @Override
                                    public void changed(ObservableValue<? extends SummarySheet> observableValue, SummarySheet oldS, SummarySheet newS) {
                                        if (newS != null && newS != oldS) {
                                            int pos = summaryList.getSelectionModel().getSelectedIndex();
                                            summaryList.getSelectionModel().isSelected(pos);

                                            //1_CHOOSE SUMMARY
                                            CatERing.getInstance().getTaskManager().setCurrentSummary(summaryList.getSelectionModel().getSelectedItem());

                                            ObservableMap<Integer, Workshift> turniPerFoglioRiep = Workshift.getAllWorkshiftsForS(CatERing.getInstance().getTaskManager().getCurrentSummaryS());
                                            List<Recipe> rec = FXCollections.observableArrayList();// lista di ricette dei menu approvati e task dell'evento selezionato

                                            //1a_OPEN SUMMARY
                                            SchedaEvento s = SchedaEvento.getSchedaFromId(CatERing.getInstance().getTaskManager().getCurrentSummaryS().getSchedaEvento());

                                            CatERing.getInstance().getEventManager().setCurrentSchedaEvento(s);

                                            workshiftButton.setDisable(false);

                                            List<Integer> menu_ids = FXCollections.observableArrayList();
                                            for (ServiceInfo serviceofE : ServiceInfo.loadServiceInfoForSchedaEvento(s.getId())) {
                                                menu_ids.add(serviceofE.getApprovedMenuId());
                                            }
                                            List<Recipe> r;
                                            if (!menu_ids.isEmpty()) {
                                                s.setApprovedMenus(menu_ids);
                                                //System.out.println("scheda evento = " + s + "  s.approved_menu = " + s.approved_menu);
                                                for (int i : menu_ids) {
                                                    r = Recipe.loadRecipesForMenu(i);
                                                    rec.addAll(r);
                                                }
                                            }

                                            aggiungiTask.setDisable(true);
                                            upButton.setDisable(true);
                                            deleteTask.setDisable(true);
                                            downButton.setDisable(true);
                                            editButton.setDisable(true);
                                            restore.setDisable(true);

                                            // carico la tabella di task
                                            summarySheetTable.getColumns().clear();
                                            tabelModel(rec);
                                        }
                                        if(!paneVisible){
                                            centralPane.getChildren().remove(emptyPane);
                                            centralPane.add(taskListPane, 1, 0);
                                            paneVisible = true;
                                        }
                                    }
                                });
                            }
                        }
                    });

                }
            }
        }else{
            eventList.refresh();
        }
    }

    // tabella delle tasks per l'evento selezionato
    public void tabelModel(List<Recipe> recipes){
        User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();

        if(CatERing.getInstance().getEventManager().getCurrentScheda().isEventChef(currentUser)){
            //SchedaEvento currentScheda = CatERing.getInstance().getEventManager().getCurrentScheda();
            SummarySheet currentSummary = CatERing.getInstance().getTaskManager().getCurrentSummaryS();

            upButton.setDisable(false);
            aggiungiTask.setDisable(false);
            deleteTask.setDisable(false);
            downButton.setDisable(false);
            editButton.setDisable(false);
            restore.setDisable(false);

            currentSummary.setTasks(CatERing.getInstance().getTaskManager().getAllTaskForSummary(currentSummary.getId()));
            int x = 1;
            for (Recipe r : recipes) {
                if (CatERing.getInstance().getTaskManager().getTaskFromRecipe(currentSummary.getId(), r.getId()).get(r) == null) {
                    Task task_for_recipe = new Task(currentSummary, r.getName(), true, r, 0, 0, x);

                    try {
                        CatERing.getInstance().getTaskManager().newTask(currentSummary,task_for_recipe);
                    } catch (UseCaseLogicException e) {
                        e.printStackTrace();
                    }
                }
                x++;
            }

            summarySheetTable.getColumns().clear(); //PER RISOLVERE PROBLEMA DOPPIE CELLE

            rows = FXCollections.observableArrayList();

            int task_pos = 1;

            for (Task t : currentSummary.getTasks()) {
                tasksTa.putAll(TaskAssignment.getAssignmentsFromT(t));

                TaskRow row = new TaskRow();
                row.task = t;
                row.taskDescription = new SimpleStringProperty(t.description);
                row.doneValue = new SimpleBooleanProperty(t.isDone());
                row.timeValue = new SimpleIntegerProperty(t.time);
                row.portionValue = new SimpleIntegerProperty(t.portions);

                row.position = new SimpleIntegerProperty(t.getPos());

                row.taskAssignment = tasksTa.get(t);
                row.workshift = new SimpleIntegerProperty(tasksTa.get(t).workshift);
                for(User u: TaskAssignment.getAssignmentsForW(tasksTa.get(t).workshift)) {
                    //System.out.println(u.getId() + "  turno: "+tasksTa.get(t).workshift);
                    row.assignmentName = new SimpleStringProperty(u.getUserName());
                }
                rows.add(row);

                task_pos++;
            }
            //salvo futura posizione della nuova task
            lastPos = task_pos;
            //System.out.println("task Pos = "+lastPos);

            summarySheetTable.setItems(rows);

            TableColumn<TaskRow, Integer> positionCol = new TableColumn<>("#");
            positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));

            TableColumn<TaskRow, String> descriptionCol = new TableColumn<>("Descrizione");
            descriptionCol.setCellValueFactory(new PropertyValueFactory<>("taskDescription"));

            TableColumn<TaskRow, Boolean> doneCol = new TableColumn<>("Completato?");
            doneCol.setCellValueFactory(new PropertyValueFactory<>("doneValue"));

            TableColumn<TaskRow, Integer> timeCol = new TableColumn<>("Tempo");
            timeCol.setCellValueFactory(new PropertyValueFactory<>("timeValue"));
            timeCol.setCellFactory(c -> new TextFieldTableCell<>(new IntegerStringConverter()));

            TableColumn<TaskRow, Integer> porzCol = new TableColumn<>("# porzioni");
            porzCol.setCellValueFactory(new PropertyValueFactory<>("portionValue"));
            porzCol.setCellFactory(c -> new TextFieldTableCell<>(new IntegerStringConverter()));

            TableColumn<TaskRow, String> assignmentCol = new TableColumn<>("Assegnato a");
            assignmentCol.setCellValueFactory(new PropertyValueFactory<>("assignmentName"));
            assignmentCol.setCellFactory(c -> new TextFieldTableCell<>());

            TableColumn<TaskRow, Integer> turnoCol = new TableColumn<>("Turno");
            turnoCol.setCellValueFactory(new PropertyValueFactory<>("workshift"));
            turnoCol.setCellFactory(c -> new TextFieldTableCell<>(new IntegerStringConverter()));

            summarySheetTable.getColumns().add(positionCol);
            summarySheetTable.getColumns().add(descriptionCol);
            summarySheetTable.getColumns().add(doneCol);
            summarySheetTable.getColumns().add(timeCol);
            summarySheetTable.getColumns().add(porzCol);
            summarySheetTable.getColumns().add(assignmentCol);
            summarySheetTable.getColumns().add(turnoCol);

            positionCol.setSortType(TableColumn.SortType.ASCENDING);
            summarySheetTable.getSortOrder().add(positionCol);
            summarySheetTable.sort();

            if (summarySheetTable.getSelectionModel().getSelectedItem() != null) {
                Task currentTask = summarySheetTable.getSelectionModel().getSelectedItem().task;
                CatERing.getInstance().getTaskManager().setCurrentTask(currentTask);
                CatERing.getInstance().getTaskManager().chooseTask(currentTask);
                //System.out.println(currentTask);
            }
            summarySheetTable.setEditable(true);
            summarySheetTable.refresh();
        }
        summarySheetTable.refresh();
    }

    //2_new
    @FXML
    public void nuovaTaskButtonPressed() {
        int pos= lastPos;
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("Aggiungi un compito");
        dialog.setHeaderText("Inserisci i dati del nuovo compito…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField descrizione = new TextField("descrizione");

        dialogPane.setContent(new VBox(3,descrizione));
        Platform.runLater(descrizione::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(descrizione.getText());
            }else return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();

        if(optionalResult.isPresent()) {
            SummarySheet currentSummary = CatERing.getInstance().getTaskManager().getCurrentSummaryS();

            String description;

            description = optionalResult.get().descTask;
            Task t = new Task(currentSummary, description, true,0, pos);

            try {
                Task task = CatERing.getInstance().getTaskManager().newTask(currentSummary,t);
                CatERing.getInstance().getTaskManager().setPosition(task,pos);
                TaskRow x = new TaskRow(t);
                rows.add(x);
            } catch (UseCaseLogicException e) {
                e.printStackTrace();
            }
            lastPos=pos+1;
        }
        summarySheetTable.sort();
        summarySheetTable.refresh();
    }

    @FXML
    public void modificaButtonPressed() {
        List<User> cooks = CatERing.getInstance().getUserManager().getAllCookOrService();

        ButtonType elimina = new ButtonType("Elimina assegnazione");

        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("Modifica task");
        dialog.setHeaderText("Modifica i dati del compito…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL,elimina);

        Task task = summarySheetTable.getSelectionModel().getSelectedItem().getTask();
        TaskAssignment ta = summarySheetTable.getSelectionModel().getSelectedItem().getTaskAssignment();

        TextField desc = new TextField(summarySheetTable.getSelectionModel().getSelectedItem().taskDescription.get());
        Label d = new Label("Descrizione:");
        TextField time = new TextField(String.valueOf(summarySheetTable.getSelectionModel().getSelectedItem().timeValue.get()));
        Label ti = new Label("Tempo:");
        TextField portions = new TextField(String.valueOf(summarySheetTable.getSelectionModel().getSelectedItem().portionValue.get()));
        Label p = new Label("# porzioni:");
        String ta_cook = summarySheetTable.getSelectionModel().getSelectedItem().assignmentName.get();
        TextField cookOrService = new TextField(ta_cook);
        Label ass = new Label("Assegna task a:");
        TextField workShiftId = new TextField(String.valueOf(summarySheetTable.getSelectionModel().getSelectedItem().workshift.get()));
        Label w = new Label("...nel turno:");
        dialogPane.setContent(new VBox(8,d,desc,ti,time,p,portions,ass,cookOrService,w,workShiftId));

        Platform.runLater(desc::requestFocus);
        Platform.runLater(time::requestFocus);
        Platform.runLater(portions::requestFocus);
        Platform.runLater(cookOrService::requestFocus);
        Platform.runLater(workShiftId::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(task, ta, desc.getText(),time.getText(),portions.getText(),cookOrService.getText(),Integer.parseInt(workShiftId.getText()));
            }else if(button == elimina){
                try {
                    TaskRow tr = summarySheetTable.getSelectionModel().getSelectedItem();
                    rows.remove(tr);
                    CatERing.getInstance().getTaskManager().deleteAssignment(tr.taskAssignment);
                    rows.add(tr);
                    summarySheetTable.sort();
                    summarySheetTable.refresh();
                }catch (UseCaseLogicException e){
                    e.printStackTrace();
                }
                return null;
            }else return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();

        if(optionalResult.isPresent()) {
            Task t;
            int inputTime, porz;
            String description, cOrS;
            int turno;

            t = optionalResult.get().task;
            //taskAss = optionalResult.get().taskAssignment;
            inputTime = Integer.parseInt(optionalResult.get().timeTask);
            porz = Integer.parseInt(optionalResult.get().porzTask);
            description = optionalResult.get().descTask;
            cOrS = optionalResult.get().cookOrService;
            turno = optionalResult.get().workshift_id;

            TaskRow tr = summarySheetTable.getSelectionModel().getSelectedItem();
            if(description!=null) {
                CatERing.getInstance().getTaskManager().editTask(t, description);
                rows.remove(tr);
                tr.setTaskDescription(description);
                rows.add(tr);
                summarySheetTable.sort();
                summarySheetTable.refresh();
            }
            if(inputTime!=0 || porz!=0){
                CatERing.getInstance().getTaskManager().editProprTask(t,inputTime,porz);
                rows.remove(tr);
                tr.setPortionValue(porz);
                tr.setTimeValue(inputTime);
                rows.add(tr);
                summarySheetTable.sort();
                summarySheetTable.refresh();
            }

            //se non c'è nessun assegnamento
            if(tr.taskAssignment.cook==0 && tr.taskAssignment.workshift==0){
                for (User u : cooks) {
                    if (u.getUserName().equals(cOrS)) {
                        System.out.println(Workshift.getCuochiDisponibiliInTurno(turno).keySet());
                        System.out.println(u.getUserName() + "  is available in  "+ turno + " (id) "+ Workshift.getCuochiDisponibiliInTurno(turno).get(turno));
                        try {
                            TaskAssignment taskAssignment = new TaskAssignment(t.id, turno, u.getId());
                            CatERing.getInstance().getTaskManager().assignTask(taskAssignment);
                            rows.remove(tr);
                            //tasksTa.get(tr.taskAssignment.cook = u.getId();
                            //tasksTa.get(tr.taskAssignment.workshift = turno;
                            tr.taskAssignment.cook = u.getId();
                            tr.taskAssignment.workshift = turno;
                            tasksTa.put(t,tr.taskAssignment);
                            rows.add(tr);
                            summarySheetTable.sort();
                            summarySheetTable.refresh();
                        } catch (UseCaseLogicException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            else {//se ho cambiato l'assegnamento precedente
                if(!cOrS.equals(User.getUserById(tr.taskAssignment.cook).getUserName()) && turno!=tr.workshift.get()){
                    Workshift.addAvailableWorker(Workshift.getTurnoFromId(tr.workshift.get()),User.getUserById(tr.taskAssignment.cook));
                    for (User u : cooks) {
                        if (u.getUserName().equals(cOrS)) {
                            try {
                                TaskAssignment taskAssignment = new TaskAssignment(t.id, turno, u.getId());
                                CatERing.getInstance().getTaskManager().assignTask(taskAssignment);
                                rows.remove(tr);
                                tasksTa.get(tr.getTask()).cook = u.getId();
                                tasksTa.get(tr.getTask()).workshift = turno;
                                Workshift.deleteAvailableWorker(Workshift.getTurnoFromId(tr.taskAssignment.workshift),User.getUserById(tr.taskAssignment.cook));
                                rows.add(tr);
                                summarySheetTable.sort();
                                summarySheetTable.refresh();
                            } catch (UseCaseLogicException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    @FXML
    public void deleteButtonPressed() {
        Task task = summarySheetTable.getSelectionModel().getSelectedItem().getTask();
        TaskRow tr = summarySheetTable.getSelectionModel().getSelectedItem();
        try {
            CatERing.getInstance().getTaskManager().deleteTask(task);

            Iterator<TaskRow> iterator = rows.iterator();
            while (iterator.hasNext()) { // = per ogni riga
                TaskRow taskRow = iterator.next();
                if (task.getId() == taskRow.task.getId()) { //la task della riga tr è da eliminare
                    iterator.remove(); //rimuoviamo la riga selezionata
                    rows.remove(taskRow);
                    while(iterator.hasNext()){ //guardiamo le righe successive
                        TaskRow taskRowNext = iterator.next();
                        int pos = taskRowNext.position.get()-1; //decrementiamo di 1 il valore della posizione

                        taskRowNext.position = new SimpleIntegerProperty(CatERing.getInstance().getTaskManager().setPosition(taskRowNext.task, pos));
                        rows.remove(taskRowNext);
                        rows.add(taskRowNext);

                        lastPos--; //decrementiamo il valore della futura nuova posizione della futura nuova task
                    }
                }
            }
            summarySheetTable.sort();
            summarySheetTable.refresh();
        } catch (UseCaseLogicException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void downButtonPressed() {
        Task t = summarySheetTable.getSelectionModel().getSelectedItem().getTask();
        int oldpos = t.getPos();
        int newpos = t.getPos() + 1;

        try {
            for(int i=rows.size()-1; i>=0;i--){
                TaskRow tr = rows.get(i);
                if(tr.task==t){ //switch
                    if(i+1>rows.size()) {
                        throw new UseCaseLogicException();
                    }else {
                        TaskRow succ = rows.get(i + 1);
                        CatERing.getInstance().getTaskManager().moveTask(succ.task, oldpos);

                        CatERing.getInstance().getTaskManager().moveTask(t, newpos);
                        succ.position = new SimpleIntegerProperty(CatERing.getInstance().getTaskManager().setPosition(rows.get(i + 1).task, oldpos));
                        tr.position = new SimpleIntegerProperty(CatERing.getInstance().getTaskManager().setPosition(rows.get(i).task, newpos));

                        rows.remove(succ);
                        rows.add(succ);
                        rows.remove(tr);
                        rows.add(tr);
                    }
                }
            }
            summarySheetTable.sort();
            summarySheetTable.refresh();
        }catch (UseCaseLogicException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    public void upButtonPressed() {
        Task t = summarySheetTable.getSelectionModel().getSelectedItem().getTask();
        int oldpos = t.getPos();
        int newpos = t.getPos() - 1;
        System.out.println("new pos = "+newpos);

        try {
            for(int i=0; i<rows.size();i++){
                TaskRow tr = rows.get(i);
                if(tr.task==t){
                    if(i-1>=0){
                        TaskRow prec = rows.get(i - 1);
                        //System.out.println("task "+rows.get(i-1).task+ " new pos = "+oldpos);
                        CatERing.getInstance().getTaskManager().moveTask(rows.get(i - 1).task, oldpos);

                        CatERing.getInstance().getTaskManager().moveTask(t, newpos);
                        prec.position = new SimpleIntegerProperty(CatERing.getInstance().getTaskManager().setPosition(rows.get(i - 1).task, oldpos));
                        tr.position = new SimpleIntegerProperty(CatERing.getInstance().getTaskManager().setPosition(rows.get(i).task, newpos));

                        rows.remove(tr);
                        rows.add(tr);
                        rows.remove(prec);
                        rows.add(prec);
                    } else {
                        throw new UseCaseLogicException();
                    }
                }
            }
        }catch(UseCaseLogicException ex){
            ex.printStackTrace();
        }
        summarySheetTable.sort();
        summarySheetTable.refresh();
    }

    //2-5
    @FXML
    public void restoreButtonPressed() {
        SummarySheet currentSummary = CatERing.getInstance().getTaskManager().getCurrentSummaryS();

        List<Task> daEliminare = new ArrayList<>();
        ObservableList<Task> tasks = CatERing.getInstance().getTaskManager().getAllTasks(currentSummary);
        ObservableList<Recipe> recipes_of_summary = CatERing.getInstance().getTaskManager().getCurrentSummaryS().getRecipes();
        for(Task t: tasks){
            for(Recipe r: recipes_of_summary) {
                if (t.recipe != r) { //se è una task che è stata aggiunta e non fa parte delle ricette la rimuovo
                    daEliminare.add(t);
                }
            }
        }
        Iterator<TaskRow> iterator = rows.iterator();
        while (iterator.hasNext()) {
            TaskRow tr = iterator.next();
            for(Task t: daEliminare) {
                if (t.getId() == tr.task.getId()) { //la task della riga tr è da eliminare
                    iterator.remove();
                    Task.removeTask(t); //ref a remove task
                    summarySheetTable.sort();
                    summarySheetTable.refresh();
                    lastPos--;
                }
            }
        }
    }

    @FXML
    public void exitButtonPressed() {
        this.endTaskManagement();
    }
    @FXML
    public void endTaskManagement() {
        mainPaneController.showStartPane();
    }

    public void setMainPaneController(Main main) {
        mainPaneController = main;
    }

    public void workshiftButtonPressed() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("workshifts-dialog.fxml"));
            BorderPane pane = loader.load();
            WorkshiftDialog controller = loader.getController();

            Stage stage = new Stage();

            controller.setOwnStage(stage);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("TABELLONE DEI TURNI");
            stage.setScene(new Scene(pane));

            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}

