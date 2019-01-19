package xxx.joker.apps.agenda.model;

import xxx.joker.apps.agenda.model.entities.Event;
import xxx.joker.libs.repository.JkDataRepo;

import java.util.Set;

public interface AgendaModel extends JkDataRepo {

    Set<Event> getEventsDataSet();

}
