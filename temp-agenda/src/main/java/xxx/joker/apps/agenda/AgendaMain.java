package xxx.joker.apps.agenda;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.agenda.common.AgendaConst;
import xxx.joker.apps.agenda.console.args.AgendaArgType;
import xxx.joker.apps.agenda.console.args.AgendaArgs;
import xxx.joker.apps.agenda.console.args.AgendaCmd;
import xxx.joker.apps.agenda.console.args.AgendaHelp;
import xxx.joker.apps.agenda.model.entities.Event;
import xxx.joker.apps.agenda.model.entities.FileAttach;
import xxx.joker.apps.agenda.service.AgendaService;
import xxx.joker.apps.agenda.service.AgendaServiceImpl;
import xxx.joker.libs.argsparser.ConsoleInputParser;
import xxx.joker.libs.argsparser.InputParser;
import xxx.joker.libs.argsparser.exceptions.DesignError;
import xxx.joker.libs.argsparser.exceptions.ParseError;
import xxx.joker.libs.core.datetime.JkTimes;
import xxx.joker.libs.core.enums.JkAlign;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.format.JkViewBuilder;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConsole;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class AgendaMain {

    private static final Logger logger = LoggerFactory.getLogger(AgendaMain.class);



    public static void main(String[] args) throws InterruptedException {
        InputParser parser = null;

        try {
            parser = new ConsoleInputParser(AgendaArgs.class, AgendaArgType.class, AgendaCmd.class, true);
        } catch (DesignError ex) {
            System.err.println(ex.toString());
            System.exit(1);
        }

        AgendaArgs iargs = null;
        try {
            iargs = parser.parse(args);
        } catch (ParseError ex) {
            System.err.println(ex.getErrorMex());
            Thread.sleep(1);
            display(AgendaHelp.USAGE);
            System.exit(1);
        }

        switch (iargs.getSelectedCommand()) {
            case CMD_ADD_EVENT_INTERACTIVE:
                doAddEventInteractive(iargs);
                break;
            case CMD_ADD_EVENT_CONSOLE:
                doAddEventConsole(iargs);
                break;
            case CMD_SHOW_EVENTS:
                doShowEvents(iargs);
                break;
            case CMD_DELETE_EVENTS:
                doDeleteEvents(iargs);
                break;
        }

    }

    private static void doDeleteEvents(AgendaArgs iargs) {
        AgendaService agendaService = new AgendaServiceImpl();
        boolean res = agendaService.removeEvent(iargs.getEventID());
        if(res) display("Removed event {}", iargs.getEventID());
        else    display("Event {} not exists", iargs.getEventID());
    }

    private static void doAddEventInteractive(AgendaArgs iargs) {
        Event e = new Event();

        String datetime = JkConsole.readUserInput("Datetime (empty, yyyyMMdd, yyyyMMddHHmm)*: ", s -> parseInputLdt(s) != null);
        e.setDatetime(parseInputLdt(datetime));

        String title = JkConsole.readUserInput("Title*: ", StringUtils::isNotBlank);
        e.setTitle(title);

        String strTags = JkConsole.readUserInput("Tags: ");
        if(StringUtils.isNotBlank(strTags)) {
            String[] tags = InputParser.splitArgsLine(strTags.trim());
            e.setTags(Arrays.asList(tags));
        }

        String notes = JkConsole.readUserInput("Notes: ");
        if(StringUtils.isNotBlank(notes)) {
            e.setNotes(notes);
        }

        List<FileAttach> attList = new ArrayList<>();
        for(Path file : iargs.getFiles()) {
            display("Attachment {}", file);
            String descr = JkConsole.readUserInput("Description: ");
            attList.add(new FileAttach(file, descr.trim()));
        }
        e.setAttachList(attList);

        AgendaService agendaService = new AgendaServiceImpl();
        boolean res = agendaService.addEvent(e);
        if(res) {
            copyAttachments(e);
            display("New event created! (ID = {})", e.getEntityID());
        } else {
            display("Unable to create new event");
        }
    }

    private static LocalDateTime parseInputLdt(String str) {
        if(StringUtils.isBlank(str))    return LocalDateTime.now();

        if(str.length() != 8 && str.length() != 12) {
            return null;
        }

        if(str.length() == 8)   str += "0000";
        return JkTimes.toDateTime(str, "yyyyMMddHHmm");
    }

    private static void doAddEventConsole(AgendaArgs iargs) {
        Event e = new Event();
        LocalDateTime dt = LocalDateTime.of(iargs.getDate(), iargs.getTime() == null ? LocalTime.MIN : iargs.getTime());
        e.setDatetime(dt);
        e.setTags(iargs.getTags());
        e.setTitle(iargs.getTitle());
        e.setNotes(iargs.getNotes());
        e.setAttachList(iargs.getAttaches());
        AgendaService agendaService = new AgendaServiceImpl();
        boolean res = agendaService.addEvent(e);
        if(res) {
            copyAttachments(e);
            display("New event created! (ID = {})", e.getEntityID());
        } else {
            display("Unable to create new event");
        }
    }

    private static void doShowEvents(AgendaArgs iargs) {
        AgendaService agendaService = new AgendaServiceImpl();
        Long eventID = iargs.getEventID();
        if(eventID == null) {
            JkViewBuilder vb = new JkViewBuilder();
            vb.addLines("ID|DATE|TIME|TITLE|TAGS|NOTES|ATTACHES");
            List<Event> sorted = JkStreams.sorted(agendaService.getEvents(), Event.temporalComparator());
            for(Event e : sorted) {
                String strDate = JkTimes.format(e.getDate(), "dd/MM/yyyy");
                String strTime = e.getTime() == LocalTime.MIN ? "-" : JkTimes.format(e.getTime(), "HH:mm");
                vb.addLines("{}|{}|{}|{}|{}|{}|{}", e.getEntityID(), strDate, strTime, e.getTitle(),
                        safeValue(JkStreams.join(e.getTags(), ", ")),
                        safeValue(e.getNotes()),
                        e.getAttachList().size()
                );
            }
            display(vb.toString("|", 2));

        } else {
            Event e = agendaService.getEvent(eventID);
            if(e == null) {
                display("No events found for ID {}", eventID);
            } else {
                JkViewBuilder vb = new JkViewBuilder();
                vb.addLines("EVENT|{}", eventID);
                String strDt = strf("Date:|{}", JkTimes.format(e.getDate(), "dd/MM/yyyy"));
                if (e.getTime() != LocalTime.MIN) {
                    strDt += "  " + JkTimes.format(e.getTime(), "HH:mm");
                }
                vb.addLines(strDt);
                vb.addLines("Title:|{}", e.getTitle());
                vb.addLines("Tags:|{}", safeValue(JkStreams.join(e.getTags(), ", ")));
                vb.addLines("Notes:|{}", safeValue(e.getNotes()));
                vb.addLines("Attaches:|{}", e.getAttachList().size());

                JkViewBuilder attb = new JkViewBuilder();
                for (FileAttach fa : e.getAttachList()) {
                    attb.addLines("{}:|{}", safeValue(fa.getDescr()), fa.getPath().getFileName());
                }
                attb.insertPrefix("  ");

                display(vb.toString("|", 2));
                if (!e.getAttachList().isEmpty()) {
                    display(attb.toString("|", 2));
                }
            }
        }
    }

    private static String safeValue(String str) {
        return StringUtils.isBlank(str) ? "-" : str;
    }

    private static void copyAttachments(Event e) {
        for(FileAttach fa : e.getAttachList()) {
            Path outPath = AgendaConst.ATTACHES_FOLDER.resolve(fa.getPath().getFileName());
            Path op = JkFiles.copyFileSafely(fa.getPath(), outPath);
            fa.setPath(op);
            logger.debug("Copied attach [{}]", fa);
        }
    }
}
