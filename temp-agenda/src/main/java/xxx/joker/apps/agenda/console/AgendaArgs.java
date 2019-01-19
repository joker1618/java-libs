package xxx.joker.apps.agenda.console;

import xxx.joker.libs.argsparser.design.annotations.JkArg;
import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AgendaArgs extends JkAbstractArgs<AgendaCmd> {

    @JkArg(argName = "add", aliases = {"-a"})
    private boolean add = false;
    @JkArg(argName = "event", aliases = {"-e"})
    private boolean event = false;
    @JkArg(argName = "show", aliases = {"-s"})
    private boolean show = false;
    @JkArg(argName = "date", aliases = {"-dt"})
    private LocalDate date;
    @JkArg(argName = "time", aliases = {"-tm"})
    private LocalTime time;
    @JkArg(argName = "title", aliases = {"-tit"})
    private String title;
    @JkArg(argName = "tags", aliases = {"-t"})
    private String[] tags;
    @JkArg(argName = "notes", aliases = {"-n"})
    private String notes;
    @JkArg(argName = "id", aliases = {"-id"})
    private Long eventId;
    @JkArg(argName = "attach", aliases = {"-att"})
    private String[] attaches;
    @JkArg(argName = "files", aliases = {"-f"})
    private Path[] files;

}
