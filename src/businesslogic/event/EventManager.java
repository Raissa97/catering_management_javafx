package businesslogic.event;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.menu.Menu;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Map;

public class EventManager {
    private EventInfo currentEvent;
    private SchedaEvento currentSchedaEvento;
    private ServiceInfo currentService;
    private ArrayList<EventEventReceiver> eventReceivers;

    public EventManager() {
        eventReceivers = new ArrayList<>();
    }

    public ObservableList<SchedaEvento> getSchedeEvento(User u) {
        return SchedaEvento.loadAllSchedeEventoOfU(u);
    }

    public ObservableList<SchedaEvento> getSchedeEvento(EventInfo e, User u) {
        ObservableList<SchedaEvento> schede = SchedaEvento.loadSchedeForEvent(e.getId());
        ObservableList<SchedaEvento> sch = SchedaEvento.loadAllSchedeEventoOfU(u);
        ObservableList<SchedaEvento> ret = FXCollections.observableArrayList();
        for(SchedaEvento s: schede){
            if(sch.contains(s)){
                ret.add(s);
            }
        }
        return ret;
    }

    public ObservableList<EventInfo> getAllEvents() {
        return EventInfo.loadAllEventInfo();
    }

    public ObservableList<SchedaEvento> getAllSchede() {
        return SchedaEvento.loadAllSchedeEvento();
    }

    public Map<Integer, SchedaEvento> getAllSchedeMap() {
        return SchedaEvento.loadAllSchedeMap();
    }

    public void addEventReceiver(EventEventReceiver rec) {
        this.eventReceivers.add(rec);
    }

    public ObservableList<ServiceInfo> getAllServices(SchedaEvento e) {
        return ServiceInfo.loadServiceInfoForSchedaEvento(e.getId());
    }

    public void setCurrentEvent(EventInfo e){
        this.currentEvent=e;
    }

    public void setCurrentSchedaEvento(SchedaEvento sc){
        this.currentSchedaEvento=sc;
    }

    public void setCurrentService(ServiceInfo s){
        this.currentService=s;
    }

    public EventInfo getCurrentEvent(){
        return this.currentEvent;
    }

    public SchedaEvento getCurrentScheda() {
        return this.currentSchedaEvento;
    }

    public ServiceInfo getCurrentService(){
        return this.currentService;
    }

