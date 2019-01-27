package xxx.joker.apps.agenda;

import xxx.joker.apps.agenda.console.args.AgendaArgType;
import xxx.joker.apps.agenda.console.args.AgendaArgs;
import xxx.joker.apps.agenda.console.args.AgendaCmd;
import xxx.joker.apps.agenda.console.args.AgendaHelp;
import xxx.joker.apps.agenda.model.entities.Event;
import xxx.joker.libs.argsparser.ConsoleInputParser;
import xxx.joker.libs.argsparser.InputParser;
import xxx.joker.libs.argsparser.exceptions.DesignError;
import xxx.joker.libs.argsparser.exceptions.ParseError;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class AgendaMain {



    public static void main(String[] args) throws InterruptedException {
        InputParser parser = null;

        try {
            parser = new ConsoleInputParser(AgendaArgs.class, AgendaArgType.class, AgendaCmd.class, true);
        } catch (DesignError ex) {
            System.err.println(ex);
            System.exit(1);
        }

        AgendaArgs iargs = null;
        try {
            iargs = parser.parse(args);
            display("{}", iargs);
        } catch (ParseError ex) {
            System.err.println(ex.getErrorMex());
            Thread.sleep(1);
            display(AgendaHelp.USAGE);
            System.exit(1);
        }

        switch (iargs.getSelectedCommand()) {
            case CMD_ADD_EVENT_INTERACTIVE:
                doAddEventInteractive(iargs, parser);
                break;
            case CMD_ADD_EVENT_CONSOLE:
                doAddEventConsole(iargs);
                break;
            case CMD_SHOW_EVENTS:
                doShowEvents(iargs);
                break;
        }

    }

    private static void doAddEventInteractive(AgendaArgs iargs, InputParser parser) {
        // todo impl
    }

    private static void doAddEventConsole(AgendaArgs iargs) {
        // todo impl
        Event e = new Event();
    }

    private static void doShowEvents(AgendaArgs iargs) {
     // todo impl
    }
}
