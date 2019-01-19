package xxx.joker.apps.agenda;

import xxx.joker.apps.agenda.console.AgendaArgType;
import xxx.joker.apps.agenda.console.AgendaArgs;
import xxx.joker.apps.agenda.console.AgendaCmd;
import xxx.joker.libs.argsparser.ConsoleInputParser;
import xxx.joker.libs.argsparser.InputParser;
import xxx.joker.libs.argsparser.exceptions.DesignError;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class AgendaMain {



    public static void main(String[] args) {
        try {
            InputParser parser = new ConsoleInputParser(AgendaArgs.class, AgendaArgType.class, AgendaCmd.class, true);
        } catch (DesignError ex) {
            System.err.println(ex);
        }
    }
}