    public void chooseSchedaEvento(SchedaEvento sc) throws UseCaseLogicException{
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!u.isOrganizer() && !sc.isEventChef(u)) throw new UseCaseLogicException();
        //if(!sc.isEventChef(u) && u.isOrganizer() && !EventInfo.getEventFromId(sc.getEvent()).isOrganizer(u)) throw new UseCaseLogicException();
        this.setCurrentSchedaEvento(sc);
    }

    public void chooseEvent(EventInfo e) throws UseCaseLogicException{
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!u.isOrganizer()) throw new UseCaseLogicException();
        if(u.isOrganizer() && u!=e.getOrganizer()) throw new UseCaseLogicException();
        this.setCurrentEvent(e);
    }

    public void chooseService(SchedaEvento e, ServiceInfo s) throws UseCaseLogicException{
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        if (u.isOrganizer() ||  e.isEventChef(u))
        this.setCurrentService(s);
        else throw new UseCaseLogicException();
    }

    public void createEvent(String name, boolean isRic, User u) {
        EventInfo e = new EventInfo(name,isRic,u);

        notifyEventCreated(e);
    }
    private void notifyEventCreated(EventInfo e) {
        for (EventEventReceiver er : this.eventReceivers) {
            er.updateEventCreated(e);
        }
    }

    public SchedaEvento createSchedaEvento(EventInfo e, Date dataI, Date dataF, int pp,String luogo) throws UseCaseLogicException{ //, int numP) {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isOrganizer()) throw new UseCaseLogicException();

        SchedaEvento sc = new SchedaEvento(e, 0, dataI, dataF, pp,luogo);

        this.notifySchedaCreated(sc);

        return sc;
    }
    private void notifySchedaCreated(SchedaEvento sc) {
        for (EventEventReceiver er : this.eventReceivers) {
            er.updateSchedaEventoCreated(sc);
        }
    }

    public void deleteEvent(EventInfo e) throws UseCaseLogicException {
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!u.isOrganizer()) throw new UseCaseLogicException();
        if(e.isActive()) throw new UseCaseLogicException();

        this.notifyEventDeleted(e);
    }
    private void notifyEventDeleted(EventInfo e) {
        for (EventEventReceiver er : this.eventReceivers) {
            er.updateEventDeleted(e);
        }
    }


    public void changeNameEvent(EventInfo e, String newName) throws UseCaseLogicException {
        if (currentEvent == null) throw new UseCaseLogicException();
        if (currentSchedaEvento == null) throw new UseCaseLogicException();
        currentEvent.setName(newName);
        for(SchedaEvento s: e.getSchede()){
            s.setName(newName);
            notifySchedaNameChanged(s);
        }
        this.notifyEventNameChanged(e);
    }
    private void notifyEventNameChanged(EventInfo e) {
        for (EventEventReceiver er : this.eventReceivers) {
            er.updateEventNameChanged(e);
        }
    }
    private void notifySchedaNameChanged(SchedaEvento e) {
        for (EventEventReceiver er : this.eventReceivers) {
            er.updateSchedaNameChanged(e);
        }
    }

    public void editScheda(SchedaEvento s, String newName, String place, Date inizio, Date fine, int pp) throws UseCaseLogicException {
        if (s == null) throw new UseCaseLogicException();

        s.setName(newName);
        s.setLuogo(place);
        s.setDateStart(inizio);
        s.setDateEnd(fine);
        s.setParticipants(pp);
        notifySchedaEdited(s);

        this.notifySchedaEdited(s);
    }
    private void notifySchedaEdited(SchedaEvento s) {
        for (EventEventReceiver er : this.eventReceivers) {
            er.updateSchedaEdited(s);
        }
    }

    public void setEventChef(SchedaEvento e, String[] allChefsName, boolean[] vals) throws UseCaseLogicException{
        if (e == null) throw new UseCaseLogicException();
        if (allChefsName.length != vals.length) throw new UseCaseLogicException();
        for (int i = 0; i < allChefsName.length; i++) {
            e.setEventChef(User.getUserByName(allChefsName[i]), vals[i]);
        }
        this.notifyEventChefAssigned(e);
    }
    private void notifyEventChefAssigned(SchedaEvento e) {
        for (EventEventReceiver er : this.eventReceivers) {
            er.updateEventChefAssigned(e);
        }
    }

    public ServiceInfo createService(int scheda_id,String name, java.sql.Date serviceDate, Time timeIn, Time timeF, int numP) throws UseCaseLogicException {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        SchedaEvento currentEvent = SchedaEvento.getSchedaFromId(scheda_id);

        if (!user.isOrganizer()) throw new UseCaseLogicException();
        if (currentEvent==null) throw new UseCaseLogicException();

        ServiceInfo s = new ServiceInfo(name);//currentEvent,name,serviceDate,timeIn,timeF,numP);
        s.setScheda_id(scheda_id);
        s.setDate(serviceDate);
        s.setTimeStart(timeIn);
        s.setTimeEnd(timeF);
        s.setParticipants(numP);

        //CatERing.getInstance().getEventManager().setCurrentService(s);

        this.notifyServiceCreated(s);

        return s;
    }
    private void notifyServiceCreated(ServiceInfo s) {
        for (EventEventReceiver er : this.eventReceivers) {
            er.updateServiceCreated(s);
        }
    }

    public void deleteServizio(ServiceInfo s) throws UseCaseLogicException {
        this.notifyServiceDeleted(s);
    }
    private void notifyServiceDeleted(ServiceInfo s) {
        for (EventEventReceiver er : this.eventReceivers) {
            er.updateServiceDeleted(s);
        }
    }

    public void changeApprovedMenu(ServiceInfo s, String[] menuTitles, boolean[] values) throws UseCaseLogicException {
        if (s == null) throw new UseCaseLogicException();
        if (menuTitles.length != values.length) throw new UseCaseLogicException();
        for (int i = 0; i < menuTitles.length; i++) {
            s.setApprovedMenu(Menu.getMenuByTitle(menuTitles[i]), values[i]);
        }
        this.notifyApprovedMenuChanged(s);
    }
    private void notifyApprovedMenuChanged(ServiceInfo s) {
        for (EventEventReceiver er : this.eventReceivers) {
            er.updateApprovedMenuChanged(s);
        }
    }

    public void changeProposedMenu(ServiceInfo s, String[] menuTitles, boolean[] values) throws UseCaseLogicException {
        if (s == null) throw new UseCaseLogicException();
        if (menuTitles.length != values.length) throw new UseCaseLogicException();
        for (int i = 0; i < menuTitles.length; i++) {
            s.setProposedMenu(Menu.getMenuByTitle(menuTitles[i]), values[i]);
        }
        this.notifyProposedMenuChanged(s);
    }
    private void notifyProposedMenuChanged(ServiceInfo s) {
        for (EventEventReceiver er : this.eventReceivers) {
            er.updateProposedMenuChanged(s);
        }
    }

    public void editService(ServiceInfo s) throws UseCaseLogicException {
        if (currentEvent==null) {
            throw new UseCaseLogicException();
        }
        this.notifyServiceEdited(s);
    }
    private void notifyServiceEdited(ServiceInfo s) {
        for (EventEventReceiver er : this.eventReceivers) {
            System.out.println("catering edit SERVICE -->"+ s.toString()+"\n");
            er.updateServiceEdited(s);
        }
    }

    public ObservableList<SchedaEvento> getAllSchedeForU(int id) {
        return SchedaEvento.loadAllSchedeEventoOfU(User.getUserById(id));
    }

    public ObservableList<SchedaEvento> getAllSchedeForChef(User u) {
        return SchedaEvento.loadAllSchedaEventoOfChef(User.getUserById(u.getId()));
    }
    public Map<Integer, SchedaEvento> getAllSchedeMapForChef(User u) {
        return SchedaEvento.loadAllSchedeMapOfChef(User.getUserById(u.getId()));
    }

    public ObservableList<EventInfo> getAllEventsForU(int id) {
        return EventInfo.loadAllEventInfoForU(User.getUserById(id));
    }

    public void endScheda(SchedaEvento currentScheda) throws UseCaseLogicException {
        for(ServiceInfo s: currentScheda.getServices().toArray(new ServiceInfo[0])){
            if(s.getApprovedMenu()!=null) throw new UseCaseLogicException();
        }
        SchedaEvento.deleteSchedaEvento(currentScheda);
    }

}
