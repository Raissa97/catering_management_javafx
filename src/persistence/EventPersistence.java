package persistence;

import businesslogic.event.EventEventReceiver;
import businesslogic.event.EventInfo;
import businesslogic.event.SchedaEvento;
import businesslogic.event.ServiceInfo;
import businesslogic.task.SummarySheet;

public class EventPersistence implements EventEventReceiver {

    @Override
    public void updateEventCreated(EventInfo e) {
        EventInfo.saveNewEvent(e);
    }

    @Override
    public void updateEventNameChanged(EventInfo e) {
        EventInfo.saveDifferentName(e);
    }

    @Override
    public void updateSchedaNameChanged(SchedaEvento s) {
        SchedaEvento.saveDifferentName(s);
    }

    @Override
    public void updateSchedaEdited(SchedaEvento s) {
        SchedaEvento.saveInfosScheda(s);
    }


    @Override
    public void updateEventChefAssigned(SchedaEvento e){
        SchedaEvento.saveChef(e);
    }

    @Override
    public void updateEventDeleted(EventInfo e) {
    }

    @Override
    public void updateServiceCreated(ServiceInfo s) {
        ServiceInfo.saveNewService(s);
    }

    @Override
    public void updateServiceDeleted(ServiceInfo s) {
        ServiceInfo.deleteService(s);
    }

    @Override
    public void updateServiceEdited(ServiceInfo s){
        ServiceInfo.editService(s);
    }

    @Override
    public void updateApprovedMenuChanged(ServiceInfo s){
        ServiceInfo.editApprovedMenu(s);
    }

    @Override
    public void updateProposedMenuChanged(ServiceInfo s){
        ServiceInfo.editProposedMenu(s);
    }

    public void updateSchedaEventoCreated(SchedaEvento e){
        SchedaEvento.saveNewSchedaEvento(e);
        SummarySheet s = new SummarySheet(e);
        SummarySheet.loadNewSummary(s);
    }
}
