package xxx.joker.apps.agenda.service;

import xxx.joker.apps.agenda.model.entities.Event;

import java.util.List;

public interface AgendaService {

    boolean addEvent(Event event);

    boolean removeEvent(long eventID);

    List<Event> getEvents();
    Event getEvent(long eventID);
}
