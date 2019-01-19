package xxx.joker.apps.agenda.model;

import xxx.joker.apps.agenda.common.AgendaConst;
import xxx.joker.apps.agenda.model.entities.Event;
import xxx.joker.libs.core.runtimes.JkEnvironment;
import xxx.joker.libs.repository.JkDataRepo;
import xxx.joker.libs.repository.JkDataRepoFile;

import java.nio.file.Path;
import java.util.Set;

public class AgendaModelImpl extends JkDataRepoFile implements AgendaModel {

    private static AgendaModel instance;

    private AgendaModelImpl() {
        super(AgendaConst.DB_FOLDER, "agenda", "xxx.joker.apps.agenda.model.entities");
    }

    public static synchronized AgendaModel getInstance() {
        if(instance == null)    instance = new AgendaModelImpl();
        return instance;
    }

    @Override
    public Set<Event> getEventsDataSet() {
        return getDataSet(Event.class);
    }

}
