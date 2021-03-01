package businesslogic.event;

public interface EventEventReceiver {
    void updateEventCreated(EventInfo e);

    void updateServiceCreated(ServiceInfo s);

    void updateEventNameChanged(EventInfo e);

    void updateSchedaNameChanged(SchedaEvento s);

    void updateSchedaEdited(SchedaEvento s);

    void updateEventChefAssigned(SchedaEvento e);

    void updateEventDeleted(EventInfo e);

    void updateServiceDeleted(ServiceInfo s);

    void updateServiceEdited(ServiceInfo s);

    void updateApprovedMenuChanged(ServiceInfo s);

    void updateProposedMenuChanged(ServiceInfo s);

    void updateSchedaEventoCreated(SchedaEvento sc);
}
