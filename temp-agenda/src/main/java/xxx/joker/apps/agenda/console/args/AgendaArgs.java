package xxx.joker.apps.agenda.console.args;

import xxx.joker.apps.agenda.model.entities.FileAttach;
import xxx.joker.libs.argsparser.design.annotations.JkArg;
import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;
import xxx.joker.libs.core.utils.JkConvert;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AgendaArgs extends JkAbstractArgs<AgendaCmd> {

    @JkArg(argName = "add", aliases = {"-a"})
    private boolean add = false;
    @JkArg(argName = "event", aliases = {"-e"})
    private boolean event = false;
    @JkArg(argName = "show", aliases = {"-s"})
    private boolean show = false;
    @JkArg(argName = "del", aliases = {"-del"})
    private boolean delete = false;
    @JkArg(argName = "date", aliases = {"-dt"})
    private LocalDate date;
    @JkArg(argName = "time", aliases = {"-tm"})
    private LocalTime time;
    @JkArg(argName = "title", aliases = {"-tit"})
    private String title;
    @JkArg(argName = "tags", aliases = {"-t", "-tags"})
    private String[] tags;
    @JkArg(argName = "notes", aliases = {"-n"})
    private String notes;
    @JkArg(argName = "id", aliases = {"-id"})
    private Long eventID;
    @JkArg(argName = "attach", aliases = {"-att"})
    private String[] attaches;
    @JkArg(argName = "files", aliases = {"-f"})
    private Path[] files;

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getTags() {
        return tags == null ? Collections.emptyList() : JkConvert.toArrayList(tags);
    }

    public String getNotes() {
        return notes;
    }

    public List<FileAttach> getAttaches() {
        if(attaches == null)    return Collections.emptyList();

        List<FileAttach> attList = new ArrayList<>();
        for(String att : attaches) {
            int idx = att.indexOf("?");
            FileAttach fa;
            if(idx == -1) {
                fa = new FileAttach(Paths.get(att));
            } else {
                fa = new FileAttach(Paths.get(att.substring(0, idx)), att.substring(idx+1));
            }
            attList.add(fa);
        }
        return attList;
    }

    public List<Path> getFiles() {
        return files == null ? Collections.emptyList() : JkConvert.toArrayList(files);
    }

    public Long getEventID() {
        return eventID;
    }
}
