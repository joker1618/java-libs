package xxx.joker.apps.agenda;

import xxx.joker.apps.agenda.console.AgendaArgType;
import xxx.joker.apps.agenda.console.AgendaArgs;
import xxx.joker.apps.agenda.console.AgendaCmd;
import xxx.joker.apps.agenda.console.AgendaHelp;
import xxx.joker.libs.argsparser.ConsoleInputParser;
import xxx.joker.libs.argsparser.InputParser;
import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;
import xxx.joker.libs.argsparser.exceptions.DesignError;
import xxx.joker.libs.argsparser.exceptions.ParseError;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class AgendaMain {



    public static void main(String[] args) {
        InputParser parser = null;

        try {
            parser = new ConsoleInputParser(AgendaArgs.class, AgendaArgType.class, AgendaCmd.class, true);
        } catch (DesignError ex) {
            System.err.println(ex);
            display(AgendaHelp.USAGE);
            System.exit(1);
        }

        AgendaArgs iargs = null;
        try {
            iargs = parser.parse(args);
        } catch (ParseError ex) {
            System.err.println(ex);
            System.exit(1);
        }

        switch (iargs.getSelectedCommand()) {
            case CMD_ADD_EVENT_INTERACTIVE:
                break;
            case CMD_ADD_EVENT_CONSOLE:
                break;
            case CMD_SHOW_EVENTS:
                break;
        }
    }
}
