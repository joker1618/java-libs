package xxx.joker.apps.agenda.service;

import xxx.joker.apps.agenda.model.AgendaModel;
import xxx.joker.apps.agenda.model.AgendaModelImpl;
import xxx.joker.apps.agenda.model.entities.Event;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConvert;

import java.util.List;

public class AgendaServiceImpl implements AgendaService {

    private final AgendaModel model;

    public AgendaServiceImpl() {
        this.model = AgendaModelImpl.getInstance();
    }

    @Override
    public boolean addEvent(Event event) {
        boolean res = model.getEventsDataSet().add(event);
        if(res) {
            model.commit();
        }
        return res;
    }

    @Override
    public boolean removeEvent(long eventID) {
        boolean res = model.getEventsDataSet().removeIf(e -> e.getEntityID() == eventID);
        if(res) {
            model.commit();
        }
        return res;
    }

    @Override
    public List<Event> getEvents() {
        return JkConvert.toArrayList(model.getEventsDataSet());
    }

    @Override
    public Event getEvent(long eventID) {
        return JkStreams.findExactMatch(model.getEventsDataSet(), e -> e.getEntityID() == eventID);
    }

}
